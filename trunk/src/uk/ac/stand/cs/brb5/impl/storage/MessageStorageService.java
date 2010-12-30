/*******************************************************************************
 * PeerBook
 * Copyright (C) 2009 Ben Birt
 * http://blogs.cs.st-andrews.ac.uk/peerbook/
 * 
 * This file is part of PeerBook.
 * 
 * PeerBook is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * PeerBook is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerBook.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package uk.ac.stand.cs.brb5.impl.storage;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlException;

import uk.ac.stAndrews.cs.hostBrb5.peerBook.messageDatabase.MessageCount;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.messageDatabase.MessageDatabaseDocument;
import uk.ac.stand.cs.brb5.impl.exceptions.FileAccessException;
import uk.ac.stand.cs.brb5.impl.util.FileIO;
import uk.ac.stand.cs.brb5.impl.util.Synchronizer;
import uk.ac.stand.cs.brb5.interfaces.IMessageStorageService;
import uk.ac.standrews.cs.nds.p2p.impl.Key;
import uk.ac.standrews.cs.nds.p2p.interfaces.IKey;
import uk.ac.standrews.cs.nds.util.Diagnostic;
import uk.ac.standrews.cs.nds.util.DiagnosticLevel;

/**
 * Implements message storage for a set of users.
 * 
 * @author Ben
 *
 */
public class MessageStorageService extends UnicastRemoteObject implements IMessageStorageService {

	private static final long serialVersionUID = -2403784214879552503L;

	private static final String MESSAGES_DIR = "Messages/";
	private static final String MESSAGE_DATABASE_FILENAME = "MessageDatabase.xml";
	private static final String MESSAGE_FILE_PREFIX = "Message";
	private static final String KEY_FILE_PREFIX = "Key";
	private static final String SIGNATURE_FILE_PREFIX = "Signature";

	private MessageDatabaseDocument messageDb;
	private Synchronizer<IKey> synchronizer;

	/**
	 * Constructs a new {@link MessageStorageService}, trying to create a message database
	 * file and associated Messages folder.
	 * 
	 * @throws FileAccessException
	 * @throws IOException
	 */
	public MessageStorageService() throws FileAccessException, IOException {
		super();
		this.synchronizer = new Synchronizer<IKey>();

		if (!FileIO.fileExists(MESSAGES_DIR, "")) {
			FileIO.makeDirectory(MESSAGES_DIR);
		}

		try {

			if (!FileIO.fileExists(MESSAGE_DATABASE_FILENAME, MESSAGES_DIR)) {
				MessageDatabaseDocument doc = MessageDatabaseDocument.Factory.newInstance();
				doc.addNewMessageDatabase();

				FileIO.storeFile(doc.toString().getBytes(), MESSAGE_DATABASE_FILENAME, MESSAGES_DIR);
			}
			messageDb = getMessageDatabase();
		} catch (IOException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
			throw new FileAccessException("Could not read or write file " + MESSAGE_DATABASE_FILENAME);
		} catch (XmlException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
			throw new FileAccessException("Could not read/parse file " + MESSAGE_DATABASE_FILENAME);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IMessageStorageService#deleteMessages(uk.ac.standrews.cs.nds.p2p.interfaces.IKey)
	 */
	public void deleteMessages(IKey usernameHash) throws IOException {
		synchronized (synchronizer.getSynchronizingObject(usernameHash)) {
			FileIO.deleteFilesInDirectoryWithPrefix(MESSAGES_DIR + usernameHash.toString(), MESSAGE_FILE_PREFIX);
			FileIO.deleteFilesInDirectoryWithPrefix(MESSAGES_DIR + usernameHash.toString(), KEY_FILE_PREFIX);
			FileIO.deleteFilesInDirectoryWithPrefix(MESSAGES_DIR + usernameHash.toString(), SIGNATURE_FILE_PREFIX);
			setMessageCount(usernameHash, 0);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IMessageStorageService#saveMessage(uk.ac.stand.cs.brb5.interfaces.IMessageStorageService.EncryptedMessage, uk.ac.standrews.cs.nds.p2p.interfaces.IKey)
	 */
	public void saveMessage(EncryptedMessage messageBytes, IKey usernameHash) throws IOException {

		synchronized (synchronizer.getSynchronizingObject(usernameHash)) {
			if (!FileIO.fileExists(usernameHash.toString(), MESSAGES_DIR)) {
				FileIO.makeDirectory(MESSAGES_DIR + usernameHash.toString());
			}

			int messageNumber = getMessageCount(usernameHash);
			FileIO.storeFile(messageBytes.getEncryptedSecretKey(), KEY_FILE_PREFIX + messageNumber, MESSAGES_DIR + usernameHash.toString() + "/");
			FileIO.storeFile(messageBytes.getEncryptedMessage(), MESSAGE_FILE_PREFIX + messageNumber, MESSAGES_DIR + usernameHash.toString() + "/");
			FileIO.storeFile(messageBytes.getSignature(), SIGNATURE_FILE_PREFIX + messageNumber, MESSAGES_DIR + usernameHash.toString() + "/");
			setMessageCount(usernameHash, messageNumber + 1);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IMessageStorageService#getMessages(uk.ac.standrews.cs.nds.p2p.interfaces.IKey)
	 */
	public EncryptedMessage[] getMessages(IKey usernameHash) throws IOException {

		synchronized (synchronizer.getSynchronizingObject(usernameHash)) {
			int messageNumber = getMessageCount(usernameHash);
			EncryptedMessage[] messageArray = new EncryptedMessage[messageNumber];

			for (int i = 0; i < messageNumber; i++) {
				byte[] encryptedSecretKey = FileIO.readFile(KEY_FILE_PREFIX + i, MESSAGES_DIR + usernameHash.toString() + "/");
				byte[] cipherText = FileIO.readFile(MESSAGE_FILE_PREFIX + i, MESSAGES_DIR + usernameHash.toString() + "/");
				byte[] signature = FileIO.readFile(SIGNATURE_FILE_PREFIX + i, MESSAGES_DIR + usernameHash.toString() + "/");
				messageArray[i] = new EncryptedMessage(encryptedSecretKey, cipherText, signature);
			}

			return messageArray;
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IMessageStorageService#getStoredKeys()
	 */
	public List<IKey> getStoredKeys() throws RemoteException {
		MessageCount[] messageCounts = messageDb.getMessageDatabase().getMessageCountArray();
		List<IKey> usernames = new ArrayList<IKey>();

		for (MessageCount messageCount : messageCounts) {
			usernames.add(new Key(messageCount.getUsername()));
		}

		return usernames;
	}

	private void setMessageCount(IKey usernameHash, int newMessageCount) throws IOException {

		boolean usernameFound = false;
		for (MessageCount count : messageDb.getMessageDatabase().getMessageCountArray()) {
			if (count.getUsername().equals(usernameHash.toString())) {
				count.setCount(newMessageCount);
				usernameFound = true;
			}
		}

		if (!usernameFound) {
			MessageCount newCount = messageDb.getMessageDatabase().addNewMessageCount();
			newCount.setUsername(usernameHash.toString());
			newCount.setCount(newMessageCount);
		}

		FileIO.storeFile(messageDb.toString().getBytes(), MESSAGE_DATABASE_FILENAME, MESSAGES_DIR);
	}

	private int getMessageCount(IKey usernameHash) {
		for (MessageCount count : messageDb.getMessageDatabase().getMessageCountArray()) {
			if (count.getUsername().equals(usernameHash.toString())) {
				return count.getCount();
			}
		}

		return 0;
	}

	private MessageDatabaseDocument getMessageDatabase() throws XmlException, IOException {
		return MessageDatabaseDocument.Factory.parse(new String(FileIO.readFile(MESSAGE_DATABASE_FILENAME, MESSAGES_DIR)));
	}

}

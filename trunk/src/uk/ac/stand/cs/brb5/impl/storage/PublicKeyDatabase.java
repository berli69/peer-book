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
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlException;

import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicKeyDatabase.KeyMapping;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicKeyDatabase.PublicKeyDatabaseDocument;
import uk.ac.stand.cs.brb5.impl.exceptions.FileAccessException;
import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.impl.util.FileIO;
import uk.ac.stand.cs.brb5.impl.util.Synchronizer;
import uk.ac.stand.cs.brb5.interfaces.IEncryptionManager;
import uk.ac.stand.cs.brb5.interfaces.IEncryptionManagerFactory;
import uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager;
import uk.ac.stand.cs.brb5.interfaces.IPublicKeyDatabase;
import uk.ac.standrews.cs.nds.util.Diagnostic;
import uk.ac.standrews.cs.nds.util.DiagnosticLevel;

/**
 * Implements storage of Username -> Public Key mappings.
 * 
 * @author Ben
 *
 */
public class PublicKeyDatabase extends UnicastRemoteObject implements IPublicKeyDatabase {

	private static final long serialVersionUID = 4731654410068411872L;

	private static final String PUBLIC_KEY_DATABASE_DIR = "PublicKeyDatabase/";
	private static final String PUBLIC_KEY_DATABASE_FILENAME = "PublicKeyDatabase.xml";

	private PublicKeyDatabaseDocument publicKeyDb;
	private IEncryptionManager encryptionManager;
	private Synchronizer<String> synchronizer;

	/**
	 * Constructs a new {@link PublicKeyDatabase}, creating a new Public Key Database file.
	 * 
	 * @param encryptionManagerFac
	 * @throws IOException
	 * @throws FileAccessException
	 */
	public PublicKeyDatabase(IEncryptionManagerFactory encryptionManagerFac) throws IOException, FileAccessException {

		this.encryptionManager = encryptionManagerFac.getEncryptionManager();
		this.synchronizer = new Synchronizer<String>();

		if (!FileIO.fileExists(PUBLIC_KEY_DATABASE_DIR, "")) {
			FileIO.makeDirectory(PUBLIC_KEY_DATABASE_DIR);
		}

		if (!FileIO.fileExists(PUBLIC_KEY_DATABASE_FILENAME, PUBLIC_KEY_DATABASE_DIR)) {
			PublicKeyDatabaseDocument doc = PublicKeyDatabaseDocument.Factory.newInstance();
			doc.addNewPublicKeyDatabase();

			FileIO.storeFile(doc.toString().getBytes(), PUBLIC_KEY_DATABASE_FILENAME, PUBLIC_KEY_DATABASE_DIR);
		}

		try {
			publicKeyDb = getPublicKeyDatabase();
		} catch (XmlException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
			throw new FileAccessException("Could not parse/read file " + PUBLIC_KEY_DATABASE_FILENAME);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPublicKeyDatabase#getPublicKey(java.lang.String)
	 */
	public PublicKey getPublicKey(String username) throws GeneralSecurityException {
		synchronized (synchronizer.getSynchronizingObject(username)) {
			KeyMapping mapping = findMapping(username);
			if (mapping != null) {
				return encryptionManager.generatePublicKey(findMapping(username).getPublicKey().getKeyByteArray());
			} else {
				return null;
			}
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPublicKeyDatabase#savePublicKey(java.lang.String, java.security.PublicKey)
	 */
	public void savePublicKey(String username, PublicKey key) throws IOException, GeneralSecurityException {

		synchronized (synchronizer.getSynchronizingObject(username)) {
			KeyMapping previouslyMapped = findMapping(username);

			if (previouslyMapped == null) {
				KeyMapping newMapping = publicKeyDb.getPublicKeyDatabase().addNewKeyMapping();
				newMapping.setUsername(username);
				newMapping.addNewPublicKey().setKeyByteArray(key.getEncoded());
				databaseChanged();
			}
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPublicKeyDatabase#changePublicKey(java.lang.String, byte[], java.security.PublicKey)
	 */
	public void changePublicKey(String username, byte[] signature, PublicKey newKey) throws IOException, GeneralSecurityException {

		synchronized (synchronizer.getSynchronizingObject(username)) {
			KeyMapping previouslyMapped = findMapping(username);

			if (previouslyMapped != null) {
				PublicKey oldKey = encryptionManager.generatePublicKey(previouslyMapped.getPublicKey().getKeyByteArray());

				if (encryptionManager.verifySignature(signature, username.getBytes(), oldKey)) {
					previouslyMapped.getPublicKey().setKeyByteArray(newKey.getEncoded());
					databaseChanged();
				}
			}
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPublicKeyDatabase#changeUsername(java.lang.String, java.lang.String, byte[])
	 */
	public void changeUsername(String oldUsername, String newUsername, byte[] signature) throws GeneralSecurityException, IOException {
		synchronized (synchronizer.getSynchronizingObject(oldUsername)) {
			KeyMapping previouslyMapped = findMapping(oldUsername);

			if (previouslyMapped != null) {
				PublicKey publicKey = encryptionManager.generatePublicKey(previouslyMapped.getPublicKey().getKeyByteArray());

				if (encryptionManager.verifySignature(signature, newUsername.getBytes(), publicKey)) {
					previouslyMapped.setUsername(newUsername);
					databaseChanged();
				}
			}
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPublicKeyDatabase#getStoredUsernames()
	 */
	public List<String> getStoredUsernames() throws RemoteException {
		List<String> usernames = new ArrayList<String>();

		for (KeyMapping keyMapping : publicKeyDb.getPublicKeyDatabase().getKeyMappingArray()) {
			usernames.add(keyMapping.getUsername());
		}

		return usernames;
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPublicKeyDatabase#searchUsername(java.lang.String)
	 */
	public List<String> searchUsername(String search) throws RemoteException, PeerBookException {
		
		if (search.length() < IKeySpaceManager.MINIMUM_USERNAME_LENGTH) {
			throw new PeerBookException("Search string too short.");
		}

		List<String> results = new ArrayList<String>();

		for (String username : getStoredUsernames()) {
			if (search.length() <= username.length() && username.substring(0, search.length()).toLowerCase().equals(search.toLowerCase())) {
				results.add(username);
			}
		}

		return results;
	}

	private void databaseChanged() throws IOException {
		FileIO.storeFile(publicKeyDb.toString().getBytes(), PUBLIC_KEY_DATABASE_FILENAME, PUBLIC_KEY_DATABASE_DIR);
	}

	private PublicKeyDatabaseDocument getPublicKeyDatabase() throws XmlException, IOException {
		return PublicKeyDatabaseDocument.Factory.parse(new String(FileIO.readFile(PUBLIC_KEY_DATABASE_FILENAME, PUBLIC_KEY_DATABASE_DIR)));
	}

	private KeyMapping findMapping(String username) {
		for (KeyMapping mapping : publicKeyDb.getPublicKeyDatabase().getKeyMappingArray()) {
			if (mapping.getUsername().equals(username)) {
				return mapping;
			}
		}
		return null;
	}

}

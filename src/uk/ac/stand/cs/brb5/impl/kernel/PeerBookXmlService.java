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
package uk.ac.stand.cs.brb5.impl.kernel;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import javax.crypto.SecretKey;

import org.apache.xmlbeans.XmlException;

import uk.ac.stAndrews.cs.hostBrb5.peerBook.message.MessageDocument;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.PrivateProfileDocument;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.PublicProfileDocument;
import uk.ac.stand.cs.brb5.impl.exceptions.FileAccessException;
import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.interfaces.ICompressionManager;
import uk.ac.stand.cs.brb5.interfaces.IEncryptionManager;
import uk.ac.stand.cs.brb5.interfaces.IEncryptionManagerFactory;
import uk.ac.stand.cs.brb5.interfaces.IPeerBookXmlService;
import uk.ac.stand.cs.brb5.interfaces.IStorageService;
import uk.ac.stand.cs.brb5.interfaces.IEncryptionManager.PublicKeyEncryptedBytes;
import uk.ac.stand.cs.brb5.interfaces.IMessageStorageService.EncryptedMessage;
import uk.ac.standrews.cs.nds.util.Diagnostic;
import uk.ac.standrews.cs.nds.util.DiagnosticLevel;

/**
 * 
 * Utility class used as a layer between saving/retrieving objects to/from the IStorageService and
 * actually having usable XML objects (involves encryption and compression).
 * 
 * @author Ben
 *
 */
public class PeerBookXmlService implements IPeerBookXmlService {

	private IEncryptionManager encryptionManager;
	private ICompressionManager compressionManager;
	private IStorageService storageService;

	/**
	 * @param encryptionManagerFac IEncryptionManager used to encrypt/decrypt PeerBook objects.
	 * @param compressionManager ICompressionManager used to compress/decompress PeerBook objects.
	 * @param storageService IStorageService on which PeerBook objects are stored.
	 */
	public PeerBookXmlService(IEncryptionManagerFactory encryptionManagerFac, ICompressionManager compressionManager, IStorageService storageService) {
		this.encryptionManager = encryptionManagerFac.getEncryptionManager();
		this.compressionManager = compressionManager;
		this.storageService = storageService;
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookXmlService#getPrivateProfile(java.lang.String, java.lang.String)
	 */
	public PrivateProfileDocument getPrivateProfile(String username, String password) throws PeerBookException {

		byte[] encryptedPrivateProfileBytes = storageService.getPrivateProfile(username);

		SecretKey passwordKey;
		byte[] decryptedPrivateProfileBytes = null;
		try {
			passwordKey = encryptionManager.generatePasswordBasedKey(password);
			decryptedPrivateProfileBytes = encryptionManager.decrypt(encryptedPrivateProfileBytes, passwordKey);
		} catch (GeneralSecurityException e) {
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
			throw new PeerBookException(e);
		}

		try {
			String decompressedPrivateProfile = compressionManager.deCompress(decryptedPrivateProfileBytes);
			return PrivateProfileDocument.Factory.parse(decompressedPrivateProfile);
		} catch (XmlException e) {
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
			throw new FileAccessException("Error parsing private profile document, probably wrong password: " + username);
		} catch (DataFormatException e) {
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
			throw new FileAccessException();
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookXmlService#getPublicProfile(java.lang.String, javax.crypto.SecretKey)
	 */
	public PublicProfileDocument getPublicProfile(String username, SecretKey key) throws PeerBookException {
		byte[] encryptedPublicProfileBytes = storageService.getPublicProfile(username);

		byte[] decryptedPublicProfileBytes;
		try {
			decryptedPublicProfileBytes = encryptionManager.decrypt(encryptedPublicProfileBytes, key);
		} catch (GeneralSecurityException e) {
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
			throw new PeerBookException(e);
		}

		try {
			String decompressedPublicProfile = compressionManager.deCompress(decryptedPublicProfileBytes);
			return PublicProfileDocument.Factory.parse(decompressedPublicProfile);
		} catch (XmlException e) {
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
			throw new FileAccessException("Error parsing public profile document, probably wrong password: " + username);
		} catch (DataFormatException e) {
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
			throw new FileAccessException();
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookXmlService#getMessages(java.lang.String, java.security.PrivateKey)
	 */
	public List<MessageDocument> getMessages(String username, PrivateKey privateKey) throws PeerBookException {
		List<MessageDocument> decodedMessages = new ArrayList<MessageDocument>();

		EncryptedMessage[] messages = storageService.getMessages(username);

		if (messages != null) {
			for (EncryptedMessage encryptedMessage : messages) {

				PublicKeyEncryptedBytes encryptedMessageAndKey = new PublicKeyEncryptedBytes(encryptedMessage.getEncryptedSecretKey(), encryptedMessage.getEncryptedMessage());
				byte[] decryptedMessageBytes;
				try {
					decryptedMessageBytes = encryptionManager.decrypt(encryptedMessageAndKey, privateKey);
				} catch (GeneralSecurityException e1) {
					Diagnostic.trace(DiagnosticLevel.RUNALL, e1);
					throw new PeerBookException(e1);
				}

				try {
					String decompressedMessage = compressionManager.deCompress(decryptedMessageBytes);
					MessageDocument message = MessageDocument.Factory.parse(decompressedMessage);
					PublicKey fromPublicKey = storageService.getPublicKey(message.getMessage().getFromUserName());

					if (encryptionManager.verifySignature(encryptedMessage.getSignature(), decryptedMessageBytes, fromPublicKey)) {
						decodedMessages.add(message);
					}
				} catch (XmlException e) {
					Diagnostic.trace(DiagnosticLevel.RUNALL, e);
					throw new FileAccessException("Error parsing message document, probably wrong private key: " + username);
				} catch (DataFormatException e) {
					Diagnostic.trace(DiagnosticLevel.RUNALL, e);
					throw new FileAccessException();
				} catch (GeneralSecurityException e) {
					Diagnostic.trace(DiagnosticLevel.RUNALL, e);
					throw new PeerBookException(e);
				}
			}
		}

		return decodedMessages;
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookXmlService#savePrivateProfile(java.lang.String, java.lang.String, uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.PrivateProfileDocument)
	 */
	public void savePrivateProfile(String username, String password, PrivateProfileDocument newPrivateProfileDoc) throws PeerBookException {
		try {
			SecretKey profileKey = encryptionManager.generatePasswordBasedKey(password);
			byte[] decryptedPrivateProfileBytes = compressionManager.compress(newPrivateProfileDoc.toString());
			byte[] encryptedPrivateProfileBytes = encryptionManager.encrypt(decryptedPrivateProfileBytes, profileKey);
			storageService.savePrivateProfile(encryptedPrivateProfileBytes, username);
		} catch (GeneralSecurityException e) {
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
			throw new PeerBookException(e);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookXmlService#savePublicProfile(uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.PublicProfileDocument, javax.crypto.SecretKey)
	 */
	public void savePublicProfile(PublicProfileDocument newPublicProfileDoc, SecretKey publicProfileKey) throws PeerBookException {
		try {
			String username = newPublicProfileDoc.getPublicProfile().getPersonalData().getName();
			byte[] decryptedPublicProfileBytes = compressionManager.compress(newPublicProfileDoc.toString());
			byte[] encryptedPublicProfileBytes = encryptionManager.encrypt(decryptedPublicProfileBytes, publicProfileKey);
			storageService.savePublicProfile(encryptedPublicProfileBytes, username);
		} catch (GeneralSecurityException e) {
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
			throw new PeerBookException(e);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookXmlService#saveMessage(uk.ac.stAndrews.cs.hostBrb5.peerBook.message.MessageDocument, java.security.PrivateKey, java.lang.String)
	 */
	public void saveMessage(MessageDocument messageDoc, PrivateKey fromPrivateKey, String to) throws PeerBookException {
		try {
			byte[] decryptedMessageBytes = compressionManager.compress(messageDoc.toString());
			PublicKeyEncryptedBytes encryptedMessageBytes = encryptionManager.encrypt(decryptedMessageBytes, storageService.getPublicKey(to));
			byte[] signatureBytes = encryptionManager.generateSignature(decryptedMessageBytes, fromPrivateKey);

			EncryptedMessage encryptedMessage = new EncryptedMessage(encryptedMessageBytes.getEncryptedSecretKey(), encryptedMessageBytes.getCipherText(), signatureBytes);
			storageService.saveMessage(encryptedMessage, to);
		} catch (GeneralSecurityException e) {
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
			throw new PeerBookException(e);
		}
	}

}

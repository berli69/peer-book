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
package uk.ac.stand.cs.brb5.interfaces;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import uk.ac.standrews.cs.nds.p2p.interfaces.IKey;

/**
 * 
 * Abstracts over a network-accessible service that stores Messages for some range of keys.
 * 
 * @author Ben
 *
 */
public interface IMessageStorageService extends Remote {

	/**
	 * Used to locate the service via RMI.
	 */
	public static final String BOUND_NAME = "MESSAGE_STORAGE_SERVICE";

	/**
	 * @param usernameHash Key corresponding to the username whose messages are to be retrieved.
	 * @return Array containing encrypted messages belonging to the username who corresponds to the key.
	 * @throws IOException
	 */
	public EncryptedMessage[] getMessages(IKey usernameHash) throws IOException;
	/**
	 * @param messageBytes Message to be saved.
	 * @param usernameHash Key of username who is the message recipient.
	 * @throws IOException
	 */
	public void saveMessage(EncryptedMessage messageBytes, IKey usernameHash) throws IOException;
	/**
	 * @param usernameHash Key of username whose messages are to be deleted.
	 * @throws IOException
	 */
	public void deleteMessages(IKey usernameHash) throws IOException;
	/**
	 * @return A list of keys whose messages are locally stored.
	 * @throws RemoteException
	 */
	public List<IKey> getStoredKeys() throws RemoteException;

	/**
	 * 
	 * A wrapper around a sent message, including the ciphertext, encrypted symmetric key
	 * which was used to encrypt the plaintext, and attached signature.
	 * 
	 * @author Ben
	 *
	 */
	public class EncryptedMessage implements Serializable {

		private static final long serialVersionUID = -4859446141772032232L;

		private byte[] encryptedSecretKey;
		private byte[] encryptedMessage;
		private byte[] signature;

		/**
		 * @param encryptedSecretKey The encrypted symmetric key which was used to encrypt the message.
		 * @param encryptedMessage The message ciphertext.
		 * @param signature Digital signature of the message sender.
		 */
		public EncryptedMessage(byte[] encryptedSecretKey, byte[] encryptedMessage, byte[] signature) {
			this.encryptedSecretKey = encryptedSecretKey;
			this.encryptedMessage = encryptedMessage;
			this.signature = signature;
		}

		/**
		 * @return The symmetric key used to encrypt the message plaintext, encrypted with the recipient's
		 * public key.
		 */
		public byte[] getEncryptedSecretKey() {
			return encryptedSecretKey;
		}

		/**
		 * @return The message ciphertext.
		 */
		public byte[] getEncryptedMessage() {
			return encryptedMessage;
		}

		/**
		 * @return Digital signature of the sender.
		 */
		public byte[] getSignature() {
			return signature;
		}
		
		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			int hashCode = 1;
			
			for (int i = 0; i < encryptedSecretKey.length; i++) {
				hashCode = hashCode * 31 + encryptedSecretKey[i];
			}
			
			for (int i = 0; i < encryptedMessage.length; i++) {
				hashCode = hashCode * 31 + encryptedMessage[i];
			}
			
			for (int i = 0; i < signature.length; i++) {
				hashCode = hashCode * 31 + signature[i];
			}
			
			return hashCode;
		}
		
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object other) {
			if (other instanceof EncryptedMessage) {
				other = (EncryptedMessage) other;
				return hashCode() == other.hashCode();
			} else {
				return false;
			}
		}

	}

}

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

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.List;
import java.util.Observer;

import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.interfaces.IMessageStorageService.EncryptedMessage;
import uk.ac.standrews.cs.stachordRMI.interfaces.IChordNode;

/**
 * 
 * Interface for the entire storage layer.
 * 
 * @author Ben
 *
 */
public interface IStorageService extends Observer {
	
	/**
	 * @param username Username whose Public Profile is to be retrieved.
	 * @return An encrypted Public Profile.
	 * @throws PeerBookException
	 */
	public byte[] getPublicProfile(String username) throws PeerBookException;
	/**
	 * @param username Username whose Private Profile is to be retrieved.
	 * @return An encrypted Private Profile.
	 * @throws PeerBookException
	 */
	public byte[] getPrivateProfile(String username) throws PeerBookException;
	/**
	 * @param username Username whose Messages are to be retrieved.
	 * @return Encrypted Messages.
	 * @throws PeerBookException
	 */
	public EncryptedMessage[] getMessages(String username) throws PeerBookException;
	/**
	 * @param username Username whos Public Key is to be retrieved.
	 * @return A Public Key.
	 * @throws PeerBookException
	 */
	public PublicKey getPublicKey(String username) throws PeerBookException;
	/**
	 * @param search A search string.
	 * @return Search results.
	 * @throws PeerBookException
	 */
	public List<String> searchUsername(String search) throws PeerBookException;
	
	/**
	 * @param publicProfileBytes An encrypted Public Profile.
	 * @param username Username who owns the Public Profile being saved.
	 * @throws PeerBookException
	 */
	public void savePublicProfile(byte[] publicProfileBytes, String username) throws PeerBookException;
	/**
	 * @param privateProfileBytes An encrypted Private Profile.
	 * @param username Username who owns the Private Profile being saved.
	 * @throws PeerBookException
	 */
	public void savePrivateProfile(byte[] privateProfileBytes, String username) throws PeerBookException;
	/**
	 * @param message An encrypted Message.
	 * @param username Username who owns the Message being saved.
	 * @throws PeerBookException
	 */
	public void saveMessage(EncryptedMessage message, String username) throws PeerBookException;
	/**
	 * @param key A Public Key to be saved.
	 * @param username Username who owns the Public Key being saved.
	 * @throws PeerBookException
	 * @throws GeneralSecurityException
	 */
	public void savePublicKey(PublicKey key, String username) throws PeerBookException, GeneralSecurityException;
	/**
	 * @param key A new Public Key to be saved.
	 * @param signature The user's signature of their username (to verify this change).
	 * @param username The username whose Public Key is to be saved.
	 * @throws PeerBookException
	 * @throws GeneralSecurityException
	 */
	public void changePublicKey(PublicKey key, byte[] signature, String username) throws PeerBookException, GeneralSecurityException;
	
	/**
	 * @return The local {@link IPublicProfileStorageService}.
	 */
	public IPublicProfileStorageService getLocalPublicProfileStorageService();
	/**
	 * @return The local {@link IPrivateProfileStorageService}.
	 */
	public IPrivateProfileStorageService getLocalPrivateProfileStorageService();
	/**
	 * @return The local {@link IMessageStorageService}.
	 */
	public IMessageStorageService getLocalMessageStorageService();
	/**
	 * @return The local {@link IPublicKeyDatabase}.
	 */
	public IPublicKeyDatabase getLocalPublicKeyDatabase();
	/**
	 * @return The local {@link IChordNode}.
	 */
	public IChordNode getLocalNode();

}

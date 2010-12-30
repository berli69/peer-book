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

import java.security.PrivateKey;
import java.util.List;

import javax.crypto.SecretKey;

import uk.ac.stAndrews.cs.hostBrb5.peerBook.message.MessageDocument;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.PrivateProfileDocument;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.PublicProfileDocument;
import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;

/**
 * 
 * Part of the kernel layer, used to abstract over having to deal with encryption/compression.
 * 
 * @author Ben
 *
 */
public interface IPeerBookXmlService {

	/**
	 * @param username Username of the Private Profile that is being retrieved.
	 * @param password Password of the user whose Private Profile that is being retrieved.
	 * @return A Private Profile.
	 * @throws PeerBookException
	 */
	public PrivateProfileDocument getPrivateProfile(String username, String password) throws PeerBookException;
	/**
	 * @param username Username of the Public Profile that is being retrieved.
	 * @param key Shared key that encrypts the Public Profile that is being retrieved.
	 * @return A Public Profile.
	 * @throws PeerBookException
	 */
	public PublicProfileDocument getPublicProfile(String username, SecretKey key) throws PeerBookException;
	/**
	 * @param username Username whose Messages are being retrieved.
	 * @param privateKey The private key of the user whose Messages are being retrieved.
	 * @return Messages belonging to the user.
	 * @throws PeerBookException
	 */
	public List<MessageDocument> getMessages(String username, PrivateKey privateKey) throws PeerBookException;

	/**
	 * @param username Username whose Private Profile is being saved.
	 * @param password Password of the user whose Private Profile is being saved.
	 * @param newPrivateProfileDoc The Private Profile to be saved.
	 * @throws PeerBookException
	 */
	public void savePrivateProfile(String username, String password, PrivateProfileDocument newPrivateProfileDoc) throws PeerBookException;
	/**
	 * @param newPublicProfileDoc The Public Profile to be saved.
	 * @param publicProfileKey Shared key that encrypts the Public Profile that is being saved.
	 * @throws PeerBookException
	 */
	public void savePublicProfile(PublicProfileDocument newPublicProfileDoc, SecretKey publicProfileKey) throws PeerBookException;
	/**
	 * @param messageDoc Message to be saved.
	 * @param fromPrivateKey Private Key of message sender.
	 * @param to Recipient of the message.
	 * @throws PeerBookException
	 */
	public void saveMessage(MessageDocument messageDoc, PrivateKey fromPrivateKey, String to) throws PeerBookException;

}

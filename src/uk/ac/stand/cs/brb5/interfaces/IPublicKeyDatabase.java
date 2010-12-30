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
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.List;

import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;

/**
 * 
 * Interface for a database holding Username -> Public Key mappings.
 * 
 * @author Ben
 *
 */
public interface IPublicKeyDatabase extends Remote {

	/**
	 * Used to locate the service via RMI.
	 */
	public static final String BOUND_NAME = "PUBLIC_KEY_DATABASE_PROXY";

	/**
	 * @param username Username whose Public Key is being retrieved.
	 * @return A Public Key.
	 * @throws GeneralSecurityException
	 * @throws RemoteException
	 */
	public PublicKey getPublicKey(String username) throws GeneralSecurityException, RemoteException;

	/**
	 * @param Username whose Public Key is being saved.
	 * @param key Public Key to be stored.
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public void savePublicKey(String username, PublicKey key) throws IOException, GeneralSecurityException;

	/**
	 * @param username Username whose Public Key is being changed.
	 * @param signature Signature by the user of their username (to verify this change). Signature is created with the user's
	 * old Private Key.
	 * @param key The new Public Key.
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public void changePublicKey(String username, byte[] signature, PublicKey key) throws IOException, GeneralSecurityException;

	/**
	 * @param oldUsername Username whose name is being changed.
	 * @param newUsername New username.
	 * @param signature Signature by the user of their new username (to verify this change).
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public void changeUsername(String oldUsername, String newUsername, byte[] signature) throws GeneralSecurityException, IOException;

	/**
	 * @return The usernames of Public Keys stored locally on this service.
	 * @throws RemoteException
	 */
	public List<String> getStoredUsernames() throws RemoteException;

	/**
	 * @param search A search String.
	 * @return Possible search results.
	 * @throws RemoteException
	 * @throws PeerBookException
	 */
	public List<String> searchUsername(String search) throws RemoteException, PeerBookException;

}

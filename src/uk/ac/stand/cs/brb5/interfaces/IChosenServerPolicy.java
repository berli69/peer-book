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
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.List;
import java.util.Set;

import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.interfaces.IMessageStorageService.EncryptedMessage;
import uk.ac.standrews.cs.nds.p2p.interfaces.IKey;

/**
 * 
 * Abstraction over a policy which decides which out of a set of possible replicas, which replica to choose to return a given file.
 * 
 * @author Ben
 *
 */
public interface IChosenServerPolicy {

	/**
	 * @param set The Public Profile replica set.
	 * @param usernameHash The key corresponding to the Public Profile that is being retrieved.
	 * @return The chosen Public profile.
	 * @throws IOException
	 */
	public byte[] choosePublicProfile(Set<IPublicProfileStorageService> set, IKey usernameHash) throws IOException;

	/**
	 * @param set The Private Profile replica set.
	 * @param usernameHash The key corresponding to the Private Profile that is being retrieved.
	 * @return The chosen Private Profile.
	 * @throws IOException
	 */
	public byte[] choosePrivateProfile(Set<IPrivateProfileStorageService> set, IKey usernameHash) throws IOException;

	/**
	 * @param messageStorageServices The Message replica set.
	 * @param usernameHash The key corresponding to the Messages that are being received.
	 * @return The chosen array of Messages.
	 * @throws IOException
	 */
	public EncryptedMessage[] chooseMessages(Set<IMessageStorageService> messageStorageServices, IKey usernameHash) throws IOException;

	/**
	 * @param publicKeyDatabases The Public Key database replica set.
	 * @param username The username whose Public Key is being retrieved.
	 * @return The chosen Public Key database.
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 */
	public PublicKey choosePublicKey(Set<IPublicKeyDatabase> publicKeyDatabases, String username) throws IOException, GeneralSecurityException;

	/**
	 * @param publicKeyDatabases The Public Key database replica set.
	 * @param search The search string.
	 * @return The chosen Public Key database.
	 * @throws RemoteException
	 * @throws PeerBookException
	 */
	public List<String> chooseSearchResults(Set<IPublicKeyDatabase> publicKeyDatabases, String search) throws RemoteException, PeerBookException;

}

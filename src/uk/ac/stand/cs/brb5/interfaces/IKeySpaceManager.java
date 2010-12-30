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

import java.util.Set;

import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.standrews.cs.nds.p2p.interfaces.IKey;

/**
 * Used as an abstraction around any dealings with the peer-to-peer keyspace.
 * 
 * @author Ben
 *
 */
public interface IKeySpaceManager {

	/**
	 * The minimum length of a username on the PeerBook system.
	 */
	public static final int MINIMUM_USERNAME_LENGTH = 5;

	/**
	 * Starts replica set fixing tasks.
	 * 
	 * @param delay Delay until tasks begin (milliseconds).
	 * @param period Period of time between individual tasks (milliseconds).
	 */
	public void startTasks(long delay, long period);
	/**
	 * Stops replica set fixing tasks.
	 */
	public void stopTasks();
	/**
	 * Begins an on-demand replica set fix.
	 */
	public void fixReplicaSet();

	/**
	 * @param publicProfileName The username for a Public Profile.
	 * @return A key corresponding to the Public Profile for the given username.
	 */
	public IKey hashPublicProfileName(String publicProfileName);
	/**
	 * @param privateProfileName The username for a Private Profile.
	 * @return A key corresponding to the Private Profile for the given username.
	 */
	public IKey hashPrivateProfileName(String privateProfileName);
	/**
	 * @param messageRecipientName The username for a set of Messages.
	 * @return A key corresponding to the Messages for the given username.
	 */
	public IKey hashMessageRecipientName(String messageRecipientName);
	/**
	 * @param shortPublicKeyName The username for a Public Key.
	 * @return A key corresponding to the Public Key for the given username.
	 */
	public IKey hashPublicKeyName(String shortPublicKeyName);

	/**
	 * @param usernameHash A key corresponding to a hash of the username whose messages that are being located.
	 * @return Replica set of {@link IMessageStorageService}s where the messages should be located.
	 * @throws PeerBookException
	 */
	public Set<IMessageStorageService> locateMessageStores(IKey usernameHash) throws PeerBookException;
	/**
	 * @param usernameHash A key corresponding to a hash of the username whose private profile is being located.
	 * @return Replica set of {@link IPrivateProfileStorageService}s where the private profile should be located.
	 * @throws PeerBookException
	 */
	public Set<IPrivateProfileStorageService> locatePrivateProfileStores(IKey usernameHash) throws PeerBookException;
	/**
	 * @param usernameHash A key corresponding to a hash of the username whose public profile is being located.
	 * @return Replica set of {@link IPublicProfileStorageService}s where the public profile should be located.
	 * @throws PeerBookException
	 */
	public Set<IPublicProfileStorageService> locatePublicProfileStores(IKey usernameHash) throws PeerBookException;
	/**
	 * @param usernameHash A key corresponding to a hash of the username whose public key is being located.
	 * @return Replica set of {@link IPublicKeyDatabase}s where the public key should be located.
	 * @throws PeerBookException
	 */
	public Set<IPublicKeyDatabase> locatePublicKeyDatabases(IKey usernameHash) throws PeerBookException;

	/**
	 * @param key Key to be tested.
	 * @return True if the given key is in the local node's key range, false otherwise.
	 */
	public boolean inLocalKeyRange(IKey key);

}

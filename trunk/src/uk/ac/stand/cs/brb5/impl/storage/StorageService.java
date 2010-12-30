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
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import uk.ac.stand.cs.brb5.impl.exceptions.FileAccessException;
import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.impl.keyspace.KeySpaceManager;
import uk.ac.stand.cs.brb5.interfaces.IChosenServerPolicy;
import uk.ac.stand.cs.brb5.interfaces.IChosenServerPolicyFactory;
import uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager;
import uk.ac.stand.cs.brb5.interfaces.IMessageStorageService;
import uk.ac.stand.cs.brb5.interfaces.IPrivateProfileStorageService;
import uk.ac.stand.cs.brb5.interfaces.IPublicKeyDatabase;
import uk.ac.stand.cs.brb5.interfaces.IPublicProfileStorageService;
import uk.ac.stand.cs.brb5.interfaces.IStorageService;
import uk.ac.stand.cs.brb5.interfaces.IMessageStorageService.EncryptedMessage;
import uk.ac.standrews.cs.nds.eventModel.IEvent;
import uk.ac.standrews.cs.nds.p2p.interfaces.IKey;
import uk.ac.standrews.cs.nds.util.Diagnostic;
import uk.ac.standrews.cs.nds.util.DiagnosticLevel;
import uk.ac.standrews.cs.stachordRMI.impl.ChordNodeImpl;
import uk.ac.standrews.cs.stachordRMI.interfaces.IChordNode;

/**
 * Implements a {@link StorageService} layer that may be used by the layer above.
 * 
 * @author Ben
 *
 */
public class StorageService implements IStorageService {

	private static final long KEYSPACE_TASK_DELAY = 10 * 1000; // 10 seconds.
	private static final long KEYSPACE_TASK_PERIOD = 30 * 1000; // 30 seconds.

	private IChordNode localNode;
	private IPublicKeyDatabase publicKeyDatabase;
	private IChosenServerPolicy chosenFilePolicy;
	private IKeySpaceManager keySpaceManager;

	private IPublicProfileStorageService publicProfileStorageService;
	private IPrivateProfileStorageService privateProfileStorageService;
	private IMessageStorageService messageStorageService;

	/**
	 * Constructs a new {@link StorageService}, binding a {@link PublicProfileStorageService}, {@link PrivateProfileStorageService},
	 * {@link MessageStorageService}, {@link PublicKeyDatabase} to the RMI registry on the provided port.
	 * 
	 * @param rmiPort Port on which an RMI registry is running.
	 * @param localNode The local {@link IChordNode}.
	 * @param chosenServerPolicyFac A policy factory used to construct a policy which chooses which replica of a replica set to choose from.
	 * @param replicaNumber The number of replicas in a replica set.
	 * @param publicKeyDbProxy A public key database from to which we can read/write.
	 * @param centralisedPublicKeyDb True if the public key database is centralised, false if decentralised.
	 * @throws IOException
	 * @throws FileAccessException
	 */
	public StorageService(int rmiPort, IChordNode localNode, IChosenServerPolicyFactory chosenServerPolicyFac, int replicaNumber, IPublicKeyDatabase publicKeyDbProxy, boolean centralisedPublicKeyDb) throws IOException, FileAccessException {
		this.localNode = localNode;
		this.publicKeyDatabase = publicKeyDbProxy;
		this.chosenFilePolicy = chosenServerPolicyFac.getPolicy();
		this.keySpaceManager = new KeySpaceManager(this, replicaNumber, centralisedPublicKeyDb);
		this.keySpaceManager.startTasks(KEYSPACE_TASK_DELAY, KEYSPACE_TASK_PERIOD);

		this.publicProfileStorageService = new PublicProfileStorageService();
		this.privateProfileStorageService = new PrivateProfileStorageService();
		this.messageStorageService = new MessageStorageService();

		Registry rmiRegistry = LocateRegistry.getRegistry(rmiPort);

		rmiRegistry.rebind(IPublicProfileStorageService.BOUND_NAME, this.publicProfileStorageService);
		rmiRegistry.rebind(IPrivateProfileStorageService.BOUND_NAME, this.privateProfileStorageService);
		rmiRegistry.rebind(IMessageStorageService.BOUND_NAME, this.messageStorageService);
		rmiRegistry.rebind(IPublicKeyDatabase.BOUND_NAME, this.publicKeyDatabase);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IStorageService#getMessages(java.lang.String)
	 */
	public EncryptedMessage[] getMessages(String username) throws PeerBookException {
		try {
			IKey usernameHash = keySpaceManager.hashMessageRecipientName(username);
			Set<IMessageStorageService> messageStorageServices = keySpaceManager.locateMessageStores(usernameHash);
			EncryptedMessage[] messages = chosenFilePolicy.chooseMessages(messageStorageServices, usernameHash);

			for (IMessageStorageService messageStorageService : messageStorageServices) {
				messageStorageService.deleteMessages(usernameHash);
			}

			return messages;
		} catch (IOException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
			throw new FileAccessException();
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IStorageService#getPrivateProfile(java.lang.String)
	 */
	public byte[] getPrivateProfile(String username) throws PeerBookException {
		try {
			IKey usernameHash = keySpaceManager.hashPrivateProfileName(username);
			return chosenFilePolicy.choosePrivateProfile(keySpaceManager.locatePrivateProfileStores(usernameHash), usernameHash);
		} catch (IOException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
			throw new FileAccessException();
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IStorageService#getPublicProfile(java.lang.String)
	 */
	public byte[] getPublicProfile(String username) throws PeerBookException {
		try {
			IKey usernameHash = keySpaceManager.hashPublicProfileName(username);
			return chosenFilePolicy.choosePublicProfile(keySpaceManager.locatePublicProfileStores(usernameHash), usernameHash);
		} catch (IOException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
			throw new FileAccessException();
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IStorageService#getPublicKey(java.lang.String)
	 */
	public PublicKey getPublicKey(String username) throws PeerBookException {
		IKey usernameHash = keySpaceManager.hashPublicKeyName(username);
		try {
			return chosenFilePolicy.choosePublicKey(keySpaceManager.locatePublicKeyDatabases(usernameHash), username);
		} catch (IOException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
			throw new FileAccessException();
		} catch (GeneralSecurityException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
			throw new FileAccessException();
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IStorageService#searchUsername(java.lang.String)
	 */
	public List<String> searchUsername(String search) throws PeerBookException {
		IKey usernameHash = keySpaceManager.hashPublicKeyName(search);
		try {
			return chosenFilePolicy.chooseSearchResults(keySpaceManager.locatePublicKeyDatabases(usernameHash), search);
		} catch (RemoteException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
			throw new FileAccessException();
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IStorageService#savePrivateProfile(byte[], java.lang.String)
	 */
	public void savePrivateProfile(byte[] privateProfileBytes, String username) throws PeerBookException {
		try {
			IKey usernameHash = keySpaceManager.hashPrivateProfileName(username);
			for (IPrivateProfileStorageService privateProfileStorageService : keySpaceManager.locatePrivateProfileStores(usernameHash)) {
				privateProfileStorageService.savePrivateProfile(privateProfileBytes, usernameHash);
			}
		} catch (IOException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
			throw new FileAccessException();
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IStorageService#savePublicProfile(byte[], java.lang.String)
	 */
	public void savePublicProfile(byte[] publicProfileBytes, String username) throws PeerBookException {
		try {
			IKey usernameHash = keySpaceManager.hashPublicProfileName(username);
			for (IPublicProfileStorageService publicProfileStorageService : keySpaceManager.locatePublicProfileStores(usernameHash)) {
				publicProfileStorageService.savePublicProfile(publicProfileBytes, usernameHash);
			}
		} catch (IOException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
			throw new FileAccessException();
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IStorageService#saveMessage(uk.ac.stand.cs.brb5.interfaces.IMessageStorageService.EncryptedMessage, java.lang.String)
	 */
	public void saveMessage(EncryptedMessage messageBytes, String username) throws PeerBookException {
		try {
			IKey usernameHash = keySpaceManager.hashMessageRecipientName(username);
			for (IMessageStorageService messageStorageService : keySpaceManager.locateMessageStores(usernameHash)) {
				messageStorageService.saveMessage(messageBytes, usernameHash);
			}
		} catch (IOException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
			throw new FileAccessException();
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IStorageService#savePublicKey(java.security.PublicKey, java.lang.String)
	 */
	public void savePublicKey(PublicKey key, String username) throws PeerBookException, GeneralSecurityException {
		try {
			IKey usernameHash = keySpaceManager.hashPublicKeyName(username);
			for (IPublicKeyDatabase publicKeyDatabase : keySpaceManager.locatePublicKeyDatabases(usernameHash)) {
				publicKeyDatabase.savePublicKey(username, key);
			}
		} catch (IOException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
			throw new FileAccessException();
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IStorageService#changePublicKey(java.security.PublicKey, byte[], java.lang.String)
	 */
	public void changePublicKey(PublicKey key, byte[] signature, String username) throws PeerBookException, GeneralSecurityException {
		try {
			IKey usernameHash = keySpaceManager.hashPublicKeyName(username);
			for (IPublicKeyDatabase publicKeyDatabase : keySpaceManager.locatePublicKeyDatabases(usernameHash)) {
				publicKeyDatabase.changePublicKey(username, signature, key);
			}
		} catch (IOException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
			throw new FileAccessException();
		}
	}

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if ((IEvent)arg == ChordNodeImpl.PREDECESSOR_CHANGE_EVENT) {
			keySpaceManager.fixReplicaSet();
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IStorageService#getLocalPublicProfileStorageService()
	 */
	public IPublicProfileStorageService getLocalPublicProfileStorageService() {
		return publicProfileStorageService;
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IStorageService#getLocalPrivateProfileStorageService()
	 */
	public IPrivateProfileStorageService getLocalPrivateProfileStorageService() {
		return privateProfileStorageService;
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IStorageService#getLocalMessageStorageService()
	 */
	public IMessageStorageService getLocalMessageStorageService() {
		return messageStorageService;
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IStorageService#getLocalPublicKeyDatabase()
	 */
	public IPublicKeyDatabase getLocalPublicKeyDatabase() {
		return publicKeyDatabase;
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IStorageService#getLocalNode()
	 */
	public IChordNode getLocalNode() {
		return localNode;
	}

}

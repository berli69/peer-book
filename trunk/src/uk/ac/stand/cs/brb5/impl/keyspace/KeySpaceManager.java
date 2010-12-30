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
package uk.ac.stand.cs.brb5.impl.keyspace;

import java.net.InetSocketAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Timer;

import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager;
import uk.ac.stand.cs.brb5.interfaces.IMessageStorageService;
import uk.ac.stand.cs.brb5.interfaces.IPrivateProfileStorageService;
import uk.ac.stand.cs.brb5.interfaces.IPublicKeyDatabase;
import uk.ac.stand.cs.brb5.interfaces.IPublicProfileStorageService;
import uk.ac.stand.cs.brb5.interfaces.IStorageService;
import uk.ac.standrews.cs.nds.p2p.interfaces.IKey;
import uk.ac.standrews.cs.nds.p2p.util.HashBasedKeyFactory;
import uk.ac.standrews.cs.nds.p2p.util.SHA1KeyFactory;
import uk.ac.standrews.cs.nds.util.Diagnostic;
import uk.ac.standrews.cs.nds.util.DiagnosticLevel;
import uk.ac.standrews.cs.stachordRMI.interfaces.IChordRemoteReference;

/**
 * 
 * Provides functionality allowing queries regarding whether this is the primary replica of a 
 * given file. Also takes care of fixing the replica set (using FixReplicaSetTask) on a given
 * timing schedule (provided by the caller).
 * 
 * @author Ben
 *
 */
public class KeySpaceManager implements IKeySpaceManager {

	private static final String MESSAGE_STORE = "_message_store";
	private static final String PUBLIC_PROFILE_STORE = "_public_profile_store";
	private static final String PRIVATE_PROFILE_STORE = "_private_profile_store";
	private static final String PUBLIC_KEY_STORE = "_public_key_store";

	private static HashBasedKeyFactory keyFactory = new SHA1KeyFactory();

	private IStorageService storageService;
	private int replicaNumber;
	private FixReplicaSetTask fixReplicaSet;
	private Timer timer;
	private boolean centralisedPublicKeyDb;

	/**
	 * @param storageService IStorageService on which PeerBook objects are stored.
	 * @param replicaNumber Size of a PeerBook replica set.
	 * @param centralisedPublicKeyDb True if the public key database is centralised, false if decentralised.
	 */
	public KeySpaceManager(IStorageService storageService, int replicaNumber, boolean centralisedPublicKeyDb) {
		this.storageService = storageService;
		this.replicaNumber = replicaNumber;
		this.fixReplicaSet = new FixReplicaSetTask(this, storageService);
		this.centralisedPublicKeyDb = centralisedPublicKeyDb;
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager#startTasks(long, long)
	 */
	public void startTasks(long delay, long period) {
		this.timer = new Timer();
		timer.schedule(fixReplicaSet, delay, period);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager#stopTasks()
	 */
	public void stopTasks() {
		timer.cancel();
		timer = null;
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager#fixReplicaSet()
	 */
	public void fixReplicaSet() {

		boolean timerWasNull = timer == null;
		if (timerWasNull) {
			timer = new Timer();
		}

		timer.schedule(new FixReplicaSetTask(this, storageService), 0);

		if (timerWasNull) {
			timer = null;
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager#locatePublicProfileStores(IKey)
	 */
	public Set<IPublicProfileStorageService> locatePublicProfileStores(IKey usernameHash) throws PeerBookException {
		try {
			Set<Registry> remoteRegistries = locateReplicaSet(usernameHash);
			Set<IPublicProfileStorageService> publicProfileStorageServices = new HashSet<IPublicProfileStorageService>();

			for (Registry registry : remoteRegistries) {
				publicProfileStorageServices.add((IPublicProfileStorageService) registry.lookup(IPublicProfileStorageService.BOUND_NAME));
			}

			return publicProfileStorageServices;
		} catch (RemoteException e) {
			Diagnostic.trace(DiagnosticLevel.RUN, e);
			throw new PeerBookException(e);
		} catch (NotBoundException e) {
			Diagnostic.trace(DiagnosticLevel.RUN, e);
			throw new PeerBookException(e);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager#locatePrivateProfileStores(IKey)
	 */
	public Set<IPrivateProfileStorageService> locatePrivateProfileStores(IKey usernameHash) throws PeerBookException {
		try {			
			Set<Registry> remoteRegistries = locateReplicaSet(usernameHash);
			Set<IPrivateProfileStorageService> privateProfileStorageServices = new HashSet<IPrivateProfileStorageService>();

			for (Registry registry : remoteRegistries) {
				privateProfileStorageServices.add((IPrivateProfileStorageService) registry.lookup(IPrivateProfileStorageService.BOUND_NAME));
			}

			return privateProfileStorageServices;
		} catch (RemoteException e) {
			Diagnostic.trace(DiagnosticLevel.RUN, e);
			throw new PeerBookException(e);
		} catch (NotBoundException e) {
			Diagnostic.trace(DiagnosticLevel.RUN, e);
			throw new PeerBookException(e);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager#locateMessageStores(IKey)
	 */
	public Set<IMessageStorageService> locateMessageStores(IKey usernameHash) throws PeerBookException {
		try {
			Set<Registry> remoteRegistries = locateReplicaSet(usernameHash);
			Set<IMessageStorageService> messageStorageServices = new HashSet<IMessageStorageService>();

			for (Registry registry : remoteRegistries) {
				messageStorageServices.add((IMessageStorageService) registry.lookup(IMessageStorageService.BOUND_NAME));
			}

			return messageStorageServices;
		} catch (RemoteException e) {
			Diagnostic.trace(DiagnosticLevel.RUN, e);
			throw new PeerBookException(e);
		} catch (NotBoundException e) {
			Diagnostic.trace(DiagnosticLevel.RUN, e);
			throw new PeerBookException(e);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager#locatePublicKeyDatabases(IKey)
	 */
	public Set<IPublicKeyDatabase> locatePublicKeyDatabases(IKey usernameHash) throws PeerBookException {
		Set<IPublicKeyDatabase> publicKeyDatabases = new HashSet<IPublicKeyDatabase>();
		
		if (centralisedPublicKeyDb) {
			publicKeyDatabases.add(storageService.getLocalPublicKeyDatabase());
		} else {
			try {
				Set<Registry> remoteRegistries = locateReplicaSet(usernameHash);

				for (Registry registry : remoteRegistries) {
					publicKeyDatabases.add((IPublicKeyDatabase) registry.lookup(IPublicKeyDatabase.BOUND_NAME));
				}

			} catch (RemoteException e) {
				Diagnostic.trace(DiagnosticLevel.RUN, e);
				throw new PeerBookException(e);
			} catch (NotBoundException e) {
				Diagnostic.trace(DiagnosticLevel.RUN, e);
				throw new PeerBookException(e);
			}
		}

		return publicKeyDatabases;
	}

	/** 
	 * @see uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager#hashPublicProfileName(java.lang.String)
	 */
	public IKey hashPublicProfileName(String publicProfileName) {
		return hash(publicProfileName + PUBLIC_PROFILE_STORE);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager#hashPrivateProfileName(java.lang.String)
	 */
	public IKey hashPrivateProfileName(String privateProfileName) {
		return hash(privateProfileName + PRIVATE_PROFILE_STORE);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager#hashMessageRecipientName(java.lang.String)
	 */
	public IKey hashMessageRecipientName(String messageRecipientName) {
		return hash(messageRecipientName + MESSAGE_STORE);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager#hashPublicKeyName(java.lang.String)
	 */
	public IKey hashPublicKeyName(String shortPublicKeyName) {
		return hash(shortPublicKeyName.substring(0, MINIMUM_USERNAME_LENGTH).toLowerCase() + PUBLIC_KEY_STORE);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager#inLocalKeyRange(uk.ac.standrews.cs.nds.p2p.interfaces.IKey)
	 */
	public boolean inLocalKeyRange(IKey key) {
		return storageService.getLocalNode().inLocalKeyRange(key);
	}

	private Set<Registry> locateReplicaSet(IKey key) throws RemoteException {
		Set<Registry> registrySet = new LinkedHashSet<Registry>();

		IChordRemoteReference remoteReference = storageService.getLocalNode().lookup(key);
		InetSocketAddress remoteAddress = remoteReference.getRemote().getAddress();
		registrySet.add(LocateRegistry.getRegistry(remoteAddress.getAddress().getHostAddress(), remoteAddress.getPort()));

		for (int i = 0; i < replicaNumber - 1; i++) {
			remoteReference = remoteReference.getRemote().getSuccessor();
			remoteAddress = remoteReference.getRemote().getAddress();
			registrySet.add(LocateRegistry.getRegistry(remoteAddress.getAddress().getHostAddress(), remoteAddress.getPort()));
		}

		return registrySet;
	}

	private static IKey hash(String s) {
		return keyFactory.generateKey(s);
	}

}

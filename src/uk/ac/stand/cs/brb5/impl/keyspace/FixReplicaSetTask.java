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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Iterator;
import java.util.Set;
import java.util.TimerTask;

import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager;
import uk.ac.stand.cs.brb5.interfaces.IMessageStorageService;
import uk.ac.stand.cs.brb5.interfaces.IPrivateProfileStorageService;
import uk.ac.stand.cs.brb5.interfaces.IPublicKeyDatabase;
import uk.ac.stand.cs.brb5.interfaces.IPublicProfileStorageService;
import uk.ac.stand.cs.brb5.interfaces.IStorageService;
import uk.ac.stand.cs.brb5.interfaces.IMessageStorageService.EncryptedMessage;
import uk.ac.standrews.cs.nds.p2p.interfaces.IKey;
import uk.ac.standrews.cs.nds.util.Diagnostic;
import uk.ac.standrews.cs.nds.util.DiagnosticLevel;
import uk.ac.standrews.cs.stachordRMI.interfaces.IChordNode;

/**
 * 
 * TimerTask/Runnable class that fixes a replica set by re-storing a given file where it should be on the
 * PeerBook network.
 * 
 * Currently only re-stores files for which this node is the primary replica.
 * TODO: Make changes so that the node re-stores files for which this node is any replica.
 * 
 * @author Ben
 *
 */
public class FixReplicaSetTask extends TimerTask {

	private static final long SLEEP_TIME = 1000; // One second.

	private IKeySpaceManager keySpaceManager;
	private IStorageService storageService;

	/**
	 * @param keySpaceManager IKeySpaceManager used to calculate if PeerBook objects are in this node's keyspace.
	 * @param storageService IStorageService used to fetch other types of Services (Public/Private Profiles, etc).
	 */
	public FixReplicaSetTask(IKeySpaceManager keySpaceManager, IStorageService storageService) {
		this.keySpaceManager = keySpaceManager;
		this.storageService = storageService;
	}

	/**
	 * Replicates any local data to any appropriate replicas on the network.
	 * 
	 * @see java.util.TimerTask#run()
	 */
	public void run() {
		try {
			IChordNode localNode = storageService.getLocalNode();
			waitForNodePredecessor(localNode);

			// Because of downstream replication, we already have files. So re-store them, and in doing so, the replica set will be updated.			
			fixLocalPublicProfilesReplicaSet();
			fixLocalPrivateProfilesReplicaSet();
			fixLocalMessagesReplicaSet();
			fixLocalPublicKeysReplicaSet();
		} catch (Exception e) {
			//If ANY exception occurs during this task, we do not want to prevent future fixes, so catch EVERYTHING.
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
		}
	}

	private void fixLocalPublicProfilesReplicaSet() throws IOException, PeerBookException {
		IPublicProfileStorageService localPublicProfileStorageService = storageService.getLocalPublicProfileStorageService();

		for (IKey usernameKey : localPublicProfileStorageService.getStoredKeys()) {
			Set<IPublicProfileStorageService> publicProfileStorageServices = keySpaceManager.locatePublicProfileStores(usernameKey);
//			if (publicProfileStorageServices.contains(localPublicProfileStorageService)) {
			if (keySpaceManager.inLocalKeyRange(usernameKey)) {

				byte[] publicProfileBytes = localPublicProfileStorageService.getPublicProfile(usernameKey);

				Iterator<IPublicProfileStorageService> publicProfileIter = publicProfileStorageServices.iterator();

				// Do not want the first one (we already are), so call next():
				// TODO: change below to instead use !.equals() [RMI tests necessary]
				publicProfileIter.next();
				while (publicProfileIter.hasNext()) {
					publicProfileIter.next().savePublicProfile(publicProfileBytes, usernameKey);
				}
			}
		}
	}

	private void fixLocalPrivateProfilesReplicaSet() throws IOException, PeerBookException {
		IPrivateProfileStorageService localPrivateProfileStorageService = storageService.getLocalPrivateProfileStorageService();

		for (IKey username : localPrivateProfileStorageService.getStoredKeys()) {
			if (keySpaceManager.inLocalKeyRange(username)) {

				byte[] privateProfileBytes = localPrivateProfileStorageService.getPrivateProfile(username);

				Set<IPrivateProfileStorageService> privateProfileStorageServices = keySpaceManager.locatePrivateProfileStores(username);
				Iterator<IPrivateProfileStorageService> privateProfileIter = privateProfileStorageServices.iterator();

				// Do not want the first one (we already are), so call next():
				// TODO: change below to instead use !.equals() [RMI tests necessary]
				privateProfileIter.next();
				while (privateProfileIter.hasNext()) {
					privateProfileIter.next().savePrivateProfile(privateProfileBytes, username);
				}
			}
		}
	}

	private void fixLocalMessagesReplicaSet() throws IOException, PeerBookException {
		IMessageStorageService localMessageStorageService = storageService.getLocalMessageStorageService();

		for (IKey usernameHash : localMessageStorageService.getStoredKeys()) {
			if (keySpaceManager.inLocalKeyRange(usernameHash)) {

				EncryptedMessage[] encryptedMessages = localMessageStorageService.getMessages(usernameHash);

				Set<IMessageStorageService> messageStorageServices = keySpaceManager.locateMessageStores(usernameHash);
				Iterator<IMessageStorageService> messageStoreIter = messageStorageServices.iterator();

				// Do not want the first one (we already are), so call next():
				// TODO: change below to instead use !.equals() [RMI tests necessary]
				messageStoreIter.next();
				while (messageStoreIter.hasNext()) {

					IMessageStorageService remoteMessageStore = messageStoreIter.next();
					remoteMessageStore.deleteMessages(usernameHash);

					for (EncryptedMessage encryptedMessage : encryptedMessages) {
						remoteMessageStore.saveMessage(encryptedMessage, usernameHash);
					}
				}
			}
		}
	}

	private void fixLocalPublicKeysReplicaSet() throws GeneralSecurityException, IOException, PeerBookException {
		IPublicKeyDatabase localPublicKeyDatabase = storageService.getLocalPublicKeyDatabase();

		for (String username : localPublicKeyDatabase.getStoredUsernames()) {
			if (keySpaceManager.inLocalKeyRange(keySpaceManager.hashPublicKeyName(username))) {
				PublicKey publicKey = localPublicKeyDatabase.getPublicKey(username);

				Set<IPublicKeyDatabase> publicKeyDatabases = keySpaceManager.locatePublicKeyDatabases(keySpaceManager.hashPublicKeyName(username));
				Iterator<IPublicKeyDatabase> publicKeyDbIter = publicKeyDatabases.iterator();

				// Do not want the first one (we already are), so call next():
				// TODO: change below to instead use !.equals() [RMI tests necessary]
				publicKeyDbIter.next();
				while (publicKeyDbIter.hasNext()) {
					publicKeyDbIter.next().savePublicKey(username, publicKey);
				}
			}
		}

	}

	private static void waitForNodePredecessor(IChordNode localNode) {
		// Waits for the predecessor node to be fixed.
		while (localNode.getPredecessor() == null) {
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				Diagnostic.trace(DiagnosticLevel.RUN, e);
			}
		}
	}

}

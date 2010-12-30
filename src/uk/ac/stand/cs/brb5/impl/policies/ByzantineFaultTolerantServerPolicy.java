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
package uk.ac.stand.cs.brb5.impl.policies;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.impl.util.ArrayUtils;
import uk.ac.stand.cs.brb5.impl.util.ThresholdChooser;
import uk.ac.stand.cs.brb5.interfaces.IChosenServerPolicy;
import uk.ac.stand.cs.brb5.interfaces.IMessageStorageService;
import uk.ac.stand.cs.brb5.interfaces.IPrivateProfileStorageService;
import uk.ac.stand.cs.brb5.interfaces.IPublicKeyDatabase;
import uk.ac.stand.cs.brb5.interfaces.IPublicProfileStorageService;
import uk.ac.stand.cs.brb5.interfaces.IMessageStorageService.EncryptedMessage;
import uk.ac.standrews.cs.nds.p2p.interfaces.IKey;

/**
 * 
 * Implements a byzantine fault tolerant choice policy. This class provides no guarantees as to what happens if
 * a 'correct' replica is not found.
 * 
 * @author Ben
 *
 */
public class ByzantineFaultTolerantServerPolicy implements IChosenServerPolicy {

	private int goodNodes;

	/**
	 * @param replicaNumber The replica number in this PeerBook network.
	 */
	public ByzantineFaultTolerantServerPolicy(int replicaNumber) {
		// Calculation derived from Byzantine Fault Tolerance equation.
		goodNodes = replicaNumber - ((replicaNumber - 1) / 3);
	}

	/**
	 * Byzantine fault tolerant choice.
	 * 
	 * @see uk.ac.stand.cs.brb5.interfaces.IChosenServerPolicy#chooseMessages(java.util.Set, uk.ac.standrews.cs.nds.p2p.interfaces.IKey)
	 */
	public EncryptedMessage[] chooseMessages(Set<IMessageStorageService> set, IKey usernameHash) throws IOException {
		List<List<EncryptedMessage>> messageArrays = new ArrayList<List<EncryptedMessage>>();

		for (IMessageStorageService messageStore : set) {
			EncryptedMessage[] messages = messageStore.getMessages(usernameHash);
			messageArrays.add(Arrays.asList(messages));
		}
		
		List<EncryptedMessage> choice = new ThresholdChooser<List<EncryptedMessage>>().findFirstAboveThreshold(messageArrays, getThreshold(set));
		
		return choice.toArray(new EncryptedMessage[0]);
	}

	/**
	 * Byzantine fault tolerant choice.
	 * 
	 * @see uk.ac.stand.cs.brb5.interfaces.IChosenServerPolicy#choosePrivateProfile(java.util.Set, uk.ac.standrews.cs.nds.p2p.interfaces.IKey)
	 */
	public byte[] choosePrivateProfile(Set<IPrivateProfileStorageService> set, IKey usernameHash) throws IOException {
		List<List<Byte>> privateProfiles = new ArrayList<List<Byte>>();

		for (IPrivateProfileStorageService privateProfileStore : set) {
			Byte[] privateProfile = ArrayUtils.getObjectByteArray(privateProfileStore.getPrivateProfile(usernameHash));
			privateProfiles.add(Arrays.asList(privateProfile));
		}
		
		List<Byte> choice = new ThresholdChooser<List<Byte>>().findFirstAboveThreshold(privateProfiles, getThreshold(set));
		
		return ArrayUtils.getPrimitiveByteArray(choice.toArray(new Byte[0]));
	}

	/**
	 * Byzantine fault tolerant choice.
	 * 
	 * @see uk.ac.stand.cs.brb5.interfaces.IChosenServerPolicy#choosePublicKey(java.util.Set, java.lang.String)
	 */
	public PublicKey choosePublicKey(Set<IPublicKeyDatabase> set, String username) throws IOException, GeneralSecurityException {
		List<PublicKey> publicKeys = new ArrayList<PublicKey>();

		for (IPublicKeyDatabase privateProfileStore : set) {
			PublicKey publicKey = privateProfileStore.getPublicKey(username);
			if (publicKey != null) {
				publicKeys.add(publicKey);
			}
		}
		
		return new ThresholdChooser<PublicKey>().findFirstAboveThreshold(publicKeys, getThreshold(set));
	}

	/**
	 * Byzantine fault tolerant choice.
	 * 
	 * @see uk.ac.stand.cs.brb5.interfaces.IChosenServerPolicy#choosePublicProfile(java.util.Set, uk.ac.standrews.cs.nds.p2p.interfaces.IKey)
	 */
	public byte[] choosePublicProfile(Set<IPublicProfileStorageService> set, IKey usernameHash) throws IOException {
		List<List<Byte>> publicProfiles = new ArrayList<List<Byte>>();

		for (IPublicProfileStorageService publicProfileStore : set) {
			Byte[] privateProfile = ArrayUtils.getObjectByteArray(publicProfileStore.getPublicProfile(usernameHash));
			publicProfiles.add(Arrays.asList(privateProfile));
		}
		
		List<Byte> choice = new ThresholdChooser<List<Byte>>().findFirstAboveThreshold(publicProfiles, getThreshold(set));
		
		return ArrayUtils.getPrimitiveByteArray(choice.toArray(new Byte[0]));
	}

	/**
	 * Byzantine fault tolerant choice.
	 * 
	 * @see uk.ac.stand.cs.brb5.interfaces.IChosenServerPolicy#chooseSearchResults(java.util.Set, java.lang.String)
	 */
	public List<String> chooseSearchResults(Set<IPublicKeyDatabase> set, String search) throws RemoteException, PeerBookException {
		List<List<String>> searchResults = new ArrayList<List<String>>();

		for (IPublicKeyDatabase publicKeyDb : set) {
			searchResults.add(publicKeyDb.searchUsername(search));
		}
		
		return new ThresholdChooser<List<String>>().findFirstAboveThreshold(searchResults, getThreshold(set));
	}
	
	private int getThreshold(Set<?> set) {
		return Math.min(goodNodes, set.size());
	}

}

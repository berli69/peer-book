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
import java.util.List;
import java.util.Set;

import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.interfaces.IChosenServerPolicy;
import uk.ac.stand.cs.brb5.interfaces.IMessageStorageService;
import uk.ac.stand.cs.brb5.interfaces.IPrivateProfileStorageService;
import uk.ac.stand.cs.brb5.interfaces.IPublicKeyDatabase;
import uk.ac.stand.cs.brb5.interfaces.IPublicProfileStorageService;
import uk.ac.stand.cs.brb5.interfaces.IMessageStorageService.EncryptedMessage;
import uk.ac.standrews.cs.nds.p2p.interfaces.IKey;

/**
 * Policy that simply chooses the primary replica from a replica set.
 * 
 * @author Ben
 *
 */
public class FirstChosenServerPolicy implements IChosenServerPolicy {

	/**
	 * Returns the first replica in the set.
	 * 
	 * @see uk.ac.stand.cs.brb5.interfaces.IChosenServerPolicy#chooseMessages(java.util.Set, uk.ac.standrews.cs.nds.p2p.interfaces.IKey)
	 */
	public EncryptedMessage[] chooseMessages(Set<IMessageStorageService> messageStorageServices, IKey usernameHash) throws IOException {
		return messageStorageServices.iterator().next().getMessages(usernameHash);
	}

	/**
	 * Returns the first replica in the set.
	 * 
	 * @see uk.ac.stand.cs.brb5.interfaces.IChosenServerPolicy#choosePrivateProfile(java.util.Set, uk.ac.standrews.cs.nds.p2p.interfaces.IKey)
	 */
	public byte[] choosePrivateProfile(Set<IPrivateProfileStorageService> set, IKey usernameHash) throws IOException {
		return set.iterator().next().getPrivateProfile(usernameHash);
	}

	/**
	 * Returns the first replica in the set.
	 * 
	 * @see uk.ac.stand.cs.brb5.interfaces.IChosenServerPolicy#choosePublicProfile(java.util.Set, uk.ac.standrews.cs.nds.p2p.interfaces.IKey)
	 */
	public byte[] choosePublicProfile(Set<IPublicProfileStorageService> set, IKey usernameHash) throws IOException {
		return set.iterator().next().getPublicProfile(usernameHash);
	}

	/**
	 * Returns the first replica in the set.
	 * 
	 * @see uk.ac.stand.cs.brb5.interfaces.IChosenServerPolicy#choosePublicKey(java.util.Set, java.lang.String)
	 */
	public PublicKey choosePublicKey(Set<IPublicKeyDatabase> publicKeyDatabases, String username) throws IOException, GeneralSecurityException {
		return publicKeyDatabases.iterator().next().getPublicKey(username);
	}

	/**
	 * Returns the first replica in the set.
	 * 
	 * @see uk.ac.stand.cs.brb5.interfaces.IChosenServerPolicy#chooseSearchResults(java.util.Set, java.lang.String)
	 */
	public List<String> chooseSearchResults(Set<IPublicKeyDatabase> locatePublicKeyDatabases, String search) throws RemoteException, PeerBookException {
		return locatePublicKeyDatabases.iterator().next().searchUsername(search);
	}

}

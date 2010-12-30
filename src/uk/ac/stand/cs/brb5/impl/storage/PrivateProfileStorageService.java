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
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.PrivateProfileDocument;
import uk.ac.stand.cs.brb5.impl.util.FileIO;
import uk.ac.stand.cs.brb5.impl.util.Synchronizer;
import uk.ac.stand.cs.brb5.interfaces.IPrivateProfileStorageService;
import uk.ac.standrews.cs.nds.p2p.impl.Key;
import uk.ac.standrews.cs.nds.p2p.interfaces.IKey;

/**
 * Implements storage of {@link PrivateProfileDocument}s.
 * 
 * @author Ben
 *
 */
public class PrivateProfileStorageService extends UnicastRemoteObject implements IPrivateProfileStorageService {

	private static final long serialVersionUID = 4878331950220038683L;

	private static final String PRIVATE_PROFILE_DIR = "PrivateProfiles/";

	private Synchronizer<IKey> synchronizer;

	/**
	 * Constructs a new {@link PrivateProfileStorageService}, creating a new Private Profile directory.
	 * 
	 * @throws IOException
	 */
	public PrivateProfileStorageService() throws IOException {
		super();
		this.synchronizer = new Synchronizer<IKey>();

		if (!FileIO.fileExists(PRIVATE_PROFILE_DIR, "")) {
			FileIO.makeDirectory(PRIVATE_PROFILE_DIR);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPrivateProfileStorageService#getPrivateProfile(uk.ac.standrews.cs.nds.p2p.interfaces.IKey)
	 */
	public byte[] getPrivateProfile(IKey usernameHash) throws IOException {
		synchronized (synchronizer.getSynchronizingObject(usernameHash)) {
			return FileIO.readFile(usernameHash.toString(), PRIVATE_PROFILE_DIR);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPrivateProfileStorageService#savePrivateProfile(byte[], uk.ac.standrews.cs.nds.p2p.interfaces.IKey)
	 */
	public void savePrivateProfile(byte[] privateProfileBytes, IKey usernameHash) throws IOException {
		synchronized (synchronizer.getSynchronizingObject(usernameHash)) {
			FileIO.storeFile(privateProfileBytes, usernameHash.toString(), PRIVATE_PROFILE_DIR);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPrivateProfileStorageService#getStoredKeys()
	 */
	public List<IKey> getStoredKeys() throws RemoteException {
		List<IKey> keys = new ArrayList<IKey>();
		for (String filename : FileIO.listFiles(PRIVATE_PROFILE_DIR)) {
			keys.add(new Key(filename));
		}
		return keys;
	}

}

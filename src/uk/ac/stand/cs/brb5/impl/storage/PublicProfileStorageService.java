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

import uk.ac.stand.cs.brb5.impl.util.FileIO;
import uk.ac.stand.cs.brb5.impl.util.Synchronizer;
import uk.ac.stand.cs.brb5.interfaces.IPublicProfileStorageService;
import uk.ac.standrews.cs.nds.p2p.impl.Key;
import uk.ac.standrews.cs.nds.p2p.interfaces.IKey;

/**
 * 
 * Implements Public Profile storage.
 * 
 * @author Ben
 *
 */
public class PublicProfileStorageService extends UnicastRemoteObject implements IPublicProfileStorageService {

	private static final long serialVersionUID = 4895238708709061992L;

	private static final String PUBLIC_PROFILE_DIR = "PublicProfiles/";

	private Synchronizer<IKey> synchronizer;

	/**
	 * Constructs a new {@link PublicProfileStorageService}, creating a new Public Profile directory.
	 * 
	 * @throws IOException
	 */
	public PublicProfileStorageService() throws IOException {
		super();
		this.synchronizer = new Synchronizer<IKey>();

		if (!FileIO.fileExists(PUBLIC_PROFILE_DIR, "")) {
			FileIO.makeDirectory(PUBLIC_PROFILE_DIR);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPublicProfileStorageService#getPublicProfile(uk.ac.standrews.cs.nds.p2p.interfaces.IKey)
	 */
	public byte[] getPublicProfile(IKey usernameHash) throws IOException {
		synchronized (synchronizer.getSynchronizingObject(usernameHash)) {
			return FileIO.readFile(usernameHash.toString(), PUBLIC_PROFILE_DIR);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPublicProfileStorageService#savePublicProfile(byte[], uk.ac.standrews.cs.nds.p2p.interfaces.IKey)
	 */
	public synchronized void savePublicProfile(byte[] publicProfileBytes, IKey usernameHash) throws IOException {
		synchronized (synchronizer.getSynchronizingObject(usernameHash)) {
			FileIO.storeFile(publicProfileBytes, usernameHash.toString(), PUBLIC_PROFILE_DIR);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPublicProfileStorageService#getStoredKeys()
	 */
	public List<IKey> getStoredKeys() throws RemoteException {
		List<IKey> keys = new ArrayList<IKey>();
		for (String filename : FileIO.listFiles(PUBLIC_PROFILE_DIR)) {
			keys.add(new Key(filename));
		}
		return keys;
	}

}

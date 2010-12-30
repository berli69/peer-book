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
import java.util.List;

import uk.ac.standrews.cs.nds.p2p.interfaces.IKey;

/**
 * 
 * Interface for an object intended to store Private Profiles.
 * 
 * @author Ben
 *
 */
public interface IPrivateProfileStorageService extends Remote {

	/**
	 * Used to locate the service via RMI.
	 */
	public static final String BOUND_NAME = "PRIVATE_PROFILE_STORAGE_SERVICE";
	
	/**
	 * @param usernameHash Key corresponding to the username whose Private Profile is being retrieved.
	 * @return An encrypted Private Profile.
	 * @throws IOException
	 */
	public byte[] getPrivateProfile(IKey usernameHash) throws IOException;
	/**
	 * @param privateProfileBytes An encrypted Private Profile.
	 * @param usernameHash Key corresponding to the username whose Private Profile is being saved.
	 * @throws IOException
	 */
	public void savePrivateProfile(byte[] privateProfileBytes, IKey usernameHash) throws IOException;
	/**
	 * @return A list of keys whose Private Profiles are locally stored.
	 * @throws RemoteException
	 */
	public List<IKey> getStoredKeys() throws RemoteException;

}

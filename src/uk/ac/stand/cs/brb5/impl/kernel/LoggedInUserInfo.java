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
package uk.ac.stand.cs.brb5.impl.kernel;

import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.PrivateProfileDocument;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.PublicProfileDocument;

/**
 * 
 * Simple wrapper around the various information kept about a user who is currently logged in.
 * 
 * @author Ben
 *
 */
public class LoggedInUserInfo {

	private String password;
	private PublicProfileDocument publicProfileDoc;
	private PrivateProfileDocument privateProfileDoc;

	/**
	 * @param password Password of the logged in user.
	 */
	public LoggedInUserInfo(String password) {
		this.password = password;
	}

	/**
	 * @return The user's password.
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * @return The {@link PublicProfileDocument} owned by the user.
	 */
	public PublicProfileDocument getPublicProfileDocument() {
		return this.publicProfileDoc;
	}

	/**
	 * @return The {@link PrivateProfileDocument} owned by the user.
	 */
	public PrivateProfileDocument getPrivateProfileDocument() {
		return this.privateProfileDoc;
	}

	/**
	 * @param privateProfileDoc The user's {@link PrivateProfileDocument}.
	 */
	public void setPrivateProfileDocument(PrivateProfileDocument privateProfileDoc) {
		this.privateProfileDoc = privateProfileDoc;
	}

	/**
	 * @param publicProfileDoc The user's {@link PublicProfileDocument}.
	 */
	public void setPublicProfileDocument(PublicProfileDocument publicProfileDoc) {
		this.publicProfileDoc = publicProfileDoc;
	}

}

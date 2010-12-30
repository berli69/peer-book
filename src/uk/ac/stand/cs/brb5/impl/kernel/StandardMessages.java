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

/**
 * 
 * Contains some standard messages that are required for friend requesting and key management.
 * 
 * @author Ben
 *
 */
public class StandardMessages {

	/**
	 * 
	 * Enumerates the different types of messages that may be sent in a PeerBook system.
	 * 
	 * @author Ben
	 *
	 */
	public static enum MessageType {
		FRIEND_REQUEST,
		NORMAL_MESSAGE,
		DELETE_FRIEND,
		NEW_KEY,
		ACCEPT_FRIEND_REQUEST
	}

	public static final String ADD_FRIEND_MESSAGE = "I have added you as my friend.";
	public static final String DELETE_FRIEND_MESSAGE = "I have removed you as a friend.";
	public static final String NEW_KEY_MESSAGE = "I have re-encoded my profile. Here is my new key.";
	public static final String ACCEPTED_FRIEND_REQUEST_MESSAGE = "I have accepted your friend request.";

}

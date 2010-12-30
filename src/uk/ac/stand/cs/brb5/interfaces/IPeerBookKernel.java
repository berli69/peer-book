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

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.FriendRequest;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.PrivateMessage;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.PublicProfileDocument;
import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.impl.kernel.ProfileField;
import uk.ac.stand.cs.brb5.impl.kernel.StandardMessages;

/**
 * Interface for the kernel layer of the PeerBook system, which carries out most core functionality.
 * 
 * @author Ben
 *
 */
public interface IPeerBookKernel {

	/**
	 * The date format to be used to parse/print any date to/from the user.
	 */
	public static final SimpleDateFormat PEERBOOK_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
	/**
	 * The minimum length of a PeerBook password.
	 */
	public static final int MINIMUM_PASSWORD_LENGTH = 4;
	
	/**
	 * @param username Username currently trying to log in.
	 * @param password Password of the user currently trying to log in..
	 * @throws PeerBookException
	 */
	public void login(String username, String password) throws PeerBookException;
	/**
	 * @param username Username that is being tested.
	 * @return True if the user is logged in, false otherwise.
	 */
	public boolean isLoggedIn(String username);
	/**
	 * @param username Username being logged out.
	 */
	public void logout(String username);

	/**
	 * @param viewerUsername The viewer of the Public Profile.
	 * @param vieweeUsername The username of the Public Profile being viewed.
	 * @return The {@link PublicProfileDocument} which is being viewed.
	 * @throws PeerBookException
	 */
	public PublicProfileDocument getProfile(String viewerUsername, String vieweeUsername) throws PeerBookException;
	/**
	 * @param username Username of profile that is being created.
	 * @param password Password of profile that is being created.
	 * @throws PeerBookException
	 */
	public void createProfile(String username, String password) throws PeerBookException;
	/**
	 * @param username Username whose profile is being updated.
	 * @param newValues A mapping from {@link ProfileField}s to new String values.
	 * @throws PeerBookException
	 */
	public void updateProfile(String username, Map<ProfileField, String> newValues) throws PeerBookException;

	/**
	 * @param search A search String.
	 * @return Search results
	 * @throws PeerBookException
	 */
	public List<String> usernameSearch(String search) throws PeerBookException;
	/**
	 * @param username User who is adding the friend.
	 * @param newFriend Username of the friend being added.
	 * @throws PeerBookException
	 */
	public void addFriend(String username, String newFriend) throws PeerBookException;
	/**
	 * @param username First username.
	 * @param friendName Second username.
	 * @return True if the second username is friends with the first.
	 */
	public boolean isFriendsWith(String username, String friendName);
	/**
	 * @param username The username whose friend is to be removed.
	 * @param friendName The friend who is to be removed as the first username's friend.
	 * @throws PeerBookException
	 */
	public void deleteFriend(String username, String friendName) throws PeerBookException;
	/**
	 * @param username User who is accepting the friend request.
	 * @param friendRequestArgument Number tag of the friend request being accepted.
	 * @throws PeerBookException
	 */
	public void acceptFriendRequest(String username, BigInteger friendRequestArgument) throws PeerBookException;
	/**
	 * @param username User who is removing the friend request.
	 * @param bigInteger Number tag of the friend request being removed.
	 * @throws PeerBookException
	 */
	public void deleteFriendRequest(String username, BigInteger bigInteger) throws PeerBookException;
	/**
	 * @param username Username whos friend requests are being retrieved.
	 * @return Friend requests to the specified username.
	 */
	public FriendRequest[] getFriendRequests(String username);

	/**
	 * @param from Message sender's username.
	 * @param to Message recipient's username.
	 * @param message Message plaintext.
	 * @param key Any attached key (for example, when sending a proposed friend a profile access key).
	 * @param messageType The type of the message.
	 * @throws PeerBookException
	 */
	public void sendMessage(String from, String to, String message, byte[] key, StandardMessages.MessageType messageType) throws PeerBookException;
	/**
	 * @param username Username of the owner of the message.
	 * @param bigInteger Number tag of the message being deleted.
	 * @throws PeerBookException
	 */
	public void deleteMessage(String username, BigInteger bigInteger) throws PeerBookException;
	/**
	 * @param username Username whose messages are being retrieved (from the network).
	 * @throws PeerBookException
	 */
	public void retrieveMessages(String username) throws PeerBookException;
	/**
	 * @param username Username whose messages are being retrieved (from local storage).
	 * @return The messages belonging to the given user.
	 */
	public PrivateMessage[] getMessages(String username);

	/**
	 * @param from Sender of the Wall Post.
	 * @param to Recipient of the Wall Post.
	 * @param message Message of the Wall Post.
	 * @throws PeerBookException
	 */
	public void sendWallPost(String from, String to, String message) throws PeerBookException;
	/**
	 * @param username Username whose Wall Post is being deleted.
	 * @param bigInteger Number tag of the Wall Post being deleted.
	 * @throws PeerBookException
	 */
	public void deleteWallPost(String username, BigInteger bigInteger) throws PeerBookException;
	/**
	 * @param username Username whose status is being deleted.
	 * @throws PeerBookException
	 */
	public void deleteStatus(String username) throws PeerBookException;

}

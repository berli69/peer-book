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

import java.math.BigInteger;
import java.security.GeneralSecurityException;

import javax.crypto.SecretKey;

import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.FriendKeyMapping;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.FriendRequest;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.PrivateMessage;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.PrivateProfileDocument;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.Friend;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.PublicProfileDocument;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.WallPost;
import uk.ac.stand.cs.brb5.interfaces.IEncryptionManager;

/**
 * 
 * Utility class used for PeerBookKernel operations.
 * Could probably be merged with PeerBookKernel.
 * 
 * @author Ben
 *
 */
public class KernelUtils {

	/**
	 * @param encryptionManager IEncryptionManager used to construct a SecretKey object.
	 * @param privateProfileDocument PrivateProfileDocument containing given friend's SecretKey.
	 * @param friendName Friend whose SecretKey is to be found.
	 * @return A SecretKey object if one is found for the given friend's name, null otherwise.
	 * @throws GeneralSecurityException
	 */
	public static SecretKey findSecretKey(IEncryptionManager encryptionManager, PrivateProfileDocument privateProfileDocument, String friendName) 
	throws GeneralSecurityException {

		int indexOfKey = findIndexOfFriendPublicProfileKey(privateProfileDocument, friendName);
		if (indexOfKey >= 0) {
			FriendKeyMapping[] friendKeys = privateProfileDocument.getPrivateProfile().getFriendKeyList().getFriendKeyArray();
			byte[] keyBytes = friendKeys[indexOfKey].getSharedProfileKey().getKeyByteArray();
			return encryptionManager.generateSecretKey(keyBytes);
		} else {
			return null;
		}
	}

	/**
	 * @param privateProfileDoc PrivateProfileDocument containing given username's friend request.
	 * @param friendRequestNumber Number of the request we are attempting to locate.
	 * @return The index of the given username's friend request, or -1 if it is not found.
	 */
	public static int findIndexOfFriendRequest(PrivateProfileDocument privateProfileDoc, BigInteger friendRequestNumber) {

		FriendRequest[] friendRequests = privateProfileDoc.getPrivateProfile().getFriendRequestList().getFriendRequestArray();
		for (int i = 0; i < friendRequests.length; i++) {
			if (friendRequests[i].getNumber().equals(friendRequestNumber)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * @param publicProfileDoc PublicProfileDocument containing given friend name.
	 * @param friendName Username whose name we are trying to locate.
	 * @return The index of the given friend's name, or -1 if it is not found.
	 */
	public static int findIndexOfFriend(PublicProfileDocument publicProfileDoc, String friendName) {

		Friend[] friendArray = publicProfileDoc.getPublicProfile().getFriendList().getFriendArray();
		for (int i = 0; i < friendArray.length; i++) {
			if (friendArray[i].getFriendName().equals(friendName)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * @param privateProfileDoc PrivateProfileDocument containing given friend's public profile key mapping.
	 * @param friendName Username whose public profile key mapping we are trying to locate.
	 * @return The index of the given friend's public profile key mapping, or -1 if it is not found.
	 */
	public static int findIndexOfFriendPublicProfileKey(PrivateProfileDocument privateProfileDoc, String friendName) {

		FriendKeyMapping[] friendKeyMappingArray = privateProfileDoc.getPrivateProfile().getFriendKeyList().getFriendKeyArray();
		for (int i = 0; i < friendKeyMappingArray.length; i++) {
			if (friendKeyMappingArray[i].getName().equals(friendName)) {
				return i;
			}
		}

		return -1;
	}
	
	/**
	 * @param publicProfileDoc {@link PublicProfileDocument} from which to find the index of the given Wall Post.
	 * @param wallPostNumber The number of the Wall Post (as stored in publicProfileDoc).
	 * @return The index of the given Wall Post, or -1 if it is not found.
	 */
	public static int findIndexOfWallPost(PublicProfileDocument publicProfileDoc, BigInteger wallPostNumber) {
		
		WallPost[] wallPosts = publicProfileDoc.getPublicProfile().getWallPosts().getWallPostArray();
		for (int i = 0; i < wallPosts.length; i++) {
			if (wallPosts[i].getNumber().equals(wallPostNumber)) {
				return i;
			}
		}
		
		return -1;
	}

	/**
	 * @param privateProfileDoc {@link PrivateProfileDocument} from which to find the given sender of the Friend Request.
	 * @param from The username of the sender of the friend request which we are testing for existence.
	 * @return True if there is a friend request from the given username, false otherwise.
	 */
	public static boolean friendRequestExists(PrivateProfileDocument privateProfileDoc, String from) {
		for (FriendRequest request : privateProfileDoc.getPrivateProfile().getFriendRequestList().getFriendRequestArray()) {
			if (request.getUsername().equals(from)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param privateProfileDoc {@link PrivateProfileDocument} containing the message with the given number.
	 * @param messageNumber The tagged number of the message.
	 * @return The index of the message, or -1 if it is not found.
	 */
	public static int findIndexOfMessage( PrivateProfileDocument privateProfileDoc, BigInteger messageNumber) {
		PrivateMessage[] messages = privateProfileDoc.getPrivateProfile().getPrivateMessageList().getPrivateMessageArray();
		for (int i = 0; i < messages.length; i++) {
			if (messages[i].getNumber().equals(messageNumber)) {
				return i;
			}
		}

		return -1;
	}

}

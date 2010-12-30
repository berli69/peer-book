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
package uk.ac.stand.cs.brb5.test.kernel;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.security.GeneralSecurityException;

import javax.crypto.SecretKey;

import org.junit.Test;

import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.FriendKeyMapping;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.FriendRequest;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.KeyBytes;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.PrivateProfile;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.PrivateProfileDocument;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.Friend;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.PublicProfile;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.PublicProfileDocument;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.WallPost;
import uk.ac.stand.cs.brb5.impl.factories.EncryptionManagerFactory;
import uk.ac.stand.cs.brb5.impl.kernel.KernelUtils;
import uk.ac.stand.cs.brb5.interfaces.IEncryptionManager;

public class KernelUtilsTest {

	private static final PublicProfileDocument PUBLIC_PROFILE_DOC = PublicProfileDocument.Factory.newInstance();
	private static final PrivateProfileDocument PRIVATE_PROFILE_DOC = PrivateProfileDocument.Factory.newInstance();
	private static final IEncryptionManager ENCRYPTION_MANAGER = new EncryptionManagerFactory().getEncryptionManager();

	static {
		PublicProfile publicProfile = PUBLIC_PROFILE_DOC.addNewPublicProfile();
		publicProfile.addNewFriendList();
		publicProfile.addNewWallPosts();

		PrivateProfile privateProfile = PRIVATE_PROFILE_DOC.addNewPrivateProfile();
		privateProfile.addNewFriendKeyList();
		privateProfile.addNewFriendRequestList();
	}

	@Test
	public void testFindExistingSecretKey() throws GeneralSecurityException {
		SecretKey secretKey = ENCRYPTION_MANAGER.generateSecretKey();
		String friendName = "bla";

		FriendKeyMapping friendKey = PRIVATE_PROFILE_DOC.getPrivateProfile().getFriendKeyList().addNewFriendKey();
		KeyBytes keyBytes = friendKey.addNewSharedProfileKey();
		keyBytes.setKeyByteArray(secretKey.getEncoded());
		friendKey.setName(friendName);

		assertEquals(secretKey, KernelUtils.findSecretKey(ENCRYPTION_MANAGER, PRIVATE_PROFILE_DOC, friendName));
	}

	@Test
	public void testFindNonExistingSecretKey() throws GeneralSecurityException {
		assertNull(KernelUtils.findSecretKey(ENCRYPTION_MANAGER, PRIVATE_PROFILE_DOC, ""));
		assertNull(KernelUtils.findSecretKey(ENCRYPTION_MANAGER, PRIVATE_PROFILE_DOC, "NONEXISTENTNAME"));
	}

	@Test
	public void testFindIndexOfExistingFriendRequest() {
		BigInteger friendRequestNum = BigInteger.valueOf(100);

		FriendRequest friendRequest = PRIVATE_PROFILE_DOC.getPrivateProfile().getFriendRequestList().addNewFriendRequest();
		friendRequest.setNumber(friendRequestNum);
		friendRequest.setUsername("b");

		assertTrue(KernelUtils.findIndexOfFriendRequest(PRIVATE_PROFILE_DOC, friendRequestNum) >= 0);
	}

	@Test
	public void testFindNonExistingFriendRequest() throws GeneralSecurityException {
		assertEquals(-1, KernelUtils.findIndexOfFriendRequest(PRIVATE_PROFILE_DOC, BigInteger.ZERO));
		assertEquals(-1, KernelUtils.findIndexOfFriendRequest(PRIVATE_PROFILE_DOC, BigInteger.ONE));
	}

	@Test
	public void testFindIndexOfExistingFriend() {
		String friendName = "friend";

		Friend friend = PUBLIC_PROFILE_DOC.getPublicProfile().getFriendList().addNewFriend();
		friend.setFriendName(friendName);

		assertTrue(KernelUtils.findIndexOfFriend(PUBLIC_PROFILE_DOC, friendName) >= 0);
	}

	@Test
	public void testFindNonExistingFriend() {
		assertEquals(-1, KernelUtils.findIndexOfFriend(PUBLIC_PROFILE_DOC, ""));
		assertEquals(-1, KernelUtils.findIndexOfFriend(PUBLIC_PROFILE_DOC, "NONEXISTENTNAME"));
	}

	@Test
	public void testFindIndexOfExistingFriendPublicProfileKey() {
		String friendName = "friend";

		FriendKeyMapping friendKey = PRIVATE_PROFILE_DOC.getPrivateProfile().getFriendKeyList().addNewFriendKey();
		friendKey.setName(friendName);

		assertTrue(KernelUtils.findIndexOfFriendPublicProfileKey(PRIVATE_PROFILE_DOC, friendName) >= 0);
	}

	@Test
	public void testFindNonExistingFriendPublicProfileKey() {
		assertEquals(-1, KernelUtils.findIndexOfFriendPublicProfileKey(PRIVATE_PROFILE_DOC, ""));
		assertEquals(-1, KernelUtils.findIndexOfFriendPublicProfileKey(PRIVATE_PROFILE_DOC, "NONEXISTENTNAME"));
	}
	
	@Test
	public void testFindIndexOfExistingWallPost() {
		BigInteger num = BigInteger.valueOf(3456);
		
		WallPost wallPost = PUBLIC_PROFILE_DOC.getPublicProfile().getWallPosts().addNewWallPost();
		wallPost.setNumber(num);
		
		assertTrue(KernelUtils.findIndexOfWallPost(PUBLIC_PROFILE_DOC, num) >= 0);
	}
	
	@Test
	public void testFindIndexOfNonExistingWallPost() {
		assertEquals(-1, KernelUtils.findIndexOfWallPost(PUBLIC_PROFILE_DOC, BigInteger.valueOf(643)));
		assertEquals(-1, KernelUtils.findIndexOfWallPost(PUBLIC_PROFILE_DOC, BigInteger.ZERO));
		assertEquals(-1, KernelUtils.findIndexOfWallPost(PUBLIC_PROFILE_DOC, BigInteger.ONE));
	}
	
	@Test
	public void testFriendRequestExists() {
		FriendRequest friendRequest = PRIVATE_PROFILE_DOC.getPrivateProfile().getFriendRequestList().addNewFriendRequest();
		friendRequest.setUsername("jsjiebeubgen");
		assertTrue(KernelUtils.friendRequestExists(PRIVATE_PROFILE_DOC, "jsjiebeubgen"));
		assertFalse(KernelUtils.friendRequestExists(PRIVATE_PROFILE_DOC, ""));
		assertFalse(KernelUtils.friendRequestExists(PRIVATE_PROFILE_DOC, "kjgh"));
	}

}

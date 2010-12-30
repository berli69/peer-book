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
import java.security.KeyPair;
import java.security.PrivateKey;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import uk.ac.stAndrews.cs.hostBrb5.peerBook.message.Message;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.message.MessageDocument;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.FriendKeyMapping;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.FriendRequest;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.KeyBytes;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.PrivateMessage;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.PrivateProfile;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.PrivateProfileDocument;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.Friend;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.PersonalData;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.PublicProfile;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.PublicProfileDocument;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.StatusPost;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.WallPost;
import uk.ac.stand.cs.brb5.impl.exceptions.FileAccessException;
import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.impl.kernel.StandardMessages.MessageType;
import uk.ac.stand.cs.brb5.interfaces.ICompressionManager;
import uk.ac.stand.cs.brb5.interfaces.IEncryptionManager;
import uk.ac.stand.cs.brb5.interfaces.IEncryptionManagerFactory;
import uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager;
import uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel;
import uk.ac.stand.cs.brb5.interfaces.IPeerBookXmlService;
import uk.ac.stand.cs.brb5.interfaces.IStorageService;
import uk.ac.standrews.cs.nds.util.Diagnostic;
import uk.ac.standrews.cs.nds.util.DiagnosticLevel;

/**
 * 
 * Implements the main core functionality of PeerBook, controlling data about currently logged on users,
 * and allowing this data to be changed or updated.
 * 
 * @author Ben
 *
 */
public class PeerBookKernel implements IPeerBookKernel {

	private IPeerBookXmlService peerBookXmlService;
	private IEncryptionManager encryptionManager;
	private IStorageService storageService;
	private Map<String, LoggedInUserInfo> loggedInUsers;

	/**
	 * @param encryptionManagerFac IEncryptionManager to control encryption/decryption/key production.
	 * @param compressionManager ICompressionManager to control compression/decompression of profiles.
	 * @param storageService IStorageService used to store/retrieve profiles and messages.
	 */
	public PeerBookKernel(IEncryptionManagerFactory encryptionManagerFac, ICompressionManager compressionManager, IStorageService storageService) {
		this.peerBookXmlService = new PeerBookXmlService(encryptionManagerFac, compressionManager, storageService);
		this.encryptionManager = encryptionManagerFac.getEncryptionManager();
		this.storageService = storageService;
		this.loggedInUsers = new HashMap<String, LoggedInUserInfo>();
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#acceptFriendRequest(java.lang.String, java.math.BigInteger)
	 */
	public void acceptFriendRequest(String username, BigInteger friendRequestNumber) throws PeerBookException {
		LoggedInUserInfo userInfo = loggedInUsers.get(username);
		PrivateProfileDocument privateProfileDoc = userInfo.getPrivateProfileDocument();
		PublicProfileDocument publicProfileDoc = userInfo.getPublicProfileDocument();

		int friendRequestIndex = KernelUtils.findIndexOfFriendRequest(privateProfileDoc, friendRequestNumber);

		if (friendRequestIndex > -1) {
			FriendRequest friendRequest = privateProfileDoc.getPrivateProfile().getFriendRequestList().getFriendRequestArray(friendRequestIndex);
			String friendName = friendRequest.getUsername();

			if (!isFriendsWith(username, friendName)) {
				// Once the given friend request is found, create a new mapping.
				addFriendToXml(privateProfileDoc, publicProfileDoc, friendName, friendRequest.getSharedProfileKey().getKeyByteArray());

				// Remove used friend request and re-save profiles.
				privateProfileDoc.getPrivateProfile().getFriendRequestList().removeFriendRequest(friendRequestIndex);
				savePrivateProfile(username, privateProfileDoc);
				savePublicProfile(username, publicProfileDoc);

				// Send a friend request acceptance.
				byte[] keyBytes = loggedInUsers.get(username).getPrivateProfileDocument().getPrivateProfile().getSharedProfileKey().getKeyByteArray();
				sendMessage(username, friendName, StandardMessages.ACCEPTED_FRIEND_REQUEST_MESSAGE, keyBytes, StandardMessages.MessageType.ACCEPT_FRIEND_REQUEST);
			}
		}
	}

	private void addFriendToXml(PrivateProfileDocument privateProfileDoc,
			PublicProfileDocument publicProfileDoc, String friendName,
			byte[] keyBytes) {
		FriendKeyMapping newMapping = privateProfileDoc.getPrivateProfile().getFriendKeyList().addNewFriendKey();
		newMapping.addNewSharedProfileKey().setKeyByteArray(keyBytes);
		newMapping.setName(friendName);

		Friend newFriend = publicProfileDoc.getPublicProfile().getFriendList().addNewFriend();
		newFriend.setFriendName(friendName);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#usernameSearch(java.lang.String)
	 */
	public List<String> usernameSearch(String search) throws PeerBookException {
		if (search.length() < IKeySpaceManager.MINIMUM_USERNAME_LENGTH) {
			throw new PeerBookException("Search string is too short.");
		}

		return storageService.searchUsername(search);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#addFriend(java.lang.String, java.lang.String)
	 */
	public void addFriend(String username, String newFriend) throws PeerBookException {
		byte[] keyBytes = loggedInUsers.get(username).getPrivateProfileDocument().getPrivateProfile().getSharedProfileKey().getKeyByteArray();
		sendMessage(username, newFriend, StandardMessages.ADD_FRIEND_MESSAGE, keyBytes, StandardMessages.MessageType.FRIEND_REQUEST);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#createProfile(java.lang.String, java.lang.String)
	 */
	public void createProfile(String username, String password) throws PeerBookException {
		if (username.length() < IKeySpaceManager.MINIMUM_USERNAME_LENGTH) {
			throw new PeerBookException("Username is too short.");
		}

		if (password.length() < IPeerBookKernel.MINIMUM_PASSWORD_LENGTH) {
			throw new PeerBookException("Password is too short.");
		}

		if (storageService.getPublicKey(username) != null) {
			throw new FileAccessException("User already exists.");
		}

		SecretKey publicProfileKey;
		KeyPair keyPair;

		try {
			publicProfileKey = encryptionManager.generateSecretKey();
			keyPair = encryptionManager.generateKeyPair();

			storageService.savePublicKey(keyPair.getPublic(), username);
		} catch (GeneralSecurityException e) {
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
			throw new PeerBookException(e);
		}

		// Create and initialise a new private profile document.
		PrivateProfileDocument newPrivateProfileDoc = PrivateProfileDocument.Factory.newInstance();
		PrivateProfile newPrivateProfile = newPrivateProfileDoc.addNewPrivateProfile();

		KeyBytes keyBytes = KeyBytes.Factory.newInstance();
		keyBytes.setKeyByteArray(publicProfileKey.getEncoded());
		newPrivateProfile.setSharedProfileKey(keyBytes);

		newPrivateProfile.addNewFriendKeyList();

		newPrivateProfile.addNewPersonalKeyPair();
		newPrivateProfile.getPersonalKeyPair().addNewPrivateKey().setKeyByteArray(keyPair.getPrivate().getEncoded());
		newPrivateProfile.getPersonalKeyPair().addNewPublicKey().setKeyByteArray(keyPair.getPublic().getEncoded());

		newPrivateProfile.addNewPrivateMessageList();
		newPrivateProfile.getPrivateMessageList().setPrivateMessageCounter(BigInteger.ZERO);

		newPrivateProfile.addNewFriendRequestList();
		newPrivateProfile.getFriendRequestList().setFriendRequestCounter(BigInteger.ZERO);

		peerBookXmlService.savePrivateProfile(username, password, newPrivateProfileDoc);

		// Create and initialise a new public profile document.
		PublicProfileDocument newPublicProfileDoc = PublicProfileDocument.Factory.newInstance();
		PublicProfile newPublicProfile = newPublicProfileDoc.addNewPublicProfile();
		newPublicProfile.addNewFriendList();
		newPublicProfile.addNewWallPosts();
		newPublicProfile.getWallPosts().setWallPostCounter(BigInteger.ZERO);

		PersonalData newPersonalData = newPublicProfile.addNewPersonalData();
		setUpPersonalData(newPersonalData, username);

		peerBookXmlService.savePublicProfile(newPublicProfileDoc, publicProfileKey);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#deleteFriend(java.lang.String, java.lang.String)
	 */
	public void deleteFriend(String username, String friendName) throws PeerBookException {
		PublicProfileDocument publicProfileDoc = updateLocalPublicProfile(username);
		PrivateProfileDocument privateProfileDoc = updateLocalPrivateProfile(username);

		// Delete friend from public profile document
		publicProfileDoc.getPublicProfile().getFriendList().removeFriend(KernelUtils.findIndexOfFriend(publicProfileDoc, friendName));

		// Delete friend's public profile key from private profile document
		privateProfileDoc.getPrivateProfile().getFriendKeyList().removeFriendKey(KernelUtils.findIndexOfFriendPublicProfileKey(privateProfileDoc, friendName));

		// Tell friend that they are removed.
		sendMessage(username, friendName, StandardMessages.DELETE_FRIEND_MESSAGE, null, StandardMessages.MessageType.DELETE_FRIEND);

		// Re-encrypt public profile
		SecretKey newKey;
		try {
			newKey = encryptionManager.generateSecretKey();
		} catch (GeneralSecurityException e) {
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
			throw new PeerBookException(e);
		}
		privateProfileDoc.getPrivateProfile().getSharedProfileKey().setKeyByteArray(newKey.getEncoded());
		savePublicProfile(username, publicProfileDoc);

		// Tell all other friends about new key.
		for (Friend friend : publicProfileDoc.getPublicProfile().getFriendList().getFriendArray()) {
			sendMessage(username, friend.getFriendName(), StandardMessages.NEW_KEY_MESSAGE, newKey.getEncoded(), StandardMessages.MessageType.NEW_KEY);
		}

		savePrivateProfile(username, privateProfileDoc);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#deleteFriendRequest(java.lang.String, java.math.BigInteger)
	 */
	public void deleteFriendRequest(String username, BigInteger friendRequestNumber) throws PeerBookException {
		PrivateProfileDocument privateProfileDoc = loggedInUsers.get(username).getPrivateProfileDocument();
		int friendRequestIndex = KernelUtils.findIndexOfFriendRequest(privateProfileDoc, friendRequestNumber);

		if (friendRequestIndex > -1) {
			privateProfileDoc.getPrivateProfile().getFriendRequestList().removeFriendRequest(friendRequestIndex);
			savePrivateProfile(username, privateProfileDoc);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#deleteMessage(java.lang.String, java.math.BigInteger)
	 */
	public void deleteMessage(String username, BigInteger messageNumber) throws PeerBookException {
		PrivateProfileDocument privateProfileDoc = loggedInUsers.get(username).getPrivateProfileDocument();
		int messageIndex = KernelUtils.findIndexOfMessage(privateProfileDoc, messageNumber);

		if (messageIndex > -1) {
			privateProfileDoc.getPrivateProfile().getPrivateMessageList().removePrivateMessage(messageIndex);
			savePrivateProfile(username, privateProfileDoc);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#deleteStatus(java.lang.String)
	 */
	public void deleteStatus(String username) throws PeerBookException {
		PublicProfileDocument publicProfileDoc = loggedInUsers.get(username).getPublicProfileDocument();

		if (publicProfileDoc.getPublicProfile().getPersonalData().isSetCurrentStatus()) {
			publicProfileDoc.getPublicProfile().getPersonalData().unsetCurrentStatus();
			savePublicProfile(username, publicProfileDoc);

			updateLocalPublicProfile(username);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#deleteWallPost(java.lang.String, java.math.BigInteger)
	 */
	public void deleteWallPost(String username, BigInteger wallPostNumber) throws PeerBookException {
		PublicProfileDocument publicProfileDoc = loggedInUsers.get(username).getPublicProfileDocument();

		int deletedWallPostIndex = KernelUtils.findIndexOfWallPost(publicProfileDoc, wallPostNumber);
		if (deletedWallPostIndex > -1) {
			publicProfileDoc.getPublicProfile().getWallPosts().removeWallPost(deletedWallPostIndex);
			savePublicProfile(username, publicProfileDoc);

			updateLocalPublicProfile(username);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#getFriendRequests(java.lang.String)
	 */
	public FriendRequest[] getFriendRequests(String username) {
		PrivateProfileDocument privateProfileDoc = loggedInUsers.get(username).getPrivateProfileDocument();
		return privateProfileDoc.getPrivateProfile().getFriendRequestList().getFriendRequestArray();
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#getMessages(java.lang.String)
	 */
	public PrivateMessage[] getMessages(String username) {
		PrivateProfileDocument privateProfileDoc = loggedInUsers.get(username).getPrivateProfileDocument();
		return privateProfileDoc.getPrivateProfile().getPrivateMessageList().getPrivateMessageArray();
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#isFriendsWith(java.lang.String, java.lang.String)
	 */
	public boolean isFriendsWith(String username, String friendName) {
		if (username.equals(friendName)) {
			return true;
		}

		if (KernelUtils.findIndexOfFriend(loggedInUsers.get(username).getPublicProfileDocument(), friendName) > -1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#isLoggedIn(java.lang.String)
	 */
	public boolean isLoggedIn(String username) {
		return loggedInUsers.containsKey(username);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#login(java.lang.String, java.lang.String)
	 */
	public void login(String username, String password) throws PeerBookException {
		if (username.equals("") || password.equals("")) {
			throw new PeerBookException("Username or password is an empty string.");
		}

		PrivateProfileDocument privateProfileDoc = peerBookXmlService.getPrivateProfile(username, password);

		byte[] keyBytes = privateProfileDoc.getPrivateProfile().getSharedProfileKey().getKeyByteArray();
		try {
			SecretKey key = encryptionManager.generateSecretKey(keyBytes);
			PublicProfileDocument publicProfileDoc = peerBookXmlService.getPublicProfile(username, key);

			LoggedInUserInfo userInfo = new LoggedInUserInfo(password);
			userInfo.setPrivateProfileDocument(privateProfileDoc);
			userInfo.setPublicProfileDocument(publicProfileDoc);

			loggedInUsers.put(username, userInfo);
		} catch (GeneralSecurityException e) {
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
			throw new PeerBookException(e);
		}
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#logout(java.lang.String)
	 */
	public void logout(String username) {
		loggedInUsers.remove(username);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#retrieveMessages(java.lang.String)
	 */
	public void retrieveMessages(String username) throws PeerBookException {
		PrivateProfileDocument privateProfileDoc = updateLocalPrivateProfile(username);

		byte[] privateKeyBytes = privateProfileDoc.getPrivateProfile().getPersonalKeyPair().getPrivateKey().getKeyByteArray();
		PrivateKey privateKey;
		try {
			privateKey = encryptionManager.generatePrivateKey(privateKeyBytes);
		} catch (GeneralSecurityException e) {
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
			throw new PeerBookException(e);
		}

		for (MessageDocument message : peerBookXmlService.getMessages(username, privateKey)) {

			StandardMessages.MessageType messageType = StandardMessages.MessageType.valueOf(message.getMessage().getMessageType());

			if (messageType.equals(StandardMessages.MessageType.NORMAL_MESSAGE)) {

				BigInteger messageCounter = privateProfileDoc.getPrivateProfile().getPrivateMessageList().getPrivateMessageCounter();

				PrivateMessage privateMessage = privateProfileDoc.getPrivateProfile().getPrivateMessageList().addNewPrivateMessage();
				privateMessage.setNumber(messageCounter.add(BigInteger.ONE));
				privateMessage.setFromName(message.getMessage().getFromUserName());
				privateMessage.setSentDate(message.getMessage().getDateSent());
				privateMessage.setMessage(message.getMessage().getMessageBody());

				privateProfileDoc.getPrivateProfile().getPrivateMessageList().setPrivateMessageCounter(messageCounter.add(BigInteger.ONE));

			} else if (messageType.equals(StandardMessages.MessageType.DELETE_FRIEND)) {

				if (isFriendsWith(username, message.getMessage().getFromUserName())) {
					deleteFriend(username, message.getMessage().getFromUserName());
				}

			} else if (messageType.equals(StandardMessages.MessageType.FRIEND_REQUEST)) {

				String from = message.getMessage().getFromUserName();

				if (!isFriendsWith(username, from) && !KernelUtils.friendRequestExists(privateProfileDoc, from)) {

					FriendRequest friendRequest = privateProfileDoc.getPrivateProfile().getFriendRequestList().addNewFriendRequest();
					friendRequest.setNumber(privateProfileDoc.getPrivateProfile().getFriendRequestList().getFriendRequestCounter().add(BigInteger.ONE));
					friendRequest.setUsername(message.getMessage().getFromUserName());
					friendRequest.addNewSharedProfileKey();
					friendRequest.getSharedProfileKey().setKeyByteArray(message.getMessage().getKey().getKeyByteArray());

				}

			} else if (messageType.equals(StandardMessages.MessageType.NEW_KEY)) {

				for (FriendKeyMapping mapping : privateProfileDoc.getPrivateProfile().getFriendKeyList().getFriendKeyArray()) {
					if (mapping.getName().equals(message.getMessage().getFromUserName())) {
						mapping.getSharedProfileKey().setKeyByteArray(message.getMessage().getKey().getKeyByteArray());
					}
				}

			} else if (messageType.equals(StandardMessages.MessageType.ACCEPT_FRIEND_REQUEST)) {
				PublicProfileDocument publicProfileDoc = updateLocalPublicProfile(username);
				addFriendToXml(privateProfileDoc, publicProfileDoc, message.getMessage().getFromUserName(), message.getMessage().getKey().getKeyByteArray());
				savePublicProfile(username, publicProfileDoc);
			}
		}

		savePrivateProfile(username, privateProfileDoc);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#sendMessage(java.lang.String, java.lang.String, java.lang.String, byte[], uk.ac.stand.cs.brb5.impl.kernel.StandardMessages.MessageType)
	 */
	public void sendMessage(String from, String to, String messageText, byte[] key, MessageType messageType) throws PeerBookException {

		if (from.length() < IKeySpaceManager.MINIMUM_USERNAME_LENGTH || to.length() < IKeySpaceManager.MINIMUM_USERNAME_LENGTH) {
			throw new PeerBookException("That username is too short.");
		}

		MessageDocument messageDoc = MessageDocument.Factory.newInstance();
		Message message = messageDoc.addNewMessage();
		message.setFromUserName(from);
		message.setDateSent(Calendar.getInstance());
		message.setMessageType(messageType.toString());
		message.setToUserName(to);
		message.setMessageBody(messageText);

		if (key != null) {
			message.addNewKey();
			message.getKey().setKeyByteArray(key);
		}

		saveMessage(from, to, messageDoc);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#sendWallPost(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void sendWallPost(String from, String to, String message) throws PeerBookException {
		PublicProfileDocument profile;
		SecretKey key;

		try {
			if (to.equals(from)) {
				byte[] keyBytes = loggedInUsers.get(from).getPrivateProfileDocument().getPrivateProfile().getSharedProfileKey().getKeyByteArray();
				key = encryptionManager.generateSecretKey(keyBytes);
				profile = updateLocalPublicProfile(from);
			} else {
				key = KernelUtils.findSecretKey(encryptionManager, loggedInUsers.get(from).getPrivateProfileDocument(), to);
				profile = peerBookXmlService.getPublicProfile(to, key);
			}
		} catch (GeneralSecurityException e) {
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
			throw new PeerBookException(e);
		}

		Calendar now = Calendar.getInstance();

		BigInteger wallPostCounter = profile.getPublicProfile().getWallPosts().getWallPostCounter();

		WallPost wallPost = profile.getPublicProfile().getWallPosts().addNewWallPost();
		wallPost.setNumber(wallPostCounter.add(BigInteger.ONE));
		wallPost.setPostDate(now);
		wallPost.setUsername(from);
		wallPost.setPost(message);

		if (to.equals(from)) {
			StatusPost statusUpdate = profile.getPublicProfile().getPersonalData().getCurrentStatus();
			if (statusUpdate == null) {
				statusUpdate = profile.getPublicProfile().getPersonalData().addNewCurrentStatus();
			}
			statusUpdate.setPostDate(now);
			statusUpdate.setStatus(message);

			loggedInUsers.get(from).setPublicProfileDocument(profile);
		}

		profile.getPublicProfile().getWallPosts().setWallPostCounter(wallPostCounter.add(BigInteger.ONE));

		peerBookXmlService.savePublicProfile(profile, key);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#updateProfile(java.lang.String, java.util.Map)
	 */
	public void updateProfile(String username, Map<ProfileField, String> newValues) throws PeerBookException {
		// TODO give option to change name
		// requires look up for non empty name, and to check the public key database for name already existing
		// and need to tell all friends of new name (otherwise linkages no longer work)

		PublicProfileDocument publicProfileDoc = updateLocalPublicProfile(username);

		PersonalData personalData = publicProfileDoc.getPublicProfile().getPersonalData();

		// Date of birth is done first -- if something bad happens while parsing date string, nothing is updated
		Calendar newDoB = Calendar.getInstance();
		try {
			if (newValues.get(ProfileField.DATE_OF_BIRTH).equals("")) {
				newDoB = null;
			} else {
				newDoB.setTime(IPeerBookKernel.PEERBOOK_DATE_FORMAT.parse(newValues.get(ProfileField.DATE_OF_BIRTH)));
			}
		} catch (ParseException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, "Date of birth string is invalid");
			throw new PeerBookException(e);
		}
		personalData.setDateOfBirth(newDoB);

		personalData.setEmailAddressArray(0, newValues.get(ProfileField.EMAIL_ADDRESS_0));
		personalData.setEmailAddressArray(1, newValues.get(ProfileField.EMAIL_ADDRESS_1));
		personalData.setEmailAddressArray(2, newValues.get(ProfileField.EMAIL_ADDRESS_2));
		personalData.setEmailAddressArray(3, newValues.get(ProfileField.EMAIL_ADDRESS_3));
		personalData.setEmailAddressArray(4, newValues.get(ProfileField.EMAIL_ADDRESS_4));
		personalData.setPhone(newValues.get(ProfileField.PHONE));
		personalData.setAddress(newValues.get(ProfileField.ADDRESS));
		personalData.setCourse(newValues.get(ProfileField.COURSE));
		personalData.setHomeTown(newValues.get(ProfileField.HOME_TOWN));
		personalData.setInterests(newValues.get(ProfileField.INTERESTS));

		savePublicProfile(username, publicProfileDoc);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel#getProfile(java.lang.String, java.lang.String)
	 */
	public PublicProfileDocument getProfile(String viewer, String username) throws PeerBookException {
		if (username.equals(viewer)) {
			return updateLocalPublicProfile(username);
		} else {
			PrivateProfileDocument privateProfileDoc = loggedInUsers.get(viewer).getPrivateProfileDocument();
			SecretKey key;
			try {
				key = KernelUtils.findSecretKey(encryptionManager, privateProfileDoc, username);
			} catch (GeneralSecurityException e) {
				Diagnostic.trace(DiagnosticLevel.RUNALL, e);
				throw new PeerBookException(e);
			}
			return peerBookXmlService.getPublicProfile(username, key);
		}
	}

	private void saveMessage(String from, String to, MessageDocument messageDoc) throws PeerBookException {
		try {
			PrivateProfileDocument privateProfileDoc = loggedInUsers.get(from).getPrivateProfileDocument();
			byte[] privateKeyBytes = privateProfileDoc.getPrivateProfile().getPersonalKeyPair().getPrivateKey().getKeyByteArray();
			PrivateKey privateKey = encryptionManager.generatePrivateKey(privateKeyBytes);
			peerBookXmlService.saveMessage(messageDoc, privateKey, to);
		} catch (GeneralSecurityException e) {
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
			throw new PeerBookException(e);
		}
	}

	private PrivateProfileDocument updateLocalPrivateProfile(String username) throws PeerBookException {
		// Refreshes local copy of the user's PrivateProfileDocument.
		LoggedInUserInfo userInfo = loggedInUsers.get(username);
		PrivateProfileDocument privateProfileDoc = peerBookXmlService.getPrivateProfile(username, userInfo.getPassword());
		userInfo.setPrivateProfileDocument(privateProfileDoc);
		return privateProfileDoc;
	}

	private PublicProfileDocument updateLocalPublicProfile(String username) throws PeerBookException {
		// Refreshes local copy of the user's PublicProfileDocument.
		LoggedInUserInfo userInfo = loggedInUsers.get(username);

		byte[] keyBytes = userInfo.getPrivateProfileDocument().getPrivateProfile().getSharedProfileKey().getKeyByteArray();
		try {
			SecretKey key = encryptionManager.generateSecretKey(keyBytes);
			PublicProfileDocument publicProfileDoc = peerBookXmlService.getPublicProfile(username, key);

			userInfo.setPublicProfileDocument(publicProfileDoc);
			return publicProfileDoc;
		} catch (GeneralSecurityException e) {
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
			throw new PeerBookException(e);
		}
	}

	private void savePublicProfile(String username, PublicProfileDocument publicProfileDoc) throws PeerBookException {
		try {
			byte[] keyBytes = loggedInUsers.get(username).getPrivateProfileDocument().getPrivateProfile().getSharedProfileKey().getKeyByteArray();
			SecretKey secretKey = encryptionManager.generateSecretKey(keyBytes);
			peerBookXmlService.savePublicProfile(publicProfileDoc, secretKey);
		} catch (GeneralSecurityException e) {
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
			throw new PeerBookException(e);
		}
	}

	private void savePrivateProfile(String username, PrivateProfileDocument privateProfileDoc) throws PeerBookException {
		String password = loggedInUsers.get(username).getPassword();
		peerBookXmlService.savePrivateProfile(username, password, privateProfileDoc);
	}

	private void setUpPersonalData(PersonalData newPersonalData, String username) {
		newPersonalData.setName(username);
		newPersonalData.setDateOfBirth(null);
		for (int i = 0; i < 5; i++) {
			newPersonalData.insertNewEmailAddress(i);
			newPersonalData.setEmailAddressArray(i, "");
		}
		newPersonalData.setPhone("");
		newPersonalData.setAddress("");
		newPersonalData.setCourse("");
		newPersonalData.setHomeTown("");
		newPersonalData.setInterests("");
	}

}

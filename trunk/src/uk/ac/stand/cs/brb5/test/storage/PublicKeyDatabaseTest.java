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
package uk.ac.stand.cs.brb5.test.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;

import uk.ac.stand.cs.brb5.impl.exceptions.FileAccessException;
import uk.ac.stand.cs.brb5.impl.factories.EncryptionManagerFactory;
import uk.ac.stand.cs.brb5.impl.storage.PublicKeyDatabase;
import uk.ac.stand.cs.brb5.interfaces.IEncryptionManager;
import uk.ac.stand.cs.brb5.interfaces.IEncryptionManagerFactory;
import uk.ac.stand.cs.brb5.interfaces.IPublicKeyDatabase;
import uk.ac.stand.cs.brb5.test.TestUtils;

public class PublicKeyDatabaseTest {
	
	private static final IEncryptionManagerFactory ENCRYPTION_MANAGER_FAC = new EncryptionManagerFactory();
	
	private IPublicKeyDatabase publicKeyDatabase;
	
	public PublicKeyDatabaseTest() throws FileAccessException, IOException {
		publicKeyDatabase = new PublicKeyDatabase(ENCRYPTION_MANAGER_FAC);
	}
	
	@Test
	public void testSaveAndGetPublicKey() throws GeneralSecurityException, IOException {
		String username = "balhfeheg";
		
		assertNull(publicKeyDatabase.getPublicKey(username));
		
		KeyPair keyPair = ENCRYPTION_MANAGER_FAC.getEncryptionManager().generateKeyPair();
		publicKeyDatabase.savePublicKey(username, keyPair.getPublic());
		
		assertEquals(keyPair.getPublic(), publicKeyDatabase.getPublicKey(username));
	}
	
	@Test
	public void testChangePublicKey() throws GeneralSecurityException, IOException {
		String username = "bhengjbnsege";
		IEncryptionManager encryptionManager = ENCRYPTION_MANAGER_FAC.getEncryptionManager();
		KeyPair keyPair = encryptionManager.generateKeyPair();
		publicKeyDatabase.savePublicKey(username, keyPair.getPublic());
		
		KeyPair newKeyPair1 = encryptionManager.generateKeyPair();
		byte[] badSignature = {4, 5, 6, 7};
		publicKeyDatabase.changePublicKey(username, badSignature, newKeyPair1.getPublic());
		assertEquals(keyPair.getPublic(), publicKeyDatabase.getPublicKey(username));
		
		KeyPair newKeyPair2 = encryptionManager.generateKeyPair();
		byte[] goodSignature = encryptionManager.generateSignature(username.getBytes(), keyPair.getPrivate());
		publicKeyDatabase.changePublicKey(username, goodSignature, newKeyPair2.getPublic());
		assertEquals(newKeyPair2.getPublic(), publicKeyDatabase.getPublicKey(username));
	}
	
	@Test
	public void testChangeUsername() throws IOException, GeneralSecurityException {
		String username = "ghwuiegabe";
		IEncryptionManager encryptionManager = ENCRYPTION_MANAGER_FAC.getEncryptionManager();
		KeyPair keyPair = encryptionManager.generateKeyPair();
		publicKeyDatabase.savePublicKey(username, keyPair.getPublic());
		
		String newName  = "geasgabgegkeoj";
		byte[] badSignature = {6, 4, 56, 77};
		publicKeyDatabase.changeUsername(username, newName, badSignature);
		assertNull(publicKeyDatabase.getPublicKey(newName));
		
		byte[] goodSignature = encryptionManager.generateSignature(newName.getBytes(), keyPair.getPrivate());
		publicKeyDatabase.changeUsername(username, newName, goodSignature);
		assertEquals(keyPair.getPublic(), publicKeyDatabase.getPublicKey(newName));
		assertNull(publicKeyDatabase.getPublicKey(username));
	}
	
	@Test
	public void testGetStoredUsernames() throws IOException, GeneralSecurityException {
		String username1 = "bla";
		String username2 = "bla2";
		
		KeyPair keyPair1 = ENCRYPTION_MANAGER_FAC.getEncryptionManager().generateKeyPair();
		KeyPair keyPair2 = ENCRYPTION_MANAGER_FAC.getEncryptionManager().generateKeyPair();
		
		List<String> usernames = publicKeyDatabase.getStoredUsernames();
		assertFalse(usernames.contains(username1));
		assertFalse(usernames.contains(username2));
		
		publicKeyDatabase.savePublicKey(username1, keyPair1.getPublic());
		
		usernames = publicKeyDatabase.getStoredUsernames();
		assertTrue(usernames.contains(username1));
		assertFalse(usernames.contains(username2));
		
		publicKeyDatabase.savePublicKey(username2, keyPair2.getPublic());
		
		usernames = publicKeyDatabase.getStoredUsernames();
		assertTrue(usernames.contains(username1));
		assertTrue(usernames.contains(username2));
	}
	
	@AfterClass
	public static void cleanUp() {
		TestUtils.deleteDirectory("PublicKeyDatabase");
	}

}

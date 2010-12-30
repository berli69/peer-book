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
package uk.ac.stand.cs.brb5.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.junit.Test;

import uk.ac.stand.cs.brb5.impl.util.EncryptionManager;
import uk.ac.stand.cs.brb5.interfaces.IEncryptionManager;
import uk.ac.stand.cs.brb5.interfaces.IEncryptionManager.PublicKeyEncryptedBytes;

public class EncryptionManagerTest {

	private static final String PASSWORD = "password";
	private static final String PASSWORD2 = "password2";
	private static final String PLAIN_TEXT = "Test message, plain text, has punctuation.\nAnd a new line.";

	private IEncryptionManager encryptionManager;
	private SecretKey secretKey;
	private PublicKey publicKey;
	private PrivateKey privateKey;

	public EncryptionManagerTest() throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
		encryptionManager = new EncryptionManager();

		byte[] keyArray = {(byte)0xAB, (byte)0xBC, (byte)0xCD, (byte)0xDE,
				(byte)0xEF, (byte)0xF0, (byte)0x01, (byte)0x12,
				(byte)0x23, (byte)0x34, (byte)0x45, (byte)0x56,
				(byte)0x67, (byte)0x78, (byte)0x89, (byte)0x9A,
				(byte)0xAB, (byte)0xBC, (byte)0xCD, (byte)0xDE,
				(byte)0xEF, (byte)0xF0, (byte)0x01, (byte)0x12};

		KeySpec dseKeySpec = new DESedeKeySpec(keyArray);
		secretKey = SecretKeyFactory.getInstance("DESede").generateSecret(dseKeySpec);

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(1024);
		KeyPair keyPair = kpg.genKeyPair();
		publicKey = keyPair.getPublic();
		privateKey = keyPair.getPrivate();
	}

	@Test
	public void testEncryptDecryptWithSecretKey() throws GeneralSecurityException {
		byte[] encrypted = encryptionManager.encrypt(PLAIN_TEXT.getBytes(), secretKey);
		byte[] decrypted = encryptionManager.decrypt(encrypted, secretKey);

		assertEquals(PLAIN_TEXT, new String(decrypted));
	}

	@Test
	public void testEncryptDecryptWithKeyPair() throws GeneralSecurityException {
		PublicKeyEncryptedBytes encrypted = encryptionManager.encrypt(PLAIN_TEXT.getBytes(), publicKey);
		byte[] decrypted = encryptionManager.decrypt(encrypted, privateKey);

		assertEquals(PLAIN_TEXT, new String(decrypted));
	}

	@Test
	public void testSignatureGenerationVerification() throws GeneralSecurityException {
		byte[] signature = encryptionManager.generateSignature(PLAIN_TEXT.getBytes(), privateKey);
		assertTrue(encryptionManager.verifySignature(signature, PLAIN_TEXT.getBytes(), publicKey));
	}

	@Test
	public void testGenerateKeyPair() throws GeneralSecurityException {
		KeyPair keyPair = encryptionManager.generateKeyPair();
		KeyPair keyPair2 = encryptionManager.generateKeyPair();
		
		String privateKey = new String(keyPair.getPrivate().getEncoded());
		String privateKey2 = new String(keyPair2.getPrivate().getEncoded());
		
		String publicKey = new String(keyPair.getPublic().getEncoded());
		String publicKey2 = new String(keyPair2.getPublic().getEncoded());

		assertFalse(privateKey.equals(privateKey2));
		assertFalse(publicKey.equals(publicKey2));
	}
	
	@Test
	public void testGenerateSecretKey() throws GeneralSecurityException {
		SecretKey secretKey = encryptionManager.generateSecretKey();
		SecretKey secretKey2 = encryptionManager.generateSecretKey();
		
		String secretKeyString = new String(secretKey.getEncoded());
		String secretKeyString2 = new String(secretKey2.getEncoded());
		
		assertFalse(secretKeyString.equals(secretKeyString2));
	}
	
	@Test
	public void testGenerateSecretKeyFromBytes() throws GeneralSecurityException {
		assertEquals(secretKey, encryptionManager.generateSecretKey(secretKey.getEncoded()));
	}
	
	@Test
	public void testGeneratePasswordBasedKey() throws GeneralSecurityException {
		SecretKey secretKey = encryptionManager.generatePasswordBasedKey(PASSWORD);
		SecretKey secretKey2 = encryptionManager.generatePasswordBasedKey(PASSWORD);
		SecretKey secretKey3 = encryptionManager.generatePasswordBasedKey(PASSWORD2);
		
		String secretKeyString = new String(secretKey.getEncoded());
		String secretKeyString2 = new String(secretKey2.getEncoded());
		String secretKeyString3 = new String(secretKey3.getEncoded());
		
		assertEquals(secretKeyString, secretKeyString2);
		assertFalse(secretKeyString2.equals(secretKeyString3));
	}
	
	@Test
	public void testGeneratePublicKey() throws GeneralSecurityException {
		assertEquals(publicKey, encryptionManager.generatePublicKey(publicKey.getEncoded()));
	}
	
	@Test
	public void testGeneratePrivateKey() throws GeneralSecurityException {
		assertEquals(privateKey, encryptionManager.generatePrivateKey(privateKey.getEncoded()));
	}

}

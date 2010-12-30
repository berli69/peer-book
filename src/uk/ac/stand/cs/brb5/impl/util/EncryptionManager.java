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
package uk.ac.stand.cs.brb5.impl.util;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import uk.ac.stand.cs.brb5.interfaces.IEncryptionManager;

/**
 * 
 * Default Encryption Manager. Uses:
 * <ul>
 * <li>DES-EDE (key length of 24 bytes) for symmetric-key cryptography.</li>
 * <li>RSA (key length of 1024 bits or 128 bytes) for public-key cryptography.</li>
 * <li>RSA with the MD5 hashing algorithm for digital signatures.</li>
 * <li>DES-EDE with the SHA-1 hashing algorithm for password-based encryption.</li>
 * </ul>
 * 
 * @author Ben
 *
 */
public class EncryptionManager implements IEncryptionManager {

	private static final int DESEDE_KEY_LENGTH_BYTES = 24;
	private static final int KEYPAIR_LENGTH = 1024;
	private static final String SYMMETRIC_KEY_ALGORITHM = "DESede";
	private static final String PUBLIC_KEY_ALGORITHM = "RSA";
	private static final String MESSAGE_SIGNING_ALGORITHM = "MD5withRSA";
	private static final String PBE_ALGORITHM = "PBEWithSHA1andDESede";

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IEncryptionManager#decrypt(byte[], javax.crypto.SecretKey)
	 */
	public byte[] decrypt(byte[] cipherText, SecretKey key) throws GeneralSecurityException {
		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), SYMMETRIC_KEY_ALGORITHM);		
		return getCipher(SYMMETRIC_KEY_ALGORITHM, Cipher.DECRYPT_MODE, secretKeySpec).doFinal(cipherText);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IEncryptionManager#decrypt(uk.ac.stand.cs.brb5.interfaces.IEncryptionManager.PublicKeyEncryptedBytes, java.security.PrivateKey)
	 */
	public byte[] decrypt(PublicKeyEncryptedBytes cipherText, PrivateKey key) throws GeneralSecurityException {
		byte[] secretKeyBytes = getCipher(PUBLIC_KEY_ALGORITHM, Cipher.DECRYPT_MODE, key).doFinal(cipherText.getEncryptedSecretKey());
		SecretKey secretKey = generateSecretKey(secretKeyBytes);
		return decrypt(cipherText.getCipherText(), secretKey);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IEncryptionManager#encrypt(byte[], javax.crypto.SecretKey)
	 */
	public byte[] encrypt(byte[] plainText, SecretKey key) throws GeneralSecurityException {
		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), SYMMETRIC_KEY_ALGORITHM);
		return getCipher(SYMMETRIC_KEY_ALGORITHM, Cipher.ENCRYPT_MODE, secretKeySpec).doFinal(plainText);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IEncryptionManager#encrypt(byte[], java.security.PublicKey)
	 */
	public PublicKeyEncryptedBytes encrypt(byte[] plainText, PublicKey key) throws GeneralSecurityException {
		SecretKey secretKey = generateSecretKey();
		byte[] encryptedSecretKey = getCipher(PUBLIC_KEY_ALGORITHM, Cipher.ENCRYPT_MODE, key).doFinal(secretKey.getEncoded());
		byte[] cipherText = encrypt(plainText, secretKey);
		return new PublicKeyEncryptedBytes(encryptedSecretKey, cipherText);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IEncryptionManager#generateSignature(byte[], java.security.PrivateKey)
	 */
	public byte[] generateSignature(byte[] plainText, PrivateKey key) throws GeneralSecurityException {
		Signature signatureEngine = Signature.getInstance(MESSAGE_SIGNING_ALGORITHM);		
		signatureEngine.initSign(key);
		signatureEngine.update(plainText);
		return signatureEngine.sign();
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IEncryptionManager#verifySignature(byte[], byte[], java.security.PublicKey)
	 */
	public boolean verifySignature(byte[] signature, byte[] plainText, PublicKey key) throws GeneralSecurityException {
		Signature signatureEngine = Signature.getInstance(MESSAGE_SIGNING_ALGORITHM);		
		signatureEngine.initVerify(key);
		signatureEngine.update(plainText);
		return signatureEngine.verify(signature);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IEncryptionManager#generateKeyPair()
	 */
	public KeyPair generateKeyPair() throws GeneralSecurityException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance(PUBLIC_KEY_ALGORITHM);
		kpg.initialize(KEYPAIR_LENGTH);
		return kpg.genKeyPair();
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IEncryptionManager#generatePasswordBasedKey(java.lang.String)
	 */
	public SecretKey generatePasswordBasedKey(String password) throws GeneralSecurityException {
		char[] finalPasswordBytes = new char[DESEDE_KEY_LENGTH_BYTES];
		char[] passwordBytes = password.toCharArray();
		
		for (int i = 0; i < finalPasswordBytes.length; i++) {
			finalPasswordBytes[i] = passwordBytes[i % passwordBytes.length];
		}
		
		PBEKeySpec pbeKeySpec = new PBEKeySpec(finalPasswordBytes);
		SecretKeyFactory factory = SecretKeyFactory.getInstance(PBE_ALGORITHM);
		return factory.generateSecret(pbeKeySpec);
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IEncryptionManager#generateSecretKey()
	 */
	public SecretKey generateSecretKey() throws GeneralSecurityException {
		KeyGenerator keyGen = KeyGenerator.getInstance(SYMMETRIC_KEY_ALGORITHM);
		return keyGen.generateKey();
	}
	
	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IEncryptionManager#generateSecretKey(byte[])
	 */
	public SecretKey generateSecretKey(byte[] keyBytes) throws GeneralSecurityException {
		DESedeKeySpec keySpec = new DESedeKeySpec(keyBytes);
		return SecretKeyFactory.getInstance(SYMMETRIC_KEY_ALGORITHM).generateSecret(keySpec);
	}
	
	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IEncryptionManager#generatePublicKey(byte[])
	 */
	public PublicKey generatePublicKey(byte[] keyBytes) throws GeneralSecurityException {
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance(PUBLIC_KEY_ALGORITHM);
	    return kf.generatePublic(keySpec);
	}
	
	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IEncryptionManager#generatePrivateKey(byte[])
	 */
	public PrivateKey generatePrivateKey(byte[] keyBytes) throws GeneralSecurityException {
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance(PUBLIC_KEY_ALGORITHM);
	    return kf.generatePrivate(keySpec);
	}

	private Cipher getCipher(String algorithm, int mode, Key key) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(mode, key);

		return cipher;
	}

}

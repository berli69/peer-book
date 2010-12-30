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

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

/**
 * 
 * Abstraction around an encryption scheme (allows the developer to change what type of cipher(s)
 * are being used).
 * 
 * @author Ben
 *
 */
public interface IEncryptionManager {
	
	/**
	 * Encrypts using symmetric-key cryptography.
	 * 
	 * @param plainText The plaintext which is to be encrypted.
	 * @param key The symmetric key used to encrypt the plaintext.
	 * @return The encrypted plaintext.
	 * @throws GeneralSecurityException
	 */
	public byte[] encrypt(byte[] plainText, SecretKey key) throws GeneralSecurityException;
	/**
	 * Decrypts using symmetric-key cryptography.
	 * 
	 * @param cipherText The ciphertext to be decrypted.
	 * @param key The symmetric key used to decrypt the ciphertext.
	 * @return The decrypted ciphertext.
	 * @throws GeneralSecurityException
	 */
	public byte[] decrypt(byte[] cipherText, SecretKey key) throws GeneralSecurityException;
	
	/**
	 * Encrypts using public-key cryptography. First constructs a symmetric key which is used to encrypt
	 * the plaintext and then encrypts this symmetric key with the given public key.
	 * 
	 * @param plainText The plaintext which is to be encrypted.
	 * @param key The public key used to encrypt the plaintext.
	 * @return The encrypted plaintext.
	 * @throws GeneralSecurityException
	 */
	public PublicKeyEncryptedBytes encrypt(byte[] plainText, PublicKey key) throws GeneralSecurityException;
	/**
	 * Decrypts using public-key cryptography. First decrypts the provided symmetric key, then uses
	 * this symmetric key to decrypt the actual plaintext.
	 * 
	 * @param cipherText The ciphertext to be decrypted.
	 * @param key The private key to use for decryption.
	 * @return The decrypted ciphertext.
	 * @throws GeneralSecurityException
	 */
	public byte[] decrypt(PublicKeyEncryptedBytes cipherText, PrivateKey key) throws GeneralSecurityException;
	
	/**
	 * Generates a digital signature.
	 * 
	 * @param plainText The plaintext for which to construct the digital signature.
	 * @param key The private key of the signer.
	 * @return The digital signature.
	 * @throws GeneralSecurityException
	 */
	public byte[] generateSignature(byte[] plainText, PrivateKey key) throws GeneralSecurityException;
	/**
	 * Verifies a digital signature against the plaintext for integrity.
	 * 
	 * @param signature The digital signature.
	 * @param plainText The plaintext corresponding to the signature.
	 * @param key The public key of the signer.
	 * @return True if the signature matches the given public key and plaintext, false otherwise.
	 * @throws GeneralSecurityException
	 */
	public boolean verifySignature(byte[] signature, byte[] plainText, PublicKey key) throws GeneralSecurityException;
	
	/**
	 * @return A new public-key cryptography keypair.
	 * @throws GeneralSecurityException
	 */
	public KeyPair generateKeyPair() throws GeneralSecurityException;
	/**
	 * @return A new symmetric-key cryptography key.
	 * @throws GeneralSecurityException
	 */
	public SecretKey generateSecretKey() throws GeneralSecurityException;
	/**
	 * @param keyBytes Byte array encoding a symmetric key.
	 * @return A symmetric key object corresponding to the given byte array.
	 * @throws GeneralSecurityException
	 */
	public SecretKey generateSecretKey(byte[] keyBytes) throws GeneralSecurityException;
	/**
	 * @param keyBytes Byte array encoding a public key.
	 * @return A public key object corresponding to the given byte array.
	 * @throws GeneralSecurityException
	 */
	public PublicKey generatePublicKey(byte[] keyBytes) throws GeneralSecurityException;
	/**
	 * @param keyBytes Byte array encoding a private key.
	 * @return A private key object corresponding to the given byte array.
	 * @throws GeneralSecurityException
	 */
	public PrivateKey generatePrivateKey(byte[] keyBytes) throws GeneralSecurityException;
	/**
	 * @param password A password String.
	 * @return A symmetric key generated using the given password.
	 * @throws GeneralSecurityException
	 */
	public SecretKey generatePasswordBasedKey(String password) throws GeneralSecurityException;
	
	/**
	 * Simple wrapper around the objects used for public-key cryptography, including
	 * the plaintext encrypted with a symmetric key and this symmetric key encrypted
	 * with a public key.
	 * 
	 * @author Ben
	 *
	 */
	public class PublicKeyEncryptedBytes {
		
		private byte[] encryptedSecretKey;
		private byte[] cipherText;
		
		/**
		 * @param encryptedSecretKey A symmetric key encrypted with a public key.
		 * @param cipherText Ciphertext encrypted with the symmetric key.
		 */
		public PublicKeyEncryptedBytes(byte[] encryptedSecretKey, byte[] cipherText) {
			this.encryptedSecretKey = encryptedSecretKey;
			this.cipherText = cipherText;
		}

		/**
		 * @return The encrypted symmetric key.
		 */
		public byte[] getEncryptedSecretKey() {
			return encryptedSecretKey;
		}

		/**
		 * @return The ciphertext which was encrypted with the accompanying secret key.
		 */
		public byte[] getCipherText() {
			return cipherText;
		}
		
	}

}

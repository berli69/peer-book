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
package uk.ac.stand.cs.brb5.test.harness;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import uk.ac.stand.cs.brb5.interfaces.IEncryptionManager;

/**
 * 
 * To be used for simple tests where encryption does not matter. Currently only returns the passed plain/ciphertext
 * when asked to encrypt/decrypt. Also currently just returns null upon any key generation; this may have to 
 * change.
 * 
 * @author Ben
 *
 */
public class PlainTextEncryptionManager implements IEncryptionManager {

	public byte[] decrypt(byte[] cipherText, SecretKey key) throws GeneralSecurityException {
		return cipherText;
	}

	public byte[] decrypt(PublicKeyEncryptedBytes cipherText, PrivateKey key) throws GeneralSecurityException {
		return cipherText.getCipherText();
	}

	public byte[] encrypt(byte[] plainText, SecretKey key) throws GeneralSecurityException {
		return plainText;
	}

	public PublicKeyEncryptedBytes encrypt(byte[] plainText, PublicKey key) throws GeneralSecurityException {
		return new PublicKeyEncryptedBytes(null, plainText);
	}

	public KeyPair generateKeyPair() throws GeneralSecurityException {
		return null;
	}

	public SecretKey generatePasswordBasedKey(String password) throws GeneralSecurityException {
		return null;
	}

	public PrivateKey generatePrivateKey(byte[] keyBytes) throws GeneralSecurityException {
		return null;
	}

	public PublicKey generatePublicKey(byte[] keyBytes) throws GeneralSecurityException {
		return null;
	}

	public SecretKey generateSecretKey() throws GeneralSecurityException {
		return null;
	}

	public SecretKey generateSecretKey(byte[] keyBytes) throws GeneralSecurityException {
		return null;
	}

	public byte[] generateSignature(byte[] plainText, PrivateKey key) throws GeneralSecurityException {
		return plainText;
	}

	public boolean verifySignature(byte[] signature, byte[] plainText, PublicKey key) throws GeneralSecurityException {
		if (signature.length != plainText.length) {
			return false;
		}
		
		for (int i = 0; i < signature.length; i++) {
			if (signature[i] != plainText[i]) {
				return false;
			}
		}
		
		return true;
	}

}

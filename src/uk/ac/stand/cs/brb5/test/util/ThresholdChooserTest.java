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

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import uk.ac.stand.cs.brb5.impl.util.EncryptionManager;
import uk.ac.stand.cs.brb5.impl.util.ThresholdChooser;
import uk.ac.stand.cs.brb5.interfaces.IEncryptionManager;
import uk.ac.stand.cs.brb5.interfaces.IMessageStorageService.EncryptedMessage;

public class ThresholdChooserTest {

	@Test
	public void testFindFirstAboveThresholdByteArray() {
		Byte[] bytes1 = {Byte.valueOf("1"), Byte.valueOf("2"), Byte.valueOf("3")};
		Byte[] bytes2 = {Byte.valueOf("1"), Byte.valueOf("2"), Byte.valueOf("3")};
		Byte[] bytes3 = {Byte.valueOf("1"), Byte.valueOf("2"), Byte.valueOf("3")};
		Byte[] bytes4 = {Byte.valueOf("1"), Byte.valueOf("2"), Byte.valueOf("2")};

		List<List<Byte>> byteArrays = new ArrayList<List<Byte>>();

		byteArrays.add(Arrays.asList(bytes1));
		byteArrays.add(Arrays.asList(bytes2));
		byteArrays.add(Arrays.asList(bytes3));
		byteArrays.add(Arrays.asList(bytes4));

		List<Byte> result = new ThresholdChooser<List<Byte>>().findFirstAboveThreshold(byteArrays, 3);

		for (int i = 0; i < bytes1.length; i++) {
			assertEquals(bytes1[i], result.get(i));
		}
	}

	@Test
	public void testFindFirstAboveThresholdEncryptedMessageArray() {
		byte[] bytes1 = {1, 2, 3};
		byte[] bytes2 = {4, 5, 6};
		byte[] bytes3 = {7, 8, 9};

		EncryptedMessage encryptedMessage1 = new EncryptedMessage(bytes1, bytes2, bytes3);
		EncryptedMessage encryptedMessage2 = new EncryptedMessage(bytes3, bytes2, bytes1);

		EncryptedMessage[] encryptedMessages1 = {encryptedMessage1, encryptedMessage2};
		EncryptedMessage[] encryptedMessages2 = {encryptedMessage1, encryptedMessage2};
		EncryptedMessage[] encryptedMessages3 = {encryptedMessage1, encryptedMessage2};

		byte[] bytes4 = {10, 11, 12};
		byte[] bytes5 = {13, 14, 15};
		byte[] bytes6 = {16, 17, 18};

		EncryptedMessage badEncryptedMessage1 = new EncryptedMessage(bytes4, bytes5, bytes6);
		EncryptedMessage badEncryptedMessage2 = new EncryptedMessage(bytes6, bytes5, bytes4);

		EncryptedMessage[] badEncryptedMessages = {badEncryptedMessage1, badEncryptedMessage2};
		
		List<List<EncryptedMessage>> encryptedMessages = new ArrayList<List<EncryptedMessage>>();
		encryptedMessages.add(Arrays.asList(encryptedMessages1));
		encryptedMessages.add(Arrays.asList(encryptedMessages2));
		encryptedMessages.add(Arrays.asList(encryptedMessages3));
		encryptedMessages.add(Arrays.asList(badEncryptedMessages));
		
		List<EncryptedMessage> result = new ThresholdChooser<List<EncryptedMessage>>().findFirstAboveThreshold(encryptedMessages, 3);
		
		for (int i = 0; i < result.size(); i++) {
			assertEquals(encryptedMessages1[i], result.get(i));
		}
	}
	
	@Test
	public void testFindFirstAboveThresholdPublicKey() throws GeneralSecurityException {
		IEncryptionManager encryptionManager = new EncryptionManager();
		PublicKey publicKey1 = encryptionManager.generateKeyPair().getPublic();
		PublicKey publicKey2 = encryptionManager.generateKeyPair().getPublic();
		
		List<PublicKey> publicKeys = new ArrayList<PublicKey>();
		publicKeys.add(publicKey1);
		publicKeys.add(publicKey1);
		publicKeys.add(publicKey1);
		publicKeys.add(publicKey2);
		
		PublicKey result = new ThresholdChooser<PublicKey>().findFirstAboveThreshold(publicKeys, 3);
		
		assertEquals(publicKey1, result);
	}
	
	@Test
	public void testFindFirstAboveThresholdStringList() {
		String[] strings1 = {"a", "b", "c"};
		String[] strings2 = {"d", "e", "f"};
		
		List<List<String>> strings = new ArrayList<List<String>>();
		strings.add(Arrays.asList(strings1));
		strings.add(Arrays.asList(strings1));
		strings.add(Arrays.asList(strings1));
		strings.add(Arrays.asList(strings2));
		
		List<String> result = new ThresholdChooser<List<String>>().findFirstAboveThreshold(strings, 3);
		
		assertEquals(Arrays.asList(strings1), result);
	}

}

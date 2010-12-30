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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import uk.ac.stand.cs.brb5.impl.exceptions.FileAccessException;
import uk.ac.stand.cs.brb5.impl.storage.MessageStorageService;
import uk.ac.stand.cs.brb5.interfaces.IMessageStorageService.EncryptedMessage;
import uk.ac.stand.cs.brb5.test.TestUtils;
import uk.ac.standrews.cs.nds.p2p.interfaces.IKey;

public class MessageStorageServiceTest {

	private static final EncryptedMessage ENCRYPTED_MESSAGE = new EncryptedMessage("bla".getBytes(), "bla".getBytes(), "bla".getBytes());

	private static final IKey KEY1 = TestUtils.getNewKey();
	private static final IKey KEY2 = TestUtils.getNewKey();

	private MessageStorageService messageStorageService;

	public MessageStorageServiceTest() throws FileAccessException, IOException {
		messageStorageService = new MessageStorageService();
	}

	@Test
	public void testSaveAndGetMessages1() throws IOException {
		assertEquals(0, messageStorageService.getMessages(KEY1).length);
	}

	@Test
	public void testSaveAndGetMessages2() throws IOException {
		messageStorageService.saveMessage(ENCRYPTED_MESSAGE, KEY1);

		EncryptedMessage[] messages = messageStorageService.getMessages(KEY1);

		assertEquals(1, messages.length);
		assertEquals(ENCRYPTED_MESSAGE, messages[0]);
	}

	@Test
	public void testSaveAndGetMessages3() throws IOException {
		for (int i = 0; i < 5; i++) {
			messageStorageService.saveMessage(ENCRYPTED_MESSAGE, KEY1);
		}

		EncryptedMessage[] messages = messageStorageService.getMessages(KEY1);

		assertEquals(5, messages.length);

		for (int i = 0; i < 5; i++) {
			assertEquals(ENCRYPTED_MESSAGE, messages[i]);
		}
	}

	@Test
	public void testSaveAndGetMessages4() throws IOException {
		messageStorageService.saveMessage(ENCRYPTED_MESSAGE, KEY1);
		messageStorageService.saveMessage(ENCRYPTED_MESSAGE, KEY2);

		EncryptedMessage[] messages1 = messageStorageService.getMessages(KEY1);

		assertEquals(1, messages1.length);
		assertEquals(ENCRYPTED_MESSAGE, messages1[0]);

		EncryptedMessage[] messages2 = messageStorageService.getMessages(KEY1);

		assertEquals(1, messages2.length);
		assertEquals(ENCRYPTED_MESSAGE, messages2[0]);
	}
	
	@Test
	public void testGetStoredKeys() throws IOException {
		messageStorageService.saveMessage(ENCRYPTED_MESSAGE, KEY1);
		messageStorageService.saveMessage(ENCRYPTED_MESSAGE, KEY2);
		
		List<IKey> keys = messageStorageService.getStoredKeys();
		
		assertTrue(keys.contains(KEY1));
		assertTrue(keys.contains(KEY2));
	}
	
	@After
	public void clearUp() throws IOException {
		messageStorageService.deleteMessages(KEY1);
		messageStorageService.deleteMessages(KEY2);
	}
	
	@AfterClass
	public static void cleanUp() {
		TestUtils.deleteDirectory("Messages");
	}

}

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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;

import uk.ac.stand.cs.brb5.impl.storage.PublicProfileStorageService;
import uk.ac.stand.cs.brb5.interfaces.IPublicProfileStorageService;
import uk.ac.stand.cs.brb5.test.TestUtils;
import uk.ac.standrews.cs.nds.p2p.interfaces.IKey;

public class PublicProfileStorageServiceTest {
	
	private static final IKey KEY1 = TestUtils.getNewKey();
	private static final IKey KEY2 = TestUtils.getNewKey();
	
	private static final byte[] PROFILE1 = {1, 2, 3, 4, 5};
	private static final byte[] PROFILE2 = {6, 7, 8, 9, 10};
	
	private IPublicProfileStorageService publicProfileStore;
	
	public PublicProfileStorageServiceTest() throws IOException {
		publicProfileStore = new PublicProfileStorageService();
	}
	
	@Test
	public void testGetAndSaveProfile() throws IOException {
		publicProfileStore.savePublicProfile(PROFILE1, KEY1);
		byte[] profile1 = publicProfileStore.getPublicProfile(KEY1);
		for (int i = 0; i < profile1.length; i++) {
			assertTrue(profile1[i] == PROFILE1[i]);
		}
		
		publicProfileStore.savePublicProfile(PROFILE2, KEY2);
		byte[] profile2 = publicProfileStore.getPublicProfile(KEY2);
		for (int i = 0; i < profile2.length; i++) {
			assertTrue(profile2[i] == PROFILE2[i]);
		}
		
		publicProfileStore.savePublicProfile(PROFILE2, KEY1);
		byte[] profile3 = publicProfileStore.getPublicProfile(KEY1);
		for (int i = 0; i < profile3.length; i++) {
			assertTrue(profile3[i] == PROFILE2[i]);
		}
	}
	
	@Test
	public void testGetStoredKeys() throws IOException {
		publicProfileStore.savePublicProfile(PROFILE1, KEY1);
		publicProfileStore.savePublicProfile(PROFILE2, KEY2);
		
		List<IKey> storedKeys = publicProfileStore.getStoredKeys();
		
		assertTrue(storedKeys.contains(KEY1));
		assertTrue(storedKeys.contains(KEY2));
	}
	
	@AfterClass
	public static void cleanUp() {
		TestUtils.deleteDirectory("PrivateProfiles");
	}

}

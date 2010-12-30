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
package uk.ac.stand.cs.brb5.test.keyspace;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.stand.cs.brb5.impl.keyspace.KeySpaceManager;
import uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager;

public class KeySpaceManagerTest {
	
	private static final IKeySpaceManager KEY_SPACE_MANAGER = new KeySpaceManager(null, 0, false);
	
	@Test
	public void testHashPublicProfileName1() {
		String name1 = "name1";
		String name2 = "name2";
		
		assertEquals(KEY_SPACE_MANAGER.hashPublicProfileName(name1), KEY_SPACE_MANAGER.hashPublicProfileName(name1));
		assertFalse(KEY_SPACE_MANAGER.hashPublicProfileName(name1).equals(KEY_SPACE_MANAGER.hashPublicProfileName(name2)));
		assertFalse(KEY_SPACE_MANAGER.hashPublicProfileName(name1).equals(KEY_SPACE_MANAGER.hashPublicProfileName("")));
	}
	
	//TODO more tests on hashes.

}

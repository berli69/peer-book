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

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.stand.cs.brb5.impl.util.Synchronizer;
import uk.ac.stand.cs.brb5.test.TestUtils;
import uk.ac.standrews.cs.nds.p2p.interfaces.IKey;

public class SynchronizerTest {
	
	@Test
	public void testSynchronizer() {
		Synchronizer<IKey> synchronizer = new Synchronizer<IKey>();
		IKey key1 = TestUtils.getNewKey();
		IKey key2 = TestUtils.getNewKey();
		IKey key3 = TestUtils.getNewKey();
		
		Object synchronizerObject1 = synchronizer.getSynchronizingObject(key1);
		Object synchronizerObject2 = synchronizer.getSynchronizingObject(key2);
		Object synchronizerObject3 = synchronizer.getSynchronizingObject(key3);
		
		assertNotNull(synchronizerObject1);
		assertNotNull(synchronizerObject2);
		assertNotNull(synchronizerObject3);
		
		assertNotSame(synchronizerObject1, synchronizerObject2);
		assertNotSame(synchronizerObject1, synchronizerObject3);
		assertNotSame(synchronizerObject2, synchronizerObject3);
	}

}

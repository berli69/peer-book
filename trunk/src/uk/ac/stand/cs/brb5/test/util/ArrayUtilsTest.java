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

import uk.ac.stand.cs.brb5.impl.util.ArrayUtils;

public class ArrayUtilsTest {
	
	@Test
	public void testGetPrimitiveByteArray() {
		Byte[] byteArray1 = {};
		assertEquals(0, ArrayUtils.getPrimitiveByteArray(byteArray1).length);
		
		Byte[] byteArray2 = {3, 4, 5};
		byte[] result2 = ArrayUtils.getPrimitiveByteArray(byteArray2);
		assertEquals(byteArray2.length, result2.length);
		for (int i = 0; i < byteArray2.length; i++) {
			assertTrue(byteArray2[i].equals(result2[i]));
		}
	}
	
	@Test
	public void testGetObjectByteArray() {
		byte[] byteArray1 = {};
		assertEquals(0, ArrayUtils.getObjectByteArray(byteArray1).length);
		
		byte[] byteArray2 = {3, 4, 5};
		Byte[] result2 = ArrayUtils.getObjectByteArray(byteArray2);
		assertEquals(byteArray2.length, result2.length);
		for (int i = 0; i < byteArray2.length; i++) {
			assertTrue(byteArray2[i] == result2[i].byteValue());
		}
	}

}

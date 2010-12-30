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

/**
 * 
 * Useful utility methods for arrays.
 * 
 * @author Ben
 *
 */
public class ArrayUtils {

	/**
	 * Transforms the given Byte[] array (object Byte as opposed to primitive byte) into a byte[] array.
	 * 
	 * @param objectByteArray A Byte[] array.
	 * @return A byte[] array.
	 */
	public static byte[] getPrimitiveByteArray(Byte[] objectByteArray) {
		byte[] result = new byte[objectByteArray.length];

		for (int i = 0; i < objectByteArray.length; i++) {
			result[i] = objectByteArray[i];
		}

		return result;
	}

	/**
	 * Transforms the given byte[] array (primitive byte as opposed to object Byte) into a Byte[] array.
	 * 
	 * @param primitiveByteArray A byte[] array.
	 * @return A Byte[] array.
	 */
	public static Byte[] getObjectByteArray(byte[] primitiveByteArray) {
		Byte[] result = new Byte[primitiveByteArray.length];

		for (int i = 0; i < primitiveByteArray.length; i++) {
			result[i] = primitiveByteArray[i];
		}

		return result;
	}

}

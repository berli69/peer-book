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

import java.util.zip.DataFormatException;

import org.junit.Test;

import uk.ac.stand.cs.brb5.impl.util.CompressionManager;
import uk.ac.stand.cs.brb5.interfaces.ICompressionManager;

public class CompressionManagerTest {
	
	@Test
	public void testCompressDecompressEmpty() throws DataFormatException {
		String empty = "";
		ICompressionManager compressionManager = new CompressionManager();
		
		byte[] compressed = compressionManager.compress(empty);
		
		assertEquals(empty, compressionManager.deCompress(compressed));
	}
	
	@Test
	public void testCompressDecompressLong() throws DataFormatException {
		ICompressionManager compressionManager = new CompressionManager();
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < 1000; i++) {
			builder.append("something 2344-<>!@£$%^&*(){}''\"\";.,/?:|\\/./");
		}
		
		byte[] compressed = compressionManager.compress(builder.toString());
		
		assertEquals(builder.toString(), compressionManager.deCompress(compressed));
	}

}

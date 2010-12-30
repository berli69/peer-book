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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import uk.ac.stand.cs.brb5.interfaces.ICompressionManager;
import uk.ac.standrews.cs.nds.util.Diagnostic;
import uk.ac.standrews.cs.nds.util.DiagnosticLevel;

/**
 * 
 * Default ZIP compression.
 * 
 * @author Ben
 *
 */
public class CompressionManager implements ICompressionManager {

	private static final String UTF_8 = "UTF-8";
	private static final int BUFFER_SIZE = 1024;
	

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.ICompressionManager#compress(java.lang.String)
	 */
	public byte[] compress(String toBeCompressed) {

		byte[] input = null;
		try {
			input = toBeCompressed.getBytes(UTF_8);
		} catch (UnsupportedEncodingException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
		}

		Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION);
		compressor.setInput(input);
		compressor.finish();

		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(input.length);
		byte[] buffer = new byte[BUFFER_SIZE];
		
		while (!compressor.finished()) {
			int count = compressor.deflate(buffer);
			byteOutputStream.write(buffer, 0, count);
		}
		
		try {
			byteOutputStream.close();
		} catch (IOException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
		}

		return byteOutputStream.toByteArray();
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.ICompressionManager#deCompress(byte[])
	 */
	public String deCompress(byte[] compressedBytes) throws DataFormatException {
		
		Inflater decompressor = new Inflater();
		decompressor.setInput(compressedBytes);
		
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(compressedBytes.length);
		byte[] buffer = new byte[BUFFER_SIZE];
		
		while (!decompressor.finished()) {
			int count = decompressor.inflate(buffer);
			byteOutputStream.write(buffer, 0, count);
		}
		
		byte[] output = byteOutputStream.toByteArray();
		String finalString = null;
		try {
			finalString = new String(output, UTF_8);
		} catch (UnsupportedEncodingException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
		}
		
		return finalString;
	}


}

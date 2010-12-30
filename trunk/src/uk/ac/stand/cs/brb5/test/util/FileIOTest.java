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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import uk.ac.stand.cs.brb5.impl.util.FileIO;

public class FileIOTest {

	@Test
	public void testStoreFile() throws IOException {
		FileIO.storeFile("blablabla".getBytes(), "bla.txt", "");

		File file = new File("bla.txt");
		assertTrue(file.exists());

		FileIO.deleteFile("bla.txt", "");
	}

	@Test
	public void testFileExists() throws IOException {
		assertFalse(FileIO.fileExists("bla.txt", ""));

		FileIO.storeFile("blablabla".getBytes(), "bla.txt", "");

		assertTrue(FileIO.fileExists("bla.txt", ""));

		FileIO.deleteFile("bla.txt", "");
	}

	@Test
	public void testReadFile() throws IOException {
		String text = "blablabla ";
		text += "\nblabla";
		FileIO.storeFile(text.getBytes(), "bla.txt", "");

		String fileText = new String(FileIO.readFile("bla.txt", ""));

		assertEquals(fileText, text);

		FileIO.deleteFile("bla.txt", "");
	}

	@Test
	public void testDeleteFile() throws IOException {
		FileIO.storeFile("blablabla".getBytes(), "bla.txt", "");

		File file = new File("bla.txt");
		FileIO.deleteFile("bla.txt", "");

		assertFalse(file.exists());
	}
	
	@Test public void testMakeDirectory() throws IOException {
		FileIO.makeDirectory("bla");
		
		File directory = new File("bla");
		assertTrue(directory.exists() && directory.isDirectory());
		
		FileIO.deleteFile("bla", "");
	}
	
	@Test
	public void testListFiles() throws IOException {
		FileIO.makeDirectory("files");
		
		for (int i = 0; i < 10; i++) {
			FileIO.storeFile("".getBytes(), String.valueOf(i), "files/");
		}
		
		List<String> filenames = FileIO.listFiles("files");
		
		assertEquals(10, filenames.size());
		
		for (int i = 0; i < 10; i++) {
			assertTrue(filenames.contains(String.valueOf(i)));
		}
		
		// Clean up
		for (int i = 0; i < 10; i++) {
			FileIO.deleteFile(String.valueOf(i), "files/");
		}
		
		FileIO.deleteFile("files", "");
	}
	
	@Test
	public void testDeleteFilesInDirectoryWithPrefix() throws IOException {
		FileIO.makeDirectory("files");
		String prefix = "bla";
		
		FileIO.storeFile("".getBytes(), prefix + "sometext", "files/");
		FileIO.deleteFilesInDirectoryWithPrefix("files", prefix);
		
		for (String filename : FileIO.listFiles("files")) {
			assertFalse(filename.startsWith(prefix));
		}
		
		FileIO.deleteFile("files", "");
	}

}

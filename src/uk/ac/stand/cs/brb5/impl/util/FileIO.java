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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * Utility class used for IO operations on files.
 * 
 * @author Ben
 *
 */
public class FileIO {

	/**
	 * @param fileText The contents of the file.
	 * @param filename The filename.
	 * @param path The directory of the file.
	 * @throws IOException
	 */
	public static void storeFile(byte[] fileText, String filename, String path) throws IOException {
		File newFile = new File(path + filename);
		FileOutputStream fileOutput = new FileOutputStream(newFile);
		fileOutput.write(fileText);
		fileOutput.close();
	}

	/**
	 * @param filename The filename.
	 * @param path The directory of the file.
	 * @return True if the filename in the directory exists, false otherwise.
	 */
	public static boolean fileExists(String filename, String path) {
		return new File(path + filename).exists();
	}

	/**
	 * @param filename The filename.
	 * @param path The directory of the file.
	 * @return The contents of the file.
	 * @throws IOException
	 */
	public static byte[] readFile(String filename, String path) throws IOException {

		File file = new File(path + filename);
		FileInputStream in = new FileInputStream(file);
		byte[] fileBytes = new byte[in.available()];

		int b;
		int i = 0;
		while ((b = in.read()) != -1) {
			fileBytes[i++] = (byte)b;
		}

		return fileBytes;
	}

	/**
	 * @param filename The filename.
	 * @param path The directory of the file.
	 * @throws IOException
	 */
	public static void deleteFile(String filename, String path) throws IOException {
		File file = new File(path + filename);

		if (file.exists()) {
			if (!file.delete()) {
				throw new IOException("File '" + path + filename + "' could not be deleted.");
			}
		}
	}

	/**
	 * @param directory The directory name.
	 * @throws IOException
	 */
	public static void makeDirectory(String directory) throws IOException {
		File newDir = new File(directory);
		if (!newDir.mkdir()) {
			throw new IOException("Directory '" + directory + "' could not be created.");
		}
	}

	/**
	 * @param directoryName The name of the directory.
	 * @return A list of the filenames in the directory.
	 */
	public static List<String> listFiles(String directoryName) {
		File directory = new File(directoryName);
		return Arrays.asList(directory.list());
	}

	/**
	 * @param directory The name of the directory.
	 * @param prefix The prefix of the files to be deleted.
	 * @throws IOException
	 */
	public static void deleteFilesInDirectoryWithPrefix(String directory, String prefix) throws IOException {
		File file = new File(directory);

		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File subFile : files) {
				if (subFile.getName().startsWith(prefix)) {
					if (!subFile.delete()) {
						throw new IOException("File called " + subFile.getName() + " in '" + directory + "' could not be deleted.");
					}
				}
			}

		}
	}

}

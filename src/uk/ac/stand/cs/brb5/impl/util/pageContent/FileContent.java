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
package uk.ac.stand.cs.brb5.impl.util.pageContent;

import java.io.IOException;

import uk.ac.stand.cs.brb5.impl.util.FileIO;
import uk.ac.standrews.cs.nds.util.Diagnostic;
import uk.ac.standrews.cs.nds.util.DiagnosticLevel;

/**
 * 
 * Models the content of a file.
 * 
 * @author Ben
 *
 */
public class FileContent extends Content {
	
	protected String filename;
	protected String filepath;

	/**
	 * @see uk.ac.stand.cs.brb5.impl.util.pageContent.Content#getContent()
	 */
	@Override
	public byte[] getContent() {
		byte[] fileBytes = null;
		try {
			fileBytes = FileIO.readFile(filename, filepath);
		} catch (IOException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, "IOException when reading file", filename, e);
			fileBytes = new byte[0];
		}
		return fileBytes;
	}

}

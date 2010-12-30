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
package uk.ac.stand.cs.brb5.impl.exceptions;

/**
 * 
 * Used when any problems are encountered when attempting to access/read
 * PeerBook profiles or messages.
 * 
 * @author Ben
 *
 */
public class FileAccessException extends PeerBookException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 263798533732217000L;

	public FileAccessException() {
		super();
	}

	public FileAccessException(String string) {
		super(string);
	}

}

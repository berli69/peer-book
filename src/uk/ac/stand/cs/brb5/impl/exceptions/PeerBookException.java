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
 * General superclass of all PeerBook exceptions. 
 * 
 * @author Ben
 *
 */
public class PeerBookException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3528421316264973819L;

	public PeerBookException() {
		super();
	}

	public PeerBookException(String message) {
		super(message);
	}
	
	public PeerBookException(Exception exception) {
		super(exception);
	}

}

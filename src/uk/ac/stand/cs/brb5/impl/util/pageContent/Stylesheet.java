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

/**
 * 
 * Models a CSS stylesheet for a HTML page.
 * 
 * @author Ben
 *
 */
public class Stylesheet extends FileContent {

	private static final String STYLESHEET_PATH = "stylesheets/";

	/**
	 * @param filename The stylesheet's name.
	 */
	public Stylesheet(String filename) {
		this.contentType = CONTENT_TYPE_CSS;
		this.filename = filename;
		this.filepath = STYLESHEET_PATH;
	}

}

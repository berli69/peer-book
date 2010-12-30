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
 * Abstractly models any content sent back over HTTP.
 * 
 * @author Ben
 *
 */
public abstract class Content {

	public static final String CONTENT_TYPE_IMG = "image/";
	public static final String CONTENT_TYPE_HTML = "text/html";
	public static final String CONTENT_TYPE_CSS = "text/css";
	public static final String CONTENT_TYPE_TEXT = "text/plain";
	
	protected String contentType;

	/**
	 * @return The content type (one of the static Strings, e.g. CONTENT_TYPE_...).
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @return The content of the object/file.
	 */
	public abstract byte[] getContent();

}

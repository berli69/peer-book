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
package uk.ac.stand.cs.brb5.impl.http;

/**
 * 
 * Simple wrapper class containing the various parts of a PeerBook HTTP request.
 * 
 * @author Ben
 *
 */
public class Request {

	private String requestedPage;
	private Arguments arguments;
	private String username;

	/**
	 * @param requestedPage The page requested by the user.
	 * @param args A string containing argument name/value pairs (of the form required
	 * in the Arguments constructor).
	 * @param username The user's username (obtained from a Cookie, if one was sent).
	 */
	public Request(String requestedPage, String args, String username) {
		this.requestedPage = requestedPage;

		if (args != null) {
			this.arguments = new Arguments(args);
		}

		this.username = username;
	}

	/**
	 * @return The page request in the HTTP header.
	 */
	public String getRequestedPage() {
		return requestedPage;
	}

	/**
	 * @return Arguments given in the request.
	 */
	public Arguments getArguments() {
		return arguments;
	}

	/**
	 * @return Any username sent in a cookie in the request.
	 */
	public String getUsername() {
		return username;
	}

}

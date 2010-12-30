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
package uk.ac.stand.cs.brb5.impl.pageHandlers;

import java.net.HttpCookie;

import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.impl.http.ArgumentType;
import uk.ac.stand.cs.brb5.impl.http.Request;
import uk.ac.stand.cs.brb5.impl.http.Response;
import uk.ac.stand.cs.brb5.impl.util.pageContent.Content;
import uk.ac.stand.cs.brb5.impl.util.pageContent.HtmlUtils;
import uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel;

/**
 * 
 * Handles login requests.
 * 
 * @author Ben
 *
 */
public class LoginHandler extends AbstractHandler {

	private static final long MAX_COOKIE_AGE = 60 * 60 * 24; // A day.

	/**
	 * Calls parent constructor with string "/login".
	 * 
	 * @see uk.ac.stand.cs.brb5.impl.pageHandlers.AbstractHandler
	 */
	public LoginHandler(IPeerBookKernel kernel, HtmlUtils htmlUtils) {
		super(kernel, "/login", htmlUtils, false);
	}

	/**
	 * If given no arguments, and user is logged in, return their profile. If not logged in, return the login page.
	 * 
	 * Otherwise, attempt to login with given arguments and return the user's profile if successful. If 
	 * unsuccessful, return the login page.
	 * 
	 * @see uk.ac.stand.cs.brb5.interfaces.IPageHandler#handleRequest(uk.ac.stand.cs.brb5.impl.http.Request)
	 */
	public Response handleRequest(Request request) throws PeerBookException {

		Content page = null;
		HttpCookie cookie = null;

		if (request.getArguments() == null) {
			if (kernel.isLoggedIn(request.getUsername())) {
				page = htmlUtils.profile(request.getUsername(), kernel.getProfile(request.getUsername(), request.getUsername()));
			} else {
				page = htmlUtils.login(false);
			}
		} else {

			String username = request.getArguments().getArgument(ArgumentType.USERNAME);
			String password = request.getArguments().getArgument(ArgumentType.PASSWORD);

			if (username == null || password == null) {
				page = htmlUtils.login(false);
			} else {
				try {
					kernel.login(username, password);
					page = htmlUtils.profile(username, kernel.getProfile(username, username));
					cookie = makeUsernameCookie(username);
				} catch (PeerBookException e) {
					return new Response(htmlUtils.login(true));
				}
			}
		}

		return new Response(page, cookie);
	}

	private HttpCookie makeUsernameCookie(String username) {
		HttpCookie cookie = new HttpCookie(ArgumentType.USERNAME.toString(), username);
		cookie.setMaxAge(MAX_COOKIE_AGE);
		return cookie;
	}

}

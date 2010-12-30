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

import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.impl.http.ArgumentType;
import uk.ac.stand.cs.brb5.impl.http.Request;
import uk.ac.stand.cs.brb5.impl.http.Response;
import uk.ac.stand.cs.brb5.impl.util.pageContent.Content;
import uk.ac.stand.cs.brb5.impl.util.pageContent.HtmlUtils;
import uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel;
import uk.ac.standrews.cs.nds.util.Diagnostic;
import uk.ac.standrews.cs.nds.util.DiagnosticLevel;

/**
 * 
 * Handles profile creation requests.
 * 
 * @author Ben
 *
 */
public class CreateProfileHandler extends AbstractHandler {

	/**
	 * Calls parent constructor with string "/createProfile".
	 * 
	 * @see uk.ac.stand.cs.brb5.impl.pageHandlers.AbstractHandler
	 */
	public CreateProfileHandler(IPeerBookKernel kernel, HtmlUtils htmlUtils) {
		super(kernel, "/createProfile", htmlUtils, false);
	}

	/**
	 * If username/password are provided, creates the profile and returns the login page. Otherwise,
	 * returns the create profile page.
	 * 
	 * @see uk.ac.stand.cs.brb5.interfaces.IPageHandler#handleRequest(uk.ac.stand.cs.brb5.impl.http.Request)
	 */
	public Response handleRequest(Request request) throws PeerBookException {

		Content page = null;
		String username = request.getArguments().getArgument(ArgumentType.USERNAME);
		String password = request.getArguments().getArgument(ArgumentType.PASSWORD);

		if (username == null || password == null) {
			page = htmlUtils.createProfile(false);
		} else {
			try {
				kernel.createProfile(username, password);
				page = htmlUtils.login(false);
			} catch (PeerBookException e) {
				Diagnostic.trace(DiagnosticLevel.RESULT, e);
				page = htmlUtils.createProfile(true);
			}
		}

		return new Response(page);
	}

}

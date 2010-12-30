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
import uk.ac.stand.cs.brb5.impl.http.Request;
import uk.ac.stand.cs.brb5.impl.http.Response;
import uk.ac.stand.cs.brb5.impl.util.pageContent.Content;
import uk.ac.stand.cs.brb5.impl.util.pageContent.HtmlUtils;
import uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel;

/**
 * 
 * Handles requests for updating a user's profile.
 * 
 * @author Ben
 *
 */
public class UpdateProfileHandler extends AbstractHandler {

	/**
	 * Calls parent constructor with string "/updateProfile".
	 * 
	 * @see uk.ac.stand.cs.brb5.impl.pageHandlers.AbstractHandler
	 */
	public UpdateProfileHandler(IPeerBookKernel kernel, HtmlUtils htmlUtils) {
		super(kernel, "/updateProfile", htmlUtils, true);
	}

	/**
	 * If the user is logged in, and the request includes updated profile fields, update the user's profile. If the request did
	 * not include updated profile fields, return the update profile page.
	 * 
	 * @see uk.ac.stand.cs.brb5.interfaces.IPageHandler#handleRequest(uk.ac.stand.cs.brb5.impl.http.Request)
	 */
	public Response handleRequest(Request request) throws PeerBookException {

		Content page = null;

		if (request.getArguments().profileWasUpdated()) {
			try {
				kernel.updateProfile(request.getUsername(), request.getArguments().getUpdatedFields());
				page = htmlUtils.profile(request.getUsername(), kernel.getProfile(request.getUsername(), request.getUsername()));
			} catch (PeerBookException e) {
				page = htmlUtils.updateProfile(true, kernel.getProfile(request.getUsername(), request.getUsername()));
			}
		} else {
			page = htmlUtils.updateProfile(false, kernel.getProfile(request.getUsername(), request.getUsername()));
		}

		return new Response(page);
	}

}

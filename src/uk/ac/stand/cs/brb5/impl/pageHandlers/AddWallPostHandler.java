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

import uk.ac.stand.cs.brb5.impl.exceptions.MalformedHttpRequestException;
import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.impl.http.ArgumentType;
import uk.ac.stand.cs.brb5.impl.http.Request;
import uk.ac.stand.cs.brb5.impl.http.Response;
import uk.ac.stand.cs.brb5.impl.util.pageContent.Content;
import uk.ac.stand.cs.brb5.impl.util.pageContent.HtmlUtils;
import uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel;

/**
 * 
 * Handles add wall post requests.
 * 
 * @author Ben
 *
 */
public class AddWallPostHandler extends AbstractHandler {

	/**
	 * Calls parent constructor with string "/addWallPost".
	 * 
	 * @see uk.ac.stand.cs.brb5.impl.pageHandlers.AbstractHandler
	 */
	public AddWallPostHandler(IPeerBookKernel kernel, HtmlUtils htmlUtils) {
		super(kernel, "/addWallPost", htmlUtils, true);
	}

	/**
	 * If logged in, submits wall post and returns the profile which was being viewed.
	 * 
	 * @see uk.ac.stand.cs.brb5.interfaces.IPageHandler#handleRequest(uk.ac.stand.cs.brb5.impl.http.Request)
	 */
	public Response handleRequest(Request request) throws PeerBookException {

		Content page = null;
		String friendName = request.getArguments().getArgument(ArgumentType.FRIENDNAME);
		String message = request.getArguments().getArgument(ArgumentType.MESSAGE);

		if (friendName == null || message == null) {
			throw new MalformedHttpRequestException();
		}
		if (!message.equals("")) {
			kernel.sendWallPost(request.getUsername(), friendName, message);
		}
		page = htmlUtils.profile(request.getUsername(), kernel.getProfile(request.getUsername(), friendName));

		return new Response(page);
	}

}

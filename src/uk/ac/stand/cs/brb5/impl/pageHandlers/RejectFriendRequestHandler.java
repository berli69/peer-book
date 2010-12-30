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

import java.math.BigInteger;

import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.impl.http.ArgumentType;
import uk.ac.stand.cs.brb5.impl.http.Request;
import uk.ac.stand.cs.brb5.impl.http.Response;
import uk.ac.stand.cs.brb5.impl.util.pageContent.HtmlUtils;
import uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel;

/**
 * 
 * Handles friend request rejections.
 * 
 * @author Ben
 *
 */
public class RejectFriendRequestHandler extends AbstractHandler {

	/**
	 * Calls parent constructor with string "/rejectFriendRequest".
	 * 
	 * @see uk.ac.stand.cs.brb5.impl.pageHandlers.AbstractHandler
	 */
	public RejectFriendRequestHandler(IPeerBookKernel kernel, HtmlUtils htmlUtils) {
		super(kernel, "/rejectFriendRequest", htmlUtils, true);
	}

	/**
	 * If user is logged in, delete the given friend request and return the user's messages page.
	 * 
	 * @see uk.ac.stand.cs.brb5.interfaces.IPageHandler#handleRequest(uk.ac.stand.cs.brb5.impl.http.Request)
	 */
	public Response handleRequest(Request request) throws PeerBookException {
		String username = request.getUsername();
		kernel.deleteFriendRequest(username, new BigInteger(request.getArguments().getArgument(ArgumentType.FRIEND_REQUEST_NUMBER)));
		kernel.retrieveMessages(username);
		return new Response(htmlUtils.messages(kernel.getMessages(username), kernel.getFriendRequests(username)));
	}

}

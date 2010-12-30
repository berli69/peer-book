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

import java.util.ArrayList;
import java.util.List;

import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.impl.http.ArgumentType;
import uk.ac.stand.cs.brb5.impl.http.Request;
import uk.ac.stand.cs.brb5.impl.http.Response;
import uk.ac.stand.cs.brb5.impl.kernel.StandardMessages;
import uk.ac.stand.cs.brb5.impl.util.pageContent.Content;
import uk.ac.stand.cs.brb5.impl.util.pageContent.HtmlUtils;
import uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel;

/**
 * 
 * Handles add friend requests.
 * 
 * @author Ben
 *
 */
public class AddFriendHandler extends AbstractHandler {

	private static final String PAGE_NAME = "/addFriend";

	/**
	 * Calls parent constructor with string "/addFriend".
	 * 
	 * @see uk.ac.stand.cs.brb5.impl.pageHandlers.AbstractHandler
	 */
	public AddFriendHandler(IPeerBookKernel kernel, HtmlUtils htmlUtils) {
		super(kernel, PAGE_NAME, htmlUtils, true);
	}

	/**
	 * If friend's name is given, adds that friend and returns sent message page.
	 * If friend's name is not given, returns add friend page.
	 * 
	 * @see uk.ac.stand.cs.brb5.interfaces.IPageHandler#handleRequest(uk.ac.stand.cs.brb5.impl.http.Request)
	 */
	public Response handleRequest(Request request) throws PeerBookException {

		Content page = null;
		String chosenName = request.getArguments().getArgument(ArgumentType.CHOSEN_NAME);
		String search = request.getArguments().getArgument(ArgumentType.SEARCH);

		if (chosenName == null) {
			List<String> notIncluded = new ArrayList<String>();
			notIncluded.add(request.getUsername());
			
			page = doSearch(search, PAGE_NAME, notIncluded);
		} else {
			kernel.addFriend(request.getUsername(), chosenName);
			page = htmlUtils.sentMessage(chosenName, StandardMessages.ADD_FRIEND_MESSAGE);
		}

		return new Response(page);
	}

}

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

import java.util.ArrayList;
import java.util.List;

import uk.ac.stand.cs.brb5.impl.exceptions.MalformedHttpRequestException;
import uk.ac.stand.cs.brb5.impl.pageHandlers.AcceptFriendRequestHandler;
import uk.ac.stand.cs.brb5.impl.pageHandlers.AddFriendHandler;
import uk.ac.stand.cs.brb5.impl.pageHandlers.AddWallPostHandler;
import uk.ac.stand.cs.brb5.impl.pageHandlers.CreateProfileHandler;
import uk.ac.stand.cs.brb5.impl.pageHandlers.DeleteMessageHandler;
import uk.ac.stand.cs.brb5.impl.pageHandlers.DeleteStatusHandler;
import uk.ac.stand.cs.brb5.impl.pageHandlers.DeleteWallPostHandler;
import uk.ac.stand.cs.brb5.impl.pageHandlers.ImageHandler;
import uk.ac.stand.cs.brb5.impl.pageHandlers.IndexHandler;
import uk.ac.stand.cs.brb5.impl.pageHandlers.LoginHandler;
import uk.ac.stand.cs.brb5.impl.pageHandlers.LogoutHandler;
import uk.ac.stand.cs.brb5.impl.pageHandlers.RejectFriendRequestHandler;
import uk.ac.stand.cs.brb5.impl.pageHandlers.RemoveFriendHandler;
import uk.ac.stand.cs.brb5.impl.pageHandlers.SendMessageHandler;
import uk.ac.stand.cs.brb5.impl.pageHandlers.StylesheetHandler;
import uk.ac.stand.cs.brb5.impl.pageHandlers.UpdateProfileHandler;
import uk.ac.stand.cs.brb5.impl.pageHandlers.ViewFriendsHandler;
import uk.ac.stand.cs.brb5.impl.pageHandlers.ViewMessagesHandler;
import uk.ac.stand.cs.brb5.impl.pageHandlers.ViewProfileHandler;
import uk.ac.stand.cs.brb5.impl.util.pageContent.HtmlUtils;
import uk.ac.stand.cs.brb5.interfaces.IPageHandler;
import uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel;
import uk.ac.stand.cs.brb5.interfaces.IRequestHandler;
import uk.ac.standrews.cs.nds.util.Diagnostic;
import uk.ac.standrews.cs.nds.util.DiagnosticLevel;

/**
 * 
 * Handles the various requests that may be sent to a PeerBook server from a browser.
 * Uses the various IPageHandler classes to provide this functionality.
 * In order to add a new 'page' to PeerBook, a new IPageHandler must be written, and
 * added to the list of IPageHandlers inside this class.
 * 
 * @author Ben
 *
 */
public class PeerBookClientRequestHandler implements IRequestHandler {

	private List<IPageHandler> pageHandlers;
	private HtmlUtils htmlUtils;
	private IPeerBookKernel kernel;

	/**
	 * @param kernel The IPeerBookKernel which lies one layer below this (and will be
	 * responsible for dealing with PeerBook data).
	 * @param htmlUtils Used to serve HTML.
	 */
	public PeerBookClientRequestHandler(IPeerBookKernel kernel, HtmlUtils htmlUtils) {
		this.pageHandlers = getRequestHandlers(kernel, htmlUtils);
		this.kernel = kernel;
		this.htmlUtils = htmlUtils;
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IRequestHandler#handleRequest(uk.ac.stand.cs.brb5.impl.http.Request)
	 */
	public Response handleRequest(Request request) throws MalformedHttpRequestException {

		try {
			for (IPageHandler handler : pageHandlers) {
				if (handler.handlesRequest(request.getRequestedPage())) {
					if (handler.userMustBeLoggedIn() && !kernel.isLoggedIn(request.getUsername())) {
						return new Response(htmlUtils.login(false));
					} else {
						return handler.handleRequest(request);
					}
				}
			}

			return new Response(htmlUtils.error404());
		} catch (Exception e) {
			Diagnostic.trace(DiagnosticLevel.RUNALL, e);
			return new Response(htmlUtils.exception(null, e));
		}
	}

	private static List<IPageHandler> getRequestHandlers(IPeerBookKernel kernel, HtmlUtils htmlUtils) {
		//TODO: dynamically load these via reflection.

		List<IPageHandler> handlers = new ArrayList<IPageHandler>();

		handlers.add(new AcceptFriendRequestHandler(kernel, htmlUtils));
		handlers.add(new AddFriendHandler(kernel, htmlUtils));
		handlers.add(new AddWallPostHandler(kernel, htmlUtils));
		handlers.add(new CreateProfileHandler(kernel, htmlUtils));
		handlers.add(new DeleteMessageHandler(kernel, htmlUtils));
		handlers.add(new DeleteStatusHandler(kernel, htmlUtils));
		handlers.add(new DeleteWallPostHandler(kernel, htmlUtils));
		handlers.add(new ImageHandler());
		handlers.add(new IndexHandler(kernel, htmlUtils));
		handlers.add(new LoginHandler(kernel, htmlUtils));
		handlers.add(new LogoutHandler(kernel, htmlUtils));
		handlers.add(new RejectFriendRequestHandler(kernel, htmlUtils));
		handlers.add(new RemoveFriendHandler(kernel, htmlUtils));
		handlers.add(new SendMessageHandler(kernel, htmlUtils));
		handlers.add(new StylesheetHandler());
		handlers.add(new UpdateProfileHandler(kernel, htmlUtils));
		handlers.add(new ViewFriendsHandler(kernel, htmlUtils));
		handlers.add(new ViewMessagesHandler(kernel, htmlUtils));
		handlers.add(new ViewProfileHandler(kernel, htmlUtils));

		return handlers;
	}

}

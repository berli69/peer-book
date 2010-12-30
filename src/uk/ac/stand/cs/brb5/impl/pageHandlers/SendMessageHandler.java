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

import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.impl.http.ArgumentType;
import uk.ac.stand.cs.brb5.impl.http.Request;
import uk.ac.stand.cs.brb5.impl.http.Response;
import uk.ac.stand.cs.brb5.impl.kernel.StandardMessages;
import uk.ac.stand.cs.brb5.impl.util.pageContent.Content;
import uk.ac.stand.cs.brb5.impl.util.pageContent.HtmlUtils;
import uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel;
import uk.ac.standrews.cs.nds.util.Diagnostic;
import uk.ac.standrews.cs.nds.util.DiagnosticLevel;

/**
 * 
 * Handles message sending requests.
 * 
 * @author Ben
 *
 */
public class SendMessageHandler extends AbstractHandler {

	private static final String PAGE_NAME = "/sendMessage";

	/**
	 * Calls parent constructor with string "/sendMessage".
	 * 
	 * @see uk.ac.stand.cs.brb5.impl.pageHandlers.AbstractHandler
	 */
	public SendMessageHandler(IPeerBookKernel kernel, HtmlUtils htmlUtils) {
		super(kernel, PAGE_NAME, htmlUtils, true);
	}

	/**
	 * If the user is logged in, and the message is empty (but the recipient is not), show the message sending
	 * page with the recipient filled in. If the message is non-empty, send the message to the given recipient.
	 * 
	 * @see uk.ac.stand.cs.brb5.interfaces.IPageHandler#handleRequest(uk.ac.stand.cs.brb5.impl.http.Request)
	 */
	public Response handleRequest(Request request) throws PeerBookException {

		Content page = null;
		String message = request.getArguments().getArgument(ArgumentType.MESSAGE);
		String chosenName = request.getArguments().getArgument(ArgumentType.CHOSEN_NAME);
		String search = request.getArguments().getArgument(ArgumentType.SEARCH);
		String recipient = request.getArguments().getArgument(ArgumentType.RECIPIENT);

			if (recipient == null) {
				if (chosenName == null) {
					page = doSearch(search, PAGE_NAME, new ArrayList<String>());
				} else {
					page = doMessage(request, message, chosenName);
				}
			} else {
				page = doMessage(request, message, recipient);
			}

		return new Response(page);
	}

	private Content doMessage(Request request, String message, String chosenName) throws PeerBookException {
		if (message == null) {
			return htmlUtils.sendMessage(false, chosenName);
		} else {
			try {
				kernel.sendMessage(request.getUsername(), chosenName, message, null, StandardMessages.MessageType.NORMAL_MESSAGE);
				return htmlUtils.sentMessage(chosenName, message);
			} catch (PeerBookException e) {
				Diagnostic.trace(DiagnosticLevel.RUNALL, e);
				return htmlUtils.sendMessage(true, null);
			}
		}
	}

}

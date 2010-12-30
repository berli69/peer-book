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

import java.util.List;

import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.impl.util.pageContent.Content;
import uk.ac.stand.cs.brb5.impl.util.pageContent.HtmlUtils;
import uk.ac.stand.cs.brb5.interfaces.IPageHandler;
import uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel;
import uk.ac.standrews.cs.nds.util.Diagnostic;
import uk.ac.standrews.cs.nds.util.DiagnosticLevel;

/**
 * 
 * Abstract type from which all other IPageHandlers inherit. Provides the method handlesRequest
 * which is used one layer above in the PeerBook model.
 * 
 * @author Ben
 *
 */
public abstract class AbstractHandler implements IPageHandler {
	
	protected IPeerBookKernel kernel;
	private String requestType;
	protected HtmlUtils htmlUtils;
	private boolean mustBeLoggedIn;

	/**
	 * @param kernel IPeerBookKernel of current PeerBook instance.
	 * @param requestType Request string for which this IPageHandler handles.
	 * @param htmlUtils Used to serve HTML.
	 */
	public AbstractHandler(IPeerBookKernel kernel, String requestType, HtmlUtils htmlUtils, boolean mustBeLoggedIn) {
		this.kernel = kernel;
		this.requestType = requestType;
		this.htmlUtils = htmlUtils;
		this.mustBeLoggedIn = mustBeLoggedIn;
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IPageHandler#handlesRequest(java.lang.String)
	 */
	public boolean handlesRequest(String requestType) {
		return this.requestType.equals(requestType);
	}

	protected Content doSearch(String search, String actionPage, List<String> notIncluded) throws PeerBookException {
		if (search == null) {
			return htmlUtils.search(false, null, actionPage);
		} else {
			try {
				List<String> searchResults = kernel.usernameSearch(search);
				searchResults.removeAll(notIncluded);
				
				return htmlUtils.search(false, searchResults, actionPage);
			} catch (PeerBookException e) {
				Diagnostic.trace(DiagnosticLevel.RESULT, e);
				return htmlUtils.search(true, null, actionPage);
			}
		}
	}
	
	public boolean userMustBeLoggedIn() {
		return mustBeLoggedIn;
	}

}

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

import java.net.HttpCookie;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.ac.stand.cs.brb5.impl.util.pageContent.Content;

/**
 * 
 * Models a HTTP Response to a Request.
 * 
 * @author Ben
 *
 */
public class Response {

	private Content page;
	private HttpCookie usernameCookie;

	/**
	 * @param page The page being returned to the browser.
	 */
	public Response(Content page) {
		this(page, null);
	}

	/**
	 * @param page The page being returned to the browser.
	 * @param usernameCookie A username cookie being returned to the browser.
	 */
	public Response(Content page, HttpCookie usernameCookie) {
		this.page = page;
		this.usernameCookie = usernameCookie;
	}

	/**
	 * @return A properly formatted HTTP Response, including header and the actual page content.
	 */
	public String getHttpHeader() {

		SimpleDateFormat httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

		StringBuilder responseBuilder = new StringBuilder("HTTP/1.1 200 OK");
		responseBuilder.append(HttpServer.EOL);

		responseBuilder.append("Date: ");
		responseBuilder.append(httpDateFormat.format(new Date()));
		responseBuilder.append(HttpServer.EOL);

		responseBuilder.append("Server: PeerBook");
		responseBuilder.append(HttpServer.EOL);

		responseBuilder.append("Content-Length: ");
		responseBuilder.append(page.getContent().length);
		responseBuilder.append(HttpServer.EOL);

		responseBuilder.append("Connection: close");
		responseBuilder.append(HttpServer.EOL);

		responseBuilder.append("Content-Type: ");
		responseBuilder.append(page.getContentType());
		responseBuilder.append("; charset=UTF-8");

		if (usernameCookie != null) {
			responseBuilder.append(HttpServer.EOL);
			responseBuilder.append("Set-Cookie: ");
			responseBuilder.append(usernameCookie.toString());
		}

		responseBuilder.append(HttpServer.EOL);
		responseBuilder.append(HttpServer.EOL);

		return responseBuilder.toString();
	}

	/**
	 * @return The response's content in a byte array form.
	 */
	public byte[] getContent() {
		return page.getContent();
	}

}

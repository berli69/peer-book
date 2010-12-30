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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;

import uk.ac.stand.cs.brb5.impl.exceptions.MalformedHttpRequestException;
import uk.ac.stand.cs.brb5.impl.util.pageContent.HtmlUtils;
import uk.ac.stand.cs.brb5.interfaces.IRequestHandler;
import uk.ac.standrews.cs.nds.util.Diagnostic;
import uk.ac.standrews.cs.nds.util.DiagnosticLevel;

/**
 * 
 * Provides the functionality of a basic HTTP server. It is almost entirely 
 * abstracted away from PeerBook functionality and so could be used elsewhere.
 * 
 * @author Ben
 *
 */
public class HttpServer implements Runnable {

	/**
	 * Marks the end of a line in a HTTP request/response.
	 */
	public static final String EOL = "\r\n";

	private static final int DEFAULT_TIMEOUT = 100;
	private static final String GET_REQUEST = "GET";
	private static final String POST_REQUEST = "POST";

	private ServerSocket listeningSocket;
	private int serverPort;
	private boolean running;
	private IRequestHandler requestHandler;
	private HtmlUtils htmlUtils;

	/**
	 * @param handler IRequestHandler object used for handling Requests sent to this HTTP Server.
	 * @param port The port the Server is to be run on.
	 * @param htmlUtils Used to serve HTML.
	 * @throws IOException
	 */
	public HttpServer(IRequestHandler handler, int port, HtmlUtils htmlUtils) throws IOException {
		this.requestHandler = handler;
		this.htmlUtils = htmlUtils;

		serverPort = port;
		listeningSocket = new ServerSocket(serverPort);
		listeningSocket.setSoTimeout(DEFAULT_TIMEOUT);
	}

	/**
	 * Starts the server.
	 */
	public void start() {
		running = true;
		new Thread(this).start();

		Diagnostic.trace(DiagnosticLevel.FINAL, "HttpRequestListener started on port " + serverPort + ".");
	}

	/**
	 * Stops the server.
	 */
	public void stop() {
		running = false;

		Diagnostic.trace(DiagnosticLevel.FINAL, "HttpRequestListener stopped.");
	}

	/**
	 * Accepts incoming HTTP requests and responds to any that it accepts.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		while(running) {

			Socket incoming;
			InputStream in;
			DataOutputStream out;

			try {
				// Attempt to receive a request.
				incoming = listeningSocket.accept();
				in = new BufferedInputStream(incoming.getInputStream());
				out = new DataOutputStream(new BufferedOutputStream(incoming.getOutputStream()));
				incoming.getOutputStream();

				Response result = null;
				try {
					Request request = getRequestedResource(in);
					result = requestHandler.handleRequest(request);
				} catch (MalformedHttpRequestException e) {
					Diagnostic.trace(DiagnosticLevel.RUN, e);
					result = new Response(htmlUtils.exception(null, e));
				}

				// Send response back to client.
				String httpHeader = result.getHttpHeader();
				byte[] content = result.getContent();
				out.writeBytes(httpHeader);
				out.write(content);
				out.flush();
				Diagnostic.trace(DiagnosticLevel.RESULT, "Sent to output:\n" + httpHeader);

				in.close();
				out.close();
				incoming.close();
			} catch (IOException e) {
				Diagnostic.trace(DiagnosticLevel.INIT, "Failed to receive incoming connection", e);
			}
		}
	}

	private Request getRequestedResource(InputStream in) throws MalformedHttpRequestException, IOException {

		String headers = getHeaders(in);
		String content = getContent(headers, in);

		Diagnostic.trace(DiagnosticLevel.RESULT, "Received request: \n" + headers + content);

		String request = headers + content;
		String requestType = request.substring(0, request.indexOf(' '));

		if (requestType.equals(GET_REQUEST)) {
			return getRequestedResourceGET(request);
		} else if (requestType.equals(POST_REQUEST)) {
			return getRequestedResourcePOST(request);
		} else throw new MalformedHttpRequestException(request);
	}

	private String getHeaders(InputStream in) throws IOException {
		StringBuilder headerBuilder = new StringBuilder();

		boolean endOfHeaders = false;
		while (!endOfHeaders) {
			headerBuilder.append((char)in.read());
			// Two EOLs means that the end of the headers has been reached.
			if (headerBuilder.toString().contains(EOL + EOL)) {
				endOfHeaders = true;
			}
		}

		return headerBuilder.toString();
	}

	private String getContent(String headers, InputStream in) throws IOException {
		if (headers.contains("Content-Length")) {

			int startIndex = headers.indexOf("Content-Length") + 16;
			int endIndex = headers.indexOf(EOL, startIndex);
			int contentLength = Integer.parseInt(headers.substring(startIndex, endIndex));

			StringBuilder contentBuilder = new StringBuilder();
			for (int i = 0; i < contentLength; i++) {
				contentBuilder.append((char)in.read());
			}

			return contentBuilder.toString();
		} else {
			return "";
		}
	}

	private Request getRequestedResourceGET(String request) throws UnsupportedEncodingException {

		String decodedPageName = getRequestedPageName(request);
		String username = getUsernameFromCookie(request);

		if (decodedPageName.contains("?")) {
			// There are GET arguments in the request, so decode them.
			String requestedPage = decodedPageName.substring(0, decodedPageName.indexOf('?'));
			String arguments = decodedPageName.substring(decodedPageName.indexOf('?') + 1);

			return new Request(requestedPage, arguments, username);
		} else {
			return new Request(decodedPageName, "", username);
		}
	}

	private Request getRequestedResourcePOST(String request) throws UnsupportedEncodingException {

		String decodedPageName = getRequestedPageName(request);
		String username = getUsernameFromCookie(request);

		int argumentsIndex = request.indexOf(EOL + EOL) + (EOL + EOL).length();
		String decodedArguments = URLDecoder.decode(request.substring(argumentsIndex), "UTF-8");

		if (!decodedArguments.isEmpty()) {
			String arguments = decodedArguments.substring(decodedArguments.indexOf('?') + 1);

			return new Request(decodedPageName, arguments, username);
		} else {
			return new Request(decodedPageName, "", username);
		}
	}

	private String getRequestedPageName(String request) throws UnsupportedEncodingException {
		String firstLine = request.substring(0, request.indexOf(EOL));
		String pageNameString = firstLine.substring(firstLine.indexOf(' ') + 1, firstLine.indexOf(' ', firstLine.indexOf(' ') + 1));
		String decodedPageName = URLDecoder.decode(pageNameString, "UTF-8");
		return decodedPageName;
	}

	private String getUsernameFromCookie(String request) {

		if (request.contains("Cookie: ")) {

			int cookieStart = request.indexOf("Cookie: ") + "Cookie: ".length();
			int cookieEnd = request.indexOf(EOL, cookieStart);
			String cookieString = request.substring(cookieStart, cookieEnd);

			int usernameStart = cookieString.indexOf(ArgumentType.USERNAME.toString() + "=\"") + 
			(ArgumentType.USERNAME.toString() + "=\"").length();
			int usernameEnd = cookieString.indexOf('"', usernameStart);
			return cookieString.substring(usernameStart, usernameEnd);

		} else {
			return null;
		}
	}

}

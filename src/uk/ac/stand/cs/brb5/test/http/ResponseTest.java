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
package uk.ac.stand.cs.brb5.test.http;

import static org.junit.Assert.*;

import java.net.HttpCookie;

import org.junit.Test;

import uk.ac.stand.cs.brb5.impl.http.Response;
import uk.ac.stand.cs.brb5.impl.util.pageContent.TextContent;

public class ResponseTest {
	
	private static final String TEXT = "text";
	private static final TextContent TEXT_CONTENT = new TextContent(TEXT);
	

	@Test
	public void getHttpHeaderNoCookie() {
		Response response = new Response(TEXT_CONTENT);
		String httpHeader = response.getHttpHeader();

		assertTrue(httpHeader.contains("Content-Length: " + TEXT.length()));
	}
	
	@Test
	public void getHttpHeaderWithCookie() {
		HttpCookie cookie = new HttpCookie("name", "value");
		
		Response response = new Response(TEXT_CONTENT, cookie);
		String httpHeader = response.getHttpHeader();
		
		assertTrue(httpHeader.contains("Set-Cookie: " + cookie.toString()));
	}

}

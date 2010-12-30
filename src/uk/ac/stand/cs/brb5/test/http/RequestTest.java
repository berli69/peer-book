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

import org.junit.Test;

import uk.ac.stand.cs.brb5.impl.http.ArgumentType;
import uk.ac.stand.cs.brb5.impl.http.Request;

public class RequestTest {
	
	@Test
	public void testGetRequestedPage() {
		Request request = new Request("test", null, null);
		assertEquals("test", request.getRequestedPage());
	}
	
	@Test
	public void testGetArguments() {
		String arguments = ArgumentType.FRIENDNAME.toString() + "=bla&" + ArgumentType.MESSAGE.toString() + "=message";
		Request request = new Request(null, arguments, null);
		assertEquals("bla", request.getArguments().getArgument(ArgumentType.FRIENDNAME));
		assertEquals("message", request.getArguments().getArgument(ArgumentType.MESSAGE));
	}
	
	@Test
	public void testGetUsername() {
		Request request = new Request(null, null, "username");
		assertEquals("username", request.getUsername());
	}

}

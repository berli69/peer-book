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
package uk.ac.stand.cs.brb5.test.util;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import uk.ac.stand.cs.brb5.impl.util.CommandLineArguments;

public class CommandLineArgumentsTest {
	
	@Test
	public void testEmptyArguments() {
		CommandLineArguments args = new CommandLineArguments(new String[0]);
		
		assertEquals(0, args.getArguments().size());
		assertEquals(0, args.getFlags().size());
	}
	
	@Test
	public void testFlagsOnly() {
		String[] arguments = {"-a", "-b", "-c"};
		CommandLineArguments args = new CommandLineArguments(arguments);
		List<String> flags = args.getFlags();
		
		assertEquals(0, args.getArguments().size());
		assertEquals(3, flags.size());
		assertTrue(flags.contains("a"));
		assertTrue(flags.contains("b"));
		assertTrue(flags.contains("c"));
	}
	
	@Test
	public void testParametersOnly() {
		String[] params = {"-a", "something", "-b", "somethingElse", "-c", "aThirdThing"};
		CommandLineArguments args = new CommandLineArguments(params);
		Map<String, String> arguments = args.getArguments();
		
		assertEquals(0, args.getFlags().size());
		assertEquals(3, arguments.size());
		assertEquals("something", arguments.get("a"));
		assertEquals("somethingElse", arguments.get("b"));
		assertEquals("aThirdThing", arguments.get("c"));
	}
	
	@Test
	public void testMixture() {
		String[] params = {"-a", "-b", "something", "-c", "somethingElse", "-d"};
		CommandLineArguments args = new CommandLineArguments(params);
		Map<String, String> arguments = args.getArguments();
		List<String> flags = args.getFlags();
		
		assertEquals(2, arguments.size());
		assertEquals(2, flags.size());
		
		assertTrue(flags.contains("a"));
		assertTrue(flags.contains("d"));

		assertEquals("something", arguments.get("b"));
		assertEquals("somethingElse", arguments.get("c"));
	}

}

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
package uk.ac.stand.cs.brb5.impl.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Parses and contains any command line arguments passed to the program.
 * <br /><br />
 * Flags (i.e. those arguments that have no parameter) will be put into the
 * 'flags' list. Parameters (i.e. those arguments that have a parameter) will be
 * put into the 'arguments' list.
 * 
 * @author Ben
 *
 */
public class CommandLineArguments {

	private List<String> flags;
	private Map<String, String> arguments;

	/**
	 * @param argumentsArray The array of arguments that was passed to the program.
	 */
	public CommandLineArguments(String[] argumentsArray) {

		arguments = new HashMap<String, String>();
		flags = new ArrayList<String>();

		for (int i = 0; i < argumentsArray.length; i++) {
			if (argumentsArray[i].startsWith("-")) {

				String flag = argumentsArray[i].substring(1);

				if (i + 1 < argumentsArray.length && !argumentsArray[i + 1].startsWith("-")) {

					String argument = argumentsArray[i + 1];
					arguments.put(flag, argument);
					i++;

				} else {
					flags.add(flag);
				}
			}
		}
	}

	/**
	 * @return Any command-line flags passed to the program.
	 */
	public List<String> getFlags() {
		return this.flags;
	}

	/**
	 * @return A mapping from command-line flags to the values of those flags.
	 * A flag will have had its leading '-' stripped.
	 */
	public Map<String, String> getArguments() {
		return this.arguments;
	}

}

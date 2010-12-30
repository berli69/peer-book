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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.stand.cs.brb5.impl.kernel.ProfileField;

/**
 * Models the various HTTP arguments that may be obtained from a GET or POST request.
 * ArgumentType is used for the various types of arguments that PeerBook requires, and
 * ProfileField is used for the various fields that make up a profile update request.
 * 
 * @author Ben
 *
 */
public class Arguments {

	private static List<String> TYPE_STRINGS = new ArrayList<String>();
	private static List<String> PROFILE_FIELD_STRINGS = new ArrayList<String>();

	static {
		for (ArgumentType type : ArgumentType.values()) {
			TYPE_STRINGS.add(type.toString());
		}

		for (ProfileField profileField : ProfileField.values()) {
			PROFILE_FIELD_STRINGS.add(profileField.toString());
		}		
	}

	public static final String PROFILE_UPDATED = "PROFILE_UPDATED";

	private Map<ArgumentType, String> argumentsMap;
	private Map<ProfileField, String> updatedFields;
	private boolean wasUpdated;

	/**
	 * @param args A String containing argument name/value pairs, with each pair separated 
	 * by '&' and a '=' symbol separating the name from the value in a pair.
	 */
	public Arguments(String args) {
		this.argumentsMap = new HashMap<ArgumentType, String>();
		this.updatedFields = new HashMap<ProfileField, String>();
		this.wasUpdated = false;

		String[] argumentPairs = args.split("&");

		for (String argumentPair : argumentPairs) {
			String[] splitPair = argumentPair.split("=");

			// Deals with emtpy arguments
			if (splitPair.length != 2) {
				String[] tempSplitPair = new String[2];
				tempSplitPair[0] = splitPair[0];
				tempSplitPair[1] = "";
				splitPair = tempSplitPair;
			}

			splitPair[1] = splitPair[1].replace("<", "&lt;");
			splitPair[1] = splitPair[1].replace(">", "&gt;");

			if (!splitPair[0].equals("")) {
				if (splitPair[0].equals(PROFILE_UPDATED)) {
					wasUpdated = true;
				} else {
					if (TYPE_STRINGS.contains(splitPair[0])) {
						argumentsMap.put(ArgumentType.valueOf(splitPair[0]), splitPair[1]);
					} else if (PROFILE_FIELD_STRINGS.contains(splitPair[0])) {
						updatedFields.put(ProfileField.valueOf(splitPair[0]), splitPair[1]);
					}
				}
			}
		}
	}

	/**
	 * @param type The type of the argument to retrieve.
	 * @return Value that was set for the given argument type, null if none given.
	 */
	public String getArgument(ArgumentType type) {
		return argumentsMap.get(type);
	}

	/**
	 * @return True or false indicating whether a user's profile was updated (and whether we 
	 * need to look at the updated fields).
	 */
	public boolean profileWasUpdated() {
		return wasUpdated;
	}

	/**
	 * @return Mapping of any updated fields sent in the HTTP request.
	 */
	public Map<ProfileField, String> getUpdatedFields() {
		return updatedFields;
	}

}

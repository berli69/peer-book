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
import uk.ac.stand.cs.brb5.impl.http.Arguments;
import uk.ac.stand.cs.brb5.impl.kernel.ProfileField;

public class ArgumentsTest {

	@Test
	public void testArguments1() {
		String message = "blabla";
		String recipient = "someone";
		String emailAddress0 = "bla@bla.com";

		String args = ArgumentType.MESSAGE.toString() + "=" + message + "&" + ArgumentType.RECIPIENT.toString() +
		"=" + recipient + "&" + ProfileField.EMAIL_ADDRESS_0 + "=" + emailAddress0;

		Arguments arguments = new Arguments(args);

		assertEquals(message, arguments.getArgument(ArgumentType.MESSAGE));
		assertEquals(recipient, arguments.getArgument(ArgumentType.RECIPIENT));
		assertEquals(emailAddress0, arguments.getUpdatedFields().get(ProfileField.EMAIL_ADDRESS_0));

		assertFalse(arguments.profileWasUpdated());

		assertNull(arguments.getArgument(ArgumentType.PASSWORD));
		assertNull(arguments.getArgument(ArgumentType.WALLPOST_NUMBER));

		assertNull(arguments.getUpdatedFields().get(ProfileField.COURSE));
		assertNull(arguments.getUpdatedFields().get(ProfileField.DATE_OF_BIRTH));
	}

	@Test public void testBadArgumentName() {
		String args = "badargument=something";

		Arguments arguments = new Arguments(args);

		for (ArgumentType argType : ArgumentType.values()) {
			assertNull(arguments.getArgument(argType));
		}

		for (ProfileField field : ProfileField.values()) {
			assertNull(arguments.getUpdatedFields().get(field));
		}
	}

	@Test
	public void testHtmlInjection() {
		String username = "<bl>ax<>";
		String fixedUsername = username.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		String args = ArgumentType.USERNAME.toString() + "=" + username;

		Arguments arguments = new Arguments(args);

		assertEquals(fixedUsername, arguments.getArgument(ArgumentType.USERNAME));
	}

}

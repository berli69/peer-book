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
package uk.ac.stand.cs.brb5.test.kernel;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.PrivateProfileDocument;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.PublicProfileDocument;
import uk.ac.stand.cs.brb5.impl.kernel.LoggedInUserInfo;

public class LoggedInUserTest {
	
	@Test
	public void testGetPassword() {
		String password = "password";
		LoggedInUserInfo userInfo = new LoggedInUserInfo(password);
		
		assertEquals(password, userInfo.getPassword());
	}
	
	@Test
	public void testSetAndGetPublicProfileDoc() {
		LoggedInUserInfo userInfo = new LoggedInUserInfo(null);
		PublicProfileDocument publicProfileDoc = PublicProfileDocument.Factory.newInstance();
		assertEquals(publicProfileDoc, userInfo.getPublicProfileDocument());
	}
	
	@Test
	public void testSetAndGetPrivateProfileDoc() {
		LoggedInUserInfo userInfo = new LoggedInUserInfo(null);
		PrivateProfileDocument privateProfileDoc = PrivateProfileDocument.Factory.newInstance();
		assertEquals(privateProfileDoc, userInfo.getPrivateProfileDocument());
	}

}

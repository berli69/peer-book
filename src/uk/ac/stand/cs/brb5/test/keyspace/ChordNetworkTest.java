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
package uk.ac.stand.cs.brb5.test.keyspace;

import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.junit.Test;

import uk.ac.standrews.cs.nds.p2p.exceptions.P2PNodeException;
import uk.ac.standrews.cs.stachordRMI.impl.ChordNodeImpl;
import uk.ac.standrews.cs.stachordRMI.interfaces.IChordNode;

public class ChordNetworkTest {

	/**
	 * @throws UnknownHostException 
	 * @throws P2PNodeException 
	 * @throws RemoteException 
	 * @throws NotBoundException 
	 */
	@Test
	public void testCreateNetwork() throws UnknownHostException, P2PNodeException, RemoteException, NotBoundException {
		
		InetAddress localAddress = InetAddress.getLocalHost();
		InetSocketAddress address1 = new InetSocketAddress(localAddress, 10000);
		InetSocketAddress address2 = new InetSocketAddress(localAddress, 10001);
		
		IChordNode node1 = new ChordNodeImpl(address1, null);
		IChordNode node2 = new ChordNodeImpl(address2, address1);
		
		assertEquals(node2.lookup(node1.getKey()).getKey(), node1.getKey());
	}

}

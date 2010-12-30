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
package uk.ac.stand.cs.brb5.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import uk.ac.stand.cs.brb5.impl.exceptions.FileAccessException;
import uk.ac.stand.cs.brb5.impl.factories.EncryptionManagerFactory;
import uk.ac.stand.cs.brb5.impl.storage.PublicKeyDatabase;
import uk.ac.stand.cs.brb5.impl.util.CommandLineArguments;
import uk.ac.stand.cs.brb5.interfaces.IPublicKeyDatabase;
import uk.ac.standrews.cs.nds.util.Diagnostic;
import uk.ac.standrews.cs.nds.util.DiagnosticLevel;

public class PublicKeyDatabaseServer implements Runnable {

	private boolean running;

	public PublicKeyDatabaseServer(int port) throws AccessException, RemoteException, AlreadyBoundException, IOException, FileAccessException {

		Registry registry = LocateRegistry.createRegistry(port);
		Diagnostic.trace(DiagnosticLevel.FINAL, "RMI started on " + InetAddress.getLocalHost().toString() + ":" + port);

		registry.bind(IPublicKeyDatabase.BOUND_NAME, new PublicKeyDatabase(new EncryptionManagerFactory()));
	}

	/**
	 * @param args
	 * @throws FileAccessException 
	 * @throws IOException 
	 * @throws AlreadyBoundException 
	 */
	public static void main(String[] args) throws AlreadyBoundException, IOException, FileAccessException {

		Diagnostic.setLevel(DiagnosticLevel.RESULT);

		CommandLineArguments arguments = new CommandLineArguments(args);
		int port = Integer.parseInt(arguments.getArguments().get("p"));
		PublicKeyDatabaseServer server = new PublicKeyDatabaseServer(port);
		server.setRunning(true);
		new Thread(server).start();

	}

	public void run() {
		while (running) {

		}
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}

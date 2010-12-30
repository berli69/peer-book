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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.util.Observable;

import uk.ac.stand.cs.brb5.impl.factories.ByzantineFaultTolerantPolicyFactory;
import uk.ac.stand.cs.brb5.impl.factories.EncryptionManagerFactory;
import uk.ac.stand.cs.brb5.impl.http.HttpServer;
import uk.ac.stand.cs.brb5.impl.http.PeerBookClientRequestHandler;
import uk.ac.stand.cs.brb5.impl.kernel.PeerBookKernel;
import uk.ac.stand.cs.brb5.impl.storage.PublicKeyDatabase;
import uk.ac.stand.cs.brb5.impl.storage.StorageService;
import uk.ac.stand.cs.brb5.impl.util.CommandLineArguments;
import uk.ac.stand.cs.brb5.impl.util.CompressionManager;
import uk.ac.stand.cs.brb5.impl.util.pageContent.HtmlUtils;
import uk.ac.stand.cs.brb5.interfaces.IChosenServerPolicyFactory;
import uk.ac.stand.cs.brb5.interfaces.IEncryptionManagerFactory;
import uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel;
import uk.ac.stand.cs.brb5.interfaces.IPublicKeyDatabase;
import uk.ac.stand.cs.brb5.interfaces.IRequestHandler;
import uk.ac.stand.cs.brb5.interfaces.IStorageService;
import uk.ac.standrews.cs.nds.p2p.util.SHA1KeyFactory;
import uk.ac.standrews.cs.nds.util.Diagnostic;
import uk.ac.standrews.cs.nds.util.DiagnosticLevel;
import uk.ac.standrews.cs.stachordRMI.impl.ChordNodeImpl;
import uk.ac.standrews.cs.stachordRMI.interfaces.IChordNode;

/**
 * 
 * The PeerBook client.
 * 
 * @author Ben
 *
 */
public class PeerBookClient {

	public static final String RMI_POLICY_FILENAME = "rmiPolicy";
	public static final DiagnosticLevel DIAGNOSTIC_LEVEL = DiagnosticLevel.RESULT;

	/**
	 * PeerBook takes 0-4 arguments:
	 * 
	 * <ul>
	 * <li>-s &lt;IP_address&gt;:&lt;port&gt; - Connection details for a node already in a PeerBook network.</li>
	 * <li>-p &lt;local port&gt; - Local port to run RMI on (default 55059).</li>
	 * <li>-b &lt;browser port&gt; - Local port to run the web server on (default 7654).</li>
	 * <li>-pubp &lt;IP_address&gt;:&lt;port&gt; - Connection details for a centralised Public Key database server (if one is being used).</li>
	 * </ul>
	 * 
	 * @param args Program arguments.
	 */
	public static void main(String[] args) {
		
		Diagnostic.setLevel(DIAGNOSTIC_LEVEL);

		PeerBookArguments arguments = new PeerBookArguments(args);

		try {
			System.setProperty("java.security.policy", RMI_POLICY_FILENAME);
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new RMISecurityManager());
			}

			InetSocketAddress localAddress = new InetSocketAddress(InetAddress.getLocalHost(), arguments.getRmiPort());
			IChordNode chordNode = new ChordNodeImpl(localAddress, arguments.getRemoteAddress(), new SHA1KeyFactory().generateKey(localAddress), DIAGNOSTIC_LEVEL);

			Diagnostic.trace(DiagnosticLevel.FINAL, "RMI started on port " + arguments.getRmiPort() + ".");

			IEncryptionManagerFactory encryptionManagerFac = new EncryptionManagerFactory();

			boolean centralisedPublicKeyDb = false;
			IPublicKeyDatabase publicKeyDatabase = null;
			if (arguments.getPublicKeyDatabaseAddress() != null) {
				InetAddress remoteDatabaseAddress = arguments.getPublicKeyDatabaseAddress().getAddress();
				int remoteDatabasePort = arguments.getPublicKeyDatabaseAddress().getPort();
				publicKeyDatabase = (IPublicKeyDatabase) LocateRegistry.getRegistry(remoteDatabaseAddress.getHostAddress(), remoteDatabasePort).lookup(IPublicKeyDatabase.BOUND_NAME);
				centralisedPublicKeyDb = true;
			} else {
				publicKeyDatabase = new PublicKeyDatabase(encryptionManagerFac);
			}

			int replicaNumber = 4;
			IChosenServerPolicyFactory chosenServerPolicyFac = new ByzantineFaultTolerantPolicyFactory(replicaNumber);
			IStorageService storageService = new StorageService(arguments.getRmiPort(), chordNode, chosenServerPolicyFac, replicaNumber, publicKeyDatabase, centralisedPublicKeyDb);
			((Observable) chordNode).addObserver(storageService);

			IPeerBookKernel kernel = new PeerBookKernel(encryptionManagerFac, new CompressionManager(), storageService);
			HtmlUtils htmlUtils = new HtmlUtils(kernel);
			IRequestHandler requestHandler = new PeerBookClientRequestHandler(kernel, htmlUtils);
			HttpServer server = new HttpServer(requestHandler, arguments.getBrowserPort(), htmlUtils);
			server.start();

		} catch (Exception e) {
			Diagnostic.trace(DiagnosticLevel.FINAL, "Problem starting PeerBook client.", e);
		}
	}	

	private static final class PeerBookArguments {

		private static final int DEFAULT_RMI_PORT = 55059;
		private static final int DEFAULT_BROWSER_PORT = 7654;

		private InetSocketAddress serverAddress;
		private int rmiPort;
		private int browserPort;
		private InetSocketAddress publicKeyDatabaseAddress;

		public PeerBookArguments(String[] args) {

			CommandLineArguments arguments = new CommandLineArguments(args);

			String serverAddressString = arguments.getArguments().get("s");
			String rmiPortString = arguments.getArguments().get("p");
			String browserPortString = arguments.getArguments().get("b");
			String publicKeyDbServerString = arguments.getArguments().get("pubp");

			try {

				if (serverAddressString != null) {
					this.serverAddress = parseAddress(serverAddressString);
				}

				if (rmiPortString != null) {
					rmiPort = Integer.parseInt(rmiPortString);
				} else {
					rmiPort = DEFAULT_RMI_PORT;
				}

				if (browserPortString != null) {
					browserPort = Integer.parseInt(browserPortString);
				} else {
					browserPort = DEFAULT_BROWSER_PORT;
				}

				if (publicKeyDbServerString != null) {
					this.publicKeyDatabaseAddress = parseAddress(publicKeyDbServerString);
				}

			} catch (Exception e) {
				Diagnostic.trace(DiagnosticLevel.RESULT, e);
				usage();
			}

		}

		public InetSocketAddress getRemoteAddress() {
			return serverAddress;
		}

		public int getRmiPort() {
			return rmiPort;
		}

		public int getBrowserPort() {
			return browserPort;
		}

		public InetSocketAddress getPublicKeyDatabaseAddress() {
			return publicKeyDatabaseAddress;
		}

		private static void usage() {
			Diagnostic.trace(DiagnosticLevel.NONE, "Usage: \"java PeerBookClient [-s <IP_address>:<port>] [-p <local port>] " +
			"[-b <browser port>] [-pubp <IP_address>:<port>]\"");
			System.exit(0);
		}

		private static InetSocketAddress parseAddress(String addressAndPort) throws UnknownHostException {
			String[] serverAddressParts = addressAndPort.split(":");
			InetAddress serverAddress = InetAddress.getByName(serverAddressParts[0]);
			int serverPort = Integer.parseInt(serverAddressParts[1]);
			return new InetSocketAddress(serverAddress, serverPort);
		}
	}

}

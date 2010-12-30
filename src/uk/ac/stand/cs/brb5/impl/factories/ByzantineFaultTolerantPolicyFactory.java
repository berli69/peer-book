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
package uk.ac.stand.cs.brb5.impl.factories;

import uk.ac.stand.cs.brb5.impl.policies.ByzantineFaultTolerantServerPolicy;
import uk.ac.stand.cs.brb5.interfaces.IChosenServerPolicy;
import uk.ac.stand.cs.brb5.interfaces.IChosenServerPolicyFactory;

/**
 * 
 * Constructs new {@link ByzantineFaultTolerantServerPolicy}s.
 * 
 * @author Ben
 *
 */
public class ByzantineFaultTolerantPolicyFactory implements IChosenServerPolicyFactory {

	private int replicaNumber;

	/**
	 * @param replicaNumber The size of replica sets in this PeerBook network.
	 */
	public ByzantineFaultTolerantPolicyFactory(int replicaNumber) {
		this.replicaNumber = replicaNumber;
	}

	/**
	 * @see uk.ac.stand.cs.brb5.interfaces.IChosenServerPolicyFactory#getPolicy()
	 */
	public IChosenServerPolicy getPolicy() {
		return new ByzantineFaultTolerantServerPolicy(replicaNumber);
	}

}

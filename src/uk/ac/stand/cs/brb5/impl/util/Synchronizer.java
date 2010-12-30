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

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Utility class used to keep track of Objects to synchronize on.
 * 
 * @author Ben
 *
 * @param <E> The type of objects for which synchronizable Objects will be stored.
 */
public class Synchronizer<E> {
	
	private Map<E, Object> synchronizers;
	
	public Synchronizer() {
		this.synchronizers = new HashMap<E, Object>();
	}
	
	/**
	 * @param key The object for which we are trying to fetch the synchronizable Object.
	 * @return An object which will be stored for the given key (and this Object will be returned for the given key forever).
	 */
	public Object getSynchronizingObject(E key) {
		Object synchronizer = synchronizers.get(key);
		
		if (synchronizer == null) {
			synchronizer = new Object();
			synchronizers.put(key, synchronizer);
		}
		
		return synchronizer;
	}

}

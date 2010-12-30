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
import java.util.List;
import java.util.Map;

/**
 * 
 * Utility class that choose out of a list of elements whichever element in the list appears
 * more than a threshold number.
 * 
 * @author Ben
 *
 */
public class ThresholdChooser<E> {

	/**
	 * Returns the first element in the list such that the number of other
	 * elements that are equal to the returned element is equal to or greater than
	 * the threshold.
	 * <br />
	 * <br />
	 * WARNING: Do not pass arrays in to this (as elements in the List), as Java array equality
	 * does not compare the contents of the array.
	 * 
	 * @param elements A list of elements to be compared.
	 * @param threshold The threshold number.
	 * @return The first element in the List that appears greater than the given threshold.
	 */	
	public E findFirstAboveThreshold(List<E> elements, int threshold) {
		Map<E, Integer> counter = new HashMap<E, Integer>();

		for (E element : elements) {

			Integer newCount = counter.get(element);
			if (newCount == null) {
				newCount = 1;
			} else {
				newCount++;
			}

			if (newCount >= threshold) {
				return element;
			}

			counter.put(element, newCount);
		}

		return null;
	}


}

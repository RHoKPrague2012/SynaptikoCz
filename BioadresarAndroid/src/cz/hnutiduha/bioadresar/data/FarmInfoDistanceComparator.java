/*  This file is part of BioAdresar.
	Copyright 2012 Jiri Zouhar (zouhar@trilobajt.cz), Jiri Prokop

    BioAdresar is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BioAdresar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with BioAdresar.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.hnutiduha.bioadresar.data;

import java.util.Comparator;

import android.location.Location;

public class FarmInfoDistanceComparator implements Comparator<FarmInfo> {

	private Location targetLocation;
	
	public FarmInfoDistanceComparator(Location targetLocation) {
		this.targetLocation = targetLocation;
	}
	
	public int compare(FarmInfo fi1, FarmInfo fi2) {
		float dist1 = fi1.getDistance(targetLocation);
		float dist2 = fi2.getDistance(targetLocation);
		
		return Math.round(Math.signum((dist1 - dist2)));
	}

}

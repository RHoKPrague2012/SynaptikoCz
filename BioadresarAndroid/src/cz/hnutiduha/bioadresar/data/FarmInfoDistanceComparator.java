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

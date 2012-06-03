package cz.hnutiduha.bioadresar.data;

import java.util.List;

import android.location.Location;

import com.google.android.maps.GeoPoint;

public class FarmInfo {
	// these are always present
	public long id;
	public String name;
	public double lat, lon;
	public List<Long> categories;
	
	// call DatabaseHelper.fillDetails to obtain these
	public String description;
	public String type;
	public FarmContact contact;
	public List<Long> products;
	
	private Location location = null;
	
	public static GeoPoint getGeoPoint(FarmInfo farm) {
		return new GeoPoint((int)(farm.lat * 1E6), (int)(farm.lon * 1E6));
	}
	
	public float getDistance(Location targetLocation) {
		Location destLocation = getLocation();
		return targetLocation.distanceTo(destLocation);
	}
	
	public Location getLocation() {
		if (location == null) {
			location = new Location("");
			location.setLatitude(lat);
			location.setLongitude(lon);
		}
		
		return location;
	}

}

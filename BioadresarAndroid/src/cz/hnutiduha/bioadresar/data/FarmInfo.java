package cz.hnutiduha.bioadresar.data;

import java.util.List;

import com.google.android.maps.GeoPoint;

public class FarmInfo {
	// these are always present
	public long id;
	public String name;
	public double lat, lon;
	
	// call DatabaseHelper.fillDetails to obtain these
	public String description;
	public String type;
	public FarmContact contact;
	public List<String> products;
	public List<String> categories;
	
	public static GeoPoint getGeoPoint(FarmInfo farm)
	{
		return new GeoPoint((int)(farm.lat * 1E6), (int)(farm.lon * 1E6));
	}
		
	public boolean isInDistance(double targetLat, double targetLon, int distanceInKm) {
		return false;
	}
	
	public float getDistanceInKm(double targetLat, double targetLon) {
		return 0;
	}

}

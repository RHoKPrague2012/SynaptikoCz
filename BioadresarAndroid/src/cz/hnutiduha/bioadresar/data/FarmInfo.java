package cz.hnutiduha.bioadresar.data;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.maps.GeoPoint;

import cz.hnutiduha.bioadresar.detail.DetailActivity;

public class FarmInfo implements OnClickListener{
	
	public static long INVALID_FARM_ID = -1;
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

	// this is little hack, it doesn't belong to data class, but it serve us nicely :)
	@Override
	public void onClick(View v) {
		DatabaseHelper helpMeeeFillMeee = DatabaseHelper.getDefaultDb();
		helpMeeeFillMeee.fillDetails(this);
		Context context = v.getContext();
		DetailActivity.setFarm(this);
		Intent detail = new Intent(context, DetailActivity.class);
		context.startActivity(detail);
	}

}

package cz.hnutiduha.bioadresar.data;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class LocationCache {
	private static Location location = null;
	
	public static Location getCurrentLocation(Context context)
	{
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		String provider;
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			provider = LocationManager.GPS_PROVIDER;
		}
		else
		{
			Criteria criteria = new Criteria();
			criteria.setPowerRequirement(Criteria.POWER_HIGH);
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			provider = locationManager.getBestProvider(criteria, true);
		}
		
		Location currentLocation = locationManager.getLastKnownLocation(provider);
		if (currentLocation == null)
		{
			Log.w("gps", "Location not available");
		    if ((0 == (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)))
				return null;
		    
		    Log.d("gps", "faking location...");
		    currentLocation = new Location(LocationManager.GPS_PROVIDER);
			currentLocation.setLatitude(49);
			currentLocation.setLongitude(16);
			currentLocation.setTime(System.currentTimeMillis());
				
		    
		}
		return currentLocation;
	}
	
	public static void centerOnGps(Context context)
	{
		location = getCurrentLocation(context);
	}
	
	public static void centerOnLocation(Location loc)
	{
		LocationCache.location = loc;
	}
		
	public static Location getCenter()
	{
		return location;
	}

	

}

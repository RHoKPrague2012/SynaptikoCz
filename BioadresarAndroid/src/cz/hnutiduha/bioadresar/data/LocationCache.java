/*  This file is part of BioAdresar.
	Copyright 2012 Jiri Zouhar (zouhar@trilobajt.cz)

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

package cz.hnutiduha.bioadresar;

import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.AttributeSet;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.readystatesoftware.maps.TapControlledMapView;

import cz.hnutiduha.bioadresar.data.DatabaseHelper;
import cz.hnutiduha.bioadresar.data.FarmInfo;

public class FarmMapView extends TapControlledMapView {
	FarmsOverlay farmOverlay;
	GeoPoint currentVisibleRectangle[];
	boolean currentDrawn = false;
	int currentZoomLevel = -1;
	

	public FarmMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
        List<Overlay> mapOverlays = this.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.marker2);
        farmOverlay = new FarmsOverlay(drawable, this);
        mapOverlays.add(farmOverlay);
	}
	
	public void dispatchDraw(Canvas canvas)
	{
		super.dispatchDraw(canvas);
		
		// TODO: don't refresh poinds on zoom-in
		
		// don't refresh on each zoom change. there may be dozen of them during one zoom
		int lastZoomLevel = currentZoomLevel;
		currentZoomLevel = getZoomLevel();
		if (currentZoomLevel != lastZoomLevel)
			return;
		
		GeoPoint lastVisibleRectangle[] = currentVisibleRectangle;
		currentVisibleRectangle = getVisibleRectangle();
		// don't refresh on every pan, wait the position to stabilize
		if (lastVisibleRectangle != null && 
				lastVisibleRectangle[0].equals(currentVisibleRectangle[0]) &&
				lastVisibleRectangle[1].equals(currentVisibleRectangle[1]))
		{
			if (currentDrawn == false)
			{
				refreshPoints();
				currentDrawn = true;
			}
		}
		else
		{
			currentDrawn = false;
		}
	}
	
	protected GeoPoint[] getVisibleRectangle()
    {
    	GeoPoint res[] = new GeoPoint[2];
    	res[0] = getProjection().fromPixels(0, 0);
    	res[1] = getProjection().fromPixels(getWidth(), getHeight());
    	
    	return res;
    }
	
	public void centerOnGeoPoint(GeoPoint center)
	{
		getController().animateTo(center, new Runnable() {
			@Override
			public void run() {
				refreshPoints();
			}
			
		});
	}
	
	public void centerOnCurrentLocation()
	{
		LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
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
		    if ((0 == (getContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)))
				return;
		    
		    Log.d("gps", "faking location...");
		    currentLocation = new Location(LocationManager.GPS_PROVIDER);
			currentLocation.setLatitude(49);
			currentLocation.setLongitude(16);
			currentLocation.setTime(System.currentTimeMillis());
				
		    
		}
		
		centerOnGeoPoint(new GeoPoint((int)(currentLocation.getLatitude() * 1E6), (int)(currentLocation.getLongitude() * 1E6)));
	}
	
	private void refreshPoints()
	{
		DatabaseHelper db = DatabaseHelper.getDefaultDb();
		if (db == null)
		{
			Log.e("db", "Fatal, can't get default db");
			return;
		}
		
		Log.d("gui", "visible area is " + currentVisibleRectangle[0].toString() + " x " + currentVisibleRectangle[1].toString());
		
		Hashtable <Long, FarmInfo> farms = db.getFarmsInRectangle(
				currentVisibleRectangle[0].getLatitudeE6() / 1E6, currentVisibleRectangle[0].getLongitudeE6() / 1E6,
				currentVisibleRectangle[1].getLatitudeE6() / 1E6, currentVisibleRectangle[1].getLongitudeE6() / 1E6);
		farmOverlay.setVisiblePoints(farms);
	}


}

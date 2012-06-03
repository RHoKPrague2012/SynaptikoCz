package cz.duha.bioadresar;

import java.util.Hashtable;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import cz.duha.bioadresar.data.DatabaseHelper;
import cz.duha.bioadresar.data.FarmInfo;

import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class MapActivity extends com.google.android.maps.MapActivity {
	private MapView mapView;
	FarmsOverlay farmOverlay;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.ic_map_marker);
        farmOverlay = new FarmsOverlay(drawable, this);
        mapOverlays.add(farmOverlay);
        
        centerOnCurrentLocation();
        mapView.getController().setZoom(10);
        
        refreshPoints();
    }
    
    protected GeoPoint[] getVisibleRectangle()
    {
    	GeoPoint res[] = new GeoPoint[2];
    	// TODO: does this work well with rotation?
    	res[0] = mapView.getProjection().fromPixels(0, 0);
    	res[1] = mapView.getProjection().fromPixels(mapView.getWidth(), mapView.getHeight());
    	
    	return res;
    }

	@Override
	protected boolean isRouteDisplayed() {
		// NOTE: we don't have time to research this topic :)
		return false;
	}
	
	private void centerOnGeoPoint(GeoPoint center)
	{
		MapController control = mapView.getController();
		control.animateTo(center);
	}
	
	private void centerOnCurrentLocation()
	{
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
			return;
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
		
		GeoPoint [] screen = this.getVisibleRectangle();
		Log.d("gui", "visible area is " + screen[0].toString() + " x " + screen[1].toString());
		
		Hashtable <Long, FarmInfo> farms = db.getFarmsInRectangle(
				screen[0].getLatitudeE6() / 1E6, screen[0].getLongitudeE6() / 1E6,
				screen[1].getLatitudeE6() / 1E6, screen[1].getLongitudeE6() / 1E6);
		this.farmOverlay.setVisiblePoints(farms);
	}
}

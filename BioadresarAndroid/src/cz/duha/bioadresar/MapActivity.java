package cz.duha.bioadresar;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

public class MapActivity extends com.google.android.maps.MapActivity {
	private MapView mapView;
	FarmsOverlay farms;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.logo);
        farms = new FarmsOverlay(drawable, this);
        mapOverlays.add(farms);
        
        centerOnCurrentLocation();
        mapView.getController().setZoom(15);
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
		OverlayItem overlayitem = new OverlayItem(center, "Whoa!", "I'm here!");
		if (farms != null)
			farms.addOverlay(overlayitem);
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
			criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
			provider = locationManager.getBestProvider(criteria, true);
		}
		
		Location currentLocation = locationManager.getLastKnownLocation(provider);
		if (currentLocation == null)
		{
			// TODO: report location not avail
			return;
		}
		
		centerOnGeoPoint(new GeoPoint((int)(currentLocation.getLatitude() * 1E6), (int)(currentLocation.getLongitude() * 1E6)));
	}
	
	

}

package cz.hnutiduha.bioadresar;

import android.os.Bundle;

public class MapActivity extends com.google.android.maps.MapActivity {
	private FarmMapView mapView;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        mapView = (FarmMapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        mapView.centerOnCurrentLocation();
        mapView.getController().setZoom(10);
    }
    
	@Override
	protected boolean isRouteDisplayed() {
		// NOTE: we don't have time to research this topic :)
		return false;
	}
}

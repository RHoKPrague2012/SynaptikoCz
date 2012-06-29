package cz.hnutiduha.bioadresar.map;

import android.os.Bundle;
import cz.hnutiduha.bioadresar.R;
import cz.hnutiduha.bioadresar.data.FarmInfo;

public class MapActivity extends com.google.android.maps.MapActivity {
	
	public static String mapNodePropertyName = "farmIdToShow";
	
	private FarmMapView mapView;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);
        
        mapView = (FarmMapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        mapView.centerMap();
        mapView.getController().setZoom(10);
        
        Long targetFarmId = this.getParent().getIntent().getLongExtra(mapNodePropertyName, FarmInfo.INVALID_FARM_ID);
        mapView.showFarmBalloonOnStart(targetFarmId.longValue());
    }
    
	@Override
	protected boolean isRouteDisplayed() {
		// NOTE: we don't have time to research this topic :)
		return false;
	}
}

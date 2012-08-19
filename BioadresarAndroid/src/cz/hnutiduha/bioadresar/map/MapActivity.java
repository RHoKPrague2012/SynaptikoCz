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

package cz.hnutiduha.bioadresar.map;

import android.app.Activity;
import android.os.Bundle;
import cz.hnutiduha.bioadresar.R;
import cz.hnutiduha.bioadresar.data.FarmInfo;

public class MapActivity extends com.google.android.maps.MapActivity {
	
	public static final String mapNodePropertyName = "farmIdToShow";
	
	private FarmMapView mapView;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);
        
        mapView = (FarmMapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        
        mapView.centerMap();
        mapView.getController().setZoom(11);
        
        Activity parent = this.getParent();
        if (parent == null)
        	parent = this;
        Long targetFarmId = parent.getIntent().getLongExtra(mapNodePropertyName, FarmInfo.INVALID_FARM_ID);
        mapView.showFarmBalloonOnStart(targetFarmId.longValue());
    }
    
	@Override
	protected boolean isRouteDisplayed() {
		// NOTE: we don't have time to research this topic :)
		return false;
	}
}

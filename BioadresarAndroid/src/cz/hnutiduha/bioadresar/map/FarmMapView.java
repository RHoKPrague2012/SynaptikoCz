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

import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import cz.hnutiduha.bioadresar.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.readystatesoftware.maps.TapControlledMapView;

import cz.hnutiduha.bioadresar.data.DatabaseHelper;
import cz.hnutiduha.bioadresar.data.FarmInfo;
import cz.hnutiduha.bioadresar.data.LocationCache;

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
        
		setOnSingleTapListener(farmOverlay);
	}
	
	public void dispatchDraw(Canvas canvas)
	{
		super.dispatchDraw(canvas);
		
		// TODO: don't refresh points on zoom-in
		
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
				// wtf. the map generate one aux click through movement
				farmOverlay.enableHiding();
			}	
		});
	}
	
	// centers on current location (from gps/cellular)
	public void centerMap()
	{
		Location center = LocationCache.getCenter();
		
		centerOnGeoPoint(new GeoPoint((int)(center.getLatitude() * 1E6), (int)(center.getLongitude() * 1E6)));
	}
	
	public void showFarmBalloonOnStart(long farmId)
	{
		farmOverlay.showFarmBalloonOnStart(farmId);
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

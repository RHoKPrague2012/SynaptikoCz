package cz.hnutiduha.bioadresar.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.maps.OnSingleTapListener;

import cz.hnutiduha.bioadresar.data.FarmInfo;

public class FarmsOverlay extends ItemizedOverlay<OverlayItem> implements OnSingleTapListener{
	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
	private FarmOverlayItem lastSelected = null;
	private FarmMapView map;
	private boolean isPinch = false;
	private long firstBalloon = FarmInfo.INVALID_FARM_ID;
	
	public FarmsOverlay(Drawable defaultMarker, FarmMapView map) {
		super(boundCenter(defaultMarker));
		this.map = map;
		populate();
	}

	public void addOverlay(OverlayItem overlay) {
	    overlays.add(overlay);
	    populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}
	
	public void hideBalloon()
	{
		if (lastSelected != null)
			lastSelected.hideBalloon();
	}
	
	public void showFarmBalloonOnStart(long farmId)
	{
		Log.d("debug", "setting next farm to " + farmId);
		firstBalloon = farmId;
	}
		
	@Override
	protected boolean onTap(int index) {
		// don't show detail on pinch
		if (isPinch)
			return false;
		
		// hide old balloon
		/* TODO: maybe we could use(reuse) only one static balloon to save memory & cpu
		 *       and only change title and show/hide particular category icons
		 */
		hideBalloon();
		Log.d("d", "show balloon");
		OverlayItem item = overlays.get(index);
		if (!(item instanceof FarmOverlayItem))
			return false;
		
		// wtf. the map sends one aux click through the movement
		disableHiding();
		map.centerOnGeoPoint(item.getPoint());
		lastSelected = (FarmOverlayItem)item;
		return lastSelected.showBalloon();
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView)
	{
		// detect pinch to prevent firing baloons on it
	    int fingers = e.getPointerCount();
	    if( e.getAction()==MotionEvent.ACTION_DOWN ){
	        isPinch=false;  // Touch DOWN, don't know if it's a pinch yet
	    }
	    if( e.getAction()==MotionEvent.ACTION_MOVE && fingers==2 ){
	        isPinch=true;   // Two fingers, def a pinch
	    }
	    return super.onTouchEvent(e,mapView);
	}
	
	/* TODO: maybe remove the old ones?
	 * TODO: maybe faster join?
	 */
	protected void setVisiblePoints(Hashtable<Long, FarmInfo> farms)
	{
		Iterator<OverlayItem> overlaysIterator = overlays.iterator();
		OverlayItem last;
		// remove existing from hashtable
		while (overlaysIterator.hasNext())
		{
			last = overlaysIterator.next();
			// ignore other overlays
			if (!(last instanceof FarmOverlayItem))
					continue;
			FarmOverlayItem lastFarm = (FarmOverlayItem)last;
			if (firstBalloon == lastFarm.data.id)
			{
				hideBalloon();
				lastSelected = lastFarm;
				lastSelected.showBalloon();
				firstBalloon = FarmInfo.INVALID_FARM_ID;
			}
			farms.remove(Long.valueOf(lastFarm.data.id));
		}
		
		Collection<FarmInfo> newFarms= farms.values();
		Iterator<FarmInfo> farmIterator = newFarms.iterator();
		FarmOverlayItem toAdd;
		FarmInfo nextFarm;
		while (farmIterator.hasNext())
		{
			nextFarm = farmIterator.next();
			toAdd = new FarmOverlayItem(FarmInfo.getGeoPoint(nextFarm), nextFarm, map);
			overlays.add(toAdd);
			if (firstBalloon == nextFarm.id)
			{
				hideBalloon();
				lastSelected = toAdd;
				lastSelected.showBalloon();
				firstBalloon = FarmInfo.INVALID_FARM_ID;
			}
		}
		
		populate();
	}
	
	/* NOTE: animating to point generates for some mysterious reason one excessive click
	 *       so we ignore all clicks between start & stop of animation
	 */
	private boolean hidingEnabled = true; 
	public void enableHiding()
	{
		hidingEnabled = true;
	}
	public void disableHiding()
	{
		hidingEnabled = false;
	}

	@Override
	public boolean onSingleTap(MotionEvent e) {
		if (lastSelected == null || !hidingEnabled)
			return false;

		hideBalloon();
		
		return true;
	}

}

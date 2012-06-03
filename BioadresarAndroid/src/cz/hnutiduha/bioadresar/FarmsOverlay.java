package cz.hnutiduha.bioadresar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import cz.hnutiduha.bioadresar.data.FarmInfo;

public class FarmsOverlay extends ItemizedOverlay<OverlayItem>{
	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
	private Context context;
	private boolean isPinch;
	
	public FarmsOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
		populate();
	}
	
	public FarmsOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
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
		
	@Override
	protected boolean onTap(int index) {
		// don't show detail on pinch
		if (isPinch)
			return false;
		
		OverlayItem item = overlays.get(index);
		if (!(item instanceof FarmOverlayItem))
			return false;
		
		return ((FarmOverlayItem)item).showBaloon(context);
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
	
	protected void setVisiblePoints(Hashtable<Long, FarmInfo> farms)
	{
		Log.d("gui", "starting redraw of visible points");
		Iterator<OverlayItem> overlaysIterator = overlays.iterator();
		OverlayItem last;
		// remove existing from hashtable
		while (overlaysIterator.hasNext())
		{
			last = overlaysIterator.next();
			// ignore other overlays
			if (!(last instanceof FarmOverlayItem))
					continue;
			
			farms.remove(Long.valueOf(((FarmOverlayItem)last).data.id));
		}
		Log.d("gui", "done going through already drawn");
		
		Collection<FarmInfo> newFarms= farms.values();
		Iterator<FarmInfo> farmIterator = newFarms.iterator();
		FarmOverlayItem toAdd;
		FarmInfo nextFarm;
		while (farmIterator.hasNext())
		{
			nextFarm = farmIterator.next();
			toAdd = new FarmOverlayItem(FarmInfo.getGeoPoint(nextFarm), nextFarm);
			overlays.add(toAdd);
		}
		Log.d("gui", "done adding new");
		
		populate();
	}

}

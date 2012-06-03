package cz.duha.bioadresar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.TextView;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import cz.duha.bioadresar.data.FarmInfo;

public class FarmsOverlay extends ItemizedOverlay<OverlayItem>{
	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
	private Context context;
	
	public FarmsOverlay(Drawable defaultMarker, Context context) {
		  super(boundCenterBottom(defaultMarker));
		  this.context = context;
		}
	
	public FarmsOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
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
		TextView message = new TextView(context);
		final SpannableString s = new SpannableString("http://google.com\n tel: 776 319 314\n");
		Linkify.addLinks(s, Linkify.ALL);
		message.setText(s);
		
		OverlayItem item = overlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(item.getTitle());
		dialog.setView(message);
		dialog.show();
		
		message.setMovementMethod(LinkMovementMethod.getInstance());
		
		return true;
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

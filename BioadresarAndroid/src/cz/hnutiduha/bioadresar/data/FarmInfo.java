/*  This file is part of BioAdresar.
	Copyright 2012 Jiri Zouhar (zouhar@trilobajt.cz), Jiri Prokop

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

package cz.hnutiduha.bioadresar.data;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;

import cz.hnutiduha.bioadresar.MainTabbedActivity;
import cz.hnutiduha.bioadresar.detail.DetailActivity;
import cz.hnutiduha.bioadresar.map.MapActivity;

public class FarmInfo implements OnClickListener{
	
	public static long INVALID_FARM_ID = -1;
	// these are always present
	public long id;
	public String name;
	public double lat, lon;
	public List<Long> categories;
	
	// call DatabaseHelper.fillDetails to obtain these
	public String description;
	public String type;
	public FarmContact contact;
	public List<Long> products;
	
	private static final int viewTagTarget = 0xdeadbeef;
	private static final String viewTargetMap = "map";
	private static final String viewTargetDetail = "detail";
	
	private Location location = null;
	
	public static GeoPoint getGeoPoint(FarmInfo farm) {
		return new GeoPoint((int)(farm.lat * 1E6), (int)(farm.lon * 1E6));
	}
	
	public float getDistance(Location targetLocation) {
		Location destLocation = getLocation();
		return targetLocation.distanceTo(destLocation);
	}
	
	public Location getLocation() {
		if (location == null) {
			location = new Location("");
			location.setLatitude(lat);
			location.setLongitude(lon);
		}
		
		return location;
	}
	
	public void goToMap(View parent)
	{
		LocationCache.centerOnLocation(getLocation());
		Context context = parent.getContext();
		Intent map = new Intent(context, MainTabbedActivity.class);
		map.putExtra(MainTabbedActivity.defaultActivityPropertyName, MainTabbedActivity.mapActivityTag);
		map.putExtra(MapActivity.mapNodePropertyName, Long.valueOf(id));
		context.startActivity(map);
	}
	
	public void goToDetail(View parent)
	{
		DatabaseHelper helpMeeeFillMeee = DatabaseHelper.getDefaultDb();
		helpMeeeFillMeee.fillDetails(this);
		Context context = parent.getContext();
		DetailActivity.setFarm(this);
		Intent detail = new Intent(context, DetailActivity.class);
		context.startActivity(detail);
	}

	// this is little hack, it doesn't belong to data class, but it serve us nicely :)
	@Override
	public void onClick(View v) {
		Object targetTag = v.getTag(viewTagTarget);
		if (targetTag == null)
		{
			Log.e("gui", "farm set as on click listener without target tag");
			return;
		}
		if (((String)targetTag).equals(viewTargetMap))
			goToMap(v);
		
		else if (((String)targetTag).equals(viewTargetDetail))
			goToDetail(v);
		else
			Log.e("gui", "farm set as on click listener with unknown target tag " + targetTag.toString());
	}
	
	public void fillInfoToView(View parent, int nameTextId, int categoriesLayoutId, Location distanceFrom, int distanceTextId)
	{	
		// name
		TextView nameView = (TextView) parent.findViewById(nameTextId);
		nameView.setText(this.name);
		
		// icons
		LinearLayout categoryIcons = (LinearLayout) parent.findViewById(categoriesLayoutId);
		Iterator<Long> it = this.categories.iterator();
		ImageView icon;
	    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20, 1);
	    params.gravity = Gravity.LEFT;
	    Context context = parent.getContext();
	    Resources resources = parent.getResources();
		while (it.hasNext())
		{	
			icon = new ImageView(context);
			icon.setImageResource(resources.getIdentifier("drawable/category_" + it.next(), null, context.getPackageName()));
		    icon.setLayoutParams(params);
		    categoryIcons.addView(icon);
		}
		
		// distance, if required
		if (distanceFrom != null)
		{
			TextView distanceText = (TextView)parent.findViewById(distanceTextId);
			if (distanceText == null)
			{
				Log.e("gui", "requesting distance label but TextView id " + distanceTextId + " is invalid");
				return;
			}
			
			long distance = (long)getDistance(distanceFrom);
			long km = distance / 1000;
			long m = distance % 1000;
			if (km > 0)
				distanceText.setText(String.valueOf(km) + "." + String.valueOf(m / 10) + " km");
			else
				distanceText.setText(String.valueOf(m) + " m");
		}
	}
	
	public void setToMapListener(View view)
	{
    	view.setTag(viewTagTarget, viewTargetMap);
    	view.setOnClickListener(this);
	}
	public void setToDetailListener(View view)
	{
		view.setTag(viewTagTarget, viewTargetDetail);
    	view.setOnClickListener(this);
	}

}

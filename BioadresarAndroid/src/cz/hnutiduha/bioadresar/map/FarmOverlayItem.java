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

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import cz.hnutiduha.bioadresar.R;

import cz.hnutiduha.bioadresar.data.FarmInfo;

public class FarmOverlayItem extends OverlayItem implements OnClickListener{
	protected FarmInfo data;
	MapView map;
	FarmOverlayView balloon = null;
	
	public FarmOverlayItem(GeoPoint point, FarmInfo farm, MapView map)
	{
		super(point, farm.name, farm.name);
		this.data = farm;
		this.map = map;
	}
	
	public void showDetail()
	{
		data.goToDetail(balloon);
	}
	
	@Override
	public void onClick(View v) {
		hideBalloon();
		showDetail();
	}
	
	private void createBalloon(Context context)
	{
		balloon = new FarmOverlayView(context, data);
		balloon.setOnClickListener(this);
		View closeRegion = (View) balloon.findViewById(R.id.balloon_close);
		if (closeRegion != null)
			closeRegion.setVisibility(View.GONE);
			
		View disclosureRegion = balloon.findViewById(R.id.balloon_disclosure);
		if (disclosureRegion != null)
			disclosureRegion.setVisibility(View.VISIBLE);
		
	}
	
	public boolean showBalloon()
	{
		boolean isRecycled = true;
		if (balloon == null)
		{
			createBalloon(map.getContext());
			isRecycled = false;
		}
		
		MapView.LayoutParams params = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, getPoint(),
				MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;
		
		balloon.setVisibility(View.VISIBLE);
		
		if (isRecycled) {
			balloon.setLayoutParams(params);
		} else {
			map.addView(balloon, params);
		}
		
		return true;
	}
	
	public void hideBalloon()
	{
		if (balloon != null)
			balloon.setVisibility(View.GONE);
	}
}

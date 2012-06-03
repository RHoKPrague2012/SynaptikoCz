package cz.hnutiduha.bioadresar;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import cz.hnutiduha.bioadresar.data.FarmInfo;

public class FarmOverlayItem extends OverlayItem{
	protected FarmInfo data;

	public FarmOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
		throw new RuntimeException("huaa this is not implemented");
	}
	
	public FarmOverlayItem(GeoPoint point, FarmInfo farm)
	{
		super(point, farm.name, farm.name);
		this.data = farm;
	}

}

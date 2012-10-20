package cz.hnutiduha.bioadresar.list;

import cz.hnutiduha.bioadresar.R;
import cz.hnutiduha.bioadresar.data.FarmInfo;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.content.Context;

public class FarmLinearLayout extends LinearLayout {
	public long farmId;
	public float distance;
	
    public FarmLinearLayout(Context context, FarmInfo farm, Location centerOfOurUniverse)
    {
    	super(context);
    	farmId = farm.id;
    	distance = farm.getDistance(centerOfOurUniverse);
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View v = inflater.inflate(R.layout.list_item_layout, this);
		
		LinearLayout toDetail = (LinearLayout)v.findViewById(R.id.toDetailArea);
		farm.setToDetailListener(toDetail);
		LinearLayout toMap = (LinearLayout)v.findViewById(R.id.toMapArea);
		farm.setToMapListener(toMap);
		
		farm.fillInfoToView(v, R.id.farmName, R.id.productionIcons, centerOfOurUniverse, R.id.distance);
    }

}

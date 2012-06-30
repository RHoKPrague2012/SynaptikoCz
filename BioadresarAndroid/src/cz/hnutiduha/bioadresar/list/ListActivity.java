package cz.hnutiduha.bioadresar.list;

import java.util.TreeSet;

import cz.hnutiduha.bioadresar.R;
import cz.hnutiduha.bioadresar.data.DatabaseHelper;
import cz.hnutiduha.bioadresar.data.FarmInfo;
import cz.hnutiduha.bioadresar.data.LocationCache;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class ListActivity extends Activity {
	 TreeSet<FarmInfo> allFarms;
	 LinearLayout view;
	 Context context;
	 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        
        view = (LinearLayout) findViewById(R.id.list_main_layout);
        context = view.getContext();
    }
    
    private static boolean farmsInitialized = false;
    
    public void onStart()
    {
    	super.onStart();
    	if (farmsInitialized)
    		return;
    	
    	// FIXME: loading all farms is really slow
        addFarms();
        farmsInitialized = true;
    }
    
    private void appendFarm(FarmInfo farm, Location centerOfOurUniverse)
    {
    	LinearLayout lay = new LinearLayout(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.list_item_layout, lay);
		
		LinearLayout toDetail = (LinearLayout)v.findViewById(R.id.toDetailArea);
		farm.setToDetailListener(toDetail);
		LinearLayout toMap = (LinearLayout)v.findViewById(R.id.toMapArea);
		farm.setToMapListener(toMap);
		
		
		farm.fillInfoToView(v, R.id.farmName, R.id.productionIcons, centerOfOurUniverse, R.id.distance);
		
		view.addView(lay);
    }
    
    private void addFarms()
    {
        DatabaseHelper defaultDb = DatabaseHelper.getDefaultDb();
		
        // TODO: get location from map
        Location loc = LocationCache.getCurrentLocation(context);
        if (loc == null)
        {
        	Log.e("gps", "can't get location");
        	return;
        }

        allFarms =  defaultDb.getAllFarmsSortedByDistance(loc);
                        
        for (FarmInfo farm : allFarms)
        {
        	appendFarm(farm, loc);
        }
    }
}

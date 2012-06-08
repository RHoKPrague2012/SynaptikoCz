package cz.hnutiduha.bioadresar.list;

import java.util.Iterator;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    
    private void addFarms()
    {
        DatabaseHelper defaultDb = DatabaseHelper.getDefaultDb();
		
        // TODO: get location from map
        Location loc = LocationCache.getCenter();
        if (loc == null)
        {
        	Log.e("gps", "can't get location");
        	return;
        }
        allFarms =  defaultDb.getAllFarmsSortedByDistance(loc);
        for (FarmInfo farm : allFarms)
        {
        	addFarm(farm, farm.getDistance(loc));
        }
    }
    
    private void addFarm(FarmInfo farm, double distance)
    {

    	// FIXME: the layouting is ugly, maybe table + adding rows?
        LinearLayout line = new LinearLayout(context);
        
        TextView title = new TextView(context);
        title.setText(farm.name);
        
        // this is how to react to click on title
        title.setOnClickListener(farm);
        line.addView(title);
		// TODO: refactor this code (creating category icons layout) and reuse in  FarmOverlayView
        // TODO: make this align all to left, no spaces
        // TODO: size of the icons is too large
		LinearLayout icons = new LinearLayout(context);
		Iterator<Long> it = farm.categories.iterator();
		ImageView icon;
		while (it.hasNext())
		{
			icon = new ImageView(context);
			icon.setImageResource(context.getResources().getIdentifier("drawable/category_" + it.next(), null, context.getPackageName()));
		    icons.addView(icon);
		}
		line.addView(icons);
		TextView distanceView = new TextView(context);
		distanceView.setText(String.valueOf(distance));
		line.addView(distanceView);
		
        line.setVisibility(View.VISIBLE);
		view.addView(line);
		
		// TODO: maybe we would want show on may -> FarmMapView.centerOnGeoPoint(...) & show activity/fire intent
    }
}

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

package cz.hnutiduha.bioadresar.list;

import java.util.Hashtable;
import java.util.TreeSet;

import cz.hnutiduha.bioadresar.R;
import cz.hnutiduha.bioadresar.data.DatabaseHelper;
import cz.hnutiduha.bioadresar.data.FarmInfo;
import cz.hnutiduha.bioadresar.data.LocationCache;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

class AddAllFarms extends AsyncTask<Void, FarmInfo, Void> {
	ListActivity activity;
	Location loc;
	
	public AddAllFarms(ListActivity activity)
	{
		super();
		this.activity = activity;
		loc = LocationCache.getCenter();
	}

	@Override
	protected Void doInBackground(Void... params) {
		
		Log.d("list", "starting background task");
        DatabaseHelper defaultDb = DatabaseHelper.getDefaultDb();
		
        if (loc == null)
        {
        	Log.e("list", "can't get location");
        	return null;
        }	        
    	TreeSet<FarmInfo> allFarms =  defaultDb.getAllFarmsSortedByDistance(loc);
        for (FarmInfo farm : allFarms)
        {
        	publishProgress(farm);
	    }
		return null;
	}
	
	protected void onProgressUpdate(FarmInfo... farms)
	{
		activity.insertFarm(farms[0],  loc);
	}
	
	protected void onPostExecute(Void result) {
		Log.d("list", "background task finished");
	}
}

class AddFarmsInRectangle extends AddAllFarms
{
	public AddFarmsInRectangle(ListActivity activity)
	{
		super(activity);
	}
	@Override
	protected Void doInBackground(Void... params) {
		
		Log.d("list", "starting background task");
        DatabaseHelper defaultDb = DatabaseHelper.getDefaultDb();
		
        // TODO: get location from map
        loc = LocationCache.getCenter();
        if (loc == null)
        {
        	Log.e("list", "can't get location");
        	return null;
        }
        
        // NOTE: hardcoded, maybe move somewhere
        double latOffset = -190520 / 1E6;
        double lonOffset = +219726 / 1E6;
        
        Hashtable<Long, FarmInfo> nearestFarms = defaultDb.getFarmsInRectangle(loc.getLatitude() - latOffset, loc.getLongitude() - lonOffset,
        		loc.getLatitude() + latOffset, loc.getLongitude() + lonOffset);
        
        for (FarmInfo farm : nearestFarms.values())
        {
        	publishProgress(farm);
	    }
		return null;
	}
}

public class ListActivity extends Activity {
	private static boolean farmsInitialized = false;
	LinearLayout view;
	Context context;
	AsyncTask<Void, FarmInfo, Void> farmsLoader = null;
	 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        
        view = (LinearLayout) findViewById(R.id.list_main_layout);
        context = view.getContext();
    }
    
    public void onStart()
    {
    	super.onStart();
    	if (farmsInitialized)
    		return;
    	
    	// FIXME: loading all farms is really slow
    	farmsLoader = new AddFarmsInRectangle(this);
    	farmsLoader.execute();
    }
    
    public void onStop()
    {
    	super.onStop();
    	if (farmsLoader != null)
    		farmsLoader.cancel(true);
    }
    
    
    // backward search - hope new items will go with greater distance
    private int getFarmPos(long farmId, float distance)
    {
    	
    	int childCount = view.getChildCount();
    	if (childCount == 0)
    	{
    		return 0;
    	}
    	
    	FarmLinearLayout childAtPos = (FarmLinearLayout)view.getChildAt(--childCount);
    	
    	while (childAtPos.distance > distance && childCount > 0)
    	{
    		childAtPos = (FarmLinearLayout) view.getChildAt(--childCount);
    	}
    	
    	if (childAtPos.farmId == farmId)
    		return -1;
    	
    	return childCount;
    }
    
    protected void insertFarm(FarmInfo farm, Location centerOfOurUniverse)
    {
    	int desiredPos = getFarmPos(farm.id, farm.getDistance(centerOfOurUniverse));
    	if (desiredPos == -1)
    		return;
    	
    	Log.d("list", "inserting farm " + farm.name + " to pos " + desiredPos);
    	LinearLayout newFarm = new FarmLinearLayout(context, farm, centerOfOurUniverse);
    	
    	view.addView(newFarm, desiredPos);
    	
    }
    
    protected void appendFarm(FarmInfo farm, Location centerOfOurUniverse)
    {
		FarmLinearLayout newFarm = new FarmLinearLayout(context, farm, centerOfOurUniverse);
		Log.d("list", "inserting farm " + farm.name + " to pos " + view.getChildCount());
		view.addView(newFarm, view.getChildCount());
    }
}

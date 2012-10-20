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
import java.util.Iterator;
import java.util.SortedSet;
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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

class AddAllFarms extends AsyncTask<Void, FarmInfo, Boolean> {
	ListActivity activity;
	Location loc;
	
	public AddAllFarms(ListActivity activity)
	{
		super();
		this.activity = activity;
		loc = LocationCache.getCenter();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Log.d("list", "loading all farms");
        if (loc == null)
        {
        	Log.e("list", "can't get location");
        	return Boolean.FALSE;
        }

        DatabaseHelper defaultDb = DatabaseHelper.getDefaultDb();
    	TreeSet<FarmInfo> allFarms =  defaultDb.getAllFarmsSortedByDistance(loc);
        for (FarmInfo farm : allFarms)
        {
        	if (isCancelled())
        		return Boolean.FALSE;
        	publishProgress(farm);
	    }
		return Boolean.TRUE;
	}
	
	protected void onProgressUpdate(FarmInfo... farms)
	{
		activity.insertFarm(farms[0], loc);
	}
	
	protected void onCancelled()
	{
		Log.d("list", "loading of farms cancelled");
		activity.showNextButton(true);
	}
	
	protected void onPostExecute(Boolean isDone) {
		activity.showNextButton(!isDone.booleanValue());
	}
}

class AddNext25 extends AddAllFarms
{
	
	public AddNext25(ListActivity activity)
	{
		super(activity);
	}
	
	private static TreeSet<FarmInfo> allFarms = null;
	private static FarmInfo next = null;
	protected Boolean doInBackground(Void...voids)
	{
		Log.d("list", "loading next 25 famrs");
		
        if (loc == null)
        {
        	Log.e("list", "can't get location");
        	return Boolean.FALSE;
        }
        
        DatabaseHelper defaultDb = DatabaseHelper.getDefaultDb();
        
    	TreeSet<FarmInfo> currentFarms =  defaultDb.getAllFarmsSortedByDistance(loc);
    	SortedSet<FarmInfo> tail = null;
    	if (!currentFarms.equals(allFarms))
    	{
    		allFarms = currentFarms;
    		next = allFarms.first();
    		tail = currentFarms;
    	}
    	else
    		tail = allFarms.tailSet(next);
    	
    	Iterator<FarmInfo> iter = tail.iterator();
    	for (int i = 0; i < 25; i++)
    	{
    		if (iter.hasNext())
    			publishProgress(iter.next());
    		else
    			return Boolean.TRUE;
    	}
    	if (iter.hasNext())
    	{
    		next = iter.next();
    		return Boolean.FALSE;
    	}
    	else
    		return Boolean.TRUE;
	}
}

class AddFarmsInRectangle extends AddAllFarms implements View.OnClickListener
{
	public AddFarmsInRectangle(ListActivity activity)
	{
		super(activity);
	}
	@Override
	protected Boolean doInBackground(Void... params) {
		Log.d("list", "starting background task");
        DatabaseHelper defaultDb = DatabaseHelper.getDefaultDb();
		
        // TODO: get location from map
        loc = LocationCache.getCenter();
        if (loc == null)
        {
        	Log.e("list", "can't get location");
        	return Boolean.FALSE;
        }
        
        // NOTE: hardcoded, maybe move somewhere
        double latOffset = -190520 / 1E6;
        double lonOffset = +219726 / 1E6;
        
        Hashtable<Long, FarmInfo> nearestFarms = defaultDb.getFarmsInRectangle(loc.getLatitude() - latOffset, loc.getLongitude() - lonOffset,
        		loc.getLatitude() + latOffset, loc.getLongitude() + lonOffset);
        
        for (FarmInfo farm : nearestFarms.values())
        {
        	if (isCancelled())
        		return Boolean.FALSE;
        	publishProgress(farm);
	    }
        // we just don't know...
		return Boolean.FALSE;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}

public class ListActivity extends Activity implements View.OnClickListener{
	private static boolean farmsInitialized = false;
	LinearLayout view;
	Button next25Button;
	Context context;
	AsyncTask<Void, FarmInfo, Boolean> farmsLoader = null;
	 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        
        view = (LinearLayout) findViewById(R.id.list_main_layout);
        context = view.getContext();
        
        next25Button = (Button)findViewById(R.id.next_25_button);
        next25Button.setOnClickListener(this);
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
    
    protected void showNextButton(boolean show)
    {
    	next25Button.setEnabled(show);
    }
    
    // backward search - hope new items will go with greater distance
    private int getFarmPos(long farmId, float distance)
    {
    	Log.d("list", "finding place for distance " + distance);
    	int childCount = view.getChildCount();
    	
    	FarmLinearLayout childAtPos;
    	
    	while(childCount > 0)
    	{
    		childAtPos = (FarmLinearLayout)view.getChildAt(--childCount);
    		if (childAtPos.farmId == farmId)
    			return -1;
    		
    		if (childAtPos.distance < distance)
    			return childCount + 1;
    	}
    	    	
    	return 0;
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
	public void onClick(View v) {
		if (v.equals(next25Button))
		{
			if (farmsLoader == null || farmsLoader.getStatus() == AsyncTask.Status.FINISHED)
			{
				showNextButton(false);
				farmsLoader = new AddNext25(this);
				farmsLoader.execute();
			}
		}
	}

}

package cz.hnutiduha.bioadresar;

import java.io.IOException;
import java.util.TreeSet;

import android.app.ListActivity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.SQLException;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import cz.hnutiduha.bioadresar.data.DatabaseHelper;
import cz.hnutiduha.bioadresar.data.FarmInfo;


public class MainTabbedActivity extends TabActivity {

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    DatabaseHelper.setContext(this);
	    
	    setContentView(R.layout.main);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, MapActivity.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("Map View").setIndicator("Map",
	    			res.getDrawable(R.drawable.ic_map_marker))
	    		.setContent(intent);
	    tabHost.addTab(spec);
	    
	    intent = new Intent().setClass(this, ListActivity.class);
	    spec = tabHost.newTabSpec("List View").setIndicator("List",
	    			res.getDrawable(R.drawable.ic_launcher))
	    		.setContent(intent);
	    tabHost.addTab(spec);
	    
	    // FIXME remove method for testing in final version
	    //testDbHelper();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		DatabaseHelper.closeDefaultDb();
	}
	
	private void testDbHelper() {
		DatabaseHelper dbHelper = DatabaseHelper.getDefaultDb();

		try {
			dbHelper.createDb();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}

		try {
			dbHelper.openDb();
			/*Hashtable<Long, FarmInfo> infos = dbHelper.getFarmsInRectangle(47, 15, 49, 17);
			Log.d("size", infos.size() + "");
			
			for (FarmInfo info : infos.values()) {
				Log.d("info detail - categories", "size: " + info.categories.size());
				
				dbHelper.fillDetails(info);
				
				Log.d("info detail", info.name + "; " + info.type + "; " + info.description);
				Log.d("info detail - contact", info.contact.city + "; " + info.contact.street + "; " + info.contact.phoneNumbers.size());
				Log.d("info detail - products", "size: " + info.products.size());
			}*/
			
			Location testLocation = new Location("");
			testLocation.setLatitude(49);
			testLocation.setLongitude(16);
			TreeSet<FarmInfo> farms = dbHelper.getAllFarmsSortedByDistance(testLocation);
			
			for (FarmInfo farm : farms) {
				Log.d("distance test", farm.getDistance(testLocation) + "; " + farm.lat + " - " + farm.lon);
			}
			
		} catch (SQLException sqle) {
			throw sqle;
		} finally {
			dbHelper.close();
		}
	}
	
}

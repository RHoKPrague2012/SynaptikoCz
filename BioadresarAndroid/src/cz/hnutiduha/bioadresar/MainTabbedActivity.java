package cz.hnutiduha.bioadresar;

import java.io.IOException;
import java.util.TreeSet;

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
import cz.hnutiduha.bioadresar.data.LocationCache;
import cz.hnutiduha.bioadresar.filter.FilterActivity;
import cz.hnutiduha.bioadresar.map.MapActivity;
import cz.hnutiduha.bioadresar.list.ListActivity;
import cz.hnutiduha.bioadresar.R;

public class MainTabbedActivity extends TabActivity {
	
	public static String defaultActivityPropertyName = "defaultActivity";
	public static String mapActivityTag = "Map View";
	public static String listActivityTag = "List View";

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    DatabaseHelper.setContext(this);
	    
	    // current location is default center of everything :)
	    if (LocationCache.getCenter() == null)
	    	LocationCache.centerOnGps(this);
	    
	    
	    setContentView(R.layout.main_view);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab
	    
	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, MapActivity.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec(mapActivityTag).setIndicator(res.getString(R.string.map_tab_title),
	    			res.getDrawable(res.getIdentifier("drawable/ic_menu_mapmode", null, "android")))
	    		.setContent(intent);
	    tabHost.addTab(spec);
	    
	    intent = new Intent().setClass(this, ListActivity.class);
	    spec = tabHost.newTabSpec(listActivityTag).setIndicator(res.getString(R.string.list_tab_title),
	    			res.getDrawable(res.getIdentifier("drawable/ic_menu_agenda", null, "android"))) 
	    		.setContent(intent);
	    tabHost.addTab(spec);
	    
	    intent = new Intent().setClass(this, FilterActivity.class);
	    spec = tabHost.newTabSpec("List View").setIndicator(res.getString(R.string.filter_tab_title),
	    			res.getDrawable(res.getIdentifier("drawable/ic_menu_search", null, "android"))) 
	    		.setContent(intent);
	    tabHost.addTab(spec);

	    
	    String targetTab = getIntent().getStringExtra(defaultActivityPropertyName);
	    if (targetTab != null)
	    {
	    	tabHost.setCurrentTabByTag(targetTab);
	    }
	    else if (savedInstanceState != null) {
            tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
	    
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

package cz.duha.bioadresar;

import cz.duha.bioadresar.data.DatabaseHelper;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;


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
/*
	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, AlbumsActivity.class);
	    spec = tabHost.newTabSpec("albums").setIndicator("Albums",
	                      res.getDrawable(R.drawable.ic_tab_albums))
	                  .setContent(intent);
	    tabHost.addTab(spec);
*/
	    //tabHost.setCurrentTab(2);
	}
	
	/*
	private void initDbHelper()
	{
		DatabaseHelper dbHelper = new DatabaseHelper(this);

		try {
			dbHelper.createDb();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}

		try {
			dbHelper.openDb();
			Log.d("ha", "db opened");
			Cursor c = dbHelper.getCategoryCursor();

			c.moveToNext();
			while (!c.isAfterLast()) {
				String name = c.getString(0);

				Log.d("category", name);
				c.moveToNext();
			}
			c.close();
			
			c = dbHelper.getFarmCursor();
			c.moveToNext();
			while (!c.isAfterLast()) {
				String name = c.getString(0);

				Log.d("farm", name);
				c.moveToNext();
			}
			c.close();
			
			c = dbHelper.getFarmCursorInArea(49.57124, 16.49922, 50, 17);
			c.moveToNext();
			while (!c.isAfterLast()) {
				String name = c.getString(0);
				Double gpsLat = c.getDouble(1);
				Double gpsLong = c.getDouble(2);

				Log.d("in area", name + ": " + gpsLat + "; " + gpsLong);
				c.moveToNext();
			}
			c.close();
		} catch (SQLException sqle) {
			throw sqle;
		} finally {
			dbHelper.close();
		}


	}
	*/
	
}

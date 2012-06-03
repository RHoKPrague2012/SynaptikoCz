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
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		DatabaseHelper.closeDefaultDb();
	}
	
}

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

package cz.hnutiduha.bioadresar;

import cz.hnutiduha.bioadresar.about.AboutActivity;
import cz.hnutiduha.bioadresar.config.ConfigActivity;
import cz.hnutiduha.bioadresar.data.DatabaseHelper;
import cz.hnutiduha.bioadresar.data.LocationCache;
import cz.hnutiduha.bioadresar.list.ListActivity;
import cz.hnutiduha.bioadresar.map.MapActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainMenuActivity extends Activity implements OnClickListener {

	TextView location;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main_menu);
        
	    DatabaseHelper.setContext(this);
	    
	    // current location is default center of everything :)
	    if (LocationCache.getCenter() == null)
	    	LocationCache.centerOnGps(this);
        
        View item = this.findViewById(R.id.listLink);
        item.setOnClickListener(this);
        item = this.findViewById(R.id.mapLink);
        item.setOnClickListener(this);
        item = this.findViewById(R.id.configLink);
        item.setOnClickListener(this);
        item = this.findViewById(R.id.aboutLink);
        item.setOnClickListener(this);
        
    	location = (TextView)this.findViewById(R.id.locationLabel);
    	location.setOnClickListener(this);
        
        String defaultActivity = PreferenceManager.getDefaultSharedPreferences(this).getString("defaultActivity", "Menu");
        if (defaultActivity.equals("Mapa"))
        {
        	showActivity(R.id.mapLink);
        }
        if (defaultActivity.equals("Seznam"))
        {
        	showActivity(R.id.listLink);
        }
    }
    
    public void onResume()
    {
    	super.onResume();
    	
    	refreshLocation();
    }
    
    public void refreshLocation()
    {
    	LocationCache.centerOnGps(this);
    	if (LocationCache.hasRealLocation())
    		location.setText(getString(R.string.renewLocationLabel) + " (" + getString(R.string.realLocation) + ")");
    	else
    		location.setText(getString(R.string.renewLocationLabel) + " (" + getString(R.string.virtualLocation) + ")");
    	
    	new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				DatabaseHelper.getDefaultDb().getAllFarmsSortedByDistance(LocationCache.getCenter());
				return null;
			}
    		
    	}.execute();
    }
        
    private void showActivity(int id)
    {
		Intent target = null;
		switch (id)
		{
		case R.id.listLink:
			target = new Intent(this, ListActivity.class);
			break;
		case R.id.mapLink:
			target = new Intent(this, MapActivity.class);
			break;
		case R.id.configLink:
			target = new Intent(this, ConfigActivity.class);
			break;
		case R.id.aboutLink:
			target = new Intent(this, AboutActivity.class);
			break;
		}
		
		if (target != null)
			startActivity(target);	
    }
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.locationLabel)
		{
			refreshLocation();
		}
		else
		{
			showActivity(id);
		}
	}
}
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
import cz.hnutiduha.bioadresar.data.DatabaseHelper;
import cz.hnutiduha.bioadresar.data.LocationCache;
import cz.hnutiduha.bioadresar.list.ListActivity;
import cz.hnutiduha.bioadresar.map.MapActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainMenuActivity extends Activity implements OnClickListener {

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
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent target = null;
		switch (v.getId())
		{
		case R.id.listLink:
			target = new Intent(this, ListActivity.class);
			break;
		case R.id.mapLink:
			target = new Intent(this, MapActivity.class);
			break;
		case R.id.configLink:
			break;
		case R.id.aboutLink:
			target = new Intent(this, AboutActivity.class);
			break;
		}
		
		if (target != null)
			startActivity(target);
	}
}
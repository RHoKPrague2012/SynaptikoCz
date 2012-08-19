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

package cz.hnutiduha.bioadresar.detail;

import java.util.Iterator;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cz.hnutiduha.bioadresar.R;
import cz.hnutiduha.bioadresar.data.DatabaseHelper;
import cz.hnutiduha.bioadresar.data.FarmInfo;

public class DetailActivity extends Activity{
	
	// we expect only one detail activity to be shown at a time
	private static FarmInfo currentFarm = null;
	View view = null;
	
	
	public static void setFarm(FarmInfo farm)
	{
		currentFarm = farm;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.detail_view);
        view = (View)findViewById(R.id.detailView);
    }
    
    public void onResume()
    {
    	super.onResume();
    	if (currentFarm != null)
    		fillFarmInfo();
    	else
    	{
    		Log.e("system", "wtf: someone accessed DetailActivity without assiging farm");
    		// or exception, or stacktrace...
    	}
    	
    }
    
    static final int NO_LINKIFY = -1;
    
    private void setFieldTextOrHideEmpty(String text, int linkifyMask, int labelId, int fieldId)
    {
    	TextView field = (TextView)view.findViewById(fieldId);
    	if (text != null && !text.equals(""))
    	{
    		field.setText(text);
    		
    		if (linkifyMask != NO_LINKIFY)
    			Linkify.addLinks(field, linkifyMask);
    	}
    	else
    	{
    		TextView label = (TextView)view.findViewById(labelId);
    		label.setVisibility(TextView.GONE);
    		field.setVisibility(TextView.GONE);
    	}
    }
    
    private void fillFarmInfo()
    {

    	ImageView map = (ImageView)view.findViewById(R.id.mapIcon);
    	currentFarm.setToMapListener(map);
    	
    	TextView field = (TextView) view.findViewById(R.id.farmName);
    	field.setText(currentFarm.name);
    	
    	setFieldTextOrHideEmpty(currentFarm.description, NO_LINKIFY, R.id.descriptionLabel, R.id.descriptionText);
        
        StringBuilder products = new StringBuilder();
		Iterator<Long> productsIterator = currentFarm.products.iterator();
		DatabaseHelper db = DatabaseHelper.getDefaultDb();
		while (productsIterator.hasNext())
		{
			products.append(db.getProductName(productsIterator.next()));
			if (productsIterator.hasNext())
				products.append(", ");
		}
    	setFieldTextOrHideEmpty(products.toString(), NO_LINKIFY, R.id.productionLabel, R.id.productionText);
    	
    	setFieldTextOrHideEmpty(currentFarm.contact.email, Linkify.EMAIL_ADDRESSES, R.id.emailLabel, R.id.emailText);
    	setFieldTextOrHideEmpty(currentFarm.contact.web, Linkify.WEB_URLS, R.id.webLabel, R.id.webText);
    	setFieldTextOrHideEmpty(currentFarm.contact.eshop, Linkify.WEB_URLS, R.id.eshopLabel, R.id.eshopText);
        
        LinearLayout phones = (LinearLayout) view.findViewById(R.id.phonesLayout);
        if (currentFarm.contact.phoneNumbers != null && currentFarm.contact.phoneNumbers.size() > 0)
        {
	        Iterator<String> phoneIterator = currentFarm.contact.phoneNumbers.iterator();
	        while(phoneIterator.hasNext())
	        {
	        	TextView phone = new TextView(phones.getContext());
	        	phone.setText(phoneIterator.next());
	            Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
	            phones.addView(phone);
	        }
        }
        else
        {
            TextView phonesLabel = (TextView) view.findViewById(R.id.phoneLabel);
            phonesLabel.setVisibility(TextView.GONE);
            phones.setVisibility(LinearLayout.GONE);
        }
        
        String address = "";
        if (currentFarm.contact.street !=null && currentFarm.contact.street.length() != 0)
        	address += currentFarm.contact.street;
        if (currentFarm.contact.city != null && currentFarm.contact.city.length() != 0)
        {
        	if (!address.equals(""))
        		address += ", ";
        	address += currentFarm.contact.city;
        }
    	setFieldTextOrHideEmpty(address, Linkify.MAP_ADDRESSES, R.id.addressLabel, R.id.addressText);
    }
}

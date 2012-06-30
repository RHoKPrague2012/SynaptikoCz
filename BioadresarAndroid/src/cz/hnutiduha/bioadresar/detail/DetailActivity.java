package cz.hnutiduha.bioadresar.detail;

import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cz.hnutiduha.bioadresar.MainTabbedActivity;
import cz.hnutiduha.bioadresar.R;
import cz.hnutiduha.bioadresar.data.DatabaseHelper;
import cz.hnutiduha.bioadresar.data.FarmInfo;
import cz.hnutiduha.bioadresar.data.LocationCache;
import cz.hnutiduha.bioadresar.map.MapActivity;

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

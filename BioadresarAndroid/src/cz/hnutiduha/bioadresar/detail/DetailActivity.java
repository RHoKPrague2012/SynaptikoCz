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
    
    private void fillFarmInfo()
    {

    	ImageView map = (ImageView)view.findViewById(R.id.mapIcon);
    	map.setOnClickListener(new OnClickListener ()
    	{

			@Override
			public void onClick(View v) {
				LocationCache.centerOnLocation(currentFarm.getLocation());
				Context context = v.getContext();
				Intent map = new Intent(context, MainTabbedActivity.class);
				map.putExtra(MainTabbedActivity.defaultActivityPropertyName, MainTabbedActivity.mapActivityTag);
				map.putExtra(MapActivity.mapNodePropertyName, Long.valueOf(currentFarm.id));
				context.startActivity(map);
			}
    		
    	});
    	
    	TextView field = (TextView) view.findViewById(R.id.farmName);
    	field.setText(currentFarm.name);
    	
        if (currentFarm.description != null)
        {
        	field = (TextView) view.findViewById(R.id.descriptionText);
        	field.setText(currentFarm.description);
        }
        
        TextView production = (TextView) view.findViewById(R.id.productinText);
        StringBuilder products = new StringBuilder();
		Iterator<Long> productsIterator = currentFarm.products.iterator();
		DatabaseHelper db = DatabaseHelper.getDefaultDb();
		while (productsIterator.hasNext())
		{
			products.append(db.getProductName(productsIterator.next()));
			if (productsIterator.hasNext())
				products.append(", ");
		}
		production.setText(products.toString());
    	
        if (currentFarm.contact.email != null)
        {
            field = (TextView) view.findViewById(R.id.emailText);
        	field.setText(currentFarm.contact.email);
        	Linkify.addLinks(field, Linkify.EMAIL_ADDRESSES);
        }
        
        if (currentFarm.contact.web != null)
        {
        	field = (TextView) view.findViewById(R.id.webText);
        	field.setText(currentFarm.contact.web);
        	Linkify.addLinks(field, Linkify.WEB_URLS);
        }
        
        
        LinearLayout phones = (LinearLayout) view.findViewById(R.id.phonesLayout);
        Iterator<String> phoneIterator = currentFarm.contact.phoneNumbers.iterator();
        while(phoneIterator.hasNext())
        {
        	TextView phone = new TextView(phones.getContext());
        	phone.setText(phoneIterator.next());
            Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
            phones.addView(phone);
        }
        
        
        field = (TextView) view.findViewById(R.id.addressText);
        field.setText(currentFarm.contact.street + ", " + currentFarm.contact.city);
        Linkify.addLinks(field, Linkify.MAP_ADDRESSES);
    }
}

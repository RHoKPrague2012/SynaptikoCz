package cz.hnutiduha.bioadresar.detail;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import cz.hnutiduha.bioadresar.R;
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
    
    private void fillFarmInfo()
    {
        TextView name = (TextView) view.findViewById(R.id.farmName);
        TextView mail = (TextView) view.findViewById(R.id.emailText);
        TextView web = (TextView) view.findViewById(R.id.webText);
        TextView phone = (TextView) view.findViewById(R.id.phoneText);
        TextView address = (TextView) view.findViewById(R.id.addressText);
        TextView desc = (TextView) view.findViewById(R.id.descriptionText);
        
        // TODO: don't crash on null pointers, only clear fields
        name.setText(currentFarm.name);
        mail.setText(currentFarm.contact.email);
        web.setText(currentFarm.contact.web);
        if (currentFarm.contact.phoneNumbers.size() > 0)
        	phone.setText(currentFarm.contact.phoneNumbers.get(0));
        address.setText(currentFarm.contact.street + ", " + currentFarm.contact.city);
        desc.setText(currentFarm.description);
        Linkify.addLinks(mail, Linkify.EMAIL_ADDRESSES);
        Linkify.addLinks(web, Linkify.WEB_URLS);
        Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
        Linkify.addLinks(address, Linkify.MAP_ADDRESSES);
    }
}

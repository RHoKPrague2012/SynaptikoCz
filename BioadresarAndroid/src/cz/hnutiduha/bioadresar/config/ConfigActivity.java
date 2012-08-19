package cz.hnutiduha.bioadresar.config;

import cz.hnutiduha.bioadresar.R;
import cz.hnutiduha.bioadresar.data.LocationCache;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class ConfigActivity extends PreferenceActivity implements OnPreferenceChangeListener {
	protected void onCreate(Bundle savedInstanceState) {
		   super.onCreate(savedInstanceState);
		   addPreferencesFromResource(R.xml.preferences);
		   
		   this.getPreferenceManager().findPreference("defaultLocation").setOnPreferenceChangeListener(this);
		}
	
	public void onStop()
	{
		
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().equals("defaultLocation"))
		{
			LocationCache.centerOnGps(this);
		}
		return false;
	}
}

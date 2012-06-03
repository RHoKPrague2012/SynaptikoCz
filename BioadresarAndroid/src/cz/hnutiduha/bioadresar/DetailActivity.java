package cz.hnutiduha.bioadresar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

public class DetailActivity extends Activity{
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.detailview);
        Intent intent = getIntent();
        TextView name = (TextView) findViewById(R.id.farmName);
        TextView mail = (TextView) findViewById(R.id.emailText);
        
        name.setText(intent.getStringExtra("name"));
        mail.setText(intent.getStringExtra("email"));
        Linkify.addLinks(mail, Linkify.EMAIL_ADDRESSES);
    }

}

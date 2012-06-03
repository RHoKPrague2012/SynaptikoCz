package cz.hnutiduha.bioadresar;

import cz.hnutiduha.bioadresar.data.FarmInfo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public class DetailActivity extends Activity{
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.detailview);
        Intent intent = getIntent();
        EditText mail = (EditText) findViewById(R.id.editText1);
        mail.setText(intent.getStringExtra("email"));
        this.setTitle(intent.getStringExtra("name"));
    }

}

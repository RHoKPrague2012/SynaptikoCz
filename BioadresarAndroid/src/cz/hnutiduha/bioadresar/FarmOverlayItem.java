package cz.hnutiduha.bioadresar;

import java.util.Iterator;
import java.util.LinkedList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import cz.hnutiduha.bioadresar.data.DatabaseHelper;
import cz.hnutiduha.bioadresar.data.FarmInfo;

public class FarmOverlayItem extends OverlayItem implements android.view.View.OnClickListener{
	protected FarmInfo data;
	protected Context context;

	public FarmOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
		throw new RuntimeException("huaa this is not implemented");
	}
	
	public FarmOverlayItem(GeoPoint point, FarmInfo farm)
	{
		super(point, farm.name, farm.name);
		this.data = farm;
	}
	
	private static final int ID_DETAIL_BUTTON = 1;
	private static final int ID_CLOSE_BUTTON = 2;
	private AlertDialog dialog = null;

	public boolean showBaloon(Context context)
	{		
		if (dialog == null)
		{
			this.context = context;
			LinearLayout baloonMainLayout = new LinearLayout(context);
			ImageView imageView;
			// add categories
			Iterator<Long> it = data.categories.iterator();
			while (it.hasNext())
			{
				imageView = new ImageView(context);
	
		        imageView.setImageResource(context.getResources().getIdentifier("drawable/category_" + it.next(), null, context.getPackageName()));
		        baloonMainLayout.addView(imageView);
			}
			
			// add details arrow
			imageView = new ImageView(context);
			imageView.setId(ID_DETAIL_BUTTON);
	        imageView.setImageResource(context.getResources().getIdentifier("drawable/ic_details", null, context.getPackageName()));
	        baloonMainLayout.addView(imageView);
	        imageView.setOnClickListener(this);
	        
	        imageView = new ImageView(context);
	        imageView.setId(ID_CLOSE_BUTTON);
	        imageView.setImageResource(context.getResources().getIdentifier("drawable/btn_dialog", null, "android"));
	        baloonMainLayout.addView(imageView);
	        imageView.setOnClickListener(this);
			
			dialog = new AlertDialog.Builder(context).create();
			dialog.setTitle(data.name);
			dialog.setCanceledOnTouchOutside(true);
			dialog.setCancelable(true);
			dialog.setView(baloonMainLayout);
		}
		
		dialog.show();
		return true;
	}
	
	public void showDetail()
	{
		Intent detail = new Intent(context, DetailActivity.class);
		DatabaseHelper.getDefaultDb().fillDetails(data);
		detail.putExtra("name", data.name);
		detail.putExtra("email", data.contact.email);
		context.startActivity(detail);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case ID_CLOSE_BUTTON:
			if (dialog != null)
				dialog.dismiss();
			break;
		case ID_DETAIL_BUTTON:
			showDetail();
			dialog.dismiss();
			break;
		}
		
	}
}

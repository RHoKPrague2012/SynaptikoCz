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

	public boolean showBaloon(Context context)
	{		
		this.context = context;
		LinearLayout baloonMainLayout = new LinearLayout(context);
		
		// add categories
		Iterator<Long> it = data.categories.iterator();
		while (it.hasNext())
		{
			ImageView imageView = new ImageView(context);

	        imageView.setImageResource(context.getResources().getIdentifier("drawable/category_" + it.next(), null, context.getPackageName()));
	        baloonMainLayout.addView(imageView);
		}
		
		// add details arrow
		ImageView imageView = new ImageView(context);
        imageView.setImageResource(context.getResources().getIdentifier("drawable/ic_details", null, context.getPackageName()));
        baloonMainLayout.addView(imageView);
        imageView.setOnClickListener(this);
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(data.name);
		dialog.setView(baloonMainLayout);
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
		showDetail();
		
	}
}

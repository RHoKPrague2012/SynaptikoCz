package cz.hnutiduha.bioadresar;

import java.util.Iterator;
import java.util.LinkedList;

import android.app.AlertDialog;
import android.content.Context;
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

public class FarmOverlayItem extends OverlayItem{
	protected FarmInfo data;

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
		Intent detail = new Intent(context, DetailActivity.class);
		DatabaseHelper.getDefaultDb().fillDetails(data);
		detail.putExtra("name", data.name);
		detail.putExtra("email", data.contact.email);
		context.startActivity(detail);
		/*
		
		LinearLayout lay = new LinearLayout(context);
		
		// add categories
		Iterator<Long> it = data.categories.iterator();
		while (it.hasNext())
		{
			ImageView imageView = new ImageView(context);

	        imageView.setImageResource(context.getResources().getIdentifier("drawable/category_" + it.next(), null, context.getPackageName()));
	        lay.addView(imageView);
		}
		
		// add details arrow
		ImageView imageView = new ImageView(context);
        imageView.setImageResource(context.getResources().getIdentifier("drawable/ic_details", null, context.getPackageName()));
        lay.addView(imageView);
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(data.name);
		dialog.setView(lay);
		dialog.show();
	*/
		return true;
	}
}

package cz.hnutiduha.bioadresar.map;

import java.util.Iterator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cz.hnutiduha.bioadresar.R;


import cz.hnutiduha.bioadresar.data.FarmInfo;


// NOTE: mostly copy-paste from BalloonOverlayView
public class FarmOverlayView extends FrameLayout {

	private LinearLayout layout;
	private TextView title;
	private LinearLayout icons;
	private FarmInfo data;
	
	public FarmOverlayView(Context context, FarmInfo data) {
		super(context);
		this.data = data;

		setPadding(10, 0, 10, 0);
		
		layout = new LimitLinearLayout(context);
		layout.setVisibility(VISIBLE);

		setupView(context, layout);

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.NO_GRAVITY;

		addView(layout, params);

	}

	protected void setupView(Context context, final ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.balloon_layout, parent);
		title = (TextView) v.findViewById(R.id.balloon_item_title);
		icons = (LinearLayout) v.findViewById(R.id.balloon_item_icons);
		
		// TODO: refactor this code outside & reuse in list view
		// wtf, icons won't align :/
		title.setText(data.name);
		Iterator<Long> it = data.categories.iterator();
		ImageView icon;
		while (it.hasNext())
		{
			icon = new ImageView(context);
			icon.setImageResource(context.getResources().getIdentifier("drawable/category_" + it.next(), null, context.getPackageName()));
		    icons.addView(icon);
		}
		
	}
	
	private class LimitLinearLayout extends LinearLayout {

	    private static final int MAX_WIDTH_DP = 280;
	    
	    final float SCALE = getContext().getResources().getDisplayMetrics().density;

	    public LimitLinearLayout(Context context) {
	        super(context);
	    }

	    public LimitLinearLayout(Context context, AttributeSet attrs) {
	        super(context, attrs);
	    }

	    @Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	        int mode = MeasureSpec.getMode(widthMeasureSpec);
	        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
	        int adjustedMaxWidth = (int)(MAX_WIDTH_DP * SCALE + 0.5f);
	        int adjustedWidth = Math.min(measuredWidth, adjustedMaxWidth);
	        int adjustedWidthMeasureSpec = MeasureSpec.makeMeasureSpec(adjustedWidth, mode);
	        super.onMeasure(adjustedWidthMeasureSpec, heightMeasureSpec);
	    }
	}
}

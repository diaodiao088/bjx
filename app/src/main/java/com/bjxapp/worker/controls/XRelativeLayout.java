package com.bjxapp.worker.controls;

import com.bjxapp.worker.utils.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class XRelativeLayout extends RelativeLayout implements IControl{

	private final Context mContext;
	private String mEvent = "";
	private String mLabel = "";

	public XRelativeLayout(Context context) {
		super(context);
		mContext = context;
	}
	
	public XRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	public XRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}
	
	@Override 
	public boolean performClick(){
		if(Utils.isNotEmpty(mEvent) && Utils.isNotEmpty(mLabel))
		{
			Toast.makeText(mContext, mEvent + ":" + mLabel, Toast.LENGTH_LONG).show();
		}

		return super.performClick();
	}
	
	@Override
	public String getEvent() {
		return mEvent;
	}

	@Override
	public void setEvent(String event) {
		mEvent = event;
	}

	@Override
	public String getLabel() {
		return mLabel;
	}

	@Override
	public void setLabel(String label) {
		mLabel = label;
	}

}

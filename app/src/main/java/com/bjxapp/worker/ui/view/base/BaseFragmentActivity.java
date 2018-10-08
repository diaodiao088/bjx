package com.bjxapp.worker.ui.view.base;

import com.bjxapp.worker.dataupload.Uploader;
import com.bjxapp.worker.global.ActivitiesManager;
import com.bjxapp.worker.utils.Utils;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public abstract class BaseFragmentActivity extends FragmentActivity {
	protected NotificationManager notificationManager;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		ActivitiesManager.getInstance().pushOneActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Uploader.onPageStart(this, getPageName());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Uploader.onPageEnd(this, getPageName());
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	/**
	 * 设置fragment activity name
	 */
	protected abstract String getPageName();
	
	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {
		Utils.finishActivity(this);
	}

}

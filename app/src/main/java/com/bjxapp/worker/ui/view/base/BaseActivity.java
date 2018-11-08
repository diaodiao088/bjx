package com.bjxapp.worker.ui.view.base;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.bjxapp.worker.controls.listview.XListView;
import com.bjxapp.worker.dataupload.Uploader;
import com.bjxapp.worker.global.ActivitiesManager;
import com.bjxapp.worker.utils.Utils;

public abstract class BaseActivity extends Activity {
	protected Activity
			context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		ActivitiesManager.getInstance().pushOneActivity(this);
		
		initControl();
		initView();
		initData();
		setListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Uploader.onPageStart(context, getPageName());
	}

	public void onPause() {
		super.onPause();
		Uploader.onPageEnd(context, getPageName());
	}

	/**
	 * 绑定控件id
	 */
	protected abstract void initControl();

	/**
	 * 初始化控件
	 */
	protected abstract void initView();

	/**
	 * 初始化数据
	 */
	protected abstract void initData();

	/**
	 * 设置监听
	 */
	protected abstract void setListener();
	
	/**
	 * 设置activity name
	 */
	protected abstract String getPageName();

	/**
	 * 打开 Activity
	 * 
	 * @param activity
	 * @param cls
	 * @param name
	 */
	public void start_Activity(Activity activity, Class<?> clazz, BasicNameValuePair... name) {
		Utils.startActivity(activity, clazz, name);
	}

	/**
	 * 关闭 Activity
	 * 
	 * @param activity
	 */
	public void finish(Activity activity) {
		Utils.finishActivity(activity);
	}

	/**
	 * 判断是否有网络连接
	 */
	public boolean isNetworkAvailable(Context context) {
		return Utils.isNetworkAvailable(context);
	}
	
	/**
	 * 动态调整listview高度
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(XListView listView) {
	    ListAdapter listAdapter = listView.getAdapter();
	    if(listAdapter == null) {
	    	return;
	    }
	    int totalHeight = 0;
	    for (int i = 0; i < listAdapter.getCount(); i++) {
	    	View listItem = listAdapter.getView(i, null, listView);
	    	listItem.measure(0, 0);
	    	totalHeight += listItem.getMeasuredHeight();
	    }
	    ViewGroup.LayoutParams params = listView.getLayoutParams();
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	    listView.setLayoutParams(params);
	}
	
}
package com.bjxapp.worker;

import com.baidu.mapapi.SDKInitializer;
import com.bjxapp.worker.dataupload.Uploader;
import android.app.Application;
import android.content.Context;

public class App extends Application {

	private static Context _context;

	@Override
	public void onCreate() {
		super.onCreate();
		_context = getApplicationContext();
		
		//初始化定位及地图sdk
		SDKInitializer.initialize(getApplicationContext());
		
		//初始化数据上报sdk
		Uploader.onApplicationStart(_context);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}

	public static Context getInstance() {
		return _context;
	}

	private static App instance;

	public synchronized static App getAppInstance() {
		if (null == instance) {
			instance = new App();
		}
		return instance;
	}

}

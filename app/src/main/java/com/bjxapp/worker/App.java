package com.bjxapp.worker;

import com.baidu.mapapi.SDKInitializer;
import com.bjxapp.worker.dataupload.Uploader;
import com.bjxapp.worker.exception.MyCrashHandler;
import com.bjxapp.worker.http.keyboard.commonutils.CommonUtilsEnv;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

public class App extends MultiDexApplication {

    private static Context _context;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MyCrashHandler.getInstance().register(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _context = getApplicationContext();

        //初始化定位及地图sdk
        SDKInitializer.initialize(getApplicationContext());

        //初始化数据上报sdk
        Uploader.onApplicationStart(_context);

        CommonUtilsEnv.createInstance(this);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    public static Context getInstance() {
        return _context;
    }

}

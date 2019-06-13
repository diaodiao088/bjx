package com.bjxapp.worker;

import com.baidu.mapapi.SDKInitializer;
import com.bjx.master.R;
import com.bjxapp.worker.dataupload.Uploader;
import com.bjxapp.worker.exception.MyCrashHandler;
import com.bjxapp.worker.http.keyboard.commonutils.CommonUtilsEnv;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.support.multidex.MultiDexApplication;

public class App extends MultiDexApplication {

    private static Context _context;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MyCrashHandler.getInstance().register(this);

        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.white, android.R.color.white);//全局设置主题颜色

                return new ClassicsHeader(context).setAccentColor(Color.parseColor("#545454"));//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });

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

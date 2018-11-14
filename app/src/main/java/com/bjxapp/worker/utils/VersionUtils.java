package com.bjxapp.worker.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.bjxapp.worker.App;

/**
 * Created by zhangdan on 2018/11/13.
 * <p>
 * comments:
 */
public class VersionUtils {

    public static int getLocalVersion() {
        int localVersion = 0;
        try {
            PackageManager packageManager = App.getInstance().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(App.getInstance().getPackageName(), 0);
            localVersion = packageInfo.versionCode;
        } catch (Exception e) {
            localVersion = 0;
        }
        return localVersion;
    }

}

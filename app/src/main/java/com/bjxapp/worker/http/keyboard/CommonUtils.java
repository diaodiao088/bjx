package com.bjxapp.worker.http.keyboard;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.Log;

import com.bjxapp.worker.http.keyboard.commonutils.CommonUtilsEnv;
import com.bjxapp.worker.http.keyboard.commonutils.KLog;
import com.bjxapp.worker.http.keyboard.commonutils.Preconditions;
import com.bjxapp.worker.http.keyboard.commonutils.ProcessType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommonUtils {
    public static final String TAG = "CommonUtils";
    private static volatile Application mGlobalContext;
    private static long sFirstInstallTime = 0L;
    private static final int MIN_CLICK_DELAY_TIME = 500;

    public static int getDensityDpi(Context context) {
        try {
            if (context != null) {
                return context.getResources().getDisplayMetrics().densityDpi;
            } else {
                return 240;
            }
        } catch (Exception e) {
            return 240;
        }
    }

    public static String getProcessName() {
        File f = new File("/proc/self/cmdline");
        InputStream reader = null;
        try {
            reader = new FileInputStream(f);
            byte[] buffer = new byte[256];
            int length = reader.read(buffer);
            if (length > 0) {
                return new String(buffer, 0, length).trim();
            }
        } catch (FileNotFoundException e) {
            if (KLog.sDEBUG) {
                KLog.e(TAG, "proc/self/cmdline not found");
            }
        } catch (IOException e) {
            if (KLog.sDEBUG) {
                KLog.e(TAG, "read cmdline error");
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return "";
    }

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        int statusBarHeightId = context.getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        if (statusBarHeightId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(
                    statusBarHeightId);
        } else {
            statusBarHeight = (int) Math.ceil(25 * context.getResources()
                    .getDisplayMetrics().density);
        }
        return statusBarHeight;
    }

    private static List<PackageInfo> getInstalledPackagesNoThrow(PackageManager pm, int flags) {
        List<PackageInfo> pkgList = null;
        try {
            pkgList = pm.getInstalledPackages(flags);
        } catch (Exception e) {
            // error handling for dumpkey: 3592114665
        }
        if (pkgList == null) {
            pkgList = new ArrayList<PackageInfo>();
        }
        return pkgList;
    }

    public static String getLanguage(Context ctx) {
        Locale locale = ctx.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language;
    }

    public static String getPackageName(Context context) {
        Preconditions.checkNotNull(context);
        return context.getPackageName();
    }


    public static boolean isMainProcess() {
        return CommonUtilsEnv.getInstance() != null && CommonUtilsEnv.getInstance().getProcessType() == ProcessType.MAIN;
    }

    public static boolean isThemeProcess() {
        return CommonUtilsEnv.getInstance() != null && CommonUtilsEnv.getInstance().getProcessType() == ProcessType.THEME;
    }

    public static int applyAlpha(final int color, final float alpha) {
        final int newAlpha = (int) (Color.alpha(color) * alpha);
        return Color.argb(newAlpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static void setGlobalContext(Application context) {
        mGlobalContext = context;
    }

    public static Application getGlobalContext() {
        return mGlobalContext;
    }

    public static long getFirstInstallTime() {
        if (sFirstInstallTime == 0L) {
            try {
                sFirstInstallTime = mGlobalContext.getPackageManager().getPackageInfo(
                        mGlobalContext.getPackageName(), 0).firstInstallTime;
            } catch (Exception e) {
                Log.w(TAG, "getFirstInstallTime: "+e.getMessage() );
            }
        }
        return sFirstInstallTime;
    }

}

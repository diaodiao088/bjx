package com.bjxapp.worker.http.keyboard.commonutils;


import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.LocaleList;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.bjxapp.worker.http.keyboard.CommonUtils;

import java.io.File;

public final class CommonUtilsEnv {

    private static CommonUtilsEnv sCommonUtilsEnv;
    private final Context mContext;
    private final String mProcessName;
    private int mProcessType;
    public static boolean sDEBUG = false;
    private String mCountryCode = "";

    public static CommonUtilsEnv getInstance() {
        return sCommonUtilsEnv;
    }

    public static synchronized CommonUtilsEnv createInstance(Context application) {
        if (sCommonUtilsEnv == null || sCommonUtilsEnv.mContext == null) {
            int processType = ProcessType.MAIN;
            String process = CommonUtils.getProcessName();

            if (application != null) {
                processType = ProcessType.getProcessType(process, application.getPackageName());
            }
            sCommonUtilsEnv = new CommonUtilsEnv(application, processType, process);
        }
        return sCommonUtilsEnv;
    }

    private CommonUtilsEnv(Context application, int processType, String processName) {
        mContext = application;
        mProcessType = processType;
        mProcessName = processName;
        KSystemUtils.init(mContext);
        KSystemUtils.initSysSettings(mContext);
        // DimenUtils.init(mContext.getResources());
        ThreadManager.startup();
    }

    public Context getApplicationContext() {
        return mContext;
    }

    public int getProcessType() {
        return mProcessType;
    }

    public String getProcessName() {
        return mProcessName;
    }

    public boolean isLowRamDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return ((ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE)).isLowRamDevice();
        }
        return true;
    }

    public String getCountryCode() {
        if (mContext == null) {
            return null;
        }
        if (!TextUtils.isEmpty(mCountryCode)) {
            return mCountryCode;
        }
        String countryCode = null;
        try {

            if (Build.VERSION.SDK_INT >= 24) {
                LocaleList locales = mContext.getResources().getConfiguration().getLocales();
                if (locales != null && locales.size() > 0) {
                    countryCode = locales.get(0).getCountry();
                }
            } else {
                countryCode = mContext.getResources().getConfiguration().locale.getCountry();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            try {
                if (TextUtils.isEmpty(countryCode)) {
                    TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                    countryCode = telephonyManager.getSimCountryIso();
                }
            } catch (Throwable t1) {
                t1.printStackTrace();
            }
        } finally {
            if (!TextUtils.isEmpty(countryCode)) {
                mCountryCode = countryCode;
            }
        }
        return countryCode;
    }

    private static final String KEY_GDPR_AGREED = "gdpr_agreed";
    private static boolean sIsGDPRAgreed = true;

    public static boolean updateGDPRAgreed(boolean defaultValue) {
        String s = FileUtils.stringFromFile(new File(CommonUtilsEnv.getInstance().getApplicationContext().getFilesDir(), KEY_GDPR_AGREED));
        sIsGDPRAgreed = TextUtils.isEmpty(s) ? defaultValue : s.equals("1");
        return sIsGDPRAgreed;
    }

    public static void setGDPRAgreed(boolean agreed) {
        FileUtils.stringToFile(agreed ? "1" : "0", new File(CommonUtilsEnv.getInstance().getApplicationContext().getFilesDir(), KEY_GDPR_AGREED));
        sIsGDPRAgreed = agreed;
    }

    public static boolean canReportGDPR() {
        return sIsGDPRAgreed;
    }
}

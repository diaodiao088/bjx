package com.bjxapp.worker.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 硬件相关辅助功能类
 *
 * @author Kid Feng <Kid.Stargazer@gmail.com>
 *         create@ Sep 27, 2010 10:26:15 AM
 */
public class DeviceUtils {
    @SuppressWarnings("unused")
    private static final String TAG = "DevicesUtils";
    private static final long KB_MULTIPLES = 1024;
    private static final long MB_MULTIPLES = 1024 * KB_MULTIPLES;
    private static long totalMen = -1;

    /**
     * 检查sd卡是否可用
     *
     * @return 如果sd卡可用, 返回sd的根目录, 否则返回null
     */
    public static File getSDCardFile() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory();
        } else {
            return null;
        }
    }

    public static boolean isNetWorkingEnable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.isAvailable()) return true;
        }
        return false;
    }

    public static float getSDCardFreeSizeKB() {
        StatFs sdcardFs = doGetSDcardFs();
        if (sdcardFs == null) return 0;
        return ((float) sdcardFs.getAvailableBlocks() / 1024) * ((float) sdcardFs.getBlockSize());
    }

    public static long getSDCardFreeSize() {
        StatFs sdcardFs = doGetSDcardFs();
        if (sdcardFs == null) return 0;
        return (long) sdcardFs.getAvailableBlocks() * sdcardFs.getBlockSize();
    }

    public static long getSDCardTotalSize() {
        StatFs sdcardFs = doGetSDcardFs();
        if (sdcardFs == null) return 0;
        return (long) sdcardFs.getBlockCount() * sdcardFs.getBlockSize();
    }

    private static StatFs doGetSDcardFs() {
        File sdcardFile = getSDCardFile();
        if (sdcardFile == null) return null;
        return new StatFs(sdcardFile.getAbsolutePath());
    }

    public static float getSystemFreeSizeKB() {
        StatFs dataFs = doGetSystemFs();
        return ((float) dataFs.getAvailableBlocks() / 1024) * ((float) dataFs.getBlockSize());
    }

    public static long getSystemFreeSize() {
        StatFs dataFs = doGetSystemFs();
        return (long) dataFs.getAvailableBlocks() * dataFs.getBlockSize();
    }

    public static long getSystemTotalSize() {
        StatFs dataFs = doGetSystemFs();
        return (long) dataFs.getBlockCount() * dataFs.getBlockSize();
    }

    private static StatFs doGetSystemFs() {
        return new StatFs(Environment.getDataDirectory().getAbsolutePath());
    }

    public static float getAvailMemMB(Context context) {
        return (float) getAvailMem(context) / MB_MULTIPLES;
    }

    public static long getAvailMem(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        MemoryInfo info = new MemoryInfo();
        am.getMemoryInfo(info);
        return info.availMem;
    }

    public static long getUsedMem(Context context) {
        try {
            return getTotalMem() - getAvailMem(context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static synchronized long getTotalMem() throws IOException {
        if (totalMen == -1) {
            String line = null;
            //Process process = Runtime.getRuntime().exec("cat /proc/meminfo"); //会无响应
            BufferedReader br = null;
            try {
                //br = new BufferedReader(new InputStreamReader(process.getInputStream()), 128);
                br = new BufferedReader(new FileReader("/proc/meminfo"));
                line = br.readLine();
                Pattern pattern = Pattern.compile("\\d+");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    int value = Integer.parseInt(matcher.group());
                    totalMen = value * KB_MULTIPLES;
                } else {
                    totalMen = 0;
                }
            } finally {
                if (br != null) br.close();
                //process.destroy();
            }
        }
        return totalMen;
    }

    /**
     * 获取屏幕分辨率(像素点)
     *
     * @param context
     * @return
     */
    public static int[] getScreenSize(Context context) {
        DisplayMetrics metrics = getDisplayMetrics(context);
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }

    /**
     * 返回原尺寸的DisplayMetrics，4.0默认会减掉通知栏部分，故要作处理
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        Display display = wm.getDefaultDisplay();

        //4.0之前的SDK直接返回当前metric
        if (Env.getSDKLevel() < Env.ANDROID_4_0) {
            return metric;
        }

        int rawWidth = metric.widthPixels;
        int rawHeight = metric.heightPixels;
        try {
            Method mGetRawH = Display.class.getMethod("getRawHeight");
            Method mGetRawW = Display.class.getMethod("getRawWidth");
            rawWidth = (Integer) mGetRawW.invoke(display);
            rawHeight = (Integer) mGetRawH.invoke(display);
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        metric.widthPixels = rawWidth;
        metric.heightPixels = rawHeight;

        return metric;
    }

    /**
     * 获取屏幕高低分
     *
     * @param context
     * @return
     */
    public static int getDisplayDensityDpi(Context context) {
        return getDisplayMetrics(context).densityDpi;
    }

    /**
     * 是否为低分
     *
     * @param context
     * @return
     */
    public static boolean isLowDensityDpi(Context context) {
        return getDisplayDensityDpi(context) == DisplayMetrics.DENSITY_LOW;
    }

    /**
     * 获取屏幕物理尺寸
     *
     * @param context
     * @return 单位为英寸
     */
    public static String getInchScreenSize(Context context) {
        DisplayMetrics metric = getDisplayMetrics(context);
        float inchf = getInchScreenSize(metric);
        try {
            DecimalFormat decimalFormat = new DecimalFormat(".0");
            String inch = decimalFormat.format(inchf);
            return inch;
        }catch(Exception e) {
            return Float.toString(inchf);
        }
    }

    private static float getInchScreenSize(DisplayMetrics metric) {
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素） 
//	    float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5） 
//	    int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 16
        float xin = width / metric.xdpi;
        float yin = height / metric.ydpi;
        float inch = (float) Math.sqrt(xin * xin + yin * yin);

        return inch;
    }

    /**
     * 屏幕尺寸 > 5，并且分辨率 > 800 * 480 以上 认为是平板设备
     *
     * @param context
     * @return
     */
    public static boolean isPadDevice(Context context) {
        DisplayMetrics metric = getDisplayMetrics(context);

        float inch = getInchScreenSize(metric);
        if (inch > 5f && (metric.widthPixels * metric.heightPixels > 800 * 480)) return true;
        return false;
    }

    /**
     * 根据pid获取包名
     *
     * @param context
     * @param pid
     * @return
     */
    public static String getPackageNameByPid(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runnings = am.getRunningAppProcesses();
        for (RunningAppProcessInfo ra : runnings) {
            if (ra.pid == pid) {
                String pn = ra.processName;
                if (pn.indexOf(":") > -1) {
                    return pn.split(":")[0];
                }
                return pn;
            }
        }
        return null;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @param context
     * @return 0:wifi 1:2g/3g 2:no net
     */
    public static int getNetWorkState(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(conMan == null) return 0;
        NetworkInfo wifiInfo = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isAvailable()) {
            return 0;
        }
        if (mobileInfo != null && mobileInfo.isAvailable()) {
            return 1;
        }
        return 2;
    }

    public static int getStatusBarHeight(Activity acitvity) {
        //4.4发的版本发现sansumg的机子可能在此处发生的崩溃特别多
        //故加try-catch
        try {
            Rect visiableRect = new Rect();
            acitvity.getWindow().getDecorView().getWindowVisibleDisplayFrame(visiableRect);
            return visiableRect.top;
        } catch (Exception e) {
            return 0;
        }
    }


}

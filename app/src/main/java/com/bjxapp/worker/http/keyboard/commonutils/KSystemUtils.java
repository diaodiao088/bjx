/**
 * @brief Package com.ijinshan.browser.utils
 * @author zhouchenguang
 * @version 1.0.0.0
 * @date 2012-12-23
 * @since 1.0.0.0
 */

package com.bjxapp.worker.http.keyboard.commonutils;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.bjxapp.worker.http.keyboard.commonutils.perference.SystemProperties;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class KSystemUtils {
    private static final String TAG = "KSystemUtils";
    private static int sScreenWidth = 480;
    private static int sScreenHeight = 800;
    private static String sScreenVga = "";
    private static float sDENSITY = 1;
    private static int sNavigationBarHeight = Integer.MIN_VALUE;
    private static Context sContext;

    public static void init(final Context context) {
        sContext = context;
    }

    public static void initSysSettings(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            sScreenWidth = dm.heightPixels;
            sScreenHeight = dm.widthPixels;
        } else {
            sScreenWidth = dm.widthPixels;
            sScreenHeight = dm.heightPixels;
        }
        sDENSITY = dm.density;
    }

    public static void initSysSettings(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService("window");
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        if (context.getResources() != null && context.getResources().getConfiguration().orientation == 2) {
            sScreenWidth = dm.heightPixels;
            sScreenHeight = dm.widthPixels;
        } else {
            sScreenWidth = dm.widthPixels;
            sScreenHeight = dm.heightPixels;
        }

        sDENSITY = dm.density;
    }

    public static float getDesity() {
        return sDENSITY;
    }

    public static int getScreenWidth() {
        return sScreenWidth;
    }

    public static int getScreenHeight() {
        return sScreenHeight;
    }

    public static int getSdkVersion() {
        return VERSION.SDK_INT;
    }

    public static String getScreenVga() {
        if (TextUtils.isEmpty(sScreenVga)) {
            StringBuilder vga = new StringBuilder(String.valueOf(getScreenWidth()));
            vga.append("x");
            vga.append(String.valueOf(getScreenHeight()));
            sScreenVga = vga.toString();
        }

        return sScreenVga;
    }

    /**
     * 由于线程竞争或进程竞争， 某些版本的android系统上 context.getFilesDir() 创建目录会失败，并返回null，
     * 为解决该问题， 当发现返回值为null时，try again。
     *
     * @param context
     * @return files directory
     */
    public static File getFilesDir(Context context) {
        File filesDir = context.getFilesDir();
        return filesDir != null ? filesDir : context.getFilesDir();
    }

    public static String getSdcardStorePath() {
        String path = KSystemUtils.getInternalSdcardPath(sContext); // 优先使用内部SD卡存储
        if (TextUtils.isEmpty(path)) {
            path = KSystemUtils.getExternalSdcardPath(sContext);
        }
        return path;
    }

    // -------------------------------------------------------------------------

    /**
     * @brief Get internal sdcard path.
     * @par Sync (or) Async: This is a Synchronous function.
     * @return path for internal sdcard. \n
     * @author huangzongming
     * @since 1.0.0.0
     * @version 1.0.0.0
     * @par Prospective Clients: External Classes
     */
    private static String getInternalSdcardPath(Context context) {
        String path = null;
        if (getSdkVersion() >= 14) {
            StorageManager mStorageManager = null;
            mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            String[] storagePathList = ReflectUtil.invoke("getVolumePaths", mStorageManager, String[].class, null, null);
            if (storagePathList != null) {
                if (storagePathList.length >= 1) {
                    if (isSDCardMount(context, storagePathList[0])) {
                        path = storagePathList[0];
                    }
                }
            }
        } else {
            // for lower than android 4.0 , still using /mnt/sdcard
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return path;
    }

    // -------------------------------------------------------------------------

    /**
     * @brief Get external sdcard path.
     * @par Sync (or) Async: This is a Synchronous function.
     * @return path for external sdcard. \n
     * @author huangzongming
     * @since 1.0.0.0
     * @version 1.0.0.0
     * @par Prospective Clients: External Classes
     */
    private static String getExternalSdcardPath(Context context) {
        String path = null;
        if (getSdkVersion() >= 14) {
            StorageManager mStorageManager = null;
            mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            String[] storagePathList = ReflectUtil.invoke("getVolumePaths", mStorageManager, String[].class, null, null);
            if (storagePathList != null) {
                if (storagePathList.length >= 2) {
                    if (isSDCardMount(context, storagePathList[1])) {
                        path = storagePathList[1];
                    }
                }
            }
        } else {
            // for lower than android 4.0 , still using /mnt/sdcard
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return path;
    }

    /**
     * 一张SD卡的相关信息
     *
     * @author caisenchuan
     */
    public static class SDCard {
        private String path;
        private boolean mount;
        private boolean removeable;

        public SDCard(int index, String path, boolean mount, boolean removeable) {
            this.path = path;
            this.mount = mount;
            this.removeable = removeable;
        }

        /**
         * 获取SD卡的名字
         */
//        public String getName(Context context) {
//            String ret = "";
//
//            if (context != null) {
//                if (!removeable) {
//                    // 手机存储
//                    if (index > 1) {
//                        ret = String.format("%s%s",
//                                context.getString(R.string.s_download_innerSDCard), index);
//                    } else {
//                        ret = context.getString(R.string.s_download_innerSDCard);
//                    }
//                } else {
//                    // SD卡
//                    if (index > 1) {
//                        ret = String.format("%s%s",
//                                context.getString(R.string.s_download_outerSDCard), index);
//                    } else {
//                        ret = context.getString(R.string.s_download_outerSDCard);
//                    }
//                }
//            }
//
//            return ret;
//        }

        /**
         * @return the path
         */
        public String getPath() {
            return path;
        }

        /**
         * @return the mount
         */
        public boolean isMount() {
            return mount;
        }

        /**
         * @return the removeable
         */
        public boolean isRemoveable() {
            return removeable;
        }
    }

    // -------------------------------------------------------------------------

    /**
     * @brief Check sdcard whether mounted.
     * @par Sync (or) Async: This is a Synchronous function.
     * @return true if sdcard been mounted \n
     * @author huangzongming
     * @since 1.0.0.0
     * @version 1.0.0.0
     * @par Prospective Clients: External Classes
     */
    public static boolean isSDCardMount(Context context, String mountPoint) {
        if (mountPoint == null) {
            return false;
        }

        if (getSdkVersion() >= 14) {
            String state = null;
            StorageManager mStorageManager = null;
            mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            try {
                state = ReflectUtil.invoke("getVolumeState", mStorageManager, String.class, new Class[]{String.class}, new Object[]{mountPoint});
            } catch (IllegalArgumentException e) {
                return false;
            }
            return Environment.MEDIA_MOUNTED.equals(state);
        } else {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        }

    }

    public static boolean isSDCardMount() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    private static File getCacheDirForNet(Context context) {
        if (context != null) {
            return context.getCacheDir();
        } else {
            return Environment.getDownloadCacheDirectory();
        }
    }

    public static File getCacheDir(Context context) {
        File cacheDir = null;
        try {
            if (isSDCardMount()) {
                cacheDir = context.getExternalCacheDir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cacheDir == null) {
            cacheDir = Environment.getDownloadCacheDirectory();
        }

        return cacheDir;
    }

    public static File getCacheDirForNetwork(Context context) {
        File file = new File(getCacheDirForNet(context), "net_cache");
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File getDownloadDirForNetwork(Context context) throws Exception {
        File file = new File(getCacheDirForNet(context), "download");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    private static final String SCHEME = "package";
    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
     */
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
     */
    private static final String APP_PKG_NAME_22 = "pkg";

    private static final String ACTION_APPLICATION_DETAILS_SETTINGS_23 = "android.settings.APPLICATION_DETAILS_SETTINGS";
    /**
     * InstalledAppDetails所在包名
     */
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    /**
     * InstalledAppDetails类名
     */
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    /**
     * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level
     * 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
     *
     * @param packageName 应用程序的包名
     */
    public static Intent getPackageDetailsIntent(String packageName) {
        Intent intent = new Intent();
        int apiLevel = 0;
        try {
            apiLevel = VERSION.SDK_INT;
        } catch (Exception ex) {
            Log.w(TAG, "getPackageDetailsIntent: "+ex.getMessage());
        }
        if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            intent.setAction(ACTION_APPLICATION_DETAILS_SETTINGS_23);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
            // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
            final String appPkgName = (apiLevel > 7 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        return intent;
    }

    /**
     * 清除所有通知
     *
     * @author caisenchuan
     */
    public static void clearAllNotification(Context context) {
        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }


    // 判断APK是否安装
    // *** 如果安装了，但是已经被系统停用，也被认为是没有安装 ***
    public static boolean isAPPInstalled(Context context, String pkgName) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            PackageInfo info = pm.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                installed = info.applicationInfo.enabled;
            }
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public static boolean isAPPInstalled(Context context, String[] pkgName) {
        for (String pkg : pkgName) {
            if (isAPPInstalled(context, pkg)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isSdkVersionJellyBean() {
        return getSdkVersion() >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * @Note: 获取状态栏的高度
     */
    public static int getStatusBarHeight(Activity activity) {
        int height = 0;
        do {
            if (activity == null) {
                break;
            }
            if (activity.getWindow() == null) {
                break;
            }
            if (activity.getWindow().getDecorView() == null) {
                break;
            }
            Rect rect = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            height = rect.top;
        } while (false);

        return height;
    }

    /**
     * 获取导航栏高度
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        if (sNavigationBarHeight != Integer.MIN_VALUE) {
            return sNavigationBarHeight;
        }
        synchronized (KSystemUtils.class) {
            if (sNavigationBarHeight == Integer.MIN_VALUE) {
                sNavigationBarHeight = getNavigationBarHeightInternal(context);
            }
        }
        return sNavigationBarHeight;
    }

    private static int getNavigationBarHeightInternal(Context context) {
        if (isExceptProcessNavigationBar()) {
            return 0;
        }
        return getNavigationHeightFromResource(context);
    }

    private static int getNavigationHeightFromResource(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int navigationBarHeight = 0;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("config_showNavigationBar",
                "bool", "android");
        if (resourceId > 0) {
            boolean hasNav = resources.getBoolean(resourceId);
            if (hasNav) {
                resourceId = resources.getIdentifier("navigation_bar_height",
                        "dimen", "android");
                if (resourceId > 0) {
                    navigationBarHeight = resources
                            .getDimensionPixelSize(resourceId);
                }
            }
        }

        if (navigationBarHeight <= 0) {
            DisplayMetrics dMetrics = new DisplayMetrics();
            display.getMetrics(dMetrics);
            int screenHeight = Math.max(dMetrics.widthPixels, dMetrics.heightPixels);
            int realHeight = 0;
            try {
                Method mt = display.getClass().getMethod("getRealSize", Point.class);
                Point size = new Point();
                mt.invoke(display, size);
                realHeight = Math.max(size.x, size.y);
            } catch (NoSuchMethodException e) {
                Method mt = null;
                try {
                    mt = display.getClass().getMethod("getRawHeight");
                } catch (NoSuchMethodException e2) {
                    try {
                        mt = display.getClass().getMethod("getRealHeight");
                    } catch (NoSuchMethodException e3) {
                        if (KLog.sDEBUG) {
                            KLog.w(TAG, "getScreenRealHeight Failed: ", e3);
                        }
                    }
                }
                if (mt != null) {
                    try {
                        realHeight = (Integer) mt.invoke(display);
                    } catch (Exception e1) {
                        if (KLog.sDEBUG) {
                            KLog.w(TAG, "getScreenRealHeight Failed: ", e1);
                        }
                    }
                }
            } catch (Exception e) {
                if (KLog.sDEBUG) {
                    KLog.w(TAG, "getScreenRealHeight Failed: ", e);
                }
            }
            navigationBarHeight = realHeight - screenHeight;
        }

        return navigationBarHeight;
    }


    private static boolean isExceptProcessNavigationBar() {
        String deviceModel = SystemProperties.get("ro.product.model", "unknown");
        if (deviceModel.equals("ZTE U950") || deviceModel.equals("ZTE U817") || deviceModel.equals("ZTE V955")
                || deviceModel.equals("GT-S5301L")
                || deviceModel.equals("LG-E425f") || deviceModel.equals("GT-S5303B")
                || deviceModel.equals("I-STYLE2.1") || deviceModel.equals("SCH-S738C")
                || deviceModel.equals("S120 LOIN") || deviceModel.equals("START 765")
                || deviceModel.equals("LG-E425j") || deviceModel.equals("Archos 50 Titanium")
                || deviceModel.equals("ZTE N880G") || deviceModel.equals("O+ 8.91")
                || deviceModel.equals("ZP330") || deviceModel.equals("Wise+")
                || deviceModel.equals("HUAWEI Y511-U30") || deviceModel.equals("Che1-L04")
                || deviceModel.equals("ASUS_T00I") || deviceModel.equals("Lenovo A319")
                || deviceModel.equals("Bird 72_wet_a_jb3") || deviceModel.equals("Sendtel Wise")
                || deviceModel.equals("cross92_3923") || deviceModel.equals("HTC X920e")
                || deviceModel.equals("ONE TOUCH 4033X") || deviceModel.equals("GSmart Roma")
                || deviceModel.equals("A74B") || deviceModel.equals("Doogee Y100 Pro")
                || deviceModel.equals("M4 SS1050") || deviceModel.equals("Ibiza_F2")
                || deviceModel.equals("Lenovo P70-A") || deviceModel.equals("Y635-L21")
                || deviceModel.equals("hi6210sft") || deviceModel.equals("TurboX6Z")
                || deviceModel.equals("ONE TOUCH 4015A") || deviceModel.equals("LENNY2")
                || deviceModel.equals("A66A*") || deviceModel.equals("ONE TOUCH 4033X")
                || deviceModel.equals("LENNY2") || deviceModel.equals("PGN606")
                || deviceModel.equals("MEU AN400") || deviceModel.equals("ONE TOUCH 4015X")
                || deviceModel.equals("4013M") || deviceModel.equals("n625ab")) {
            return true;
        }
        if ("OPPO".equals(Build.MANUFACTURER) || "Meizu".equals(Build.MANUFACTURER)) {
            return true;
        }
        return false;
    }

    /**
     * InputMethodManager内存泄露
     * http://blog.csdn.net/sodino/article/details/32188809
     * @param destContext
     */
    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object objGet = null;
        for (int i = 0; i < arr.length; i++) {
            String param = arr[i];
            try {
                f = imm.getClass().getDeclaredField(param);
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                } // author: sodino mail:sodino@qq.com
                objGet = f.get(imm);
                if (objGet != null && objGet instanceof View) {
                    View vGet = (View) objGet;
                    if (vGet.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
                        f.set(imm, null); // 置空，破坏掉path to gc节点
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

}


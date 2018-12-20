package com.bjxapp.worker.exception;

import android.content.Context;
import android.os.Environment;

import com.bjx.master.BuildConfig;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zhangdan on 2018/12/17.
 * comments:
 */

public class MyCrashHandler implements Thread.UncaughtExceptionHandler {

    private static MyCrashHandler mInstance;

    private static Thread.UncaughtExceptionHandler mOldHandler;

    private static final String CRASH_LOG_PATH = "/data/data/"
            + BuildConfig.APPLICATION_ID + "/app_log/";

    boolean mRegisted = false;

    private static String mLogPath;

    public synchronized static MyCrashHandler getInstance() {
        if (mInstance == null) {
            mInstance = new MyCrashHandler();
            mOldHandler = Thread.getDefaultUncaughtExceptionHandler();
        }

        return mInstance;
    }

    public void register(Context ctx) {
        if (!mRegisted) {
            mRegisted = true;
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
    }


    @Override
    public void uncaughtException(Thread t, Throwable e) {

        doCaughtException(e);

        if (mOldHandler != null) {
            mOldHandler.uncaughtException(t, e);
        }
    }

    public void doCaughtException(Throwable ex) {
        outputCrashLog(ex, null, false);
    }

    private void outputCrashLog(Throwable ex, Object o, boolean b) {
        File f = new File(getCrashFileName());
        if (f != null) {
            FileWriter fw = null;
            PrintWriter pw = null;
            try {
                fw = new FileWriter(f);
                if (fw != null) {
                    fw.write("\n\n----exception localized message----\n");
                    String s = ex.getLocalizedMessage();
                    if (s != null) {
                        fw.write(s);
                    }

                    fw.write("\n\n----exception stack trace----\n");

                    pw = new PrintWriter(fw);
                    if (pw != null) {
                        Throwable c = ex;

                        // StackTraceElement[] crashCheckElements = null;

                        while (c != null) {
                            c.printStackTrace(pw);

                            StringWriter errors = new StringWriter();
                            c.printStackTrace(new PrintWriter(errors));

                            // crashCheckElements = c.getStackTrace();
                            c = c.getCause();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeSilently(pw);
                closeSilently(fw);
            }
        }
    }

    public static void closeSilently(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String getCrashFileName() {
        File f = new File(getLogPath());
        if (f != null && !f.exists()) {
            f.mkdir();
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
        String s = df.format(new Date());
        return getLogPath() + "bjx" + "_" + s
                + ".txt";
    }

    private static boolean mInitLogPath = false;

    public String getLogPath() {
        if (!mInitLogPath) {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                String sdcardPath = Environment.getExternalStorageDirectory().getPath() + "/" +
                        "bjx" + "/";
                File fileSdDir = new File(sdcardPath);
                fileSdDir.mkdir();
                if (!fileSdDir.exists()) {
                    mLogPath = CRASH_LOG_PATH;
                } else {
                    mLogPath = sdcardPath;
                }
            } else {
                mLogPath = CRASH_LOG_PATH;
            }

            mInitLogPath = true;
        }

        return mLogPath;
    }

}

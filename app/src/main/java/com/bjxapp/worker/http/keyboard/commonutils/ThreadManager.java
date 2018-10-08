package com.bjxapp.worker.http.keyboard.commonutils;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

public final class ThreadManager {

    public static final boolean DEBUG = CommonUtilsEnv.sDEBUG;

    public static final int THREAD_UI = 0;
    public static final int THREAD_CLOUD = 1;
    public static final int THREAD_SYNC = 2;
    /**
     * ONLY for accessibility and preference DB
     */
    public static final int THREAD_DB = 3;
    public static final int THREAD_ACCESSIBILITY = 4;
    public static final int THREAD_AD_LOAD = 5;

    private static final String[] THREAD_NAME_LIST = new String[]{"THREAD_UI", "THREAD_CLOUD", "THREAD_SYNC", "THREAD_DB", "THREAD_ACCESSIBILITY", "THREAD_AD_LOAD"};
    private static final Handler[] HANDLER_LIST = new Handler[THREAD_NAME_LIST.length];

    private ThreadManager() {
    }

    static void startup() {
        synchronized (HANDLER_LIST) {
            HANDLER_LIST[0] = new Handler(Looper.getMainLooper());
        }
    }

    public static void post(int index, Runnable r) {
        postDelayed(index, r, 0L);
    }

    public static void postDelayed(int index, Runnable r, long delayMillis) {
        getHandler(index).postDelayed(r, delayMillis);
    }

    public static void removeCallbacks(int index, Runnable r) {
        if (index < THREAD_NAME_LIST.length) {
            getHandler(index).removeCallbacks(r);
        }
    }

    public static Handler getHandler(int index) {
        if (index < 0 || index >= THREAD_NAME_LIST.length) {
            throw new IllegalArgumentException("Index " + index + " is invalid");
        }

        if (index == 0) {
            return HANDLER_LIST[0];
        }

        synchronized (HANDLER_LIST) {
            if (HANDLER_LIST[index] != null) {
                return HANDLER_LIST[index];
            }
            HandlerThread thread = new HandlerThread(THREAD_NAME_LIST[index]);
            thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
            thread.start();
            Handler handler = new Handler(thread.getLooper());
            HANDLER_LIST[index] = handler;
        }
        return HANDLER_LIST[index];
    }

    public static boolean runningOn(int index) {
        return getHandler(index).getLooper() == Looper.myLooper();
    }

    public static void currentlyOn(int index) {
        if (DEBUG && Thread.currentThread().getId() != getHandler(index).getLooper().getThread().getId()) {
            throw new RuntimeException("The current thread is out of line with expectations!");
        }
    }

    /**
     * 键盘进程中，在LatinIME的onDestroy时机中清空所有消息；
     * 因为ThreadManager作用域是进程全局，所以在其他进程或场景，
     * 慎重removeCallbacksAndMessages，需要时自行removeCallback或removeMessage。
     */
    public static void terminate() {
        synchronized (HANDLER_LIST) {
            for (Handler handler : HANDLER_LIST) {
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                }
            }
        }
    }
}

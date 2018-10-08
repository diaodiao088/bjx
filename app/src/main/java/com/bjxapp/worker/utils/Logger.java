package com.bjxapp.worker.utils;

import android.util.Log;

import com.bjxapp.worker.global.Constant;

public class Logger {

    public static void i(String msg) {
        if (BuildConfig.DEBUG)
            Log.i(Constant.APP_LOG_TAG, msg);
    }

    public static void e(String msg) {
        if (BuildConfig.DEBUG)
            Log.e(Constant.APP_LOG_TAG, msg);
    }

    public static void e(Object msg) {
        if (BuildConfig.DEBUG)
            if (msg != null && BuildConfig.DEBUG) {
                e(msg.toString());
            }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG)
            Log.e(tag, msg);
    }

    public static void e(String msg, Throwable t) {
        if (BuildConfig.DEBUG)
            Log.e(Constant.APP_LOG_TAG, msg, t);
    }

    public static void e(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG)
            Log.e(tag, msg, t);
    }

    public static void w(String msg) {
        if (BuildConfig.DEBUG)
            Log.w(Constant.APP_LOG_TAG, msg);
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG)
            Log.w(tag, msg);
    }

    public static void w(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG)
            Log.w(tag, msg, t);
    }

    public static void w(String tag, Throwable t) {
        if (BuildConfig.DEBUG)
            Log.w(tag, t);
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG)
            Log.i(tag, msg);
    }

    public static void v(String tag, String s) {
        if (BuildConfig.DEBUG)
            Log.v(tag, s);
    }
}

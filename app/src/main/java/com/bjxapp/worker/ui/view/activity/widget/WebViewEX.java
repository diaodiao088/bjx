package com.bjxapp.worker.ui.view.activity.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by zhangdan on 2018/11/28.
 * comments:
 */

public class WebViewEX extends WebView {

    @SuppressLint("NewApi")
    public WebViewEX(Context context) {
        super(context);
        if (Build.VERSION.SDK_INT > 10 && Build.VERSION.SDK_INT < 17) {
            removeJavascriptInterface("searchBoxJavaBridge_");
            removeJavascriptInterface("accessibility");
            removeJavascriptInterface("accessibilityTraversal");
        }

    }

    @SuppressLint("NewApi")
    public WebViewEX(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (Build.VERSION.SDK_INT > 10 && Build.VERSION.SDK_INT < 17) {
            removeJavascriptInterface("searchBoxJavaBridge_");
            removeJavascriptInterface("accessibility");
            removeJavascriptInterface("accessibilityTraversal");
        }
    }

    @SuppressLint("NewApi")
    public WebViewEX(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (Build.VERSION.SDK_INT > 10 && Build.VERSION.SDK_INT < 17) {
            removeJavascriptInterface("searchBoxJavaBridge_");
            removeJavascriptInterface("accessibility");
            removeJavascriptInterface("accessibilityTraversal");
        }
    }

    public void resumeTimersAndClearSsl() {
        // NOTE: WebView#pauseTimers() is a global requests. We need to resumeTimers here in case the timer is paused.
        // http://developer.android.com/intl/zh-cn/reference/android/webkit/WebView.html#pauseTimers()
        resumeTimers();
        // Fallback solution, clear ssl preference again for Android 4.3
        clearSslPreferences();
    }

}

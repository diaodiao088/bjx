package com.bjxapp.worker.ui.view.activity.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bjxapp.worker.App;
import com.bjxapp.worker.utils.BuildConfig;

public class DialogWindow {

    private final static String TAG = "DialogWindow";

    private Handler mHandler;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private View mView;
    private boolean mShow = false;
    private DialogInterface.OnDismissListener mOnDismissListener;

    private static DialogWindow sINS;

    public static DialogWindow getIns() {

        if (sINS == null) {
            sINS = new DialogWindow();
        }

        return sINS;
    }

    private DialogWindow() {
        mShow = false;
        mWindowManager = (WindowManager) App.getInstance().getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mLayoutParams.gravity = Gravity.CENTER;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        mLayoutParams.dimAmount = 0.7f;
        mLayoutParams.windowAnimations = android.R.style.Animation_Dialog;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public Context getContext() {
        return App.getInstance();
    }

    public synchronized void show(final View contentView) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (BuildConfig.DEBUG) {
                    if (Looper.getMainLooper() != Looper.myLooper()) {
                        throw new RuntimeException("Must show in main thread");
                    }
                }
                if (!mShow) {
                    if (mWindowManager != null && mLayoutParams != null) {
                        try {
                            try {
                                if (mView != null) {
                                    mWindowManager.removeView(mView);
                                    mView = null;
                                    mOnDismissListener = null;
                                }
                            } catch (Exception e) {
                                Log.w(TAG, "run: " + e.getMessage());
                            }
                            updateWindowType();
                            mView = contentView;

                            mWindowManager.addView(mView, mLayoutParams);
                            mShow = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void updateWindowType() {
        if (null == mLayoutParams) {
            return;
        }

        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
    }

    public synchronized void showAtPosition(final View contentView, final int gravity, int x, int y) {
        mLayoutParams.gravity = gravity;
        mLayoutParams.x = x;
        mLayoutParams.y = y;
        show(contentView);
    }

    public void setOrientation(int orientation) {
        mLayoutParams.screenOrientation = orientation;
    }

    public synchronized void hide(final DialogInterface dialog) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (BuildConfig.DEBUG) {
                    if (Looper.getMainLooper() != Looper.myLooper()) {
                        throw new RuntimeException("Must remove in main thread");
                    }
                }
                if (mShow) {
                    if (mView != null && mWindowManager != null) {
                        if (mOnDismissListener != null) {
                            mOnDismissListener.onDismiss(dialog);
                        }
                        try {
                            mWindowManager.removeView(mView);
                            mShow = false;
                            mView = null;
                            mOnDismissListener = null;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public boolean isShowing() {
        return mShow;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        mOnDismissListener = listener;
    }
}

package com.bjxapp.worker.ui.view.activity.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bjxapp.worker.ui.widget.DimenUtils;

public class ShowDialog extends Dialog {
    private static final String TAG = "ShowDialog";
    private Window mWindow = null;
    private View mView = null;
    private boolean mIsDismiss = true;
    private boolean mIsFullScreenMode = false;
    private int mFullScreenLayerColor = Color.TRANSPARENT;
    private Context mContext = null;
    private boolean mNonActivityLevelDialog = false;
    private int dialogMargin = 10;

    private int mWindowType;

    public ShowDialog(Context context, int theme, View view) {
        super(context, theme);
        mView = view;
        mWindow = getWindow();
        mContext = context;
    }

    public void setDialogSize(int width, int height) {
        if (mWindow != null) {
            WindowManager.LayoutParams lp = mWindow.getAttributes();
            lp.width = width;
            if (height > 0) {
                lp.height = height;
            }
            mWindow.setAttributes(lp);
        }
    }

    public ShowDialog(Context context, int theme, View view, boolean isDismiss) {
        super(context, theme);
        mView = view;
        mWindow = getWindow();
        mContext = context;
        mIsDismiss = isDismiss;
    }

    public void setDialogMargin(int margin) {
        dialogMargin = margin;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (mIsFullScreenMode) {
            mWindow.setBackgroundDrawable(new ColorDrawable(mFullScreenLayerColor));
        }
        setContentView(mView);
        try {
            int ScreenWidth = DimenUtils.getScreenWidth(mContext)
                    - DimenUtils.dp2px(dialogMargin, mContext) * 2;
            setDialogSize(ScreenWidth, 0);
        } catch (Exception e) {
            Log.w(TAG, "onCreate: " + e.getMessage());
        }
        if (!mIsDismiss) {
            setCanceledOnTouchOutside(false);
        }
    }

    // 设置显示位置
    public void setPosition(int gravity, int x, int y) {
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.x = x;
        lp.y = y;
        lp.gravity = gravity;
        mWindow.setAttributes(lp);
    }

    public void setFullScreenLayer(int backgroundColor) {
        mFullScreenLayerColor = backgroundColor;
        mIsFullScreenMode = true;
    }

    // 设置动画效果
    public void setAnimation(int resId) {
        mWindow.setWindowAnimations(resId);
    }

    @Override
    public void show() {

        if (!mNonActivityLevelDialog && !isActivityValid()) {
            return;
        }

        try {
            super.show();
        } catch (Throwable e) {
            Log.w(TAG, "show: " + e.getMessage());
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
            Log.w(TAG, "dismiss: " + e.getMessage());
        }
    }

    // ///////////////////////////////////
    // /< protected
    protected boolean isActivityValid() {
        if (null != mContext && mContext instanceof Activity) {
            Activity at = (Activity) mContext;
            if (at.isFinishing()) {
                // /< 是 activity，但已finish
                return false;
            } else {
                // /< 是activity，还在运行中...
                return true;
            }
        } else {
            // /< context无效 或 context不是有效的activity
            return false;
        }
    }

    public void setWindowType(int windowType) {
        if (windowType == WindowManager.LayoutParams.TYPE_PHONE
                || windowType == WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                || windowType == WindowManager.LayoutParams.TYPE_TOAST) {
            mNonActivityLevelDialog = true;
        } else {
            mNonActivityLevelDialog = false;
        }
        mWindowType = windowType;
        mWindow.setType(windowType);
    }
}

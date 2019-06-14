package com.bjxapp.worker.ui.view.activity.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.ui.widget.DimenUtils;

;

/**
 * Created by zhangdan on 2018/11/7.
 * comments:
 */

public class PermissionAlertDialog {

    private Context mCtx;

    private CustomLayoutDialog mDialog = null;

    private TextView mTitleTv;
    private TextView mContentTv;
    private TextView mCancelTv;
    private TextView mOkTv;
    private View mRootView;

    private LinearLayout mMessageLayout;

    public PermissionAlertDialog(Context ctx) {
        this.mCtx = ctx;
        mDialog = new CustomLayoutDialog(ctx, R.layout.permission_alert_dialog);
        initView();
    }

    private void initView() {
        mRootView = mDialog.getView();
        if (mRootView != null) {
            mTitleTv = mRootView.findViewById(R.id.dialog_title_tv);
            mContentTv = mRootView.findViewById(R.id.content_tv);
            mCancelTv = mRootView.findViewById(R.id.dialog_cancel_tv);
            mOkTv = mRootView.findViewById(R.id.dialog_confirm_tv);
            mMessageLayout = mRootView.findViewById(R.id.content_layout);
        }
    }

    public void addMessageLayout(View view, boolean removeTopMargin, boolean removeBottomMargin) {

        if (mMessageLayout != null) {
            mMessageLayout.addView(view , new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , DimenUtils.dp2px(200 , mMessageLayout.getContext())));
            mMessageLayout.setVisibility(View.VISIBLE);
            mContentTv.setVisibility(View.GONE);
        }

        /*ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mMessageLayout.getLayoutParams();
        if (params != null) {
            params.topMargin = removeTopMargin ? 0 : params.topMargin;
            params.bottomMargin = removeBottomMargin ? 0 : params.bottomMargin;
        }*/
    }

    public PermissionAlertDialog setOnNegativeListener(int resId, View.OnClickListener listener) {

        if (resId > 0) {
            String text = mCtx.getResources().getString(resId);

            mCancelTv.setText(text);
        }

        mCancelTv.setOnClickListener(listener);

        return this;
    }

    public PermissionAlertDialog setOnNegativeListener(String resId, View.OnClickListener listener) {


        mCancelTv.setText(resId);

        mCancelTv.setOnClickListener(listener);

        return this;
    }

    public PermissionAlertDialog setOnPositiveListener(String resId, View.OnClickListener listener) {


        mOkTv.setText(resId);
        mOkTv.setOnClickListener(listener);

        return this;
    }

    public PermissionAlertDialog setOnPositiveListener(int resId, View.OnClickListener listener) {

        if (resId > 0) {
            String text = mCtx.getResources().getString(resId);

            mOkTv.setText(text);
        }

        mOkTv.setOnClickListener(listener);

        return this;
    }


    public PermissionAlertDialog setOnCancelListener(DialogInterface.OnCancelListener listener) {
        mDialog.setOnCancelListener(listener);
        return this;
    }

    public PermissionAlertDialog setCancelable(boolean cancelable){
        mDialog.setCancelable(cancelable);
        return this;
    }

    public PermissionAlertDialog setTitle(int resId) {

        mTitleTv.setText(mCtx.getResources().getString(resId));

        return this;
    }

    public PermissionAlertDialog setTitle(String txt) {

        mTitleTv.setText(txt);

        return this;
    }

    public PermissionAlertDialog setContent(int resId) {

        mContentTv.setText(mCtx.getResources().getString(resId));

        return this;
    }

    public PermissionAlertDialog setContent(String content) {
        if (!TextUtils.isEmpty(content)) {
            mContentTv.setText(content);
        }

        return this;
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public boolean isShow() {
        if (mDialog == null)
            return false;
        return mDialog.isShowing();
    }


    public PermissionAlertDialog setContentVisible(int visibility) {
        mContentTv.setVisibility(visibility);
        return this;
    }

    public PermissionAlertDialog setTitleVisible(int visibility) {
        mTitleTv.setVisibility(visibility);
        return this;
    }


}

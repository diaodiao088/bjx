package com.bjxapp.worker.ui.view.activity.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bjx.master.R;

/**
 * Created by zhangdan on 2018/11/7.
 * comments:
 */

public class DeviceConfirmDialog {

    private Context mCtx;

    private CustomLayoutDialog mDialog = null;

    private TextView mNormalTv;
    private TextView mUnNormalTv;
    private TextView mCancelTv;
    private View mRootView;


    public DeviceConfirmDialog(Context ctx) {
        this.mCtx = ctx;
        mDialog = new CustomLayoutDialog(ctx, R.layout.device_info_select_dialog);
        initView();
    }

    private void initView() {
        mRootView = mDialog.getView();
        if (mRootView != null) {
            mNormalTv = mRootView.findViewById(R.id.status_normal);
            mUnNormalTv = mRootView.findViewById(R.id.status_un_normal);
            mCancelTv = mRootView.findViewById(R.id.status_cancel);
        }
    }

    public DeviceConfirmDialog setOnNegativeListener(int resId, View.OnClickListener listener) {

        if (resId > 0) {
            String text = mCtx.getResources().getString(resId);

            mCancelTv.setText(text);
        }

        mCancelTv.setOnClickListener(listener);

        return this;
    }

    public DeviceConfirmDialog setOnNegativeListener(String resId, View.OnClickListener listener) {


        mCancelTv.setText(resId);

        mCancelTv.setOnClickListener(listener);

        return this;
    }


    public DeviceConfirmDialog setOnCancelListener(DialogInterface.OnCancelListener listener) {
        mDialog.setOnCancelListener(listener);
        return this;
    }

    public DeviceConfirmDialog setCancelable(boolean cancelable) {
        mDialog.setCancelable(cancelable);
        return this;
    }

    public DeviceConfirmDialog setTitle(int resId) {

        mNormalTv.setText(mCtx.getResources().getString(resId));

        return this;
    }

    public DeviceConfirmDialog setTitle(String txt) {

        mNormalTv.setText(txt);

        return this;
    }

    public DeviceConfirmDialog setContent(int resId) {

        mUnNormalTv.setText(mCtx.getResources().getString(resId));

        return this;
    }

    public DeviceConfirmDialog setContent(String content) {
        if (!TextUtils.isEmpty(content)) {
            mUnNormalTv.setText(content);
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


    public DeviceConfirmDialog setContentVisible(int visibility) {
        mUnNormalTv.setVisibility(visibility);
        return this;
    }

    public DeviceConfirmDialog setTitleVisible(int visibility) {
        mNormalTv.setVisibility(visibility);
        return this;
    }

    public DeviceConfirmDialog setNormalListener(View.OnClickListener listener) {

        mNormalTv.setOnClickListener(listener);

        return this;

    }

    public DeviceConfirmDialog setUnNormalListener(View.OnClickListener listener) {

        mUnNormalTv.setOnClickListener(listener);

        return this;

    }

    public DeviceConfirmDialog setCancelBtnListener(View.OnClickListener listener) {

        mCancelTv.setOnClickListener(listener);

        return this;

    }


}

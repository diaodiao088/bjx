package com.bjxapp.worker.ui.view.activity.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.model.OtherPriceBean;
import com.bjxapp.worker.ui.widget.DimenUtils;

/**
 * Created by zhangdan on 2018/11/7.
 * comments:
 */

public class AddOtherPriceDialog {

    private Context mCtx;

    private CustomLayoutDialog mDialog = null;

    private TextView mCancelTv;
    private TextView mOkTv;
    private View mRootView;

    private EditText mNameEv;
    private EditText mPriceEv;

    public AddOtherPriceDialog(Context ctx) {
        this.mCtx = ctx;
        mDialog = new CustomLayoutDialog(ctx, R.layout.other_price_dialog);
        initView();
    }

    private void initView() {
        mRootView = mDialog.getView();
        if (mRootView != null) {
            mCancelTv = mRootView.findViewById(R.id.dialog_cancel_tv);
            mOkTv = mRootView.findViewById(R.id.dialog_log_out_tv);

            mNameEv = mRootView.findViewById(R.id.name_tv);
            mPriceEv = mRootView.findViewById(R.id.price_tv);

        }
    }

    public void setPreviousData(OtherPriceBean otherPriceBean){

        if (otherPriceBean != null){
            mNameEv.setText(otherPriceBean.getName());
            mPriceEv.setText(otherPriceBean.getPrice());
        }

    }


    public AddOtherPriceDialog setOnNegativeListener(int resId, View.OnClickListener listener) {

        if (resId > 0) {
            String text = mCtx.getResources().getString(resId);

            mCancelTv.setText(text);
        }

        mCancelTv.setOnClickListener(listener);

        return this;
    }

    public AddOtherPriceDialog setOnNegativeListener(String resId, View.OnClickListener listener) {


        mCancelTv.setText(resId);

        mCancelTv.setOnClickListener(listener);

        return this;
    }

    public AddOtherPriceDialog setOnPositiveListener(String resId, View.OnClickListener listener) {


        mOkTv.setText(resId);
        mOkTv.setOnClickListener(listener);

        return this;
    }

    public AddOtherPriceDialog setOnPositiveListener(int resId, View.OnClickListener listener) {

        if (resId > 0) {
            String text = mCtx.getResources().getString(resId);

            mOkTv.setText(text);
        }

        mOkTv.setOnClickListener(listener);

        return this;
    }


    public AddOtherPriceDialog setOnCancelListener(DialogInterface.OnCancelListener listener) {
        mDialog.setOnCancelListener(listener);
        return this;
    }

    public AddOtherPriceDialog setCancelable(boolean cancelable) {
        mDialog.setCancelable(cancelable);
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

    public boolean isDataValid() {
        return !TextUtils.isEmpty(mNameEv.getText().toString()) && !TextUtils.isEmpty(mPriceEv.getText().toString());
    }

    public OtherPriceBean getValidBean() {
        OtherPriceBean otherPriceBean = new OtherPriceBean();
        otherPriceBean.setName(mNameEv.getText().toString());
        otherPriceBean.setPrice(mPriceEv.getText().toString());
        return otherPriceBean;
    }


}

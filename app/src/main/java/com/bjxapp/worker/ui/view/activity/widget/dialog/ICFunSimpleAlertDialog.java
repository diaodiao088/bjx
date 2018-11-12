package com.bjxapp.worker.ui.view.activity.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjx.master.R;;


/**
 * Created by zhangdan on 2018/9/27.
 * <p>
 * comments:
 */

public class ICFunSimpleAlertDialog {

    private Context mCtx;

    private CustomLayoutDialog mDialog = null;

    private TextView mTitleTv;
    private TextView mContentTv;
    private TextView mCancelTv;
    private TextView mOkTv;
    private View mRootView;

    private LinearLayout mMessageLayout;

    public ICFunSimpleAlertDialog(Context ctx) {
        this.mCtx = ctx;
        mDialog = new CustomLayoutDialog(ctx, R.layout.simple_alert_dialog);
        initView();
    }

    private void initView() {
        mRootView = mDialog.getView();
        if (mRootView != null) {
            mTitleTv = mRootView.findViewById(R.id.dialog_title_tv);
            mContentTv = mRootView.findViewById(R.id.content_tv);
            mCancelTv = mRootView.findViewById(R.id.dialog_cancel_tv);
            mMessageLayout = mRootView.findViewById(R.id.content_layout);
        }
    }

    public void addMessageLayout(View view, boolean removeTopMargin, boolean removeBottomMargin) {

        if (mMessageLayout != null) {
            mMessageLayout.addView(view);
            mMessageLayout.setVisibility(View.VISIBLE);
        }

        /*ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mMessageLayout.getLayoutParams();
        if (params != null) {
            params.topMargin = removeTopMargin ? 0 : params.topMargin;
            params.bottomMargin = removeBottomMargin ? 0 : params.bottomMargin;
        }*/
    }

    public ICFunSimpleAlertDialog setOnNegativeListener(View.OnClickListener listener){
        mCancelTv.setOnClickListener(listener);
        return this;
    }

    public ICFunSimpleAlertDialog setOnNegativeListener(int resId, View.OnClickListener listener) {

        String text = mCtx.getResources().getString(resId);

        mCancelTv.setText(text);

        mCancelTv.setOnClickListener(listener);

        return this;
    }

    public ICFunSimpleAlertDialog setOnPositiveListener(int resId, View.OnClickListener listener) {

        String text = mCtx.getResources().getString(resId);

        mOkTv.setText(text);

        mOkTv.setOnClickListener(listener);

        return this;
    }

    public ICFunSimpleAlertDialog setTitle(int resId){

        mTitleTv.setText(mCtx.getResources().getString(resId));

        return this;
    }

    public ICFunSimpleAlertDialog setContent(int resId){

        mContentTv.setText(mCtx.getResources().getString(resId));

        return this;
    }

    public ICFunSimpleAlertDialog setContent(String resTxt){

        mContentTv.setText(resTxt);

        return this;
    }


    public void show(){
        if (mDialog != null){
            mDialog.show();
        }
    }

    public void dismiss(){
        if (mDialog != null){
            mDialog.dismiss();
        }
    }

}

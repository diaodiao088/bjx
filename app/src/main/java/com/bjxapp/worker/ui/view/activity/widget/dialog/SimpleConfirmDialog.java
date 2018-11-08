package com.bjxapp.worker.ui.view.activity.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjxapp.worker.R;

/**
 * Created by zhangdan on 2018/11/7.
 * comments:
 */

public class SimpleConfirmDialog {

    private Context mCtx;

    private CustomLayoutDialog mDialog = null;

    private TextView mTitleTv;
    private TextView mContentTv;
    private TextView mCancelTv;
    private TextView mOkTv;
    private View mRootView;

    private LinearLayout mMessageLayout;

    public SimpleConfirmDialog(Context ctx) {
        this.mCtx = ctx;
        mDialog = new CustomLayoutDialog(ctx, R.layout.simple_confirm_dialog);
        initView();
    }

    private void initView() {
        mRootView = mDialog.getView();
        if (mRootView != null) {
            mTitleTv = mRootView.findViewById(R.id.dialog_title_tv);
            mContentTv = mRootView.findViewById(R.id.content_tv);
            mCancelTv = mRootView.findViewById(R.id.dialog_cancel_tv);
            mOkTv = mRootView.findViewById(R.id.dialog_log_out_tv);
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

    public SimpleConfirmDialog setOnNegativeListener(int resId, View.OnClickListener listener) {

        if (resId > 0){
            String text = mCtx.getResources().getString(resId);

            mCancelTv.setText(text);
        }

        mCancelTv.setOnClickListener(listener);

        return this;
    }

    public SimpleConfirmDialog setOnPositiveListener(int resId, View.OnClickListener listener) {

        if (resId > 0){
            String text = mCtx.getResources().getString(resId);

            mOkTv.setText(text);
        }

        mOkTv.setOnClickListener(listener);

        return this;
    }


    public SimpleConfirmDialog setOnCancelListener(DialogInterface.OnCancelListener listener){
        mDialog.setOnCancelListener(listener);
        return this;
    }


    public SimpleConfirmDialog setTitle(int resId){

        mTitleTv.setText(mCtx.getResources().getString(resId));

        return this;
    }

    public SimpleConfirmDialog setContent(int resId){

        mContentTv.setText(mCtx.getResources().getString(resId));

        return this;
    }
    public SimpleConfirmDialog setContent(String content){
        if(!TextUtils.isEmpty(content)) {
            mContentTv.setText(content);
        }

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

    public boolean isShow() {
        if (mDialog == null)
            return false;
        return mDialog.isShowing();
    }


    public SimpleConfirmDialog setContentVisible(int visibility){
        mContentTv.setVisibility(visibility);
        return this;
    }

    public SimpleConfirmDialog setTitleVisible(int visibility){
        mTitleTv.setVisibility(visibility);
        return this;
    }


}
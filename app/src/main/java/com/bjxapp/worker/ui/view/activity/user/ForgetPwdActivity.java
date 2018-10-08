package com.bjxapp.worker.ui.view.activity.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.bjxapp.worker.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XEditText;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.dataupload.ReportEvent;
import com.bjxapp.worker.dataupload.Uploader;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.Account;
import com.bjxapp.worker.model.XResult;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;

public class ForgetPwdActivity extends BaseActivity implements OnClickListener {
    protected static final String TAG = "登陆界面";
    private XTextView mTitleTextView;
    private XImageView mBackImageView, mVerifyCodeImageView;
    private XButton mLoginButton;
    private XButton mSendAuthButton;
    private XEditText mMobileEditText;
    private XEditText mPasswordEditText, mVerifyCodeEditText;
    private int mKeyBackClickCount = 0;
    private XWaitingDialog mWaitingDialog;
    private String mLoginKey = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_forget_pwd);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initControl() {
        mLoginButton = (XButton) findViewById(R.id.login_button_login);
        mSendAuthButton = (XButton) findViewById(R.id.login_button_sendauthcode);
        mMobileEditText = (XEditText) findViewById(R.id.login_edit_mobile);
        mPasswordEditText = (XEditText) findViewById(R.id.login_edit_password);

        mVerifyCodeEditText = (XEditText) findViewById(R.id.login_edit_verify_code);
        mVerifyCodeImageView = (XImageView) findViewById(R.id.login_image_verify_code);

        mWaitingDialog = new XWaitingDialog(context);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        mMobileEditText.setText(ConfigManager.getInstance(context).getUserCode());
        getLoginKey();
    }

    @Override
    protected void setListener() {
        mVerifyCodeImageView.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mSendAuthButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_image_back:
                Utils.finishWithoutAnim(ForgetPwdActivity.this);
                break;
            case R.id.login_image_verify_code:
                getVerifyImage();
                break;
            case R.id.login_button_sendauthcode:
                sendAuthCode();
                break;
            case R.id.login_button_login:
                Uploader.onEvent(context, ReportEvent.EVENT_LOGIN_BUTTON_CLICK, ReportEvent.Label.EVENT_LOGIN_BUTTON_CLICK_LABEL);
                getLogin();
                break;
            case R.id.pwd_login_tv:
                LoginPwdActivity.goToActivity(this);
                Utils.finishWithoutAnim(ForgetPwdActivity.this);
                break;
            default:
                break;
        }
    }

    private AsyncTask<Void, Void, String> mGetLoginKey;

    private void getLoginKey() {
        mGetLoginKey = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return LogicFactory.getAccountLogic(context).getLoginKey();
            }

            @Override
            protected void onPostExecute(String result) {
                if (Utils.isNotEmpty(result)) {
                    mLoginKey = result;
                    getVerifyImage();
                }
            }
        };

        mGetLoginKey.execute();
    }

    private AsyncTask<Void, Void, Bitmap> mGetVerifyImage;

    private void getVerifyImage() {
        if (!Utils.isNotEmpty(mLoginKey)) {
            Utils.showShortToast(context, "获取数据失败，请退出应用，重新进入！");
            return;
        }
        mGetVerifyImage = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                return LogicFactory.getAccountLogic(context).getVerifyCode(mLoginKey);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null) {
                    mVerifyCodeImageView.setImageBitmap(result);
                }
            }
        };

        mGetVerifyImage.execute();
    }

    private MyCount mCounter;
    private AsyncTask<Void, Void, Integer> mSendAuthCode;

    private void sendAuthCode() {
        final String mobile = mMobileEditText.getText().toString();
        if (TextUtils.isEmpty(mobile)) {
            Utils.showLongToast(context, "请输入手机号！");
            return;
        }
        final String verifyCode = mVerifyCodeEditText.getText().toString();
        if (TextUtils.isEmpty(verifyCode)) {
            Utils.showLongToast(context, "请输入图形验证码！");
            return;
        }

        if (mCounter == null) {
            mCounter = new MyCount(60000, 1000);
        }
        mCounter.start();

        mSendAuthCode = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                String mobile = mMobileEditText.getText().toString();
                return LogicFactory.getAccountLogic(context).sendAuth(mobile, mLoginKey, verifyCode);
            }

            @Override
            protected void onPostExecute(Integer result) {
                if (result == APIConstants.RESULT_CODE_SUCCESS) {

                } else {
                    Utils.showLongToast(context, "获取验证码失败，请检查图形码是否正确！");
                    mCounter.cancel();
                    mSendAuthButton.setEnabled(true);
                    mSendAuthButton.setText("获取验证码");
                }
            }
        };

        mSendAuthCode.execute();
    }


    private void getLogin() {
        Utils.hideSoftInput(context, mMobileEditText);
        Utils.hideSoftInput(context, mPasswordEditText);
        String userCode = mMobileEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();
        getLogin(userCode, password);
    }

    private void getLogin(final String userCode, final String password) {
        if (!TextUtils.isEmpty(userCode) && !TextUtils.isEmpty(password)) {
            final Account account = new Account();
            account.setMobile(userCode);
            account.setAuthCode(password);

            mWaitingDialog.show("正在验证，请稍后...", false);


            new AsyncTask<Void, Void, XResult>() {
                Boolean loginSuccess = false;

                @Override
                protected XResult doInBackground(Void... params) {
                    return LogicFactory.getAccountLogic(context).login(account);
                }

                @Override
                protected void onPostExecute(XResult result) {
                    mWaitingDialog.dismiss();

                    if (result != null) {
                        if (result.getResultCode() == APIConstants.RESULT_CODE_SUCCESS) {
                           loginSuccess = true;
                        }
                    }

                    if (loginSuccess) {
                        doNextStep();
                    } else {
                        Utils.showLongToast(ForgetPwdActivity.this, getString(R.string.login_fail_warning));
                    }
                }

            }.execute();

        } else {
            Utils.showLongToast(ForgetPwdActivity.this, getString(R.string.login_account_warning));
        }
    }

    private void doNextStep(){
        mLoginButton.setBackgroundResource(R.drawable.button_background_green);
        mLoginButton.setClickable(true);
        ChangePwdActivity.goToActivityForResult(this);
        Utils.finishWithoutAnim(this);
    }

    /* 定义一个倒计时的内部类 */
    private class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            mSendAuthButton.setEnabled(true);
            mSendAuthButton.setText("获取验证码");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mSendAuthButton.setEnabled(false);
            mSendAuthButton.setText("(" + millisUntilFinished / 1000 + ")" + getString(R.string.common_time_second));
        }
    }

    @Override
    protected String getPageName() {
        return TAG;
    }

    @Override
    public void onDestroy() {
        try {
            if (mSendAuthCode != null) {
                mSendAuthCode.cancel(true);
            }
            if (mLoginButton != null){
                mLoginButton.setClickable(false);
            }
        } catch (Exception e) {
        }

        super.onDestroy();
    }

    public static void goToActivity(Context ctx){
        Intent intent = new Intent();
        intent.setClass(ctx , ForgetPwdActivity.class);
        ctx.startActivity(intent);
    }

}

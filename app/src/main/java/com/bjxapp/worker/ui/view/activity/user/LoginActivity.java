package com.bjxapp.worker.ui.view.activity.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bjxapp.worker.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XEditText;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.dataupload.ReportEvent;
import com.bjxapp.worker.dataupload.Uploader;
import com.bjxapp.worker.global.ActivitiesManager;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.http.keyboard.commonutils.Constants;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.Account;
import com.bjxapp.worker.model.XResult;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements OnClickListener {
    protected static final String TAG = "登陆界面";

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @BindView(R.id.login_image_verify_code)
    XImageView mVerifyCodeImageView;

    @BindView(R.id.login_button_login)
    XButton mLoginButton;

    @BindView(R.id.login_button_sendauthcode)
    XButton mSendAuthButton;

    @BindView(R.id.login_edit_mobile)
    XEditText mMobileEditText;

    @BindView(R.id.login_edit_password)
    XEditText mPasswordEditText;

    @BindView(R.id.login_edit_verify_code)
    XEditText mVerifyCodeEditText;

    @BindView(R.id.pwd_login_tv)
    TextView mLoginPwdTv;

    private int mKeyBackClickCount = 0;
    private XWaitingDialog mWaitingDialog;
    private String mLoginKey = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_login_new);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initControl() {
        mWaitingDialog = new XWaitingDialog(context);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        mMobileEditText.setText(ConfigManager.getInstance(context).getUserCode());
        getLoginKeyNew();
    }

    @Override
    protected void setListener() {
        mVerifyCodeImageView.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mSendAuthButton.setOnClickListener(this);
        mLoginPwdTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_image_back:
                Utils.finishWithoutAnim(LoginActivity.this);
                break;
            case R.id.login_image_verify_code:
                getVerifyImageNew();
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
                Utils.finishWithoutAnim(LoginActivity.this);
                break;
            default:
                break;
        }
    }


    private void getLoginKeyNew() {

        LoginApi httpService = KHttpWorker.ins().createHttpService(LoginApi.URL, LoginApi.class);

        Call<JsonObject> getKeyRequest = httpService.getLoginKey();

        getKeyRequest.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject object = response.body();
                if (object != null) {
                    mLoginKey = object.get("loginKey").getAsString();
                    if (!TextUtils.isEmpty(mLoginKey)) {
                        getVerifyImageNew();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    private void getVerifyImageNew() {

        if (!Utils.isNotEmpty(mLoginKey)) {
            LoginActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.showShortToast(context, "获取数据失败，请退出应用，重新进入！");
                }
            });
            return;
        }

        OkHttpClient client = new OkHttpClient();
        String url = LoginApi.URL + "/login/captcha";
        try {

            RequestBody requestBodyPost = new FormBody.Builder().add("loginKey", mLoginKey).build();

            Request request = new Request.Builder().url(url).post(requestBodyPost).build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.showShortToast(context, "验证码获取失败，请重新进入！");
                        }
                    });
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    InputStream is = response.body().byteStream();
                    final Bitmap bm = BitmapFactory.decodeStream(is);

                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mVerifyCodeImageView.setImageBitmap(bm);
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        LoginApi httpService = KHttpWorker.ins().createHttpService(LoginApi.URL, LoginApi.class);

        Map params = new HashMap();

        params.put("loginKey", mLoginKey);
        params.put("captchaCode", verifyCode);
        params.put("userCode", mobile);

        Call<JsonObject> getKeyRequest = httpService.getAuthCode(params);

        getKeyRequest.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() != APIConstants.RESULT_CODE_SUCCESS) {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.showLongToast(context, "获取验证码失败，请检查图形码是否正确！");
                            mCounter.cancel();
                            mSendAuthButton.setEnabled(true);
                            mSendAuthButton.setText("获取验证码");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showLongToast(context, "获取验证码失败，请检查图形码是否正确！");
                        mCounter.cancel();
                        mSendAuthButton.setEnabled(true);
                        mSendAuthButton.setText("获取验证码");
                    }
                });
            }
        });

        /*mSendAuthCode = new AsyncTask<Void, Void, Integer>() {
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

        mSendAuthCode.execute();*/
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

            mWaitingDialog.show(getString(R.string.login_waiting_message), false);

            LoginApi httpService = KHttpWorker.ins().createHttpService(LoginApi.URL, LoginApi.class);

            Map params = new HashMap();

            params.put("userCode", userCode);
            params.put("authCode", password);

            Call<JsonObject> loginRequest = httpService.authCodeLogin(params);

            loginRequest.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mWaitingDialog != null){
                                mWaitingDialog.dismiss();
                            }
                        }
                    });

                    if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {

                        JsonObject object = response.body();

                        final String msg = object.get("msg").getAsString();
                        final int code = object.get("code").getAsInt();

                        if (object != null) {
                            if (object.get("token") != null) {
                                String token = object.get("token").getAsString();
                                if (!TextUtils.isEmpty(token)) {
                                    ConfigManager.getInstance(context).setUserCode(userCode);
                                    ConfigManager.getInstance(context).setUserSession(token);
                                    setResult(RESULT_OK);
                                    Utils.finishWithoutAnim(LoginActivity.this);
                                }
                            }else{
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mWaitingDialog != null){
                                            mWaitingDialog.dismiss();
                                        }
                                        Utils.showLongToast(LoginActivity.this, msg + " : " + code);
                                    }
                                });
                            }
                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Utils.showLongToast(LoginActivity.this, msg + " : " + code);
                                }
                            });
                        }
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showLongToast(LoginActivity.this, getString(R.string.login_fail_warning));
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mWaitingDialog != null){
                                mWaitingDialog.dismiss();
                            }
                        }
                    });


                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Utils.showLongToast(LoginActivity.this, getString(R.string.login_fail_warning));
                        }
                    });
                }
            });

            /*new AsyncTask<Void, Void, XResult>() {
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
                            Account account = (Account) result.getDataObject();
                            ConfigManager.getInstance(context).setUserCode(account.getMobile());
                            ConfigManager.getInstance(context).setUserSession(account.getSession());
                            ConfigManager.getInstance(context).setUserStatus(account.getStatus());
                            loginSuccess = true;
                        }
                    }

                    if (loginSuccess) {
                        setResult(RESULT_OK);
                        Utils.finishWithoutAnim(LoginActivity.this);
                    } else {
                        Utils.showLongToast(LoginActivity.this, getString(R.string.login_fail_warning));
                        //ignore login validate,debug...

                        ConfigManager.getInstance(context).setUserCode("15901066870");
                        ConfigManager.getInstance(context).setUserName("Jason");
                        ConfigManager.getInstance(context).setUserSession("jason15901066870");
                        setResult(RESULT_OK);
                        Utils.finishWithoutAnim(LoginActivity.this);
                    }
                }
            }.execute();*/

        } else {
            Utils.showLongToast(LoginActivity.this, getString(R.string.login_account_warning));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (mKeyBackClickCount++) {
                case 0:
                    Toast.makeText(this, getString(R.string.common_exit_message), Toast.LENGTH_SHORT).show();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mKeyBackClickCount = 0;
                        }
                    }, 3000);
                    break;
                case 1:
                    ActivitiesManager.getInstance().finishAllActivities();
                    break;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        } catch (Exception e) {
        }

        super.onDestroy();
    }

    public static void goToActivity(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, LoginActivity.class);
        ctx.startActivity(intent);
    }

}

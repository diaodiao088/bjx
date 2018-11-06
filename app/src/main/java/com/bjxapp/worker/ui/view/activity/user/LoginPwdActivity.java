package com.bjxapp.worker.ui.view.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bjxapp.worker.MainActivity;
import com.bjxapp.worker.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zhangdan on 2018/9/10.
 * <p>
 * comments:
 */

public class LoginPwdActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = LoginPwdActivity.class.getSimpleName();

    private EditText mPhoneTv;
    private EditText mPwdTv;
    private TextView mConfirmTv;

    private TextView mForgetPwdTv;
    private TextView mPhoneLoginTv;
    private TextView mRegisterTv;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private XWaitingDialog mWaitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login_pwd);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initControl() {

    }

    @Override
    protected void initView() {
        mPhoneTv = findViewById(R.id.login_edit_mobile);
        mPwdTv = findViewById(R.id.login_pwd);
        mConfirmTv = findViewById(R.id.login_button_confirm);

        mForgetPwdTv = findViewById(R.id.forget_pwd_tv);
        mPhoneLoginTv = findViewById(R.id.login_phone_tv);
        mRegisterTv = findViewById(R.id.free_register_tv);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
        mConfirmTv.setOnClickListener(this);
        mForgetPwdTv.setOnClickListener(this);
        mPhoneLoginTv.setOnClickListener(this);
        mRegisterTv.setOnClickListener(this);
    }

    @Override
    protected String getPageName() {
        return TAG;
    }

    public static void goToActivity(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, LoginPwdActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button_confirm:
                tryLogin();
                break;
            case R.id.forget_pwd_tv:
                ForgetPwdActivity.goToActivity(this);
                break;
            case R.id.login_phone_tv:
                LoginActivity.goToActivity(this);
                Utils.finishWithoutAnim(LoginPwdActivity.this);
                break;
            case R.id.free_register_tv:
                LoginActivity.goToActivity(this);
                Utils.finishWithoutAnim(LoginPwdActivity.this);
                break;
        }
    }

    private void tryLogin() {

        String phoneNumber = mPhoneTv.getText().toString().trim();
        String pwd = mPwdTv.getText().toString().trim();

        if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(pwd)) {
            getLoginReal(phoneNumber, pwd);
        } else if (TextUtils.isEmpty(phoneNumber)) {
            Utils.showLongToast(LoginPwdActivity.this, "请填写手机号");
        } else if (TextUtils.isEmpty(pwd)) {
            Utils.showLongToast(LoginPwdActivity.this, "请输入密码");
        }
    }

    private void getLoginReal(final String phoneNumber, String pwd) {

        mWaitingDialog = new XWaitingDialog(LoginPwdActivity.this);

        mWaitingDialog.show(getString(R.string.login_waiting_message), false);

        LoginApi httpService = KHttpWorker.ins().createHttpService(LoginApi.URL, LoginApi.class);

        Map params = new HashMap();

        params.put("userCode", phoneNumber);
        params.put("password", pwd);

        Call<JsonObject> loginRequest = httpService.pwdLogin(params);

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
                                ConfigManager.getInstance(context).setUserCode(phoneNumber);
                                ConfigManager.getInstance(context).setUserSession(token);

                                Intent intent = new Intent();
                                intent.setClass(LoginPwdActivity.this , MainActivity.class);
                                LoginPwdActivity.this.startActivity(intent);

                                Utils.finishWithoutAnim(LoginPwdActivity.this);
                            }
                        }else{
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Utils.showLongToast(LoginPwdActivity.this, msg + ":" + code);
                                }
                            });
                        }
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showLongToast(LoginPwdActivity.this, msg + ":" + code);
                            }
                        });
                    }
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Utils.showLongToast(LoginPwdActivity.this, getString(R.string.login_fail_warning));
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
                        Utils.showLongToast(LoginPwdActivity.this, getString(R.string.login_fail_warning));
                    }
                });
            }
        });

    }

}

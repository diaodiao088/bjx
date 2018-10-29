package com.bjxapp.worker.ui.view.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bjxapp.worker.R;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;

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
            // TODO: 2018/10/29

        } else if (TextUtils.isEmpty(phoneNumber)){
            Utils.showLongToast(LoginPwdActivity.this, "请填写手机号");
        } else if (TextUtils.isEmpty(pwd)){
            Utils.showLongToast(LoginPwdActivity.this, "请输入密码");
        }
    }


}

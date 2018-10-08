package com.bjxapp.worker.ui.view.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.bjxapp.worker.R;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;

import java.util.regex.Pattern;

/**
 * Created by zhangdan on 2018/9/10.
 * <p>
 * comments:
 */

public class ChangePwdActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ChangePwdActivity.class.getSimpleName();

    private XButton mOkBtn;
    private XImageView mBackIv;
    private EditText mPwdTv;
    private EditText mPwdSureTv;

    String pattern = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_change_pwd);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initControl() {

    }

    @Override
    protected void initView() {

        mOkBtn = findViewById(R.id.login_button_confirm);
        mBackIv = findViewById(R.id.title_image_back);
        mPwdTv = findViewById(R.id.enter_pwd_tv);
        mPwdSureTv = findViewById(R.id.enter_pwd_tv_sure);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
        mOkBtn.setOnClickListener(this);
        mBackIv.setOnClickListener(this);
    }

    @Override
    protected String getPageName() {
        return TAG;
    }


    public static void goToActivityForResult(Activity ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, ChangePwdActivity.class);
        ctx.startActivityForResult(intent, Constant.CONSULT_SETTING_PWD);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forget_pwd_tv:
                ForgetPwdActivity.goToActivity(this);
                break;
            case R.id.login_phone_tv:
                LoginActivity.goToActivity(this);
                Utils.finishWithoutAnim(ChangePwdActivity.this);
                break;
            case R.id.title_image_back:
                Utils.finishActivity(this);
                break;
            case R.id.login_button_confirm:
                tryChangePwd();
                break;
            case R.id.free_register_tv:
                break;
        }
    }

    // TODO: 2018/9/18
    private void tryChangePwd() {

        String pwd = mPwdTv.getText().toString();
        String pwdSure = mPwdSureTv.getText().toString();

        if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwdSure)){
            Utils.showShortToast(context, "请输入密码！");
            return;
        }

        if (!pwd.equals(pwdSure)){
            Utils.showShortToast(context, "两次密码不一样！");
            return;
        }

        boolean isMatch = Pattern.matches(pattern , pwd);

        if (!isMatch){
            Utils.showShortToast(context, "密码格式错误！");
        }

        // TODO: 2018/10/7 网络请求


    }


}

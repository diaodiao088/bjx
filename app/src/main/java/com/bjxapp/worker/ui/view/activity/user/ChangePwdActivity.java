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
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @BindView(R.id.title_text_tv)
    XTextView mTitleTv;

    String pattern = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$";

    public static final int FROM_FORGET_PWD = 0x01;
    public static final int FROM_REGISTER_PWD = 0x02;

    public static final String FROM_TYPE = "from_type";

    public static final String KEY_TYPE = "key_type";

    private int mFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_change_pwd);
        ButterKnife.bind(this);

        mTitleTv.setText("设置密码");
        handleIntent();
        super.onCreate(savedInstanceState);
    }

    private void handleIntent() {

        Intent intent = getIntent();
        if (intent != null) {
            mFrom = intent.getIntExtra(FROM_TYPE, 0x00);
        }
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

    public static void goToActivityForResult(Activity ctx, int from) {
        Intent intent = new Intent();
        intent.setClass(ctx, ChangePwdActivity.class);
        intent.putExtra(FROM_TYPE, from);
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

    private void tryChangePwd() {

        String pwd = mPwdTv.getText().toString();
        String pwdSure = mPwdSureTv.getText().toString();

        if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwdSure)) {
            Utils.showShortToast(context, "请输入密码！");
            return;
        }

        if (!pwd.equals(pwdSure)) {
            Utils.showShortToast(context, "两次密码不一样！");
            return;
        }

        boolean isMatch = Pattern.matches(pattern, pwd);

        if (!isMatch) {
            Utils.showShortToast(context, "密码格式错误！");
        }

        if (mFrom == FROM_REGISTER_PWD) {
            Intent intent = new Intent();
            intent.putExtra(KEY_TYPE, mPwdSureTv.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        } else if (mFrom == FROM_FORGET_PWD) {
            changePwdReal();
        }
    }

    private void changePwdReal() {

    }

}

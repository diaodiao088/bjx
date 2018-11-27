package com.bjxapp.worker.ui.view.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.bjx.master.R;;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.MD5Util;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    String pattern = "^-?[1-9]d*$";

    public static final int FROM_FORGET_PWD = 0x01;
    public static final int FROM_REGISTER_PWD = 0x02;
    public static final int FROM_EDIT_APPLY = 0x03;

    public static final String FROM_TYPE = "from_type";

    public static final String KEY_TYPE = "key_type";

    private int mFrom;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private XWaitingDialog mDialog;

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
            return;
        }

        if (mFrom == FROM_REGISTER_PWD) {
            Intent intent = new Intent();
            intent.putExtra(KEY_TYPE, MD5Util.getStringMD5(mPwdSureTv.getText().toString()));
            setResult(RESULT_OK, intent);
            finish();
        } else if (mFrom == FROM_FORGET_PWD || mFrom == FROM_EDIT_APPLY) {
            changePwdReal();
        }
    }

    private void changePwdReal() {

        mDialog = new XWaitingDialog(this);

        LoginApi httpService = KHttpWorker.ins().createHttpService(LoginApi.URL, LoginApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("userCode", ConfigManager.getInstance(ChangePwdActivity.this).getUserCode());
        params.put("token", ConfigManager.getInstance(ChangePwdActivity.this).getUserSession());
        params.put("password", MD5Util.getStringMD5(mPwdTv.getText().toString()));

        Call<JsonObject> request = httpService.changePwd(params);

        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    JsonObject object = response.body();
                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();
                    if (object != null && code == 0) {

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(context, "修改密码成功！");
                            }
                        });

                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (mDialog != null) {
                                    mDialog.dismiss();
                                }

                                Utils.showShortToast(context, msg);
                            }
                        });
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (mDialog != null) {
                            mDialog.dismiss();
                        }

                        Utils.showShortToast(context, "修改密码失败！");
                    }
                });
            }
        });
    }

}

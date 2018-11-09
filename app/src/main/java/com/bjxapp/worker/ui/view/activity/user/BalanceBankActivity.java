package com.bjxapp.worker.ui.view.activity.user;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.bjxapp.worker.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.ProfileApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.BankInfo;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.BankCardValidate;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BalanceBankActivity extends BaseActivity implements OnClickListener {

    protected static final String TAG = "填写银行卡界面";
    private XTextView mTitleTextView;
    private XImageView mBackImageView;

    private EditText mBankCardEdit, mBankNameEdit, mBankPersonEdit, mBankMobileEdit;

    XButton mSaveButton;

    private XWaitingDialog mWaitingDialog;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_balance_bank);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initControl() {
        mTitleTextView = (XTextView) findViewById(R.id.title_text_tv);
        mTitleTextView.setText("填写银行卡信息");
        mBackImageView = (XImageView) findViewById(R.id.title_image_back);
        mBackImageView.setVisibility(View.VISIBLE);

        mBankCardEdit = (EditText) findViewById(R.id.balance_bank_card_edit);
        mBankNameEdit = (EditText) findViewById(R.id.balance_bank_name_edit);
        mBankPersonEdit = (EditText) findViewById(R.id.balance_bank_person_edit);
        mBankMobileEdit = (EditText) findViewById(R.id.balance_bank_mobile_edit);
        mSaveButton = (XButton) findViewById(R.id.balance_bank_button_save);

        mWaitingDialog = new XWaitingDialog(context);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
        mBackImageView.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_image_back:
                Utils.finishActivity(BalanceBankActivity.this);
                break;
            case R.id.balance_bank_button_save:
                saveOperation();
            default:
                break;
        }
    }

    private void saveOperation() {
        if (!Utils.isNetworkAvailable(context)) {
            Utils.showShortToast(context, getString(R.string.common_no_network_message));
            return;
        }

        String bankCard = mBankCardEdit.getText().toString().trim();
        String bankName = mBankNameEdit.getText().toString().trim();
        String bankPerson = mBankPersonEdit.getText().toString().trim();
        String bankMobile = mBankMobileEdit.getText().toString().trim();

        if (!Utils.isNotEmpty(bankCard)) {
            Utils.showShortToast(context, "银行卡号不能为空！");
            mBankCardEdit.setFocusable(true);
            return;
        }
        if (!Utils.isNotEmpty(bankName)) {
            Utils.showShortToast(context, "开户银行不能为空！");
            mBankNameEdit.setFocusable(true);
            return;
        }
        if (!Utils.isNotEmpty(bankPerson)) {
            Utils.showShortToast(context, "开户人不能为空！");
            mBankPersonEdit.setFocusable(true);
            return;
        }
        if (!Utils.isNotEmpty(bankMobile)) {
            Utils.showShortToast(context, "开户手机号不能为空！");
            mBankMobileEdit.setFocusable(true);
            return;
        }
        if (!BankCardValidate.checkBankCard(bankCard)) {
            Utils.showShortToast(context, "银行卡号输入不正确！");
            mBankCardEdit.setFocusable(true);
            return;
        }
        if (!Utils.isMobile(bankMobile)) {
            Utils.showShortToast(context, "开户手机号输入不正确！");
            mBankMobileEdit.setFocusable(true);
            return;
        }

        final BankInfo bankInfo = new BankInfo();
        bankInfo.setCard(bankCard.trim());
        bankInfo.setName(bankName.trim());
        bankInfo.setPerson(bankPerson.trim());
        bankInfo.setMobile(bankMobile.trim());

        mWaitingDialog.show("正在保存，请稍候...", false);

        ProfileApi profileApi = KHttpWorker.ins().createHttpService(LoginApi.URL, ProfileApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("bankCardNo", bankCard);
        params.put("bankName", bankName);
        params.put("bankAccountName", bankPerson);
        params.put("bankAccountPhone", bankMobile);

        final Call<JsonObject> request = profileApi.bindBank(params);

        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject object = response.body();

                final String msg = object.get("msg").getAsString();
                final int code = object.get("code").getAsInt();

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS && object.get("code").getAsInt() == 0) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mWaitingDialog != null) {
                                mWaitingDialog.dismiss();
                                Utils.showShortToast(BalanceBankActivity.this, "保存成功");
                            }
                        }
                    });

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Utils.startActivity(context, BalanceBankWithdrawActivity.class);
                            Utils.finishActivity(BalanceBankActivity.this);
                        }
                    }, 500);
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mWaitingDialog != null) {
                                mWaitingDialog.dismiss();
                                Utils.showShortToast(BalanceBankActivity.this, msg + ":" + code);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mWaitingDialog != null) {
                            mWaitingDialog.dismiss();
                            Utils.showShortToast(BalanceBankActivity.this, "保存失败，请重试！");
                        }
                    }
                });
            }
        });



        /*new AsyncTask<String, Void, Integer>() {
            @Override
            protected Integer doInBackground(String... params) {
                return LogicFactory.getAccountLogic(context).saveBalanceBankInfomation(bankInfo);
            }

            @Override
            protected void onPostExecute(Integer result) {
                mWaitingDialog.dismiss();
                if (result == APIConstants.RESULT_CODE_SUCCESS) {
                    Utils.startActivity(context, BalanceBankWithdrawActivity.class);
                    Utils.finishActivity(BalanceBankActivity.this);
                } else {
                    Utils.showShortToast(context, "保存失败，请重试！");
                }
            }

        }.execute();*/


    }

    @Override
    protected String getPageName() {
        return TAG;
    }

}

package com.bjxapp.worker.ui.view.activity.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.ProfileApi;
import com.bjxapp.worker.controls.XEditText;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.dialog.WithdrawInputDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.BankInfo;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.HandleUrlLinkMovementMethod;
import com.bjxapp.worker.utils.Utils;
import com.bjx.master.R;;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BalanceBankWithdrawActivity extends BaseActivity {

    protected static final String TAG = "提现界面";

    @BindView(R.id.title_right_small_tv)
    TextView mRightTextView;

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @OnClick(R.id.title_image_back)
    void onBack() {
        Utils.finishActivity(BalanceBankWithdrawActivity.this);
    }

    @BindView(R.id.balance_withdraw_card_edit)
    TextView mBankCardEdit;

    @BindView(R.id.balance_withdraw_name_edit)
    TextView mBankNameTv;

    @BindView(R.id.balance_withdraw_person_edit)
    TextView mBankPersonTv;

    @BindView(R.id.balance_withdraw_mobile_edit)
    TextView mBankMobileTv;

    @BindView(R.id.balance_withdraw_balance_edit)
    TextView mBalanceEdit;

    @BindView(R.id.balance_withdraw_balance_allow_edit)
    TextView mAllowCashEdit;

    @BindView(R.id.balance_withdraw_information_edit)
    TextView mInformationEdit;

    @OnClick(R.id.title_right_small_tv)
    void onClickHistory() {
        showWithdrawHistory();
    }

    @OnClick(R.id.balance_withdraw_button_save)
    void onClickBtn() {
        saveOperation();
    }

    @OnClick(R.id.balance_withdraw_balance_info_image)
    void clickBalanceInfo() {
        showBalanceInfo();
    }

    private XWaitingDialog mWaitingDialog;

    private String mQuestionInformation = "";
    private String mInputInformation = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_balance_withdraw);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initControl() {
        mRightTextView.setText("提现历史");
        mRightTextView.setVisibility(View.VISIBLE);
        mTitleTextView.setText("提现");
        mWaitingDialog = new XWaitingDialog(context);

        HandleUrlLinkMovementMethod instance = HandleUrlLinkMovementMethod.getInstance();
        instance.setOnLinkCallBack(new HandleUrlLinkMovementMethod.OnLinkCallBack() {
            @Override
            public void onClick(String url) {
                if (url.contains("action")) {
                    callService();
                }
            }
        });

        mInformationEdit.setMovementMethod(instance);
        mInformationEdit.setLinkTextColor(Color.parseColor("#00A551"));
        mInformationEdit.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        loadData();
    }

    @Override
    protected void setListener() {
    }

    private void showWithdrawHistory() {
        Utils.startActivity(context, BalanceWithdrawHistoryActivity.class);
    }

    private void callService() {
        String mobile = "010-5317025";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + mobile));
        startActivity(intent);
    }

    private void showBalanceInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("百家修");
        builder.setMessage(getQuestion(mPeriodDay));
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private AsyncTask<String, Void, BankInfo> mLoadDataTask;

    private void loadData() {

        mWaitingDialog.show("正在加载中，请稍候...", false);

        ProfileApi profileApi = KHttpWorker.ins().createHttpService(LoginApi.URL, ProfileApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());

        Call<JsonObject> call = profileApi.getBankInfo(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject object = response.body();

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS && object.get("code").getAsInt() == 0) {
                    JsonObject bankInfoItem = object.getAsJsonObject("bankInfo");
                    if (bankInfoItem == null || bankInfoItem.get("bankAccountName") == null) {
                        getBankInfoFailed();
                    } else {
                        getBankInfoSucc(bankInfoItem);
                    }
                } else {
                    getBankInfoFailed();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                getBankInfoFailed();
            }
        });

        loadConfig();

        /*mLoadDataTask = new AsyncTask<String, Void, BankInfo>() {
            @Override
            protected BankInfo doInBackground(String... params) {
                return LogicFactory.getAccountLogic(context).getBalanceBankInfomation();
            }

            @Override
            protected void onPostExecute(BankInfo result) {
                mWaitingDialog.dismiss();

                if (result == null) {
                    return;
                }

                mBankCardEdit.setText(result.getCard());
                mBankNameTv.setText(result.getName());
                mBankPersonTv.setText(result.getPerson());
                mBankMobileTv.setText(result.getMobile());
                mBalanceEdit.setText(String.valueOf(result.getBalanceMoney()) + "元");
                mBalanceEdit.setTag(result.getBalanceMoney());
                mAllowCashEdit.setText(String.valueOf(result.getCashMoney()) + "元");
                mAllowCashEdit.setTag(result.getCashMoney());

                //  mInformationEdit.setText(getInformation(result));
                mQuestionInformation = getQuestion(result);
                mInputInformation = getInputInformation(result);

            }
        };
        mLoadDataTask.execute();*/
    }

    private int mPeriodDay = 15;

    private String mCashInfoStr;

    private void loadConfig() {

        ProfileApi profileApi = KHttpWorker.ins().createHttpService(LoginApi.URL, ProfileApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());

        Call<JsonObject> call = profileApi.getWithDrawDay(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject object = response.body();

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS && object.get("code").getAsInt() == 0) {
                    JsonArray infoArray = object.getAsJsonArray("value");

                    if (infoArray != null && infoArray.size() > 0) {
                        mCashInfoStr = getCashInfo(infoArray);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(mCashInfoStr)) {
                                    mInformationEdit.setText(Html.fromHtml(mCashInfoStr));
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });


        Map<String, String> params1 = new HashMap<>();
        params1.put("token", ConfigManager.getInstance(this).getUserSession());
        params1.put("userCode", ConfigManager.getInstance(this).getUserCode());
        Call<JsonObject> callPeriod = profileApi.getGuarPeriod(params1);
        callPeriod.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject object = response.body();

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS && object.get("code").getAsInt() == 0) {
                    int value = object.get("value").getAsInt();
                    mPeriodDay = value >= 0 ? value : 14;
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private String getCashInfo(JsonArray array) {
        StringBuilder builder = new StringBuilder();
        builder.append("提示：\n每个月");
        for (int i = 0; i < array.size(); i++) {
            builder.append(array.get(i).toString() + "日、");
        }
        builder.append(getString(R.string.bank_subtitle));
        return builder.toString();
    }

    private void getBankInfoFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mWaitingDialog != null) {
                    mWaitingDialog.dismiss();
                }
                Utils.showShortToast(BalanceBankWithdrawActivity.this, "银行卡信息加载失败！");
            }
        });
    }

    private void getBankInfoSucc(final JsonObject backInfoItem) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mWaitingDialog != null) {
                    mWaitingDialog.dismiss();
                }

                String bankNum = backInfoItem.get("bankCardNo").getAsString();
                String bankName = backInfoItem.get("bankName").getAsString();
                String bankPerson = backInfoItem.get("bankAccountName").getAsString();
                String bankPhone = backInfoItem.get("masterPhone").getAsString();
                String bankAmout = backInfoItem.get("balanceAmount").getAsString();
                String canWithdrawalAmount = backInfoItem.get("canWithdrawalAmount").getAsString();

                mBankCardEdit.setText(bankNum != null ? bankNum : "");
                mBankNameTv.setText(bankName != null ? bankName : "");
                mBankPersonTv.setText(bankPerson != null ? bankPerson : "");
                mBankMobileTv.setText(bankPhone != null ? bankPhone : "");
                mBalanceEdit.setText(bankAmout != null ? bankAmout : "");
                mAllowCashEdit.setText(canWithdrawalAmount != null ? canWithdrawalAmount : "");
            }
        });
    }


    private boolean isCashValid(String str) {
        Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"); // 判断小数点后2位的数字的正则表达式
        Matcher match = pattern.matcher(str);
        if (!match.matches()) {
            return false;
        } else {
            return true;
        }
    }

    private String getInformation(BankInfo bankInfo) {
        String result = "注意：\n每个月";
        if (bankInfo.getCashStart1() > 0 && bankInfo.getCashEnd1() > 0) {
            result = result + bankInfo.getCashStart1() + "日至" + bankInfo.getCashEnd1() + "日";
        }
        if (bankInfo.getCashStart2() > 0 && bankInfo.getCashEnd2() > 0) {
            result = result + "、" + bankInfo.getCashStart2() + "日至" + bankInfo.getCashEnd2() + "日";
        }
        if (bankInfo.getCashStart3() > 0 && bankInfo.getCashEnd3() > 0) {
            result = result + "、" + bankInfo.getCashStart3() + "日至" + bankInfo.getCashEnd3() + "日";
        }
        result = result + "为提现日，每个提现日只能提现一次。";

        return result;
    }

    private String getInputInformation(BankInfo bankInfo) {
        String result = "每月";
        if (bankInfo.getCashStart1() > 0 && bankInfo.getCashEnd1() > 0) {
            result = result + bankInfo.getCashStart1() + "日至" + bankInfo.getCashEnd1() + "日，将在" + bankInfo.getCashEnd1() + "日之后的第一个工作日汇款；";
        }
        if (bankInfo.getCashStart2() > 0 && bankInfo.getCashEnd2() > 0) {
            result = result + bankInfo.getCashStart2() + "日至" + bankInfo.getCashEnd2() + "日，将在" + bankInfo.getCashEnd2() + "日之后的第一个工作日汇款；";
        }
        if (bankInfo.getCashStart3() > 0 && bankInfo.getCashEnd3() > 0) {
            result = result + bankInfo.getCashStart3() + "日至" + bankInfo.getCashEnd3() + "日，将在" + bankInfo.getCashEnd3() + "日之后的第一个工作日汇款；";
        }

        return result;
    }

    private String getQuestion(BankInfo bankInfo) {
        return "为了保证维修的质量，需要质押维修金" + bankInfo.getPledgeDays() + "个工作日，" + bankInfo.getPledgeDays() + "个工作日后，方能将维修金提现。";
    }

    private String getQuestion(int day) {
        return "为了保证维修的质量，需要质押维修金" + day + "个自然日，" + day + "个自然日后，方能将维修金提现。";
    }


    private AsyncTask<Void, Void, Integer> mGetBankStatusTask;

    private void saveOperation() {
        if (!Utils.isNetworkAvailable(context)) {
            Utils.showShortToast(context, getString(R.string.common_no_network_message));
            return;
        }

        withdrawOperation();

        /*mWaitingDialog.show("正在获取提现信息，请稍候...", false);

        mGetBankStatusTask = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                int result = LogicFactory.getAccountLogic(context).getWithdrawAllowStatus();
                if (isCancelled()) {
                    return -1;
                }
                return result;
            }

            @Override
            protected void onPostExecute(Integer result) {
                mWaitingDialog.dismiss();
                if (result != -1) {
                    if (result == 0) {
                        Utils.showShortToast(context, "今天不是提现日或者您今天已经提现了！");
                    } else {
                        withdrawOperation();
                    }
                } else {
                    Utils.showShortToast(context, "未知错误，请稍候重试！");
                }
            }
        };

        mGetBankStatusTask.execute();*/
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public void withdrawOperation() {
        final WithdrawInputDialog withdrawDialog = new WithdrawInputDialog(context);
        withdrawDialog.show();

        XTextView infoTextView = (XTextView) withdrawDialog.findViewById(R.id.balance_withdraw_input_infomation);
        final XEditText moneyEditText = (XEditText) withdrawDialog.findViewById(R.id.balance_withdraw_input_edit);
        infoTextView.setText(mInputInformation);

        withdrawDialog.setClicklistener(new WithdrawInputDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                String moneyString = moneyEditText.getText().toString().trim();
                if (!Utils.isNotEmpty(moneyString)) {
                    Utils.showShortToast(context, "提现金额不能为空！");
                    return;
                }
                /*if (!Utils.isNumber(moneyString)) {
                    Utils.showShortToast(context, "您只能输入整形数字！");
                    return;
                }*/
                final double cashMoney = Double.parseDouble(mAllowCashEdit.getText().toString());
                double inputMoney;

                try {
                    inputMoney = Double.parseDouble(moneyString);
                } catch (Exception e) {
                    inputMoney = -1;
                }

                if (inputMoney > cashMoney || inputMoney <= 0.0) {
                    Utils.showShortToast(context, "您输入提现金额必须大于0小于" + cashMoney + "的整数！");
                    return;
                }

                mWaitingDialog.show("正在提交申请，请稍候...", false);

                ProfileApi profileApi = KHttpWorker.ins().createHttpService(LoginApi.URL, ProfileApi.class);
                Map<String, String> params = new HashMap<>();
                params.put("token", ConfigManager.getInstance(BalanceBankWithdrawActivity.this).getUserSession());
                params.put("userCode", ConfigManager.getInstance(BalanceBankWithdrawActivity.this).getUserCode());
                params.put("amount", moneyString);

                Call<JsonObject> call = profileApi.applyCash(params);

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                        Log.d("slog_zd", "response : " + response.body().toString());

                        JsonObject object = response.body();

                        final String msg = object.get("msg").getAsString();
                        final int code = object.get("code").getAsInt();

                        if (response.code() == APIConstants.RESULT_CODE_SUCCESS && code == 0) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mWaitingDialog != null) {
                                        mWaitingDialog.dismiss();
                                        Utils.showShortToast(BalanceBankWithdrawActivity.this, "您的申请已提交，请耐心等待");
                                    }

                                    if (withdrawDialog != null) {
                                        withdrawDialog.dismiss();
                                        loadData(); // 重新刷一次数据
                                    }

                                }
                            });
                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mWaitingDialog != null) {
                                        mWaitingDialog.dismiss();
                                        loadData(); // 重新刷一次数据
                                        Utils.showShortToast(BalanceBankWithdrawActivity.this, code + " : " + msg);
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
                                    loadData(); // 重新刷一次数据
                                    Utils.showShortToast(BalanceBankWithdrawActivity.this, "提现失败，请重试！");
                                }
                            }
                        });
                    }
                });


                /*new AsyncTask<String, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(String... params) {
                        return LogicFactory.getAccountLogic(context).saveWithdrawCashMoney(String.valueOf(inputMoney));
                    }

                    @Override
                    protected void onPostExecute(Integer result) {
                        mWaitingDialog.dismiss();
                        if (result == APIConstants.RESULT_CODE_SUCCESS) {
                            withdrawDialog.dismiss();
                            loadData();
                        } else {
                            if (result == 913) {
                                Utils.showShortToast(context, "提现失败：余额不足！");
                            } else if (result == 914) {
                                Utils.showShortToast(context, "提现失败：不能提现！");
                            } else {
                                Utils.showShortToast(context, "提现失败：未知原因，请重试！");
                            }
                        }
                    }
                }.execute();*/
            }
        });
    }

    @Override
    protected String getPageName() {
        return TAG;
    }

    @Override
    public void onDestroy() {
        try {
            if (mLoadDataTask != null) {
                mLoadDataTask.cancel(true);
            }
        } catch (Exception e) {
        }

        super.onDestroy();
    }

}

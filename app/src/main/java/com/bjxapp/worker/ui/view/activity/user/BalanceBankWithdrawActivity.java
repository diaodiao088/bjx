package com.bjxapp.worker.ui.view.activity.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.controls.XEditText;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.dialog.WithdrawInputDialog;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.BankInfo;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        String mobile = getString(R.string.service_telephone);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + mobile));
        startActivity(intent);
    }

    private void showBalanceInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("百家修");
        builder.setMessage(mQuestionInformation);
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
        mLoadDataTask = new AsyncTask<String, Void, BankInfo>() {
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
        mLoadDataTask.execute();
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

    private AsyncTask<Void, Void, Integer> mGetBankStatusTask;

    private void saveOperation() {
        if (!Utils.isNetworkAvailable(context)) {
            Utils.showShortToast(context, getString(R.string.common_no_network_message));
            return;
        }

        mWaitingDialog.show("正在获取提现信息，请稍候...", false);
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

        mGetBankStatusTask.execute();
    }

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
                if (!Utils.isNumber(moneyString)) {
                    Utils.showShortToast(context, "您只能输入整形数字！");
                    return;
                }
                final double cashMoney = Double.parseDouble(mAllowCashEdit.getTag().toString());
                final double inputMoney = Double.parseDouble(moneyString);
                if (inputMoney > cashMoney || inputMoney <= 0.0) {
                    Utils.showShortToast(context, "您输入提现金额必须大于0小于" + cashMoney + "的整数！");
                    return;
                }

                mWaitingDialog.show("正在提交申请，请稍候...", false);
                new AsyncTask<String, Void, Integer>() {
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
                }.execute();
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

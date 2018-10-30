package com.bjxapp.worker.ui.view.fragment;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bjxapp.worker.SplashActivity;
import com.bjxapp.worker.controls.XCircleImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ActivitiesManager;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.UserApplyInfo;
import com.bjxapp.worker.ui.view.activity.WebViewActivity;
import com.bjxapp.worker.ui.view.activity.user.ApplyActivity;
import com.bjxapp.worker.ui.view.activity.user.BalanceBankActivity;
import com.bjxapp.worker.ui.view.activity.user.BalanceBankWithdrawActivity;
import com.bjxapp.worker.ui.view.base.BaseFragment;
import com.bjxapp.worker.ui.widget.FlowLayout;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager.DataType;
import com.bjxapp.worker.utils.image.BitmapManager;
import com.bjxapp.worker.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Fragment_Main_Fourth extends BaseFragment implements OnClickListener {
    protected static final String TAG = "我的";

    @BindView(R.id.me_phone_tv)
    TextView mMobileTv;

    @BindView(R.id.me_name_tv)
    TextView mUserName;

    @BindView(R.id.me_phone_year)
    TextView mYearsTv;

    @BindView(R.id.me_header_iv)
    XCircleImageView mHeadImage;

    @BindView(R.id.total_bill_tv)
    TextView mTotalBillsTv;

    @BindView(R.id.total_cash_tv)
    TextView mTotalCashTv;

    @BindView(R.id.rank_tv)
    TextView mRankTv;

    @BindView(R.id.flow_ly)
    FlowLayout mFlowLy;

    @OnClick(R.id.me_about_ly)
    void onClickAbout() {
        Utils.startActivity(mActivity, WebViewActivity.class,
                new BasicNameValuePair("title", "关于百家修"),
                new BasicNameValuePair("url", getString(R.string.service_about_url)));
    }


    @OnClick(R.id.balance_cash_ly)
    void onClickBalance() {
        showWithdraw();
    }

    @OnClick(R.id.log_out_btn)
    void onClickLogOut() {
        logOut();
    }

    @OnClick(R.id.me_service_phone_ly)
    void onCallClick() {
        callService();
    }

    @OnClick(R.id.me_update_ly)
    void onClickUpdate() {
        checkUpdate();
    }


    private XWaitingDialog mWaitingDialog;

    @Override
    protected void initView() {
        initViews();
        //loadData();
    }

    @Override
    protected void finish() {

    }

    @Override
    protected int onCreateContent() {
        return R.layout.fragment_me;
    }

    @Override
    public void refresh(int enterType) {
        // loadData();
    }

    private void initViews() {

        String userMobile = ConfigManager.getInstance(mActivity).getUserCode();
        mMobileTv.setText(userMobile);
        mUserName.setText(ConfigManager.getInstance(mActivity).getUserName());
        mWaitingDialog = new XWaitingDialog(mActivity);
        displayHeadImage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_user_balance_money_get_button:
                showWithdraw();
                break;
            case R.id.profile_user_info:
                Utils.startActivityForResult(mActivity, Fragment_Main_Fourth.this, ApplyActivity.class, Constant.ACTIVITY_APPLY_RESULT_CODE);
                break;

            case R.id.profile_service_call:
                callService();
                break;
            case R.id.profile_check_update:
                checkUpdate();
                break;
            case R.id.profile_logout:
                logOut();
                break;
            default:
                break;
        }
    }

    private void checkUpdate() {
        if (LogicFactory.getUpdateLogic(mActivity).isNeedUpdate(mActivity, false)) {
            LogicFactory.getUpdateLogic(mActivity).showUpdateDialog(mActivity);
        } else {
            Utils.showLongToast(mActivity, "已经是最新版本了，无需更新！");
        }
    }

    private void logOut() {
        ConfigManager.getInstance(mActivity).setUserCode("");
        ConfigManager.getInstance(mActivity).setUserSession("");
        ConfigManager.getInstance(mActivity).setUserName("");
        ConfigManager.getInstance(mActivity).setUserStatus(-1);
        ConfigManager.getInstance(mActivity).setUserHeadImageUrl("");
        ConfigManager.getInstance(mActivity).setDesktopMessagesDot(0);
        ConfigManager.getInstance(mActivity).setDesktopMessagesDotServer(0);
        ConfigManager.getInstance(mActivity).setDesktopOrdersDot(0);
        ConfigManager.getInstance(mActivity).setDesktopOrdersDotServer(0);
        ActivitiesManager.getInstance().finishAllActivities();
        Utils.startActivity(mActivity, SplashActivity.class);
    }

    private void callService() {
        String mobile = getString(R.string.service_telephone);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + mobile));
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.ACTIVITY_APPLY_RESULT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    mYearsTv.setText(data.getIntExtra("workyears", 1) + "年经验");
                    mUserName.setText(ConfigManager.getInstance(mActivity).getUserName());
                    displayHeadImage();
                }
                break;
        }
    }

    private void displayHeadImage() {
        String imageUrl = ConfigManager.getInstance(mActivity).getUserHeadImageUrl();
        if (!Utils.isNotEmpty(imageUrl)) return;

        try {
            BitmapManager.OnBitmapLoadListener mOnBitmapLoadListener = new BitmapManager.OnBitmapLoadListener() {
                @Override
                public void onLoaded(String url, Bitmap bitmap, boolean isSuccessful) {
                    if (isSuccessful && bitmap != null) {
                        mHeadImage.setImageBitmap(bitmap);
                    }
                }
            };

            BitmapManager.getInstance(mActivity).loadBitmap(imageUrl, DataType.UserData, mOnBitmapLoadListener);
        } catch (Exception e) {

        }
    }

    private AsyncTask<String, Void, UserApplyInfo> mLoadDataTask;

    private void loadData() {
        mLoadDataTask = new AsyncTask<String, Void, UserApplyInfo>() {
            @Override
            protected UserApplyInfo doInBackground(String... params) {
                return LogicFactory.getAccountLogic(mActivity).getRegisterInfo();
            }

            @Override
            protected void onPostExecute(UserApplyInfo result) {
                if (result == null) {
                    return;
                }

                mUserName.setText(result.getPersonName());
                mYearsTv.setText(result.getWorkYear() + "年经验");

                ConfigManager.getInstance(mActivity).setUserHeadImageUrl(result.getHeadImageUrl());
                displayHeadImage();
            }
        };
        mLoadDataTask.execute();
    }

    private AsyncTask<Void, Void, Integer> mGetBankStatusTask;

    private void showWithdraw() {

        if (mWaitingDialog == null){
            mWaitingDialog = new XWaitingDialog(mActivity);
        }

        mWaitingDialog.show("正在查询银行信息，请稍候...", false);

        mGetBankStatusTask = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                int result = LogicFactory.getAccountLogic(mActivity).getBalanceBankStatus();
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
                        Utils.startActivity(mActivity, BalanceBankActivity.class);
                    } else {
                        Utils.startActivity(mActivity, BalanceBankWithdrawActivity.class);
                    }
                } else {
                    Utils.showShortToast(mActivity, "未知错误，请稍候重试！");
                }
            }
        };

        mGetBankStatusTask.execute();
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
            if (mGetBankStatusTask != null) {
                mGetBankStatusTask.cancel(true);
            }
        } catch (Exception e) {
        }

        super.onDestroy();
    }

}
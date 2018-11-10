package com.bjxapp.worker.ui.view.fragment;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bjxapp.worker.App;
import com.bjxapp.worker.SplashActivity;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.ProfileApi;
import com.bjxapp.worker.controls.XCircleImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ActivitiesManager;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.AccountInfo;
import com.bjxapp.worker.model.EvaluationStatInfo;
import com.bjxapp.worker.model.LabelStat;
import com.bjxapp.worker.model.UserApplyInfo;
import com.bjxapp.worker.model.UserInfo;
import com.bjxapp.worker.model.UserInfoDetail;
import com.bjxapp.worker.ui.view.activity.WebViewActivity;
import com.bjxapp.worker.ui.view.activity.user.ApplyActivity;
import com.bjxapp.worker.ui.view.activity.user.ApplyEditActivity;
import com.bjxapp.worker.ui.view.activity.user.BalanceBankActivity;
import com.bjxapp.worker.ui.view.activity.user.BalanceBankWithdrawActivity;
import com.bjxapp.worker.ui.view.base.BaseFragment;
import com.bjxapp.worker.ui.view.fragment.ctrl.UserInfoManagerCtrl;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.FlowLayout;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager.DataType;
import com.bjxapp.worker.utils.image.BitmapManager;
import com.bjxapp.worker.R;
import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @BindView(R.id.attitude_view)
    TextView mAttitudeView;
    @BindView(R.id.attitude_percent_tv)
    TextView mAttitudeTv;
    @BindView(R.id.skill_view)
    TextView mSkillView;
    @BindView(R.id.skill_percent_tv)
    TextView mSkillTv;
    @BindView(R.id.look_view)
    TextView mLookView;
    @BindView(R.id.look_percent_tv)
    TextView mLookTv;

    public static final int MAX_VIEW_WIDTH = DimenUtils.dp2px(200, App.getInstance());

    @OnClick(R.id.me_header)
    void onClickHeader(){
        ApplyEditActivity.goToActivity(getActivity());
    }

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
        loadData();
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
        loadData();
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

        startLogoutReal();

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

    private void startLogoutReal() {

        LoginApi loginApi = KHttpWorker.ins().createHttpService(LoginApi.URL, LoginApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(getActivity()).getUserSession());
        params.put("userCode", ConfigManager.getInstance(getActivity()).getUserCode());

        Call<JsonObject> call = loginApi.logOut(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
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
        /*mLoadDataTask = new AsyncTask<String, Void, UserApplyInfo>() {
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
        mLoadDataTask.execute();*/

        ProfileApi profileApi = KHttpWorker.ins().createHttpService(LoginApi.URL, ProfileApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("userCode", ConfigManager.getInstance(getContext()).getUserCode());
        params.put("token", ConfigManager.getInstance(getContext()).getUserSession());

        Call<JsonObject> request = profileApi.getProfileDetail(params);

        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("slog_zd", "profile : " + response.body().toString());

                JsonObject result = response.body();
                int code = result.get("code").getAsInt();
                if (response.code() == APIConstants.RESULT_CODE_SUCCESS && code == 0) {

                    UserInfo userInfo = new UserInfo();
                    JsonObject masterItem = result.getAsJsonObject("master");

                    userInfo.setmPhoneNum(masterItem.get("phone").getAsString());
                    userInfo.setmServiceStat(masterItem.get("serviceState").getAsInt());

                    JsonObject accountItem = masterItem.getAsJsonObject("account");
                    AccountInfo accountInfo = new AccountInfo(accountItem.get("balanceAmount").getAsFloat(),
                            accountItem.get("canWithdrawalAmount").getAsFloat(),
                            accountItem.get("incomeRank").getAsInt(),
                            accountItem.get("orderQuantity").getAsInt(),
                            accountItem.get("totalIncome").getAsFloat(),
                            accountItem.get("totalOrderAmount").getAsFloat(),
                            accountItem.get("withdrawnAmount").getAsFloat());
                    userInfo.setAccountInfo(accountInfo);

                    JsonObject evaluationStatInfo = masterItem.getAsJsonObject("evaluationStat");

                    EvaluationStatInfo statInfo = new EvaluationStatInfo();

                    statInfo.setAppearanceLevel(evaluationStatInfo.get("appearanceLevel").getAsFloat());
                    statInfo.setAttitudeLevel(evaluationStatInfo.get("attitudeLevel").getAsFloat());
                    statInfo.setSkillLevel(evaluationStatInfo.get("skillLevel").getAsFloat());

                    JsonArray labelArray = evaluationStatInfo.getAsJsonArray("labelList");

                    statInfo.getmLabelList().clear();

                    if (labelArray != null && labelArray.size() > 0) {

                        for (int i = 0; i < labelArray.size(); i++) {
                            JsonObject labelItem = (JsonObject) labelArray.get(i);

                            LabelStat statItem = new LabelStat(labelItem.get("labelName").getAsString(),
                                    labelItem.get("quantity").getAsInt());

                            statInfo.getmLabelList().add(statItem);
                        }
                    }

                    userInfo.setEvaluationStatInfo(statInfo);

                    JsonObject infoDetail = masterItem.getAsJsonObject("info");
                    UserInfoDetail infoDetail1 = new UserInfoDetail(infoDetail.get("avatarUrl").getAsString(),
                            infoDetail.get("identityCardBehindImgUrl").getAsString(),
                            infoDetail.get("identityCardFrontImgUrl").getAsString(),
                            infoDetail.get("latitude").getAsString(),
                            infoDetail.get("locationAddress").getAsString(),
                            infoDetail.get("longitude").getAsString(),
                            infoDetail.get("name").getAsString(),
                            infoDetail.get("regionId").getAsString(),
                            infoDetail.get("regionName").getAsString(),
                            infoDetail.get("workingYear").getAsInt());

                    userInfo.setInfoDetail(infoDetail1);
                    UserInfoManagerCtrl.getsIns().setmUserInfo(userInfo);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUiStatus();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void updateUiStatus() {

        updateAcccountInfo();

        updateStatInfo();

        updateUserInfo();
    }

    private void updateUserInfo() {

        UserInfoDetail detail = UserInfoManagerCtrl.getsIns().getmUserInfo().getInfoDetail();

        if (detail != null) {

            Glide.with(this).load(detail.getAvatarUrl()).into(mHeadImage);

            mUserName.setText(detail.getName());
            mYearsTv.setText(detail.getmWorkingYear() + "年经验");
            mMobileTv.setText(UserInfoManagerCtrl.getsIns().getmUserInfo().getmPhoneNum());
        }
    }

    private void updateAcccountInfo() {
        AccountInfo accountInfo = UserInfoManagerCtrl.getsIns().getmUserInfo().getAccountInfo();

        if (accountInfo != null) {
            mTotalBillsTv.setText(String.valueOf(accountInfo.getOrderQuantity()));
            mRankTv.setText(String.valueOf(accountInfo.getIncomeRank()));
            mTotalCashTv.setText(String.valueOf(accountInfo.getTotalIncome()));
        }
    }

    private void updateStatInfo() {

        EvaluationStatInfo statInfo = UserInfoManagerCtrl.getsIns().getmUserInfo().getEvaluationStatInfo();

        if (statInfo != null) {

            float appearPercent = statInfo.getAppearanceLevel() / 5;
            mLookView.setWidth((int) (MAX_VIEW_WIDTH * appearPercent));
            mLookTv.setText(formattedDecimalToPercentage(appearPercent));

            float skillPercent = statInfo.getSkillLevel() / 5;
            mSkillView.setWidth((int) (MAX_VIEW_WIDTH * skillPercent));
            mSkillTv.setText(formattedDecimalToPercentage(skillPercent));

            float attitudePercent = statInfo.getAttitudeLevel() / 5;
            mAttitudeView.setWidth((int) (MAX_VIEW_WIDTH * attitudePercent));
            mAttitudeTv.setText(formattedDecimalToPercentage(attitudePercent));

            ArrayList<LabelStat> labelList = statInfo.getmLabelList();

            int padding = DimenUtils.dp2px(3, App.getInstance());

            mFlowLy.removeAllViews();

            for (int i = 0; i < labelList.size(); i++) {

                LabelStat statItem = labelList.get(i);
                TextView textItem = new TextView(getActivity());
                textItem.setPadding(padding, padding, padding, padding);
                textItem.setTextSize(10);
                textItem.setBackgroundResource(R.drawable.score_bg);
                ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = DimenUtils.dp2px(6, App.getInstance());
                params.rightMargin = DimenUtils.dp2px(12, App.getInstance());

                textItem.setLayoutParams(params);
                textItem.setText(statItem.getLabelName() + "(" + statItem.getQuantity() + ")");
                mFlowLy.addView(textItem);
            }
        }
    }

    private static String formattedDecimalToPercentage(double decimal) {
        //获取格式化对象
        NumberFormat nt = NumberFormat.getPercentInstance();
        //设置百分数精确度2即保留两位小数
        nt.setMinimumFractionDigits(0);
        return nt.format(decimal);
    }

    private void showWithdraw() {

        if (mWaitingDialog == null) {
            mWaitingDialog = new XWaitingDialog(mActivity);
        }

        mWaitingDialog.show("正在查询银行信息，请稍候...", false);

        ProfileApi profileApi = KHttpWorker.ins().createHttpService(LoginApi.URL, ProfileApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(getActivity()).getUserSession());
        params.put("userCode", ConfigManager.getInstance(getActivity()).getUserCode());

        Call<JsonObject> call = profileApi.getBankInfo(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject object = response.body();

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS && object.get("code").getAsInt() == 0) {

                    if (object.get("bankInfo") instanceof JsonNull){
                        goToBankActivity();
                        return;
                    }

                    JsonObject bankInfoItem = object.getAsJsonObject("bankInfo");
                    if (bankInfoItem == null || bankInfoItem.get("bankAccountName") == null) {
                        goToBankActivity();
                    } else {
                        getBankInfoSucc();
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
    }

    private void getBankInfoFailed() {
        if (mActivity != null && !mActivity.isFinishing()) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mWaitingDialog != null) {
                        mWaitingDialog.dismiss();
                    }
                    Utils.showShortToast(mActivity, "未知错误，请稍候重试！");
                }
            });
        }
    }

    private void goToBankActivity(){
        if (mActivity != null && !mActivity.isFinishing()) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mWaitingDialog != null) {
                        mWaitingDialog.dismiss();
                    }
                    Utils.startActivity(mActivity, BalanceBankActivity.class);
                }
            });
        }
    }

    private void getBankInfoSucc() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mWaitingDialog != null) {
                    mWaitingDialog.dismiss();
                }
                Utils.startActivity(mActivity, BalanceBankWithdrawActivity.class);
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
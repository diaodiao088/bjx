package com.bjxapp.worker.ui.view.activity.user;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.bjx.master.R;;
import com.bjxapp.worker.adapter.WithdrawAdapter;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.ProfileApi;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.controls.listview.XListView;
import com.bjxapp.worker.controls.listview.XListView.IXListViewListener;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.WithdrawInfo;
import com.bjxapp.worker.model.WithdrawList;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Logger;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BalanceWithdrawHistoryActivity extends BaseActivity implements OnClickListener, IXListViewListener {
    protected static final String TAG = "提现历史界面";
    private XTextView mTitleTextView;
    private XImageView mBackImageView;

    private RelativeLayout mLoadAgainLayout;
    private ArrayList<WithdrawInfo> mWithdrawArray = new ArrayList<WithdrawInfo>();
    private WithdrawAdapter mWithdrawAdapter;
    private XListView mXListView;
    private XTextView mWithdrawTotalMoney;
    private int mBatchCount = 10;
    private int mCurrentBatch = 1;

    private XWaitingDialog mWaitingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_balance_withdraw_list);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initControl() {
        mTitleTextView = (XTextView) findViewById(R.id.title_text_tv);
        mTitleTextView.setText("提现历史");
        mBackImageView = (XImageView) findViewById(R.id.title_image_back);
        mBackImageView.setVisibility(View.VISIBLE);

        mWithdrawTotalMoney = (XTextView) findViewById(R.id.withdraw_list_total_money);

        mWaitingDialog = new XWaitingDialog(context);

        mLoadAgainLayout = (RelativeLayout) findViewById(R.id.withdraw_list_load_again);
        mLoadAgainLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onFirstLoadData(true);
            }
        });

        mXListView = (XListView) findViewById(R.id.withdraw_list_listview);
        mWithdrawAdapter = new WithdrawAdapter(context, mWithdrawArray);
        mXListView.setAdapter(mWithdrawAdapter);
        mXListView.setCacheColorHint(Color.TRANSPARENT);
        mXListView.setPullLoadEnable(true);
        mXListView.setPullRefreshEnable(false);
        mXListView.setXListViewListener(this);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        onFirstLoadData(false);
    }

    @Override
    protected void setListener() {
        mBackImageView.setOnClickListener(this);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_image_back:
                Utils.finishActivity(BalanceWithdrawHistoryActivity.this);
                break;
            default:
                break;
        }
    }

    private void onLoadFinished() {
        mXListView.stopRefresh();
        mXListView.stopLoadMore();
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //String refreshTimeString = format.format(new Date());
        //mXListView.setRefreshTime(refreshTimeString);
    }

    private AsyncTask<Void, Void, WithdrawList> mFirstLoadTask;

    private String mCreateTime;

    private String getFormatedTime() {

        if (TextUtils.isEmpty(mCreateTime)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mCreateTime = format.format(new Date());
        }

        return mCreateTime;
    }

    private void onFirstLoadData(final Boolean loading) {

        if (!Utils.isNetworkAvailable(context)) {
            Utils.showShortToast(context, getString(R.string.common_no_network_message));
            return;
        }

        if (loading) {
            mWaitingDialog.show("正在加载中，请稍候...", false);
        }

        ProfileApi profileApi = KHttpWorker.ins().createHttpService(LoginApi.URL, ProfileApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("pageNum", String.valueOf(1));
        params.put("pageSize", String.valueOf(20));
        params.put("endCreateTime", getFormatedTime());

        Call<JsonObject> call = profileApi.getWithDrawHistory(params);

        call.enqueue(new Callback<JsonObject>() {
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


                JsonObject jsonObject = response.body();
                String msg = jsonObject.get("msg").getAsString();
                int code = jsonObject.get("code").getAsInt();

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS && code == 0) {

                    final String withdrawnAmount = jsonObject.get("withdrawnAmount").getAsString();

                    JsonObject pageObject = jsonObject.getAsJsonObject("page");

                    String total = pageObject.get("total").getAsString();

                    JsonArray itemArray = pageObject.getAsJsonArray("list");

                    if (itemArray != null && itemArray.size() > 0) {
                        mWithdrawArray.clear();
                        for (int i = 0; i < itemArray.size(); i++) {
                            JsonObject item = (JsonObject) itemArray.get(i);
                            WithdrawInfo withdrawInfoItem = new WithdrawInfo();
                            withdrawInfoItem.setDate(item.get("createTime").getAsString());
                            withdrawInfoItem.setMoney(item.get("amount").getAsString());
                            withdrawInfoItem.setStatus(item.get("status").getAsInt());
                            mWithdrawArray.add(withdrawInfoItem);
                        }
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCurrentBatch = 1;

                            mWithdrawTotalMoney.setText((TextUtils.isEmpty(withdrawnAmount) ? 0 : withdrawnAmount) + "元");

                            mWithdrawAdapter = new WithdrawAdapter(context, mWithdrawArray);
                            mXListView.setAdapter(mWithdrawAdapter);
                            mCurrentBatch++;
                            onLoadFinished();

                            if (mWithdrawArray.size() > 0) {
                                mLoadAgainLayout.setVisibility(View.GONE);
                                mXListView.setVisibility(View.VISIBLE);
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
                        if (mWaitingDialog != null){
                            mWaitingDialog.dismiss();
                        }
                    }
                });
            }
        });

		/*mFirstLoadTask = new AsyncTask<Void, Void, WithdrawList>() {
            @Override
			protected WithdrawList doInBackground(Void... params) {
				return LogicFactory.getAccountLogic(context).getWithdrawList(1, mBatchCount);
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void onPostExecute(WithdrawList result) {
				if (loading) {
					mWaitingDialog.dismiss();
				}
				if (result == null) {
					mLoadAgainLayout.setVisibility(View.VISIBLE);
					mXListView.setVisibility(View.GONE);
					return;
				}

				mWithdrawTotalMoney.setText("¥" + result.getTotalMoney() + "元");

				mCurrentBatch = 1;
				mWithdrawArray.clear();
				mWithdrawArray.addAll((List<WithdrawInfo>) result.getDataObject());
				mWithdrawAdapter = new WithdrawAdapter(context, mWithdrawArray);
				mXListView.setAdapter(mWithdrawAdapter);
				mCurrentBatch++;

				if (mWithdrawArray.size() > 0) {
					mLoadAgainLayout.setVisibility(View.GONE);
					mXListView.setVisibility(View.VISIBLE);
				}
			}
		};
		mFirstLoadTask.execute();*/
    }

    private AsyncTask<Void, Void, WithdrawList> mRefreshTask;

    @Override
    public void onRefresh() {

    }

    private AsyncTask<Void, Void, WithdrawList> mLoadMoreTask;

    @Override
    public void onLoadMore() {

        if (!Utils.isNetworkAvailable(context)) {
            Utils.showShortToast(context, getString(R.string.common_no_network_message));
            return;
        }

        ProfileApi profileApi = KHttpWorker.ins().createHttpService(LoginApi.URL, ProfileApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("pageNum", String.valueOf(mCurrentBatch));
        params.put("pageSize", String.valueOf(20));
        params.put("endCreateTime", getFormatedTime());

        Call<JsonObject> call = profileApi.getWithDrawHistory(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject jsonObject = response.body();
                String msg = jsonObject.get("msg").getAsString();
                int code = jsonObject.get("code").getAsInt();

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS && code == 0) {

                    final String withdrawnAmount = jsonObject.get("withdrawnAmount").getAsString();

                    JsonObject pageObject = jsonObject.getAsJsonObject("page");

                    String total = pageObject.get("total").getAsString();

                    JsonArray itemArray = pageObject.getAsJsonArray("list");

                    final ArrayList<WithdrawInfo> arrayList = new ArrayList<>();

                    if (itemArray != null && itemArray.size() > 0) {
                        for (int i = 0; i < itemArray.size(); i++) {
                            JsonObject item = (JsonObject) itemArray.get(i);
                            WithdrawInfo withdrawInfoItem = new WithdrawInfo();
                            withdrawInfoItem.setDate(item.get("createTime").getAsString());
                            withdrawInfoItem.setMoney(item.get("amount").getAsString());
                            withdrawInfoItem.setStatus(item.get("status").getAsInt());
                            arrayList.add(withdrawInfoItem);
                        }
                    } else {

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(context, getString(R.string.common_no_more_data_message));
                                onLoadFinished();
                            }
                        });

                        return;
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mWithdrawArray.addAll((arrayList));
                            mWithdrawAdapter.notifyDataSetChanged();
                            onLoadFinished();
                            mCurrentBatch++;
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

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
            if (mFirstLoadTask != null) {
                mFirstLoadTask.cancel(true);
            }
            if (mRefreshTask != null) {
                mRefreshTask.cancel(true);
            }
        } catch (Exception e) {
        }

        super.onDestroy();
    }

}

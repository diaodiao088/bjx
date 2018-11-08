package com.bjxapp.worker.ui.view.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.bjxapp.worker.adapter.OrderAdapter;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.ProfileApi;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.controls.listview.XListView;
import com.bjxapp.worker.controls.listview.XListView.IXListViewListener;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.AccountInfo;
import com.bjxapp.worker.model.OrderDes;
import com.bjxapp.worker.model.ReceiveOrder;
import com.bjxapp.worker.ui.view.activity.order.OrderDetailActivity;
import com.bjxapp.worker.ui.view.base.BaseFragment;
import com.bjxapp.worker.utils.Logger;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.R;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Main_Second extends BaseFragment implements OnClickListener, IXListViewListener {

    protected static final String TAG = "历史";

    private RelativeLayout mLoadAgainLayout;
    private XWaitingDialog mWaitingDialog;
    private ArrayList<OrderDes> mOrdersArray = new ArrayList<OrderDes>();
    private OrderAdapter mOrderAdapter;
    private XListView mXListView;

    private int mCurrentBatch = 1;
    private int mBatchSize = 20;

    private static final int TYPE_TOTAL = 0x01;
    private static final int TYPE_SAFE = 0X02;
    private static final int TYPE_UNSAFE = 0X03;

    private int mCurrentType;

    private LinearLayout mHistoryTotalLy, mHistorySafeLy, mHistoryUnsafeLy;
    private TextView mHistoryTotalTv, mHistorySafeTv, mHistoryUnsafeTv;
    private View mHistoryTotalDiv, mHistorySafeDiv, mHistoryUnsafeDiv;

    @Override
    protected void initView() {
        registerUpdateUIBroadcast();
        initViews();
        setOnListener();
        onFirstLoadData(false);
    }

    @Override
    protected void finish() {

    }

    @Override
    public void onResume() {
        super.onResume();
        loadBillInfo();
    }

    @Override
    protected int onCreateContent() {
        return R.layout.fragment_main_second;
    }

    @Override
    public void refresh(int enterType) {
    }

    private void loadBillInfo() {

        ProfileApi profileApi = KHttpWorker.ins().createHttpService(LoginApi.URL, ProfileApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("userCode", ConfigManager.getInstance(getContext()).getUserCode());
        params.put("token", ConfigManager.getInstance(getContext()).getUserSession());

        Call<JsonObject> request = profileApi.getAccountInfo(params);

        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject result = response.body();
                int code = result.get("code").getAsInt();

                if (code == 0) {
                    JsonObject accountItem = result.getAsJsonObject("account");

                    final AccountInfo accountInfo = new AccountInfo(accountItem.get("balanceAmount").getAsFloat(),
                            accountItem.get("canWithdrawalAmount").getAsFloat(),
                            accountItem.get("incomeRank").getAsInt(),
                            accountItem.get("orderQuantity").getAsInt(),
                            accountItem.get("totalIncome").getAsFloat(),
                            accountItem.get("totalOrderAmount").getAsFloat(),
                            accountItem.get("withdrawnAmount").getAsFloat());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateAccountInfo(accountInfo);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void updateAccountInfo(AccountInfo accountInfo) {
        mHistoryTotalTv.setText("累计订单：" + accountInfo.getOrderQuantity() + "单");
        mHistorySafeTv.setText("累计金额" + accountInfo.getTotalOrderAmount() + "元");
        mHistoryUnsafeTv.setText("累计收入" + accountInfo.getWithdrawnAmount() + "元");
    }

    private void initViews() {
        mLoadAgainLayout = (RelativeLayout) findViewById(R.id.order_history_list_load_again);
        mLoadAgainLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onFirstLoadData(true);
            }
        });

        mXListView = (XListView) findViewById(R.id.order_history_list_listview);
        mOrderAdapter = new OrderAdapter(mActivity, mOrdersArray);
        mXListView.setAdapter(mOrderAdapter);
        mXListView.setCacheColorHint(Color.TRANSPARENT);
        mXListView.setPullLoadEnable(true);
        mXListView.setPullRefreshEnable(true);
        mXListView.setXListViewListener(this);

        mXListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReceiveOrder order = (ReceiveOrder) mXListView.getItemAtPosition(position);
                startOrderDetailActivity(String.valueOf(order.getOrderID()));
            }
        });

        mWaitingDialog = new XWaitingDialog(mActivity);

        mHistoryTotalLy = (LinearLayout) findViewById(R.id.history_total_ly);
        mHistorySafeLy = (LinearLayout) findViewById(R.id.history_safe_ly);
        mHistoryUnsafeLy = (LinearLayout) findViewById(R.id.history_unsafe_ly);

        mHistoryTotalTv = (TextView) findViewById(R.id.history_total_tv);
        mHistorySafeTv = (TextView) findViewById(R.id.history_safe_tv);
        mHistoryUnsafeTv = (TextView) findViewById(R.id.history_unsafe_tv);

        mHistoryTotalDiv = findViewById(R.id.history_total_divider);
        mHistorySafeDiv = findViewById(R.id.history_safe_divider);
        mHistoryUnsafeDiv = findViewById(R.id.history_unsafe_divider);

        mHistorySafeDiv.setVisibility(View.GONE);
        mHistoryUnsafeDiv.setVisibility(View.GONE);

        mHistoryTotalLy.setOnClickListener(this);
        mHistorySafeLy.setOnClickListener(this);
        mHistoryUnsafeLy.setOnClickListener(this);
    }

    private void setOnListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.history_total_ly:
                changeStatus(TYPE_TOTAL);
                break;
            case R.id.history_safe_ly:
                changeStatus(TYPE_SAFE);
                break;
            case R.id.history_unsafe_ly:
                changeStatus(TYPE_UNSAFE);
                break;
            default:
                break;
        }
    }

    private void onLoadFinished() {
        mXListView.stopRefresh();
        mXListView.stopLoadMore();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String refreshTimeString = format.format(new Date());
        mXListView.setRefreshTime(refreshTimeString);
    }

    private AsyncTask<Void, Void, List<ReceiveOrder>> mFirstLoadTask;

    private void onFirstLoadData(final Boolean loading) {
        /*if (Utils.isNetworkAvailable(mActivity)) {
            if (loading) {
                mWaitingDialog.show("正在加载中，请稍候...", false);
            }
            mFirstLoadTask = new AsyncTask<Void, Void, List<ReceiveOrder>>() {
                @Override
                protected List<ReceiveOrder> doInBackground(Void... params) {
                    return LogicFactory.getDesktopLogic(mActivity).getHistoryOrders(1, mBatchSize);
                }

                @Override
                protected void onPostExecute(List<ReceiveOrder> result) {
                    if (loading) {
                        mWaitingDialog.dismiss();
                    }

                    if (result == null) {
                        mLoadAgainLayout.setVisibility(View.VISIBLE);
                        mXListView.setVisibility(View.GONE);
                        return;
                    }
                    mCurrentBatch = 1;
                    mOrdersArray.clear();
                    mOrdersArray.addAll(result);
                    mOrderAdapter = new OrderAdapter(mActivity, mOrdersArray);
                    mXListView.setAdapter(mOrderAdapter);
                    mCurrentBatch++;

                    if (mOrdersArray.size() > 0) {
                        mLoadAgainLayout.setVisibility(View.GONE);
                        mXListView.setVisibility(View.VISIBLE);
                    }
                }
            };
            mFirstLoadTask.execute();
        } else {
            Utils.showShortToast(mActivity, getString(R.string.common_no_network_message));
        }*/
    }

    private AsyncTask<Void, Void, List<ReceiveOrder>> mRefreshTask;

    @Override
    public void onRefresh() {
        /*mRefreshTask = new AsyncTask<Void, Void, List<ReceiveOrder>>() {
            @Override
            protected List<ReceiveOrder> doInBackground(Void... params) {
                return LogicFactory.getDesktopLogic(mActivity).getHistoryOrders(1, mBatchSize);
            }

            @Override
            protected void onPostExecute(List<ReceiveOrder> result) {
                if (result == null) {
                    try {
                        onLoadFinished();
                    } catch (Exception e) {
                        Logger.i(e.getMessage());
                    }
                    return;
                }
                mCurrentBatch = 1;
                mOrdersArray.clear();
                mOrdersArray.addAll(result);
                mOrderAdapter = new OrderAdapter(mActivity, mOrdersArray);
                mXListView.setAdapter(mOrderAdapter);
                mCurrentBatch++;

                onLoadFinished();
            }
        };
        mRefreshTask.execute();*/
    }

    private AsyncTask<Void, Void, List<ReceiveOrder>> mLoadMoreTask;

    @Override
    public void onLoadMore() {
        /*mLoadMoreTask = new AsyncTask<Void, Void, List<ReceiveOrder>>() {
            @Override
            protected List<ReceiveOrder> doInBackground(Void... params) {
                return LogicFactory.getDesktopLogic(mActivity).getHistoryOrders(mCurrentBatch, mBatchSize);
            }

            @Override
            protected void onPostExecute(List<ReceiveOrder> result) {
                if (result == null) {
                    try {
                        Utils.showShortToast(mActivity, getString(R.string.common_no_more_data_message));
                        onLoadFinished();
                    } catch (Exception e) {
                        Logger.i(e.getMessage());
                    }
                    return;
                }
                mOrdersArray.addAll(result);
                mOrderAdapter.notifyDataSetChanged();
                onLoadFinished();
                mCurrentBatch++;
            }
        };
        mLoadMoreTask.execute();*/
    }

    private void startOrderDetailActivity(String orderID) {
        Intent intent = new Intent();
        intent.setClass(mActivity, OrderDetailActivity.class);
        intent.putExtra("order_id", orderID);
        startActivityForResult(intent, Constant.ACTIVITY_ORDER_DETAIL_RESULT_CODE);
        mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 定义广播接收器（内部类）
     *
     * @author Jason
     */
    private UpdateUIBroadcastReceiver broadcastReceiver;

    private class UpdateUIBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //String message = intent.getStringExtra("message");
            //Utils.showLongToast(mActivity, message);
            onFirstLoadData(false);
        }
    }

    /**
     * 动态注册广播
     */
    private void registerUpdateUIBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.PUSH_ACTION_ORDER_MODIFIED);
        broadcastReceiver = new UpdateUIBroadcastReceiver();
        mActivity.registerReceiver(broadcastReceiver, filter);
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
            if (mLoadMoreTask != null) {
                mLoadMoreTask.cancel(true);
            }

            //注销广播
            mActivity.unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
        }

        super.onDestroy();
    }

    private void changeStatus(int status) {

        refreshItem(status);
        mCurrentType = status;

        switch (status) {
            case TYPE_TOTAL:
                mHistoryTotalDiv.setVisibility(View.VISIBLE);
                mHistorySafeDiv.setVisibility(View.GONE);
                mHistoryUnsafeDiv.setVisibility(View.GONE);
                break;
            case TYPE_SAFE:
                mHistoryTotalDiv.setVisibility(View.GONE);
                mHistorySafeDiv.setVisibility(View.VISIBLE);
                mHistoryUnsafeDiv.setVisibility(View.GONE);
                break;
            case TYPE_UNSAFE:
                mHistoryTotalDiv.setVisibility(View.GONE);
                mHistorySafeDiv.setVisibility(View.GONE);
                mHistoryUnsafeDiv.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void refreshItem(int status) {


    }

}

package com.bjxapp.worker.ui.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

import com.bjx.master.R;
import com.bjxapp.worker.adapter.PushBillAdapter;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.NotificationApi;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.controls.listview.XListView;
import com.bjxapp.worker.controls.listview.XListView.IXListViewListener;
import com.bjxapp.worker.db.BjxInfo;
import com.bjxapp.worker.db.DBManager;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.Message;
import com.bjxapp.worker.ui.view.activity.CheckOrderDetailActivity;
import com.bjxapp.worker.ui.view.activity.MessageDetailActivity;
import com.bjxapp.worker.ui.view.activity.order.OrderDetailActivityNew;
import com.bjxapp.worker.ui.view.activity.user.BalanceWithdrawHistoryActivity;
import com.bjxapp.worker.ui.view.base.BaseFragment;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.utils.LogUtils;
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

public class Fragment_Main_Bill extends BaseFragment implements OnClickListener, IXListViewListener {
    protected static final String TAG = "通知";
    private RelativeLayout mLoadAgainLayout;
    private XWaitingDialog mWaitingDialog;
    private ArrayList<BjxInfo> mMessagesArray = new ArrayList<BjxInfo>();
    private PushBillAdapter mMessageAdapter;
    private XListView mXListView;
    private int mCurrentBatch = 0;

    private Fragment_Main_Third_New mParentFragment;

    private DBManager dbManager;

    public static final int BATCH_SIZE = 200;

    @Override
    protected void initView() {
        dbManager = new DBManager(getActivity());
        mLoadAgainLayout = (RelativeLayout) findViewById(R.id.message_list_load_again);
        mLoadAgainLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onFirstLoadData();
            }
        });

        mXListView = (XListView) findViewById(R.id.message_list_listview);
        mMessageAdapter = new PushBillAdapter(mActivity, mMessagesArray);
        mXListView.setAdapter(mMessageAdapter);
        mXListView.setCacheColorHint(Color.TRANSPARENT);
        mXListView.setPullLoadEnable(true);
        mXListView.setPullRefreshEnable(true);
        mXListView.setXListViewListener(this);
        mXListView.setDividerHeight(DimenUtils.dp2px(5, getActivity()));

        mXListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BjxInfo message = (BjxInfo) mXListView.getItemAtPosition(position);

                String orderId = message.getOrderId();
                int type = message.getType();

                Intent intent = new Intent();

                switch (type) {

                    case 0:
                    case 10:
                    case 11:
                    case 53:
                    case 7:
                    case 54:
                    case 55:
                        intent.putExtra("order_id", orderId);
                        intent.putExtra("processStatus", 0);
                        intent.setClass(getActivity(), OrderDetailActivityNew.class);
                        break;

                    case 20:
                    case 21:
                        Utils.startActivity(getActivity(), BalanceWithdrawHistoryActivity.class);
                        break;
                        default:
                            CheckOrderDetailActivity.goToActivity(getActivity() , orderId , 0);
                            break;
                }


                message.setRead(true);
                mMessageAdapter.notifyDataSetChanged();
                dbManager.updateAsRead(message.getId());
                mActivity.updateRedotCount();

                if (mParentFragment != null) {
                    mParentFragment.updateRedot();
                }

                getActivity().startActivity(intent);

            }
        });

        mWaitingDialog = new XWaitingDialog(mActivity);

        onFirstLoadData();
    }

    @Override
    protected void finish() {

    }

    @Override
    protected int onCreateContent() {
        return R.layout.fragment_main_third;
    }

    @Override
    public void refresh(int enterType) {
        if (enterType != 0) {
            onFirstLoadData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case 0:
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

    private String mCreateTime;

    private String getFormatedTime() {

        if (TextUtils.isEmpty(mCreateTime)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mCreateTime = format.format(new Date());
        }

        return mCreateTime;
    }

    private String updateFormatedTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mCreateTime = format.format(new Date());
        return mCreateTime;
    }

    private AsyncTask<Void, Void, List<BjxInfo>> mFirstLoadTask;

    private void onFirstLoadData() {

        mFirstLoadTask = new AsyncTask<Void, Void, List<BjxInfo>>() {
            @Override
            protected List<BjxInfo> doInBackground(Void... voids) {

                ArrayList<BjxInfo> list = (ArrayList<BjxInfo>) dbManager.queryBill(BATCH_SIZE, 0 * BATCH_SIZE);

                return list;
            }

            @Override
            protected void onPostExecute(List<BjxInfo> bjxInfos) {

                if (bjxInfos == null || bjxInfos.size() == 0) {
                    return;
                }

                LogUtils.log(bjxInfos.toString());

                mCurrentBatch = 0;

                mMessagesArray.clear();
                mMessagesArray.addAll(bjxInfos);
                mMessageAdapter.notifyDataSetChanged();
                mCurrentBatch++;

                if (mMessagesArray.size() > 0) {
                    //  ConfigManager.getInstance(mActivity).setDesktopMessagesDot(ConfigManager.getInstance(mActivity).getDesktopMessagesDotServer());
                    mLoadAgainLayout.setVisibility(View.GONE);
                    mXListView.setVisibility(View.VISIBLE);
                }

            }
        };

        mFirstLoadTask.execute();
    }

    private AsyncTask<Void, Void, List<BjxInfo>> mRefreshTask;

    @Override
    public void onRefresh() {

        mRefreshTask = new AsyncTask<Void, Void, List<BjxInfo>>() {
            @Override
            protected List<BjxInfo> doInBackground(Void... voids) {

                ArrayList<BjxInfo> list = (ArrayList<BjxInfo>) dbManager.queryBill(BATCH_SIZE, 0 * BATCH_SIZE);

                return list;
            }

            @Override
            protected void onPostExecute(List<BjxInfo> bjxInfos) {

                if (bjxInfos == null) {
                    return;
                }

                mCurrentBatch = 0;

                mMessagesArray.clear();
                mMessagesArray.addAll(bjxInfos);
                mMessageAdapter.notifyDataSetChanged();
                mCurrentBatch++;

                if (mMessagesArray.size() > 0) {
                    //  ConfigManager.getInstance(mActivity).setDesktopMessagesDot(ConfigManager.getInstance(mActivity).getDesktopMessagesDotServer());
                    mLoadAgainLayout.setVisibility(View.GONE);
                    mXListView.setVisibility(View.VISIBLE);
                }

                onLoadFinished();
            }
        };

        mRefreshTask.execute();
    }

    private AsyncTask<Void, Void, List<BjxInfo>> mLoadMoreTask;


    @Override
    public void onLoadMore() {

        mLoadMoreTask = new AsyncTask<Void, Void, List<BjxInfo>>() {
            @Override
            protected List<BjxInfo> doInBackground(Void... voids) {

                ArrayList<BjxInfo> list = (ArrayList<BjxInfo>) dbManager.queryBill(BATCH_SIZE, mCurrentBatch * BATCH_SIZE);

                return list;
            }

            @Override
            protected void onPostExecute(List<BjxInfo> bjxInfos) {

                if (bjxInfos == null) {
                    return;
                }

                mCurrentBatch++;
                mMessagesArray.addAll(bjxInfos);
                mMessageAdapter.notifyDataSetChanged();
                onLoadFinished();
            }
        };

        mLoadMoreTask.execute();
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

    public Fragment_Main_Bill setParentFragment(Fragment_Main_Third_New fragment) {
        this.mParentFragment = fragment;
        return this;
    }


}

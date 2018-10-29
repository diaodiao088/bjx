package com.bjxapp.worker.ui.view.fragment.subfragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.bjxapp.worker.R;
import com.bjxapp.worker.adapter.OrderAdapter;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.controls.listview.XListView;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.FirstPageResult;
import com.bjxapp.worker.model.ReceiveOrder;
import com.bjxapp.worker.ui.view.activity.order.OrderDetailActivity;
import com.bjxapp.worker.ui.view.fragment.ctrl.DataManagerCtrl;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangdan on 2018/9/20.
 * <p>
 * comments:
 */

public abstract class BillBaseFragment extends Fragment implements XListView.IXListViewListener, DataManagerCtrl.OnDataLoadFinishListener {

    private View mRootView;
    private XListView mListView;
    private OrderAdapter mOrderAdapter;
    private ArrayList<ReceiveOrder> mOrdersArray = new ArrayList<ReceiveOrder>();
    private XWaitingDialog mWaitingDialog;
    private RelativeLayout mLoadAgainLayout;

    private AsyncTask<Void, Void, FirstPageResult> mFirstLoadTask;
    private AsyncTask<Void, Void, FirstPageResult> mRefreshTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        DataManagerCtrl.getIns().registerListener(this);

        FirstPageResult pageResult = DataManagerCtrl.getIns().getPageResult();

        if (pageResult == null || pageResult.getOrderObject() == null) {
            loadData(false);
        } else {
            mLoadAgainLayout.setVisibility(View.GONE);
            mOrdersArray.clear();
            mOrdersArray.addAll((List<ReceiveOrder>) pageResult.getOrderObject());
            mOrderAdapter.setReceiverInfo(mOrdersArray);
            mOrderAdapter.notifyDataSetChanged();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutRes(), container, false);
            initView();
        }

        return mRootView;
    }


    public void initView() {
        mListView = mRootView.findViewById(R.id.main_first_order_listview);
        mListView.setDividerHeight(DimenUtils.dp2px(5 , getContext()));
        mOrderAdapter = new OrderAdapter(mRootView.getContext(), mOrdersArray);
        mListView.setAdapter(mOrderAdapter);

        mListView.setCacheColorHint(Color.TRANSPARENT);
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReceiveOrder order = (ReceiveOrder) mListView.getItemAtPosition(position);
                startOrderDetailActivity(String.valueOf(order.getOrderID()));
            }
        });

        mLoadAgainLayout = mRootView.findViewById(R.id.main_first_order_load_again);

        mLoadAgainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(true);
            }
        });

        mWaitingDialog = new XWaitingDialog(getContext());
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {

        mRefreshTask = new AsyncTask<Void, Void, FirstPageResult>() {
            @Override
            protected FirstPageResult doInBackground(Void... params) {
                return LogicFactory.getDesktopLogic(getContext()).getFirstPageData();
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void onPostExecute(FirstPageResult result) {

                DataManagerCtrl.getIns().setPageResult(result);
                onLoadFinish();
            }
        };
        mRefreshTask.execute();
    }

    private void startOrderDetailActivity(String orderID) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), OrderDetailActivity.class);
        intent.putExtra("order_id", orderID);
        startActivityForResult(intent, Constant.ACTIVITY_ORDER_DETAIL_RESULT_CODE);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void onLoadFinished() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String refreshTimeString = format.format(new Date());
        mListView.setRefreshTime(refreshTimeString);
    }

    @Override
    public void onPause() {
        super.onPause();
        DataManagerCtrl.getIns().unRegisterListener(this);
    }

    @SuppressLint("StaticFieldLeak")
    private void loadData(final Boolean loading) {
        if (Utils.isNetworkAvailable(getContext())) {
            if (loading) {
                mWaitingDialog.show("正在加载中，请稍候...", false);
            }
            mFirstLoadTask = new AsyncTask<Void, Void, FirstPageResult>() {
                @Override
                protected FirstPageResult doInBackground(Void... params) {
                    return LogicFactory.getDesktopLogic(getContext()).getFirstPageData();
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void onPostExecute(FirstPageResult result) {
                    if (loading) {
                        mWaitingDialog.dismiss();
                    }

                    DataManagerCtrl.getIns().setPageResult(result);
                }
            };
            mFirstLoadTask.execute();
        } else {
            Utils.showShortToast(getContext(), getString(R.string.common_no_network_message));
        }
    }

    @Override
    public void onLoadFinish() {

        onLoadFinished();

        FirstPageResult result = DataManagerCtrl.getIns().getPageResult();

        if (result == null || result.getOrderObject() == null) {
            mLoadAgainLayout.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
            return;
        }

        mOrdersArray.clear();
        mOrdersArray.addAll((List<ReceiveOrder>) result.getOrderObject());
        mOrderAdapter.setReceiverInfo(mOrdersArray);
        mListView.setAdapter(mOrderAdapter);

        if (mOrdersArray.size() > 0) {
            ConfigManager.getInstance(getContext()).setDesktopOrdersDot(ConfigManager.getInstance(getContext()).getDesktopOrdersDotServer());
            mLoadAgainLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }

        if (mOrderAdapter != null && mListView != null) {
            mOrderAdapter.setReceiverInfo(getOrderArray());
            mOrderAdapter.notifyDataSetChanged();
        }
    }

    protected abstract int getLayoutRes();

    protected ArrayList<ReceiveOrder> getOrderArray() {
        return null;
    }

    ;
}

package com.bjxapp.worker.ui.view.fragment.subfragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.bjx.master.R;
import com.bjxapp.worker.adapter.OrderAdapter;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.BillApi;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.controls.listview.XListView;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.OrderDes;
import com.bjxapp.worker.ui.view.activity.RepairActivity;
import com.bjxapp.worker.ui.view.activity.order.OrderDetailActivityNew;
import com.bjxapp.worker.ui.view.fragment.ctrl.DataManagerCtrl;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.utils.LogUtils;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

;

/**
 * Created by zhangdan on 2018/9/20.
 * <p>
 * comments:
 */

public abstract class BillBaseFragment extends Fragment implements XListView.IXListViewListener, DataManagerCtrl.OnDataLoadFinishListener {

    private View mRootView;
    private XListView mListView;
    private OrderAdapter mOrderAdapter;
    protected ArrayList<OrderDes> mOrdersArray = new ArrayList<OrderDes>();
    private XWaitingDialog mWaitingDialog;
    private RelativeLayout mLoadAgainLayout;

    protected Comparator<OrderDes> comparator = new Comparator<OrderDes>() {
        @Override
        public int compare(OrderDes o1, OrderDes o2) {

            if (o1.getStatus() == 4 && o2.getStatus() == 4) {
                return 0;
            } else if (o1.getStatus() == 4 && o2.getStatus() != 4) {
                return 1;
            } else if (o1.getStatus() != 4 && o2.getStatus() == 4) {
                return 1;
            }

            return 0;
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        DataManagerCtrl.getIns().registerListener(this);

        if (DataManagerCtrl.getIns().isDataDirty()) {
            loadData(false);
        }

    }

    private Handler mHandler = new Handler(Looper.getMainLooper());


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            final ArrayList<OrderDes> pageResult = DataManagerCtrl.getIns().getPageResult();
            if (pageResult == null || pageResult.size() == 0 || DataManagerCtrl.getIns().isDataDirty()) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadData(false);
                    }
                });
            } else {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLoadAgainLayout.setVisibility(View.GONE);
                        mOrdersArray.clear();
                        mOrdersArray.addAll(pageResult);
                        mOrderAdapter.setReceiverInfo(getOrderArray());
                        mOrderAdapter.notifyDataSetChanged();

                        if (getOrderArray().size() <= 0){
                            mLoadAgainLayout.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }
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
        mListView.setDividerHeight(DimenUtils.dp2px(5, getContext()));
        mOrderAdapter = new OrderAdapter(mRootView.getContext(), mOrdersArray);
        mListView.setAdapter(mOrderAdapter);

        mListView.setCacheColorHint(Color.TRANSPARENT);
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderDes order = (OrderDes) mListView.getItemAtPosition(position);
                startOrderDetailActivity(order);
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

        BillApi billApi = KHttpWorker.ins().createHttpService(LoginApi.URL, BillApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("userCode", ConfigManager.getInstance(getActivity()).getUserCode());
        params.put("token", ConfigManager.getInstance(getActivity()).getUserSession());
        Call<JsonObject> orderRequest = billApi.getOrderList(params);

        orderRequest.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                parseResponse(response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    private void parseResponse(Response<JsonObject> response) {
        if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {

            JsonObject object = response.body();
            int code = object.get("code").getAsInt();

            if (code == 0) {
                JsonArray listArray = object.getAsJsonArray("list");
                if (listArray != null && listArray.size() > 0) {

                    ArrayList<OrderDes> list = new ArrayList<>();
                    for (int i = 0; i < listArray.size(); i++) {
                        JsonObject item = (JsonObject) listArray.get(i);
                        String orderId = item.get("orderId").getAsString();
                        int processStatus = item.get("processStatus").getAsInt();

                        int settleStatus = 0;

                        if (item.has("settlementStatus")) {
                            settleStatus = item.get("settlementStatus").getAsInt();
                        }

                        int status = item.get("status").getAsInt();
                        int bussinessType = item.get("type").getAsInt();
                        JsonObject detailItem = item.getAsJsonObject("appointmentDetail");
                        String serviceName = detailItem.get("serviceName").getAsString();
                        String appointmentDay = detailItem.get("appointmentDay").getAsString();
                        String appointmentEndTime = detailItem.get("appointmentEndTime").getAsString();
                        String appointmentStartTime = detailItem.get("appointmentStartTime").getAsString();
                        String locationAddress = detailItem.get("locationAddress").getAsString();
                        String serviceVisitCost = detailItem.get("serviceVisitCost").getAsString();
                        String serviceEsCost = detailItem.get("serviceVisitCost").getAsString();
                        String selectTime = detailItem.get("selectMasterTime").getAsString();
                        String type = detailItem.get("type").getAsString();

                        String shopName = detailItem.get("shopName").getAsString();
                        String enterpriseName = detailItem.get("enterpriseName").getAsString();


                        OrderDes orderItem = new OrderDes(orderId, processStatus, status,
                                serviceName, appointmentDay, appointmentEndTime, appointmentStartTime,
                                locationAddress, serviceVisitCost);

                        orderItem.setmShopName(shopName);
                        orderItem.setmEnterpriseName(enterpriseName);

                        orderItem.setmSelectTime(selectTime);
                        orderItem.setBillType(Integer.parseInt(type));

                        orderItem.setSettleStatus(settleStatus);

                        JsonObject maintainItem = item.getAsJsonObject("maintainDetail");
                        String orderTime = "";

                        try {
                            if (maintainItem.get("receiveOrderTime") != null) {
                                orderTime = maintainItem.get("receiveOrderTime").getAsString();
                            }

                            savePayAmount(orderItem, maintainItem);

                        } catch (Exception e) {

                        }
                        orderItem.setSelectMasterTime(orderTime);

                        orderItem.setEsCost(serviceEsCost);

                        orderItem.setBusinessType(bussinessType);

                        list.add(orderItem);
                    }

                    DataManagerCtrl.getIns().setPageResult(list);
                } else {
                    DataManagerCtrl.getIns().setPageResult(new ArrayList<OrderDes>());
                }
            } else {
                DataManagerCtrl.getIns().setPageResult(new ArrayList<OrderDes>());
            }
        }
    }

    private void savePayAmount(OrderDes orderItem, JsonObject maintainItem) {
        String payAmount = "";
        if (maintainItem.get("payAmount") != null) {
            payAmount = maintainItem.get("payAmount").getAsString();
        }

        orderItem.setPayAmount(payAmount);
    }

    private void startOrderDetailActivity(OrderDes order) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), OrderDetailActivityNew.class);
        intent.putExtra("order_id", order.getOrderId());
        intent.putExtra("processStatus", order.getProcessStatus());
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

        if (!Utils.isNetworkAvailable(getContext())) {
            Utils.showShortToast(getContext(), getString(R.string.common_no_network_message));
        }

        if (loading) {
            mWaitingDialog.show("正在加载中，请稍候...", false);
        }

        BillApi billApi = KHttpWorker.ins().createHttpService(LoginApi.URL, BillApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("userCode", ConfigManager.getInstance(getActivity()).getUserCode());
        params.put("token", ConfigManager.getInstance(getActivity()).getUserSession());
        Call<JsonObject> orderRequest = billApi.getOrderList(params);

        orderRequest.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (loading && mWaitingDialog != null) {
                                mWaitingDialog.dismiss();
                            }
                        }
                    });

                    parseResponse(response);
                }


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (loading && mWaitingDialog != null) {
                                mWaitingDialog.dismiss();
                            }
                        }
                    });
                }

            }
        });

    }

    @Override
    public void onLoadFinish() {

        onLoadFinished();

        ArrayList<OrderDes> result = DataManagerCtrl.getIns().getPageResult();

        refreshRedot(result);

        if (result == null || result.size() <= 0) {
            mLoadAgainLayout.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
            return;
        }

        if (result.size() > 0) {
            ConfigManager.getInstance(getContext()).setDesktopOrdersDot(ConfigManager.getInstance(getContext()).getDesktopOrdersDotServer());
            mLoadAgainLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }

        mOrdersArray.clear();
        mOrdersArray.addAll(result);
        mOrderAdapter.setReceiverInfo(getOrderArray());
        mOrderAdapter.notifyDataSetChanged();
        mListView.setAdapter(mOrderAdapter);

        if (getOrderArray().size() <= 0){
            mLoadAgainLayout.setVisibility(View.VISIBLE);
        }else{
            mLoadAgainLayout.setVisibility(View.GONE);
        }

    }

    private void refreshRedot(ArrayList<OrderDes> orderArray) {
//        if (getParentFragment() != null && getParentFragment() instanceof Fragment_Main_First) {
//            ((Fragment_Main_First) getParentFragment()).refreshRedot(orderArray);
//        }

        if (getActivity() instanceof RepairActivity && !getActivity().isFinishing()) {
            ((RepairActivity) getActivity()).refreshRedot(orderArray);
        }
    }


    protected abstract int getLayoutRes();

    protected ArrayList<OrderDes> getOrderArray() {

        for (int i = 0; i < mOrdersArray.size(); i++) {
            LogUtils.log(mOrdersArray.get(i).getStatus() + "");
        }


        return sortArray(mOrdersArray);
    }

    protected ArrayList<OrderDes> sortArray(ArrayList<OrderDes> mList) {

        ArrayList<OrderDes> list_unexpet = new ArrayList<>();

        ArrayList<OrderDes> list_normal = new ArrayList<>();

        for (int i = 0; i < mList.size(); i++) {

            OrderDes orderDes = mList.get(i);

            if (orderDes.getStatus() == 4) {
                list_unexpet.add(orderDes);
            } else {
                list_normal.add(orderDes);
            }
        }

        mList.clear();
        mList.addAll(list_unexpet);
        mList.addAll(list_normal);
        //   Collections.sort(mList, comparator);
        return mList;
    }
}

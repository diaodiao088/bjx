package com.bjxapp.worker.ui.view.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bjxapp.worker.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.BillApi;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.controls.listview.XListView.IXListViewListener;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.DateTime;
import com.bjxapp.worker.model.OrderDes;
import com.bjxapp.worker.model.ReceiveButton;
import com.bjxapp.worker.ui.view.activity.order.OrderPaySuccessActivity;
import com.bjxapp.worker.ui.view.base.BaseFragment;
import com.bjxapp.worker.ui.view.fragment.ctrl.PageSlipingCtrl;
import com.bjxapp.worker.ui.view.fragment.subfragment.AlreadyRoomFragment;
import com.bjxapp.worker.ui.view.fragment.subfragment.BillAdapter;
import com.bjxapp.worker.ui.view.fragment.subfragment.NewBillFragment;
import com.bjxapp.worker.ui.view.fragment.subfragment.TotalFragment;
import com.bjxapp.worker.ui.view.fragment.subfragment.WaitingContactFragment;
import com.bjxapp.worker.ui.view.fragment.subfragment.WaitingPayFragment;
import com.bjxapp.worker.ui.view.fragment.subfragment.WaitingRoomFragment;
import com.bjxapp.worker.ui.widget.ToggleSwitchButton;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Main_First extends BaseFragment implements OnClickListener, IXListViewListener {

    protected static final String TAG = "首页";

    private XWaitingDialog mWaitingDialog;
    private ToggleSwitchButton mSwitchBtn;
    private TextView mSwitchStatusTv;
    private ViewPager mVp;
    private PageSlipingCtrl mSlipCtrl;
    private BillAdapter mBillAdapter;

    @Override
    protected void initView() {
        initViews();
        initializeReceiveButton(null);
    }

    @Override
    protected void finish() {

    }

    @Override
    protected int onCreateContent() {
        return R.layout.fragment_main_first;
    }

    @Override
    public void refresh(int enterType) {
        if (enterType != 0) {

        }
    }

    private void initViews() {
        registerUpdateUIBroadcast();

        mSwitchStatusTv = (TextView) findViewById(R.id.status_tv);

        initVp();

        mSwitchBtn = (ToggleSwitchButton) findViewById(R.id.toggle_switch_btn);
        mSlipCtrl = new PageSlipingCtrl(mRoot);
        mSlipCtrl.init();
        mSlipCtrl.updateUnderLineUi(0);

        initListener();
        initSwitchBar();

        mWaitingDialog = new XWaitingDialog(mActivity);
    }

    private void initVp() {
        mVp = (ViewPager) findViewById(R.id.main_pager);
        mVp.setOffscreenPageLimit(5);
        mBillAdapter = new BillAdapter(getChildFragmentManager());

        mBillAdapter.addFragment(TotalFragment.getIns());
        mBillAdapter.addFragment(NewBillFragment.getIns());
        mBillAdapter.addFragment(WaitingContactFragment.getIns());
        mBillAdapter.addFragment(WaitingRoomFragment.getIns());
        mBillAdapter.addFragment(AlreadyRoomFragment.getIns());
        mBillAdapter.addFragment(WaitingPayFragment.getIns());

        mVp.setAdapter(mBillAdapter);
        mVp.setCurrentItem(0);
        mVp.setEnabled(false);
    }

    private void initListener() {
        findViewById(R.id.total_ly).setOnClickListener(this);
        findViewById(R.id.new_bill_ly).setOnClickListener(this);
        findViewById(R.id.waiting_contact_ly).setOnClickListener(this);
        findViewById(R.id.already_enter_ly).setOnClickListener(this);
        findViewById(R.id.waiting_room_ly).setOnClickListener(this);
        findViewById(R.id.waiting_pay_ly).setOnClickListener(this);
    }

    private void initSwitchBar() {

        boolean receiverStatus = ConfigManager.getInstance(getContext()).getReceiverBillStatus();

        if (receiverStatus) {
            mSwitchBtn.setCheckedWithoutCallback(true);
            mSwitchStatusTv.setText("恭喜你，开启了你的赚钱旅程");
        } else {
            mSwitchBtn.setCheckedWithoutCallback(false);
            mSwitchStatusTv.setText("今天你赚钱了吗？马上开启你的赚钱旅程吧！");
        }

        mSwitchBtn.setOnCheckedChangeListener(new ToggleSwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean isChecked) {

                changeStatusUi(isChecked);

                changeStatusReal(isChecked);

                ConfigManager.getInstance(getContext()).setReceiverBillStatus(isChecked);
            }
        });
    }

    private void changeStatusUi(boolean isChecked) {
        if (isChecked) {
            mSwitchStatusTv.setText("恭喜你，开启了你的赚钱旅程");
        } else {
            mSwitchStatusTv.setText("今天你赚钱了吗？马上开启你的赚钱旅程吧！");
        }
    }

    /**
     * @param shouldReceiveBill
     */
    private void changeStatusReal(boolean shouldReceiveBill) {

        BillApi billApi = KHttpWorker.ins().createHttpService(LoginApi.URL, BillApi.class);

        Map<String, String> params = new HashMap<>();

        params.put("userCode", ConfigManager.getInstance(getActivity()).getUserCode());
        params.put("token", ConfigManager.getInstance(getActivity()).getUserSession());

        if (shouldReceiveBill) {
            Call<JsonObject> request = billApi.receiveBill(params);

            KHttpWorker.ins().requestWithOrigin(request, new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {


                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });

        } else {
            Call<JsonObject> request = billApi.denyBill(params);

            KHttpWorker.ins().requestWithOrigin(request, new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }

    }

    /**
     * 是否接单
     *
     * @param receiveBill
     */
    public void changeServiceStatusReal(boolean receiveBill) {
        ConfigManager.getInstance(getContext()).setReceiverBillStatus(receiveBill);

        if (receiveBill) {
            mSwitchBtn.setCheckedWithoutCallback(true);
        } else {
            mSwitchBtn.setCheckedWithoutCallback(false);
        }

        changeStatusUi(receiveBill);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.total_ly:
                mSlipCtrl.updateUnderLineUi(0);
                mVp.setCurrentItem(0, true);
                break;

            case R.id.new_bill_ly:
                mSlipCtrl.updateUnderLineUi(1);
                mVp.setCurrentItem(1, true);
                break;

            case R.id.waiting_room_ly:
                mSlipCtrl.updateUnderLineUi(3);
                mVp.setCurrentItem(3, true);
                break;

            case R.id.waiting_contact_ly:
                mSlipCtrl.updateUnderLineUi(2);
                mVp.setCurrentItem(2, true);
                break;

            case R.id.already_enter_ly:

                mSlipCtrl.updateUnderLineUi(4);
                mVp.setCurrentItem(4, true);
                break;

            case R.id.waiting_pay_ly:
                mSlipCtrl.updateUnderLineUi(5);
                mVp.setCurrentItem(5, true);
                break;
            default:
                break;
        }
    }

    private void initializeReceiveButton(List<ReceiveButton> buttons) {
        String today = DateTime.getTodayDateTimeString();
        String tomorrow = DateTime.getTomorrowDateTimeString();

        ReceiveButton receiveButton = new ReceiveButton();
        receiveButton.setDate(today);
        receiveButton.setType(0);
        receiveButton.setFlag(0);
        if (buttons != null && buttons.size() > 0) {
            for (ReceiveButton button : buttons) {
                if (button.getDate().equals(today) && button.getType() == 0) {
                    receiveButton.setFlag(1);
                }
            }
        }

        receiveButton = new ReceiveButton();
        receiveButton.setDate(tomorrow);
        receiveButton.setType(0);
        receiveButton.setFlag(0);
        if (buttons != null && buttons.size() > 0) {
            for (ReceiveButton button : buttons) {
                if (button.getDate().equals(tomorrow) && button.getType() == 0) {
                    receiveButton.setFlag(1);
                }
            }
        }

        receiveButton = new ReceiveButton();
        receiveButton.setDate(today);
        receiveButton.setType(1);
        receiveButton.setFlag(0);
        if (buttons != null && buttons.size() > 0) {
            for (ReceiveButton button : buttons) {
                if (button.getDate().equals(today) && button.getType() == 1) {
                    receiveButton.setFlag(1);
                }
            }
        }

        receiveButton = new ReceiveButton();
        receiveButton.setDate(tomorrow);
        receiveButton.setType(1);
        receiveButton.setFlag(0);
        if (buttons != null && buttons.size() > 0) {
            for (ReceiveButton button : buttons) {
                if (button.getDate().equals(tomorrow) && button.getType() == 1) {
                    receiveButton.setFlag(1);
                }
            }
        }
    }

    private void updateReceiveButtonOperation(final XButton button) {
        if (!Utils.isNetworkAvailable(mActivity)) {
            Utils.showShortToast(mActivity, getString(R.string.common_no_network_message));
            return;
        }

        if (button.getTag() == null) return;
        ReceiveButton receiveButton = (ReceiveButton) button.getTag();
        if (receiveButton.getFlag() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setTitle("百家修");
            builder.setMessage("您将不会再收到订单，确认此操作吗？");
            builder.setCancelable(false);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateReceiveState(button);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
        } else {
            updateReceiveState(button);
        }
    }

    private AsyncTask<Void, Void, Integer> mUpdateReceiveStateTask;

    private void updateReceiveState(final XButton button) {
        ReceiveButton receiveButton = (ReceiveButton) button.getTag();
        final String date = receiveButton.getDate();
        final String type = String.valueOf(receiveButton.getType());
        String flagString = "";
        if (receiveButton.getFlag() == 0) {
            flagString = "1";
        } else {
            flagString = "0";
        }
        final String flag = flagString;

        mWaitingDialog.show("正在更改接单状态，请稍候...", false);
        mUpdateReceiveStateTask = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                int result = LogicFactory.getDesktopLogic(mActivity).setOrderReceiveState(date, type, flag);
                return result;
            }

            @Override
            protected void onPostExecute(Integer result) {
                mWaitingDialog.dismiss();
                if (result == APIConstants.RESULT_CODE_SUCCESS) {
                    ReceiveButton receiveButton = (ReceiveButton) button.getTag();
                    if (receiveButton.getFlag() == 0) {
                        receiveButton.setFlag(1);
                        button.setText("不能接单");
                        button.setTextColor(mActivity.getResources().getColor(R.color.receive_button_not));
                        button.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_receive_not));
                    } else {
                        receiveButton.setFlag(0);
                        button.setText("可以接单");
                        button.setTextColor(mActivity.getResources().getColor(R.color.receive_button_can));
                        button.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_receive_can));
                    }
                }
            }
        };

        mUpdateReceiveStateTask.execute();
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

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
            String orderID = intent.getStringExtra("business_id");
            int pushType = intent.getIntExtra("push_type", 0);
            String message = intent.getStringExtra("message");
            String extra = intent.getStringExtra("extra");
            Utils.showLongToast(mActivity, message);
            // onFirstLoadData(false);

            if (pushType == Constant.PUSH_TYPE_ORDER_PAY) {
                showPaySuccessActivity(orderID, extra);
            }
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

    private void showPaySuccessActivity(String orderID, String money) {
        Intent intent = new Intent();
        intent.setClass(mActivity, OrderPaySuccessActivity.class);
        intent.putExtra("order_id", orderID);
        intent.putExtra("money", money);
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {
                case Constant.ACTIVITY_ORDER_DETAIL_RESULT_CODE:
                    if (resultCode == Activity.RESULT_OK) {
                        // onFirstLoadData(true);
                    }
                    break;
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected String getPageName() {
        return TAG;
    }

    public void refreshRedot(ArrayList<OrderDes> list){

        if (getActivity() != null && !getActivity().isFinishing()) {
            mSlipCtrl.updateRedot(list);
        }
    }


    @Override
    public void onDestroy() {
        try {

            //注销广播
            mActivity.unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
        }

        super.onDestroy();
    }

}

package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.BillApi;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.DateTime;
import com.bjxapp.worker.model.OrderDes;
import com.bjxapp.worker.model.ReceiveButton;
import com.bjxapp.worker.ui.view.activity.order.OrderPaySuccessActivity;
import com.bjxapp.worker.ui.view.fragment.ctrl.DataManagerCtrl;
import com.bjxapp.worker.ui.view.fragment.ctrl.PageSlipingCtrl;
import com.bjxapp.worker.ui.view.fragment.subfragment.AlreadyRoomFragment;
import com.bjxapp.worker.ui.view.fragment.subfragment.BillAdapter;
import com.bjxapp.worker.ui.view.fragment.subfragment.NewBillFragment;
import com.bjxapp.worker.ui.view.fragment.subfragment.TotalFragment;
import com.bjxapp.worker.ui.view.fragment.subfragment.WaitingContactFragment;
import com.bjxapp.worker.ui.view.fragment.subfragment.WaitingPayFragment;
import com.bjxapp.worker.ui.view.fragment.subfragment.WaitingRoomFragment;
import com.bjxapp.worker.ui.widget.ToggleSwitchButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepairActivity extends FragmentActivity implements View.OnClickListener {

    protected static final String TAG = "首页";

    private XWaitingDialog mWaitingDialog;
    private ViewPager mVp;
    private PageSlipingCtrl mSlipCtrl;
    private BillAdapter mBillAdapter;
    private View mRoot;


    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main_first);
        ButterKnife.bind(this);
        initViews();
    }

    private void initVp() {
        mVp = (ViewPager) findViewById(R.id.main_pager);
        mVp.setOffscreenPageLimit(5);
        mBillAdapter = new BillAdapter(getSupportFragmentManager());

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

    private void initViews() {
        registerUpdateUIBroadcast();

        mRoot = findViewById(R.id.root);

        mTitleTextView.setText("维修");


        initVp();

        mSlipCtrl = new PageSlipingCtrl(mRoot);
        mSlipCtrl.init();
        mSlipCtrl.updateUnderLineUi(0);

        initListener();

        mWaitingDialog = new XWaitingDialog(this);
    }

    private void initListener() {
        findViewById(R.id.total_ly).setOnClickListener(this);
        findViewById(R.id.new_bill_ly).setOnClickListener(this);
        findViewById(R.id.waiting_contact_ly).setOnClickListener(this);
        findViewById(R.id.already_enter_ly).setOnClickListener(this);
        findViewById(R.id.waiting_room_ly).setOnClickListener(this);
        findViewById(R.id.waiting_pay_ly).setOnClickListener(this);
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


    private AsyncTask<Void, Void, Integer> mUpdateReceiveStateTask;


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
        registerReceiver(broadcastReceiver, filter);
    }

    private void showPaySuccessActivity(String orderID, String money) {
        Intent intent = new Intent();
        intent.setClass(this, OrderPaySuccessActivity.class);
        intent.putExtra("order_id", orderID);
        intent.putExtra("money", money);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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

    public void refreshRedot(ArrayList<OrderDes> list) {

        if (!isFinishing()) {
            mSlipCtrl.updateRedot(list);
        }
    }


    @Override
    public void onDestroy() {
        try {
            //注销广播
            unregisterReceiver(broadcastReceiver);
            DataManagerCtrl.getIns().markDataDirty(true);
        } catch (Exception e) {
        }

        super.onDestroy();
    }

    public static void gotoActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, RepairActivity.class);
        context.startActivity(intent);
    }


}

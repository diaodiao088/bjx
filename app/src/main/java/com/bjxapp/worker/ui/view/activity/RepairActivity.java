package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.model.OrderDes;
import com.bjxapp.worker.ui.view.activity.order.OrderPaySuccessActivity;
import com.bjxapp.worker.ui.view.fragment.ctrl.DataManagerCtrl;
import com.bjxapp.worker.ui.view.fragment.subfragment.AlreadyRoomFragment;
import com.bjxapp.worker.ui.view.fragment.subfragment.BillAdapter;
import com.bjxapp.worker.ui.view.fragment.subfragment.JieSuanFragment;
import com.bjxapp.worker.ui.view.fragment.subfragment.NewBillFragment;
import com.bjxapp.worker.ui.view.fragment.subfragment.TotalFragment;
import com.bjxapp.worker.ui.view.fragment.subfragment.WaitingContactFragment;
import com.bjxapp.worker.ui.view.fragment.subfragment.WaitingPayFragment;
import com.bjxapp.worker.ui.view.fragment.subfragment.WaitingRoomFragment;
import com.bjxapp.worker.ui.view.fragment.subfragment.XieTiaoFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RepairActivity extends FragmentActivity implements View.OnClickListener {

    protected static final String TAG = "首页";

    private ViewPager mVp;
    private BillAdapter mBillAdapter;
    private View mRoot;

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @BindView(R.id.all_bill_ly)
    RelativeLayout mAllBillTv;

    @BindView(R.id.all_bill_tv)
    TextView mAllTv;

    @BindView(R.id.feed_back_ly)
    RelativeLayout mFeedbackLy;

    @BindView(R.id.enter_now_tv)
    TextView mFeedbackTv;

    @BindView(R.id.feed_back_tv)
    TextView mXieTiaoTv;

    @BindView(R.id.xietiao_ly)
    RelativeLayout mXieTiaoLy;

    @BindView(R.id.all_bill_redot)
    TextView mAllBilRedotTv;

    @BindView(R.id.enter_now_redot_tv)
    TextView mEnterNowRedotTv;

    @BindView(R.id.feed_back_redot_tv)
    TextView mFeedBackRedotTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main_first);
        ButterKnife.bind(this);
        initViews();
    }

    private void initVp() {
        mVp = (ViewPager) findViewById(R.id.main_pager);
        mVp.setOffscreenPageLimit(6);
        mBillAdapter = new BillAdapter(getSupportFragmentManager());

        mBillAdapter.addFragment(TotalFragment.getIns());
        mBillAdapter.addFragment(NewBillFragment.getIns());
        mBillAdapter.addFragment(WaitingContactFragment.getIns());
        mBillAdapter.addFragment(WaitingRoomFragment.getIns());
        mBillAdapter.addFragment(AlreadyRoomFragment.getIns());
        mBillAdapter.addFragment(WaitingPayFragment.getIns());
        mBillAdapter.addFragment(XieTiaoFragment.getIns());
        mBillAdapter.addFragment(JieSuanFragment.getIns());

        mVp.setAdapter(mBillAdapter);
        mVp.setCurrentItem(0);
        mVp.setEnabled(false);
    }

    private void initViews() {
        registerUpdateUIBroadcast();
        mRoot = findViewById(R.id.root);
        mTitleTextView.setText("工作台");
        initVp();
        initListener();
    }

    private void initListener() {
        findViewById(R.id.all_bill_ly).setOnClickListener(this);
        findViewById(R.id.feed_back_ly).setOnClickListener(this);
        findViewById(R.id.xietiao_ly).setOnClickListener(this);
    }

    private void changeState(int index) {
        switch (index) {
            case 0:

                mAllBillTv.setBackgroundResource(R.drawable.green_bg);
                mXieTiaoLy.setBackgroundResource(R.drawable.white_bg);
                mFeedbackLy.setBackgroundResource(R.drawable.white_bg);

                mAllTv.setTextColor(Color.WHITE);
                mXieTiaoTv.setTextColor(Color.parseColor("#545454"));
                mFeedbackTv.setTextColor(Color.parseColor("#545454"));

                break;
            case 1:

                mAllBillTv.setBackgroundResource(R.drawable.white_bg);
                mXieTiaoLy.setBackgroundResource(R.drawable.white_bg);
                mFeedbackLy.setBackgroundResource(R.drawable.green_bg);

                mFeedbackTv.setTextColor(Color.WHITE);
                mXieTiaoTv.setTextColor(Color.parseColor("#545454"));
                mAllTv.setTextColor(Color.parseColor("#545454"));

                break;
            case 2:

                mAllBillTv.setBackgroundResource(R.drawable.white_bg);
                mXieTiaoLy.setBackgroundResource(R.drawable.green_bg);
                mFeedbackLy.setBackgroundResource(R.drawable.white_bg);

                mXieTiaoTv.setTextColor(Color.WHITE);
                mAllTv.setTextColor(Color.parseColor("#545454"));
                mFeedbackTv.setTextColor(Color.parseColor("#545454"));
                break;
        }
    }


    @Override
    public void onClick(View v) {

        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }

        switch (v.getId()) {

            case R.id.feed_back_ly:
                mVp.setCurrentItem(4, false);
                changeState(1);
                break;

            case R.id.already_room_tv:
                mVp.setCurrentItem(4, false);

                if (getSpecCount(4, mList) > 0) {
                    mAllBilRedotTv.setText(String.valueOf(getSpecCount(4, mList)));
                    mAllBilRedotTv.setVisibility(View.VISIBLE);
                } else {
                    mAllBilRedotTv.setVisibility(View.GONE);
                }


                mAllTv.setText("已上门");

                break;
            case R.id.xietiao_ly:
                mVp.setCurrentItem(6, false);
                changeState(2);
                break;
            case R.id.xietiao_tv:

                if (getXieTiaoSize(mList) > 0) {
                    mAllBilRedotTv.setText(String.valueOf(getXieTiaoSize(mList)));
                    mAllBilRedotTv.setVisibility(View.VISIBLE);
                } else {
                    mAllBilRedotTv.setVisibility(View.GONE);
                }

                mAllTv.setText("协调中");

                mVp.setCurrentItem(6, false);
                break;
            case R.id.total_tv:

                if (mList.size() > 0) {
                    mAllBilRedotTv.setText(String.valueOf(mList.size()));
                    mAllBilRedotTv.setVisibility(View.VISIBLE);
                } else {
                    mAllBilRedotTv.setVisibility(View.GONE);
                }

                mAllTv.setText("全部工单");

                mVp.setCurrentItem(0, false);
                break;

            case R.id.all_bill_ly:
                showPopupWindow(v);
                changeState(0);
                break;

            case R.id.new_bill_ly:

                mAllTv.setText("新订单");

                mVp.setCurrentItem(1, false);
                break;

            case R.id.wait_room_tv:

                mAllTv.setText("待上门");

                if (getSpecCount(3, mList) > 0) {
                    mAllBilRedotTv.setText(String.valueOf(getSpecCount(3, mList)));
                    mAllBilRedotTv.setVisibility(View.VISIBLE);
                } else {
                    mAllBilRedotTv.setVisibility(View.GONE);
                }

                mVp.setCurrentItem(3, false);
                break;

            case R.id.wait_contact_tv:

                if (getSpecCount(2, mList) > 0) {
                    mAllBilRedotTv.setText(String.valueOf(getSpecCount(2, mList)));
                    mAllBilRedotTv.setVisibility(View.VISIBLE);
                } else {
                    mAllBilRedotTv.setVisibility(View.GONE);
                }

                mAllTv.setText("待联系");
                mVp.setCurrentItem(2, false);
                break;

            case R.id.waitpay_tv:

                if (getSpecCount(5, mList) > 0) {
                    mAllBilRedotTv.setText(String.valueOf(getSpecCount(2, mList)));
                    mAllBilRedotTv.setVisibility(View.VISIBLE);
                } else {
                    mAllBilRedotTv.setVisibility(View.GONE);
                }

                mAllTv.setText("待支付");

                mVp.setCurrentItem(5, false);
                break;

            case R.id.jiesuan_tv:

                if (getJieSuanSize(mList) > 0) {
                    mAllBilRedotTv.setText(String.valueOf(getJieSuanSize(mList)));
                    mAllBilRedotTv.setVisibility(View.VISIBLE);
                } else {
                    mAllBilRedotTv.setVisibility(View.GONE);
                }

                mAllTv.setText("结算审核");

                mVp.setCurrentItem(7, false);
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

    private ArrayList<OrderDes> mList = new ArrayList<>();

    public void refreshRedot(ArrayList<OrderDes> list) {

        this.mList = list;

        if (!isFinishing() && list.size() > 0) {
            mAllBilRedotTv.setVisibility(View.VISIBLE);
            mAllBilRedotTv.setText(String.valueOf(list.size()));

            if (getFeedbackSize(list) > 0) {
                mEnterNowRedotTv.setText(String.valueOf(getFeedbackSize(list)));
                mEnterNowRedotTv.setVisibility(View.VISIBLE);
            } else {
                mEnterNowRedotTv.setVisibility(View.GONE);
            }

            if (getXieTiaoSize(list) > 0) {
                mFeedBackRedotTv.setVisibility(View.VISIBLE);
                mFeedBackRedotTv.setText(String.valueOf(getXieTiaoSize(list)));
            } else {
                mFeedBackRedotTv.setVisibility(View.GONE);
            }

        } else {
            mAllBilRedotTv.setVisibility(View.GONE);
            mEnterNowRedotTv.setVisibility(View.GONE);
            mFeedBackRedotTv.setVisibility(View.GONE);
        }
    }

    private int getFeedbackSize(ArrayList<OrderDes> list) {

        int result = 0;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getProcessStatus() == 4) {
                result++;
            }
        }

        return result;
    }

    private int getXieTiaoSize(ArrayList<OrderDes> list) {

        int result = 0;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getProcessStatus() == 43) {
                result++;
            }
        }

        return result;
    }

    private int getJieSuanSize(ArrayList<OrderDes> list) {

        int result = 0;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getSettleStatus() == 3) {
                result++;
            }
        }

        return result;
    }

    private int getSpecCount(int processType, ArrayList<OrderDes> list) {

        int result = 0;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getProcessStatus() == processType) {
                result++;
            }
        }

        return result;

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

    PopupWindow mPopupWindow;

    private void showPopupWindow(View v) {

        View view = LayoutInflater.from(this).inflate(R.layout.bill_state_popup_window, null, false);

        LinearLayout totalTv = view.findViewById(R.id.total_tv);
        TextView totalRedotTv = view.findViewById(R.id.total_redot);
        totalTv.setOnClickListener(this);

        if (mList.size() > 0) {
            totalRedotTv.setText(String.valueOf(mList.size()));
        } else {
            totalRedotTv.setVisibility(View.GONE);
        }

        LinearLayout waitContactTv = view.findViewById(R.id.wait_contact_tv);
        TextView waitContactRedotTv = view.findViewById(R.id.wait_contact_redot);
        waitContactTv.setOnClickListener(this);

        if (getSpecCount(0x02, mList) > 0) {
            waitContactRedotTv.setVisibility(View.VISIBLE);
            waitContactRedotTv.setText(String.valueOf(getSpecCount(0x02, mList)));
        } else {
            waitContactRedotTv.setVisibility(View.GONE);
        }

        LinearLayout waitRoomTv = view.findViewById(R.id.wait_room_tv);
        TextView waitRoomRedot = view.findViewById(R.id.wait_room_redot);
        waitRoomTv.setOnClickListener(this);

        if (getSpecCount(0x03, mList) > 0) {
            waitRoomRedot.setVisibility(View.VISIBLE);
            waitRoomRedot.setText(String.valueOf(getSpecCount(0x03, mList)));
        } else {
            waitRoomRedot.setVisibility(View.GONE);
        }

        LinearLayout already_room_tv = view.findViewById(R.id.already_room_tv);
        TextView already_room_redot = view.findViewById(R.id.already_room_redot);
        already_room_tv.setOnClickListener(this);

        if (getSpecCount(0x04, mList) > 0) {
            already_room_redot.setVisibility(View.VISIBLE);
            already_room_redot.setText(String.valueOf(getSpecCount(0x04, mList)));
        } else {
            already_room_redot.setVisibility(View.GONE);
        }

        LinearLayout xietiao_tv = view.findViewById(R.id.xietiao_tv);
        TextView xietiao_redot = view.findViewById(R.id.xietiao_redot);
        xietiao_tv.setOnClickListener(this);

        if (getXieTiaoSize(mList) > 0) {
            xietiao_redot.setVisibility(View.VISIBLE);
            xietiao_redot.setText(String.valueOf(getXieTiaoSize(mList)));
        } else {
            xietiao_redot.setVisibility(View.GONE);
        }

        LinearLayout jiesuan_tv = view.findViewById(R.id.jiesuan_tv);
        TextView jiesuan_redot = view.findViewById(R.id.jiesuan_redot);
        jiesuan_tv.setOnClickListener(this);

        if (getJieSuanSize(mList) > 0) {
            jiesuan_redot.setVisibility(View.VISIBLE);
            jiesuan_redot.setText(String.valueOf(getJieSuanSize(mList)));
        } else {
            jiesuan_redot.setVisibility(View.GONE);
        }

        LinearLayout waitpay_tv = view.findViewById(R.id.waitpay_tv);
        TextView waitpay_redot = view.findViewById(R.id.waitpay_redot);
        waitpay_tv.setOnClickListener(this);

        if (getSpecCount(0x05, mList) > 0) {
            waitpay_redot.setVisibility(View.VISIBLE);
            waitpay_redot.setText(String.valueOf(getSpecCount(0x05, mList)));
        } else {
            waitpay_redot.setVisibility(View.GONE);
        }

        mPopupWindow = new PopupWindow(view, mAllBillTv.getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        mPopupWindow.setTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0xf0848484));
        mPopupWindow.showAsDropDown(v, 0, 0);

    }

}

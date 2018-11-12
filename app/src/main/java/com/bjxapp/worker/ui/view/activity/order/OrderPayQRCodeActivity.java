package com.bjxapp.worker.ui.view.activity.order;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bjx.master.R;;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.zxing.encoding.EncodeManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderPayQRCodeActivity extends BaseActivity implements OnClickListener {
    protected static final String TAG = "支付界面";
    private XTextView mTitleTextView;
    private XImageView mBackImageView;
    private XImageView mQRCodeImageView;

    public static final int FROM_PRE_PILL = 0x01;
    public static final int FROM_BILL = 0X02;

    private int mFrom;

    @BindView(R.id.pay_price)
    TextView mPayTv;

    @BindView(R.id.setting_tv)
    XTextView mSettingTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_pay_qrcode);
        ButterKnife.bind(this);
        registerUpdateUIBroadcast();
        super.onCreate(savedInstanceState);

        handleIntent();
    }

    private void handleIntent(){
        if (getIntent() != null){
            mFrom = getIntent().getIntExtra("from",0);
        }

        if (mFrom == 0x01){
            mTitleTextView.setText("订单预支付");
            mSettingTv.setText("请扫码完成预支付金额");
        }

    }

    private void registerUpdateUIBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.PUSH_ACTION_ORDER_MODIFIED);
        broadcastReceiver = new UpdateUIBroadcastReceiver();
        registerReceiver(broadcastReceiver, filter);
    }

    private UpdateUIBroadcastReceiver broadcastReceiver;

    private class UpdateUIBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String orderID = intent.getStringExtra("business_id");
            int pushType = intent.getIntExtra("push_type", 0);
            String message = intent.getStringExtra("message");
            String extra = intent.getStringExtra("extra");
            int payType = intent.getIntExtra("pay_type", 0);

            if (pushType == Constant.PUSH_TYPE_ORDER_PAY) {
                showPaySuccessActivity(orderID, extra);
            }
        }
    }

    private void showPaySuccessActivity(String orderID, String money) {
        Intent intent = new Intent();
        intent.setClass(this, OrderPaySuccessActivity.class);
        intent.putExtra("order_id", orderID);
        intent.putExtra("money", moneyDetail);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        finish(this);
    }

    @Override
    protected void initControl() {
        mTitleTextView = (XTextView) findViewById(R.id.title_text_tv);
        mTitleTextView.setText("订单支付");
        mBackImageView = (XImageView) findViewById(R.id.title_image_back);
        mBackImageView.setVisibility(View.VISIBLE);

        mQRCodeImageView = (XImageView) findViewById(R.id.order_pay_qrcode_image);
    }

    @Override
    protected void initView() {

    }

    String moneyDetail = "";

    @Override
    protected void initData() {
        String url = getIntent().getStringExtra("url");
        String money = getIntent().getStringExtra("money");

        moneyDetail = money;
        mPayTv.setText(!TextUtils.isEmpty(money) ? money + "元" : "");

        Bitmap qrcodeBitmap = EncodeManager.generateQRCode(url);
        mQRCodeImageView.setImageBitmap(qrcodeBitmap);
    }

    @Override
    protected void setListener() {
        mBackImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_image_back:
              //
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    protected String getPageName() {
        return TAG;
    }

    @Override
    public void onBackPressed() {
      //  super.onBackPressed();
        if (mFrom != FROM_PRE_PILL){
            Intent intent = new Intent(this, OrderDetailActivity.class);
            startActivity(intent);
        }else {
            Utils.finishActivity(OrderPayQRCodeActivity.this);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
        }

    }
}

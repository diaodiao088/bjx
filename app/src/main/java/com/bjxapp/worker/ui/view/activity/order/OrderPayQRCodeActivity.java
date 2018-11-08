package com.bjxapp.worker.ui.view.activity.order;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.bjxapp.worker.R;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.zxing.encoding.EncodeManager;

import butterknife.ButterKnife;

public class OrderPayQRCodeActivity extends BaseActivity implements OnClickListener {
    protected static final String TAG = "支付界面";
    private XTextView mTitleTextView;
    private XImageView mBackImageView;
    private XImageView mQRCodeImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_pay_qrcode);
        ButterKnife.bind(this);
        registerUpdateUIBroadcast();
        super.onCreate(savedInstanceState);
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
            Utils.showLongToast(OrderPayQRCodeActivity.this, message);
            // onFirstLoadData(false);

            if (pushType == Constant.PUSH_TYPE_ORDER_PAY) {
                showPaySuccessActivity(orderID, extra);
            }
        }
    }

    private void showPaySuccessActivity(String orderID, String money) {
        Intent intent = new Intent();
        intent.setClass(this, OrderPaySuccessActivity.class);
        intent.putExtra("order_id", orderID);
        intent.putExtra("money", money);
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

    @Override
    protected void initData() {
        String url = getIntent().getStringExtra("url");
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
                Utils.finishActivity(OrderPayQRCodeActivity.this);
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
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
        }

    }
}

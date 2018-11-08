package com.bjxapp.worker.ui.view.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.bjxapp.worker.R;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;

public class OrderPaySuccessActivity extends BaseActivity implements OnClickListener {

    protected static final String TAG = "支付成功界面";
    private XTextView mTitleTextView;
    private XImageView mBackImageView;
    private XTextView mSucessMoney;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_pay_success);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initControl() {
        mTitleTextView = (XTextView) findViewById(R.id.title_text_tv);
        mTitleTextView.setText("订单支付成功");
        mBackImageView = (XImageView) findViewById(R.id.title_image_back);
        mBackImageView.setVisibility(View.VISIBLE);

        mSucessMoney = (XTextView) findViewById(R.id.order_pay_success_money);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        String money = getIntent().getStringExtra("money");
        //String orderID = getIntent().getStringExtra("order_id");
        if (Utils.isNotEmpty(money)) {
            mSucessMoney.setText(money);
        }
    }

    @Override
    protected void setListener() {
        mBackImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_image_back:
                /*Bundle bundle = getIntent().getExtras();
                if (bundle != null && bundle.getString(Constant.EXTRA_RETURN_KEY_CLASS_NAME) != null) {
                    String returnClassName = bundle.getString(Constant.EXTRA_RETURN_KEY_CLASS_NAME);
                    Intent it = new Intent();
                    it.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    String packageName = context.getPackageName();
                    it.setClassName(packageName == null ? Constant.APP_PACKAGE_NAME : packageName, returnClassName);
                    startActivity(it);
                }
                Utils.finishActivity(OrderPaySuccessActivity.this);*/
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();

        Intent intent = new Intent();
        intent.setClass(OrderPaySuccessActivity.this , OrderDetailActivity.class);
        startActivity(intent);
        Utils.finishActivity(OrderPaySuccessActivity.this);
    }

    @Override
    protected String getPageName() {
        return TAG;
    }

}

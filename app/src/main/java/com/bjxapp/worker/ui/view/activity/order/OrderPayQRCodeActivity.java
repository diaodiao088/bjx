package com.bjxapp.worker.ui.view.activity.order;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.bjxapp.worker.R;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
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
		super.onCreate(savedInstanceState);
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

}

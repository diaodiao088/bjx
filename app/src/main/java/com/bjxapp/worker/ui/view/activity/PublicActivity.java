package com.bjxapp.worker.ui.view.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;
import com.bjx.master.R;;

public class PublicActivity extends BaseActivity implements OnClickListener {
	protected static final String TAG = "公共界面";
	private XTextView mTitleTextView;
	private XImageView mBackImageView;;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_public);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initControl() {
		mTitleTextView = (XTextView) findViewById(R.id.title_text_tv);
		String Name = getIntent().getStringExtra("activity_name");
		mTitleTextView.setText(Name);
		mBackImageView = (XImageView) findViewById(R.id.title_image_back);
		mBackImageView.setVisibility(View.VISIBLE);
	}

	@Override
	protected void initView() {

	}

	@Override
	protected void initData() {
	}

	@Override
	protected void setListener() {
		mBackImageView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_image_back:
			Utils.finishActivity(PublicActivity.this);
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

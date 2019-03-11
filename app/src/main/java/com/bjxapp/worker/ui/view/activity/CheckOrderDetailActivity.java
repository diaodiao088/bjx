package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckOrderDetailActivity extends Activity {

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    public static final String

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_order_detail_activity);
        ButterKnife.bind(this);
        initView();
        handleIntent();
        initData();
    }

    private void handleIntent() {

    }

    private void initView() {

        mTitleTextView.setText("服务详情");


    }


    private void initData() {

    }


}

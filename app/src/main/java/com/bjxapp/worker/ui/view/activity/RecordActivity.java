package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecordActivity extends Activity {

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;


    @OnClick(R.id.add_confirm_btn)
    void onClickConfirm(){
        RecordDetailActivity.gotoActivity(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTitleTextView.setText("设备录入");
    }

    public static void gotoActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context , RecordActivity.class);
        context.startActivity(intent);
    }


}

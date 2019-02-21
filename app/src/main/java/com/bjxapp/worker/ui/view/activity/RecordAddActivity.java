package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecordAddActivity extends Activity {

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }
}

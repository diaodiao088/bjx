package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.bjx.master.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MaintainActivity extends Activity {

    @BindView(R.id.title_text_tv)
    TextView mTitleTv;

    @OnClick(R.id.title_image_back)
    void onClickBack() {
        onBackPressed();
    }

    @BindView(R.id.malfun_tv)
    TextView mManfulTv;

    @OnClick(R.id.malfun_tv)
    void onClickManuTv(){

    }

    private ArrayList<String> mMalfulList = new ArrayList<>();

    private String mSelectedMalfulStr;
    private int mSelectedMalfulIndex;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_select);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTitleTv.setText("维修项");





    }


}

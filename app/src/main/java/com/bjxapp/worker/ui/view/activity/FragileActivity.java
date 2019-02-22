package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.ui.view.activity.adapter.FragileAdapter;
import com.bjxapp.worker.ui.view.activity.bean.FragileBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class FragileActivity extends Activity {

    @BindView(R.id.title_right_small_tv)
    TextView mTitleRightTv;

    @OnClick(R.id.title_right_small_tv)
    void onClickSmallTv() {
        FragileBean fragileBean = new FragileBean();
        mList.add(fragileBean);
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @BindView(R.id.fragile_recycler_view)
    RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;

    private FragileAdapter mAdapter;

    private ArrayList<FragileBean> mList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragle_add_activity);
        initView();
        initData();
        handleIntent();
    }

    private void handleIntent() {

    }

    private void initView() {
        mTitleTextView.setText("消毒柜");
        mTitleRightTv.setVisibility(View.VISIBLE);
        mTitleRightTv.setText("添加");

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FragileAdapter();
        mAdapter.setItems(mList);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void initData() {

        FragileBean fragileBean = new FragileBean();

    }


}

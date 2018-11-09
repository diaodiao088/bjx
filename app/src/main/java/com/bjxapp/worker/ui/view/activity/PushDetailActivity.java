package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bjxapp.worker.R;
import com.bjxapp.worker.adapter.PushAdapter;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.listview.XListView;
import com.bjxapp.worker.db.BjxInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhangdan on 2018/11/9.
 *
 * comments:
 */
public class PushDetailActivity extends Activity implements XListView.IXListViewListener {

    @OnClick(R.id.title_image_back)
    void onClickBack() {
        onBackPressed();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTv;

    @BindView(R.id.push_list)
    XListView mListView;

    private PushAdapter mAdapter;

    public static final int BATCH_SIZE = 20;

    private ArrayList<BjxInfo> mList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_detail_activity);
        ButterKnife.bind(this);
        initView();
        onFirstLoadData();
    }

    private void initView() {
        mTitleTv.setText("消息通知");
        mAdapter = new PushAdapter(this, mList);
        mListView.setAdapter(mAdapter);

        mListView.setCacheColorHint(Color.TRANSPARENT);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
    }

    private void onFirstLoadData(){

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }
}

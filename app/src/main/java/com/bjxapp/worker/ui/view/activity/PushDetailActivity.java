package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bjxapp.worker.R;
import com.bjxapp.worker.adapter.PushAdapter;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.listview.XListView;
import com.bjxapp.worker.db.BjxInfo;
import com.bjxapp.worker.db.DBManager;
import com.bjxapp.worker.utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhangdan on 2018/11/9.
 * <p>
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

    public static final int BATCH_SIZE = 100;
    private int mCurrentBatch = 0;

    private ArrayList<BjxInfo> mList = new ArrayList<>();

    private DBManager dbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_detail_activity);
        ButterKnife.bind(this);
        dbManager = new DBManager(this);
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

    private AsyncTask<Void, Void, List<BjxInfo>> mFirstLoadTask;

    private void onFirstLoadData() {

        mFirstLoadTask = new AsyncTask<Void, Void, List<BjxInfo>>() {
            @Override
            protected List<BjxInfo> doInBackground(Void... voids) {

                ArrayList<BjxInfo> list = (ArrayList<BjxInfo>) dbManager.query(BATCH_SIZE, 0 * BATCH_SIZE);

                return list;
            }

            @Override
            protected void onPostExecute(List<BjxInfo> bjxInfos) {

                if (bjxInfos == null || bjxInfos.size() == 0) {
                    return;
                }

                LogUtils.log(bjxInfos.toString());

                mCurrentBatch = 0;

                mList.clear();
                mList.addAll(bjxInfos);

                markAllAsRead();

                mAdapter.notifyDataSetChanged();

                mCurrentBatch++;
            }
        };

        mFirstLoadTask.execute();
    }

    private void markAllAsRead(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    dbManager.updateAsRead();
                }catch (Exception e){

                }
            }
        }).run();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private AsyncTask<Void, Void, List<BjxInfo>> mRefreshTask;

    @Override
    public void onRefresh() {

        mRefreshTask = new AsyncTask<Void, Void, List<BjxInfo>>() {
            @Override
            protected List<BjxInfo> doInBackground(Void... voids) {

                ArrayList<BjxInfo> list = (ArrayList<BjxInfo>) dbManager.query(BATCH_SIZE, 0 * BATCH_SIZE);

                return list;
            }

            @Override
            protected void onPostExecute(List<BjxInfo> bjxInfos) {

                if (bjxInfos == null) {
                    return;
                }

                mCurrentBatch = 0;

                mList.clear();
                mList.addAll(bjxInfos);
                mAdapter.notifyDataSetChanged();
                mCurrentBatch++;
                onLoadFinished();
                markAllAsRead();
            }
        };

        mRefreshTask.execute();
    }

    private AsyncTask<Void, Void, List<BjxInfo>> mLoadMoreTask;

    @Override
    public void onLoadMore() {

        mLoadMoreTask = new AsyncTask<Void, Void, List<BjxInfo>>() {
            @Override
            protected List<BjxInfo> doInBackground(Void... voids) {

                ArrayList<BjxInfo> list = (ArrayList<BjxInfo>) dbManager.query(BATCH_SIZE, mCurrentBatch * BATCH_SIZE);

                return list;
            }

            @Override
            protected void onPostExecute(List<BjxInfo> bjxInfos) {

                if (bjxInfos == null) {
                    return;
                }

                mCurrentBatch++;
                mList.addAll(bjxInfos);
                mAdapter.notifyDataSetChanged();
                onLoadFinished();
                markAllAsRead();
            }
        };

        mLoadMoreTask.execute();
    }

    private void onLoadFinished() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String refreshTimeString = format.format(new Date());
        mListView.setRefreshTime(refreshTimeString);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {

            if (mFirstLoadTask != null) {
                mFirstLoadTask.cancel(true);
            }

            if (mLoadMoreTask != null) {
                mLoadMoreTask.cancel(true);
            }

            if (mRefreshTask != null) {
                mRefreshTask.cancel(true);
            }

        } catch (Exception e) {

        }
    }
}

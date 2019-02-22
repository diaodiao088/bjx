package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
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
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragileActivity extends Activity {

    @BindView(R.id.title_right_small_tv)
    TextView mTitleRightTv;

    @OnClick(R.id.title_right_small_tv)
    void onClickSmallTv() {
        FragileBean fragileBean = new FragileBean();
        FragileBean.ImageBean bean = fragileBean.new ImageBean(FragileBean.ImageBean.TYPE_IMAGE, "");
        fragileBean.getImageList().add(bean);
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
        ButterKnife.bind(this);
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
        mAdapter.setListener(new FragileAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(int position) {
                mList.remove(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void addImage(int position) {

            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(15));

    }

    private void initData() {

        FragileBean fragileBean = new FragileBean();

    }

    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        int mSpace;

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = mSpace;
        }

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }
    }

    public static void gotoActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, FragileActivity.class);
        context.startActivity(intent);
    }

}

package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.ui.view.activity.bean.RecordBean;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.RecordItemLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecordDetailActivity extends Activity {

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @BindView(R.id.record_recycler_view)
    RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;

    private RecordAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_detail_activity);
        ButterKnife.bind(this);
        initView();
        bindData();
    }

    private void bindData() {

        ArrayList<RecordBean> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            RecordBean recordBean = new RecordBean();
            recordBean.setTypeName("录入详情：" + i);

            ArrayList<RecordBean.RecordItemBean> list1 = new ArrayList<>();

            for (int j = 0; j < 3; j++) {
                RecordBean.RecordItemBean bean = recordBean.new RecordItemBean();
                bean.setName("消毒柜：" + j);
                list1.add(bean);
            }

            recordBean.setmItemList(list1);


            list.add(recordBean);
        }

        mAdapter.setItems(list);

    }

    private void initView() {
        mTitleTextView.setText("录入详情");

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RecordAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(DimenUtils.dp2px(15, this)));

    }

    public static void gotoActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, RecordDetailActivity.class);
        context.startActivity(intent);
    }


    private class RecordAdapter extends RecyclerView.Adapter<RecordBaseHolder> {

        private ArrayList<RecordBean> mList = new ArrayList<>();

        @Override
        public RecordBaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item_layout, parent, false);

            return new RecordBaseHolder(view);
        }

        @Override
        public void onBindViewHolder(RecordBaseHolder holder, int position) {

            RecordBean recordBean = mList.get(position);

            holder.bindData(recordBean);
        }

        public void setItems(ArrayList<RecordBean> list) {
            this.mList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private class RecordBaseHolder extends RecyclerView.ViewHolder {

        private TextView mRecordTypeTv;
        private LinearLayout mRecordItemContainer;

        public RecordBaseHolder(View itemView) {
            super(itemView);
            mRecordTypeTv = itemView.findViewById(R.id.type_name_tv);
            mRecordItemContainer = itemView.findViewById(R.id.record_item_container);
        }

        public void bindData(RecordBean recordBean) {

            if (!TextUtils.isEmpty(recordBean.getTypeName())) {
                mRecordTypeTv.setText(recordBean.getTypeName());
            }

            ArrayList<RecordBean.RecordItemBean> itemList = recordBean.getmItemList();

            if (itemList.size() > 0) {
                mRecordItemContainer.removeAllViews();
                for (int i = 0; i < itemList.size(); i++) {
                    generateItemLayout(itemList.get(i));
                }
            } else {
                mRecordItemContainer.setVisibility(View.GONE);
            }

        }

        public void generateItemLayout(RecordBean.RecordItemBean itemBean) {


            RecordItemLayout itemLayout = new RecordItemLayout(mRecordItemContainer.getContext());

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    DimenUtils.dp2px(45, mRecordItemContainer.getContext()));

            itemLayout.bindData(itemBean);

            mRecordItemContainer.addView(itemLayout, layoutParams);
        }

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

}

package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.ui.view.activity.order.ImageOrderActivity;
import com.bjxapp.worker.ui.view.activity.widget.SpaceItemDecoration;
import com.bjxapp.worker.ui.widget.RoundImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageListActivity extends Activity {

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.result_tv)
    TextView mResultTv;

    @BindView(R.id.result_container)
    RecyclerView mResultRecyclerView;

    @BindView(R.id.method_container)
    RecyclerView mPlanRecyclerView;


    public static final String TYPE_LIST = "plan_list";
    public static final String TYPE_RESULT = "result_list";

    ArrayList<String> planList = new ArrayList<>();
    ArrayList<String> resultList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_list_activity);
        ButterKnife.bind(this);

        mTitleTextView.setText("维修照片");

        planList = getIntent().getStringArrayListExtra(TYPE_LIST);
        resultList = getIntent().getStringArrayListExtra(TYPE_RESULT);

        if (resultList == null || resultList.size() <= 0) {
            mResultTv.setVisibility(View.GONE);
            mResultRecyclerView.setVisibility(View.GONE);
        }

        if (planList != null && planList.size() > 0) {
            GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 4);
            mPlanRecyclerView.setLayoutManager(mGridLayoutManager);
            mPlanRecyclerView.addItemDecoration(new SpaceItemDecoration(4, 50, false));
            mPlanRecyclerView.setAdapter(new MyAdapter());
        }

        if (resultList != null && resultList.size() > 0) {
            GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 4);
            mResultRecyclerView.setLayoutManager(mGridLayoutManager);
            mResultRecyclerView.addItemDecoration(new SpaceItemDecoration(4, 50, false));
            mResultRecyclerView.setAdapter(new MyAdapterResult());
        }

    }


    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        public MyAdapter() {

        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iamge_single, parent, false);

            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {

            holder.setData(planList.get(position));
        }


        @Override
        public int getItemCount() {
            return planList.size();
        }
    }

    private class MyAdapterResult extends RecyclerView.Adapter<MyHolder> {

        public MyAdapterResult() {

        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iamge_single, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {

            holder.setData(planList.get(position));

        }


        @Override
        public int getItemCount() {
            return resultList.size();
        }

    }


    class MyHolder extends RecyclerView.ViewHolder {

        ImageView imgIv;

        public MyHolder(View itemView) {
            super(itemView);
            imgIv = itemView.findViewById(R.id.screenShotImageView);
        }

        public void setData(final String imgPath) {
            Glide.with(ImageListActivity.this).load(imgPath).into(imgIv);

            imgIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageOrderActivity.goToActivity(ImageListActivity.this , imgPath);
                }
            });

        }
    }


    public static void goToActivity(Context act, ArrayList<String> planList, ArrayList<String> resultList) {

        Intent intent = new Intent();

        intent.putExtra(TYPE_LIST, planList);
        intent.putExtra(TYPE_RESULT, resultList);

        intent.setClass(act, ImageListActivity.class);
        act.startActivity(intent);
    }


}

package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bjxapp.worker.R;
import com.bjxapp.worker.ui.view.activity.widget.RoundImageView;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bumptech.glide.Glide;

/**
 * Created by zhangdan on 2018/9/29.
 * <p>
 * comments:
 */

public class ImageDetailActivity extends Activity {

    public RecyclerView mRecyclerView;

    private String[] mUrlList;

    private MyAdapter mAdapter;

    public static final String LIST_TAG = "img_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.image_detail_activity);
        handleIntent();
        initView();
    }

    private void handleIntent() {

        Intent intent = getIntent();

        if (intent != null) {
            mUrlList = intent.getStringArrayExtra(LIST_TAG);
        }

        mUrlList = new String[]{"http://pic29.nipic.com/20130511/9252150_174018365301_2.jpg",
                "http://pic11.nipic.com/20101203/6161416_131226032949_2.jpg"};

    }

    protected void initView() {

        mRecyclerView = findViewById(R.id.image_detail_recycler_view);

        mAdapter = new MyAdapter(this);
        mAdapter.setItems(mUrlList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(DimenUtils.dp2px(12 , this)));
        mRecyclerView.setAdapter(mAdapter);
    }

    public class MyAdapter extends RecyclerView.Adapter<VH> {

        private String[] mList;

        private Context mCtx;

        public MyAdapter(Context ctx) {
            this.mCtx = ctx;
        }

        public void setItems(String[] list) {
            this.mList = list;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_detail, parent, false);

            return new VH(view);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            Glide.with(mCtx).load(mList[position]).into(holder.mImg);
        }

        @Override
        public int getItemCount() {
            return mList.length;
        }
    }

    public class VH extends RecyclerView.ViewHolder {

        private RoundImageView mImg;

        public VH(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.round_img);
            mImg.setMode(RoundImageView.MODE_DEFAULT);
            mImg.setType(RoundImageView.TYPE_ROUND_ALL);
            mImg.setBorderRadius(DimenUtils.dp2px(4 , mImg.getContext()));
        }

    }

    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        int mSpace;

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildAdapterPosition(view);
            if (position == (parent.getAdapter().getItemCount() - 1)) {
                outRect.bottom = 0;
            } else {
                outRect.bottom = mSpace;
            }
        }

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }
    }

}

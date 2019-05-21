package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.model.FollowUpBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FollowUpActivity extends Activity {

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private MyAdapter myAdapter;

    public static final String TYPE_FOLLOW = "type_follow";

    private ArrayList<FollowUpBean> mlist = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_up_layout);
        ButterKnife.bind(this);

        mlist = getIntent().getParcelableArrayListExtra(TYPE_FOLLOW);

        if (mlist == null) {
            mlist = new ArrayList<>();
        }

        mTitleTextView.setText("跟进");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        myAdapter = new MyAdapter();

        mRecyclerView.setAdapter(myAdapter);

    }

    private class MyAdapter extends RecyclerView.Adapter<FollowUpHolder> {


        @Override
        public FollowUpHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow_up, parent, false);

            return new FollowUpHolder(view);
        }

        @Override
        public void onBindViewHolder(FollowUpHolder holder, int position) {

            FollowUpBean followUpBean = mlist.get(position);

            holder.setData(followUpBean);

        }

        @Override
        public int getItemCount() {
            return mlist.size();
        }
    }


    private class FollowUpHolder extends RecyclerView.ViewHolder {

        TextView mTimeTv;

        TextView mContentTv;

        public FollowUpHolder(View itemView) {
            super(itemView);
            mTimeTv = itemView.findViewById(R.id.time);
            mContentTv = itemView.findViewById(R.id.content);
        }

        public void setData(FollowUpBean bean) {
            mTimeTv.setText(getFormatTime(bean.getCreateTime()));
            mContentTv.setText(bean.getContent());
        }

        private String getFormatTime(long time) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss");
            java.util.Date dt = new Date(time);
            String sDateTime = sdf.format(dt);  //得到精确到秒的表示：08/31/2006 21:08:00
            return sDateTime;
        }

    }

    public static void goToActivity(Context context, ArrayList<FollowUpBean> content) {
        Intent intent = new Intent();
        intent.setClass(context, FollowUpActivity.class);
        intent.putParcelableArrayListExtra(TYPE_FOLLOW, content);
        context.startActivity(intent);
    }

}

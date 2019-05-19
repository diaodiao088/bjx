package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;

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

    public static final String TYPE_FOLLOW = "type_follow";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mTitleTextView.setText("跟进");

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    private class FollowUpHolder extends RecyclerView.ViewHolder {

        TextView mTimeTv;

        TextView mContentTv;

        public FollowUpHolder(View itemView) {
            super(itemView);
            mTimeTv = itemView.findViewById(R.id.time);
            mContentTv = itemView.findViewById(R.id.content);
        }
    }


    public static void goToActivity(Context context, String content) {
        Intent intent = new Intent();
        intent.setClass(context, FollowUpActivity.class);
        intent.putExtra(TYPE_FOLLOW, content);
        context.startActivity(intent);
    }

}

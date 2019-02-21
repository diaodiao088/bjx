package com.bjxapp.worker.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.ui.view.activity.RecordAddActivity;
import com.bjxapp.worker.ui.view.activity.bean.RecordBean;

public class RecordItemLayout extends LinearLayout {

    private TextView mSubNameTv, mSubStatusTv;

    private View mRootView;

    private RecordBean.RecordItemBean itemBean;

    public RecordItemLayout(Context context) {
        super(context);
        init();
    }

    public RecordItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        LayoutInflater.from(getContext()).inflate(R.layout.record_item_sub_layout, this);

        mSubNameTv = findViewById(R.id.sub_name);
        mSubStatusTv = findViewById(R.id.sub_status);
    }

    public void bindData(RecordBean.RecordItemBean itemBean) {
        this.itemBean = itemBean;
        mSubNameTv.setText(itemBean.getName());
        mSubStatusTv.setText(itemBean.getStatus() == 1 ? "已录入" : "待录入");

        mSubStatusTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordAddActivity.gotoActivity((Activity) getContext(), RecordAddActivity.REQUEST_CODE_RECORD_ADD);
            }
        });

    }


}

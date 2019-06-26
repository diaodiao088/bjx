package com.bjxapp.worker.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.ui.view.activity.CheckOrderDetailActivity;
import com.bjxapp.worker.ui.view.activity.DeviceInfoActivity;
import com.bjxapp.worker.ui.view.activity.bean.CheckDetailBean;

public class CheckOrderItemLayout extends LinearLayout {

    private TextView mSubNameTv, mSubStatusTv;

    private View mRootView;

    private CheckDetailBean.DeviceBean itemBean;

    public CheckOrderItemLayout(Context context) {
        super(context);
        init();
    }

    public CheckOrderItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CheckOrderItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.check_item_sub_layout, this);
        mSubNameTv = findViewById(R.id.sub_name);
        mSubStatusTv = findViewById(R.id.sub_status);
    }

    public void update(){
        if (itemBean != null){
            if (itemBean.getStatus() == 2){
                mSubStatusTv.setText("已完成");
                mSubStatusTv.setTextColor(Color.parseColor("#00a551"));
            }
        }
    }

    public void bindData(final int processState , final CheckDetailBean.DeviceBean itemBean, final String shopId,
                         final boolean flag, final CheckOrderDetailActivity parentAct) {
        this.itemBean = itemBean;
        mSubNameTv.setText(itemBean.getEquipName());

        if (itemBean.getStatus() == 1) {
            mSubStatusTv.setText("已提交");
            mSubStatusTv.setTextColor(Color.parseColor("#00a551"));
        } else if (itemBean.getStatus() == 0) {
            mSubStatusTv.setText("未提交");
            mSubStatusTv.setTextColor(Color.parseColor("#f96057"));
        } else if (itemBean.getStatus() == 2){
            mSubStatusTv.setText("已完成");
            mSubStatusTv.setTextColor(Color.parseColor("#00a551"));
        }

        mSubStatusTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //  RecordAddActivity.goToActivity((Activity) getContext(), itemBean, shopId);
                DeviceInfoActivity.goToActivityForResult((Activity) getContext(), itemBean.getId() , processState <= 3 , flag);

                parentAct.clickBean = itemBean;
                parentAct.clickLayout = CheckOrderItemLayout.this;
            }
        });

    }


}

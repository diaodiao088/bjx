package com.bjxapp.worker.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.model.MainTainBean;

public class MaintainItemLayout extends LinearLayout implements View.OnClickListener {

    private View mRootView;

    private TextView mPlusTv, mLessTv;

    private TextView mCountTv;

    private TextView mTypeNameTv;

    private TextView mPriceTv;

    private TextView mDelTv;

    private LinearLayout mOtherLy;
    private TextView mRealPriceTv;

    private MainTainBean maintainInfo;

    private TextView mNameTv;

    public MaintainItemLayout(Context context) {
        super(context);
        init();
    }

    public MaintainItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaintainItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.maintain_item_layout, this);

        mPlusTv = mRootView.findViewById(R.id.plus_maintain_tv);
        mLessTv = mRootView.findViewById(R.id.less_maintain_tv);
        mCountTv = mRootView.findViewById(R.id.count_tv);

        mTypeNameTv = mRootView.findViewById(R.id.maintain_item_type_name_tv);
        mPriceTv = mRootView.findViewById(R.id.other_price_ev);
        mOtherLy = mRootView.findViewById(R.id.other_price_ly);

        mRealPriceTv = mRootView.findViewById(R.id.real_price_tv);
        mDelTv = mRootView.findViewById(R.id.del_tv);


    }

    public void bindData(MainTainBean maintainInfo) {
        this.maintainInfo = maintainInfo;

        if (maintainInfo.isOthers()) {
            mOtherLy.setVisibility(VISIBLE);
            mRealPriceTv.setVisibility(GONE);

        } else {
            mOtherLy.setVisibility(GONE);
            mRealPriceTv.setVisibility(VISIBLE);
            mNameTv.setText(maintainInfo.getComponentName());
            mTypeNameTv.setText(maintainInfo.getModel());
        }

        mCountTv.setText(maintainInfo.getQuantity());

    }


    @Override
    public void onClick(View v) {

    }


}

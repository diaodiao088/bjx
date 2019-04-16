package com.bjxapp.worker.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjx.master.R;

public class MaintainItemLayout extends LinearLayout implements View.OnClickListener {

    private View mRootView;

    private TextView mPlusTv, mLessTv;

    private TextView mCountTv;

    private TextView mTypeNameTv;

    private TextView mPriceTv;

    private TextView mDelTv;

    private LinearLayout mOtherLy;
    private TextView mRealPriceTv;

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

    private void bindData() {

    }


    @Override
    public void onClick(View v) {

    }


}

package com.bjxapp.worker.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
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

    private TextView mRenGongTv;

    private TextView mNameTv;
    private EditText mNameEv;

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

        mNameEv = mRootView.findViewById(R.id.name_ev);
        mNameTv = mRootView.findViewById(R.id.name);

        mRenGongTv = mRootView.findViewById(R.id.rengong_price_tv);

        mPlusTv.setOnClickListener(this);
        mLessTv.setOnClickListener(this);
        mDelTv.setOnClickListener(this);

        mPriceTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String realStr = s.toString();

                maintainInfo.setCost(realStr);

                if (listener != null) {
                    listener.onPriceChange();
                }

            }
        });

        mNameEv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                maintainInfo.setComponentName(s.toString());

            }
        });

    }

    public void bindData(MainTainBean maintainInfo) {
        this.maintainInfo = maintainInfo;

        if (maintainInfo.isOthers()) {
            mOtherLy.setVisibility(VISIBLE);
            mRealPriceTv.setVisibility(GONE);
            mNameTv.setVisibility(GONE);
            mNameEv.setVisibility(VISIBLE);
            mPriceTv.setText(maintainInfo.getCost());
            mNameEv.setText(maintainInfo.getComponentName());
        } else {
            mOtherLy.setVisibility(GONE);
            mRealPriceTv.setVisibility(VISIBLE);
            mNameTv.setVisibility(VISIBLE);
            mNameEv.setVisibility(GONE);
            mNameTv.setText(maintainInfo.getComponentName());

            mRenGongTv.setText("¥" + maintainInfo.getLaborCost() + "/" + maintainInfo.getUnit());
            mRealPriceTv.setText("¥" + maintainInfo.getRengongCost() + "/" + maintainInfo.getUnit());
        }

        mTypeNameTv.setText(maintainInfo.getModel());
        mCountTv.setText(String.valueOf(maintainInfo.getQuantity()));
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.del_tv:
                final ViewParent viewParent = getParent();
                if (viewParent != null && viewParent instanceof ViewGroup) {
                    ((ViewGroup) viewParent).removeViewInLayout(MaintainItemLayout.this);

                    viewParent.requestLayout();
                }

                if (listener != null) {
                    listener.onDelete(maintainInfo);
                }

                break;

            case R.id.less_maintain_tv:
                int currentCount = maintainInfo.getQuantity();

                if (currentCount > 1) {
                    currentCount--;
                }

                mCountTv.setText(String.valueOf(currentCount));

                maintainInfo.setQuantity(currentCount);

                if (listener != null) {
                    listener.onCountChange();
                }

                break;
            case R.id.plus_maintain_tv:

                int currentCountPlus = maintainInfo.getQuantity();
                currentCountPlus++;

                mCountTv.setText(String.valueOf(currentCountPlus));
                maintainInfo.setQuantity(currentCountPlus);

                if (listener != null) {
                    listener.onCountChange();
                }

                break;
        }
    }

    public interface OnOperationListener {

        void onDelete(MainTainBean mainTainBean);

        void onPriceChange();

        void onCountChange();

    }

    public OnOperationListener listener;

    public void setOperationListener(OnOperationListener listener) {
        this.listener = listener;
    }


}

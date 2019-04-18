package com.bjxapp.worker.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.model.MainTainBean;

public class MaintainItemLayoutStub extends LinearLayout implements View.OnClickListener {

    private View mRootView;


    private TextView mCountTv;

    private TextView mTypeNameTv;

    private TextView mRealPriceTv;

    private MainTainBean maintainInfo;

    private TextView mNameTv;

    public MaintainItemLayoutStub(Context context) {
        super(context);
        init();
    }

    public MaintainItemLayoutStub(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaintainItemLayoutStub(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.maintain_item_stub_layout, this);

        mCountTv = mRootView.findViewById(R.id.count_tv);

        mTypeNameTv = mRootView.findViewById(R.id.maintain_item_type_name_tv);

        mRealPriceTv = mRootView.findViewById(R.id.real_price_tv);

        mNameTv = mRootView.findViewById(R.id.name);
    }

    public void bindData(MainTainBean maintainInfo) {
        this.maintainInfo = maintainInfo;

        mRealPriceTv.setVisibility(VISIBLE);
        mNameTv.setVisibility(VISIBLE);
        mNameTv.setText(maintainInfo.getComponentName());

        if (!TextUtils.isEmpty(maintainInfo.getUnit())){
            mRealPriceTv.setText(maintainInfo.getCost() + "/" + maintainInfo.getUnit());
        }else{
            mRealPriceTv.setText(maintainInfo.getCost());
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
                    ((ViewGroup) viewParent).removeViewInLayout(MaintainItemLayoutStub.this);
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

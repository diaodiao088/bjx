package com.bjxapp.worker.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.model.MainTainBean;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MaintainCallItemLayout extends LinearLayout implements View.OnClickListener {

    private View mRootView;

    @BindView(R.id.issue_reason_tv)
    XTextView mReasonTv;

    @BindView(R.id.strategy_content_tv)
    XTextView mNextTimeTv;

    @BindView(R.id.guzhang_content_tv)
    XTextView mGuZhangTv;

    @BindView(R.id.modify_content_tv)
    XTextView mModifyTv;

    @BindView(R.id.price_content)
    TextView mPriceTv;

    private LinearLayout mOtherLy;
    private TextView mRealPriceTv;

    private MainTainBean maintainInfo;


    public MaintainCallItemLayout(Context context) {
        super(context);
        init();
    }

    public MaintainCallItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaintainCallItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.maintain_xietiao_layout, this);

        ButterKnife.bind(this);


        mOtherLy = mRootView.findViewById(R.id.other_price_ly);

        mRealPriceTv = mRootView.findViewById(R.id.real_price_tv);


    }

    public void bindData(MainTainBean maintainInfo) {
        this.maintainInfo = maintainInfo;

//        if (maintainInfo.isOthers()) {
//            mOtherLy.setVisibility(VISIBLE);
//            mRealPriceTv.setVisibility(GONE);
//            mNameTv.setVisibility(GONE);
//            mNameEv.setVisibility(VISIBLE);
//            mPriceTv.setText(maintainInfo.getCost());
//            mNameEv.setText(maintainInfo.getComponentName());
//        } else {
//            mOtherLy.setVisibility(GONE);
//            mRealPriceTv.setVisibility(VISIBLE);
//            mNameTv.setVisibility(VISIBLE);
//            mNameEv.setVisibility(GONE);
//            mNameTv.setText(maintainInfo.getComponentName());
//
//            mRealPriceTv.setText(maintainInfo.getCost() + "/" + maintainInfo.getUnit());
//        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.del_tv:
                final ViewParent viewParent = getParent();
                if (viewParent != null && viewParent instanceof ViewGroup) {
                    ((ViewGroup) viewParent).removeViewInLayout(MaintainCallItemLayout.this);

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

                maintainInfo.setQuantity(currentCount);

                if (listener != null) {
                    listener.onCountChange();
                }

                break;
            case R.id.plus_maintain_tv:

                int currentCountPlus = maintainInfo.getQuantity();
                currentCountPlus++;

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

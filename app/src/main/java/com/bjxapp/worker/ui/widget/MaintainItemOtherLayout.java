package com.bjxapp.worker.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.model.MainTainBean;
import com.bjxapp.worker.model.ThiOtherBean;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MaintainItemOtherLayout extends LinearLayout implements View.OnClickListener {

    private View mRootView;

    private TextView mPlusTv, mLessTv;

    private TextView mCountTv;

    private TextView mTypeNameTv;

    private TextView mRenGongTv;

    private TextView mDelTv;

    private TextView mRealPriceTv;

    private TextView mRemarkTv;

    private MainTainBean maintainInfo;

    private TextView mNameTv;

    private LinearLayout mImageLy;

    public MaintainItemOtherLayout(Context context) {
        super(context);
        init();
    }

    public MaintainItemOtherLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaintainItemOtherLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.maintain_item_layout_new, this);

        mPlusTv = mRootView.findViewById(R.id.plus_maintain_tv);
        mLessTv = mRootView.findViewById(R.id.less_maintain_tv);
        mCountTv = mRootView.findViewById(R.id.count_tv);

        mTypeNameTv = mRootView.findViewById(R.id.maintain_item_type_name_tv);
        mRenGongTv = mRootView.findViewById(R.id.rengong_tv);

        mRemarkTv = mRootView.findViewById(R.id.remark_tv);

        mRealPriceTv = mRootView.findViewById(R.id.real_price_tv);
        mDelTv = mRootView.findViewById(R.id.del_tv);

        mImageLy = mRootView.findViewById(R.id.img_ly);

        mNameTv = mRootView.findViewById(R.id.name);

        mPlusTv.setOnClickListener(this);
        mLessTv.setOnClickListener(this);
        mDelTv.setOnClickListener(this);

    }

    public void bindData(MainTainBean maintainInfo) {
        this.maintainInfo = maintainInfo;

        ThiOtherBean thiOtherBean = maintainInfo.getThiOtherBean();

        mRealPriceTv.setVisibility(VISIBLE);
        mNameTv.setVisibility(VISIBLE);
        mNameTv.setText(thiOtherBean.getName());

        mRealPriceTv.setText(thiOtherBean.getCost());

        if (!TextUtils.isEmpty(thiOtherBean.getModel())) {
            mTypeNameTv.setText(maintainInfo.getModel());
        } else {
            mTypeNameTv.setText(maintainInfo.getModel());
        }

        if (!TextUtils.isEmpty(thiOtherBean.getRenGongCost())) {
            mRenGongTv.setText(thiOtherBean.getRenGongCost());
        }

        mRemarkTv.setText(thiOtherBean.getRemark());

        mCountTv.setText(String.valueOf(maintainInfo.getQuantity()));

        addImageIfNeed(thiOtherBean);
    }

    private void addImageIfNeed(ThiOtherBean thiOtherBean) {

        ArrayList<String> imgList = thiOtherBean.getImgList();

        if (imgList.size() <= 0) {
            return;
        }

        for (int i = 0; i < imgList.size(); i++) {
            ImageView imageView = new ImageView(getContext());

            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(DimenUtils.dp2px(60, getContext()),
                    DimenUtils.dp2px(60, getContext()));

            lp.setMargins(0, 0, DimenUtils.dp2px(10, getContext()), 0);

            Glide.with(getContext()).load(imgList.get(i)).into(imageView);

            mImageLy.addView(imageView, lp);
        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.del_tv:
                final ViewParent viewParent = getParent();
                if (viewParent != null && viewParent instanceof ViewGroup) {
                    ((ViewGroup) viewParent).removeViewInLayout(MaintainItemOtherLayout.this);

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

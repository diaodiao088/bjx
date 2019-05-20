package com.bjxapp.worker.ui.widget;

import android.app.Activity;
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
import com.bjxapp.worker.model.MainTainBean;
import com.bjxapp.worker.model.OtherPriceBean;
import com.bjxapp.worker.ui.view.activity.RecordAddActivity;

public class OtherPriceLayout extends LinearLayout {

    private TextView mSubNameTv, mPriceTv;

    private View mRootView;

    private OtherPriceBean itemBean;

    public OtherPriceLayout(Context context) {
        super(context);
        init();
    }

    public OtherPriceLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OtherPriceLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        LayoutInflater.from(getContext()).inflate(R.layout.other_price_sub_layout, this);

        mSubNameTv = findViewById(R.id.sub_name);
        mPriceTv = findViewById(R.id.price_tv);
    }

    public void bindData(final OtherPriceBean itemBean, final String shopId) {
        this.itemBean = itemBean;
        mSubNameTv.setText(itemBean.getName());
        mPriceTv.setText("$" + itemBean.getPrice());

        mSubNameTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final ViewParent viewParent = getParent();
                if (viewParent != null && viewParent instanceof ViewGroup) {
                    ((ViewGroup) viewParent).removeViewInLayout(OtherPriceLayout.this);

                    viewParent.requestLayout();
                }

                if (listener != null) {
                    listener.onDelete(itemBean);
                }
            }
        });
    }

    public interface OnOperationListener {

        void onDelete(OtherPriceBean priceBean);

    }

    public OnOperationListener listener;

    public void setOperationListener(OnOperationListener listener) {
        this.listener = listener;
    }


}

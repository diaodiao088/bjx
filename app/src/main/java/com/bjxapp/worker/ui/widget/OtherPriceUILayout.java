package com.bjxapp.worker.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.model.MainTainBean;
import com.bjxapp.worker.model.OtherPriceBean;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OtherPriceUILayout extends LinearLayout {

    private OtherPriceBean mainTainBean;

    @BindView(R.id.name_tv)
    TextView mNameTv;

    @BindView(R.id.price_tv)
    TextView mPriceTv;

    public OtherPriceUILayout(Context context) {
        super(context);
        init();
    }

    public OtherPriceUILayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OtherPriceUILayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.other_price_layout, this);
        ButterKnife.bind(this);
    }

    public void bindData(OtherPriceBean mainTainBean) {
        this.mainTainBean = mainTainBean;

        mNameTv.setText(mainTainBean.getName());

        mPriceTv.setText("¥" + mainTainBean.getPrice());

    }

}

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

import butterknife.BindView;
import butterknife.ButterKnife;

public class PeijianUILayout extends LinearLayout {

    private MainTainBean mainTainBean;

    @BindView(R.id.name_tv)
    TextView mNameTv;

    @BindView(R.id.model_tv)
    TextView mModelTv;

    @BindView(R.id.count_tv)
    TextView mCountTv;

    @BindView(R.id.price_tv)
    TextView mPriceTv;

    public PeijianUILayout(Context context) {
        super(context);
        init();
    }

    public PeijianUILayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PeijianUILayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.peijian_ui_layout, this);
        ButterKnife.bind(this);
    }

    public void bindData(MainTainBean mainTainBean) {
        this.mainTainBean = mainTainBean;

        mNameTv.setText(mainTainBean.getComponentName());
        mModelTv.setText((TextUtils.isEmpty(mainTainBean.getModel()) ? "其他" : mainTainBean.getModel()));

        mCountTv.setText("X" + mainTainBean.getQuantity() + (TextUtils.isEmpty(mainTainBean.getUnit()) ? "个" : mainTainBean.getUnit()));

        mPriceTv.setText("¥" + mainTainBean.getCost());

    }

}

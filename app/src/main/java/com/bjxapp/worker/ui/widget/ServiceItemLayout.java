package com.bjxapp.worker.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.ui.view.activity.DeviceInfoActivity;
import com.bjxapp.worker.ui.view.activity.bean.CheckDetailBean;

import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;

public class ServiceItemLayout extends LinearLayout {

    private TextView mIndexTv, mServiceNameTv;

    private TextView mMaxTv;

    private TextView mScoreTv;

    private TextView mScoreBtn;


    private View mRootView;

    private CheckDetailBean.DeviceBean itemBean;

    private DeviceInfoActivity.ServiceItem serviceItem;

    public ServiceItemLayout(Context context) {
        super(context);
        init();
    }

    public ServiceItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ServiceItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        LayoutInflater.from(getContext()).inflate(R.layout.service_item_sub_layout, this);

        mIndexTv = findViewById(R.id.index);
        mServiceNameTv = findViewById(R.id.service_name_tv);
        mMaxTv = findViewById(R.id.service_max_tv);

        mScoreTv = findViewById(R.id.service_score_tv);
        mScoreBtn = findViewById(R.id.service_btn);

    }


    public void bindData(int index, final DeviceInfoActivity.ServiceItem serviceItem, boolean isNeedMod) {

        this.serviceItem = serviceItem;

        mIndexTv.setText(String.valueOf(index + 1) + ".");


        mMaxTv.setText("（" + serviceItem.getMaxScore() + "分）");

        mServiceNameTv.setText(serviceItem.getProcessName());

        if (!TextUtils.isEmpty(serviceItem.getActualScore())) {

            if ("0".equals(serviceItem.getActualScore())) {
                mScoreTv.setText("不正常");
            } else {
                mScoreTv.setText("正常");
            }
        }

        if (!isNeedMod){
            mScoreBtn.setVisibility(GONE);
        }

        mScoreBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(serviceItem.getMaxScore());
            }
        });

    }

    private void showTimePicker(final int maxScore) {
        OptionPicker picker = new OptionPicker((Activity) getContext(),
                new String[]{"正常", "不正常"});
        picker.setCycleDisable(true);//不禁用循环
        picker.setTopBackgroundColor(0xFFffffff);
        picker.setTopHeight(30);
        picker.setTopLineColor(0xfffdfdfd);
        picker.setTopLineHeight(3);
        picker.setTitleText("选择分值");
        picker.setTitleTextColor(0xFF545454);
        picker.setTitleTextSize(14);
        picker.setCancelTextColor(0xFF545454);
        picker.setCancelTextSize(12);
        picker.setSubmitTextColor(0xFF00a551);
        picker.setSubmitTextSize(12);
        picker.setTextColor(0xFF545454, 0x99545454);
        WheelView.DividerConfig config = new WheelView.DividerConfig();
        config.setColor(0xFff5f5f5);//线颜色
        config.setAlpha(250);//线透明度
        config.setRatio((float) (1.0 / 8.0));//线比率
        picker.setDividerConfig(config);
        picker.setBackgroundColor(0xFFffffff);
        picker.setSelectedIndex(0);
        picker.setCanceledOnTouchOutside(true);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                mScoreTv.setText(item);
                if ("正常".equals(item)) {
                    serviceItem.setActualScore(String.valueOf(maxScore));
                } else {
                    serviceItem.setActualScore(String.valueOf(0));
                }

            }
        });
        picker.show();
    }

    private String[] generateString(int maxScore) {

        String[] str = new String[maxScore];

        for (int i = 0; i < maxScore; i++) {
            str[i] = String.valueOf(i + 1);
        }

        return str;
    }

}

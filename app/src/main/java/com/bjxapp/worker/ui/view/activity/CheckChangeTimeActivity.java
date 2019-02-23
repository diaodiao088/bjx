package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.TimePicker;
import cn.qqtheme.framework.util.ConvertUtils;

public class CheckChangeTimeActivity extends Activity {

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @BindView(R.id.change_time_tv)
    TextView mChangeTimeTv;

    @BindView(R.id.change_reason_tv)
    EditText mChangeReasonTv;

    @BindView(R.id.content_limit)
    TextView mContentLimitTv;

    @OnClick(R.id.change_time_tv)
    void onClickChangeTime() {
        showChangeTimePicker();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_change_time_activity);
        ButterKnife.bind(this);
        initListener();
    }

    public void initListener() {

        mChangeReasonTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int textSum = s.toString().length();

                if (textSum <= 200) {
                    mContentLimitTv.setText(textSum + "/200");
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, CheckChangeTimeActivity.class);
        context.startActivity(intent);
    }

    private void showChangeTimePicker() {
        TimePicker picker = new TimePicker(this, TimePicker.HOUR_24);
        picker.setUseWeight(false);
        picker.setCycleDisable(false);
        picker.setRangeStart(0, 0);//00:00
        picker.setRangeEnd(23, 59);//23:59
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        picker.setSelectedItem(currentHour, currentMinute);
        picker.setTopLineVisible(false);
        picker.setTextPadding(ConvertUtils.toPx(this, 15));
        picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
            @Override
            public void onTimePicked(String hour, String minute) {
                //showToast(hour + ":" + minute);
            }
        });
        picker.show();
    }
}

package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.utils.DateUtils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;

public class RecordAddActivity extends Activity {

    public static final int REQUEST_CODE_RECORD_ADD = 0x01;

    @BindView(R.id.record_device_name)
    TextView mRecordDeviceNameTv;

    @BindView(R.id.record_brand_name_ev)
    EditText mRecordBrandNameTv;

    @BindView(R.id.record_type_tv)
    EditText mRecordTypeTv;

    @BindView(R.id.record_time_tv)
    TextView mRecordTimeTv;

    @BindView(R.id.record_status_tv)
    TextView mRecordStatusTv;

    @OnClick(R.id.record_status_tv)
    void onClickStatus() {
        showStatusPicker();
    }

    @OnClick(R.id.record_time_tv)
    void onClickTime(){
        showTimerPicker();
    }


    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_add_activity);
        ButterKnife.bind(this);
    }

    public static void gotoActivity(Activity context, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, RecordAddActivity.class);
        context.startActivityForResult(intent, requestCode);
    }


    public void showTimerPicker() {

        DatePicker mPicker = new DatePicker(this);
        mPicker.setCanceledOnTouchOutside(true);
        mPicker.setUseWeight(true);
        mPicker.setTopPadding(DimenUtils.dp2px(10, this));
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH);
        int day = ca.get(Calendar.DATE);
        mPicker.setRangeEnd(year, month + 1, day);
        mPicker.setRangeStart(2000, 01, 01);


        int selectYear = Integer.valueOf(DateUtils.getYear(0));
        int selectMonth = Integer.valueOf(DateUtils.getMonth(0));
        int selectDay = Integer.valueOf(DateUtils.getDay(0));
        mPicker.setSelectedItem(selectYear, selectMonth, selectDay);
        mPicker.setResetWhileWheel(false);
        mPicker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {

            }
        });
        mPicker.show();

    }


    /**
     * select service name .
     */
    public void showStatusPicker() {
        OptionPicker picker = new OptionPicker(this,
                new String[]{"在用", "停用"});
        picker.setCycleDisable(true);//不禁用循环
        picker.setTopBackgroundColor(0xFFffffff);
        picker.setTopHeight(30);
        picker.setTopLineColor(0xfffdfdfd);
        picker.setTopLineHeight(3);
        picker.setTitleText("设备状态");
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
                mRecordStatusTv.setText(item);

            }
        });
        picker.show();
    }

    public void confirm() {


    }


}

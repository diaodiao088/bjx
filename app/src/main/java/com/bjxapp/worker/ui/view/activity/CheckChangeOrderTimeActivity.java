package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.picker.TimePicker;
import cn.qqtheme.framework.util.ConvertUtils;
import cn.qqtheme.framework.widget.WheelView;

public class CheckChangeOrderTimeActivity extends Activity {

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

    @BindView(R.id.change_year_tv)
    TextView mChangeYearTv;

    private XWaitingDialog mWaitingDialog;

    @OnClick(R.id.change_time_tv)
    void onClickChangeTime() {
        showStatusPicker();
    }

    @OnClick(R.id.change_year_tv)
    void onClickYear() {
        showTimerPicker();
    }

    @OnClick(R.id.add_confirm_btn)
    void onConfirm() {
        startCommit();
    }

    public static final String TYPE_DAY = "type_day";
    public static final String TYPE_HOUR = "type_hour";
    public static final String TYPE_ID = "type_id";

    private String mDay;
    private String mHour;
    private String mId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_change_time_activity);
        ButterKnife.bind(this);
        initListener();
        handleIntent();
        initView();
    }

    private void initView() {
        mTitleTextView.setText("修改时间");
        mChangeTimeTv.setText(mHour);
        mChangeYearTv.setText(mDay);
        mWaitingDialog = new XWaitingDialog(this);
    }

    private void handleIntent() {

        Intent intent = getIntent();

        if (intent != null) {
            mDay = intent.getStringExtra(TYPE_DAY);
            mHour = intent.getStringExtra(TYPE_HOUR);
            mId = intent.getStringExtra(TYPE_ID);
        }

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

    public static void startActivity(Context context, String day, String hour, String id) {
        Intent intent = new Intent();
        intent.setClass(context, CheckChangeOrderTimeActivity.class);
        intent.putExtra(TYPE_DAY, day);
        intent.putExtra(TYPE_HOUR, hour);
        intent.putExtra(TYPE_ID, id);
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
                mChangeTimeTv.setText(hour + ":" + minute);
            }
        });
        picker.show();
    }

    public void showTimerPicker() {

        DatePicker mPicker = new DatePicker(this);
        mPicker.setCanceledOnTouchOutside(true);
        mPicker.setUseWeight(true);
        mPicker.setTopPadding(DimenUtils.dp2px(10, this));
        Calendar ca1 = Calendar.getInstance();
        ca1.add(Calendar.DATE, 10);
        int year1 = ca1.get(Calendar.YEAR);
        int month1 = ca1.get(Calendar.MONTH) + 1;
        int day1 = ca1.get(Calendar.DATE);
        mPicker.setRangeEnd(year1, month1, day1);


        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH) + 1;
        int day = ca.get(Calendar.DATE);
        mPicker.setRangeStart(year, month, day);


        int selectYear = Integer.valueOf(DateUtils.getYear(0));
        int selectMonth = Integer.valueOf(DateUtils.getMonth(0));
        int selectDay = Integer.valueOf(DateUtils.getDay(0));
        mPicker.setSelectedItem(selectYear, selectMonth, selectDay);
        mPicker.setResetWhileWheel(false);
        mPicker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                mChangeYearTv.setText(year + "-" + month + "-" + day);
            }
        });
        mPicker.show();
    }

    public void showStatusPicker() {
        OptionPicker picker = new OptionPicker(this,
                new String[]{"00:00:00-01:00:00", "01:00:00-02:00:00",
                        "02:00:00-03:00:00", "03:00:00-04:00:00",
                        "04:00:00-05:00:00", "05:00:00-06:00:00",
                        "06:00:00-07:00:00", "07:00:00-08:00:00",
                        "08:00:00-09:00:00", "09:00:00-10:00:00",
                        "11:00:00-12:00:00", "12:00:00-13:00:00",
                        "13:00:00-14:00:00", "14:00:00-15:00:00",
                        "15:00:00-16:00:00", "16:00:00-17:00:00",
                        "17:00:00-18:00:00", "18:00:00-19:00:00",
                        "19:00:00-20:00:00", "20:00:00-21:00:00",
                        "21:00:00-22:00:00", "22:00:00-23:00:00",
                        "23:00:00-24:00:00"});
        picker.setCycleDisable(true);//不禁用循环
        picker.setTopBackgroundColor(0xFFffffff);
        picker.setTopHeight(30);
        picker.setTopLineColor(0xfffdfdfd);
        picker.setTopLineHeight(3);
        picker.setTitleText("时间选择");
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
                mChangeTimeTv.setText(item);
                mHour = item;
            }
        });
        picker.show();
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());


    private void startCommit() {

        if (TextUtils.isEmpty(mChangeTimeTv.getText().toString())) {
            Toast.makeText(this, "请输入时间", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mChangeYearTv.getText().toString())) {
            Toast.makeText(this, "请输入时间", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mChangeReasonTv.getText().toString())) {
            Toast.makeText(this, "请输入修改原因", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isTimeValid()) {
            Toast.makeText(this, "要修改的时间必须是未来时间", Toast.LENGTH_SHORT).show();
            return;
        }


        Intent intent = new Intent();
        intent.putExtra(TYPE_DAY, mChangeYearTv.getText().toString());
        intent.putExtra(TYPE_HOUR, mChangeTimeTv.getText().toString());
        intent.putExtra("reason", mChangeReasonTv.getText().toString());
        setResult(RESULT_OK, intent);

        finish();
    }


    private boolean isTimeValid() {

        try {
            String date = mChangeYearTv.getText().toString() + " " + mChangeTimeTv.getText().toString().split("-")[0];
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//24小时制

            long time2 = simpleDateFormat.parse(date).getTime();

            return time2 > System.currentTimeMillis();
        } catch (Exception e) {
            return true;
        }

    }

    public static void goToActivityForResult(Activity context, String day, String hour) {
        Intent intent = new Intent();
        intent.setClass(context, CheckChangeOrderTimeActivity.class);
        intent.putExtra(TYPE_DAY, day);
        intent.putExtra(TYPE_HOUR, hour);
        context.startActivityForResult(intent, 0x07);
    }


}

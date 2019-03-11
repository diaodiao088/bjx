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
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.RecordApi;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.utils.DateUtils;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.TimePicker;
import cn.qqtheme.framework.util.ConvertUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @BindView(R.id.change_year_tv)
    TextView mChangeYearTv;

    private XWaitingDialog mWaitingDialog;

    @OnClick(R.id.change_time_tv)
    void onClickChangeTime() {
        showChangeTimePicker();
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
        mChangeTimeTv.setText(mHour.substring(11, 16));
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
        intent.setClass(context, CheckChangeTimeActivity.class);
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
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH);
        int day = ca.get(Calendar.DATE);
        mPicker.setRangeEnd(year + 1, month + 1, day);
        mPicker.setRangeStart(2000, 01, 01);


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

        if (mWaitingDialog != null) {
            mWaitingDialog.show("正在修改.", false);
        }

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("id", mId);
        params.put("reason", mChangeReasonTv.getText().toString());
        params.put("actualTime", mChangeYearTv.getText().toString() + " " + mChangeTimeTv.getText().toString() + ":00");

        Call<JsonObject> call = recordApi.updateOrder(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (mWaitingDialog != null) {
                    mWaitingDialog.dismiss();
                }

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    final JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {
                        Utils.showShortToast(CheckChangeTimeActivity.this, "修改成功");
                        finish();
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(CheckChangeTimeActivity.this, msg + ":" + code);
                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mWaitingDialog != null) {
                            mWaitingDialog.dismiss();
                        }
                        Toast.makeText(CheckChangeTimeActivity.this, "修改失败..", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }


}

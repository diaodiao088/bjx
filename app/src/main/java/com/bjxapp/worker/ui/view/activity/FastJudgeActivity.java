package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.RecordApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.ServiceItemLayout;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FastJudgeActivity extends Activity {

    public static final String IS_FINISHED = "is_finished";

    private boolean isFinished;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private ArrayList<DeviceInfoActivity.ServiceItem> mList = new ArrayList<>();

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @BindView(R.id.service_process_ly)
    LinearLayout mServiceLy;

    @OnClick(R.id.title_image_back)
    void onClickBack() {
        onBackPressed();
    }

    @BindView(R.id.judge_des_tv)
    TextView mJudgeTv;

    @OnClick(R.id.add_confirm_btn)
    void onClickConfirm() {

    }

    @BindView(R.id.process_status_tv)
    TextView mProcessStatusTv;

    @BindView(R.id.add_confirm_btn)
    XButton mConfirmBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.judge_activity);
        ButterKnife.bind(this);
        handleIntent();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void handleIntent() {
        isFinished = getIntent().getBooleanExtra(IS_FINISHED, false);
    }

    private void initView() {

        if (isFinished) {
            mJudgeTv.setText("设备维修已完成，请选择您的处理意见～");
            mConfirmBtn.setVisibility(View.VISIBLE);
        } else {
            mJudgeTv.setText("设备维续评价已完成～");
            mConfirmBtn.setVisibility(View.GONE);
        }

    }

    private void initData() {

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("id", "");

        Call<JsonObject> call = recordApi.getOrderEquip(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    final JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {

                        parseData(object.get("equipment").getAsJsonObject());

                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(FastJudgeActivity.this, msg + ":" + code);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void parseData(JsonObject object) {

        JsonArray serviceArray = object.get("serviceProcessList").getAsJsonArray();

        for (int i = 0; i < serviceArray.size(); i++) {
            JsonObject item = serviceArray.get(i).getAsJsonObject();
            DeviceInfoActivity.ServiceItem serviceItem = new DeviceInfoActivity.ServiceItem();
            if (item.get("actualScore") != null && !(item.get("actualScore") instanceof JsonNull)) {
                serviceItem.setActualScore(item.get("actualScore").getAsString());
            }

            serviceItem.setId(item.get("id").getAsString());
            serviceItem.setMaxScore(item.get("maxScore").getAsInt());
            serviceItem.setProcessName(item.get("processName").getAsString());
            mList.add(serviceItem);
        }

        updateUi();
    }

    private void updateUi() {

        if (!isAllSelected()) {
            mProcessStatusTv.setText("选择");
        } else if (isAllMaxScore()) {
            mProcessStatusTv.setText("正常");
        } else {
            mProcessStatusTv.setText("有异常");
        }

        if (mList.size() > 0) {
            mServiceLy.setVisibility(View.VISIBLE);
            mServiceLy.removeAllViews();
            updateServiceLy();
        } else {
            mServiceLy.setVisibility(View.GONE);
        }
    }

    private void updateServiceLy() {
        for (int i = 0; i < mList.size(); i++) {
            DeviceInfoActivity.ServiceItem serviceItem = mList.get(i);
            ServiceItemLayout serviceItemLayout = new ServiceItemLayout(this);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    DimenUtils.dp2px(50, this));

            serviceItemLayout.bindData(i, serviceItem, !isFinished, isAllMaxScore());

            mServiceLy.addView(serviceItemLayout, layoutParams);
        }
    }

    private boolean isAllSelected() {
        for (int i = 0; i < mList.size(); i++) {
            if (TextUtils.isEmpty(mList.get(i).getActualScore())) {
                return false;
            }

        }
        return true;
    }

    private boolean isAllMaxScore() {
        for (int i = 0; i < mList.size(); i++) {
            if (TextUtils.isEmpty(mList.get(i).getActualScore())) {
                return false;
            }

            if (Integer.parseInt(mList.get(i).getActualScore()) != mList.get(i).getMaxScore()) {
                return false;
            }

        }
        return true;
    }

    public static void goToActivity(Context context, boolean isFinished) {

        Intent intent = new Intent();

        intent.setClass(context, FastJudgeActivity.class);
        intent.putExtra(IS_FINISHED, isFinished);

        context.startActivity(intent);
    }
}

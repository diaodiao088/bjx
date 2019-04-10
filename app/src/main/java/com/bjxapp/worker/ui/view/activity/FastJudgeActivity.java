package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.RecordApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.ui.view.activity.widget.dialog.DeviceConfirmDialog;
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
    public static final String ORDER_ID = "order_id";
    public static final String EQUIP_ID = "equip_id";
    public static final String IMG_LIST = "img_list";
    public static final String ORDER_ID_REAL = "order_id_real";

    private boolean isFinished;
    private Integer situation;

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

    @BindView(R.id.device_group)
    RadioGroup mDeviceRadioGroup;

    @BindView(R.id.judge_des_tv)
    TextView mJudgeTv;

    @OnClick(R.id.add_confirm_btn)
    void onClickConfirm() {
        startCommit();
    }

    @BindView(R.id.content_limit)
    TextView mLimitTv;

    @BindView(R.id.change_reason_tv)
    EditText mReasonTv;

    @BindView(R.id.process_sit_ly)
    LinearLayout mProcessSitLy;

    @BindView(R.id.process_divider)
    View dividerView;

    @BindView(R.id.process_status_tv)
    TextView mProcessStatusTv;

    @BindView(R.id.add_confirm_btn)
    XButton mConfirmBtn;

    @BindView(R.id.process_total_Ly)
    LinearLayout processTotalLy;

    @BindView(R.id.situation_tv)
    TextView situationTv;

    @OnClick(R.id.process_status_tv)
    void onStatusClick() {
        showStatusDialog();
    }

    ArrayList<String> imgList = new ArrayList<>();

    private XWaitingDialog mWaitingDialog;

    private String mOrderId;
    private String mOrderIdReal;
    private String mEquipId;
    private String remark;
    private String id;
    private String realId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.judge_activity);
        ButterKnife.bind(this);
        handleIntent();
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void handleIntent() {
        isFinished = getIntent().getBooleanExtra(IS_FINISHED, false);
        mOrderId = getIntent().getStringExtra(ORDER_ID);
        mEquipId = getIntent().getStringExtra(EQUIP_ID);
        imgList = getIntent().getStringArrayListExtra(IMG_LIST);
        mOrderIdReal = getIntent().getStringExtra(ORDER_ID_REAL);
    }

    private void initView() {

        if (!isFinished) {
            mJudgeTv.setText("设备维修已完成，请选择您的处理意见～");
            mConfirmBtn.setVisibility(View.VISIBLE);
        } else {
            mJudgeTv.setText("设备维修评价已完成～");
            mConfirmBtn.setVisibility(View.GONE);
        }

        mTitleTextView.setText("设备评价");

        situationTv.setVisibility(View.GONE);

        if (isFinished) {

            mDeviceRadioGroup.setFocusable(false);
            mDeviceRadioGroup.setFocusableInTouchMode(false);

            mReasonTv.setFocusable(false);
            mReasonTv.setFocusableInTouchMode(false);

            mProcessStatusTv.setEnabled(false);
            mProcessStatusTv.setClickable(false);

            situationTv.setVisibility(View.VISIBLE);
            mDeviceRadioGroup.setVisibility(View.GONE);
        }

        mWaitingDialog = new XWaitingDialog(this);

        mReasonTv.addTextChangedListener(new TextWatcher() {
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
                    mLimitTv.setText(textSum + "/200");
                }
            }
        });

        mDeviceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.must:
                        situation = 6;
                        break;

                    case R.id.recommend:
                        situation = 3;
                        break;
                }
            }
        });

    }

    private void initData() {

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("orderId", mOrderId);
        params.put("equipmentId", mEquipId);

        Call<JsonObject> call = recordApi.getOrderEquipV2(params);

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

        if (isFinished) {

            if (object.get("remark") != null && !(object.get("remark") instanceof JsonNull)) {
                remark = object.get("remark").getAsString();
            }
            if (object.get("situation") != null && !(object.get("situation") instanceof JsonNull)) {
                situation = object.get("situation").getAsInt();
            }
        }


        id = object.get("equipmentId").getAsString();
        realId = object.get("id").getAsString();

        JsonArray serviceArray = object.get("serviceProcessList").getAsJsonArray();

        for (int i = 0; i < serviceArray.size(); i++) {
            JsonObject item = serviceArray.get(i).getAsJsonObject();
            DeviceInfoActivity.ServiceItem serviceItem = new DeviceInfoActivity.ServiceItem();
            if (item.get("actualScore") != null && !(item.get("actualScore") instanceof JsonNull) && isFinished) {
                serviceItem.setActualScore(item.get("actualScore").getAsString());
            }

            serviceItem.setId(item.get("id").getAsString());
            serviceItem.setMaxScore(item.get("maxScore").getAsInt());
            serviceItem.setProcessName(item.get("processName").getAsString());
            mList.add(serviceItem);
        }

        updateUi();
    }

    private void showStatusDialog() {

        final DeviceConfirmDialog deviceConfirmDialog = new DeviceConfirmDialog(this);

        deviceConfirmDialog.setNormalListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAllAsNormal();
                hideSituation();
                deviceConfirmDialog.dismiss();
            }
        }).setUnNormalListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSituation();
                updateAllAsUnNormal();
                deviceConfirmDialog.dismiss();

            }
        }).setCancelBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deviceConfirmDialog.dismiss();

            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                deviceConfirmDialog.dismiss();

            }
        }).show();

    }

    private void updateUi() {

        if (!TextUtils.isEmpty(remark)) {
            mReasonTv.setText(remark);
        }

        if (situation != null) {
            if (situation == 0) {
                hideSituation();
            } else if (situation == 3) {
                showSituation();
                mDeviceRadioGroup.check(R.id.recommend);
            } else {
                showSituation();
                mDeviceRadioGroup.check(R.id.must);
            }
        }

        if (!isAllSelected()) {
            mProcessStatusTv.setText("选择");
            hideSituation();
        } else if (isAllMaxScore()) {
            mProcessStatusTv.setText("正常");
            hideSituation();
        } else {
            mProcessStatusTv.setText("有异常");
            showSituation();
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

    private void startCommit() {

        if (!isAllChecked()) {
            Toast.makeText(this, "请先进行选择评分", Toast.LENGTH_SHORT).show();
            return;
        }

        if (situation == null) {
            Toast.makeText(this, "请先选择设备状态", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isAllMaxScore() && situation == 0) {
            Toast.makeText(this, "请先选择设备状态", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isExpectChecked()) {
            Toast.makeText(this, "请选择异常原因", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("id", realId);
        params.put("maintainOrderId", mOrderIdReal);
        params.put("situation", String.valueOf((int) situation));

        if (!TextUtils.isEmpty(mReasonTv.getText().toString())) {
            params.put("remark", mReasonTv.getText().toString());
        }

        putPartial(params);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < imgList.size(); i++) {
            builder.append(imgList.get(i));
            if (i != imgList.size() - 1) {
                builder.append(",");
            }
        }

        if (imgList.size() > 0) {
            params.put("imgUrls", builder.toString());
        }

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Call<JsonObject> call = recordApi.updateOrderAgain(params);

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
                        Utils.showShortToast(FastJudgeActivity.this, "提交成功");
                        finish();
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
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mWaitingDialog != null) {
                            mWaitingDialog.dismiss();
                        }
                        Toast.makeText(FastJudgeActivity.this, "提交失败..", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    private void putPartial(Map<String, String> params) {

        if (mList == null || mList.size() <= 0) {
            return;
        }

        for (int i = 0; i < mList.size(); i++) {

            String namekey = "serviceProcessList[" + i + "].id";
            String urlkey = "serviceProcessList[" + i + "].actualScore";

            String nameValue = mList.get(i).getId();

            params.put(namekey, nameValue);

            String score = mList.get(i).getActualScore();

            params.put(urlkey, score);

        }
    }


    private boolean isExpectChecked() {

        if ("有异常".equals(mProcessStatusTv.getText().toString())) {
            if (isAllMaxScore()) {
                return false;
            }
        }

        return true;
    }

    private void hideSituation() {
        situation = 0;
        mDeviceRadioGroup.setVisibility(View.GONE);
        mProcessSitLy.setVisibility(View.GONE);
        dividerView.setVisibility(View.GONE);
        processTotalLy.setVisibility(View.GONE);
    }


    private void showSituation() {

        processTotalLy.setVisibility(View.VISIBLE);
        dividerView.setVisibility(View.VISIBLE);
        mProcessSitLy.setVisibility(View.VISIBLE);

        if (isFinished) {
            mDeviceRadioGroup.setVisibility(View.GONE);
            situationTv.setVisibility(View.VISIBLE);
            if (situation == 3) {
                situationTv.setText("设备带病运行，建议报修");
            } else {
                situationTv.setText("设备情况故障，必须报修");
            }
        } else {
            mDeviceRadioGroup.setVisibility(View.VISIBLE);
            situationTv.setVisibility(View.GONE);
        }
    }

    private boolean isAllChecked() {
        for (int i = 0; i < mList.size(); i++) {
            if (TextUtils.isEmpty(mList.get(i).getActualScore())) {
                return false;
            }
        }
        return true;
    }

    public void updateAllAsNormal() {
        mProcessStatusTv.setText("正常");

        for (int i = 0; i < mServiceLy.getChildCount(); i++) {

            View view = mServiceLy.getChildAt(i);

            if (view instanceof ServiceItemLayout) {
                ((ServiceItemLayout) view).showAsNormal();
            }
        }
    }

    public void updateAllAsUnNormal() {

        mProcessStatusTv.setText("有异常");

        for (int i = 0; i < mServiceLy.getChildCount(); i++) {

            View view = mServiceLy.getChildAt(i);

            if (view instanceof ServiceItemLayout) {
                ((ServiceItemLayout) view).showAsSerNormal();
            }
        }
    }


    public static void goToActivity(Context context, boolean isFinished, String orderId, String equipId, ArrayList<String> imgList, String orderIdReal) {

        Intent intent = new Intent();

        intent.setClass(context, FastJudgeActivity.class);
        intent.putExtra(IS_FINISHED, isFinished);
        intent.putExtra(ORDER_ID, orderId);
        intent.putExtra(EQUIP_ID, equipId);
        intent.putExtra(IMG_LIST, imgList);
        intent.putExtra(ORDER_ID_REAL, orderIdReal);

        context.startActivity(intent);
    }
}

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
import com.bjxapp.worker.ui.view.activity.bean.RecordItemBean;
import com.bjxapp.worker.ui.view.activity.order.AddImageActivity;
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

public class DeviceInfoActivity extends Activity {

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @OnClick(R.id.device_info_tv)
    void onClickInfo() {

        RecordItemBean recordItemBean = new RecordItemBean();
        recordItemBean.setId(id);
        recordItemBean.setRecordStatus(2);

        RecordAddActivity.goToActivity(this, recordItemBean, "");
    }

    @BindView(R.id.add_confirm_btn)
    XButton mBtn;

    @OnClick(R.id.add_confirm_btn)
    void onConfirm() {
        startCommit();
    }

    @OnClick(R.id.add_img_ly)
    void onAddImage() {
        AddImageActivity.goToActivity(this, AddImageActivity.OP_ADD, mImgList, !isNeedMod);
    }


    private String id;

    private String realId;

    private String[] imgUrls;

    private Integer needMaintain;

    private String remark;

    private int status;

    private Integer situation;

    private ArrayList<ServiceItem> mList = new ArrayList<>();

    ArrayList<String> mImgList = new ArrayList<>();

    public static final String TYPE_ID = "type_id";
    public static final String IS_NEED_MOD = "is_need_Mod";

    @BindView(R.id.change_reason_tv)
    EditText mReasonTv;

    @BindView(R.id.content_limit)
    TextView mLimitTv;

    @BindView(R.id.need_maintain)
    RadioGroup mRadioGroup;

    @BindView(R.id.device_group)
    RadioGroup mDeviceRadioGroup;

    @BindView(R.id.service_process_ly)
    LinearLayout mServiceLy;

    @BindView(R.id.process_status_tv)
    TextView mProcessStatusTv;

    @OnClick(R.id.process_status_tv)
    void onStatusClick() {
        showStatusDialog();
    }


    private Handler mHandler = new Handler(Looper.getMainLooper());

    private XWaitingDialog mWaitingDialog;

    private boolean isNeedMod = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_detail_activity);
        ButterKnife.bind(this);
        id = getIntent().getStringExtra(TYPE_ID);
        isNeedMod = getIntent().getBooleanExtra(IS_NEED_MOD, true);
        initView();
        initData();
    }

    private void initView() {

        mTitleTextView.setText("设备详情");

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

        mWaitingDialog = new XWaitingDialog(this);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.yes:
                        needMaintain = 1;
                        break;

                    case R.id.no:
                        needMaintain = 0;
                        break;
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

                    case R.id.normal:
                        situation = 0;
                        break;
                }
            }
        });

        if (!isNeedMod) {
            mBtn.setVisibility(View.GONE);
            mRadioGroup.setFocusable(false);
            mRadioGroup.setFocusableInTouchMode(false);

            mDeviceRadioGroup.setFocusable(false);
            mDeviceRadioGroup.setFocusableInTouchMode(false);

            mReasonTv.setFocusable(false);
            mReasonTv.setFocusableInTouchMode(false);

            mProcessStatusTv.setEnabled(false);
            mProcessStatusTv.setClickable(false);
//            for (int i = 0; i < mRadioGroup.getChildCount(); i++) {
//                mRadioGroup.getChildAt(i).setEnabled(false);
//            }
//
//            for (int i = 0; i < mDeviceRadioGroup.getChildCount(); i++) {
//                mDeviceRadioGroup.getChildAt(i).setEnabled(false);
//            }

        }

    }

    private void initData() {

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("id", id);

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
                                Utils.showShortToast(DeviceInfoActivity.this, msg + ":" + code);
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

        if (object.get("needMaintain") != null && !(object.get("needMaintain") instanceof JsonNull)) {
            needMaintain = object.get("needMaintain").getAsInt();
        }

        if (object.get("remark") != null && !(object.get("remark") instanceof JsonNull)) {
            remark = object.get("remark").getAsString();
        }

        if (object.get("situation") != null && !(object.get("situation") instanceof JsonNull)) {
            situation = object.get("situation").getAsInt();
        }

        id = object.get("equipmentId").getAsString();
        realId = object.get("id").getAsString();

        JsonArray urlArray = object.get("imgUrls").getAsJsonArray();

        if (urlArray != null && urlArray.size() > 0) {
            for (int i = 0; i < urlArray.size(); i++) {
                String itemUrl = urlArray.get(i).getAsString();
                mImgList.add(itemUrl);
            }
        }

        JsonArray serviceArray = object.get("serviceProcessList").getAsJsonArray();

        for (int i = 0; i < serviceArray.size(); i++) {
            JsonObject item = serviceArray.get(i).getAsJsonObject();
            ServiceItem serviceItem = new ServiceItem();
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
        if (!TextUtils.isEmpty(remark)) {
            mReasonTv.setText(remark);
        }

        if (needMaintain != null) {
            if (needMaintain == 1) {
                mRadioGroup.check(R.id.yes);
            } else {
                mRadioGroup.check(R.id.no);
            }
        }

        if (situation != null) {
            if (situation == 0) {
                mDeviceRadioGroup.check(R.id.normal);
            } else if (situation == 3) {
                mDeviceRadioGroup.check(R.id.recommend);
            } else {
                mDeviceRadioGroup.check(R.id.must);
            }
        }


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
            ServiceItem serviceItem = mList.get(i);
            ServiceItemLayout serviceItemLayout = new ServiceItemLayout(this);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    DimenUtils.dp2px(50, this));

            serviceItemLayout.bindData(i, serviceItem, isNeedMod, isAllMaxScore());

            mServiceLy.addView(serviceItemLayout, layoutParams);
        }
    }


    public static void goToActivity(Context context, String deviceId, boolean flag) {
        Intent intent = new Intent();
        intent.setClass(context, DeviceInfoActivity.class);
        intent.putExtra(TYPE_ID, deviceId);
        intent.putExtra(IS_NEED_MOD, flag);

        context.startActivity(intent);
    }

    public class ServiceItem {

        String actualScore;

        String id;

        int maxScore;

        String processName;

        public String getActualScore() {
            return actualScore;
        }

        public void setActualScore(String actualScore) {
            this.actualScore = actualScore;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getMaxScore() {
            return maxScore;
        }

        public void setMaxScore(int maxScore) {
            this.maxScore = maxScore;
        }

        public String getProcessName() {
            return processName;
        }

        public void setProcessName(String processName) {
            this.processName = processName;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AddImageActivity.OP_ADD:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> list = data.getStringArrayListExtra("result");
                    if (list != null) {
                        mImgList.clear();
                        mImgList.addAll(list);
                    }
                }
                break;
        }

    }

    public void startCommit() {

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        if (situation == null) {
            Toast.makeText(this, "请先选择设备状态", Toast.LENGTH_SHORT).show();
            return;
        }

        if (needMaintain == null) {
            Toast.makeText(this, "请选择是否需要维修", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isAllChecked()) {
            Toast.makeText(this, "请先进行选择评分", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isExpectChecked()) {
            Toast.makeText(this, "请选择异常原因", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mImgList.size() <= 0) {
            Toast.makeText(this, "请添加照片", Toast.LENGTH_SHORT).show();
            return;
        }


        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("id", realId);
        params.put("situation", String.valueOf((int) situation));
        params.put("needMaintain", String.valueOf(needMaintain));

        if (!TextUtils.isEmpty(mReasonTv.getText().toString())) {
            params.put("remark", mReasonTv.getText().toString());
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mImgList.size(); i++) {
            if (i < mImgList.size() - 1) {
                builder.append(mImgList.get(i) + ",");
            } else {
                builder.append(mImgList.get(i));
            }
        }

        params.put("imgUrls", builder.toString());

        putPartial(params);

        Call<JsonObject> call = recordApi.updateEquip(params);


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
                        Utils.showShortToast(DeviceInfoActivity.this, "提交成功");
                        finish();
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(DeviceInfoActivity.this, msg + ":" + code);
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
                        Toast.makeText(DeviceInfoActivity.this, "提交失败..", Toast.LENGTH_SHORT).show();
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

    private boolean isAllChecked() {
        for (int i = 0; i < mList.size(); i++) {
            if (TextUtils.isEmpty(mList.get(i).getActualScore())) {
                return false;
            }
        }
        return true;
    }

    private boolean isExpectChecked() {

        if ("有异常".equals(mProcessStatusTv.getText().toString())) {
            if (isAllMaxScore()){
                return false;
            }
        }

        return true;

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

    private void showStatusDialog() {

        final DeviceConfirmDialog deviceConfirmDialog = new DeviceConfirmDialog(this);

        deviceConfirmDialog.setNormalListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAllAsNormal();
                deviceConfirmDialog.dismiss();
            }
        }).setUnNormalListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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


}

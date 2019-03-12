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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.RecordApi;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.ui.view.activity.bean.RecordItemBean;
import com.bjxapp.worker.ui.view.activity.order.AddImageActivity;
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

    @OnClick(R.id.add_img_ly)
    void onAddImage() {
        AddImageActivity.goToActivity(this, AddImageActivity.OP_ADD, mImgList, false);
    }

    private String id;

    private String[] imgUrls;

    private Boolean needMaintain;

    private String remark;

    private int status;

    private Integer situation;

    private ArrayList<ServiceItem> mList = new ArrayList<>();

    ArrayList<String> mImgList = new ArrayList<>();

    public static final String TYPE_ID = "type_id";

    @BindView(R.id.change_reason_tv)
    EditText mReasonTv;

    @BindView(R.id.content_limit)
    TextView mLimitTv;

    @BindView(R.id.need_maintain)
    RadioGroup mRadioGroup;


    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_detail_activity);
        ButterKnife.bind(this);
        id = getIntent().getStringExtra(TYPE_ID);
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
            needMaintain = object.get("needMaintain").getAsBoolean();
        }

        if (object.get("remark") != null && !(object.get("remark") instanceof JsonNull)) {
            remark = object.get("remark").getAsString();
        }

        if (object.get("situation") != null && !(object.get("situation") instanceof JsonNull)) {
            situation = object.get("situation").getAsInt();
        }

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

            serviceItem.setId(item.get("id").getAsInt());
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

        if (needMaintain != null){
            if (needMaintain){
                mRadioGroup.check(R.id.yes);
            }else{
                mRadioGroup.check(R.id.no);
            }
        }
    }


    public static void goToActivity(Context context, String deviceId) {
        Intent intent = new Intent();
        intent.setClass(context, DeviceInfoActivity.class);
        intent.putExtra(TYPE_ID, deviceId);

        context.startActivity(intent);
    }

    class ServiceItem {

        String actualScore;

        int id;

        int maxScore;

        String processName;

        public String getActualScore() {
            return actualScore;
        }

        public void setActualScore(String actualScore) {
            this.actualScore = actualScore;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
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

    public void startComit() {

    }
}

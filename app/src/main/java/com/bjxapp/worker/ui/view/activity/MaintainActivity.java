package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.EnterpriseApi;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.MainTainBean;
import com.bjxapp.worker.model.MaintainInfo;
import com.bjxapp.worker.model.ThiInfoBean;
import com.bjxapp.worker.ui.view.activity.widget.dialog.ManfulDialog;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.MaintainItemLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaintainActivity extends Activity {

    private ArrayList<String> mMalfulList = new ArrayList<>();

    private String mSelectedMalfulStr;
    private int mSelectedMalfulIndex;

    @BindView(R.id.title_text_tv)
    TextView mTitleTv;

    @OnClick(R.id.title_image_back)
    void onClickBack() {
        onBackPressed();
    }

    @BindView(R.id.malfun_tv)
    TextView mManfulTv;

    @BindView(R.id.scroll_view)
    ScrollView mScrollView;

    private String equipId;
    private String orderId;

    public ArrayList<MainTainBean> mMainTainList = new ArrayList<>();

    @OnClick(R.id.malfun_tv)
    void onClickManuTv() {

        if (mMalfulList.size() <= 0) {
            return;
        }

        ManfulDialog manfulDialog = new ManfulDialog(this);

        manfulDialog.setData(mMalfulList);

        manfulDialog.setClickListener(new ManfulDialog.OnManClickListener() {
            @Override
            public void onClick(String name, int index) {
                mManfulTv.setText(name);
                mSelectedMalfulIndex = index;
                mSelectedMalfulStr = name;
            }
        });

        manfulDialog.show();
    }

    @OnClick(R.id.add_thi_ly)
    void onAddThiClick() {
        ThiActivity.goToActivityForResult(this, equipId);
    }

    @BindView(R.id.change_reason_tv)
    EditText mMethodTv;

    @BindView(R.id.content_limit)
    TextView mLimitTv;

    @BindView(R.id.main_container_ly)
    LinearLayout mContainerLy;

    @BindView(R.id.total_price_tv)
    TextView mTotalPriceTv;

    @OnClick(R.id.add_confirm_btn)
    void onConfirmClick() {
        startCommit();
    }

    private XWaitingDialog mWaitingDialog;

    private String plan;
    private String fault;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_select);
        ButterKnife.bind(this);
        initView();
        handleIntent();
        initData();
    }

    private void initData() {
        getDicList();
    }

    private void handleIntent() {

        equipId = getIntent().getStringExtra("equip_id");
        orderId = getIntent().getStringExtra("order_id");
        plan = getIntent().getStringExtra("plan");
        fault = getIntent().getStringExtra("fault");
        mMainTainList = getIntent().getParcelableArrayListExtra("maintainList");

        if (mMainTainList == null) {
            mMainTainList = new ArrayList<>();
        }

        refreshUi();
    }

    private void refreshUi() {

        mMethodTv.setText(plan);
        mManfulTv.setText(fault);

        for (int i = 0; i < mMainTainList.size(); i++) {
            addUi(mMainTainList.get(i));
        }

    }

    private void getDicList() {

        EnterpriseApi enterpriseApi = KHttpWorker.ins().createHttpService(LoginApi.URL, EnterpriseApi.class);

        Call<JsonObject> call = null;

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("type", "maintainFaultReason");

        call = enterpriseApi.getDicList(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    final JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {

                        JsonArray array = object.get("list").getAsJsonArray();

                        mMalfulList.clear();

                        for (int i = 0; i < array.size(); i++) {
                            JsonObject item = array.get(i).getAsJsonObject();
                            String value = item.get("value").getAsString();
                            mMalfulList.add(value);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void initView() {
        mWaitingDialog = new XWaitingDialog(this);
        mTitleTv.setText("维修项");

        mMethodTv.addTextChangedListener(new TextWatcher() {
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ThiActivity.REQUEST_CODE:

                    Intent intent = data;
                    boolean isOthers = intent.getBooleanExtra("type_other", false);
                    MainTainBean mainTainBean = new MainTainBean();

                    if (!isOthers) {
                        ThiInfoBean thiInfoBean = intent.getParcelableExtra("bean");
                        if (thiInfoBean != null) {
                            mainTainBean.setComponentId(thiInfoBean.getId());
                            mainTainBean.setCost(thiInfoBean.getCost());
                            mainTainBean.setModel(thiInfoBean.getModel());
                            mainTainBean.setCost(thiInfoBean.getCost());
                            mainTainBean.setUnit(thiInfoBean.getUnit());
                            mainTainBean.setComponentName(thiInfoBean.getName());
                            mainTainBean.setOthers(false);
                            mainTainBean.setQuantity(1);
                        }
                        mMainTainList.add(mainTainBean);

                    } else {
                        mainTainBean.setOthers(true);
                        mainTainBean.setQuantity(1);
                        mainTainBean.setModel("其他");
                        mMainTainList.add(mainTainBean);
                    }

                    addUi(mainTainBean);

                    break;
            }
        }
    }


    private void addUi(MainTainBean mainTainBean) {

        final MaintainItemLayout maintainItemLayout = new MaintainItemLayout(this);
        maintainItemLayout.bindData(mainTainBean);

        maintainItemLayout.setOperationListener(new MaintainItemLayout.OnOperationListener() {
            @Override
            public void onDelete(MainTainBean mainTainBean) {
                mMainTainList.remove(mainTainBean);
                calTotalCount();
            }

            @Override
            public void onPriceChange() {
                calTotalCount();
            }

            @Override
            public void onCountChange() {
                calTotalCount();
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, DimenUtils.dp2px(10, this), 0, 0);
        mContainerLy.addView(maintainItemLayout, params);
        mScrollView.fullScroll(View.FOCUS_DOWN);

        calTotalCount();
    }


    private void calTotalCount() {

        mTotalPriceTv.setText("总报价：" + getTotalPrice() + "元");

    }

    private String getTotalPrice() {

        double price = 0l;

        for (int i = 0; i < mMainTainList.size(); i++) {

            MainTainBean item = mMainTainList.get(i);

            if (!TextUtils.isEmpty(item.getCost())) {
                price += (item.getQuantity() * Double.parseDouble(item.getCost()));
            }

        }

        return getFormatPrice(price);
    }


    private String getFormatPrice(double price) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(price);
    }


    public void startCommit() {

        if (TextUtils.isEmpty(mManfulTv.getText().toString())) {
            Toast.makeText(this, "请先选择故障原因", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mMethodTv.getText().toString())) {
            Toast.makeText(this, "请先选择故障原因", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isDataValid()) {
            Toast.makeText(this, "请填写完整数据", Toast.LENGTH_SHORT).show();
            return;
        }

        mWaitingDialog.show("正在提交", false);

        EnterpriseApi enterpriseApi = KHttpWorker.ins().createHttpService(LoginApi.URL, EnterpriseApi.class);

        Call<JsonObject> call = null;

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("orderId", orderId);
        params.put("fault", mManfulTv.getText().toString());
        params.put("plan", mMethodTv.getText().toString());
        params.put("totalCost", getTotalPrice());

        putPartialList(params);

        call = enterpriseApi.saveMainTain(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                mWaitingDialog.dismiss();

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    final JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {

                        MaintainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MaintainActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                    } else {
                        MaintainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MaintainActivity.this, msg + ":" + code, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                MaintainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWaitingDialog.dismiss();
                        Toast.makeText(MaintainActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private boolean isDataValid() {

        for (int i = 0; i < mMainTainList.size(); i++) {
            MainTainBean item = mMainTainList.get(i);

            if (TextUtils.isEmpty(item.getComponentName())
                    || TextUtils.isEmpty(item.getCost())) {
                return false;
            }
        }

        return true;

    }

    private void putPartialList(Map<String, String> params) {

        for (int i = 0; i < mMainTainList.size(); i++) {

            MainTainBean item = mMainTainList.get(i);

            String nameKey = "equipmentComponentList[" + i + "].name";
            String costKey = "equipmentComponentList[" + i + "].cost";
            String quantityKey = "equipmentComponentList[" + i + "].quantity";

            String modelKey = "equipmentComponentList[" + i + "].model";
            params.put(modelKey, String.valueOf(item.getModel()));

            params.put(nameKey, item.getComponentName());
            params.put(costKey, item.getCost());
            params.put(quantityKey, String.valueOf(item.getQuantity()));

            if (!item.isOthers) {
                String idKey = "equipmentComponentList[" + i + "].id";
                params.put(idKey, String.valueOf(item.getComponentId()));
                String unitKey = "equipmentComponentList[" + i + "].unit";
                params.put(unitKey, item.getUnit());
            }
        }

    }


    public static void goToActivity(Activity context, String equipId, String orderId, MaintainInfo maintainInfo) {

        Intent intent = new Intent();
        intent.setClass(context, MaintainActivity.class);
        intent.putExtra("equip_id", equipId);
        intent.putExtra("order_id", orderId);

        intent.putExtra("plan", maintainInfo.getPlan());
        intent.putExtra("fault", maintainInfo.getFault());
        intent.putParcelableArrayListExtra("maintainList", maintainInfo.getmMaintainList());

        context.startActivityForResult(intent, 0x05);
    }


}

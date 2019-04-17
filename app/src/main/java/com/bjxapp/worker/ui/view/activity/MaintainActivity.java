package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.EnterpriseApi;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.MainTainBean;
import com.bjxapp.worker.model.ThiInfoBean;
import com.bjxapp.worker.ui.view.activity.widget.dialog.ManfulDialog;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.MaintainItemLayout;
import com.google.gson.JsonArray;
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
        ThiActivity.goToActivityForResult(this ,equipId);
    }

    @BindView(R.id.change_reason_tv)
    EditText mMethodTv;

    @BindView(R.id.content_limit)
    TextView mLimitTv;

    @BindView(R.id.main_container_ly)
    LinearLayout mContainerLy;

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

                    refreshUi(mainTainBean);

                    break;
            }
        }
    }


    private void refreshUi(MainTainBean mainTainBean) {

        MaintainItemLayout maintainItemLayout = new MaintainItemLayout(this);

        maintainItemLayout.bindData(mainTainBean);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, DimenUtils.dp2px(10, this), 0, 0);

        mContainerLy.addView(maintainItemLayout, params);

        mScrollView.fullScroll(View.FOCUS_DOWN);
    }


    public void startCommit() {

    }


    public static void goToActivity(Activity context, String equipId) {

        Intent intent = new Intent();
        intent.setClass(context, MaintainActivity.class);
        intent.putExtra("equip_id", equipId);
        context.startActivityForResult(intent, 0x05);
    }


}

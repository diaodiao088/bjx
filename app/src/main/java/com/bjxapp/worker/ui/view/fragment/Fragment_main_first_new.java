package com.bjxapp.worker.ui.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.RecordApi;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.ui.view.activity.CheckMainActivity;
import com.bjxapp.worker.ui.view.activity.RecordActivity;
import com.bjxapp.worker.ui.view.activity.RepairActivity;
import com.bjxapp.worker.ui.view.base.BaseFragment;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_main_first_new extends BaseFragment implements View.OnClickListener {

    protected static final String TAG = "首页";

    private CardView mRepairView, mCheckView, mRecordView, mMaintainView;

    private TextView mMainRedotTv, mCheckRedotTv, mBaoYangRedotTv;

    @Override
    protected void initView() {
        initViews();
        startMainRedot();
        startCheckRedot();
        startBaoYangeRedot();
    }

    private void startMainRedot() {

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(getActivity()).getUserSession());
        params.put("userCode", ConfigManager.getInstance(getActivity()).getUserCode());

        Call<JsonObject> call = recordApi.getRepairRedotCount(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {

                    final JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {
                        int count = object.get("quantity").getAsInt();
                        if (count > 0) {
                            mMainRedotTv.setVisibility(View.VISIBLE);
                            mMainRedotTv.setText(String.valueOf(count));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }


    private void startCheckRedot() {

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(getActivity()).getUserSession());
        params.put("userCode", ConfigManager.getInstance(getActivity()).getUserCode());
        params.put("serviceType", String.valueOf(0));

        Call<JsonObject> call = recordApi.getEnterRedotCount(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {

                    final JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {
                        int count = object.get("quantity").getAsInt();
                        if (count > 0) {
                            mCheckRedotTv.setVisibility(View.VISIBLE);
                            mCheckRedotTv.setText(String.valueOf(count));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    private void startBaoYangeRedot() {
        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(getActivity()).getUserSession());
        params.put("userCode", ConfigManager.getInstance(getActivity()).getUserCode());
        params.put("serviceType", String.valueOf(1));

        Call<JsonObject> call = recordApi.getEnterRedotCount(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {

                    final JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {
                        int count = object.get("quantity").getAsInt();
                        if (count > 0) {
                            mBaoYangRedotTv.setVisibility(View.VISIBLE);
                            mBaoYangRedotTv.setText(String.valueOf(count));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    @Override
    protected void finish() {

    }

    @Override
    protected int onCreateContent() {
        return R.layout.fragment_main_first_new;
    }

    @Override
    public void refresh(int enterType) {

    }

    private void initViews() {

        mRepairView = (CardView) findViewById(R.id.service_entrance_ly);
        mCheckView = (CardView) findViewById(R.id.check_entrance_ly);
        mRecordView = (CardView) findViewById(R.id.record_entrance_ly);
        mMaintainView = (CardView) findViewById(R.id.maintain_entrance_ly);

        mMainRedotTv = (TextView) findViewById(R.id.redot_weixiu);
        mCheckRedotTv = (TextView) findViewById(R.id.redot_check);
        mBaoYangRedotTv = (TextView) findViewById(R.id.redot_baoyang);

        mRepairView.setOnClickListener(this);
        mCheckView.setOnClickListener(this);
        mRecordView.setOnClickListener(this);
        mMaintainView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.check_entrance_ly:
                CheckMainActivity.goToActivity(getActivity(), CheckMainActivity.TYPE_CHECK);
                break;
            case R.id.maintain_entrance_ly:
                CheckMainActivity.goToActivity(getActivity(), CheckMainActivity.TYPE_MAIN);
                break;

            case R.id.service_entrance_ly:
                RepairActivity.gotoActivity(getActivity());
                break;
            case R.id.record_entrance_ly:
                RecordActivity.gotoActivity(getActivity());
                break;

            default:
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {
                case Constant.ACTIVITY_ORDER_DETAIL_RESULT_CODE:
                    if (resultCode == Activity.RESULT_OK) {
                        // onFirstLoadData(true);
                    }
                    break;
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected String getPageName() {
        return TAG;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }


}

package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.RecordApi;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.ShopInfoBean;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordActivity extends Activity {

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @BindView(R.id.record_num_tv)
    EditText mRecordTv;

    private Handler mHandler = new Handler(Looper.getMainLooper());


    @OnClick(R.id.add_confirm_btn)
    void onClickConfirm() {

        if (TextUtils.isEmpty(mRecordTv.getText().toString())) {
            Toast.makeText(this, "请填写编号", Toast.LENGTH_SHORT).show();
            return;
        }

        requestRecordInfo();
    }

    private void requestRecordInfo() {

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());

        Call<JsonObject> call = recordApi.getRecordInfo(mRecordTv.getText().toString(), params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {

                    JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {

                        JsonObject shopInfo = object.get("shop").getAsJsonObject();

                        String detailAddress = shopInfo.get("detailAddress").getAsString();
                        String enterpriseId = shopInfo.get("enterpriseId").getAsString();
                        String enterpriseName = shopInfo.get("enterpriseName").getAsString();
                        String id = shopInfo.get("id").getAsString();
                        String latitude = shopInfo.get("latitude").getAsString();
                        String longitude = shopInfo.get("longitude").getAsString();
                        String name = shopInfo.get("name").getAsString();
                        String shopNum = shopInfo.get("shopNo").getAsString();
                        String locationAddress = shopInfo.get("locationAddress").getAsString();

                        ShopInfoBean shopInfoBean = new ShopInfoBean(detailAddress, enterpriseId, enterpriseName,
                                id, latitude, longitude, name, shopNum, locationAddress);

                        RecordDetailActivity.gotoActivity(RecordActivity.this, shopInfoBean);

                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(RecordActivity.this, msg + ":" + code);
                            }
                        });
                    }

                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RecordActivity.this, "编号错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RecordActivity.this, "编号错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTitleTextView.setText("设备录入");
    }

    public static void gotoActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, RecordActivity.class);
        context.startActivity(intent);
    }


}

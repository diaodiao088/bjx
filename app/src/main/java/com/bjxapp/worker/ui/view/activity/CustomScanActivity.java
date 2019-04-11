package com.bjxapp.worker.ui.view.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bjx.master.R;
import com.bjxapp.worker.zxing.CaptureActivity;
import com.google.zxing.Result;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomScanActivity extends CaptureActivity {

    public static final String ORDER_ID = "order_id";
    public static final String ORDER_NUM = "order_num";
    public static final String PROCESS_STATE = "process_state";
    public static final String CURRENT_TYPE = "current_type";

    private String orderId;
    private int processState;
    private int currentType;

    @BindView(R.id.scan_close_iv)
    ImageView mCloseIv;

    @BindView(R.id.ivFlash_iv)
    ImageView mLightTv;

    @OnClick(R.id.scan_close_iv)
    void onClickClose() {
        onBackPressed();
    }

    @Override
    public int getLayoutId() {
        return R.layout.custom_capture_activity;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        handleIntent();
        ButterKnife.bind(this);
        findViewById(R.id.ivFlash_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFlash(v);
            }
        });
        getBeepManager().setPlayBeep(true);
        getBeepManager().setVibrate(true);
    }

    private void handleIntent() {
        if (getIntent() != null) {
            orderId = getIntent().getStringExtra(ORDER_ID);
            processState = getIntent().getIntExtra(PROCESS_STATE, 0);
            currentType = getIntent().getIntExtra(CURRENT_TYPE, 0);
        }
    }

    /**
     * 关闭闪光灯（手电筒）
     */
    private void offFlash() {
        Camera camera = getCameraManager().getOpenCamera().getCamera();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameters);
    }

    /**
     * 开启闪光灯（手电筒）
     */
    public void openFlash() {
        Camera camera = getCameraManager().getOpenCamera().getCamera();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(parameters);
    }

    @Override
    public void onResult(Result result) {
//        Intent intent = new Intent();
//        intent.putExtra(KEY_RESULT, result.getText());
//        setResult(RESULT_OK, intent);

        DeviceInfoActivity.goToActivity(this, result.getText(), processState <= 3,
                currentType == 0, false, true, orderId);

        finish();
    }

    private void clickFlash(View v) {
        if (v.isSelected()) {
            offFlash();
            v.setSelected(false);
        } else {
            openFlash();
            v.setSelected(true);
        }
    }


    public static void goToActivity(Context context, String orderId, int processState, int currentType) {
        Intent intent = new Intent();
        intent.putExtra(ORDER_ID, orderId);
        intent.putExtra(PROCESS_STATE, processState);
        intent.putExtra(CURRENT_TYPE, currentType);
        intent.setClass(context, CustomScanActivity.class);
        context.startActivity(intent);
    }


}

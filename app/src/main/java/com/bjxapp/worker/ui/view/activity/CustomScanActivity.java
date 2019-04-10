package com.bjxapp.worker.ui.view.activity;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bjx.master.R;
import com.bjxapp.worker.zxing.CaptureActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomScanActivity extends CaptureActivity {


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

    private void clickFlash(View v) {
        if (v.isSelected()) {
            offFlash();
            v.setSelected(false);
        } else {
            openFlash();
            v.setSelected(true);
        }

    }


}

package com.bjxapp.worker.ui.view.activity;

import android.hardware.Camera;
import android.os.Bundle;
import android.widget.ImageView;

import com.bjx.master.R;
import com.bjxapp.worker.zxing.CaptureActivity;

import butterknife.ButterKnife;

public class CustomScanActivity extends CaptureActivity {



    ImageView mCloseIv;

    ImageView mLightTv;


    @Override
    public int getLayoutId() {
        return R.layout.custom_capture_activity;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        ButterKnife.bind(this);

        getBeepManager().setPlayBeep(true);
        getBeepManager().setVibrate(true);
    }

    /**
     * 关闭闪光灯（手电筒）
     */
    private void offFlash(){
        Camera camera = getCameraManager().getOpenCamera().getCamera();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameters);
    }

    /**
     * 开启闪光灯（手电筒）
     */
    public void openFlash(){
        Camera camera = getCameraManager().getOpenCamera().getCamera();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(parameters);
    }




}

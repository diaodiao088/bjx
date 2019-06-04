package com.bjxapp.worker.utils.mask;

import android.app.Activity;
import android.graphics.Color;

import com.bjx.master.R;
import com.bjxapp.worker.imagelist.imgsel.ISNav;
import com.bjxapp.worker.imagelist.imgsel.config.ISListConfig;

public class ImageSelectUtil {

    public static int REQUEST_LIST_CODE = 0x100;

    public static ISListConfig config = new ISListConfig.Builder()
            // 是否多选, 默认true
            .multiSelect(true)
            // 是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
            .rememberSelected(false)
            // “确定”按钮背景色
            .btnBgColor(Color.parseColor("#00a551"))
            // “确定”按钮文字颜色
            .btnTextColor(Color.parseColor("#ffffff"))
            // 使用沉浸式状态栏
            .statusBarColor(Color.parseColor("#ffffff"))
            // 返回图标ResId
            .backResId(R.drawable.back)
            // 标题
            .title("图片")
            // 标题文字颜色
            .titleColor(Color.parseColor("#545454"))
            // TitleBar背景色
            .titleBgColor(Color.parseColor("#f5f5f5"))
            // 裁剪大小。needCrop为true的时候配置
            .cropSize(1, 1, 200, 200)
            .needCrop(false)
            // 第一个是否显示相机，默认true
            .needCamera(false)
            // 最大选择图片数量，默认9
            .maxNum(9)
            .build();

    public static void goToImageListActivity(Activity act , int size){
        ISNav.getInstance().toListActivity(act, config, REQUEST_LIST_CODE);
    }



}

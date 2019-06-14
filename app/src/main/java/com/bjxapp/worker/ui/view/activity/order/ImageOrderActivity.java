package com.bjxapp.worker.ui.view.activity.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bjx.master.R;;
import com.bjxapp.worker.controls.XTextView;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhangdan on 2018/10/22.
 * comments:
 */

public class ImageOrderActivity extends Activity {

    @BindView(R.id.image_iv)
    PhotoView mIv;

    @BindView(R.id.title_text_tv)
    XTextView mTitleTv;

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    public static final String URL_IMAGE_PATH = "url_image_path";
    public static final String FROM_SERVICE = "from_service";

    private String mImagePath;
    private boolean mFromService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        ButterKnife.bind(this);
        handleIntent();
        initView();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mImagePath = intent.getStringExtra(URL_IMAGE_PATH);
            mFromService = intent.getBooleanExtra(FROM_SERVICE , false);
        }
    }

    private void initView() {

        if (!mFromService){
            mTitleTv.setText("照片详情");
        }else{
            mTitleTv.setText("服务项目列表");
        }



        if (TextUtils.isEmpty(mImagePath)) {
            return;
        }
        mIv.enable();
        Glide.with(this).load(mImagePath).into(mIv);
    }

    public static void goToActivity(Context ctx, String url) {
       goToActivity(ctx , url , false);
    }

    public static void goToActivity(Context ctx, String url , boolean fromService) {
        Intent intent = new Intent();
        intent.setClass(ctx, ImageOrderActivity.class);
        intent.putExtra(URL_IMAGE_PATH, url);
        intent.putExtra(FROM_SERVICE , fromService);
        ctx.startActivity(intent);
    }

}

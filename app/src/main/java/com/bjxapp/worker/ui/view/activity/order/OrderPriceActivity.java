package com.bjxapp.worker.ui.view.activity.order;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bjxapp.worker.App;
import com.bjxapp.worker.R;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.RoundImageView;
import com.bjxapp.worker.utils.UploadFile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhangdan on 2018/10/14.
 * comments:
 */

public class OrderPriceActivity extends Activity implements View.OnClickListener {

    private static final int ADD_IMAGE_RESULT = 0x01;
    private static final int SCREEN_SHOT_MAX_NUMS = 3;

    private XButton mConfirmBtn;

    private EditText mContentTv;
    private EditText mPriceTv;

    private LinearLayout mContainer;
    private ImageView mAddIv;

    private int mScreenShotCount;

    @BindView(R.id.title_text_tv)
    XTextView mTitleTv;

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    private List<String> mScreenShotList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_price);
        ButterKnife.bind(this);
        mTitleTv.setText("预付项");
        mContentTv = findViewById(R.id.enter_pwd_tv);
        mPriceTv = findViewById(R.id.enter_pwd_tv_sure);
        mAddIv = findViewById(R.id.btn_screen_shot);
        mConfirmBtn = findViewById(R.id.get_qr_code_btn);
        mAddIv.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);
        mContainer = findViewById(R.id.image_layout);
    }

    private void loadImages() {
        Uri imageURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        if (imageURI != null) {
            try {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, imageURI);
                    startActivityForResult(intent, ADD_IMAGE_RESULT);
                }
            } catch (Exception e) {
                Log.w("FeedbackPresenter", "loadImages: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_IMAGE_RESULT) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = null;
                try {
                    cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        final String imagePath = cursor.getString(columnIndex);
                        //根据手机屏幕设置图片宽度
                        Bitmap bitmap = UploadFile.createImageThumbnail(imagePath, getScreenShotWidth(), true);
                        if (bitmap != null) {
                            insertImg(bitmap, imagePath, true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }
    }

    private int getScreenShotWidth() {
        int screenWidth = DimenUtils.getScreenWidth(this);
        screenWidth = screenWidth - (int) (screenWidth * 1.0 / 9); // 除掉边框
        return screenWidth / 4;
    }

    public void insertImg(Bitmap bitmap, final String imagePath,
                          boolean showDelImg) {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View screenShotLayout = inflater.inflate(R.layout.add_image, null);
        RoundImageView screenShotImageView = (RoundImageView) screenShotLayout.findViewById(R.id.screenShotImageView);
        View deleteImageView = screenShotLayout.findViewById(R.id.deleteImageView);
        screenShotImageView.setBorderRadius(4);
        screenShotImageView.setMode(RoundImageView.MODE_DEFAULT);
        screenShotImageView.setType(RoundImageView.TYPE_ROUND_ALL);
        screenShotImageView.setImageBitmap(bitmap);

        if (showDelImg) {
            deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContainer.removeView(screenShotLayout);
                    mScreenShotList.remove(imagePath);
                    mScreenShotCount--;
                    if (mScreenShotCount != SCREEN_SHOT_MAX_NUMS) {//添加的个数达到3个
                        mAddIv.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            deleteImageView.setVisibility(View.GONE);
        }
        mContainer.addView(screenShotLayout);
        mContainer.setVisibility(View.VISIBLE);
        mScreenShotList.add(imagePath);
        mScreenShotCount++; //记录添加的图片个数
        if (mScreenShotCount == SCREEN_SHOT_MAX_NUMS) {//添加的个数达到3个
            mAddIv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_screen_shot:
                loadImages();
                break;
            case R.id.get_qr_code_btn:
                getQrCode();
                break;
        }
    }

    private void getQrCode() {

    }

    public static void goToActivity(Context ctx) {

        if (ctx == null) {
            ctx = App.getInstance();
        }

        Intent intent = new Intent();
        intent.setClass(ctx, OrderPriceActivity.class);
        ctx.startActivity(intent);
    }


}

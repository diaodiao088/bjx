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
import android.os.Handler;
import android.os.Looper;
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
import android.widget.TextView;

import com.bjxapp.worker.App;
import com.bjxapp.worker.R;
import com.bjxapp.worker.apinew.BillApi;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.RoundImageView;
import com.bjxapp.worker.utils.LogUtils;
import com.bjxapp.worker.utils.UploadFile;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;

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

    @BindView(R.id.price)
    TextView mPriceTotalTv;

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    private ArrayList<String> mImageUrl = new ArrayList<>();

    private List<String> mScreenShotList = new ArrayList<>();

    private XWaitingDialog mDialog;

    private String orderId = "";

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
        mDialog = new XWaitingDialog(this);
        handleIntent();
    }

    private void handleIntent() {
        orderId = getIntent() != null ? getIntent().getStringExtra("order_id") : "";
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

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private void getQrCode() {

        if (mContentTv.getText().length() == 0 || mPriceTv.getText().length() == 0) {
            Utils.showShortToast(this, "请填写完整信息");
            return;
        }

        mPriceTv.setVisibility(View.VISIBLE);
        mPriceTv.setText(mPriceTv.getText().toString());

        mDialog.show("正在生成二维码，请稍后..", false);

        // step 1 : 上传图片
        if (mScreenShotList == null || mScreenShotList.size() <= 0) {
            toSecondStep();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);

        for (int i = 0; i < mScreenShotList.size(); i++) {

            String url = mScreenShotList.get(i);

            File file = new File(url);

            if (!file.exists()) {
                break;
            }

            String targetPath = getCacheDir() + file.getName();

            final String compressImage = CompressUtil.compressImage(url, targetPath, 30);

            final File compressFile = new File(compressImage);

            if (compressFile.exists()) {
                RequestBody body = RequestBody.create(MediaType.parse("image/*"), compressFile);
                requestBody.addFormDataPart("files", compressFile.getName(), body);
            }
        }

        Request request = new Request.Builder().url(LoginApi.URL + "/image/upload").post(requestBody.build()).tag(OrderPriceActivity.this).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000 * 100, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {

                OrderPriceActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showShortToast(OrderPriceActivity.this, "图片上传失败！:" + e.getLocalizedMessage());

                        if (mDialog != null) {
                            mDialog.dismiss();
                        }

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String str = response.body().string();

                    ArrayList<String> list = new ArrayList<>();
                    try {
                        JSONObject object = new JSONObject(str);
                        JSONArray accessAddress = object.getJSONArray("list");

                        for (int i = 0; i < accessAddress.length(); i++) {
                            list.add(accessAddress.get(i).toString());
                        }

                        mImageUrl.clear();
                        mImageUrl.addAll(list);

                        LogUtils.log("order pic upload pic succ . ");

                        toSecondStep();

                    } catch (JSONException e) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (mDialog != null) {
                                    mDialog.dismiss();
                                }

                                Utils.showShortToast(OrderPriceActivity.this, "图片上传失败！EXC");
                            }
                        });
                    }

                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (mDialog != null) {
                                mDialog.dismiss();
                            }

                            Utils.showShortToast(OrderPriceActivity.this, "图片上传失败！");
                        }
                    });
                }
            }
        });
    }


    private void toSecondStep() {

        LogUtils.log("to second step . ");

        BillApi billApi = KHttpWorker.ins().createHttpService(LoginApi.URL, BillApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("orderId", orderId);
        params.put("prepayService", mContentTv.getText().toString());
        params.put("prepayCost", mPriceTv.getText().toString());

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mImageUrl.size(); i++) {
            builder.append(mImageUrl.get(i));
            if (i != mImageUrl.size() - 1) {
                builder.append(",");
            }
        }

        if (mImageUrl.size() > 0) {
            params.put("prepayImgUrls", builder.toString());
        }

        retrofit2.Call<JsonObject> request = billApi.prepay(params);

        request.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                LogUtils.log("two step info : " + response.body().toString());

                JsonObject object = response.body();
                final String msg = object.get("msg").getAsString();
                final int code = object.get("code").getAsInt();

                if (code == 0) {
                    toThirdStep();
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (mDialog != null) {
                                mDialog.dismiss();
                            }
                            Utils.showShortToast(OrderPriceActivity.this, msg + ": " + code);
                        }
                    });
                }
            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (mDialog != null) {
                            mDialog.dismiss();
                        }
                        Utils.showShortToast(OrderPriceActivity.this, "获取订单信息失败");
                    }
                });
            }
        });
    }

    private void toThirdStep() {

        BillApi billApi = KHttpWorker.ins().createHttpService(LoginApi.URL, BillApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("orderId", orderId);
        params.put("payType", String.valueOf(0));

        retrofit2.Call<JsonObject> request = billApi.getPayUrl(params);

        request.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                LogUtils.log("pay url : " + response.body().toString());

                JsonObject object = response.body();
                final String msg = object.get("msg").getAsString();
                final int code = object.get("code").getAsInt();

                if (code == 0) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (mDialog != null) {
                                mDialog.dismiss();
                            }
                        }
                    });

                    String url = object.get("url").getAsString();

                    Intent intent = new Intent(OrderPriceActivity.this, OrderPayQRCodeActivity.class);
                    intent.putExtra("url", url);
                    OrderPriceActivity.this.startActivity(intent);


                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (mDialog != null) {
                                mDialog.dismiss();
                            }
                            Utils.showShortToast(OrderPriceActivity.this, msg + ": " + code);
                        }
                    });
                }

            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (mDialog != null) {
                            mDialog.dismiss();
                        }
                        Utils.showShortToast(OrderPriceActivity.this, "获取支付链接失败");
                    }
                });
            }
        });

    }


    public static void goToActivity(Context ctx, String orderId) {

        if (ctx == null) {
            ctx = App.getInstance();
        }

        Intent intent = new Intent();
        intent.setClass(ctx, OrderPriceActivity.class);
        intent.putExtra("order_id", orderId);
        ctx.startActivity(intent);
    }


}

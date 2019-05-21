package com.bjxapp.worker.ui.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.EnterpriseApi;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.ui.view.activity.order.CompressUtil;
import com.bjxapp.worker.ui.view.activity.order.ImageOrderActivity;
import com.bjxapp.worker.ui.view.activity.widget.SpaceItemDecoration;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.RoundImageView;
import com.bjxapp.worker.utils.SDCardUtils;
import com.bjxapp.worker.utils.UploadFile;
import com.bjxapp.worker.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bjxapp.worker.global.Constant.REQUEST_CODE_CLOCK_TAKE_PHOTO;
import static java.lang.String.valueOf;

public class CompleteActivity extends Activity {

    @BindView(R.id.title_text_tv)
    TextView mTitleTv;

    @OnClick(R.id.title_image_back)
    void onClickBack() {
        onBackPressed();
    }

    @BindView(R.id.recycler_view_complete)
    RecyclerView mRecyclerView;

    private String equipId;
    private String orderId;

    private ArrayList<ImageBean> mImageList = new ArrayList<>();

    @BindView(R.id.change_reason_complete_tv)
    EditText mMethodTv;

    @BindView(R.id.content_complete_limit)
    TextView mLimitTv;

    @OnClick(R.id.wait_contact_change_btn)
    void confirm() {

        if (TextUtils.isEmpty(mMethodTv.getText().toString())) {
            Toast.makeText(this, "请输入维修结果说明", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mImageList.size() <= 1) {
            Toast.makeText(this, "请至少添加一张照片", Toast.LENGTH_SHORT).show();
            return;
        }

        commitImage();
    }


    private XWaitingDialog mWaitingDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_layout);
        ButterKnife.bind(this);
        initView();
        handleIntent();
        initData();
    }

    private void initData() {
    }

    private void handleIntent() {

        equipId = getIntent().getStringExtra("plan_id");
        orderId = getIntent().getStringExtra("order_id");
    }

    private GridLayoutManager mGridLayoutManager;

    private void initView() {

        mWaitingDialog = new XWaitingDialog(this);
        mTitleTv.setText("上传维修结果照片");

        mGridLayoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(4, 50, true));
        myAdapter = new MyAdapter();
        mRecyclerView.setAdapter(myAdapter);

        ImageBean bean = new ImageBean(ImageBean.TYPE_IMAGE, "");
        mImageList.add(bean);
        myAdapter.setList(mImageList);
        myAdapter.notifyDataSetChanged();

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

        if (requestCode == 0x02) {
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
                    //KLog.error(KLog.KLogFeature.toulan,"load image failed, msg:"+e.getMessage());
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        } else if (requestCode == REQUEST_CODE_CLOCK_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {

                try {
                    String filePath = PATH + "/" + name;
                    Bitmap bitmap = UploadFile.createImageThumbnail(filePath, getScreenShotWidth(), true);
                    if (bitmap != null) {
                        insertImg(bitmap, filePath, true);
                    }
                } catch (Exception e) {

                }
            }
        }
    }

    public void insertImg(Bitmap bitmap, final String imagePath,
                          boolean showDelImg) {

        ImageBean bean = new ImageBean(ImageBean.TYPE_ADD, imagePath);
        mImageList.add(0, bean);
        imgList.add(imagePath);
        myAdapter.notifyDataSetChanged();
    }

    private ArrayList<String> imgList = new ArrayList<>();

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public void commitImage() {

        if (TextUtils.isEmpty(mMethodTv.getText().toString())) {
            Toast.makeText(this, "请先填写维修方案", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mImageList.size() <= 1) {
            Toast.makeText(this, "请至少添加一张维修照片", Toast.LENGTH_SHORT).show();
            return;
        }

        mWaitingDialog.show("正在提交", false);

        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);

        Map<String, String> map = new HashMap<>();
        map.put("userCode", ConfigManager.getInstance(this).getUserCode());
        map.put("token", ConfigManager.getInstance(this).getUserSession());

        boolean isFileValid = false;

        for (int i = 0; i < mImageList.size(); i++) {

            ImageBean item = mImageList.get(i);

            if (item.type == ImageBean.TYPE_ADD) {
                File file = new File(item.getUrl());

                if (!file.exists()) {
                    break;
                }

                String targetPath = getCacheDir() + file.getName();

                final String compressImage = CompressUtil.compressImage(item.getUrl(), targetPath, 30);

                final File compressFile = new File(compressImage);

                if (compressFile.exists()) {
                    isFileValid = true;
                    RequestBody body = RequestBody.create(MediaType.parse("image/*"), compressFile);
                    requestBody.addFormDataPart("files", compressFile.getName(), body);
                }
            }
        }

        if (!isFileValid) {
            return;
        }

        for (Map.Entry entry : map.entrySet()) {
            requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
        }

        Request request = new Request.Builder().url(LoginApi.URL + "/image/upload").post(requestBody.build()).tag(CompleteActivity.this).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000 * 100, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, final IOException e) {

                CompleteActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (mWaitingDialog != null) {
                            mWaitingDialog.dismiss();
                        }

                        Utils.showShortToast(CompleteActivity.this, "图片上传失败！:" + e.getLocalizedMessage());
                    }
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {


                if (response.isSuccessful()) {
                    String str = response.body().string();

                    final ArrayList<String> list = new ArrayList<>();
                    try {
                        JSONObject object = new JSONObject(str);
                        JSONArray accessAddress = object.getJSONArray("list");

                        for (int i = 0; i < accessAddress.length(); i++) {
                            list.add(accessAddress.get(i).toString());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            startCommit(list);
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Utils.showShortToast(CompleteActivity.this, "图片上传失败！");
                            Utils.finishWithoutAnim(CompleteActivity.this);
                        }
                    });
                }
            }
        });

    }

    public void startCommit(ArrayList<String> list) {


        EnterpriseApi enterpriseApi = KHttpWorker.ins().createHttpService(LoginApi.URL, EnterpriseApi.class);

        Call<JsonObject> call = null;

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("orderId", orderId);

        params.put("maintainPlanId", equipId);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i < list.size() - 1) {
                builder.append(list.get(i) + ",");
            } else {
                builder.append(list.get(i));
            }
        }

        params.put("resultImgUrls", builder.toString());
        params.put("result", mMethodTv.getText().toString());

        call = enterpriseApi.completePlan(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                mWaitingDialog.dismiss();

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    final JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {
                        CompleteActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CompleteActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    } else {
                        CompleteActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CompleteActivity.this, msg + ":" + code, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                CompleteActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWaitingDialog.dismiss();
                        Toast.makeText(CompleteActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public static void goToActivity(Activity context, String equipId, String orderId) {

        Intent intent = new Intent();
        intent.setClass(context, CompleteActivity.class);
        intent.putExtra("plan_id", equipId);
        intent.putExtra("order_id", orderId);

        context.startActivityForResult(intent, 0x05);
    }

    private MyAdapter myAdapter;

    private class MyAdapter extends RecyclerView.Adapter {

        private ArrayList<ImageBean> mList = new ArrayList<>();

        public MyAdapter() {

        }

        public void setList(ArrayList<ImageBean> imageBean) {
            this.mList = imageBean;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder mHolder = null;

            if (viewType == ImageBean.TYPE_ADD) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_image_item, parent, false);
                mHolder = new VH_IMAGE_ITEM(view);
            } else if (viewType == ImageBean.TYPE_IMAGE) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_image_item_cl, parent, false);
                mHolder = new VH_DELETE_ITEM(view);
            }

            return mHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            final ImageBean bean = mList.get(position);

            if (holder instanceof VH_IMAGE_ITEM) {

                Glide.with(CompleteActivity.this).load(bean.getUrl()).into(((VH_IMAGE_ITEM) holder).mIv);

                ((VH_IMAGE_ITEM) holder).mDeleteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.deleteImage(position);
                        }
                    }
                });

                ((VH_IMAGE_ITEM) holder).mIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.goToImageDetail(bean);
                        }
                    }
                });

            } else if (holder instanceof VH_DELETE_ITEM) {

                if (mList.size() >= 20) {
                    holder.itemView.setVisibility(View.GONE);
                } else {
                    holder.itemView.setVisibility(View.VISIBLE);
                }

                ((VH_DELETE_ITEM) holder).mDeleteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.addImage();
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mList.get(position).getType();
        }

    }

    private class VH_IMAGE_ITEM extends RecyclerView.ViewHolder {

        private RoundImageView mIv;
        private ImageView mDeleteIv;

        public VH_IMAGE_ITEM(View itemView) {
            super(itemView);
            mDeleteIv = itemView.findViewById(R.id.deleteImageView);
            mIv = itemView.findViewById(R.id.screenShotImageView);
        }
    }

    private class VH_DELETE_ITEM extends RecyclerView.ViewHolder {

        private ImageView mDeleteIv;

        public VH_DELETE_ITEM(View itemView) {
            super(itemView);
            mDeleteIv = itemView.findViewById(R.id.screenShotImageView);
        }
    }


    private class ImageBean {

        private int type;
        private String url;

        public static final int TYPE_ADD = 0x01;
        public static final int TYPE_IMAGE = 0x02;

        public ImageBean() {

        }

        public ImageBean(int type, String url) {
            this.type = type;
            this.url = url;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public OnOperationListener mListener = new OnOperationListener() {
        @Override
        public void addImage() {
            loadImages();
        }

        @Override
        public void deleteImage(int position) {
            mImageList.remove(position);
            myAdapter.setList(mImageList);
            myAdapter.notifyDataSetChanged();
        }

        @Override
        public void goToImageDetail(ImageBean bean) {
            ImageOrderActivity.goToActivity(CompleteActivity.this, bean.getUrl());
        }
    };

    private int getScreenShotWidth() {
        int screenWidth = DimenUtils.getScreenWidth(this);
        screenWidth = screenWidth - (int) (screenWidth * 1.0 / 9); // 除掉边框
        return screenWidth / 4;
    }

    private static final String PATH = Environment
            .getExternalStorageDirectory() + "/DCIM";

    protected void loadImages() {

        final CharSequence[] items = getResources().getStringArray(R.array.user_select_image_items);
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle("选取照片")
                .setNegativeButton("取消", null)
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        try {
                            if (!SDCardUtils.exist()) {
                                Utils.showShortToast(CompleteActivity.this, "SD卡被占用或不存在");
                            } else {
                                if (item == 0) {
                                    Uri imageURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                    if (imageURI != null) {
                                        try {
                                            if (ContextCompat.checkSelfPermission(CompleteActivity.this,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                                                    != PackageManager.PERMISSION_GRANTED) {
                                                ActivityCompat.requestPermissions(CompleteActivity.this,
                                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                                            } else {
                                                Intent intent = new Intent(Intent.ACTION_PICK, imageURI);
                                                startActivityForResult(intent, 0x02);
                                            }
                                        } catch (Exception e) {
                                            Log.w("FeedbackPresenter", "loadImages: " + e.getMessage());
                                        }
                                    }
                                } else {
                                    if (ContextCompat.checkSelfPermission(CompleteActivity.this,
                                            Manifest.permission.READ_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(CompleteActivity.this,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(CompleteActivity.this,
                                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                                    } else {
                                        //  Intent intent = new Intent(Intent.ACTION_PICK, imageURI);
                                        //   startActivityForResult(intent, FEEDBACK_LOAD_IMAGES_RESULT);
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用系统相机
                                        name = android.text.format.DateFormat.format("yyyyMMdd_hhmmss",
                                                Calendar.getInstance(Locale.CHINA))
                                                + ".jpg";//以当前时间作为文件名
                                        Uri imageUri = Uri.fromFile(new File(PATH, name));

                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                                        startActivityForResult(intent, REQUEST_CODE_CLOCK_TAKE_PHOTO);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Utils.showShortToast(CompleteActivity.this, "SD卡被占用或不存在");
                        }
                    }
                }).create();
        dlg.show();

    }

    String name;

    public interface OnOperationListener {

        void addImage();


        void deleteImage(int position);


        void goToImageDetail(ImageBean bean);

    }


}

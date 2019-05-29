package com.bjxapp.worker.ui.view.activity.order;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bjx.master.R;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.ui.view.activity.CheckOrderDetailActivity;
import com.bjxapp.worker.ui.view.activity.DeviceInfoActivity;
import com.bjxapp.worker.ui.view.activity.MaintainActivity;
import com.bjxapp.worker.ui.view.activity.widget.SpaceItemDecoration;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.RoundImageView;
import com.bjxapp.worker.utils.SDCardUtils;
import com.bjxapp.worker.utils.UploadFile;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.mask.MaskFile;
import com.bumptech.glide.Glide;

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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.bjxapp.worker.global.Constant.REQUEST_CODE_CLOCK_TAKE_PHOTO;
import static java.lang.String.valueOf;

/**
 * Created by zhangdan on 2018/10/14.\
 * <p>
 * comments:
 */

public class AddImageActivity extends Activity {

    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private MyAdapter mAdapter;

    private static final int MAX_PIC_COUNT = 20;
    private static final int FEEDBACK_LOAD_IMAGES_RESULT = 1;

    private ArrayList<ImageBean> mList = new ArrayList<>();

    public static final int OP_ADD = 0x01;

    private XWaitingDialog mWaitingDialog;

    private boolean isFinishedBill;

    @OnClick(R.id.title_image_back)
    void onClickBack() {
        onBackPressed();
    }

    @OnClick(R.id.add_confirm_btn)
    void onClickConfirm() {
        startConfirmReal();
    }

    @BindView(R.id.add_confirm_btn)
    XButton mConfirmBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        ButterKnife.bind(this);
        mRecyclerView = findViewById(R.id.recycler_view);
        mGridLayoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(4, 50, true));
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mWaitingDialog = new XWaitingDialog(this);
        handleIntent();
        initData();
    }

    private void initData() {
        if (!isFinishedBill) {
            ImageBean bean = new ImageBean(ImageBean.TYPE_IMAGE, "");
            mList.add(bean);
        } else {
            mConfirmBtn.setVisibility(View.GONE);
        }

        mAdapter.setList(mList);
        mAdapter.notifyDataSetChanged();
    }

    private void handleIntent() {

        Intent intent = getIntent();

        try {
            ArrayList<String> mlist = intent.getStringArrayListExtra("img_list");
            isFinishedBill = intent.getBooleanExtra("isHistoryBill", false);

            if (mlist != null && mlist.size() > 0) {
                // mList.addAll(0 , mlist);
                ArrayList<ImageBean> temList = new ArrayList<>();
                for (int i = 0; i < mlist.size(); i++) {
                    ImageBean item = new ImageBean(ImageBean.TYPE_ADD, mlist.get(i));
                    temList.add(item);
                }
                mList.addAll(0, temList);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {

        }

    }

    /**
     *
     */
    public OnOperationListener mListener = new OnOperationListener() {
        @Override
        public void addImage() {
            loadImages();
        }

        @Override
        public void deleteImage(int position) {
            mList.remove(position);
            mAdapter.setList(mList);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void goToImageDetail(ImageBean bean) {
            ImageOrderActivity.goToActivity(AddImageActivity.this, bean.getUrl());
        }
    };

    private class MyAdapter extends RecyclerView.Adapter {

        private ArrayList<ImageBean> mList;

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

                Glide.with(AddImageActivity.this).load(bean.getUrl()).into(((VH_IMAGE_ITEM) holder).mIv);

                ((VH_IMAGE_ITEM) holder).mDeleteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null && !isFinishedBill) {
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

                if (mList.size() >= MAX_PIC_COUNT) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FEEDBACK_LOAD_IMAGES_RESULT) {
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

                        insertImg(imagePath, true);
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

                    insertImg(filePath, true);

                } catch (Exception e) {

                }


               /* Uri imageURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String photoPath = Utils.getPhotoUrl(AddImageActivity.this, imageURI, data);

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = null;
                try {
                  //  cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                  //  if (cursor != null) {
                   //     cursor.moveToFirst();
                   //     int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                   //     final String imagePath = cursor.getString(columnIndex);
                        //根据手机屏幕设置图片宽度
                        Bitmap bitmap = UploadFile.createImageThumbnail(photoPath, getScreenShotWidth(), true);
                        if (bitmap != null) {
                            insertImg(bitmap, photoPath, true);
                        }
                  //  }
                } catch (Exception e) {
                    e.printStackTrace();
                    //KLog.error(KLog.KLogFeature.toulan,"load image failed, msg:"+e.getMessage());
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }*/
            }
        }
    }

    public void insertImg(final String imagePath,
                          boolean showDelImg) {

        String targetPath = getCacheDir() + new File(imagePath).getName();

        final String compressImage = CompressUtil.compressImage(imagePath, targetPath, 30);

        ImageBean bean = new ImageBean(ImageBean.TYPE_ADD, compressImage);
        mList.add(0, bean);
        mAdapter.notifyDataSetChanged();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (from_static) {
                    MaskFile.addMask(compressImage, CheckOrderDetailActivity.currentAddress_static, CheckOrderDetailActivity.shopAddress_static,
                            CheckOrderDetailActivity.enterpriseAddress_static, DeviceInfoActivity.model_static);
                }

            }
        }, 300);

    }


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
                                Utils.showShortToast(AddImageActivity.this, "SD卡被占用或不存在");
                            } else {
                                if (item == 0) {
                                    Uri imageURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                    if (imageURI != null) {
                                        try {
                                            if (ContextCompat.checkSelfPermission(AddImageActivity.this,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                                                    != PackageManager.PERMISSION_GRANTED) {
                                                ActivityCompat.requestPermissions(AddImageActivity.this,
                                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                                            } else {
                                                Intent intent = new Intent(Intent.ACTION_PICK, imageURI);
                                                startActivityForResult(intent, FEEDBACK_LOAD_IMAGES_RESULT);
                                            }
                                        } catch (Exception e) {
                                            Log.w("FeedbackPresenter", "loadImages: " + e.getMessage());
                                        }
                                    }
                                } else {
                                    if (ContextCompat.checkSelfPermission(AddImageActivity.this,
                                            Manifest.permission.READ_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(AddImageActivity.this,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(AddImageActivity.this,
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
                            Utils.showShortToast(AddImageActivity.this, "SD卡被占用或不存在");
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


    public static boolean from_static = false;

    public static void goToActivity(Activity ctx, int code, ArrayList<String> mImgList, boolean isHistoryBill, boolean fromDeviceInfo) {

        Intent intent = new Intent();
        intent.setClass(ctx, AddImageActivity.class);
        intent.putStringArrayListExtra("img_list", mImgList);
        intent.putExtra("isHistoryBill", isHistoryBill);

        from_static = fromDeviceInfo;

        //ctx.startactivityfor(intent);
        ctx.startActivityForResult(intent, code);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void startConfirmReal() {
        if (mList.size() <= 1) {
            Utils.showShortToast(this, "请先选择照片 ..");
        } else {
            startCommitImage();
        }
    }

    private void startCommitImage() {

        mWaitingDialog.show("正在上传图片..", false);

        post_file();
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public void post_file() {

        if (mList == null || mList.size() <= 1) {
            return;
        }

        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);

        Map<String, String> map = new HashMap<>();
        map.put("userCode", ConfigManager.getInstance(this).getUserCode());
        map.put("token", ConfigManager.getInstance(this).getUserSession());

        for (int i = 0; i < mList.size(); i++) {

            ImageBean item = mList.get(i);

            if (item.type == ImageBean.TYPE_ADD) {
                File file = new File(item.getUrl());

                if (!file.exists()) {
                    break;
                }

                if (file.exists()) {
                    RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
                    requestBody.addFormDataPart("files", file.getName(), body);
                }
            }
        }

        for (Map.Entry entry : map.entrySet()) {
            requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
        }

        Request request = new Request.Builder().url(LoginApi.URL + "/image/upload").post(requestBody.build()).tag(AddImageActivity.this).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000 * 100, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {

                AddImageActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (mWaitingDialog != null) {
                            mWaitingDialog.dismiss();
                        }

                        Utils.showShortToast(AddImageActivity.this, "图片上传失败！:" + e.getLocalizedMessage());

                        Utils.finishWithoutAnim(AddImageActivity.this);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mWaitingDialog != null) {
                            mWaitingDialog.dismiss();
                        }
                    }
                });

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
                            Utils.showShortToast(AddImageActivity.this, "图片保存成功！");
                            Intent intent = new Intent();
                            updateImageList(list);
                            intent.putStringArrayListExtra("result", list);
                            setResult(RESULT_OK, intent);
                            Utils.finishWithoutAnim(AddImageActivity.this);
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Utils.showShortToast(AddImageActivity.this, "图片上传失败！");
                            Utils.finishWithoutAnim(AddImageActivity.this);
                        }
                    });
                }
            }
        });
    }

    private void updateImageList(ArrayList<String> imageList) {

        if (mList.size() <= 0) {
            return;
        }

        for (int i = 0; i < mList.size(); i++) {
            String item = mList.get(i).getUrl();
            if (item.startsWith("http")) {
                imageList.add(item);
            }
        }
    }


}

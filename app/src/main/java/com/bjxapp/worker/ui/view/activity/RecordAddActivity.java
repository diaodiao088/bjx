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
import android.text.TextUtils;
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
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.RecordApi;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.ui.view.activity.bean.RecordItemBean;
import com.bjxapp.worker.ui.view.activity.widget.SpaceItemDecoration;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.RoundImageView;
import com.bjxapp.worker.utils.DateUtils;
import com.bjxapp.worker.utils.SDCardUtils;
import com.bjxapp.worker.utils.UploadFile;
import com.bjxapp.worker.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bjxapp.worker.global.Constant.REQUEST_CODE_CLOCK_TAKE_PHOTO;

public class RecordAddActivity extends Activity {

    public static final int REQUEST_CODE_RECORD_ADD = 0x01;

    @BindView(R.id.record_device_name)
    TextView mRecordDeviceNameTv;

    @BindView(R.id.record_brand_name_ev)
    EditText mRecordBrandNameTv;

    @BindView(R.id.record_type_tv)
    EditText mRecordTypeTv;

    @BindView(R.id.record_time_tv)
    TextView mRecordTimeTv;

    @BindView(R.id.record_status_tv)
    TextView mRecordStatusTv;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.title_right_small_tv)
    TextView mTitleRightTv;

    @BindView(R.id.mistake_reason_tv)
    EditText mMistakeReasonTv;

    @OnClick(R.id.title_right_small_tv)
    void onClickDelete() {
        deleteDevice();
    }

    @OnClick(R.id.title_right_small_tv)
    void onClickSmallTv() {

    }

    private RecordItemBean mRecordItemBean;

    private GridLayoutManager mGridLayoutManager;
    private MyAdapter mAdapter;

    private static final int MAX_PIC_COUNT = 20;
    private static final int FEEDBACK_LOAD_IMAGES_RESULT = 1;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @OnClick(R.id.record_status_tv)
    void onClickStatus() {
        showStatusPicker();
    }

    @OnClick(R.id.record_time_tv)
    void onClickTime() {
        showTimerPicker();
    }


    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @OnClick(R.id.add_confirm_btn)
    void confirm() {
        if (mIsAdd){
            addConfirm();
        }
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @OnClick(R.id.frag_layout)
    void onClickFrag() {
        FragileActivity.gotoActivity(this);
    }

    private ArrayList<ImageBean> mImageList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_add_activity);
        ButterKnife.bind(this);
        initView();
        handleIntent();
        initData();
    }

    private void initView() {
        mGridLayoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(4, 50, true));
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mTitleRightTv.setVisibility(View.VISIBLE);
        mTitleRightTv.setText("删除");

    }

    private void initData() {

        mRecordDeviceNameTv.setText(mName);

        // 如果是新增
        if (mIsAdd == true) {

            mTitleRightTv.setVisibility(View.GONE);

        } else {

            mTitleRightTv.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(mRecordItemBean.getBrandName())) {
                mRecordBrandNameTv.setText(mRecordItemBean.getBrandName());
            }

        }

        ImageBean bean = new ImageBean(ImageBean.TYPE_IMAGE, "");
        mImageList.add(bean);
        mAdapter.setList(mImageList);
        mAdapter.notifyDataSetChanged();
    }

    public static void gotoActivity(Activity context, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, RecordAddActivity.class);
        context.startActivityForResult(intent, requestCode);
    }


    public void showTimerPicker() {

        DatePicker mPicker = new DatePicker(this);
        mPicker.setCanceledOnTouchOutside(true);
        mPicker.setUseWeight(true);
        mPicker.setTopPadding(DimenUtils.dp2px(10, this));
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH);
        int day = ca.get(Calendar.DATE);
        mPicker.setRangeEnd(year, month + 1, day);
        mPicker.setRangeStart(2000, 01, 01);


        int selectYear = Integer.valueOf(DateUtils.getYear(0));
        int selectMonth = Integer.valueOf(DateUtils.getMonth(0));
        int selectDay = Integer.valueOf(DateUtils.getDay(0));
        mPicker.setSelectedItem(selectYear, selectMonth, selectDay);
        mPicker.setResetWhileWheel(false);
        mPicker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                mRecordTimeTv.setText(year + "-" + month + "-" + day + "  00:00:00");
            }
        });
        mPicker.show();
    }


    /**
     * select service name .
     */
    public void showStatusPicker() {
        OptionPicker picker = new OptionPicker(this,
                new String[]{"在用", "停用"});
        picker.setCycleDisable(true);//不禁用循环
        picker.setTopBackgroundColor(0xFFffffff);
        picker.setTopHeight(30);
        picker.setTopLineColor(0xfffdfdfd);
        picker.setTopLineHeight(3);
        picker.setTitleText("设备状态");
        picker.setTitleTextColor(0xFF545454);
        picker.setTitleTextSize(14);
        picker.setCancelTextColor(0xFF545454);
        picker.setCancelTextSize(12);
        picker.setSubmitTextColor(0xFF00a551);
        picker.setSubmitTextSize(12);
        picker.setTextColor(0xFF545454, 0x99545454);
        WheelView.DividerConfig config = new WheelView.DividerConfig();
        config.setColor(0xFff5f5f5);//线颜色
        config.setAlpha(250);//线透明度
        config.setRatio((float) (1.0 / 8.0));//线比率
        picker.setDividerConfig(config);
        picker.setBackgroundColor(0xFFffffff);
        picker.setSelectedIndex(0);
        picker.setCanceledOnTouchOutside(true);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                mRecordStatusTv.setText(item);

            }
        });
        picker.show();
    }

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

                Glide.with(RecordAddActivity.this).load(bean.getUrl()).into(((VH_IMAGE_ITEM) holder).mIv);

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

    public OnOperationListener mListener = new OnOperationListener() {
        @Override
        public void addImage() {
            loadImages();
        }

        @Override
        public void deleteImage(int position) {
            mImageList.remove(position);
            mAdapter.setList(mImageList);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void goToImageDetail(ImageBean bean) {
            //  ImageOrderActivity.goToActivity(AddImageActivity.this, bean.getUrl());
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
                                Utils.showShortToast(RecordAddActivity.this, "SD卡被占用或不存在");
                            } else {
                                if (item == 0) {
                                    Uri imageURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                    if (imageURI != null) {
                                        try {
                                            if (ContextCompat.checkSelfPermission(RecordAddActivity.this,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                                                    != PackageManager.PERMISSION_GRANTED) {
                                                ActivityCompat.requestPermissions(RecordAddActivity.this,
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
                                    if (ContextCompat.checkSelfPermission(RecordAddActivity.this,
                                            Manifest.permission.READ_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(RecordAddActivity.this,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(RecordAddActivity.this,
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
                            Utils.showShortToast(RecordAddActivity.this, "SD卡被占用或不存在");
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
        mAdapter.notifyDataSetChanged();
    }

    private String mName;
    private String mParentId;
    private String mEnterId;
    private String mShopId;
    private String mCategoryId;
    private boolean mIsAdd;

    public static final String TYPE_NAME = "type_name";
    public static final String TYPE_PARENT_ID = "parent_id";
    public static final String TYPE_ENTER_ID = "enter_id";
    public static final String TYPE_SHOP_ID = "shop_id";
    public static final String TYPE_CATEGORY_ID = "category_id";
    public static final String TYPE_IS_ADD = "is_add";

    public static final String TYPE_BEAN = "type_bean";

    public static final int REQUEST_CODE_ADD_DEVICE = 0x02;


    private void handleIntent() {

        Intent intent = getIntent();

        if (intent != null) {
            mName = intent.getStringExtra(TYPE_NAME);
            mParentId = intent.getStringExtra(TYPE_PARENT_ID);
            mEnterId = intent.getStringExtra(TYPE_ENTER_ID);
            mShopId = intent.getStringExtra(TYPE_SHOP_ID);
            mCategoryId = intent.getStringExtra(TYPE_CATEGORY_ID);
            mIsAdd = intent.getBooleanExtra(TYPE_IS_ADD, false);
            mRecordItemBean = intent.getParcelableExtra(TYPE_BEAN);
        }

    }

    public static void goToActivityForResult(Activity context, String name, String parentId, String enterId,
                                             String shopId, String categoryId) {

        Intent intent = new Intent();

        intent.setClass(context, RecordAddActivity.class);

        intent.putExtra(TYPE_NAME, name);
        intent.putExtra(TYPE_SHOP_ID, shopId);
        intent.putExtra(TYPE_CATEGORY_ID, categoryId);
        intent.putExtra(TYPE_PARENT_ID, parentId);
        intent.putExtra(TYPE_ENTER_ID, enterId);
        intent.putExtra(TYPE_IS_ADD, true);

        context.startActivityForResult(intent, REQUEST_CODE_ADD_DEVICE);
    }

    public static void goToActivity(Activity context, RecordItemBean recordItemBean, String shopId) {
        Intent intent = new Intent();
        intent.setClass(context, RecordAddActivity.class);
        intent.putExtra(TYPE_BEAN, recordItemBean);
        intent.putExtra(TYPE_SHOP_ID, shopId);
        context.startActivity(intent);
    }


    private void deleteDevice() {

        if (mRecordItemBean == null) {
            return;
        }

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("id", mRecordItemBean.getId());

        Call<JsonObject> objectCall = recordApi.deleteDevice(params);
        objectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    final JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(RecordAddActivity.this, msg + ":" + code);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RecordAddActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public void addConfirm() {

        if (TextUtils.isEmpty(mRecordDeviceNameTv.getText().toString())) {
            Utils.showShortToast(RecordAddActivity.this, "请填写名称.");
            return;
        }

        if (TextUtils.isEmpty(mRecordBrandNameTv.getText().toString())) {
            Utils.showShortToast(RecordAddActivity.this, "请输入品牌名称.");
            return;
        }

        if (TextUtils.isEmpty(mRecordTypeTv.getText().toString())) {
            Utils.showShortToast(RecordAddActivity.this, "请填写规格型号.");
            return;
        }

        if (TextUtils.isEmpty(mRecordTimeTv.getText().toString())) {
            Utils.showShortToast(RecordAddActivity.this, "请选择时间.");
            return;
        }

        if (TextUtils.isEmpty(mRecordStatusTv.getText().toString())) {
            Utils.showShortToast(RecordAddActivity.this, "请选择设备状态.");
            return;
        }

        if (mImageList.size() <= 1) {
            Utils.showShortToast(RecordAddActivity.this, "请添加照片.");
            return;
        }

        // TODO: 2019/2/27
        if (TextUtils.isEmpty(mMistakeReasonTv.getText().toString())) {

        }


        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("enterpriseId", mEnterId);
        params.put("shopId", mShopId);
        params.put("parentCategoryId", mParentId);
        params.put("categoryId", mCategoryId);
        params.put("name", mRecordDeviceNameTv.getText().toString());
        params.put("brandName", mRecordBrandNameTv.getText().toString());
        params.put("model", mRecordTypeTv.getText().toString());
        params.put("productionTime", mRecordTimeTv.getText().toString());
        params.put("remark", mMistakeReasonTv.getText().toString());
        // TODO: 2019/2/27
        params.put("imgUrls", "www.baidu.com");

        boolean isDisable = mRecordStatusTv.getText().toString().equals("禁用");

        params.put("status", isDisable ? "0" : "1");

        Call<JsonObject> call = recordApi.addDevice(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    final JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(RecordAddActivity.this, "添加成功");
                                finish();
                            }
                        });
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(RecordAddActivity.this, msg + ":" + code);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RecordAddActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }


}

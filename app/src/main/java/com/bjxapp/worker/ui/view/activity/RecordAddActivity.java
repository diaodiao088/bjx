package com.bjxapp.worker.ui.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bjx.master.R;
import com.bjxapp.worker.App;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.RecordApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.ui.view.activity.bean.FragileBean;
import com.bjxapp.worker.ui.view.activity.bean.RecordItemBean;
import com.bjxapp.worker.ui.view.activity.order.CompressUtil;
import com.bjxapp.worker.ui.view.activity.widget.SpaceItemDecoration;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.RoundImageView;
import com.bjxapp.worker.utils.DateUtils;
import com.bjxapp.worker.utils.SDCardUtils;
import com.bjxapp.worker.utils.UploadFile;
import com.bjxapp.worker.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
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
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;
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

    private XWaitingDialog mWaitingDialog;

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
        if (mIsAdd) {
            addConfirm();
        } else {
            changeConfirm();
        }
    }

    @OnClick(R.id.copy_tv)
    void onClickCopy() {

        if (TextUtils.isEmpty(mSerTv.getText().toString())) {
            return;
        }

        try{
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(mSerTv.getText());
            Toast.makeText(this , "复制成功", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this , "复制失败", Toast.LENGTH_SHORT).show();
        }

    }

    @BindView(R.id.add_confirm_btn)
    XButton mBtn;

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @BindView(R.id.ser_divider)
    View divider;

    @BindView(R.id.ser_ly)
    LinearLayout mSerly;

    @BindView(R.id.record_device_ser_num)
    TextView mSerTv;

    @OnClick(R.id.frag_layout)
    void onClickFrag() {
        FragileActivity.gotoActivity(this, mFragList, isFinished);
    }

    private ArrayList<ImageBean> mImageList = new ArrayList<>();

    private ArrayList<FragileBean> mFragList = new ArrayList<>();

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
        mTitleTextView.setText("设备录入详情");
        mWaitingDialog = new XWaitingDialog(this);

    }

    private void initData() {

        mRecordDeviceNameTv.setText(mName);

        // 如果是新增
        if (mIsAdd) {

            mTitleRightTv.setVisibility(View.GONE);

            ImageBean bean = new ImageBean(ImageBean.TYPE_IMAGE, "");
            mImageList.add(bean);
            mAdapter.setList(mImageList);
            mAdapter.notifyDataSetChanged();

        } else {

            mTitleRightTv.setVisibility(View.VISIBLE);

            if (mRecordItemBean != null) {
                if (mRecordItemBean.getRecordStatus() == 2) {
                    mTitleRightTv.setVisibility(View.GONE);
                    isFinished = true;
                    mBtn.setVisibility(View.GONE);
                    mSerly.setVisibility(View.VISIBLE);
                    divider.setVisibility(View.VISIBLE);
                    disableEvent();
                }
            }

            mAdapter.setList(mImageList);
            loadDetails();
        }
    }

    private void disableEvent() {

        mRecordBrandNameTv.setFocusableInTouchMode(false);
        mRecordBrandNameTv.setFocusable(false);

        mRecordDeviceNameTv.setFocusable(false);
        mRecordDeviceNameTv.setFocusableInTouchMode(false);

        mRecordTypeTv.setFocusable(false);
        mRecordTypeTv.setFocusableInTouchMode(false);

        mMistakeReasonTv.setFocusableInTouchMode(false);
        mMistakeReasonTv.setFocusable(false);

        mRecordTimeTv.setClickable(false);
        mRecordTimeTv.setEnabled(false);

        mRecordStatusTv.setClickable(false);
        mRecordStatusTv.setEnabled(false);

    }

    private boolean isFinished;

    private void loadDetails() {

        if (mRecordItemBean == null) {
            return;
        }

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(App.getInstance()).getUserSession());
        params.put("userCode", ConfigManager.getInstance(App.getInstance()).getUserCode());

        Call<JsonObject> call = recordApi.getDeviceInfo(mRecordItemBean.getId(), params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {

                    JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {
                        parseRecordData(object);
                    }
                } else {
                    updateUi();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                updateUi();

            }
        });


    }

    private void parseRecordData(JsonObject mainObject) {

        if (mainObject.get("equipment") == null || (mainObject).get("equipment") instanceof JsonNull){
            return;
        }


        JsonObject object = mainObject.get("equipment").getAsJsonObject();

        RecordItemBean recordItemBean = new RecordItemBean();

        recordItemBean.setBrandName(object.get("brandName").getAsString());
        recordItemBean.setParentId(object.get("parentCategoryId").getAsString());
        recordItemBean.setId(object.get("id").getAsString());
        recordItemBean.setCategoryId(object.get("categoryId").getAsString());
        recordItemBean.setEquipmentNo(object.get("equipmentNo").getAsString());
        recordItemBean.setModel(object.get("model").getAsString());

        recordItemBean.setName(object.get("name").getAsString());
        recordItemBean.setProductTime(object.get("productionTime").getAsString());
        realTime = recordItemBean.getProductTime();
        recordItemBean.setRecordStatus(object.get("recordState").getAsInt());
        recordItemBean.setRemark(object.get("remark").getAsString());
        recordItemBean.setShopId(object.get("shopId").getAsString());
        recordItemBean.setEnableStatus(object.get("status").getAsInt());
        recordItemBean.setEnterId(object.get("enterpriseId").getAsString());

        JsonArray urlArray = object.get("imgUrls").getAsJsonArray();


        ArrayList<String> customImgUrls = new ArrayList<>();

        if (urlArray != null && urlArray.size() > 0) {
            for (int i = 0; i < urlArray.size(); i++) {
                String itemUrl = urlArray.get(i).getAsString();
                customImgUrls.add(itemUrl);
            }
        }

        JsonArray parlist = object.get("partList").getAsJsonArray();

        for (int i = 0; i < parlist.size(); i++) {
            JsonObject parObject = parlist.get(i).getAsJsonObject();
            FragileBean fragileBean = new FragileBean();
            fragileBean.setFragileName(parObject.get("name").getAsString());

            JsonArray fragUrlArray = parObject.get("imgUrls").getAsJsonArray();

            if (fragUrlArray != null && fragUrlArray.size() > 0) {
                for (int j = 0; j < fragUrlArray.size(); j++) {
                    String itemUrl = fragUrlArray.get(j).getAsString();
                    FragileBean.ImageBean imageBean = fragileBean.new ImageBean(FragileBean.ImageBean.TYPE_ADD, itemUrl);
                    fragileBean.getImageList().add(imageBean);
                    fragileBean.getUrls().add(itemUrl);
                }
            }

            mFragList.add(fragileBean);
        }

        recordItemBean.setmImgUrls(customImgUrls);

        mRecordItemBean = recordItemBean;

        updateUi();
    }

    private void updateUi() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mRecordDeviceNameTv.setText(mRecordItemBean.getName());
                mRecordBrandNameTv.setText(mRecordItemBean.getBrandName());
                mRecordTypeTv.setText(mRecordItemBean.getModel());
                mSerTv.setText(mRecordItemBean.getEquipmentNo());

                String time = mRecordItemBean.getProductTime();

                if (!TextUtils.isEmpty(time) && time.length() > 10) {
                    time = time.substring(0, 10);
                }

                mRecordTimeTv.setText(time);
                if (mRecordItemBean.getEnableStatus() == 1) {
                    mRecordStatusTv.setText("在用");
                } else {
                    mRecordStatusTv.setText("禁用");
                }

                if (mImageList != null) {
                    // mList.addAll(0 , mlist);
                    ArrayList<ImageBean> temList = new ArrayList<>();
                    for (int i = 0; i < mRecordItemBean.getmImgUrls().size(); i++) {
                        ImageBean item = new ImageBean(ImageBean.TYPE_ADD, mRecordItemBean.getmImgUrls().get(i));
                        temList.add(item);
                    }
                    mImageList.addAll(0, temList);

                    if (mImageList.size() < 20) {
                        ImageBean bean = new ImageBean(ImageBean.TYPE_IMAGE, "");
                        mImageList.add(bean);

                    }

                    mAdapter.setList(mImageList);
                    mAdapter.notifyDataSetChanged();

                }

                mMistakeReasonTv.setText(mRecordItemBean.getRemark());
            }
        });
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

                realTime = year + "-" + month + "-" + day + "  00:00:00";
                mRecordTimeTv.setText(year + "-" + month + "-" + day);
            }
        });
        mPicker.show();
    }

    String realTime = "";


    /**
     * select service name .
     */
    public void showStatusPicker() {
        OptionPicker picker = new OptionPicker(this,
                new String[]{"在用", "禁用"});
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

                Glide.with(RecordAddActivity.this).load(bean.getUrl()).into(((VH_IMAGE_ITEM) holder).mIv);

                if (isFinished) {
                    ((VH_IMAGE_ITEM) holder).mDeleteIv.setVisibility(View.GONE);
                }

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

                if (mList.size() >= MAX_PIC_COUNT || isFinished) {
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
        } else if (requestCode == FragileActivity.REQUEST_CODE_RESULT && resultCode == RESULT_OK) {

            mFragList = data.getParcelableArrayListExtra(FragileActivity.TYPE_LIST);

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

    public void changeConfirm() {
        if (TextUtils.isEmpty(mRecordDeviceNameTv.getText().toString())) {
            Utils.showShortToast(RecordAddActivity.this, "请填写名称.");
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

        commitImage(true);

    }


    public void addConfirm() {

        if (TextUtils.isEmpty(mRecordDeviceNameTv.getText().toString())) {
            Utils.showShortToast(RecordAddActivity.this, "请填写名称.");
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

        commitImage(false);
    }

    private void commitImage(final boolean isUpdate) {

        if (mWaitingDialog != null) {
            mWaitingDialog.show("正在提交...", false);
        }

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
            if (isUpdate) {
                realStartUpdate(new ArrayList<String>());
            } else {
                realStartCommit(new ArrayList<String>());
            }
            return;
        }

        for (Map.Entry entry : map.entrySet()) {
            requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
        }

        Request request = new Request.Builder().url(LoginApi.URL + "/image/upload").post(requestBody.build()).tag(RecordAddActivity.this).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000 * 100, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, final IOException e) {

                RecordAddActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (mWaitingDialog != null) {
                            mWaitingDialog.dismiss();
                        }

                        Utils.showShortToast(RecordAddActivity.this, "图片上传失败！:" + e.getLocalizedMessage());
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

                            if (isUpdate) {
                                realStartUpdate(list);
                            } else {
                                realStartCommit(list);
                            }
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Utils.showShortToast(RecordAddActivity.this, "图片上传失败！");
                            Utils.finishWithoutAnim(RecordAddActivity.this);
                        }
                    });
                }
            }
        });
    }

    private void updateImageList(ArrayList<String> imageList) {

        if (mImageList.size() <= 0) {
            return;
        }

        for (int i = 0; i < mImageList.size(); i++) {
            String item = mImageList.get(i).getUrl();
            if (item.startsWith("http")) {
                imageList.add(item);
            }
        }
    }


    private void realStartUpdate(ArrayList<String> imageList) {

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("id", mRecordItemBean.getId());
        params.put("name", mRecordDeviceNameTv.getText().toString());
        params.put("brandName", mRecordBrandNameTv.getText().toString());
        params.put("model", mRecordTypeTv.getText().toString());
        params.put("productionTime", realTime);
        params.put("remark", mMistakeReasonTv.getText().toString());

        updateImageList(imageList);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < imageList.size(); i++) {
            if (i < imageList.size() - 1) {
                builder.append(imageList.get(i) + ",");
            } else {
                builder.append(imageList.get(i));
            }
        }


        params.put("imgUrls", builder.toString());

        putPartial(params);

        boolean isDisable = mRecordStatusTv.getText().toString().equals("禁用");

        params.put("status", isDisable ? "0" : "1");

        Call<JsonObject> call = recordApi.updateInfo(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (mWaitingDialog != null) {
                    mWaitingDialog.dismiss();
                }

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    final JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();


                    if (code == 0) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(RecordAddActivity.this, "提交成功");
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
                        if (mWaitingDialog != null) {
                            mWaitingDialog.dismiss();
                        }
                        Toast.makeText(RecordAddActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    private void realStartCommit(ArrayList<String> imageList) {

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
        params.put("productionTime", realTime);
        params.put("remark", mMistakeReasonTv.getText().toString());

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < imageList.size(); i++) {
            if (i < imageList.size() - 1) {
                builder.append(imageList.get(i) + ",");
            } else {
                builder.append(imageList.get(i));
            }
        }

        params.put("imgUrls", builder.toString());

        putPartial(params);

        boolean isDisable = mRecordStatusTv.getText().toString().equals("禁用");

        params.put("status", isDisable ? "0" : "1");

        Call<JsonObject> call = recordApi.addDevice(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (mWaitingDialog != null) {
                    mWaitingDialog.dismiss();
                }

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

                if (mWaitingDialog != null) {
                    mWaitingDialog.dismiss();
                }


                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RecordAddActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void putPartial(Map<String, String> params) {

        if (mFragList == null || mFragList.size() <= 0) {
            return;
        }

        for (int i = 0; i < mFragList.size(); i++) {

            String namekey = "partList[" + i + "].name";
            String urlkey = "partList[" + i + "].imgUrls";

            String nameValue = mFragList.get(i).getFragileName();

            params.put(namekey, nameValue);

            ArrayList<String> urls = mFragList.get(i).getUrls();

            StringBuilder urlValue = new StringBuilder();

            for (int j = 0; j < urls.size(); j++) {
                if (j < urls.size() - 1) {
                    urlValue.append(urls.get(j) + ",");
                } else {
                    urlValue.append(urls.get(j));
                }
            }

            params.put(urlkey, urlValue.toString());


        }


    }


}

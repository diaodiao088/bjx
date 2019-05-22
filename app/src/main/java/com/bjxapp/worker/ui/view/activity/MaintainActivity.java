package com.bjxapp.worker.ui.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.EnterpriseApi;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.MainTainBean;
import com.bjxapp.worker.model.MaintainInfo;
import com.bjxapp.worker.model.OtherPriceBean;
import com.bjxapp.worker.model.ThiInfoBean;
import com.bjxapp.worker.model.ThiOtherBean;
import com.bjxapp.worker.ui.view.activity.order.CompressUtil;
import com.bjxapp.worker.ui.view.activity.order.ImageOrderActivity;
import com.bjxapp.worker.ui.view.activity.order.OrderDetailActivityNew;
import com.bjxapp.worker.ui.view.activity.widget.SpaceItemDecoration;
import com.bjxapp.worker.ui.view.activity.widget.dialog.AddOtherPriceDialog;
import com.bjxapp.worker.ui.view.activity.widget.dialog.ManfulDialog;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.MaintainItemLayout;
import com.bjxapp.worker.ui.widget.MaintainItemOtherLayout;
import com.bjxapp.worker.ui.widget.OtherPriceLayout;
import com.bjxapp.worker.ui.widget.RoundImageView;
import com.bjxapp.worker.utils.DateUtils;
import com.bjxapp.worker.utils.SDCardUtils;
import com.bjxapp.worker.utils.UploadFile;
import com.bjxapp.worker.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
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
import cn.qqtheme.framework.picker.DoublePicker;
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

public class MaintainActivity extends Activity {

    private ArrayList<String> mMalfulList = new ArrayList<>();

    private ArrayList<String> mXieTiaoList = new ArrayList<>();


    @BindView(R.id.title_text_tv)
    TextView mTitleTv;

    @BindView(R.id.tuichi_time_tv)
    TextView mTuichiTimeTv;

    @OnClick(R.id.title_image_back)
    void onClickBack() {
        onBackPressed();
    }

    @OnClick(R.id.order_receive_detail_save)
    void onClickXTSuccess() {
        commitImage(false);
    }

    @Override
    public void onBackPressed() {

        if (mContentLy.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
        } else {
            toDiffStatus(true);
        }
    }

    @BindView(R.id.xietiao_ly)
    RelativeLayout mXieTiaoLy;

    @BindView(R.id.xietiao_reason_tv)
    TextView mXieTiaoReasonTv;

    String xietiaoName = "";

    @OnClick(R.id.xietiao_reason_tv)
    void onClickXieTiao() {
        if (mXieTiaoList.size() <= 0) {
            return;
        }

        ManfulDialog manfulDialog = new ManfulDialog(this);

        manfulDialog.setData(mXieTiaoList);

        manfulDialog.mTitleTv.setText("协调原因");

        manfulDialog.setClickListener(new ManfulDialog.OnManClickListener() {
            @Override
            public void onClick(String name, int index) {
                mXieTiaoReasonTv.setText(name);
                xietiaoName = name;
            }
        });

        manfulDialog.show();
    }

    @BindView(R.id.content_ly)
    LinearLayout mContentLy;

    @BindView(R.id.malfun_tv)
    TextView mManfulTv;

    @BindView(R.id.scroll_view)
    ScrollView mScrollView;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.add_other_price_ly)
    LinearLayout mAddOtherPriceLy;

    @BindView(R.id.other_price_ly)
    LinearLayout mOtherContainerLy;

    @OnClick(R.id.add_other_price_ly)
    void onAddOtherPrice() {

        final AddOtherPriceDialog otherPriceDialog = new AddOtherPriceDialog(this);

        otherPriceDialog.setCancelable(true);

        otherPriceDialog.setOnNegativeListener("取消", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otherPriceDialog.dismiss();
            }
        }).setOnPositiveListener("确认", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!otherPriceDialog.isDataValid()) {
                    Toast.makeText(MaintainActivity.this, "请填写完整数据", Toast.LENGTH_SHORT).show();
                } else {
                    OtherPriceBean validBean = otherPriceDialog.getValidBean();
                    mOtherPriceList.add(validBean);
                    addOtherUi(validBean);
                    calTotalCount();
                    otherPriceDialog.dismiss();
                }
            }
        });

        otherPriceDialog.show();

    }

    private String equipId;
    private String orderId;
    private String enterPriseOrderId;

    public ArrayList<MainTainBean> mMainTainList = new ArrayList<>();

    private ArrayList<ImageBean> mImageList = new ArrayList<>();

    private ArrayList<OtherPriceBean> mOtherPriceList = new ArrayList<>();

    @OnClick(R.id.malfun_tv)
    void onClickManuTv() {

        if (mMalfulList.size() <= 0) {
            return;
        }

        ManfulDialog manfulDialog = new ManfulDialog(this);

        manfulDialog.setData(mMalfulList);

        manfulDialog.setClickListener(new ManfulDialog.OnManClickListener() {
            @Override
            public void onClick(String name, int index) {
                mManfulTv.setText(name);
            }
        });

        manfulDialog.show();
    }

    @OnClick(R.id.add_thi_ly)
    void onAddThiClick() {
        ThiActivity.goToActivityForResult(this, equipId);
    }

    @BindView(R.id.change_reason_tv)
    EditText mMethodTv;

    @BindView(R.id.content_limit)
    TextView mLimitTv;

    @BindView(R.id.main_container_ly)
    LinearLayout mContainerLy;

    @BindView(R.id.total_price_tv)
    TextView mTotalPriceTv;

    @OnClick(R.id.wait_contact_ok_btn)
    void onClickComplete() {

        if (Double.parseDouble(getTotalPrice()) > 500) {
            Toast.makeText(this, "订单金额大于500 ，请点击需要协调", Toast.LENGTH_SHORT).show();
        } else {
            commitImage(true);
        }

    }

    @OnClick(R.id.wait_contact_change_btn)
    void onClickSettle() {
        toDiffStatus(false);
    }

    @OnClick(R.id.xietiao_time_tv)
    void onChangeTime() {
        changeDate();
    }

    @BindView(R.id.xietiao_time_tv)
    TextView mTimeTv;

    private ArrayList<String> getTimeList(String[] array) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }
        return list;
    }

    private boolean isChangeTime = false;

    private String day = "";
    private String time = "";

    void changeDate() {
        final ArrayList<String> firstData = new ArrayList<>();
        firstData.add(DateUtils.addDay(0));
        firstData.add(DateUtils.addDay(1));
        firstData.add(DateUtils.addDay(2));
        firstData.add(DateUtils.addDay(3));

//        final ArrayList<String> secondData = new ArrayList<>();
//        secondData.add("08:00:00--11:00:00");
//        secondData.add("11:00:00--14:00:00");
//        secondData.add("14:00:00--17:00:00");
//        secondData.add("17:00:00--20:00:00");
        final ArrayList<String> secondData = getTimeList(new String[]{"00:00:00-01:00:00", "01:00:00-02:00:00",
                "02:00:00-03:00:00", "03:00:00-04:00:00",
                "04:00:00-05:00:00", "05:00:00-06:00:00",
                "06:00:00-07:00:00", "07:00:00-08:00:00",
                "08:00:00-09:00:00", "09:00:00-10:00:00",
                "11:00:00-12:00:00", "12:00:00-13:00:00",
                "13:00:00-14:00:00", "14:00:00-15:00:00",
                "15:00:00-16:00:00", "16:00:00-17:00:00",
                "17:00:00-18:00:00", "18:00:00-19:00:00",
                "19:00:00-20:00:00", "20:00:00-21:00:00",
                "21:00:00-22:00:00", "22:00:00-23:00:00",
                "23:00:00-24:00:00"});

        final DoublePicker picker = new DoublePicker(this, firstData, secondData);
        picker.setDividerVisible(true);

        Calendar startTime = Calendar.getInstance();

        int selectFirstIndex = 0;
        int selectSecondIndex = 0;

//        if (startTime.get(Calendar.HOUR_OF_DAY) < 8) {
//            selectSecondIndex = 0;
//        } else if (startTime.get(Calendar.HOUR_OF_DAY) < 11) {
//            selectSecondIndex = 1;
//        } else if (startTime.get(Calendar.HOUR_OF_DAY) < 14) {
//            selectSecondIndex = 2;
//        } else if (startTime.get(Calendar.HOUR_OF_DAY) < 17) {
//            selectSecondIndex = 3;
//        } else {
//            selectFirstIndex = 1;
//        }

        picker.setSelectedIndex(selectFirstIndex, selectSecondIndex);
        picker.setTextSize(12);
        picker.setContentPadding(15, 10);
        picker.setOnPickListener(new DoublePicker.OnPickListener() {
            @Override
            public void onPicked(int selectedFirstIndex, int selectedSecondIndex) {

                isChangeTime = true;

                mTimeTv.setText(firstData.get(selectedFirstIndex) + " " + secondData.get(selectedSecondIndex));
                mTimeTv.setTextColor(Color.parseColor("#545454"));

                mTuichiTimeTv.setText("协调天数" + selectedFirstIndex + "天");

                day = firstData.get(selectedFirstIndex);
                time = secondData.get(selectedSecondIndex);
            }
        });
        picker.show();
    }

    private XWaitingDialog mWaitingDialog;

    private String plan;
    private String fault;

    private void toDiffStatus(boolean isMain) {
        if (isMain) {
            mContentLy.setVisibility(View.VISIBLE);
            mXieTiaoLy.setVisibility(View.GONE);
            mTitleTv.setText("维修项");
        } else {
            mContentLy.setVisibility(View.GONE);
            mXieTiaoLy.setVisibility(View.VISIBLE);
            mTitleTv.setText("选择协调分类");
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_select);
        ButterKnife.bind(this);
        initView();
        handleIntent();
        initData();
    }

    private void initData() {
        getDicList();
        getDicList1();
    }

    private void handleIntent() {

        equipId = getIntent().getStringExtra("equip_id");
        orderId = getIntent().getStringExtra("order_id");
        plan = getIntent().getStringExtra("plan");
        fault = getIntent().getStringExtra("fault");
        mMainTainList = getIntent().getParcelableArrayListExtra("maintainList");
        enterPriseOrderId = getIntent().getStringExtra("enter_order_id");



        if (mMainTainList == null) {
            mMainTainList = new ArrayList<>();
        }

        refreshUi();
    }

    private void refreshUi() {

        mMethodTv.setText(plan);
        mManfulTv.setText(fault);

        for (int i = 0; i < mMainTainList.size(); i++) {
            addUi(mMainTainList.get(i));
        }
    }

    private void getDicList() {

        EnterpriseApi enterpriseApi = KHttpWorker.ins().createHttpService(LoginApi.URL, EnterpriseApi.class);

        Call<JsonObject> call = null;

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("type", "maintainFaultReason");

        call = enterpriseApi.getDicList(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    final JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {

                        JsonArray array = object.get("list").getAsJsonArray();

                        mMalfulList.clear();

                        for (int i = 0; i < array.size(); i++) {
                            JsonObject item = array.get(i).getAsJsonObject();
                            String value = item.get("value").getAsString();
                            mMalfulList.add(value);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void getDicList1() {

        EnterpriseApi enterpriseApi = KHttpWorker.ins().createHttpService(LoginApi.URL, EnterpriseApi.class);

        Call<JsonObject> call = null;

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("type", "maintainCoordinateReason");

        call = enterpriseApi.getDicList(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    final JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {

                        JsonArray array = object.get("list").getAsJsonArray();

                        mXieTiaoList.clear();

                        for (int i = 0; i < array.size(); i++) {
                            JsonObject item = array.get(i).getAsJsonObject();
                            String value = item.get("value").getAsString();
                            mXieTiaoList.add(value);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private GridLayoutManager mGridLayoutManager;

    private void initView() {

        mContentLy.setVisibility(View.VISIBLE);
        mXieTiaoLy.setVisibility(View.GONE);

        mWaitingDialog = new XWaitingDialog(this);
        mTitleTv.setText("维修项");

        mGridLayoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(4, 50, true));
        myAdapter = new MyAdapter();
        mRecyclerView.setAdapter(myAdapter);

        ImageBean bean = new ImageBean(ImageBean.TYPE_IMAGE, "");
        mImageList.add(bean);
        myAdapter.setList(mImageList);
        myAdapter.notifyDataSetChanged();

        mMethodTv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });


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
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0x10:

                    ThiOtherBean otherBean = data.getParcelableExtra("other");

                    if (specItem != null) {
                        specItem.updateData(otherBean);
                    }

                    calTotalCount();


                    break;


                case ThiActivity.REQUEST_CODE:

                    Intent intent = data;
                    boolean isOthers = intent.getBooleanExtra("type_other", false);
                    MainTainBean mainTainBean = new MainTainBean();

                    if (!isOthers) {
                        ThiInfoBean thiInfoBean = intent.getParcelableExtra("bean");
                        if (thiInfoBean != null) {
                            mainTainBean.setComponentId(thiInfoBean.getId());
                            double costReal = Double.parseDouble(thiInfoBean.getPrice()) + Double.parseDouble(thiInfoBean.getLaborCost());
                            mainTainBean.setCost(String.valueOf(costReal));
                            mainTainBean.setLaborCost(thiInfoBean.getLaborCost());
                            mainTainBean.setRengongCost(thiInfoBean.getPrice());
                            mainTainBean.setModel(thiInfoBean.getModel());
                            mainTainBean.setUnit(thiInfoBean.getUnit());
                            mainTainBean.setComponentName(thiInfoBean.getName());
                            mainTainBean.setOthers(false);
                            mainTainBean.setQuantity(1);
                        }
                        mMainTainList.add(mainTainBean);

                    } else {
                        mainTainBean.setOthers(true);
                        mainTainBean.setQuantity(1);
                        mainTainBean.setThiOtherBean((ThiOtherBean) data.getParcelableExtra("other"));
                        mMainTainList.add(mainTainBean);
                    }

                    addUi(mainTainBean);

                    break;
            }
        }

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


    private void addOtherUi(final OtherPriceBean otherPriceBean) {

        OtherPriceLayout layout = new OtherPriceLayout(this);
        layout.bindData(otherPriceBean, "");

        layout.setOperationListener(new OtherPriceLayout.OnOperationListener() {
            @Override
            public void onDelete(OtherPriceBean priceBean) {
                mOtherPriceList.remove(otherPriceBean);
                calTotalCount();
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, DimenUtils.dp2px(10, this), 0, 0);
        mOtherContainerLy.addView(layout, params);
        mScrollView.fullScroll(View.FOCUS_DOWN);
    }

    private MaintainItemOtherLayout specItem;

    private void addUi(MainTainBean mainTainBean) {

        if (mainTainBean.isOthers()) {
            final MaintainItemOtherLayout maintainItemLayout = new MaintainItemOtherLayout(this);
            maintainItemLayout.bindData(mainTainBean);

            maintainItemLayout.setOperationListener(new MaintainItemOtherLayout.OnOperationListener() {
                @Override
                public void onDelete(MainTainBean mainTainBean) {
                    mMainTainList.remove(mainTainBean);
                    calTotalCount();
                }

                @Override
                public void onUpdataUi(MaintainItemOtherLayout item, ThiOtherBean otherBean) {

                    specItem = item;

                    ThiOtherActivity.goToActivityForResult(MaintainActivity.this, "", true, 0x10, otherBean);
                }

                @Override
                public void onCountChange() {
                    calTotalCount();
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, DimenUtils.dp2px(10, this), 0, 0);
            mContainerLy.addView(maintainItemLayout, params);
            mScrollView.fullScroll(View.FOCUS_DOWN);
        } else {
            final MaintainItemLayout maintainItemLayout = new MaintainItemLayout(this);
            maintainItemLayout.bindData(mainTainBean);

            maintainItemLayout.setOperationListener(new MaintainItemLayout.OnOperationListener() {
                @Override
                public void onDelete(MainTainBean mainTainBean) {
                    mMainTainList.remove(mainTainBean);
                    calTotalCount();
                }

                @Override
                public void onPriceChange() {
                    calTotalCount();
                }

                @Override
                public void onCountChange() {
                    calTotalCount();
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, DimenUtils.dp2px(10, this), 0, 0);
            mContainerLy.addView(maintainItemLayout, params);
            mScrollView.fullScroll(View.FOCUS_DOWN);
        }


        calTotalCount();
    }


    private void calTotalCount() {

        mTotalPriceTv.setText("总报价：" + getTotalPrice() + "元");

    }

    private String getTotalPrice() {

        double price = 0l;

        for (int i = 0; i < mMainTainList.size(); i++) {

            MainTainBean item = mMainTainList.get(i);

            if (item.isOthers()) {

                ThiOtherBean thiOtherBean = item.getThiOtherBean();

                price += (item.getQuantity() * (Double.parseDouble(thiOtherBean.getRenGongCost())
                        + Double.parseDouble(thiOtherBean.getCost())));

            } else {
                if (!TextUtils.isEmpty(item.getCost())) {
                    price += (item.getQuantity() * Double.parseDouble(item.getCost()));
                }
            }

        }

        for (int i = 0; i < mOtherPriceList.size(); i++) {
            price += (Double.parseDouble(mOtherPriceList.get(i).getPrice()));
        }

        return getFormatPrice(price);
    }


    private String getFormatPrice(double price) {
        // DecimalFormat df = new DecimalFormat("#.00");
        return String.format("%.2f", price);
    }

    private Handler mHandler = new Handler();

    public void commitImage(final boolean isComplete) {

        if (TextUtils.isEmpty(mManfulTv.getText().toString())) {
            Toast.makeText(this, "请先选择故障原因", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isChangeTime && !isComplete) {
            Toast.makeText(this, "请先选择时间修改", Toast.LENGTH_SHORT).show();
            return;
        }

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

        Request request = new Request.Builder().url(LoginApi.URL + "/image/upload").post(requestBody.build()).tag(MaintainActivity.this).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000 * 100, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, final IOException e) {

                MaintainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (mWaitingDialog != null) {
                            mWaitingDialog.dismiss();
                        }

                        Utils.showShortToast(MaintainActivity.this, "图片上传失败！:" + e.getLocalizedMessage());
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
                            startCommit(isComplete, list);
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Utils.showShortToast(MaintainActivity.this, "图片上传失败！");
                            Utils.finishWithoutAnim(MaintainActivity.this);
                        }
                    });
                }
            }
        });

    }

    public void startCommit(final boolean isComplete, ArrayList<String> list) {


        EnterpriseApi enterpriseApi = KHttpWorker.ins().createHttpService(LoginApi.URL, EnterpriseApi.class);

        Call<JsonObject> call = null;

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("orderId", orderId);

        params.put("operate", isComplete ? String.valueOf(1) : String.valueOf(0));

        params.put("fault", mManfulTv.getText().toString());
        params.put("plan", mMethodTv.getText().toString());


        if (!isComplete) {
            params.put("coordinateReason", xietiaoName);
            params.put("coordinateNextHandleStartTime", day + " " + time.split("-")[0]);
            params.put("coordinateNextHandleEndTime", day + " " + time.split("-")[1]);
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i < list.size() - 1) {
                builder.append(list.get(i) + ",");
            } else {
                builder.append(list.get(i));
            }
        }

        params.put("planImgUrls", builder.toString());

        putPartialList(params);

        putExtraCost(params);

        call = enterpriseApi.saveMainTain(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                mWaitingDialog.dismiss();

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    final JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {

                        final String mainTainPlanId = object.get("maintainPlanId").getAsString();

                        if (isComplete) {

                            // CompleteActivity.goToActivity(MaintainActivity.this, mainTainPlanId, orderId);
                            finish();

                            if (isDeviceBill_static && !isFinised_static){
                                FastJudgeActivity.goToActivity(MaintainActivity.this, false, enterPriseOrderId,
                                        equipId, imgList, orderId);
                            }

                            // CompleteActivity.goToActivity();

                        } else {
                            MaintainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MaintainActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }

                    } else {
                        MaintainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MaintainActivity.this, msg + ":" + code, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                MaintainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWaitingDialog.dismiss();
                        Toast.makeText(MaintainActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void putExtraCost(Map<String, String> params) {

        for (int i = 0; i < mOtherPriceList.size(); i++) {
            OtherPriceBean otherPriceBean = mOtherPriceList.get(i);

            String costName = "extraCostList[" + i + "].name";
            String amount = "extraCostList[" + i + "].amount";

            params.put(costName, otherPriceBean.getName());
            params.put(amount, otherPriceBean.getPrice());

        }
    }

    private void putPartialList(Map<String, String> params) {

        for (int i = 0; i < mMainTainList.size(); i++) {

            MainTainBean item = mMainTainList.get(i);

            ThiOtherBean otherBean = item.getThiOtherBean();

            String nameKey = "equipmentComponentList[" + i + "].name";
            String costKey = "equipmentComponentList[" + i + "].price";
            String quantityKey = "equipmentComponentList[" + i + "].quantity";
            String laborCost = "equipmentComponentList[" + i + "].laborCost";
            String remark = "equipmentComponentList[" + i + "].remark";
            String imgUrls = "equipmentComponentList[" + i + "].imgUrls";

            String modelKey = "equipmentComponentList[" + i + "].model";
            params.put(modelKey, !item.isOthers ? String.valueOf(item.getModel()) : otherBean.getModel());
            params.put(nameKey, !item.isOthers ? item.getComponentName() : otherBean.getName());
            params.put(laborCost, !item.isOthers ? String.valueOf(0) : otherBean.getRenGongCost());
            params.put(costKey, !item.isOthers ? item.getCost() : otherBean.getCost());
            params.put(remark, !item.isOthers ? "" : otherBean.getRemark());
            params.put(quantityKey, String.valueOf(item.getQuantity()));


            if (item.isOthers) {
                StringBuilder builder = new StringBuilder();
                for (int j = 0; j < otherBean.getImgList().size(); j++) {
                    if (i < otherBean.getImgList().size() - 1) {
                        builder.append(otherBean.getImgList().get(j) + ",");
                    } else {
                        builder.append(otherBean.getImgList().get(j));
                    }
                }

                if (!TextUtils.isEmpty(builder.toString())) {
                    params.put(imgUrls, builder.toString());
                }
            }


            if (!item.isOthers) {
                String idKey = "equipmentComponentList[" + i + "].id";
                params.put(idKey, String.valueOf(item.getComponentId()));
                String unitKey = "equipmentComponentList[" + i + "].unit";
                params.put(unitKey, item.getUnit());
            }
        }
    }

    public static boolean isDeviceBill_static = false;
    public static boolean isFinised_static = false;

    public static void goToActivity(Activity context, String equipId, String orderId, MaintainInfo maintainInfo, String enterpriseOrderId,
                                    boolean isDeviceBill , boolean isFinised) {

        Intent intent = new Intent();
        intent.setClass(context, MaintainActivity.class);
        intent.putExtra("equip_id", equipId);
        intent.putExtra("order_id", orderId);
        intent.putExtra("enter_order_id", enterpriseOrderId);

        intent.putExtra("plan", maintainInfo.getPlan());
        intent.putExtra("fault", maintainInfo.getFault());
        intent.putParcelableArrayListExtra("maintainList", maintainInfo.getmMaintainList());

        isDeviceBill_static = isDeviceBill;
        isFinised_static = isFinised;

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

                Glide.with(MaintainActivity.this).load(bean.getUrl()).into(((VH_IMAGE_ITEM) holder).mIv);

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
            ImageOrderActivity.goToActivity(MaintainActivity.this, bean.getUrl());
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
                                Utils.showShortToast(MaintainActivity.this, "SD卡被占用或不存在");
                            } else {
                                if (item == 0) {
                                    Uri imageURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                    if (imageURI != null) {
                                        try {
                                            if (ContextCompat.checkSelfPermission(MaintainActivity.this,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                                                    != PackageManager.PERMISSION_GRANTED) {
                                                ActivityCompat.requestPermissions(MaintainActivity.this,
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
                                    if (ContextCompat.checkSelfPermission(MaintainActivity.this,
                                            Manifest.permission.READ_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MaintainActivity.this,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(MaintainActivity.this,
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
                            Utils.showShortToast(MaintainActivity.this, "SD卡被占用或不存在");
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

package com.bjxapp.worker.ui.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.RecordApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.db.DBManager;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.ui.expandablelayout.ExpandableLayout;
import com.bjxapp.worker.ui.expandablelayout.Section;
import com.bjxapp.worker.ui.view.activity.bean.CheckDetailBean;
import com.bjxapp.worker.ui.view.activity.bean.RecordItemBean;
import com.bjxapp.worker.ui.view.activity.order.AddImageActivity;
import com.bjxapp.worker.ui.view.activity.order.CompressUtil;
import com.bjxapp.worker.ui.view.activity.order.ImageOrderActivity;
import com.bjxapp.worker.ui.view.activity.widget.SpaceItemDecoration;
import com.bjxapp.worker.ui.view.activity.widget.dialog.DeviceConfirmDialog;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.RoundImageView;
import com.bjxapp.worker.ui.widget.ServiceItemLayout;
import com.bjxapp.worker.utils.SDCardUtils;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.mask.ImageSelectUtil;
import com.bjxapp.worker.utils.mask.MaskFile;
import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bjxapp.worker.global.Constant.REQUEST_CODE_CLOCK_TAKE_PHOTO;

public class DeviceInfoActivity extends Activity {

    private DBManager mDbManager;

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @OnClick(R.id.device_info_tv)
    void onClickInfo() {

        RecordItemBean recordItemBean = new RecordItemBean();
        recordItemBean.setId(id);
        recordItemBean.setRecordStatus(2);

        RecordAddActivity.goToActivity(this, recordItemBean, "");
    }

    @BindView(R.id.add_confirm_btn)
    XButton mBtn;

    @BindView(R.id.add_img_tv)
    TextView mAddImgTv;

    @OnClick(R.id.add_confirm_btn)
    void onConfirm() {
        startCommit();
    }

    public static String model_static = "";

//    @OnClick(R.id.add_img_ly)
//    void onAddImage() {
//
//        AddImageActivity.goToActivity(this, AddImageActivity.OP_ADD, mImgList, !isNeedMod , true);
//    }

    private ArrayList<ImageBean> mImageList = new ArrayList<>();

    private MyAdapter mAdapter;


    private String id;

    private String realId;

    private String[] imgUrls;

    private Integer needMaintain;

    private String remark;

    private int status;

    private Integer situation;

    private ArrayList<ServiceItem> mList = new ArrayList<>();

    ArrayList<String> mImgList = new ArrayList<>();

    public static final String TYPE_ID = "type_id";
    public static final String IS_NEED_MOD = "is_need_Mod";
    public static final String TYPE_NUM = "type_num";

    @BindView(R.id.change_reason_tv)
    EditText mReasonTv;

    @BindView(R.id.content_limit)
    TextView mLimitTv;

    @BindView(R.id.need_maintain)
    RadioGroup mRadioGroup;

    @BindView(R.id.device_group)
    RadioGroup mDeviceRadioGroup;

    @BindView(R.id.service_process_ly)
    LinearLayout mServiceLy;

    @BindView(R.id.process_status_tv)
    TextView mProcessStatusTv;

    @BindView(R.id.expand_ly)
    ExpandableLayout mExpandLayout;

    @BindView(R.id.process_divider)
    View dividerView;

    @BindView(R.id.info_tv)
    TextView mInfoTv;

    @BindView(R.id.status_ly)
    LinearLayout mStatusLy;

    @BindView(R.id.process_sit_ly)
    LinearLayout mProcessSitLy;


    @OnClick(R.id.process_status_tv)
    void onStatusClick() {
        showStatusDialog();
    }


    private Handler mHandler = new Handler(Looper.getMainLooper());

    private XWaitingDialog mWaitingDialog;

    private boolean isNeedMod = true;
    private boolean isCheck = true;
    private boolean isFromBill = false;

    private boolean isFromScan = false;

    private String orderNum = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_detail_activity);
        ButterKnife.bind(this);
        id = getIntent().getStringExtra(TYPE_ID);
        isCheck = getIntent().getBooleanExtra("is_check", true);
        isFromBill = getIntent().getBooleanExtra("is_from_bill", false);
        isFromScan = getIntent().getBooleanExtra("is_from_scan", false);
        isNeedMod = getIntent().getBooleanExtra(IS_NEED_MOD, true);

        orderNum = getIntent().getStringExtra(TYPE_NUM);

        mDbManager = new DBManager(this);

        initView();
        initData();
    }

    private void initView() {

        mTitleTextView.setText("设备详情");

        if (isCheck) {
            mExpandLayout.setVisibility(View.GONE);
            mBtn.setText("巡检完成");
        } else {
            mExpandLayout.setVisibility(View.VISIBLE);
            mBtn.setText("保养完成");
        }

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(4, 50, true));
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);


        mReasonTv.addTextChangedListener(new TextWatcher() {
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

        mWaitingDialog = new XWaitingDialog(this);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.yes:
                        needMaintain = 1;
                        break;

                    case R.id.no:
                        needMaintain = 0;
                        break;
                }
            }
        });

        mDeviceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.must:
                        situation = 6;
                        break;

                    case R.id.recommend:
                        situation = 3;
                        break;
                }
            }
        });

        if (isNeedMod) {
            ImageBean bean = new ImageBean(ImageBean.TYPE_IMAGE, "");
            mImageList.add(bean);
            mAdapter.setList(mImageList);
            mAdapter.notifyDataSetChanged();
        }

        if (!isNeedMod && !isFromBill) {
            mBtn.setVisibility(View.GONE);
            mRadioGroup.setFocusable(false);
            mRadioGroup.setFocusableInTouchMode(false);

            mDeviceRadioGroup.setFocusable(false);
            mDeviceRadioGroup.setFocusableInTouchMode(false);

            mReasonTv.setFocusable(false);
            mReasonTv.setFocusableInTouchMode(false);

            mProcessStatusTv.setEnabled(false);
            mProcessStatusTv.setClickable(false);

            //mRecordStatusTv.setTextColor(Color.TRANSPARENT);


            mAddImgTv.setText("查看照片");


//            for (int i = 0; i < mRadioGroup.getChildCount(); i++) {
//                mRadioGroup.getChildAt(i).setEnabled(false);
//            }
//
//            for (int i = 0; i < mDeviceRadioGroup.getChildCount(); i++) {
//                mDeviceRadioGroup.getChildAt(i).setEnabled(false);
//            }
        } else if (isFromBill) {
            mBtn.setVisibility(View.GONE);
            mRadioGroup.setFocusable(false);
            mRadioGroup.setFocusableInTouchMode(false);

            mDeviceRadioGroup.setFocusable(false);
            mDeviceRadioGroup.setFocusableInTouchMode(false);

            mReasonTv.setFocusable(false);
            mReasonTv.setFocusableInTouchMode(false);

            mProcessStatusTv.setEnabled(false);
            mProcessStatusTv.setClickable(false);

            mExpandLayout.setVisibility(View.GONE);

            mDeviceRadioGroup.setVisibility(View.GONE);
            mProcessSitLy.setVisibility(View.GONE);
            dividerView.setVisibility(View.GONE);

            mStatusLy.setVisibility(View.GONE);

            // mRecordStatusTv.setTextColor(Color.TRANSPARENT);
            mAddImgTv.setText("查看照片");


//            for (int i = 0; i < mRadioGroup.getCh
        }

        mExpandLayout.setRenderer(new ExpandableLayout.Renderer<CategoryService, ServiceItemDes>() {

            @Override
            public void renderParent(View view, CategoryService model, boolean isExpanded, int parentPosition) {
                // ((TextView) view.findViewById(R.id.tvParent)).setText(model.name);
                view.findViewById(R.id.arrow).setBackgroundResource(isExpanded ? R.drawable.arrow_up : R.drawable.arrow_down);
            }

            @Override
            public void renderChild(View view, ServiceItemDes model, int parentPosition, int childPosition) {

                WebView webView = view.findViewById(R.id.tvChild);

                WebSettings wSet = webView.getSettings();
                wSet.setJavaScriptEnabled(true);
                wSet.setDefaultTextEncodingName("utf-8");

                webView.loadData(model.getServiceDes(), "text/html; charset=UTF-8", null);

            }
        });

        mExpandLayout.addSection(getSection());

    }


    CategoryService categoryService;

    private Section<CategoryService, ServiceItemDes> getSection() {

        Section<CategoryService, ServiceItemDes> section = new Section<>();

        categoryService = new CategoryService();
        categoryService.setName("服务步骤");

        section.parent = categoryService;

        section.expanded = false;

        return section;
    }


    private void initData() {

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Call<JsonObject> call = null;

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());

        if (isFromScan) {
            params.put("equipmentNo", orderNum);
            params.put("orderId", id);
            call = recordApi.getOrderEquip(params);
        } else {
            params.put("id", id);
            call = recordApi.getOrderEquipOld(params);
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    final JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {

                        parseData(object.get("equipment").getAsJsonObject());

                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(DeviceInfoActivity.this, msg + ":" + code);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private String deviceName = "";

    CheckDetailBean.DeviceBean deviceBean = null;


    private void parseData(JsonObject object) {

        id = object.get("equipmentId").getAsString();
        realId = object.get("id").getAsString();


        if (isComplete_static) {
            deviceBean = mDbManager.getSpecBean(realId);
        }

        deviceName = object.get("equipmentName").getAsString();

        if (object.get("needMaintain") != null && !(object.get("needMaintain") instanceof JsonNull)) {
            needMaintain = object.get("needMaintain").getAsInt();



        }

        if (deviceBean != null) {
            needMaintain = Integer.parseInt(deviceBean.getNeedMaintain());
        }

        if (object.get("remark") != null && !(object.get("remark") instanceof JsonNull)) {
            remark = object.get("remark").getAsString();
        }

        if (deviceBean != null) {
            remark = deviceBean.getRemark();
        }

        if (object.get("situation") != null && !(object.get("situation") instanceof JsonNull)) {
            situation = object.get("situation").getAsInt();

        }

        if (deviceBean != null) {
            situation = Integer.parseInt(deviceBean.getSituation());
        }

        JsonArray urlArray = object.get("imgUrls").getAsJsonArray();

        if (urlArray != null && urlArray.size() > 0) {
            for (int i = 0; i < urlArray.size(); i++) {
                String itemUrl = urlArray.get(i).getAsString();
                mImgList.add(itemUrl);
            }
        }

        if (deviceBean != null) {

            mImgList.clear();

            String imgUrls = deviceBean.getImgUrls();

            if (TextUtils.isEmpty(imgUrls)) {

                String[] urls = imgUrls.split(",");

                for (int i = 0; i < urls.length; i++) {
                    mImgList.add(urls[i]);
                }
            }
        }
        String[] serviceScore = null;
        if (deviceBean != null) {
            serviceScore = deviceBean.getScore().split(",");
        }

        JsonArray serviceArray = object.get("serviceProcessList").getAsJsonArray();

        for (int i = 0; i < serviceArray.size(); i++) {
            JsonObject item = serviceArray.get(i).getAsJsonObject();
            ServiceItem serviceItem = new ServiceItem();
            if (item.get("actualScore") != null && !(item.get("actualScore") instanceof JsonNull)) {
                serviceItem.setActualScore(item.get("actualScore").getAsString());
            }

            try {
                if (deviceBean != null && serviceScore != null) {
                    serviceItem.setActualScore(serviceScore[i]);
                }
            } catch (Exception e) {
            }


            serviceItem.setId(item.get("id").getAsString());
            serviceItem.setMaxScore(item.get("maxScore").getAsInt());
            serviceItem.setProcessName(item.get("processName").getAsString());
            mList.add(serviceItem);
        }

        if (object.get("stepList") != null) {
            JsonArray stepArray = object.get("stepList").getAsJsonArray();

            ArrayList<String> customImgUrls = new ArrayList<>();

            if (stepArray != null && stepArray.size() > 0) {
                for (int i = 0; i < stepArray.size(); i++) {
                    String itemUrl = stepArray.get(i).getAsString();
                    customImgUrls.add(itemUrl);
                }
            }

            steplist = customImgUrls;
        }

        updateUi();

    }

    ArrayList<String> steplist = new ArrayList<>();

    private void updateUi() {

        if (!TextUtils.isEmpty(deviceName)) {
            mInfoTv.setText(deviceName + " " + "设备信息");
        }

        model_static = deviceName;

        if (mImgList != null) {
            // mList.addAll(0 , mlist);
            ArrayList<ImageBean> temList = new ArrayList<>();
            for (int i = 0; i < mImgList.size(); i++) {
                ImageBean item = new ImageBean(ImageBean.TYPE_ADD, mImgList.get(i));
                temList.add(item);
            }
            mImageList.clear();
            mImageList.addAll(0, temList);

            if (mImageList.size() < 20) {
                ImageBean bean = new ImageBean(ImageBean.TYPE_IMAGE, "");
                mImageList.add(bean);

            }

            mAdapter.setList(mImageList);
            mAdapter.notifyDataSetChanged();

        }

        if (!TextUtils.isEmpty(remark)) {
            mReasonTv.setText(remark);
        }

        if (needMaintain != null) {
            if (needMaintain == 1) {
                mRadioGroup.check(R.id.yes);
            } else {
                mRadioGroup.check(R.id.no);
            }
        }

        if (situation != null) {
            if (situation == 0) {
                hideSituation();
            } else if (situation == 3) {
                showSituation();
                mDeviceRadioGroup.check(R.id.recommend);
            } else {
                showSituation();
                mDeviceRadioGroup.check(R.id.must);
            }
        }


        if (!isAllSelected()) {
            mProcessStatusTv.setText("选择");
            hideSituation();
        } else if (isAllMaxScore()) {
            mProcessStatusTv.setText("正常");
            hideSituation();
            needMaintain = 0;
        } else {
            mProcessStatusTv.setText("有异常");
            showSituation();
            needMaintain = 1;
        }

        if (mList.size() > 0) {
            mServiceLy.setVisibility(View.VISIBLE);
            mServiceLy.removeAllViews();
            updateServiceLy();
        } else {
            mServiceLy.setVisibility(View.GONE);
        }

        insertObj();
    }

    private void insertObj() {

        if (steplist == null || steplist.size() <= 0) {
            mExpandLayout.setVisibility(View.GONE);
            return;
        }

        for (int i = 0; i < steplist.size(); i++) {
            ServiceItemDes serviceItemDes = new ServiceItemDes();
            serviceItemDes.setIndex(String.valueOf(i + 1));
            serviceItemDes.setServiceDes(steplist.get(i));
            mExpandLayout.addChild(categoryService, serviceItemDes);
        }

    }

    private void updateServiceLy() {
        for (int i = 0; i < mList.size(); i++) {
            ServiceItem serviceItem = mList.get(i);
            ServiceItemLayout serviceItemLayout = new ServiceItemLayout(this);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    DimenUtils.dp2px(50, this));

            serviceItemLayout.bindData(i, serviceItem, isNeedMod, isAllMaxScore());

            mServiceLy.addView(serviceItemLayout, layoutParams);
        }
    }

    public static void goToActivity(Context context, String orderNum, boolean isNeedMod, boolean isCheck, boolean isFromBill, boolean fromScan, String order_id) {
        Intent intent = new Intent();
        intent.setClass(context, DeviceInfoActivity.class);
        intent.putExtra(TYPE_NUM, orderNum);
        intent.putExtra(TYPE_ID, order_id);
        intent.putExtra(IS_NEED_MOD, isNeedMod);
        intent.putExtra("is_check", isCheck);
        intent.putExtra("is_from_bill", isFromBill);
        intent.putExtra("is_from_scan", fromScan);

        context.startActivity(intent);
    }

    static boolean isComplete_static;

    public static void goToActivityForResult(Activity context, String deviceId, boolean flag, boolean isCheck, boolean isComplete) {
        Intent intent = new Intent();
        intent.setClass(context, DeviceInfoActivity.class);
        intent.putExtra(TYPE_ID, deviceId);
        intent.putExtra(IS_NEED_MOD, flag);
        intent.putExtra("is_check", isCheck);
        intent.putExtra("is_from_bill", false);

        isComplete_static = isComplete;

        context.startActivityForResult(intent, CheckOrderDetailActivity.REQUEST_CODE);
    }

    public static void goToActivity(Activity context, String deviceId, boolean flag, boolean isCheck) {
        goToActivity(context, deviceId, flag, isCheck, false);
    }

    public static void goToActivity(Context context, String deviceId, boolean flag, boolean isCheck, boolean isFromBill) {
        Intent intent = new Intent();
        intent.setClass(context, DeviceInfoActivity.class);
        intent.putExtra(TYPE_ID, deviceId);
        intent.putExtra(IS_NEED_MOD, flag);
        intent.putExtra("is_check", isCheck);
        intent.putExtra("is_from_bill", isFromBill);

        context.startActivity(intent);
    }

    public static class ServiceItem {

        String actualScore;

        String id;

        int maxScore;

        String processName;

        public String getActualScore() {
            return actualScore;
        }

        public void setActualScore(String actualScore) {
            this.actualScore = actualScore;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getMaxScore() {
            return maxScore;
        }

        public void setMaxScore(int maxScore) {
            this.maxScore = maxScore;
        }

        public String getProcessName() {
            return processName;
        }

        public void setProcessName(String processName) {
            this.processName = processName;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AddImageActivity.OP_ADD:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> list = data.getStringArrayListExtra("result");
                    if (list != null) {
                        mImgList.clear();
                        mImgList.addAll(list);

                        if (mImgList.size() > 0) {
                            String imgCountStr = getResources().getString(R.string.img_count, String.valueOf(mImgList.size()));
                        }
                    }
                }
                break;
        }


        if (requestCode == ImageSelectUtil.REQUEST_LIST_CODE) {
            if (resultCode == RESULT_OK && data != null) {

                List<String> pathList = data.getStringArrayListExtra("result");
                for (String path : pathList) {

                    if (mImageList.size() <= 20) {
                        insertImg(path, true);
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
            }
        }
    }

    public void insertImg(final String imagePath,
                          boolean showDelImg) {

        String targetPath = getCacheDir() + new File(imagePath).getName();
        final String compressImage = CompressUtil.compressImage(imagePath, targetPath, 30);
        ImageBean bean = new ImageBean(ImageBean.TYPE_ADD, compressImage);
        mImageList.add(0, bean);
        mAdapter.notifyDataSetChanged();

        MaskFile.addMask(compressImage, CheckOrderDetailActivity.currentAddress_static, CheckOrderDetailActivity.shopAddress_static,
                CheckOrderDetailActivity.enterpriseAddress_static, DeviceInfoActivity.model_static);

    }

    public void startCommit() {


        if (situation == null) {
            Toast.makeText(this, "请先选择设备状态", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isAllMaxScore() && situation == 0) {
            Toast.makeText(this, "请先选择设备状态", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!isAllChecked()) {
            Toast.makeText(this, "请先进行选择评分", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isExpectChecked()) {
            Toast.makeText(this, "请选择异常原因", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mImageList.size() <= 1) {
            Toast.makeText(this, "请添加照片", Toast.LENGTH_SHORT).show();
            return;
        }


        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mImageList.size(); i++) {
            if (!TextUtils.isEmpty(mImageList.get(i).getUrl())) {
                if (i < mImageList.size() - 1) {
                    builder.append(mImageList.get(i).getUrl() + ",");
                } else {
                    builder.append(mImageList.get(i).getUrl());
                }
            }
        }

        //params.put("imgUrls", builder.toString());

        mDbManager.addDeviceInfo(String.valueOf(realId), String.valueOf(situation), String.valueOf(needMaintain), remark, builder.toString(), getScoreStr(), getIdStr());

        setResult(RESULT_OK);
        finish();

//        Map<String, String> params = new HashMap<>();
//        params.put("token", ConfigManager.getInstance(this).getUserSession());
//        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
//        params.put("id", realId);
//        params.put("situation", String.valueOf((int) situation));
//        params.put("needMaintain", String.valueOf(needMaintain));
//
//        if (!TextUtils.isEmpty(mReasonTv.getText().toString())) {
//            params.put("remark", mReasonTv.getText().toString());
//        }
//
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0; i < mImgList.size(); i++) {
//            if (i < mImgList.size() - 1) {
//                builder.append(mImgList.get(i) + ",");
//            } else {
//                builder.append(mImgList.get(i));
//            }
//        }
//
//        params.put("imgUrls", builder.toString());
//
//        putPartial(params);
//
//        Call<JsonObject> call = recordApi.updateEquip(params);
//
//
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if (mWaitingDialog != null) {
//                    mWaitingDialog.dismiss();
//                }
//
//                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
//                    final JsonObject object = response.body();
//
//                    final String msg = object.get("msg").getAsString();
//                    final int code = object.get("code").getAsInt();
//
//                    if (code == 0) {
//                        Utils.showShortToast(DeviceInfoActivity.this, "提交成功");
//                        finish();
//                    } else {
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Utils.showShortToast(DeviceInfoActivity.this, msg + ":" + code);
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (mWaitingDialog != null) {
//                            mWaitingDialog.dismiss();
//                        }
//                        Toast.makeText(DeviceInfoActivity.this, "提交失败..", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });

    }

    private void hideSituation() {
        situation = 0;
        mDeviceRadioGroup.setVisibility(View.GONE);
        mProcessSitLy.setVisibility(View.GONE);
        dividerView.setVisibility(View.GONE);
    }

    private void showSituation() {
        mDeviceRadioGroup.setVisibility(View.VISIBLE);
        mProcessSitLy.setVisibility(View.VISIBLE);
        dividerView.setVisibility(View.VISIBLE);
    }

    private String getScoreStr() {

        if (mList == null || mList.size() <= 0) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < mList.size(); i++) {
            if (i < mList.size() - 1) {
                stringBuilder.append(mList.get(i).getActualScore() + ",");
            } else {
                stringBuilder.append(mList.get(i).getActualScore());
            }
        }

        return stringBuilder.toString();
    }


    private String getIdStr() {
        if (mList == null || mList.size() <= 0) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < mList.size(); i++) {
            if (i < mList.size() - 1) {
                stringBuilder.append(mList.get(i).getId() + ",");
            } else {
                stringBuilder.append(mList.get(i).getId());
            }
        }

        return stringBuilder.toString();
    }


    private boolean isAllChecked() {
        for (int i = 0; i < mList.size(); i++) {
            if (TextUtils.isEmpty(mList.get(i).getActualScore())) {
                return false;
            }
        }
        return true;
    }

    private boolean isExpectChecked() {

        if ("有异常".equals(mProcessStatusTv.getText().toString())) {
            if (isAllMaxScore()) {
                return false;
            }
        }

        return true;

    }


    private boolean isAllSelected() {
        for (int i = 0; i < mList.size(); i++) {
            if (TextUtils.isEmpty(mList.get(i).getActualScore())) {
                return false;
            }

        }
        return true;
    }

    private boolean isAllMaxScore() {
        for (int i = 0; i < mList.size(); i++) {
            if (TextUtils.isEmpty(mList.get(i).getActualScore())) {
                return false;
            }

            if (Integer.parseInt(mList.get(i).getActualScore()) != mList.get(i).getMaxScore()) {
                return false;
            }

        }
        return true;
    }

    private void showStatusDialog() {

        final DeviceConfirmDialog deviceConfirmDialog = new DeviceConfirmDialog(this);

        deviceConfirmDialog.setNormalListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAllAsNormal();
                hideSituation();
                deviceConfirmDialog.dismiss();
            }
        }).setUnNormalListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                needMaintain = 1;
                showSituation();
                updateAllAsUnNormal();
                deviceConfirmDialog.dismiss();

            }
        }).setCancelBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deviceConfirmDialog.dismiss();

            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                deviceConfirmDialog.dismiss();

            }
        }).show();

    }

    public void updateAllAsNormal() {
        needMaintain = 0;
        mProcessStatusTv.setText("正常");

        for (int i = 0; i < mServiceLy.getChildCount(); i++) {

            View view = mServiceLy.getChildAt(i);

            if (view instanceof ServiceItemLayout) {
                ((ServiceItemLayout) view).showAsNormal();
            }
        }
    }

    public void updateAllAsUnNormal() {

        mProcessStatusTv.setText("有异常");

        for (int i = 0; i < mServiceLy.getChildCount(); i++) {

            View view = mServiceLy.getChildAt(i);

            if (view instanceof ServiceItemLayout) {
                ((ServiceItemLayout) view).showAsSerNormal();
            }
        }
    }


    static class CategoryService {

        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    static class ServiceItemDes {

        String index;

        String serviceDes;

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getServiceDes() {
            return serviceDes;
        }

        public void setServiceDes(String serviceDes) {
            this.serviceDes = serviceDes;
        }
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

                Glide.with(DeviceInfoActivity.this).load(bean.getUrl()).into(((VH_IMAGE_ITEM) holder).mIv);

                if (!isNeedMod) {
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

                if (mList.size() >= 20 || !isNeedMod) {
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

    private boolean isFinished = false;


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
            ImageOrderActivity.goToActivity(DeviceInfoActivity.this, bean.getUrl());
        }
    };

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
                                Utils.showShortToast(DeviceInfoActivity.this, "SD卡被占用或不存在");
                            } else {
                                if (item == 0) {
                                    Uri imageURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                    if (imageURI != null) {
                                        try {
                                            if (ContextCompat.checkSelfPermission(DeviceInfoActivity.this,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                                                    != PackageManager.PERMISSION_GRANTED) {
                                                ActivityCompat.requestPermissions(DeviceInfoActivity.this,
                                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                                            } else {
//                                                Intent intent = new Intent(Intent.ACTION_PICK, imageURI);
//                                                startActivityForResult(intent, FEEDBACK_LOAD_IMAGES_RESULT);
                                                ImageSelectUtil.goToImageListActivity(DeviceInfoActivity.this, 0);
                                            }
                                        } catch (Exception e) {
                                            Log.w("FeedbackPresenter", "loadImages: " + e.getMessage());
                                        }
                                    }
                                } else {
                                    if (ContextCompat.checkSelfPermission(DeviceInfoActivity.this,
                                            Manifest.permission.READ_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(DeviceInfoActivity.this,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(DeviceInfoActivity.this,
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
                            Utils.showShortToast(DeviceInfoActivity.this, "SD卡被占用或不存在");
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

package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.RecordApi;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.ShopInfoBean;
import com.bjxapp.worker.ui.view.activity.bean.RecordBean;
import com.bjxapp.worker.ui.view.activity.bean.RecordItemBean;
import com.bjxapp.worker.ui.view.activity.category.CategoryDataManager;
import com.bjxapp.worker.ui.view.activity.map.MapPositioning;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.RecordItemLayout;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordDetailActivity extends Activity {

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @BindView(R.id.record_name)
    TextView mRecordNameTv;

    @BindView(R.id.record_phone)
    TextView mRecordPhoneTv;

    private XWaitingDialog mWaitingDialog;

    @OnClick(R.id.record_phone)
    void onClickPhone() {

        if (shopInfoBean == null || TextUtils.isEmpty(shopInfoBean.getContactNumber())) {
            return;
        }

        String mobile = shopInfoBean.getContactNumber();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + mobile));
        startActivity(intent);
    }

    @BindView(R.id.record_address)
    TextView mRecordAddrTv;

    @BindView(R.id.record_recycler_view)
    RecyclerView mRecyclerView;

    @OnClick(R.id.add_confirm_btn)
    void onClickConfirm() {
        submitRecordDetail();
    }

    private LinearLayoutManager mLayoutManager;

    private RecordAdapter mAdapter;

    public static final String SHOP_INFO = "shop_info";

    private ShopInfoBean shopInfoBean;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private ArrayList<RecordBean> mRecordList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_detail_activity);
        ButterKnife.bind(this);
        initView();
        handleIntent();
        initAddress();
        bindData();
    }

    private void initAddress() {
        MapPositioning mMapPositioning = MapPositioning.getInstance();
        mMapPositioning.setmLocation(new MapPositioning.XbdLocation() {

            @Override
            public void locSuccess(BDLocation location) {
                address_static = location.getAddrStr() + location.getLocationDescribe();
            }


            @Override
            public void locFailure(int errorType, String errorString) {

            }
        });
        mMapPositioning.start();
    }

    private void handleIntent() {

        Intent intent = getIntent();

        ShopInfoBean shopInfoBean = intent.getParcelableExtra(SHOP_INFO);

        this.shopInfoBean = shopInfoBean;

    }

    public static String shop_add_static = "";
    public static String enter_static = "";
    public static String address_static = "";

    private void bindData() {

        if (shopInfoBean == null) {
            finish();
        }

        mRecordNameTv.setText("门店：" + shopInfoBean.getEnterpriseName() + shopInfoBean.getName());

        shop_add_static = shopInfoBean.getName();
        enter_static = shopInfoBean.getEnterpriseName();

        mRecordAddrTv.setText("地址：" + shopInfoBean.getLocationAddress() + shopInfoBean.getDetailAddress());
        mRecordPhoneTv.setText(shopInfoBean.getContactPerson() + "/" + shopInfoBean.getContactNumber());
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestCategoryListIfNeed();
    }

    private void requestCategoryListIfNeed() {

        CategoryDataManager.getIns().loadDataIfNeed(new CategoryDataManager.OnCategoryLoadListener() {
            @Override
            public void onDataLoadSuccess() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateCategoryListUi();
                        requestRecordInfo();
                    }
                });
            }

            @Override
            public void onDataLoadFail() {

            }
        });
    }

    private void updateCategoryListUi() {

        mRecordList = CategoryDataManager.getIns().getCategoryListWithOutList();
        clearItemData();
        mAdapter.setItems(mRecordList);
    }


    private void clearItemData() {
        for (int i = 0; i < mRecordList.size(); i++) {
            ArrayList<RecordItemBean> itemList = mRecordList.get(i).getmItemList();
            itemList.clear();
        }
    }


    private void requestRecordInfo() {

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("shopId", shopInfoBean.getId());

        Call<JsonObject> call = recordApi.getRecordTypeInfo(params);

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
                                parseRecordData(object);
                            }
                        });
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(RecordDetailActivity.this, msg + ":" + code);
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

    private void parseRecordData(JsonObject mainObject) {

        JsonArray categoryArray = mainObject.getAsJsonArray("list");

        for (int i = 0; i < mRecordList.size(); i++) {

            mRecordList.get(i).getmItemList().clear();

        }

        for (int i = 0; i < categoryArray.size(); i++) {
            JsonObject object = categoryArray.get(i).getAsJsonObject();

            RecordItemBean recordItemBean = new RecordItemBean();

            recordItemBean.setBrandName(object.get("brandName").getAsString());
            recordItemBean.setParentId(object.get("parentCategoryId").getAsString());
            recordItemBean.setId(object.get("id").getAsString());
            recordItemBean.setCategoryId(object.get("categoryId").getAsString());
            recordItemBean.setEquipmentNo(object.get("equipmentNo").getAsString());
            recordItemBean.setModel(object.get("model").getAsString());

            recordItemBean.setName(object.get("name").getAsString());
            recordItemBean.setProductTime(object.get("productionTime").getAsString());
            recordItemBean.setRecordStatus(object.get("recordState").getAsInt());
            recordItemBean.setRemark(object.get("remark").getAsString());
            recordItemBean.setShopId(object.get("shopId").getAsString());
            recordItemBean.setEnableStatus(object.get("status").getAsInt());
            recordItemBean.setEnterId(object.get("enterpriseId").getAsString());
            addToSpecifiedParent(recordItemBean);
        }
        mAdapter.notifyDataSetChanged();
    }


    private void addToSpecifiedParent(RecordItemBean recordItemBean) {
        for (int i = 0; i < mRecordList.size(); i++) {

//            mRecordList.get(i).getmItemList().clear();

            if (recordItemBean.getParentId().equals(mRecordList.get(i).getTypeId())) {
                mRecordList.get(i).getmItemList().add(recordItemBean);
            }
        }
    }


    private void initView() {
        mTitleTextView.setText("录入详情");

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RecordAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(DimenUtils.dp2px(15, this)));

        mWaitingDialog = new XWaitingDialog(this);

    }

    public static void gotoActivity(Context context, ShopInfoBean shopInfoBean) {
        Intent intent = new Intent();
        intent.setClass(context, RecordDetailActivity.class);
        intent.putExtra(SHOP_INFO, shopInfoBean);
        context.startActivity(intent);
    }


    private class RecordAdapter extends RecyclerView.Adapter<RecordBaseHolder> {

        private ArrayList<RecordBean> mList = new ArrayList<>();

        @Override
        public RecordBaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item_layout, parent, false);

            return new RecordBaseHolder(view);
        }

        @Override
        public void onBindViewHolder(RecordBaseHolder holder, int position) {

            RecordBean recordBean = mList.get(position);

            holder.bindData(recordBean);
        }

        public void setItems(ArrayList<RecordBean> list) {
            this.mList = list;
            notifyDataSetChanged();
        }

        public void addSpecItem(RecordItemBean bean, String typeId) {

            for (RecordBean item : mList) {
                if (item.getTypeName().equals(typeId)) {
                    ArrayList<RecordItemBean> itemList = item.getmItemList();
                    itemList.add(bean);
                    break;
                }
            }

            notifyDataSetChanged();
        }


        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private class RecordBaseHolder extends RecyclerView.ViewHolder {

        private TextView mRecordTypeTv;
        private LinearLayout mRecordItemContainer;
        private TextView mPlusIv;

        public RecordBaseHolder(View itemView) {
            super(itemView);
            mRecordTypeTv = itemView.findViewById(R.id.type_name_tv);
            mRecordItemContainer = itemView.findViewById(R.id.record_item_container);
            mPlusIv = itemView.findViewById(R.id.plus);
        }

        public void bindData(final RecordBean recordBean) {

            if (!TextUtils.isEmpty(recordBean.getTypeName())) {
                mRecordTypeTv.setText(recordBean.getTypeName());
            }

            ArrayList<RecordItemBean> itemList = recordBean.getmItemList();

            if (itemList.size() > 0) {
                mRecordItemContainer.removeAllViews();
                mRecordItemContainer.setVisibility(View.VISIBLE);
                for (int i = 0; i < itemList.size(); i++) {
                    generateItemLayout(itemList.get(i));
                }
            } else {
                mRecordItemContainer.setVisibility(View.GONE);
            }

            mPlusIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onConstellationPicker(recordBean);
                }
            });

        }

        public void generateItemLayout(RecordItemBean itemBean) {


            RecordItemLayout itemLayout = new RecordItemLayout(mRecordItemContainer.getContext());

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    DimenUtils.dp2px(45, mRecordItemContainer.getContext()));

            itemLayout.bindData(itemBean, shopInfoBean.getId());

            mRecordItemContainer.addView(itemLayout, layoutParams);
        }

    }

    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        int mSpace;

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = mSpace;
        }

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }
    }


    /**
     * select service name .
     */
    public void onConstellationPicker(final RecordBean recordBean) {

        final ArrayList<RecordItemBean> itemList = CategoryDataManager.getIns().getSpecItemBean(recordBean);


        if (itemList.size() <= 0) {
            return;
        }

        OptionPicker picker = new OptionPicker(this,
                CategoryDataManager.getIns().getTypeString(itemList));
        picker.setTopBackgroundColor(0xFFffffff);
        picker.setTopHeight(30);
        picker.setTopLineColor(0xdfdfdf);
        picker.setTopLineHeight(3);
        picker.setTitleText("设备分类");
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
        picker.setCanceledOnTouchOutside(true);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
//                RecordItemBean itemBean = new RecordItemBean();
//                itemBean.setName(item);
//                itemBean.setStatus(0);
//                recordBean.getmItemList().add(itemBean);
//                mAdapter.notifyDataSetChanged();

                RecordAddActivity.goToActivityForResult(RecordDetailActivity.this, item, recordBean.getTypeId(),
                        shopInfoBean.getEnterpriseId(), shopInfoBean.getId(), itemList.get(index).getCategoryId(), getCurrentIndex(recordBean.getmItemList(), item));

            }
        });
        picker.show();
    }


    private int getCurrentIndex(ArrayList<RecordItemBean> list, String itemStr) {
        int index = 1;

        if (list.size() <= 0) {
            return index;
        }

        for (int i = 0; i < list.size(); i++) {

            RecordItemBean recordItemBean = list.get(i);

            if (recordItemBean.getName().startsWith(itemStr)) {
                index++;
            }

        }

        while (!isValidIndex(index, list, itemStr)) {
            index++;
        }


        return index;
    }


    private boolean isValidIndex(int currentIndex, ArrayList<RecordItemBean> list, String itemStr) {

        for (int i = 0; i < list.size(); i++) {

            RecordItemBean recordItemBean = list.get(i);

            if (recordItemBean.getName().equals(itemStr + "-" + currentIndex)) {
                return false;
            }

        }

        return true;
    }


    public void submitRecordDetail() {

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("shopId", shopInfoBean.getId());

        if (mWaitingDialog != null) {
            mWaitingDialog.show("正在提交中", false);
        }

        Call<JsonObject> call = recordApi.submitDevice(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mWaitingDialog != null) {
                            mWaitingDialog.dismiss();
                        }
                    }
                });

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    final JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(RecordDetailActivity.this, "操作成功");
                                requestCategoryListIfNeed();
                            }
                        });
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(RecordDetailActivity.this, msg + ":" + code);
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
                        Toast.makeText(RecordDetailActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

        }
    }
}

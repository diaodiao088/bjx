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

import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.RecordApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.ShopInfoBean;
import com.bjxapp.worker.ui.view.activity.bean.CheckDetailBean;
import com.bjxapp.worker.ui.widget.CheckOrderItemLayout;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOrderDetailActivity extends Activity {

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @BindView(R.id.time)
    TextView mTimeTv;

    @BindView(R.id.shop)
    TextView mShopTv;

    @BindView(R.id.address)
    TextView mAddressTv;

    @BindView(R.id.phone)
    TextView mPhoneTv;

    @OnClick(R.id.phone)
    void onClickPhone() {
        if (checkDetailBean == null) {
            return;
        }

        String mobile = checkDetailBean.getShopInfoBean().getContactNumber();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + mobile));
        startActivity(intent);
    }

    @OnClick(R.id.change_time_tv)
    void onChangeTime() {
        CheckChangeTimeActivity.startActivity(this, checkDetailBean.getTime(), checkDetailBean.getActualTime(), checkDetailBean.getId());
    }

    @BindView(R.id.change_time_tv)
    TextView mChangeTimeTv;

    @OnClick(R.id.add_confirm_btn)
    void onConfirm() {
        startConfirm();
    }

    @BindView(R.id.add_confirm_btn)
    XButton mConfirmBtn;

    private LinearLayoutManager mLayoutManager;

    @BindView(R.id.record_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.check_to_scan_tv)
    TextView mScanTv;

    @OnClick(R.id.scan_ly)
    void onClickScan() {

    }

    @BindView(R.id.name)
    TextView mTitleName;

    private String orderId;

    private CheckDetailBean checkDetailBean;

    private XWaitingDialog mWaitingDialog;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static final String TYPE_ID = "type_id";

    private int mCurrentType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_order_detail_activity);
        ButterKnife.bind(this);
        handleIntent();
        initView();
    }

    private void handleIntent() {

        Intent intent = getIntent();

        if (intent != null) {
            orderId = intent.getStringExtra(TYPE_ID);
            mCurrentType = intent.getIntExtra("type", 0);
        }
    }

    RecordAdapter recordAdapter;


    private void initView() {

        mTitleTextView.setText("订单详情");

        mLayoutManager = new LinearLayoutManager(this);
        recordAdapter = new RecordAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(recordAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(DimenUtils.dp2px(15, this)));

        if (mCurrentType == 0) {
            mTitleName.setText("门店巡检");
            mScanTv.setText("点击进行巡检");
        } else {
            mTitleName.setText("门店保养");
            mScanTv.setText("点击进行保养");
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());

        Call<JsonObject> call = recordApi.getCheckInfo(orderId, params);

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

                        parseData(object);

                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(CheckOrderDetailActivity.this, msg + ":" + code);
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
                        Toast.makeText(CheckOrderDetailActivity.this, "读取数据失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void parseData(JsonObject object) {

        checkDetailBean = new CheckDetailBean();

        JsonObject mainObj = object.get("order").getAsJsonObject();

        String day = mainObj.get("actualDay").getAsString();
        String actualTime = mainObj.get("actualTime").getAsString();
        String id = mainObj.get("id").getAsString();


        checkDetailBean.setTime(day);
        checkDetailBean.setId(id);
        checkDetailBean.setActualTime(actualTime);
        checkDetailBean.setProcessState(mainObj.get("processState").getAsInt());

        JsonArray categoryArray = mainObj.get("equipmentCategoryList").getAsJsonArray();

        for (int i = 0; i < categoryArray.size(); i++) {

            CheckDetailBean.CategoryBean categoryBean = new CheckDetailBean.CategoryBean();

            JsonObject categoryObj = categoryArray.get(i).getAsJsonObject();

            categoryBean.setId(categoryObj.get("id").getAsString());
            categoryBean.setName(categoryObj.get("name").getAsString());

            JsonArray deviceArray = categoryObj.get("equipmentList").getAsJsonArray();

            for (int j = 0; j < deviceArray.size(); j++) {
                JsonObject deviceObj = deviceArray.get(j).getAsJsonObject();

                CheckDetailBean.DeviceBean deviceBean = new CheckDetailBean.DeviceBean();

                deviceBean.setEquipName(deviceObj.get("equipmentName").getAsString());
                deviceBean.setId(deviceObj.get("id").getAsString());
                deviceBean.setStatus(deviceObj.get("status").getAsInt());

                categoryBean.getDeviceList().add(deviceBean);
            }


            checkDetailBean.getCategoryList().add(categoryBean);
        }

        JsonObject shopItem = mainObj.get("shop").getAsJsonObject();
        ShopInfoBean shopInfoBean = new ShopInfoBean();

        shopInfoBean.setDetailAddress(shopItem.get("locationAddress").getAsString());
        shopInfoBean.setEnterpriseName(shopItem.get("enterpriseName").getAsString());
        shopInfoBean.setName(shopItem.get("name").getAsString());

        String contactPerson = shopItem.get("contactPerson").getAsString();
        String contactNumber = shopItem.get("contactPhone").getAsString();

        shopInfoBean.setContactPerson(contactPerson);
        shopInfoBean.setContactNumber(contactNumber);

        checkDetailBean.setShopInfoBean(shopInfoBean);

        notifyDataChanged();
    }

    private void notifyDataChanged() {

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                mTimeTv.setText(checkDetailBean.getActualTime());
                mAddressTv.setText(checkDetailBean.getShopInfoBean().getDetailAddress());
                mPhoneTv.setText(checkDetailBean.getShopInfoBean().getContactPerson()
                        + "/" + checkDetailBean.getShopInfoBean().getContactNumber());
                mShopTv.setText(checkDetailBean.getShopInfoBean().getEnterpriseName()
                        + checkDetailBean.getShopInfoBean().getName());
                recordAdapter.setItems(checkDetailBean.getCategoryList());

                if (checkDetailBean.getProcessState() >= 3) {
                    mChangeTimeTv.setVisibility(View.GONE);
                }

                if (checkDetailBean.getProcessState() >= 6) {
                    mConfirmBtn.setVisibility(View.GONE);
                }

            }
        });

    }

    public static void goToActivity(Context context, String orderId, int type) {
        Intent intent = new Intent();
        intent.setClass(context, CheckOrderDetailActivity.class);
        intent.putExtra(TYPE_ID, orderId);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    private class RecordAdapter extends RecyclerView.Adapter<RecordBaseHolder> {

        private ArrayList<CheckDetailBean.CategoryBean> mList = new ArrayList<>();

        @Override
        public RecordBaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_order_item_layout, parent, false);

            return new RecordBaseHolder(view);
        }

        @Override
        public void onBindViewHolder(RecordBaseHolder holder, int position) {

            CheckDetailBean.CategoryBean recordBean = mList.get(position);

            holder.bindData(recordBean);
        }

        public void setItems(ArrayList<CheckDetailBean.CategoryBean> list) {
            this.mList = list;
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

        public RecordBaseHolder(View itemView) {
            super(itemView);
            mRecordTypeTv = itemView.findViewById(R.id.type_name_tv);
            mRecordItemContainer = itemView.findViewById(R.id.record_item_container);
        }

        public void bindData(final CheckDetailBean.CategoryBean checkBean) {

            if (!TextUtils.isEmpty(checkBean.getName())) {
                mRecordTypeTv.setText(checkBean.getName());
            }

            ArrayList<CheckDetailBean.DeviceBean> itemList = checkBean.getDeviceList();

            if (itemList.size() > 0) {
                mRecordItemContainer.removeAllViews();
                mRecordItemContainer.setVisibility(View.VISIBLE);
                for (int i = 0; i < itemList.size(); i++) {
                    generateItemLayout(itemList.get(i));
                }
            } else {
                mRecordItemContainer.setVisibility(View.GONE);
            }

        }

        public void generateItemLayout(CheckDetailBean.DeviceBean itemBean) {


            CheckOrderItemLayout itemLayout = new CheckOrderItemLayout(mRecordItemContainer.getContext());

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    DimenUtils.dp2px(45, mRecordItemContainer.getContext()));

            itemLayout.bindData(checkDetailBean.getProcessState(), itemBean, itemBean.getId(), mCurrentType == 0);

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

    private void startConfirm() {

        if (checkDetailBean == null) {
            return;
        }

        for (int i = 0; i < checkDetailBean.getCategoryList().size(); i++) {

            CheckDetailBean.CategoryBean categoryBean = checkDetailBean.getCategoryList().get(i);

            for (int j = 0; j < categoryBean.getDeviceList().size(); j++) {

                CheckDetailBean.DeviceBean deviceBean = categoryBean.getDeviceList().get(j);

                if (deviceBean.getStatus() == 0) {

                    Toast.makeText(this, "请先提交设备信息.", Toast.LENGTH_SHORT).show();

                    return;
                }

            }

        }

        if (mWaitingDialog != null) {
            mWaitingDialog.show("提交成功.", false);
        }

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("id", checkDetailBean.getId());

        Call<JsonObject> call = recordApi.submitOrder(params);

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
                        Utils.showShortToast(CheckOrderDetailActivity.this, "提交成功");
                        finish();
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(CheckOrderDetailActivity.this, msg + ":" + code);
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
                        Toast.makeText(CheckOrderDetailActivity.this, "修改失败..", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }

}

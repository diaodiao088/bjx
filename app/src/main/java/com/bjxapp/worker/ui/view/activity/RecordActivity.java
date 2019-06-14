package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.RecordApi;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.ShopInfoBean;
import com.bjxapp.worker.utils.ACache;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordActivity extends Activity {

    public static final String TYPE_STRING = "history_list";

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @BindView(R.id.record_num_tv)
    EditText mRecordTv;

    @BindView(R.id.history_view)
    RecyclerView mHistoryListView;

    @BindView(R.id.record_history_ly)
    LinearLayout mRecordHistoryLy;

    private ArrayList<HistoryBean> mHistoryList = new ArrayList<>();

    private LinearLayoutManager mLayoutManager;

    private MyAdapter myAdapter;

    private Handler mHandler = new Handler(Looper.getMainLooper());


    @OnClick(R.id.add_confirm_btn)
    void onClickConfirm() {

        if (TextUtils.isEmpty(mRecordTv.getText().toString())) {
            Toast.makeText(this, "请填写编号", Toast.LENGTH_SHORT).show();
            return;
        }

        requestRecordInfo(mRecordTv.getText().toString());
    }

    private void requestRecordInfo(final String serNum) {

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());

        Call<JsonObject> call = recordApi.getRecordInfo(serNum, params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {

                    JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {

                        JsonObject shopInfo = object.get("shop").getAsJsonObject();

                        String detailAddress = shopInfo.get("detailAddress").getAsString();
                        String enterpriseId = shopInfo.get("enterpriseId").getAsString();
                        String enterpriseName = shopInfo.get("enterpriseName").getAsString();
                        String id = shopInfo.get("id").getAsString();
                        String latitude = shopInfo.get("latitude").getAsString();
                        String longitude = shopInfo.get("longitude").getAsString();
                        String name = shopInfo.get("name").getAsString();
                        String shopNum = shopInfo.get("shopNo").getAsString();
                        String locationAddress = shopInfo.get("locationAddress").getAsString();
                        String serviceImgUrl = "";
                        try{
                            serviceImgUrl = shopInfo.get("serviceImgUrl").getAsString();
                        }catch (Exception e){}


                        String contactPerson = shopInfo.get("contactPerson").getAsString();
                        String contactNumber = shopInfo.get("contactPhone").getAsString();

                        ShopInfoBean shopInfoBean = new ShopInfoBean(detailAddress, enterpriseId, enterpriseName,
                                id, latitude, longitude, name, shopNum, locationAddress);

                        shopInfoBean.setContactNumber(contactNumber);
                        shopInfoBean.setContactPerson(contactPerson);
                        shopInfoBean.setServiceImgUrl(serviceImgUrl);

                        RecordDetailActivity.gotoActivity(RecordActivity.this, shopInfoBean);

                        insertCache(shopInfoBean, serNum);

                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(RecordActivity.this, msg + ":" + code);
                            }
                        });
                    }

                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RecordActivity.this, "编号错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RecordActivity.this, "编号错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTitleTextView.setText("设备录入");
        myAdapter = new MyAdapter();
        mLayoutManager = new LinearLayoutManager(this);
        mHistoryListView.setLayoutManager(mLayoutManager);
        mHistoryListView.setAdapter(myAdapter);
    }

    public static void gotoActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, RecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getHistoryCache();
    }

    private void getHistoryCache() {
        ACache aCache = ACache.get(this);
        ArrayList<HistoryBean> list = (ArrayList<RecordActivity.HistoryBean>) aCache.getAsObject(TYPE_STRING);

        if (list != null) {
            mHistoryList = list;
        }

        if (mHistoryList != null && mHistoryList.size() > 0) {
            mRecordHistoryLy.setVisibility(View.VISIBLE);
            myAdapter.notifyDataSetChanged();
        } else {
            mRecordHistoryLy.setVisibility(View.GONE);
        }

    }

    private void insertCache(ShopInfoBean shopInfoBean, String serNum) {

        HistoryBean historyBean = new HistoryBean(serNum, shopInfoBean.getLocationAddress(),
                shopInfoBean.getDetailAddress(), shopInfoBean.getEnterpriseName(), shopInfoBean.getName());

        ACache aCache = ACache.get(this);

        mHistoryList.remove(historyBean);
        mHistoryList.add(0, historyBean);

        aCache.remove(TYPE_STRING);

        aCache.put(TYPE_STRING, mHistoryList);
    }

    class MyAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);

            return new MyHolder(rootView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            HistoryBean historyBean = mHistoryList.get(position);

            if (holder instanceof MyHolder) {
                ((MyHolder) holder).setData(historyBean);
            }

        }

        @Override
        public int getItemCount() {
            return mHistoryList.size();
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {

        private View mRootView;

        private TextView mAddressTv, mEnterpriseTv;

        public MyHolder(View itemView) {
            super(itemView);

            this.mRootView = itemView;
            this.mAddressTv = mRootView.findViewById(R.id.address_tv);
            this.mEnterpriseTv = mRootView.findViewById(R.id.enterprise_name);

        }

        public void setData(final HistoryBean historyBean) {

            this.mEnterpriseTv.setText(historyBean.getEnterpriseName() + historyBean.getShopName());
            this.mAddressTv.setText(historyBean.getLocationAddress() + historyBean.getDetailAddress());

            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestRecordInfo(historyBean.getSerNum());
                }
            });
        }
    }

    public static class HistoryBean implements Serializable {

        private String serNum;

        private String locationAddress;

        private String detailAddress;

        private String enterpriseName;

        private String shopName;


        public HistoryBean(String serNum, String locationAddress, String detailAddress, String enterpriseName, String shopName) {
            this.serNum = serNum;
            this.locationAddress = locationAddress;
            this.detailAddress = detailAddress;
            this.enterpriseName = enterpriseName;
            this.shopName = shopName;
        }

        public String getSerNum() {
            return serNum;
        }

        public void setSerNum(String serNum) {
            this.serNum = serNum;
        }

        public String getLocationAddress() {
            return locationAddress;
        }

        public void setLocationAddress(String locationAddress) {
            this.locationAddress = locationAddress;
        }

        public String getDetailAddress() {
            return detailAddress;
        }

        public void setDetailAddress(String detailAddress) {
            this.detailAddress = detailAddress;
        }

        public String getEnterpriseName() {
            return enterpriseName;
        }

        public void setEnterpriseName(String enterpriseName) {
            this.enterpriseName = enterpriseName;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        @Override
        public boolean equals(Object obj) {

            if (obj instanceof HistoryBean) {
                return this.serNum.equals(((HistoryBean) obj).getSerNum());
            }

            return false;
        }
    }

}

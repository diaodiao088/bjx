package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.EnterpriseApi;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.ThiInfoBean;
import com.bjxapp.worker.ui.widget.DimenUtils;
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

public class ThiOtherActivity extends Activity {

    public static final int REQUEST_CODE = 0x01;

    public static final String TYPE_ID = "type_id";

    private String equipId;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.title_text_tv)
    TextView mTitleTv;

    @BindView(R.id.thi_name_ev)
    EditText mNameEv;

    @OnClick(R.id.title_image_back)
    void onClickBack() {
        onBackPressed();
    }

    private MyAdapter mAdapter;

    private ArrayList<ThiInfoBean> mAllList = new ArrayList<>();

    private ArrayList<ThiInfoBean> mSelectedList = new ArrayList<>();

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thi);
        ButterKnife.bind(this);
        initView();

        equipId = getIntent().getStringExtra(TYPE_ID);

        initData();
    }

    private void initData() {

        EnterpriseApi enterpriseApi = KHttpWorker.ins().createHttpService(LoginApi.URL, EnterpriseApi.class);

        Call<JsonObject> call = null;

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("equipmentId", equipId);

        call = enterpriseApi.getComponentList(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    final JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {

                        JsonArray array = object.get("list").getAsJsonArray();

                        mAllList.clear();
                        for (int i = 0; i < array.size(); i++) {
                            JsonObject item = array.get(i).getAsJsonObject();

                            ThiInfoBean infoBean = new ThiInfoBean();

                            infoBean.setCost(item.get("cost").getAsString());
                            infoBean.setId(item.get("id").getAsInt());
                            infoBean.setModel(item.get("model").getAsString());
                            infoBean.setName(item.get("name").getAsString());
                            infoBean.setUnit(item.get("unit").getAsString());
                            infoBean.setOther(false);

                            mAllList.add(infoBean);

                        }

                        refreshList();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


    private void refreshList() {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(mNameEv.getText().toString())) {
                    mSelectedList.clear();
                    for (int i = 0; i < mAllList.size(); i++) {
                        ThiInfoBean bean = mAllList.get(i);
                        if (bean.getName().contains(mNameEv.getText().toString())) {
                            mSelectedList.add(bean);
                        }
                    }
                    addOtherBean();
                    mAdapter.notifyDataSetChanged();
                } else {
                    mSelectedList.clear();
                    for (int i = 0; i < mAllList.size(); i++) {
                        ThiInfoBean bean = mAllList.get(i);
                        mSelectedList.add(bean);
                    }
                    addOtherBean();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void addOtherBean() {
        ThiInfoBean thiInfoBean = new ThiInfoBean();
        thiInfoBean.setOther(true);
        mSelectedList.add(thiInfoBean);
    }


    private void initView() {
        mTitleTv.setText("添加其他配件");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.addItemDecoration(new FragileActivity.SpaceItemDecoration(DimenUtils.dp2px(10, this)));

        addOtherBean();

        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mNameEv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    String name = s.toString();
                    mSelectedList.clear();
                    for (int i = 0; i < mAllList.size(); i++) {
                        ThiInfoBean bean = mAllList.get(i);
                        if (bean.getName().contains(name)) {
                            mSelectedList.add(bean);
                        }
                    }
                    addOtherBean();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    public static void goToActivityForResult(Activity context, String type_id) {
        Intent intent = new Intent();
        intent.setClass(context, ThiOtherActivity.class);
        intent.putExtra(TYPE_ID, type_id);
        context.startActivityForResult(intent, REQUEST_CODE);
    }


    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == 0x01) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_thi_item_layout, parent, false);
                return new OtherHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thi_item_layout, parent, false);
                return new MyHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {

            ThiInfoBean thiInfoBean = mSelectedList.get(position);

            if (!thiInfoBean.isOther()) {
                holder.setData(thiInfoBean);
            }

        }

        @Override
        public int getItemCount() {
            return mSelectedList.size();
        }

        @Override
        public int getItemViewType(int position) {

            ThiInfoBean infoBean = mSelectedList.get(position);

            if (infoBean.isOther()) {
                return 0x01;
            } else {
                return 0x02;
            }

        }
    }


    private class MyHolder extends RecyclerView.ViewHolder {

        private ThiInfoBean mInfoBean;

        private TextView mNameTv;

        private TextView mTypeTv;

        private View mRootView;

        public MyHolder(View itemView) {
            super(itemView);
            this.mRootView = itemView;
            this.mNameTv = mRootView.findViewById(R.id.type_name_tv);
            this.mTypeTv = mRootView.findViewById(R.id.type_tv);
        }

        public void setData(ThiInfoBean infoBean) {
            this.mInfoBean = infoBean;

            mNameTv.setText(infoBean.getName());
            mTypeTv.setText(infoBean.getModel());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("type_other", false);
                    intent.putExtra("bean", mInfoBean);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

        }
    }

    private class OtherHolder extends MyHolder {

        public OtherHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("type_other", true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }

        public void setData(ThiInfoBean infoBean) {

        }
    }


}

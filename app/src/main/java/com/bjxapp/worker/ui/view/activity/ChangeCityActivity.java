package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bjxapp.worker.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.RegisterApi;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.CityInfo;
import com.bjxapp.worker.ui.view.activity.bean.CityBean;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zhangdan on 2018/9/12.
 * <p>
 * comments:
 */
public class ChangeCityActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = ChangeCityActivity.class.getSimpleName();

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private RecyclerView mRecyclerView;
    private MyAdapter myAdapter;
    private View mTitleRootView;
    private XTextView mTitleTextView;

    @OnClick(R.id.title_image_back)
    void onClickBack() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_change_city);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void initControl() {

    }

    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        myAdapter = new MyAdapter();
        mRecyclerView.setAdapter(myAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ChangeCityActivity.this));
        mTitleRootView = findViewById(R.id.title_bar_root);
        mTitleTextView = (XTextView) findViewById(R.id.title_text_tv);
        mTitleTextView.setText("选择接单城市");
    }

    Call<JsonObject> getKeyRequest;

    ArrayList<CityInfo> mList = new ArrayList<>();

    @Override
    protected void initData() {

        RegisterApi httpService = KHttpWorker.ins().createHttpService(LoginApi.URL, RegisterApi.class);

        getKeyRequest = httpService.getCity();

        getKeyRequest.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {

                    JsonObject jsonObject = response.body();

                    Set<Map.Entry<String, JsonElement>> set = jsonObject.entrySet();

                    JsonObject cityJson = jsonObject.getAsJsonObject("list");

                    for (Map.Entry<String, JsonElement> entry : cityJson.entrySet()) {

                        String key = entry.getKey();

                        JsonArray cityArray = (JsonArray) entry.getValue();

                        for (int i = 0; i < cityArray.size(); i++) {
                            JsonObject item = (JsonObject) cityArray.get(i);

                            Log.d("slog_zd", "item : " + item.toString());

                            CityInfo cityBean = new CityInfo();
                            cityBean.setShowCategory(i == 0);
                            cityBean.setCategoryId(key);
                            cityBean.setCityName(item.get("name").getAsString());

                            mList.add(cityBean);
                        }
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.setDataModel(mList);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getKeyRequest != null && !getKeyRequest.isCanceled()) {
            getKeyRequest.cancel();
        }
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected String getPageName() {
        return TAG;
    }


    private class MyAdapter extends RecyclerView.Adapter {

        private ArrayList<CityInfo> mlist = new ArrayList<>();

        public void setDataModel(ArrayList<CityInfo> list) {
            this.mlist = list;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_change_city, parent, false);

            VH vh = new VH(itemView);

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((VH) holder).setData(mlist.get(position));
        }

        @Override
        public int getItemCount() {
            return mlist.size();
        }
    }

    private class VH extends RecyclerView.ViewHolder {

        private TextView mCategoryTv;
        private TextView mCityNameTv;
        private View mRootView;

        public VH(View itemView) {
            super(itemView);
            this.mCategoryTv = itemView.findViewById(R.id.change_city_category_id);
            this.mRootView = itemView;
            this.mCityNameTv = itemView.findViewById(R.id.change_city_name_tv);
        }

        public void setData(final CityInfo cityInfo) {
            if (cityInfo.isShowCategory()) {
                mCategoryTv.setVisibility(View.VISIBLE);
                mCategoryTv.setText(cityInfo.getCategoryId());
            } else {
                mCategoryTv.setText(cityInfo.getCategoryId());
                mCategoryTv.setVisibility(View.INVISIBLE);
            }
            mCityNameTv.setText(cityInfo.getCityName());

            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("city", cityInfo.getCityName());
                    setResult(RESULT_OK, intent);
                    Utils.finishWithoutAnim(ChangeCityActivity.this);
                }
            });
        }
    }

    public static void goToActivityForResult(Activity ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, ChangeCityActivity.class);
        ctx.startActivityForResult(intent, Constant.CONSULT_WORK_CITY);
        ctx.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}

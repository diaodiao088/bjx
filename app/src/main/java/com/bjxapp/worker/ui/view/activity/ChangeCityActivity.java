package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bjxapp.worker.R;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.model.CityInfo;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;

import java.util.ArrayList;

/**
 * Created by zhangdan on 2018/9/12.
 * <p>
 * comments:
 */
public class ChangeCityActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = ChangeCityActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private MyAdapter myAdapter;
    private View mTitleRootView;
    private XTextView mTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_change_city);
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
        mTitleRootView = findViewById(R.id.title_bar_root);
        mTitleTextView = (XTextView) findViewById(R.id.title_text_title);
        mTitleTextView.setText("选择接单城市");
    }

    @Override
    protected void initData() {

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
                mCategoryTv.setVisibility(View.GONE);
            }
            mCityNameTv.setText(cityInfo.getCityName());

            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("city" , cityInfo.getCityName());
                    setResult(RESULT_OK , intent);
                    Utils.finishWithoutAnim(ChangeCityActivity.this);
                }
            });
        }
    }

    public static void goToActivityForResult(Activity ctx){
        Intent intent = new Intent();
        intent.setClass(ctx , ChangeCityActivity.class);
        ctx.startActivityForResult(intent, Constant.CONSULT_WORK_CITY);
        ctx.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
    }
}

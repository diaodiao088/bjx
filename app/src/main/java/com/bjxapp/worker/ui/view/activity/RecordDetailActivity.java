package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.model.ShopInfoBean;
import com.bjxapp.worker.ui.view.activity.bean.RecordBean;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.RecordItemLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;

public class RecordDetailActivity extends Activity {

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @BindView(R.id.record_name)
    TextView mRecordNameTv;

    @BindView(R.id.record_address)
    TextView mRecordAddrTv;

    @BindView(R.id.record_recycler_view)
    RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;

    private RecordAdapter mAdapter;

    public static final String SHOP_INFO = "shop_info";

    private ShopInfoBean shopInfoBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_detail_activity);
        ButterKnife.bind(this);
        initView();
        handleIntent();
        bindData();
    }

    private void handleIntent() {

        Intent intent = getIntent();

        ShopInfoBean shopInfoBean = intent.getParcelableExtra(SHOP_INFO);

        this.shopInfoBean = shopInfoBean;

    }

    private void bindData() {

        if (shopInfoBean == null) {
            finish();
        }

        mRecordNameTv.setText("门店：" + shopInfoBean.getName());
        mRecordAddrTv.setText("地址：" + shopInfoBean.getDetailAddress());

        requestRecordInfo();


//        ArrayList<RecordBean> list = new ArrayList<>();
//
//        for (int i = 0; i < 10; i++) {
//            RecordBean recordBean = new RecordBean();
//            recordBean.setTypeName("录入详情：" + i);
//
//            ArrayList<RecordBean.RecordItemBean> list1 = new ArrayList<>();
//
//            for (int j = 0; j < 3; j++) {
//                RecordBean.RecordItemBean bean = recordBean.new RecordItemBean();
//                bean.setName("消毒柜：" + j);
//                list1.add(bean);
//            }
//
//            recordBean.setmItemList(list1);
//
//
//            list.add(recordBean);
//        }
//
//        mAdapter.setItems(list);
    }

    private void requestRecordInfo() {



    }

    private void initView() {
        mTitleTextView.setText("录入详情");

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RecordAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(DimenUtils.dp2px(15, this)));

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

        public void addSpecItem(RecordBean.RecordItemBean bean, String typeId) {

            for (RecordBean item : mList) {
                if (item.getTypeName().equals(typeId)) {
                    ArrayList<RecordBean.RecordItemBean> itemList = item.getmItemList();
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
        private ImageView mPlusIv;

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

            ArrayList<RecordBean.RecordItemBean> itemList = recordBean.getmItemList();

            if (itemList.size() > 0) {
                mRecordItemContainer.removeAllViews();
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

        public void generateItemLayout(RecordBean.RecordItemBean itemBean) {


            RecordItemLayout itemLayout = new RecordItemLayout(mRecordItemContainer.getContext());

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    DimenUtils.dp2px(45, mRecordItemContainer.getContext()));

            itemLayout.bindData(itemBean);

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
        OptionPicker picker = new OptionPicker(this,
                new String[]{
                        "洗碗机", "玻璃机", "和面机", "录音机", "路由器", "巨蟹座",
                        "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"});
        picker.setCycleDisable(false);//不禁用循环
        picker.setTopBackgroundColor(0xFFffffff);
        picker.setTopHeight(30);
        picker.setTopLineColor(0xdfdfdf);
        picker.setTopLineHeight(3);
        picker.setTitleText("服务名称");
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
        picker.setSelectedIndex(5);
        picker.setCanceledOnTouchOutside(true);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                RecordBean.RecordItemBean itemBean = recordBean.new RecordItemBean();
                itemBean.setName(item);
                itemBean.setStatus(0);
                recordBean.getmItemList().add(itemBean);
                mAdapter.notifyDataSetChanged();
            }
        });
        picker.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

        }
    }
}

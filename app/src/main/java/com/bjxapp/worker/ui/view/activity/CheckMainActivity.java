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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.RecordApi;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.ShopInfoBean;
import com.bjxapp.worker.ui.view.activity.bean.CheckBean;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckMainActivity extends Activity implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnMonthChangeListener,
        CalendarView.OnYearChangeListener,
        View.OnClickListener {

    @BindView(R.id.calendarView)
    CalendarView mCalendarView;

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @BindView(R.id.calendarLayout)
    CalendarLayout mCalendarLayout;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @OnClick(R.id.title_image_back)
    void onBack() {
        onBackPressed();
    }

    private XWaitingDialog mWaitingDialog;

    private MyAdapter mAdapter;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private ArrayList<CheckBean> mDataList = new ArrayList<>();

    private ArrayList<CheckBean> mAdapterList = new ArrayList<>();

    public static final int TYPE_CHECK = 0x00;
    public static final int TYPE_MAIN = 0x01;

    public static final String ACTIVITY_TYPE = "activity_type";

    private int mCurrentType;

    private int mYear;
    private int mMonth;
    private int mDay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_main_activity);
        ButterKnife.bind(this);

        mCurrentType = getIntent().getIntExtra(ACTIVITY_TYPE, TYPE_CHECK);
        initView();

        initCalendar();

        mYear = mCalendarView.getCurYear();
        mMonth = mCalendarView.getCurMonth();
        mDay = mCalendarView.getCurDay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData(mYear, mMonth, mDay);
    }

    private void initView() {

        mCalendarLayout = (CalendarLayout) findViewById(R.id.calendarLayout);
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnMonthChangeListener(this);
        mCalendarView.setOnCalendarSelectListener(this);

        mTitleTextView.setText(mCalendarView.getCurYear() + "年" + mCalendarView.getCurMonth() + "月");

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new MyAdapter();
        mAdapter.setmList(mAdapterList);

        mRecyclerView.addItemDecoration(new FragileActivity.SpaceItemDecoration(15));
        mRecyclerView.setAdapter(mAdapter);

        mWaitingDialog = new XWaitingDialog(this);
    }

    Call<JsonObject> mCall = null;

    /**
     * @param year
     * @param month
     */
    private void initData(final int year, final int month, final int day) {

        if (mWaitingDialog != null) {
            mWaitingDialog.show("正在加载", false);
        }

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("month", getFormatMonth(year, month));
        params.put("serviceType", String.valueOf(mCurrentType));

        if (mCall != null && !mCall.isCanceled()) {
            mCall.cancel();
        }

        mCall = recordApi.getCheckList(params);

        mCall.enqueue(new Callback<JsonObject>() {
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

                        JsonArray array = object.get("list").getAsJsonArray();

                        mDataList.clear();

                        for (int i = 0; i < array.size(); i++) {

                            JsonObject item = array.get(i).getAsJsonObject();

                            CheckBean checkBean = new CheckBean();
                            checkBean.setDay(item.get("actualDay").getAsString());
                            checkBean.setTime(item.get("actualTime").getAsString());
                            checkBean.setProcessStatus(item.get("processState").getAsInt());
                            checkBean.setServiceName(item.get("serviceName").getAsString());
                            checkBean.setOrderId(item.get("id").getAsString());


                            JsonObject shopItem = item.get("shop").getAsJsonObject();
                            ShopInfoBean shopInfoBean = new ShopInfoBean();
                            checkBean.setShopInfoBean(shopInfoBean);

                            shopInfoBean.setDetailAddress(shopItem.get("locationAddress").getAsString());
                            shopInfoBean.setEnterpriseName(shopItem.get("enterpriseName").getAsString());
                            shopInfoBean.setName(shopItem.get("name").getAsString());

                            mDataList.add(checkBean);
                        }

                        onDataChanged(year, month, day);

                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(CheckMainActivity.this, msg + ":" + code);
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
                        Toast.makeText(CheckMainActivity.this, "读取数据失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private String getFormatMonth(int year, int month) {
        return year + "-" + String.format("%02d", month) + "-" + "01 00:00:00";
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onYearChange(int year) {

    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mTitleTextView.setText(calendar.getYear() + "年" + calendar.getMonth() + "月");
        if (isClick) {
            mYear = calendar.getYear();
            mMonth = calendar.getMonth();
            mDay = calendar.getDay();
            onDataChanged(calendar.getYear(), calendar.getMonth(), calendar.getDay());
        } else {
            initData(calendar.getYear(), calendar.getMonth(), calendar.getDay());
        }

    }


    private void onDataChanged(int year, int month, int day) {

        mAdapterList.clear();
        CheckBean checkBean = new CheckBean();
        checkBean.setType(CheckBean.TYPE_TITLE);
        mAdapterList.add(checkBean);

        for (int i = 0; i < mDataList.size(); i++) {
            CheckBean checkBean1 = mDataList.get(i);
            if (checkBean1.isDateMatched(year, month, day)) {
                mAdapterList.add(checkBean1);
            }
        }

        mAdapter.notifyDataSetChanged();

        changeCalendar(year, month, day);
    }

    Map<String, Calendar> map = new HashMap<>();


    private void changeCalendar(int yearReal, int monthReal, int dayReal) {

        map.clear();

        ArrayList<CheckBean> hasBillList = getBillList();

        for (int i = 0; i < hasBillList.size(); i++) {

            CheckBean checkBean = hasBillList.get(i);

            map.put(getSchemeCalendar(checkBean.getYear(), checkBean.getMonth(), checkBean.getDays(), 0xffccf4d8, "记").toString(),
                    getSchemeCalendar(checkBean.getYear(), checkBean.getMonth(), checkBean.getDays(), 0xffccf4d8, "记"));

        }

        ArrayList<CheckBean> uncertList = getUncertList();

        for (int i = 0; i < uncertList.size(); i++) {

            CheckBean checkBean = uncertList.get(i);

            map.put(getSchemeCalendar(checkBean.getYear(), checkBean.getMonth(), checkBean.getDays(), 0xfffcdedd, "记").toString(),
                    getSchemeCalendar(checkBean.getYear(), checkBean.getMonth(), checkBean.getDays(), 0xfffcdedd, "记"));

        }


        initCalendar();

    }

    private ArrayList getUncertList() {
        ArrayList<CheckBean> tempList = new ArrayList<>();

        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).getProcessStatus() == 6) {
                tempList.add(mDataList.get(i));
            }
        }

        return tempList;
    }

    private ArrayList getBillList() {

        ArrayList<CheckBean> tempList = new ArrayList<>();

        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).getProcessStatus() != 6) {
                tempList.add(mDataList.get(i));
            }
        }

        return tempList;
    }

    private void initCalendar() {
        int year = mCalendarView.getCurYear();
        int month = mCalendarView.getCurMonth();
        int day = mCalendarView.getCurDay();

        mCalendarView.setSchemeDate(map);
    }


    public static void goToActivity(Context context, int type) {
        Intent intent = new Intent();
        intent.setClass(context, CheckMainActivity.class);
        intent.putExtra(ACTIVITY_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    public void onMonthChange(int year, int month) {

    }


    class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        private ArrayList<CheckBean> mList = new ArrayList<>();

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == 0x00) {
                View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_title, parent, false);
                return new MyHolder(root);
            } else {
                View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_item_layout, parent, false);
                return new MyHolder(root);
            }

        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {

            if (mList.size() > 0) {
                holder.setData(mList.get(position));
            }

        }

        @Override
        public int getItemViewType(int position) {

            CheckBean checkBean = mList.get(position);

            if (checkBean.getType() == CheckBean.TYPE_TITLE) {
                return 0x00;
            } else {
                return 0x01;
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setmList(ArrayList<CheckBean> list) {
            this.mList = list;
            notifyDataSetChanged();
        }
    }


    class MyHolder extends RecyclerView.ViewHolder {

        private View mRootView;

        private TextView mTimeTv;
        private TextView mAddressTv;
        private TextView mShopTv;

        private TextView mStatusTv;
        private TextView mNameTv;

        public MyHolder(View itemView) {
            super(itemView);
            mTimeTv = itemView.findViewById(R.id.time);
            mAddressTv = itemView.findViewById(R.id.address);
            mShopTv = itemView.findViewById(R.id.shop);
            mStatusTv = itemView.findViewById(R.id.status);
            mNameTv = itemView.findViewById(R.id.name);
            mRootView = itemView;
        }

        public void setData(final CheckBean checkBean) {

            if (checkBean.getType() != CheckBean.TYPE_TITLE) {

                mTimeTv.setText(checkBean.getTime());
                mAddressTv.setText(checkBean.getShopInfoBean().getDetailAddress());
                mShopTv.setText(checkBean.getShopInfoBean().getEnterpriseName() + checkBean.getShopInfoBean().getName());

                int status = checkBean.getProcessStatus();

                if (status == 0) {
                    mStatusTv.setText("待上门");
                } else if (status == 3) {
                    mStatusTv.setText("已上门");
                } else if (status == 6) {
                    mStatusTv.setText("待确认");
                } else if (status == 9) {
                    mStatusTv.setText("已完成");
                }

                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            CheckOrderDetailActivity.goToActivity(CheckMainActivity.this, checkBean.getOrderId(), mCurrentType);
                    }
                });

                if (mCurrentType == TYPE_CHECK) {
                    mNameTv.setText("门店巡检");
                } else {
                    mNameTv.setText("门店保养");
                }

            }
        }

    }


}

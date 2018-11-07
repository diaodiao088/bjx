package com.bjxapp.worker.ui.view.activity.order;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bjxapp.worker.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.BillApi;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.OrderDes;
import com.bjxapp.worker.model.OrderDetail;
import com.bjxapp.worker.model.OrderDetailInfo;
import com.bjxapp.worker.model.XResult;
import com.bjxapp.worker.ui.view.activity.PublicImagesActivity;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.DateUtils;
import com.bjxapp.worker.utils.HandleUrlLinkMovementMethod;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DoublePicker;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderDetailActivity extends BaseActivity implements OnClickListener {

    protected static final String TAG = OrderDetailActivity.class.getSimpleName();

    private int mOrderCode;

    private int mCurrentStatus;

    @BindView(R.id.title_image_back)
    XImageView mBackImageView;

    /* 通用状态 btn */
    @BindView(R.id.order_status_tv)
    XTextView mStatusTv;

    @BindView(R.id.save_ly)
    RelativeLayout mSaveLy;

    /* 新订单 */
    @BindView(R.id.bill_name)
    XTextView mServiceNameTv; // 维修项目
    @BindView(R.id.order_receive_textview_orderdate)
    XTextView mDateTv; // 日期
    @BindView(R.id.order_receive_textview_address)
    XTextView mAdressTv;
    @BindView(R.id.order_receive_textview_money)
    XTextView mPriceTv;
    @BindView(R.id.order_receive_textview_remark)
    XTextView mRemarkTv;
    @BindView(R.id.order_receive_textview_contact)
    XTextView mPhoneTv;
    @BindView(R.id.cancel_bill)
    TextView mCancelBillTv;

    /* 待预约 */
    @BindView(R.id.last_hour)
    XTextView mHourLastTv;
    @BindView(R.id.send_message_tv)
    TextView mSendMsgTv;

    @BindView(R.id.wait_contact_ok_btn)
    XButton mChangeDateOk;

    /* 订单详情 */
    @BindView(R.id.modify_strategy_ly)
    LinearLayout modifyLy;
    @BindView(R.id.issue_reason_tv)
    XTextView mIssueReasonTv;
    @BindView(R.id.strategy_content_tv)
    XTextView mStrategyContentTv; // 维修措施
    @BindView(R.id.price_content)
    XTextView mIssuePriceTv; // 维修报价
    @BindView(R.id.issue_edit_btn)
    XButton mServiceEditBtn;  // 维修编辑
    @BindView(R.id.add_image_content)
    XTextView mIssueImgTv;  // 添加维修照片

    /* 预付项 */
    @BindView(R.id.order_bill_btn)
    XButton preBillBtn;
    @BindView(R.id.pre_bill_tv)
    XTextView preBillContentTv;
    @BindView(R.id.order_bill_cash_content_tv)
    XTextView preBillCashTv;
    @BindView(R.id.order_bill_ly)
    LinearLayout mPreBillLy;

    /* 订单总和 */
    @BindView(R.id.final_money_ly)
    LinearLayout mFinalMoneyLy;
    @BindView(R.id.enter_room_content_tv)
    XTextView mEnterRoomPrice;
    @BindView(R.id.entire_price_content_tv)
    XTextView mTotalPriceTv;
    @BindView(R.id.total_content_tv)
    XTextView mTotalTv;
    @BindView(R.id.price_ready_content_tv)
    XTextView mPrePayPriceTv;
    @BindView(R.id.fukuan_content_tv)
    XTextView mFuKuanContentTv;

    /* 查看故障照片 */
    @BindView(R.id.order_receive_detail_images_text)
    XTextView mLookImageTv;
    @BindView(R.id.order_receive_detail_images)
    LinearLayout mOrderImagesLinear;

    ArrayList<String> mIDImageUrls = new ArrayList<>();

    @BindView(R.id.order_receive_detail_save)
    XButton mSaveButton;

    @BindView(R.id.order_receiver_ly)
    LinearLayout mOrderWaitLy;


    @OnClick(R.id.order_receive_textview_address)
    void onAddressClick() {

        if (mDetailInfo == null) {
            return;
        }

        try {
            if (isInstallByRead("com.autonavi.minimap")) {
                goToNaviActivity(getPageName(), mDetailInfo.getOrderDes().getLocationAddress(),
                        mDetailInfo.getOrderDes().getmLatitude(), mDetailInfo.getOrderDes().getmLongtitude(),
                        String.valueOf(0), String.valueOf(0));
            } else {
                Utils.showShortToast(this, "请安装高德地图或者自行打开");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToNaviActivity(String sourceApplication, String poiname, String lat, String lon, String dev, String style) {
        StringBuffer stringBuffer = new StringBuffer("androidamap://navi?sourceApplication=")
                .append(sourceApplication);
        if (!TextUtils.isEmpty(poiname)) {
            stringBuffer.append("&poiname=").append(poiname);
        }
        stringBuffer.append("&lat=").append(lat)
                .append("&lon=").append(lon)
                .append("&dev=").append(dev)
                .append("&style=").append(style);

        Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse(stringBuffer.toString()));
        intent.setPackage("com.autonavi.minimap");
        startActivity(intent);
    }

    public boolean isInstallByRead(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }


    private CountDownTimer mCountDownTimer;

    private XWaitingDialog mWaitingDialog;

    private OrderDetailInfo mDetailInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        handleIntent();
        super.onCreate(savedInstanceState);
    }

    @OnClick(R.id.order_receive_detail_images_text)
    void clickLookImage() {
        showUserImages();
    }

    @OnClick(R.id.order_receive_textview_address)
    void clickAddress() {

    }

    @OnClick(R.id.order_receive_textview_contact)
    void clickPhone() {
        callService();
    }

    /**
     * 取消订单
     */
    @OnClick(R.id.cancel_bill)
    void cancelBill() {
        CancelBillActivity.goToActivity(this);
    }

    /**
     * 预约点击确定
     */
    @OnClick(R.id.wait_contact_ok_btn)
    void changeContactOk() {
        toDetailStatus();
    }

    /**
     * 点击维修编辑
     */
    @OnClick(R.id.issue_edit_btn)
    void editIssueDetail() {
        ServiceBillActivity.goToActivity(this);
    }

    @OnClick(R.id.add_image_content)
    void addIssueImage() {
        AddImageActivity.goToActivity(this);
    }

    @OnClick(R.id.order_bill_btn)
    void editPreBill() {
        OrderPriceActivity.goToActivity(this);
    }


    @OnClick(R.id.wait_contact_change_btn)
    void changeDate() {
        final ArrayList<String> firstData = new ArrayList<>();
        firstData.add(DateUtils.addDay(0));
        firstData.add(DateUtils.addDay(1));
        firstData.add(DateUtils.addDay(2));
        firstData.add(DateUtils.addDay(3));
        firstData.add(DateUtils.addDay(4));
        firstData.add(DateUtils.addDay(5));
        firstData.add(DateUtils.addDay(6));
        firstData.add(DateUtils.addDay(7));
        firstData.add(DateUtils.addDay(8));
        firstData.add(DateUtils.addDay(9));

        final ArrayList<String> secondData = new ArrayList<>();
        secondData.add("8:00--11:00");
        secondData.add("11:00--14:00");
        secondData.add("14:00--17:00");
        secondData.add("17:00--20:00");
        final DoublePicker picker = new DoublePicker(this, firstData, secondData);
        picker.setDividerVisible(true);
        picker.setSelectedIndex(0, 0);
        picker.setTextSize(12);
        picker.setContentPadding(15, 10);
        picker.setOnPickListener(new DoublePicker.OnPickListener() {
            @Override
            public void onPicked(int selectedFirstIndex, int selectedSecondIndex) {
                // showToast(firstData.get(selectedFirstIndex) + " " + secondData.get(selectedSecondIndex));
            }
        });
        picker.show();
    }

    String orderId = "";
    int processStatus = -1;

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            orderId = intent.getStringExtra("order_id");
            processStatus = intent.getIntExtra("processStatus", -1);
        }
    }

    @Override
    protected void initControl() {
        initStatus();
        initTitle();
        mOrderImagesLinear.setVisibility(View.GONE);
        mOrderWaitLy.setVisibility(View.GONE);
        mHourLastTv.setVisibility(View.GONE);
        mSaveButton.setEnabled(false);
        mWaitingDialog = new XWaitingDialog(context);
    }

    private void initStatus() {

        if (processStatus < 0)
            return;

        if (processStatus == 1) {
            toNewBillStatus();
        } else if (processStatus == 2) {
            toWaitStatus();
        }
    }


    private void initTitle() {
        XTextView mTitleTextView = (XTextView) findViewById(R.id.title_text_tv);
        mTitleTextView.setText("订单详情");
        mBackImageView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        loadData(false);
    }

    @Override
    protected void setListener() {
        mBackImageView.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        findViewById(R.id.order_receive_detail_images).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_image_back:
                Bundle bundle = getIntent().getExtras();
                if (bundle != null && bundle.getString(Constant.EXTRA_RETURN_KEY_CLASS_NAME) != null) {
                    String returnClassName = bundle.getString(Constant.EXTRA_RETURN_KEY_CLASS_NAME);
                    Intent it = new Intent();
                    it.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    String packageName = context.getPackageName();
                    it.setClassName(packageName == null ? Constant.APP_PACKAGE_NAME : packageName, returnClassName);
                    startActivity(it);
                }
                Utils.finishActivity(OrderDetailActivity.this);
                break;
            case R.id.order_receive_textview_contact:
                callService();
                break;
            case R.id.issue_edit_btn:
                startAdditionActivity();
                break;
            case R.id.order_receive_detail_save:
                SaveOperation();
                break;
            case R.id.wait_contact_ok_btn:
                toDetailStatus();
                break;
            default:
                break;
        }
    }


    private void toDetailStatus() {

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        mStatusTv.setText("已联系");
        mSaveButton.setText("完成");
        mSaveButton.setEnabled(true);
        mServiceEditBtn.setEnabled(true);
        mHourLastTv.setVisibility(View.GONE);
        mOrderWaitLy.setVisibility(View.GONE);
        mSaveButton.setVisibility(View.VISIBLE);
        mCancelBillTv.setVisibility(View.GONE);
        mSaveLy.setVisibility(View.VISIBLE);

        modifyLy.setVisibility(View.VISIBLE);
        mFinalMoneyLy.setVisibility(View.VISIBLE);
        mPreBillLy.setVisibility(View.VISIBLE);
    }

    private AsyncTask<String, Void, OrderDetail> mLoadDataTask;

    private Handler mHandler = new Handler(Looper.getMainLooper());


    private void loadData(final Boolean loading) {

        if (TextUtils.isEmpty(orderId)) {
            return;
        }

        if (loading) {
            mWaitingDialog.show("正在加载中，请稍候...", false);
        }

        String url = LoginApi.URL + "/order/info/" + Long.parseLong(orderId);

        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("token", ConfigManager.getInstance(this).getUserSession())
                .add("userCode", ConfigManager.getInstance(this).getUserCode()).build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.d("slog_zd", "detail error : " + e.getLocalizedMessage());

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (loading && mWaitingDialog != null) {
                            mWaitingDialog.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (loading && mWaitingDialog != null) {
                            mWaitingDialog.dismiss();
                        }
                    }
                });

                try {
                    JSONObject object = new JSONObject(response.body().string());

                    if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {

                        int code = (int) object.get("code");

                        if (code == 0) {

                            JSONObject detailJson = (JSONObject) object.get("order");
                            mDetailInfo = new OrderDetailInfo();
                            mDetailInfo.setOrderDes(getOrderDes(detailJson));
                            // TODO: 2018/11/7

                            refreshUiSync();

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    private void refreshUiSync() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                refreshBasic();
                toDiffStatus();
            }
        });
    }

    private void refreshBasic() {

        if (mDetailInfo == null || mDetailInfo.getOrderDes() == null) {
            return;
        }

        if (isFinishing()) {
            return;
        }

        OrderDes order = mDetailInfo.getOrderDes();

        mServiceNameTv.setText(order.getServiceName());
        mPhoneTv.setText(order.getPersonName() + "/" + order.getContactPhone());
        mDateTv.setText(order.getAppointmentDay() + " " + order.getAppointmentStartTime() + " - " + order.getAppointmentEndTime());
        mAdressTv.setText(order.getLocationAddress());
        mPriceTv.setText(order.getServiceVisitCost());
        mRemarkTv.setText(order.getmRemarkDes());

        ArrayList<String> imgList = order.getmCustomImageUrls();

        if (imgList != null && imgList.size() > 0) {
            mIDImageUrls.clear();
            mIDImageUrls.addAll(imgList);
            mOrderImagesLinear.setVisibility(View.VISIBLE);
        } else {
            mOrderImagesLinear.setVisibility(View.GONE);
        }

    }


    private void toDiffStatus() {

        if (mDetailInfo == null || mDetailInfo.getOrderDes() == null) {
            return;
        }

        int processStatus = mDetailInfo.getOrderDes().getProcessStatus();

        switch (processStatus) {

            case 1:
                toNewBillStatus();
                break;
            case 2:
                toWaitStatus();
                break;


        }

    }


    private OrderDes getOrderDes(JSONObject detailJson) {

        try {
            String orderId = (String) detailJson.getString("orderId");
            int processStatus = (int) detailJson.getInt("processStatus");
            int status = (int) detailJson.getInt("status");

            JSONObject detailItem = detailJson.getJSONObject("appointmentDetail");

            String serviceName = detailItem.getString("serviceName");
            String appointmentDay = detailItem.getString("appointmentDay");
            String appointmentEndTime = detailItem.getString("appointmentEndTime");
            String appointmentStartTime = detailItem.getString("appointmentStartTime");
            String locationAddress = detailItem.getString("locationAddress");
            String serviceVisitCost = detailItem.getString("serviceVisitCost");

            String phoneNum = detailItem.getString("contactPhone");
            String selectTime = detailItem.getString("selectMasterTime");
            String remark = detailItem.getString("customerRemark");
            String personName = detailItem.getString("contactPerson");

            String latitude = detailItem.getString("latitude");
            String lontitude = detailItem.getString("longitude");

            JSONArray urlArray = detailItem.getJSONArray("customerImgUrls");

            ArrayList<String> customImgUrls = new ArrayList<>();

            if (urlArray != null && urlArray.length() > 0) {
                for (int i = 0; i < urlArray.length(); i++) {
                    String itemUrl = urlArray.get(i).toString();
                    customImgUrls.add(itemUrl);
                }
            }

            OrderDes orderItem = new OrderDes(orderId, processStatus, status,
                    serviceName, appointmentDay, appointmentEndTime, appointmentStartTime,
                    locationAddress, serviceVisitCost);

            orderItem.setContactPhone(phoneNum);
            orderItem.setmSelectTime(selectTime);
            orderItem.setmCustomImageUrls(customImgUrls);
            orderItem.setmRemarkDes(remark);
            orderItem.setPersonName(personName);
            orderItem.setmLatitude(latitude);
            orderItem.setmLongtitude(lontitude);
            return orderItem;

        } catch (Exception e) {
        }

        return null;
    }


    private void changeStatus(OrderDetail result) {

        if (result == null) {
            return;
        }

        mOrderCode = result.getOrderID();

        mDateTv.setText(result.getOrderDate() + " " + result.getOrderTime());
        mAdressTv.setText(result.getAddress() + result.getHouseNumber());

        mPhoneTv.setText(result.getContacts() + " / " + result.getTelephone());
        mPhoneTv.setTag(result.getTelephone());

        mServiceNameTv.setText(result.getServiceSubName());
        mRemarkTv.setText(result.getRemark());

        changeStatusUI(result);
    }

    private void changeStatusUI(OrderDetail result) {
        String statusString = "";

        switch (result.getOrderStatus()) {
            case OrderStatusCtrl.TYPE_NEW_BILL:
                toNewBillStatus();
                break;
            case 1:
                statusString = "已接单";
                mSaveButton.setText("完成");
                mSaveButton.setEnabled(true);
                mServiceEditBtn.setEnabled(true);
                break;
            case 2:
                statusString = "待支付";
                mSaveButton.setText("支付");
                mSaveButton.setEnabled(true);
                mServiceEditBtn.setVisibility(View.GONE);
                break;
            case 3:
                statusString = "已结算";
                mSaveButton.setText("查看支付情况");
                mSaveButton.setEnabled(true);
                mServiceEditBtn.setVisibility(View.GONE);
                break;
            case 4:
                statusString = "已结算";
                mSaveButton.setVisibility(View.GONE);
                mServiceEditBtn.setVisibility(View.GONE);
                break;
            case 98:
                statusString = "已取消";
                mSaveButton.setVisibility(View.GONE);
                mServiceEditBtn.setVisibility(View.GONE);
                break;
            case 99:
                statusString = "异常";
                mSaveButton.setVisibility(View.GONE);
                mServiceEditBtn.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        mSaveButton.setTag(result.getOrderStatus());
    }

    /**
     * 新订单状态
     */
    private void toNewBillStatus() {
        mSaveButton.setText("接单");
        mSaveButton.setEnabled(true);
        mStatusTv.setText("新订单");
        modifyLy.setVisibility(View.GONE);
        mPreBillLy.setVisibility(View.GONE);
        mFinalMoneyLy.setVisibility(View.GONE);
        mHourLastTv.setVisibility(View.GONE);
        mOrderWaitLy.setVisibility(View.GONE);
        mSaveLy.setVisibility(View.VISIBLE);
        mCurrentStatus = OrderStatusCtrl.TYPE_NEW_BILL;
    }

    /**
     * 待预约状态
     */
    private void toWaitStatus() {

        mStatusTv.setText("待预约");
        mHourLastTv.setVisibility(View.VISIBLE);
        mPreBillLy.setVisibility(View.GONE);
        mFinalMoneyLy.setVisibility(View.GONE);
        mSaveLy.setVisibility(View.GONE);
        mOrderWaitLy.setVisibility(View.VISIBLE);

        mCountDownTimer = new CountDownTimer(30 * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mHourLastTv.setText(String.valueOf(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                mHourLastTv.setText("已超时");
                mHourLastTv.setTextColor(Color.parseColor("#fd3838"));
            }
        };

        mSendMsgTv.setText(Html.fromHtml(getResources().getString(R.string.send_msg)));
        HandleUrlLinkMovementMethod instance = HandleUrlLinkMovementMethod.getInstance();
        instance.setOnLinkCallBack(new HandleUrlLinkMovementMethod.OnLinkCallBack() {
            @Override
            public void onClick(String url) {
                if (url.contains("action")) {
                    Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                    sendIntent.setData(Uri.parse("smsto:" + mPhoneTv.getText()));
                    sendIntent.putExtra("sms_body", "此处为短信模板");
                    OrderDetailActivity.this.startActivity(sendIntent);
                }
            }
        });

        mSendMsgTv.setMovementMethod(instance);
        mSendMsgTv.setLinkTextColor(Color.parseColor("#00A551"));
        mSendMsgTv.setHighlightColor(Color.TRANSPARENT);

        mCountDownTimer.start();
    }


    private void SaveOperation() {
        if (!Utils.isNetworkAvailable(context)) {
            Utils.showShortToast(context, getString(R.string.common_no_network_message));
            return;
        }

        if (mDetailInfo == null || mDetailInfo.getOrderDes() == null) {
            return;
        }

        OrderDes orderDes = mDetailInfo.getOrderDes();

        switch (orderDes.getProcessStatus()) {
            case 1:
                acceptOperation();
                break;
        }


        /*if (mSaveButton.getTag() == null) return;

        int status = Integer.parseInt(mSaveButton.getTag().toString());

        //接单
        if (status == 0) {
            acceptOperation();
        }

        //完成订单
        if (status == 1) {
            finishOperation();
        }

        //生成支付二维码
        if (status == 2) {
            payOperation();
        }

        //查看支付情况
        if (status == 3) {
            showPaySuccessActivity();
        }*/
    }

    private void acceptOperation() {

        if (mDetailInfo == null || mDetailInfo.getOrderDes() == null) {
            return;
        }

        OrderDes order = mDetailInfo.getOrderDes();

        String id = order.getOrderId();

        mWaitingDialog.show("正在接单，请稍候...", false);

        BillApi billApi = KHttpWorker.ins().createHttpService(LoginApi.URL, BillApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("orderId", id);
        params.put("isReceived", String.valueOf(true));

        final retrofit2.Call<JsonObject> request = billApi.acceptOrder(params);

        request.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                Log.d("slog_zd","receive bill : " + response.body().toString());

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mWaitingDialog != null) {
                            mWaitingDialog.dismiss();
                        }
                    }
                });

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {

                    JsonObject jsonObject = response.body();

                    final String msg = jsonObject.get("msg").getAsString();
                    final int code = jsonObject.get("code").getAsInt();

                    if (code == 0) {

                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(context, msg + " : " + code);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mWaitingDialog != null) {
                            mWaitingDialog.dismiss();
                        }
                        Utils.showShortToast(context, "接单失败，请重试！");
                    }
                });
            }
        });

        /*mWaitingDialog.show("正在接单，请稍候...", false);
        new AsyncTask<String, Void, Integer>() {
            @Override
            protected Integer doInBackground(String... params) {
                int orderID = Integer.valueOf(params[0]);
                return LogicFactory.getDesktopLogic(context).saveOrderReceiveState(orderID);
            }

            @Override
            protected void onPostExecute(Integer result) {
                mWaitingDialog.dismiss();
                if (result == APIConstants.RESULT_CODE_SUCCESS) {
                   *//* Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    Utils.finishWithoutAnim(OrderDetailActivity.this);*//*
                    toWaitStatus();
                } else {
                    Utils.showShortToast(context, "接单失败，请重试！");
                }
            }

        }.execute(orderID);*/
    }


    private void finishOperation() {
        String orderID = getIntent().getStringExtra("order_id");
        if (!Utils.isNotEmpty(orderID)) {
            return;
        }

        mWaitingDialog.show("正在完成订单，请稍候...", false);
        new AsyncTask<String, Void, Integer>() {
            @Override
            protected Integer doInBackground(String... params) {
                int orderID = Integer.valueOf(params[0]);
                return LogicFactory.getDesktopLogic(context).saveOrderFinishState(orderID);
            }

            @Override
            protected void onPostExecute(Integer result) {
                mWaitingDialog.dismiss();
                if (result == APIConstants.RESULT_CODE_SUCCESS) {
                    payOperation();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    Utils.finishWithoutAnim(OrderDetailActivity.this);
                } else {
                    Utils.showShortToast(context, "完成订单失败，请重试！");
                }
            }

        }.execute(orderID);
    }

    private void payOperation() {
        String orderID = getIntent().getStringExtra("order_id");
        if (!Utils.isNotEmpty(orderID)) {
            return;
        }

        mWaitingDialog.show("正在生成支付二维码，请稍候...", false);
        new AsyncTask<String, Void, XResult>() {
            @Override
            protected XResult doInBackground(String... params) {
                int orderID = Integer.valueOf(params[0]);
                return LogicFactory.getDesktopLogic(context).getOrderPayUrl(orderID);
            }

            @Override
            protected void onPostExecute(XResult result) {
                mWaitingDialog.dismiss();
                if (result != null && result.getResultCode() == APIConstants.RESULT_CODE_SUCCESS) {
                    createPayQRCode(result.getDataObject().toString());
                } else {
                    Utils.showShortToast(context, "生成支付二维码失败，请重试！");
                }
            }

        }.execute(orderID);
    }

    private void createPayQRCode(String url) {
        if (!Utils.isNotEmpty(url)) {
            Utils.showLongToast(context, "支付链接错误，请联系客服人员！");
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context, OrderPayQRCodeActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void showPaySuccessActivity() {
        Intent intent = new Intent();
        intent.setClass(context, OrderPaySuccessActivity.class);
        intent.putExtra("order_id", String.valueOf(mOrderCode));
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void startAdditionActivity() {
        Intent intent = new Intent();
        intent.setClass(context, OrderDetailAdditionActivity.class);
        intent.putExtra("order_id", String.valueOf(mOrderCode));
        intent.putExtra("add_item", mIssueReasonTv.getText());
        context.startActivityForResult(intent, Constant.ACTIVITY_ORDER_ADDITION_RESULT_CODE);
        context.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {
                case Constant.ACTIVITY_ORDER_ADDITION_RESULT_CODE:
                    if (resultCode == RESULT_OK) {
                        loadData(true);
                    }
                    break;
            }
        } catch (Exception e) {

        }
    }

    private void callService() {

        String mobile = mDetailInfo != null ? mDetailInfo.getOrderDes().getContactPhone() : "0";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + mobile));
        startActivity(intent);
    }

    private void showUserImages() {
        if (mIDImageUrls == null || mIDImageUrls.size() == 0) {
            Utils.showShortToast(context, "用户没有上传照片！");
            return;
        }

        Intent intent = new Intent();
        intent.putStringArrayListExtra("urls", mIDImageUrls);
        intent.putExtra("operation_flag", "2");
        intent.putExtra("title", "用户故障照片");
        intent.setClass(context, PublicImagesActivity.class);
        startActivity(intent);
    }

    @Override
    protected String getPageName() {
        return TAG;
    }

    @Override
    public void onDestroy() {
        try {
            if (mLoadDataTask != null) {
                mLoadDataTask.cancel(true);
            }
        } catch (Exception e) {
        }

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        super.onDestroy();
    }
}

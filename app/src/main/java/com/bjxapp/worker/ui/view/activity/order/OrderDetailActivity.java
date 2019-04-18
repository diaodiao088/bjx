package com.bjxapp.worker.ui.view.activity.order;

import android.content.DialogInterface;
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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.BillApi;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.RecordApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.MainTainBean;
import com.bjxapp.worker.model.MaintainInfo;
import com.bjxapp.worker.model.OrderDes;
import com.bjxapp.worker.model.OrderDetail;
import com.bjxapp.worker.model.OrderDetailInfo;
import com.bjxapp.worker.ui.view.activity.DeviceInfoActivity;
import com.bjxapp.worker.ui.view.activity.FastJudgeActivity;
import com.bjxapp.worker.ui.view.activity.MaintainActivity;
import com.bjxapp.worker.ui.view.activity.PublicImagesActivity;
import com.bjxapp.worker.ui.view.activity.widget.dialog.ICFunSimpleAlertDialog;
import com.bjxapp.worker.ui.view.activity.widget.dialog.SimpleConfirmDialog;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.ui.view.fragment.ctrl.DataManagerCtrl;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.MaintainItemLayout;
import com.bjxapp.worker.ui.widget.MaintainItemLayoutStub;
import com.bjxapp.worker.utils.DateUtils;
import com.bjxapp.worker.utils.HandleUrlLinkMovementMethod;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
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

;

public class OrderDetailActivity extends BaseActivity implements OnClickListener {

    protected static final String TAG = OrderDetailActivity.class.getSimpleName();

    private int mOrderCode;

    private int mCurrentStatus;

    private boolean isDeviceBill;

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

    @BindView(R.id.wait_contact_change_btn)
    TextView mChangeDateTv;


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
    @BindView(R.id.issue_add_image_ly)
    RelativeLayout mIssueImgLy;

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
    RelativeLayout mOrderImagesLinear;

    @BindView(R.id.custom_img_size_tv)
    TextView mImgSizetTv;

    ArrayList<String> mIDImageUrls = new ArrayList<>();

    @BindView(R.id.order_receive_detail_save)
    XButton mSaveButton;

    @BindView(R.id.order_receiver_ly)
    LinearLayout mOrderWaitLy;

    @BindView(R.id.tele_contacts_ly)
    RelativeLayout mContactLy;

    @BindView(R.id.ready_ly)
    LinearLayout mReadyLy;

    @BindView(R.id.ready_tips_tv)
    TextView mReadyTv;

    @BindView(R.id.check_service_ly)
    RelativeLayout mCheckServiceLy;

    @BindView(R.id.check_tv)
    TextView mCheckTv;

    @BindView(R.id.look_info)
    TextView mLookInfoTv;

    @BindView(R.id.main_container_ly)
    LinearLayout mMainLy;

    @BindView(R.id.detail_price_ly)
    LinearLayout mPriceDetailLy;

    @OnClick(R.id.look_info)
    void onClickLookinfo() {

        if (mImageList.size() > 0) {
            FastJudgeActivity.goToActivity(this, true, mDetailInfo.getOrderDes().getEnterpriseOrderId(),
                    mDetailInfo.getOrderDes().getEnterpriseId(), mImageList, mDetailInfo.getOrderDes().getOrderId());
        } else {
            Toast.makeText(OrderDetailActivity.this, "请至少添加一张照片", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.check_service_ly)
    void onClickService() {

        if (mDetailInfo == null || mDetailInfo.getOrderDes() == null) {
            return;
        }

        DeviceInfoActivity.goToActivity(this, mDetailInfo.getOrderDes().getDetailId(),
                false, true, true);
    }

    @OnClick(R.id.order_receive_textview_address)
    void onAddressClick() {

        if (mDetailInfo == null) {
            return;
        }

        try {
            if (isInstallByRead("com.autonavi.minimap")) {
                setUpGaodeAppByMine();
            } else {
                Utils.showShortToast(this, "请安装高德地图或者自行打开");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadData(false);
    }

    void setUpGaodeAppByMine() {
        try {
            Intent intent = Intent.getIntent("androidamap://route?sourceApplication=softname&sname=我的位置&dlat="
                    + mDetailInfo.getOrderDes().getmLatitude() + "&dlon=" + mDetailInfo.getOrderDes().getmLongtitude()
                    + "&dname=" + mDetailInfo.getOrderDes().getLocationAddress() + "&dev=0&m=1&t=1");

            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isInstallByRead(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    private CountDownTimer mCountDownTimer;

    private CountDownTimer mNewBillTimer;

    private XWaitingDialog mWaitingDialog;

    private OrderDetailInfo mDetailInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        handleIntent();
        DataManagerCtrl.getIns().markDataDirty(true);
        super.onCreate(savedInstanceState);
    }

    @OnClick(R.id.order_receive_detail_images)
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

        if (mDetailInfo == null || mDetailInfo.getOrderDes() == null) {
            return;
        }

        CancelBillActivity.goToActivity(this, mDetailInfo.getOrderDes().getOrderId());
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

        if (mDetailInfo == null || mDetailInfo.getMaintainInfo() == null
                || mDetailInfo.getOrderDes() == null) {
            return;
        }

        if (isDeviceBill) {
            MaintainInfo maintainInfo = mDetailInfo.getMaintainInfo();
            MaintainActivity.goToActivity(this, mDetailInfo.getOrderDes().getEnterpriseId(), mDetailInfo.getOrderDes().getOrderId(), maintainInfo);
        } else {
            MaintainInfo maintainInfo = mDetailInfo.getMaintainInfo();
            ServiceBillActivity.goToActivity(this, ServiceBillActivity.SERVICE_BILL_CODE, maintainInfo, mDetailInfo.getOrderDes().getOrderId());
        }

    }

    boolean isBillFinished = false;

    @OnClick(R.id.add_image_content)
    void addIssueImage() {

        if (mDetailInfo != null) {
            OrderDes orderDes = mDetailInfo.getOrderDes();
            if (orderDes != null) {
                int status = orderDes.getProcessStatus();
                if (status > 5) {
                    isBillFinished = true;
                }
            }
        }

        AddImageActivity.goToActivity(this, AddImageActivity.OP_ADD, mImageList, isBillFinished);
    }

    @OnClick(R.id.order_bill_btn)
    void editPreBill() {
        if (TextUtils.isEmpty(mIssuePriceTv.getText().toString()) || TextUtils.isEmpty(mStrategyContentTv.getText().toString())) {
            Utils.showShortToast(this, "请先填写维修项信息");
        } else {
            OrderPriceActivity.goToActivity(this, mDetailInfo != null ? mDetailInfo.getOrderDes().getOrderId() : String.valueOf(-1),
                    mIssuePriceTv.getText().toString(),
                    mDetailInfo.getMaintainInfo().getPrePayService(),
                    mDetailInfo.getMaintainInfo().getPreCost(),
                    mDetailInfo.getMaintainInfo().getPrepayImgUrls());
        }
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
        secondData.add("08:00:00--11:00:00");
        secondData.add("11:00:00--14:00:00");
        secondData.add("14:00:00--17:00:00");
        secondData.add("17:00:00--20:00:00");
        final DoublePicker picker = new DoublePicker(this, firstData, secondData);
        picker.setDividerVisible(true);

        Calendar startTime = Calendar.getInstance();

        int selectFirstIndex = 0;
        int selectSecondIndex = 0;

        if (startTime.get(Calendar.HOUR_OF_DAY) < 8) {
            selectSecondIndex = 0;
        } else if (startTime.get(Calendar.HOUR_OF_DAY) < 11) {
            selectSecondIndex = 1;
        } else if (startTime.get(Calendar.HOUR_OF_DAY) < 14) {
            selectSecondIndex = 2;
        } else if (startTime.get(Calendar.HOUR_OF_DAY) < 17) {
            selectSecondIndex = 3;
        } else {
            selectFirstIndex = 1;
        }

        picker.setSelectedIndex(selectFirstIndex, selectSecondIndex);
        picker.setTextSize(12);
        picker.setContentPadding(15, 10);
        picker.setOnPickListener(new DoublePicker.OnPickListener() {
            @Override
            public void onPicked(int selectedFirstIndex, int selectedSecondIndex) {
                //  showToast(firstData.get(selectedFirstIndex) + " " + secondData.get(selectedSecondIndex));

                changeDateReal(firstData.get(selectedFirstIndex), secondData.get(selectedSecondIndex));

            }
        });
        picker.show();
    }

    private void changeDateReal(final String day, final String time) {

        if (mDetailInfo == null || mDetailInfo.getOrderDes() == null) {
            return;
        }

        final OrderDes orderDes = mDetailInfo.getOrderDes();

        String orderId = orderDes.getOrderId();

        BillApi billApi = KHttpWorker.ins().createHttpService(LoginApi.URL, BillApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("orderId", orderId);
        params.put("appointmentDay", day + " 00:00:00");
        params.put("appointmentStartTime", day + " " + time.split("--")[0]);
        params.put("appointmentEndTime", day + " " + time.split("--")[1]);

        retrofit2.Call<JsonObject> request = billApi.changeTime(params);

        mWaitingDialog.show("正在修改时间..", false);

        request.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

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
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // toWaitStatus();
                                orderDes.setAppointmentDay(day);
                                orderDes.setAppointmentStartTime(time.split("--")[0]);
                                orderDes.setAppointmentEndTime(time.split("--")[1]);

                                if (orderDes.getBillType() == OrderDes.BILL_TYPE_EMERGENCY) {
                                    mDateTv.setText("紧急上门");
                                } else {
                                    mDateTv.setText(day + " " + time.split("--")[0] + " - " + time.split("--")[1]);
                                }
                            }
                        });
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
                        Utils.showShortToast(context, "时间修改失败 ！");
                    }
                });
            }
        });

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
        } else if (processStatus == 3 || processStatus == 4 || processStatus == 5
                || processStatus == 6 || processStatus == 7) {
            toDetailUi();
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
        //loadData(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(false);
    }

    @Override
    protected void setListener() {
        mBackImageView.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        mChangeDateTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_image_back:
                onBackPressed();
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
            case R.id.wait_contact_change_btn:
                changeDate();
                break;
            default:
                break;
        }
    }


    private void toDetailStatus() {

        if (mDetailInfo == null || mDetailInfo.getOrderDes() == null) {
            return;
        }

        final OrderDes orderDes = mDetailInfo.getOrderDes();

        mWaitingDialog.show("正在确认，请稍候...", false);

        BillApi billApi = KHttpWorker.ins().createHttpService(LoginApi.URL, BillApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("orderId", String.valueOf(orderDes.getOrderId()));

        final retrofit2.Call<JsonObject> request = billApi.confirmAppoinment(params);

        request.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                Log.d("slog_zd", "confirm : " + response.body().toString());

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
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                orderDes.setProcessStatus(3);
                                toDetailUi();
                            }
                        });
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
                    }
                });
            }
        });
    }

    private void toDetailUi() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        mContactLy.setVisibility(View.VISIBLE);
        mStatusTv.setText("待上门");
        mStatusTv.setBackgroundResource(R.drawable.layout_textview_radius);
        mSaveButton.setText("完成");
        mSaveButton.setEnabled(true);
        mServiceEditBtn.setEnabled(true);
        mHourLastTv.setVisibility(View.GONE);
        mOrderWaitLy.setVisibility(View.GONE);
        mSaveButton.setVisibility(View.VISIBLE);
        mCancelBillTv.setVisibility(View.GONE);
        mSaveLy.setVisibility(View.VISIBLE);
        mIssueImgLy.setVisibility(View.VISIBLE);

        modifyLy.setVisibility(View.VISIBLE);
        mFinalMoneyLy.setVisibility(View.VISIBLE);
        mPreBillLy.setVisibility(View.VISIBLE);

        if (mDetailInfo == null || mDetailInfo.getMaintainInfo() == null || mDetailInfo.getOrderDes() == null) {
            return;
        }

        int processStatus = mDetailInfo.getOrderDes().getProcessStatus();
        int status = mDetailInfo.getOrderDes().getStatus();

        mLookInfoTv.setVisibility(View.GONE);

        if (processStatus == 3) {
            mStatusTv.setText("待上门");

            if (!mDetailInfo.getOrderDes().isTwiceServed() && isDeviceBill) {
                mSaveButton.setText("下一步");
            } else {
                mSaveButton.setText("完成");
            }


            if (mDetailInfo.getOrderDes().isTwiceServed() && isDeviceBill) {
                mLookInfoTv.setVisibility(View.VISIBLE);
            }


        } else if (processStatus == 4) {
            mStatusTv.setText("已上门");
            if (!mDetailInfo.getOrderDes().isTwiceServed() && isDeviceBill) {
                mSaveButton.setText("下一步");
            } else {
                mSaveButton.setText("完成");
            }

            if (mDetailInfo.getOrderDes().isTwiceServed() && isDeviceBill) {
                mLookInfoTv.setVisibility(View.VISIBLE);
            }


        } else if (processStatus == 5) {
            mStatusTv.setText("待支付");
            preBillBtn.setVisibility(View.GONE);
            mSaveButton.setText("支付");
            mServiceEditBtn.setVisibility(View.GONE);

            if (isDeviceBill) {
                mLookInfoTv.setVisibility(View.VISIBLE);
            }

        } else if (processStatus == 6) {
            mStatusTv.setText("待评价");
            preBillBtn.setVisibility(View.GONE);
            mServiceEditBtn.setVisibility(View.GONE);
            mSaveButton.setVisibility(View.GONE);

            if (isDeviceBill) {
                mLookInfoTv.setVisibility(View.VISIBLE);
            }

        } else if (processStatus == 7) {
            mStatusTv.setText("已评价");
            preBillBtn.setVisibility(View.GONE);
            mServiceEditBtn.setVisibility(View.GONE);
            mSaveButton.setVisibility(View.GONE);

            if (isDeviceBill) {
                mLookInfoTv.setVisibility(View.VISIBLE);
            }

        }

        if (status == 4) {
            mStatusTv.setText("异常");
            preBillBtn.setVisibility(View.GONE);
            mServiceEditBtn.setVisibility(View.GONE);
            mSaveButton.setVisibility(View.GONE);
        }

        MaintainInfo maintainInfo = mDetailInfo.getMaintainInfo();

        if (maintainInfo.getPrePaid()) {
            preBillBtn.setVisibility(View.GONE);
        }

        preBillContentTv.setText("null".equals(maintainInfo.getPrePayService()) ? "" : maintainInfo.getPrePayService());
        preBillCashTv.setText(maintainInfo.getPreCost());

        mIssueReasonTv.setText("null".equals(maintainInfo.getFault()) ? "" : maintainInfo.getFault());
        mStrategyContentTv.setText("null".equals(maintainInfo.getPlan()) ? "" : maintainInfo.getPlan());
        mIssuePriceTv.setText(maintainInfo.getTotalCost());

        mTotalPriceTv.setText("+ " + maintainInfo.getTotalCost()); // 总报价

        if (!isDeviceBill) {
            mPriceDetailLy.setVisibility(View.GONE);
        } else {
            mPriceDetailLy.setVisibility(View.VISIBLE);
            for (int i = 0; i < maintainInfo.getmMaintainList().size(); i++) {
                addUi(maintainInfo.getmMaintainList().get(i));
            }
        }

        BigDecimal totalCost = new BigDecimal(Double.parseDouble(maintainInfo.getTotalCost()));
        BigDecimal serviceCost = new BigDecimal(mDetailInfo.getOrderDes().getServiceVisitCost());
        BigDecimal preCost = new BigDecimal(Double.parseDouble(maintainInfo.getPreCost()));

        maintainInfo.setTotalAmount(String.valueOf(totalCost.add(serviceCost).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
        mTotalTv.setText(String.valueOf(totalCost.add(serviceCost).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue())); //总计
        mPrePayPriceTv.setText("- " + maintainInfo.getPreCost());

        // double payAmount = Double.parseDouble(maintainInfo.getTotalCost()) + Double.parseDouble(mDetailInfo.getOrderDes().getServiceVisitCost()) - Double.parseDouble(maintainInfo.getPreCost());
        double payAmount = totalCost.add(serviceCost).subtract(preCost).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        payAmount = payAmount > 0 ? payAmount : 0.00;
        maintainInfo.setPayAmount(String.valueOf(payAmount));


        mFuKuanContentTv.setText(String.valueOf(payAmount));

    }

    private void addUi(MainTainBean mainTainBean) {

        final MaintainItemLayoutStub maintainItemLayout = new MaintainItemLayoutStub(this);
        maintainItemLayout.bindData(mainTainBean);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, DimenUtils.dp2px(10, this), 0, 0);
        mMainLy.addView(maintainItemLayout, params);

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
                            mDetailInfo.setMaintainInfo(getMainTainInfo(detailJson));

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

        mServiceNameTv.setText(order.getOrderNum());
        mPhoneTv.setText(order.getPersonName() + "/" + order.getContactPhone());

        if (order.getBillType() == OrderDes.BILL_TYPE_EMERGENCY) {
            mDateTv.setText("紧急上门");
        } else {
            mDateTv.setText(order.getAppointmentDay() + " " + order.getAppointmentStartTime() + " - " + order.getAppointmentEndTime());
        }

        mAdressTv.setText(order.getLocationAddress() + " - " + order.getDetailAddress());
        mPriceTv.setText("费用 : " + order.getServiceVisitCost());
        mRemarkTv.setText(order.getmRemarkDes());

        mEnterRoomPrice.setText(order.getServiceVisitCost());

        ArrayList<String> imgList = order.getmCustomImageUrls();

        if (imgList != null && imgList.size() > 0) {
            mIDImageUrls.clear();
            mIDImageUrls.addAll(imgList);
            mOrderImagesLinear.setVisibility(View.VISIBLE);
            mImgSizetTv.setText(imgList.size() + "张");
        } else {
            mOrderImagesLinear.setVisibility(View.GONE);
        }

        if (order.getmServiceType().equals("0")) {
            mCheckServiceLy.setVisibility(View.VISIBLE);
            mCheckTv.setText("查看巡检详情");
            isDeviceBill = true;
        } else if (order.getmServiceType().equals("1")) {
            mCheckServiceLy.setVisibility(View.VISIBLE);
            mCheckTv.setText("查看保养详情");
            isDeviceBill = true;
        } else {
            mCheckServiceLy.setVisibility(View.GONE);
        }

        // if is free ,then return

        if (order.isFree()) {
            mPreBillLy.setVisibility(View.GONE);
        } else {
            mPreBillLy.setVisibility(View.VISIBLE);
        }

    }


    private void toDiffStatus() {

        if (mDetailInfo == null || mDetailInfo.getOrderDes() == null || mDetailInfo.getMaintainInfo() == null) {
            return;
        }

        int processStatus = mDetailInfo.getOrderDes().getProcessStatus();

        updateCommonUI(mDetailInfo.getOrderDes().getOriginType(), mDetailInfo.getMaintainInfo().getPreCost());

        switch (processStatus) {

            case 1:
                toNewBillStatus();
                break;
            case 2:
                toWaitStatus();
                break;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                toDetailUi();
                break;
        }
    }

    private void updateCommonUI(int originType, String preCost) {
        if (originType == 2) {
            mReadyLy.setVisibility(View.VISIBLE);
            String tipContent = getResources().getString(R.string.ready_tips);
            String tipsReal = String.format(tipContent, preCost);
            mReadyTv.setText(tipsReal);
        }
    }

    public String reasonTemp = "";
    public String planTemp = "";
    public String costDetailTemp = "";
    public String totalCostTemp = "";

    private MaintainInfo getMainTainInfo(JSONObject detailJson) {

        try {
            JSONObject detailItem = detailJson.getJSONObject("maintainDetail");

            ArrayList<MainTainBean> mainTainList = new ArrayList<>();

            if (detailItem.has("equipmentComponentList")) {

                JSONArray array = detailItem.getJSONArray("equipmentComponentList");

                for (int i = 0; i < array.length(); i++) {

                    JSONObject mainItem = array.getJSONObject(i);
                    MainTainBean mainTainBean = new MainTainBean();
                    mainTainBean.setComponentName(mainItem.getString("equipmentComponentName"));
                    mainTainBean.setCost(mainItem.getString("equipmentComponentCost"));
                    mainTainBean.setQuantity(mainItem.getInt("equipmentComponentQuantity"));

                    if (!mainItem.get("equipmentComponentId").toString().equals("null")) {
                        mainTainBean.setComponentId(mainItem.getInt("equipmentComponentId"));
                    }

                    if (!mainItem.get("equipmentComponentModel").toString().equals("null")) {

                        if (TextUtils.isEmpty(mainItem.getString("equipmentComponentModel"))) {
                            mainTainBean.setOthers(true);
                        } else {
                            mainTainBean.setModel(mainItem.getString("equipmentComponentModel"));
                        }
                    } else {
                        mainTainBean.setOthers(true);
                    }

                    if (!mainItem.get("equipmentComponentUnit").toString().equals("null")) {
                        mainTainBean.setUnit(mainItem.getString("equipmentComponentUnit"));
                    }

                    mainTainList.add(mainTainBean);
                }
            }

            String costDetail = detailItem.getString("costDetail");
            String fault = detailItem.getString("fault");
            boolean paid = detailItem.getBoolean("paid");
            String payAmount = detailItem.getString("payAmount");
            String plan = detailItem.getString("plan");
            boolean prePaid = detailItem.getBoolean("prepaid");
            String preCost = detailItem.getString("prepayCost");
            String prePayService = detailItem.getString("prepayService");
            String totalAmount = detailItem.getString("totalAmount");
            String totalCost = detailItem.getString("totalCost");
            String orderTime = detailItem.getString("receiveOrderTime");

            JSONArray urlArray = detailItem.getJSONArray("masterImgUrls");

            ArrayList<String> customImgUrls = new ArrayList<>();
            if (urlArray != null && urlArray.length() > 0) {
                for (int i = 0; i < urlArray.length(); i++) {
                    String itemUrl = urlArray.get(i).toString();
                    customImgUrls.add(itemUrl);
                }
            }

            // 预付照片
            JSONArray prePayImgArray = detailItem.getJSONArray("prepayImgUrls");
            ArrayList<String> prePayImgUrls = new ArrayList<>();
            if (prePayImgArray != null && prePayImgArray.length() > 0) {
                for (int i = 0; i < prePayImgArray.length(); i++) {
                    String itemUrl = prePayImgArray.get(i).toString();
                    prePayImgUrls.add(itemUrl);
                }
            }

            MaintainInfo maintainInfo = new MaintainInfo(costDetail, fault, paid, payAmount,
                    plan, prePaid, preCost, prePayService, totalAmount, totalCost);

            maintainInfo.setOrderTime(orderTime);

            maintainInfo.setmMaintainList(mainTainList);

            if (!TextUtils.isEmpty(costDetailTemp)) {
                maintainInfo.setCostDetail(costDetailTemp);
            }

            if (!TextUtils.isEmpty(planTemp)) {
                maintainInfo.setPlan(planTemp);
            }

            if (!TextUtils.isEmpty(reasonTemp)) {
                maintainInfo.setFault(reasonTemp);
            }

            if (!TextUtils.isEmpty(totalCostTemp)) {
                maintainInfo.setTotalCost(totalCostTemp);
            }

            maintainInfo.setMasterImgUrls(customImgUrls);

            if (customImgUrls.size() > 0) {
                mImageList.clear();
                mImageList.addAll(customImgUrls);
            }

            maintainInfo.setPrepayImgUrls(prePayImgUrls);

            return maintainInfo;

        } catch (Exception e) {

        }
        return null;
    }


    private OrderDes getOrderDes(JSONObject detailJson) {

        try {
            String orderId = (String) detailJson.getString("orderId");
            int processStatus = (int) detailJson.getInt("processStatus");
            int status = (int) detailJson.getInt("status");
            int origin = detailJson.getInt("origin");
            String orderNum = detailJson.getString("orderNo");

            JSONObject detailItem = detailJson.getJSONObject("appointmentDetail");

            String serviceName = detailItem.getString("serviceName");
            String appointmentDay = detailItem.getString("appointmentDay");
            String appointmentEndTime = detailItem.getString("appointmentEndTime");
            String appointmentStartTime = detailItem.getString("appointmentStartTime");
            String locationAddress = detailItem.getString("locationAddress");
            String detailAddress = detailItem.getString("detailAddress");
            String serviceVisitCost = detailItem.getString("serviceVisitCost");

            String phoneNum = detailItem.getString("contactPhone");
            String selectTime = detailItem.getString("selectMasterTime");
            String remark = detailItem.getString("customerRemark");
            String personName = detailItem.getString("contactPerson");
            String billType = detailItem.getString("type");

            String latitude = detailItem.getString("latitude");
            String lontitude = detailItem.getString("longitude");

            boolean isTwiceServed = false;

            if (detailItem.has("twiceServed") && detailItem.get("twiceServed") != null && !detailItem.get("twiceServed").toString().equals("null")) {
                isTwiceServed = detailItem.getBoolean("twiceServed");
            }

            boolean isFree = false;

            if (detailItem.has("free") && detailItem.get("free") != null && !detailItem.get("free").toString().equals("null")) {
                isFree = detailItem.getBoolean("free");
            }

            JSONArray urlArray = detailItem.getJSONArray("customerImgUrls");

            String enterpriseId = "";
            String serviceType = "";
            String enterpriseOrderId = "";

            String detailId = "";

            if (detailItem.has("equipmentId")) {
                enterpriseId = detailItem.getString("equipmentId");

            }

            if (detailItem.has("enterpriseOrderServiceType")) {
                serviceType = detailItem.getString("enterpriseOrderServiceType");

            }

            if (detailItem.has("enterpriseOrderId")) {
                enterpriseOrderId = detailItem.getString("enterpriseOrderId");
            }


            if (detailItem.has("enterpriseOrderEquipmentId")) {
                detailId = detailItem.getString("enterpriseOrderEquipmentId");
            }

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
            orderItem.setOrderNum(orderNum);
            orderItem.setDetailAddress(detailAddress);
            orderItem.setBillType(Integer.parseInt(billType));
            orderItem.setOriginType(origin);
            orderItem.setmServiceType(serviceType);
            orderItem.setEnterpriseId(enterpriseId);
            orderItem.setFree(isFree);
            orderItem.setTwiceServed(isTwiceServed);
            orderItem.setEnterpriseOrderId(enterpriseOrderId);

            orderItem.setDetailId(detailId);

            return orderItem;

        } catch (Exception e) {
        }

        return null;
    }

    /**
     * 新订单状态
     */
    private void toNewBillStatus() {

        mSaveButton.setText("接单");
        mSaveButton.setEnabled(true);
        mStatusTv.setText("新订单");
        mStatusTv.setBackgroundResource(R.drawable.layout_textview_radius);
        modifyLy.setVisibility(View.GONE);
        mPreBillLy.setVisibility(View.GONE);
        mFinalMoneyLy.setVisibility(View.GONE);
        mHourLastTv.setVisibility(View.VISIBLE);
        mOrderWaitLy.setVisibility(View.GONE);
        mIssueImgLy.setVisibility(View.GONE);
        mContactLy.setVisibility(View.GONE);

        mSaveLy.setVisibility(View.VISIBLE);
        mCurrentStatus = OrderStatusCtrl.TYPE_NEW_BILL;

        if (mDetailInfo == null || mDetailInfo.getOrderDes() == null) {
            return;
        }

        int status = mDetailInfo.getOrderDes().getStatus();

        if (status == 4) {
            mStatusTv.setText("异常");
            mSaveButton.setVisibility(View.GONE);
        }

        if (mDetailInfo == null || mDetailInfo.getOrderDes() == null) {
            return;
        }

        OrderDes orderDes = mDetailInfo.getOrderDes();
        long selectMasterTime = Long.parseLong(orderDes.getmSelectTime());

        long currentTime = System.currentTimeMillis();

        if (mNewBillTimer != null) {
            mNewBillTimer.cancel();
        }

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        if (currentTime - selectMasterTime <= 30 * 60 * 1000) {
            mNewBillTimer = new CountDownTimer(30 * 60 * 1000 - (currentTime - selectMasterTime), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    int minute = (int) (millisUntilFinished / (60 * 1000));
                    int second = (int) (millisUntilFinished % (60 * 1000));
                    mHourLastTv.setText(minute + ":" + (second / 1000));
                }

                @Override
                public void onFinish() {
                    mHourLastTv.setText("已超时");
                    mHourLastTv.setTextColor(Color.parseColor("#fd3838"));
                    mHourLastTv.setVisibility(View.GONE);
                    mStatusTv.setText("已超时");
                    mStatusTv.setBackgroundResource(R.drawable.layout_textview_red_radius);
                }
            };

            mNewBillTimer.start();
        } else {
            mHourLastTv.setText("已超时");
            mHourLastTv.setTextColor(Color.parseColor("#fd3838"));
            mHourLastTv.setVisibility(View.GONE);
            mStatusTv.setText("已超时");
            mStatusTv.setBackgroundResource(R.drawable.layout_textview_red_radius);
        }
    }

    /**
     * 待预约状态
     */
    private void toWaitStatus() {

        mStatusTv.setText("待联系");
        mStatusTv.setBackgroundResource(R.drawable.layout_textview_radius);
        mHourLastTv.setVisibility(View.VISIBLE);
        mPreBillLy.setVisibility(View.GONE);
        mFinalMoneyLy.setVisibility(View.GONE);
        mSaveLy.setVisibility(View.GONE);
        mOrderWaitLy.setVisibility(View.VISIBLE);
        mIssueImgLy.setVisibility(View.GONE);
        mContactLy.setVisibility(View.VISIBLE);

        if (mDetailInfo == null || mDetailInfo.getOrderDes() == null || mDetailInfo.getMaintainInfo() == null
                || mDetailInfo.getMaintainInfo().getOrderTime() == null) {
            return;
        }

        long selectMasterTime = Long.parseLong(mDetailInfo.getMaintainInfo().getOrderTime());

        long currentTime = System.currentTimeMillis();

        int billType = mDetailInfo.getOrderDes().getBillType();

        if (mNewBillTimer != null) {
            mNewBillTimer.cancel();
        }

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        if (currentTime - selectMasterTime <= 30 * 60 * 1000) {
            mCountDownTimer = new CountDownTimer(30 * 60 * 1000 - (currentTime - selectMasterTime), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    int minute = (int) (millisUntilFinished / (60 * 1000));

                    int second = (int) (millisUntilFinished % (60 * 1000));

                    mHourLastTv.setText(minute + ":" + (second / 1000));
                }

                @Override
                public void onFinish() {
                    mHourLastTv.setText("已超时");
                    mHourLastTv.setTextColor(Color.parseColor("#fd3838"));
                    mHourLastTv.setVisibility(View.GONE);
                    mStatusTv.setText("已超时");
                    mStatusTv.setBackgroundResource(R.drawable.layout_textview_red_radius);
                }
            };

            mCountDownTimer.start();
        } else {
            mHourLastTv.setVisibility(View.GONE);
            mHourLastTv.setTextColor(Color.parseColor("#fd3838"));
            mHourLastTv.setText("已超时");
            mStatusTv.setText("已超时");
            mStatusTv.setBackgroundResource(R.drawable.layout_textview_red_radius);
        }

        if (billType == OrderDes.BILL_TYPE_EMERGENCY) {
            mChangeDateTv.setClickable(false);
            mChangeDateTv.setOnClickListener(null);
        }

        mSendMsgTv.setText(Html.fromHtml(getResources().getString(R.string.send_msg)));
        HandleUrlLinkMovementMethod instance = HandleUrlLinkMovementMethod.getInstance();
        instance.setOnLinkCallBack(new HandleUrlLinkMovementMethod.OnLinkCallBack() {
            @Override
            public void onClick(String url) {
                if (url.contains("action")) {
                    /*Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                    sendIntent.setData(Uri.parse("smsto:" + mPhoneTv.getText()));
                    sendIntent.putExtra("sms_body", "此处为短信模板");
                    OrderDetailActivity.this.startActivity(sendIntent);*/
                    showConfirmDialog();
                }
            }
        });

        mSendMsgTv.setMovementMethod(instance);
        mSendMsgTv.setLinkTextColor(Color.parseColor("#00A551"));
        mSendMsgTv.setHighlightColor(Color.TRANSPARENT);
    }

    private void showConfirmDialog() {

        final SimpleConfirmDialog dialog = new SimpleConfirmDialog(this);

        dialog.setTitleVisible(View.GONE);

        dialog.setContent("确定给用户发送短信");

        dialog.setOnNegativeListener(-1, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShow()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.setOnPositiveListener(-1, new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialog != null && dialog.isShow()) {
                    dialog.dismiss();
                }

                sendMessageToCustomer();
            }
        });

        dialog.show();
    }

    private void sendMessageToCustomer() {

        if (mDetailInfo == null || mDetailInfo.getOrderDes() == null) {
            return;
        }

        OrderDes orderDes = mDetailInfo.getOrderDes();

        mWaitingDialog.show("正在发送短信，请稍候...", false);

        BillApi billApi = KHttpWorker.ins().createHttpService(LoginApi.URL, BillApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("orderId", String.valueOf(orderDes.getOrderId()));

        final retrofit2.Call<JsonObject> request = billApi.sendMessage(params);

        request.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

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
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(context, "短信发送成功.");
                            }
                        });
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
                    }
                });
            }
        });
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
            case 4:
            case 3:

                if (!mDetailInfo.getOrderDes().isTwiceServed() && isDeviceBill) {
                    if (mImageList.size() > 0) {
                        FastJudgeActivity.goToActivity(OrderDetailActivity.this, false, mDetailInfo.getOrderDes().getEnterpriseOrderId(),
                                mDetailInfo.getOrderDes().getEnterpriseId(), mImageList, mDetailInfo.getOrderDes().getOrderId());
                    } else {
                        Toast.makeText(OrderDetailActivity.this, "请至少添加一张照片", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    toWaitPay(orderDes.getOriginType() == 2, orderDes.isFree());
                }

                break;
            case 5:
                toThirdStep(true, orderDes.getOriginType() == 2, orderDes.isFree());
                break;
        }

    }

    private void toWaitPay(boolean isNeedShowDialog, boolean isFree) {
        if (!isNeedShowDialog && !isFree) {
            toWaitPayReal();
        } else {
            showReadyAlertDialog(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveCompleteInfoOnly();
                }
            }, isFree);
        }
    }

    private void saveCompleteInfoOnly() {
        if (mDetailInfo == null || mDetailInfo.getOrderDes() == null || mDetailInfo.getMaintainInfo() == null) {
            return;
        }

        final OrderDes order = mDetailInfo.getOrderDes();
        String id = order.getOrderId();


        MaintainInfo maintainInfo = mDetailInfo.getMaintainInfo();

        if ("null".equals(maintainInfo.getFault()) || maintainInfo.getFault().length() == 0) {
            Utils.showShortToast(OrderDetailActivity.this, "请填写维修项完整信息");
            return;
        }

        mWaitingDialog.show("正在提交，请稍后..", false);

        BillApi billApi = KHttpWorker.ins().createHttpService(LoginApi.URL, BillApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(OrderDetailActivity.this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(OrderDetailActivity.this).getUserCode());
        params.put("orderId", id);
        params.put("totalAmount", maintainInfo.getTotalAmount());
        params.put("payAmount", maintainInfo.getPayAmount());

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mImageList.size(); i++) {
            builder.append(mImageList.get(i));
            if (i != mImageList.size() - 1) {
                builder.append(",");
            }
        }

        if (mImageList.size() > 0) {
            params.put("masterImgUrls", builder.toString());
        }

        retrofit2.Call<JsonObject> request = billApi.completePay(params);

        request.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                JsonObject object = response.body();
                final String msg = object.get("msg").getAsString();
                final int code = object.get("code").getAsInt();

                if (code == 0) {
                    if (mWaitingDialog != null) {
                        mWaitingDialog.dismiss();
                    }
                    finish();
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (mWaitingDialog != null) {
                                mWaitingDialog.dismiss();
                            }
                            Utils.showShortToast(OrderDetailActivity.this, msg + ": " + code);
                        }
                    });
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
                        Utils.showShortToast(OrderDetailActivity.this, "提交订单失败..");
                    }
                });
            }
        });
    }

    private void toWaitPayReal() {
        if (mDetailInfo == null || mDetailInfo.getOrderDes() == null || mDetailInfo.getMaintainInfo() == null) {
            return;
        }

        final OrderDes order = mDetailInfo.getOrderDes();
        String id = order.getOrderId();


        MaintainInfo maintainInfo = mDetailInfo.getMaintainInfo();

        if ("null".equals(maintainInfo.getFault()) || maintainInfo.getFault().length() == 0) {
            Utils.showShortToast(OrderDetailActivity.this, "请填写维修项完整信息");
            return;
        }

        mWaitingDialog.show("正在生成支付二维码，请稍后", false);

        BillApi billApi = KHttpWorker.ins().createHttpService(LoginApi.URL, BillApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(OrderDetailActivity.this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(OrderDetailActivity.this).getUserCode());
        params.put("orderId", id);
        params.put("totalAmount", maintainInfo.getTotalAmount());
        params.put("payAmount", maintainInfo.getPayAmount());

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mImageList.size(); i++) {
            builder.append(mImageList.get(i));
            if (i != mImageList.size() - 1) {
                builder.append(",");
            }
        }

        if (mImageList.size() > 0) {
            params.put("masterImgUrls", builder.toString());
        }

        retrofit2.Call<JsonObject> request = billApi.completePay(params);

        request.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                JsonObject object = response.body();
                final String msg = object.get("msg").getAsString();
                final int code = object.get("code").getAsInt();

                if (code == 0) {
                    toThirdStep(false, mDetailInfo.getOrderDes().getOriginType() == 2, mDetailInfo.getOrderDes().isFree());
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (mWaitingDialog != null) {
                                mWaitingDialog.dismiss();
                            }
                            Utils.showShortToast(OrderDetailActivity.this, msg + ": " + code);
                        }
                    });
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
                        Utils.showShortToast(OrderDetailActivity.this, "申请支付二维码失败.");
                    }
                });
            }
        });
    }

    private void showReadyAlertDialog(final OnClickListener listener, boolean isFree) {
        final ICFunSimpleAlertDialog dialog = new ICFunSimpleAlertDialog(this);
        dialog.setOnNegativeListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }

                if (listener != null) {
                    listener.onClick(v);
                }
            }
        });

        dialog.setOncancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (listener != null) {
                    listener.onClick(null);
                }
            }
        });

        if (isFree) {
            dialog.setContent("该订单无需支付");
        } else {
            dialog.setContent("如有增项费用，请督促客户在到位平台进行补差价！");
        }


        dialog.show();
    }

    private void toThirdStep(final boolean showDialog, boolean isNeedShowAlertDialog, boolean isFree) {

        if (isNeedShowAlertDialog || isFree) {
            showReadyAlertDialog(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }, isFree);
            return;
        }


        if (mDetailInfo == null || mDetailInfo.getMaintainInfo() == null || mDetailInfo.getOrderDes() == null) {
            return;
        }

        if (showDialog) {
            mWaitingDialog.show("正在申请支付链接 ..", false);
        }

        final String money = mDetailInfo.getMaintainInfo().getPayAmount();

        final OrderDes orderDes = mDetailInfo.getOrderDes();

        BillApi billApi = KHttpWorker.ins().createHttpService(LoginApi.URL, BillApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(OrderDetailActivity.this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(OrderDetailActivity.this).getUserCode());
        params.put("orderId", orderId);
        params.put("payType", String.valueOf(1));

        retrofit2.Call<JsonObject> request = billApi.getPayUrl(params);

        request.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                JsonObject object = response.body();
                final String msg = object.get("msg").getAsString();
                final int code = object.get("code").getAsInt();

                if (code == 0) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            mSaveButton.setText("支付");
                            orderDes.setProcessStatus(5);

                            if (mWaitingDialog != null) {
                                mWaitingDialog.dismiss();
                            }
                        }
                    });

                    String url = object.get("url").getAsString();

                    Intent intent = new Intent(OrderDetailActivity.this, OrderPayQRCodeActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("money", money);
                    OrderDetailActivity.this.startActivity(intent);

                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (mWaitingDialog != null) {
                                mWaitingDialog.dismiss();
                            }
                            Utils.showShortToast(OrderDetailActivity.this, msg + ": " + code);
                        }
                    });
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
                        Utils.showShortToast(OrderDetailActivity.this, "获取支付链接失败");
                    }
                });
            }
        });

    }

    private void acceptOperation() {

        if (mDetailInfo == null || mDetailInfo.getOrderDes() == null) {
            return;
        }

        final OrderDes order = mDetailInfo.getOrderDes();

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
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //order.setProcessStatus(2);
                                //toWaitStatus();
                                loadData(false);
                            }
                        });
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

    private ArrayList<String> mImageList = new ArrayList<>();

    private void startAdditionActivity() {
        Intent intent = new Intent();
        intent.setClass(context, OrderDetailAdditionActivity.class);
        intent.putExtra("order_id", String.valueOf(mOrderCode));
        intent.putExtra("add_item", mIssueReasonTv.getText());
        context.startActivityForResult(intent, Constant.ACTIVITY_ORDER_ADDITION_RESULT_CODE);
        context.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void commitImage(ArrayList<String> imageList) {

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < imageList.size(); i++) {
            if (i < imageList.size() - 1) {
                builder.append(imageList.get(i) + ",");
            } else {
                builder.append(imageList.get(i));
            }
        }

        params.put("masterImgUrls", builder.toString());
        params.put("orderId", mDetailInfo.getOrderDes().getOrderId());

        retrofit2.Call<JsonObject> call = recordApi.updateImage(params);

        call.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {

            }
        });
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
                case AddImageActivity.OP_ADD:
                    if (resultCode == RESULT_OK) {
                        ArrayList<String> list = data.getStringArrayListExtra("result");
                        if (list != null) {

                            mImageList.clear();
                            mImageList.addAll(list);

                            if (mDetailInfo != null && mDetailInfo.getMaintainInfo() != null) {
                                mDetailInfo.getMaintainInfo().setMasterImgUrls(mImageList);
                            }

                            commitImage(mImageList);

                        }
                    }
                    break;
                case ServiceBillActivity.SERVICE_BILL_CODE:
                    if (resultCode == RESULT_OK) {

                        if (mDetailInfo == null || mDetailInfo.getMaintainInfo() == null) {
                            return;
                        }

                        MaintainInfo maintainInfo = mDetailInfo.getMaintainInfo();

                        reasonTemp = data.getStringExtra(ServiceBillActivity.REASON);
                        planTemp = data.getStringExtra(ServiceBillActivity.STRATEGY);
                        costDetailTemp = data.getStringExtra(ServiceBillActivity.DETAIL);
                        totalCostTemp = data.getStringExtra(ServiceBillActivity.PRICE);

                        maintainInfo.setFault(data.getStringExtra(ServiceBillActivity.REASON));
                        maintainInfo.setPlan(data.getStringExtra(ServiceBillActivity.STRATEGY));
                        maintainInfo.setCostDetail(data.getStringExtra(ServiceBillActivity.DETAIL));
                        maintainInfo.setTotalCost(data.getStringExtra(ServiceBillActivity.PRICE));

                        updateMainTainUi(maintainInfo);
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

    private void updateMainTainUi(MaintainInfo maintainInfo) {
        mIssueReasonTv.setText(maintainInfo.getFault());
        mIssuePriceTv.setText(maintainInfo.getTotalCost());
        mStrategyContentTv.setText(maintainInfo.getPlan());
        mTotalPriceTv.setText("+ " + maintainInfo.getTotalCost());

        BigDecimal totalCost = new BigDecimal(Double.parseDouble(maintainInfo.getTotalCost()));
        BigDecimal serviceCost = new BigDecimal(mDetailInfo.getOrderDes().getServiceVisitCost());
        BigDecimal preCost = new BigDecimal(Double.parseDouble(maintainInfo.getPreCost()));

        maintainInfo.setTotalAmount(String.valueOf(totalCost.add(serviceCost).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
        mTotalTv.setText(maintainInfo.getTotalAmount());
        mPrePayPriceTv.setText("- " + maintainInfo.getPreCost());

        double payAmount = totalCost.add(serviceCost).subtract(preCost).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        payAmount = payAmount > 0 ? payAmount : 0.00;
        maintainInfo.setPayAmount(String.valueOf(payAmount));
        mFuKuanContentTv.setText(maintainInfo.getPayAmount());
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

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        if (mNewBillTimer != null) {
            mNewBillTimer.cancel();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

//        Intent intent = new Intent(this, RepairActivity.class);
//
//        startActivity(intent);
//
//        Utils.finishActivity(this);
    }
}

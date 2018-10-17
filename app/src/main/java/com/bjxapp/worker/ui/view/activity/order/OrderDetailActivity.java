package com.bjxapp.worker.ui.view.activity.order;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.OrderDetail;
import com.bjxapp.worker.model.XResult;
import com.bjxapp.worker.ui.view.activity.PublicImagesActivity;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.DateUtils;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.R;

import butterknife.ButterKnife;
import cn.qqtheme.framework.picker.DoublePicker;

public class OrderDetailActivity extends BaseActivity implements OnClickListener {

    protected static final String TAG = OrderDetailActivity.class.getSimpleName();

    private int mOrderCode;

    private XImageView mBackImageView;

    /* 通用状态 btn */
    private XTextView mStatusTv;

    /* 新订单 */
    private XTextView mBillNumTv;  // 订单号
    private XTextView mServiceNameTv; // 维修项目
    private XTextView mDateTv;
    private XTextView mAdressTv;
    private XTextView mPriceTv;
    private XTextView mRemarkTv;
    private XTextView mPhoneTv;

    /* 待预约 */



    private XTextView mOrderStatus, mOrderDate, mAddress, mContacts, mServiceName, mTotalMoney, mRemark;
    private XButton mAdditionEditButton;
    private XButton mWaitOkBtn, mWaitCancelBtn;
    ArrayList<String> mIDImageUrls;
    private XTextView mAdditionContent;
    private XButton mSaveButton;
    private XTextView mHourLastTv;

    LinearLayout mAdditionLinear, mIncomeLinear, mOrderImagesLinear;
    RelativeLayout mOrderFastLayout;

    LinearLayout mOrderWaitLy;

    private CountDownTimer mCountDownTimer;

    private XWaitingDialog mWaitingDialog;
    private TextView mCancelBillTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initControl() {

        initTitle();

        mOrderStatus = (XTextView) findViewById(R.id.order_receive_textview_status);
        mOrderDate = (XTextView) findViewById(R.id.order_receive_textview_orderdate);
        mAddress = (XTextView) findViewById(R.id.order_receive_textview_address);
        mContacts = (XTextView) findViewById(R.id.order_receive_textview_contact);
        mContacts.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mContacts.getPaint().setAntiAlias(true);
        // 代表服务名称
        mServiceName = (XTextView) findViewById(R.id.order_receive_textview_service);
        mTotalMoney = (XTextView) findViewById(R.id.order_receive_textview_money);
        mRemark = (XTextView) findViewById(R.id.order_receive_textview_remark);

        mAdditionEditButton = (XButton) findViewById(R.id.order_receive_detail_addition_edit);

        mAdditionContent = (XTextView) findViewById(R.id.order_receive_detail_addition_content);
        mAdditionLinear = (LinearLayout) findViewById(R.id.order_receive_detail_addition);
        mOrderImagesLinear = (LinearLayout) findViewById(R.id.order_receive_detail_images);
        mOrderWaitLy = findViewById(R.id.order_receiver_ly);
        mHourLastTv = findViewById(R.id.last_hour);
        mCancelBillTv = findViewById(R.id.cancel_bill);
        mCancelBillTv.setOnClickListener(this);

        mAdditionLinear.setVisibility(View.GONE);
        mIncomeLinear.setVisibility(View.GONE);
        mOrderImagesLinear.setVisibility(View.GONE);
        mOrderFastLayout.setVisibility(View.GONE);
        mOrderWaitLy.setVisibility(View.GONE);
        mHourLastTv.setVisibility(View.GONE);

        mSaveButton = (XButton) findViewById(R.id.order_receive_detail_save);
        mSaveButton.setEnabled(false);
        mAdditionEditButton.setEnabled(false);

        mWaitOkBtn = findViewById(R.id.wait_contact_ok_btn);
        mWaitCancelBtn = findViewById(R.id.wait_contact_change_btn);

        mWaitCancelBtn.setOnClickListener(this);
        mWaitOkBtn.setOnClickListener(this);

        mWaitingDialog = new XWaitingDialog(context);
    }

    private void initTitle(){
        XTextView mTitleTextView = (XTextView) findViewById(R.id.title_text_title);
        mTitleTextView.setText("订单详情");
        mBackImageView = (XImageView) findViewById(R.id.title_image_back);
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
        mContacts.setOnClickListener(this);
        mAdditionEditButton.setOnClickListener(this);
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
            case R.id.order_receive_detail_addition_edit:
                startAdditionActivity();
                break;
            case R.id.order_receive_detail_images:
                showUserImages();
                break;
            case R.id.order_receive_detail_save:
                SaveOperation();
                break;
            case R.id.wait_contact_ok_btn:
                toDetailStatus();
                break;
            case R.id.wait_contact_change_btn:
                changeDate();
                break;
            case R.id.cancel_bill:
                cancelBill();
                break;
            default:
                break;
        }
    }

    private void cancelBill() {
        CancelBillActivity.goToActivity(this);
    }

    private void toDetailStatus() {
        mOrderStatus.setText("已联系");
        mSaveButton.setText("完成");
        mSaveButton.setEnabled(true);
        mAdditionLinear.setVisibility(View.VISIBLE);
        mIncomeLinear.setVisibility(View.VISIBLE);
        mAdditionEditButton.setEnabled(true);
        mHourLastTv.setVisibility(View.GONE);
        mOrderWaitLy.setVisibility(View.GONE);
        mSaveButton.setVisibility(View.VISIBLE);
    }

    private AsyncTask<String, Void, OrderDetail> mLoadDataTask;

    private void loadData(final Boolean loading) {
        String orderID = getIntent().getStringExtra("order_id");
        if (!Utils.isNotEmpty(orderID)) {
            return;
        }

        if (loading) {
            mWaitingDialog.show("正在加载中，请稍候...", false);
        }

        mLoadDataTask = new AsyncTask<String, Void, OrderDetail>() {
            @Override
            protected OrderDetail doInBackground(String... params) {
                int id = Integer.valueOf(params[0]);
                return LogicFactory.getDesktopLogic(context).getOrderDetail(id);
            }

            @Override
            protected void onPostExecute(OrderDetail result) {
                if (loading) {
                    mWaitingDialog.dismiss();
                }

                changeStatus(result);
            }
        };

        mLoadDataTask.execute(orderID);
    }

    private void changeStatus(OrderDetail result) {

        if (result == null) {
            return;
        }

        if (result.getOrderType() == 1) {
            mOrderFastLayout.setVisibility(View.VISIBLE);
        }

        mOrderCode = result.getOrderID();

        mOrderDate.setText(result.getOrderDate() + " " + result.getOrderTime());
        mAddress.setText(result.getAddress() + result.getHouseNumber());
        mContacts.setText(result.getContacts() + " / " + result.getTelephone());
        mContacts.setTag(result.getTelephone());
        mServiceName.setText(result.getServiceSubName());
        if (Utils.isNotEmpty(result.getRemark())) {
            mRemark.setText(result.getRemark());
        } else {
            mRemark.setText("无备注");
        }
        if (Utils.isNotEmpty(result.getAddItem())) {
            mAdditionContent.setText(result.getAddItem());
        } else {
            mAdditionContent.setText("无");
        }

        String statusString = "";
        String feeInfo = "";
        switch (result.getOrderStatus()) {
            case 0:
                statusString = "新订单";
                feeInfo = "费用预估：";
                mSaveButton.setText("接单");
                mSaveButton.setEnabled(true);
                mAdditionLinear.setVisibility(View.GONE);
                mIncomeLinear.setVisibility(View.GONE);
                mAdditionEditButton.setEnabled(false);
                break;
            case 1:
                statusString = "已接单";
                feeInfo = "费用：";
                mSaveButton.setText("完成");
                mSaveButton.setEnabled(true);
                mAdditionLinear.setVisibility(View.VISIBLE);
                mIncomeLinear.setVisibility(View.VISIBLE);
                mAdditionEditButton.setEnabled(true);
                break;
            case 2:
                statusString = "待支付";
                feeInfo = "费用：";
                mSaveButton.setText("支付");
                mSaveButton.setEnabled(true);
                mAdditionLinear.setVisibility(View.VISIBLE);
                mIncomeLinear.setVisibility(View.VISIBLE);
                mAdditionEditButton.setVisibility(View.GONE);
                break;
            case 3:
                statusString = "已结算";
                feeInfo = "费用：";
                mSaveButton.setText("查看支付情况");
                mSaveButton.setEnabled(true);
                mAdditionLinear.setVisibility(View.VISIBLE);
                mIncomeLinear.setVisibility(View.VISIBLE);
                mAdditionEditButton.setVisibility(View.GONE);
                break;
            case 4:
                statusString = "已结算";
                feeInfo = "费用：";
                mSaveButton.setVisibility(View.GONE);
                mAdditionLinear.setVisibility(View.VISIBLE);
                mIncomeLinear.setVisibility(View.VISIBLE);
                mAdditionEditButton.setVisibility(View.GONE);
                break;
            case 98:
                statusString = "已取消";
                feeInfo = "费用：";
                mSaveButton.setVisibility(View.GONE);
                mAdditionLinear.setVisibility(View.GONE);
                mIncomeLinear.setVisibility(View.GONE);
                mAdditionEditButton.setVisibility(View.GONE);
                break;
            case 99:
                statusString = "异常";
                feeInfo = "费用：";
                mSaveButton.setVisibility(View.GONE);
                mAdditionLinear.setVisibility(View.GONE);
                mIncomeLinear.setVisibility(View.GONE);
                mAdditionEditButton.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        mSaveButton.setTag(result.getOrderStatus());
        mTotalMoney.setText(feeInfo + result.getTotalMoney() + "元");
        mOrderStatus.setText(statusString);

        mIDImageUrls = new ArrayList<String>();
        if (Utils.isNotEmpty(result.getImageOne().trim())) {
            mIDImageUrls.add(result.getImageOne().trim());
        }
        if (Utils.isNotEmpty(result.getImageTwo().trim())) {
            mIDImageUrls.add(result.getImageTwo().trim());
        }
        if (Utils.isNotEmpty(result.getImageThree().trim())) {
            mIDImageUrls.add(result.getImageThree().trim());
        }
        if (mIDImageUrls.size() > 0) {
            mOrderImagesLinear.setVisibility(View.VISIBLE);
        }
    }

    private void SaveOperation() {
        if (!Utils.isNetworkAvailable(context)) {
            Utils.showShortToast(context, getString(R.string.common_no_network_message));
            return;
        }

        if (mSaveButton.getTag() == null) return;

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
        }
    }

    private void acceptOperation() {
        String orderID = getIntent().getStringExtra("order_id");
        if (!Utils.isNotEmpty(orderID)) {
            return;
        }

        mWaitingDialog.show("正在接单，请稍候...", false);
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
                   /* Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    Utils.finishWithoutAnim(OrderDetailActivity.this);*/
                    changeStatusToWaitTime();
                } else {
                    Utils.showShortToast(context, "接单失败，请重试！");
                }
            }

        }.execute(orderID);
    }


    private void changeStatusToWaitTime() {
        mHourLastTv.setVisibility(View.VISIBLE);

        if (mSaveButton != null) {
            mSaveButton.setVisibility(View.GONE);
        }

        mOrderWaitLy.setVisibility(View.VISIBLE);

        mCountDownTimer = new CountDownTimer(5000, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                mHourLastTv.setText(String.valueOf(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                mHourLastTv.setText("已超时");
            }
        };

        mCountDownTimer.start();


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
        intent.putExtra("add_item", mAdditionContent.getText());
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
        if (mContacts.getTag() == null) return;
        String mobile = mContacts.getTag().toString();
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

    private void changeDate() {
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

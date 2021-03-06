package com.bjxapp.worker.ui.view.activity.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bjxapp.worker.App;
import com.bjx.master.R;;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.BillApi;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.MaintainInfo;
import com.bjxapp.worker.ui.view.activity.widget.dialog.ICFunSimpleAlertDialog;
import com.bjxapp.worker.utils.CashReg;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zhangdan on 2018/10/14.
 * <p>
 * comments:
 */
public class ServiceBillActivity extends Activity implements View.OnClickListener {

    private XButton mConfirmTv;
    private EditText mReasonTv;
    private EditText mDoTv;
    private EditText mPriceTv;

    @BindView(R.id.total_price_edit_tv)
    EditText mTotalPriceTv;

    private XWaitingDialog mWaitingDialog;

    public static final String REASON = "reason";
    public static final String STRATEGY = "strategy";
    public static final String DETAIL = "detail";
    public static final String PRICE = "price";
    public static final String PRE_PRICE = "pre_price";
    public static final String ORDER_ID = "order_id";

    public static final int SERVICE_BILL_CODE = 0x03;

    @OnClick(R.id.title_image_back)
    void onClickBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTv;

    String prePrice;

    private String orderId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_bill);
        ButterKnife.bind(this);
        initView();
        handleIntent();
    }

    private void handleIntent() {
        if (getIntent() != null) {

            orderId = getIntent().getStringExtra(ORDER_ID);

            String reason = getIntent().getStringExtra(REASON);
            mReasonTv.setText(TextUtils.isEmpty(reason) ? "" : reason);
            String strategy = getIntent().getStringExtra(STRATEGY);
            mDoTv.setText(TextUtils.isEmpty(strategy) ? "" : strategy);
            String detail = getIntent().getStringExtra(DETAIL);
            mPriceTv.setText(TextUtils.isEmpty(detail) ? "" : detail);
            String price = getIntent().getStringExtra(PRICE);

            if (Double.parseDouble(price) != 0) {
                mTotalPriceTv.setText(TextUtils.isEmpty(price) ? "" : price);
            }

            prePrice = getIntent().getStringExtra(PRE_PRICE);
        }
    }

    private void initView() {
        mConfirmTv = findViewById(R.id.confirm);
        mReasonTv = findViewById(R.id.mistake_reason_tv);
        mDoTv = findViewById(R.id.mistake_do_tv);
        mPriceTv = findViewById(R.id.mistake_bill_tv);
        mConfirmTv.setOnClickListener(this);
        mTitleTv.setText("维修项");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                tryToCommit();
                break;
        }
    }

    public void tryToCommit() {

        if (mDoTv.getText().length() == 0
                || mReasonTv.getText().length() == 0
                || mPriceTv.getText().length() == 0 || mTotalPriceTv.getText().length() == 0) {
            final ICFunSimpleAlertDialog dialog = new ICFunSimpleAlertDialog(this);
            dialog.setOnNegativeListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            dialog.setContent("请填写完整的信息");
            dialog.show();
        } else if (!CashReg.isCashValid(mTotalPriceTv.getText().toString())) {
            final ICFunSimpleAlertDialog dialog = new ICFunSimpleAlertDialog(this);
            dialog.setOnNegativeListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            dialog.setContent("金额格式不正确");
            dialog.show();
        } else if (Double.parseDouble(mTotalPriceTv.getText().toString()) > 9999) {
            final ICFunSimpleAlertDialog dialog = new ICFunSimpleAlertDialog(this);
            dialog.setOnNegativeListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            dialog.setContent("订单总额度不能大于9999");
            dialog.show();
        } else if (!TextUtils.isEmpty(prePrice) &&
                Double.parseDouble(mTotalPriceTv.getText().toString()) < Double.parseDouble(prePrice)) {
            final ICFunSimpleAlertDialog dialog = new ICFunSimpleAlertDialog(this);
            dialog.setOnNegativeListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            dialog.setContent("总订单额度不能小于预付额度");
            dialog.show();
        } else {
            commitReal();
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private void commitReal() {

        BillApi billApi = KHttpWorker.ins().createHttpService(LoginApi.URL, BillApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("orderId", orderId);
        params.put("fault", mReasonTv.getText().toString());
        params.put("plan", mDoTv.getText().toString());
        params.put("costDetail", mPriceTv.getText().toString());
        params.put("totalCost", mTotalPriceTv.getText().toString());

        Call<JsonObject> request = billApi.saveMaintain(params);

        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    JsonObject object = response.body();
                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.putExtra(REASON, mReasonTv.getText().toString());
                                intent.putExtra(STRATEGY, mDoTv.getText().toString());
                                intent.putExtra(PRICE, mTotalPriceTv.getText().toString());
                                intent.putExtra(DETAIL, mPriceTv.getText().toString());
                                setResult(RESULT_OK, intent);
                                Utils.finishWithoutAnim(ServiceBillActivity.this);
                            }
                        });
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(ServiceBillActivity.this, msg + " : " + code);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    public static void goToActivity(Activity context, int code, MaintainInfo maintainInfo, String orderId) {

        Intent intent = new Intent();
        intent.setClass(context, ServiceBillActivity.class);
        intent.putExtra(REASON, maintainInfo.getFault());
        intent.putExtra(STRATEGY, maintainInfo.getPlan());
        intent.putExtra(DETAIL, maintainInfo.getCostDetail());
        intent.putExtra(PRICE, maintainInfo.getTotalCost());
        intent.putExtra(PRE_PRICE, maintainInfo.getPreCost());
        intent.putExtra(ORDER_ID, orderId);
        context.startActivityForResult(intent, code);
    }
}

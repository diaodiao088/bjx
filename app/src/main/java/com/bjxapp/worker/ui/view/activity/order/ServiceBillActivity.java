package com.bjxapp.worker.ui.view.activity.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bjxapp.worker.App;
import com.bjxapp.worker.R;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.model.MaintainInfo;
import com.bjxapp.worker.ui.view.activity.widget.dialog.ICFunSimpleAlertDialog;
import com.bjxapp.worker.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    public static final int SERVICE_BILL_CODE = 0x03;

    @OnClick(R.id.title_image_back)
    void onClickBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTv;

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
            String reason = getIntent().getStringExtra(REASON);
            mReasonTv.setText(TextUtils.isEmpty(reason) ? "" : reason);
            String strategy = getIntent().getStringExtra(STRATEGY);
            mDoTv.setText(TextUtils.isEmpty(strategy) ? "" : strategy);
            String detail = getIntent().getStringExtra(STRATEGY);
            mPriceTv.setText(TextUtils.isEmpty(detail) ? "" : detail);
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
        } else {
            commitReal();
        }
    }

    private void commitReal() {

        Intent intent = new Intent();
        intent.putExtra(REASON, mReasonTv.getText().toString());
        intent.putExtra(STRATEGY, mDoTv.getText().toString());
        intent.putExtra(PRICE, mTotalPriceTv.getText().toString());
        intent.putExtra(DETAIL, mPriceTv.getText().toString());

        setResult(RESULT_OK, intent);
        Utils.finishWithoutAnim(ServiceBillActivity.this);
    }

    public static void goToActivity(Activity context, int code, MaintainInfo maintainInfo) {

        Intent intent = new Intent();
        intent.setClass(context, ServiceBillActivity.class);
        intent.putExtra(REASON, maintainInfo.getFault());
        intent.putExtra(STRATEGY, maintainInfo.getPlan());
        intent.putExtra(DETAIL, maintainInfo.getCostDetail());
        context.startActivityForResult(intent, code);
    }
}

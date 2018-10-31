package com.bjxapp.worker.ui.view.activity.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.bjxapp.worker.App;
import com.bjxapp.worker.R;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.ui.view.activity.widget.dialog.ICFunSimpleAlertDialog;

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
    private XWaitingDialog mWaitingDialog;

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
                || mPriceTv.getText().length() == 0) {
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

    }

    public static void goToActivity(Context context) {

        if (context == null) {
            context = App.getInstance();
        }

        Intent intent = new Intent();
        intent.setClass(context, ServiceBillActivity.class);
        context.startActivity(intent);
    }
}

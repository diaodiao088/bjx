package com.bjxapp.worker.ui.view.activity.order;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.bjxapp.worker.R;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.ui.view.activity.widget.dialog.ICFunSimpleAlertDialog;

/**
 * Created by zhangdan on 2018/10/14.
 * comments:
 */

public class ServiceBillActivity extends Activity implements View.OnClickListener {

    private XButton mConfirmTv;

    private EditText mReasonTv;
    private EditText mDoTv;
    private EditText mPriceTv;

    private XWaitingDialog mWaitingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_bill);
        initView();
    }

    private void initView() {
        mConfirmTv = findViewById(R.id.confirm);
        mReasonTv = findViewById(R.id.mistake_reason_tv);
        mDoTv = findViewById(R.id.mistake_do_tv);
        mPriceTv = findViewById(R.id.mistake_bill_tv);
        mConfirmTv.setOnClickListener(this);
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
        }else{
            commitReal();
        }
    }

    private void commitReal(){

    }

}

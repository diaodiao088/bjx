package com.bjxapp.worker.ui.view.activity.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bjxapp.worker.R;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.ui.view.activity.widget.dialog.ICFunSimpleAlertDialog;

/**
 * Created by zhangdan on 2018/10/11.
 * <p>
 * comments:
 */

public class CancelBillActivity extends Activity implements View.OnClickListener {

    private EditText mEditTv;
    private TextView mConfirmTv;
    private TextView mContentLimitTv;
    private XWaitingDialog mWaitDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_bill);
        mEditTv = findViewById(R.id.cancel_content);
        mConfirmTv = findViewById(R.id.confirm);
        mContentLimitTv = findViewById(R.id.content_limit);
        initListener();
    }

    private void initListener() {
        mConfirmTv.setOnClickListener(this);

        mEditTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {




            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int textSum = s.toString().length();

                if (textSum <= 200) {
                    mContentLimitTv.setText(textSum + "/200");
                } else {

                }
            }
        });

    }

    public static void goToActivity(Context context) {

        Intent intent = new Intent();

        intent.setClass(context, CancelBillActivity.class);

        context.startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:

                if (mEditTv.getText().length() <= 0) {
                    final ICFunSimpleAlertDialog dialog = new ICFunSimpleAlertDialog(this);
                    dialog.setOnNegativeListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });
                    dialog.setContent("编辑内容不能为空");
                    dialog.show();
                }

                break;
        }
    }
}

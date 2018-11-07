package com.bjxapp.worker.ui.view.activity.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bjxapp.worker.App;
import com.bjxapp.worker.MainActivity;
import com.bjxapp.worker.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.BillApi;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.ui.view.activity.widget.dialog.ICFunSimpleAlertDialog;
import com.bjxapp.worker.ui.view.fragment.ctrl.DataManagerCtrl;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @BindView(R.id.title_text_tv)
    TextView mTitleTv;

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    private String orderId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_bill);
        ButterKnife.bind(this);
        handleIntent();
        mEditTv = findViewById(R.id.cancel_content);
        mConfirmTv = findViewById(R.id.confirm);
        mContentLimitTv = findViewById(R.id.content_limit);
        mTitleTv.setText("取消订单");
        mWaitDialog = new XWaitingDialog(this);
        initListener();
    }

    private void handleIntent() {

        Intent intent = getIntent();

        if (intent != null) {
            orderId = intent.getStringExtra("order_id");
        }
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

    public static void goToActivity(Context context , String orderId) {
        Intent intent = new Intent();
        intent.setClass(context, CancelBillActivity.class);
        intent.putExtra("order_id",orderId);
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
                } else {
                    startCommit();
                }
                break;
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private void startCommit() {

        mWaitDialog.show("正在退单，请稍候...", false);

        BillApi billApi = KHttpWorker.ins().createHttpService(LoginApi.URL, BillApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("orderId", orderId);
        params.put("isReceived", String.valueOf(false));
        params.put("reason",mEditTv.getText().toString());

        final retrofit2.Call<JsonObject> request = billApi.acceptOrder(params);

        request.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mWaitDialog != null) {
                            mWaitDialog.dismiss();
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
                                Utils.showShortToast(CancelBillActivity.this, "退单成功");

                                Intent intent = new Intent(CancelBillActivity.this, MainActivity.class);
                                DataManagerCtrl.getIns().markDataDirty(true);
                                CancelBillActivity.this.startActivity(intent);
                            }
                        });
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(CancelBillActivity.this, msg + " : " + code);
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
                        if (mWaitDialog != null) {
                            mWaitDialog.dismiss();
                        }
                        Utils.showShortToast(CancelBillActivity.this, "接单失败，请重试！");
                    }
                });
            }
        });

    }


}

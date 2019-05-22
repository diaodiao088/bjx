package com.bjxapp.worker.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bjx.master.R;
import com.bjxapp.worker.model.MainTainBean;
import com.bjxapp.worker.model.OtherPriceBean;
import com.bjxapp.worker.ui.view.activity.MaintainActivity;
import com.bjxapp.worker.ui.view.activity.RecordAddActivity;
import com.bjxapp.worker.ui.view.activity.widget.dialog.AddOtherPriceDialog;

public class OtherPriceLayout extends LinearLayout {

    private TextView mSubNameTv, mPriceTv;

    private TextView mModifyTv;

    private View mRootView;

    private OtherPriceBean itemBean;

    public OtherPriceLayout(Context context) {
        super(context);
        init();
    }

    public OtherPriceLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OtherPriceLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        LayoutInflater.from(getContext()).inflate(R.layout.other_price_sub_layout, this);

        mSubNameTv = findViewById(R.id.sub_name);
        mPriceTv = findViewById(R.id.price_tv);

        mModifyTv = findViewById(R.id.modify);

        mModifyTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                final AddOtherPriceDialog otherPriceDialog = new AddOtherPriceDialog(getContext());

                otherPriceDialog.setPreviousData(itemBean);

                otherPriceDialog.setCancelable(true);

                otherPriceDialog.setOnNegativeListener("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        otherPriceDialog.dismiss();
                    }
                }).setOnPositiveListener("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!otherPriceDialog.isDataValid()) {
                            Toast.makeText(getContext(), "请填写完整数据", Toast.LENGTH_SHORT).show();
                        } else {
                            OtherPriceBean validBean = otherPriceDialog.getValidBean();

                            itemBean.setPrice(validBean.getPrice());
                            itemBean.setName(validBean.getName());

                            mSubNameTv.setText(itemBean.getName());
                            mPriceTv.setText("¥" + itemBean.getPrice());

                            otherPriceDialog.dismiss();
                        }
                    }
                });

                otherPriceDialog.show();

            }
        });
    }

    public void bindData(final OtherPriceBean itemBean, final String shopId) {
        this.itemBean = itemBean;
        mSubNameTv.setText(itemBean.getName());
        mPriceTv.setText("¥" + itemBean.getPrice());

        mSubNameTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final ViewParent viewParent = getParent();
                if (viewParent != null && viewParent instanceof ViewGroup) {
                    ((ViewGroup) viewParent).removeViewInLayout(OtherPriceLayout.this);

                    viewParent.requestLayout();
                }

                if (listener != null) {
                    listener.onDelete(itemBean);
                }
            }
        });
    }

    public interface OnOperationListener {

        void onDelete(OtherPriceBean priceBean);

    }

    public OnOperationListener listener;

    public void setOperationListener(OnOperationListener listener) {
        this.listener = listener;
    }


}

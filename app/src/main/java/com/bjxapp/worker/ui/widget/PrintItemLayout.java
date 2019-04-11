package com.bjxapp.worker.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjx.master.R;
import com.dothantech.printer.IDzPrinter;

public class PrintItemLayout extends LinearLayout {

    private TextView mSubNameTv, mSubStatusTv;

    private View mRootView;

    private IDzPrinter.PrinterAddress printerAddress;

    public PrintItemLayout(Context context) {
        super(context);
        init();
    }

    public PrintItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PrintItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.search_print_sub_layout, this);

        mSubNameTv = findViewById(R.id.sub_name);
        mSubStatusTv = findViewById(R.id.sub_status);
    }

    public void bindData(final IDzPrinter.PrinterAddress itemBean) {
        this.printerAddress = itemBean;

        if (printerAddress != null) {
            mSubNameTv.setText(printerAddress.shownName);

            mRootView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.tryToConnectPrinter(printerAddress);
                    }
                }
            });
        }
    }


    public OnBlueClickListener listener;

    public void setOnBlueClickListener(OnBlueClickListener listener){
        this.listener = listener;
    }

    public interface OnBlueClickListener {

        void tryToConnectPrinter(IDzPrinter.PrinterAddress printerAddress);

    }

}

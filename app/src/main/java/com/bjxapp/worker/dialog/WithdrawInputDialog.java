package com.bjxapp.worker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.bjx.master.R;;
import com.bjxapp.worker.controls.XButton;

public class WithdrawInputDialog extends Dialog {

    private Context context;
    private ClickListenerInterface clickListenerInterface;

    public interface ClickListenerInterface {

        public void doConfirm();
    }

    public WithdrawInputDialog(Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    public void initialize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_balance_withdraw_input, null);
        setContentView(view);

        XButton confirmButton = (XButton) view.findViewById(R.id.balance_withdraw_input_save);
        confirmButton.setOnClickListener(new clickListener());
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.balance_withdraw_input_save:
                    clickListenerInterface.doConfirm();
                    break;
            }
        }
    }

    ;
}

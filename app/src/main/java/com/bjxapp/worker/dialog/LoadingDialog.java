package com.bjxapp.worker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bjx.master.R;;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.utils.Utils;

public class LoadingDialog extends Dialog {

    private Context context;
    private String mMessage = "";
    
    public LoadingDialog(Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;
    }
    
    public LoadingDialog(Context context, String message) {
        super(context, R.style.CustomDialog);
        this.context = context;
        mMessage = message;
    }
    
    public void setMessage(String message) {
		mMessage = message;
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    public void initialize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_common_waiting, null);
        setContentView(view);
        
        XTextView messageTextView = (XTextView) view.findViewById(R.id.dialog_common_waiting_message);
        if(Utils.isNotEmpty(mMessage)){
        	messageTextView.setText(mMessage);
        }  
        
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); 
        lp.width = (int) (d.widthPixels * 0.8); 
        dialogWindow.setAttributes(lp);
    }

}

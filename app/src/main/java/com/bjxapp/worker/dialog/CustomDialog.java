package com.bjxapp.worker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.bjxapp.worker.R;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.utils.Utils;

public class CustomDialog extends Dialog {

    private Context context;
    private ClickListenerInterface clickListenerInterface;
    
    private String mDialogTitle = "";
    private String mDialogContent = "";
    private Boolean mShowCancelButton = false;

    public interface ClickListenerInterface {

        public void doConfirm();
        public void doCancel();
    }

    public CustomDialog(Context context, String content) {
        super(context, R.style.CustomDialog);
        this.context = context;
        mDialogContent = content;
    }
    
    public CustomDialog(Context context, String title, String content) {
        super(context, R.style.CustomDialog);
        this.context = context;
        mDialogContent = content;
        mDialogTitle = title;
    }
    
    public CustomDialog(Context context, String title, String content, Boolean showCancelButton) {
        super(context, R.style.CustomDialog);
        this.context = context;
        mDialogContent = content;
        mDialogTitle = title;
        mShowCancelButton = showCancelButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    public void initialize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_common_message, null);
        setContentView(view);

        if(!Utils.isNotEmpty(mDialogTitle)){
        	mDialogTitle = context.getString(R.string.app_name);
        }
        if(mDialogContent != null){
        	mDialogContent = "        " + mDialogContent.trim();
        }
        else{
        	mDialogContent = "王八蛋程序员竟然传了个null值！";
        }
        
        XTextView titleTextView = (XTextView) view.findViewById(R.id.dialog_common_message_title);
        titleTextView.setText(mDialogTitle);
        XTextView contentTextView = (XTextView) view.findViewById(R.id.dialog_common_message_content);
        contentTextView.setText(mDialogContent);
        
        XButton confirmButton = (XButton) view.findViewById(R.id.dialog_common_message_button_ok);
        XButton cancelButton = (XButton) view.findViewById(R.id.dialog_common_message_button_cancel);
        if(mShowCancelButton){
        	cancelButton.setVisibility(View.VISIBLE);
        }
        else{
        	cancelButton.setVisibility(View.GONE);
        }
        
        confirmButton.setOnClickListener(new clickListener());
        cancelButton.setOnClickListener(new clickListener());

        //Window dialogWindow = getWindow();
        //WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        //获取屏幕宽、高用
        //DisplayMetrics d = context.getResources().getDisplayMetrics(); 
        //宽度设置为屏幕的0.8,高度设置为屏幕的0.6
        //lp.width = (int) (d.widthPixels * 0.8); 
        //lp.height = (int) (d.heightPixels * 0.6);
        //dialogWindow.setAttributes(lp);
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
	            case R.id.dialog_common_message_button_ok:
	                clickListenerInterface.doConfirm();
	                break;
	            case R.id.dialog_common_message_button_cancel:
	                clickListenerInterface.doCancel();
	                break;    
            }
        }
    };
}

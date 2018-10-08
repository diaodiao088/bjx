package com.bjxapp.worker.controls;

import android.app.ProgressDialog;
import android.content.Context;

public class XWaitingDialog{

	private ProgressDialog mProgressDialog;
	
	public XWaitingDialog(Context context) {
		mProgressDialog = new ProgressDialog(context);
	}
	
	public void show(String message, Boolean canCancel){
		mProgressDialog.setMessage(message);
		mProgressDialog.setCancelable(canCancel);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		if(canCancel){
			mProgressDialog.setCanceledOnTouchOutside(false);
		}
		mProgressDialog.show();
	}
	
	public void dismiss(){
		mProgressDialog.dismiss();
	}
}

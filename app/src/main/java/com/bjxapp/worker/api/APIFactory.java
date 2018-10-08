package com.bjxapp.worker.api;

import android.content.Context;

import com.bjxapp.worker.api.impl.AccountAPIImpl;
import com.bjxapp.worker.api.impl.DesktopAPIImpl;
import com.bjxapp.worker.api.impl.MessageAPIImpl;
import com.bjxapp.worker.api.impl.UpdateAPIImpl;
import com.bjxapp.worker.api.impl.UploadImagesAPIImpl;

public class APIFactory {
	public synchronized static IUpdateAPI getUpdateAPI(Context context) {
		return UpdateAPIImpl.getInstance(context);
	}
	
	public synchronized static IMessageAPI getMessageAPI(Context context) {
		return MessageAPIImpl.getInstance(context);
	}
	
	public synchronized static IAccountAPI getAccountAPI(Context context) {
		return AccountAPIImpl.getInstance(context);
	}
	
	public synchronized static IUploadImagesAPI getUploadImagesAPI(Context context) {
		return UploadImagesAPIImpl.getInstance(context);
	}
	
	public synchronized static IDesktopAPI getDesktopAPI(Context context) {
		return DesktopAPIImpl.getInstance(context);
	}
	
}

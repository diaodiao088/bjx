package com.bjxapp.worker.logic;

import android.content.Context;

import com.bjxapp.worker.logic.IUserLogic;
import com.bjxapp.worker.logic.impl.AccountLogicImpl;
import com.bjxapp.worker.logic.impl.ConsultLogicImpl;
import com.bjxapp.worker.logic.impl.DesktopLogicImpl;
import com.bjxapp.worker.logic.impl.MessageLogicImpl;
import com.bjxapp.worker.logic.impl.NotificationLogicImpl;
import com.bjxapp.worker.logic.impl.UpdateLogicImpl;
import com.bjxapp.worker.logic.impl.UploadImagesLogicImpl;
import com.bjxapp.worker.logic.impl.UserLogicImpl;

public class LogicFactory {
	public synchronized static IAccountLogic getAccountLogic(Context context) {
		return AccountLogicImpl.getInstance(context);
	} 
	
	public synchronized static IUpdateLogic getUpdateLogic(Context context) {
		return UpdateLogicImpl.getInstance(context);
	}
	
	public synchronized static IUserLogic getUserLogic(Context context) {
		return UserLogicImpl.getInstance(context);
	} 
	
	public synchronized static INotificationLogic getNotificationLogic(Context context) {
		return NotificationLogicImpl.getInstance(context);
	} 
	
	public synchronized static IMessageLogic getMessageLogic(Context context) {
		return MessageLogicImpl.getInstance(context);
	} 

	public synchronized static IUploadImagesLogic getUploadImagesLogic(Context context) {
		return UploadImagesLogicImpl.getInstance(context);
	} 
	
	public synchronized static IDesktopLogic getDesktopLogic(Context context) {
		return DesktopLogicImpl.getInstance(context);
	}
	
	public synchronized static IConsultLogic getConsultLogic(Context context) {
		return ConsultLogicImpl.getInstance(context);
	} 
}

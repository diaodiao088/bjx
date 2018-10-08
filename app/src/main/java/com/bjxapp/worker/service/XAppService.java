package com.bjxapp.worker.service;

import com.bjxapp.worker.push.XPushManager;
import com.bjxapp.worker.receiver.ReceiverHandler;
import com.bjxapp.worker.service.timer.TimerTaskFactory;
import com.bjxapp.worker.service.timer.TimerTaskManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class XAppService extends Service {

	//定时轮询管理器
	TimerTaskManager mPollingManager;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		startPush();
		startAmPolling();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent,startId);
		ReceiverHandler.handleLongTimeConsumingReceiver(intent, this.getApplicationContext());
	}
	
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    private void startAmPolling() {
    	mPollingManager = TimerTaskManager.getInstance(this);
        //mPollingManager.registerTask(TimerTaskFactory.getFourHoursPollingTask(this));
        mPollingManager.registerTask(TimerTaskFactory.getOneDayPollingTask(this));
        mPollingManager.registerTask(TimerTaskFactory.getOneHourPollingTask(this));
    }
    
    private void startPush(){
    	XPushManager.startPush(getApplicationContext());
    }
}

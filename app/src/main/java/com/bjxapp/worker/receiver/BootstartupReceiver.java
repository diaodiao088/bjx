package com.bjxapp.worker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bjxapp.worker.service.ServiceManager;

public class BootstartupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) 
    {
        //启动服务
        ServiceManager.startServices(context);
    }

}

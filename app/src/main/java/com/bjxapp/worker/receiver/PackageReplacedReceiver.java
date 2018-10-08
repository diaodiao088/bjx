package com.bjxapp.worker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bjxapp.worker.service.ServiceManager;

/**
 * 应用被替换，通常出现在升级情况下
 *
 * @author jason
 */
public class PackageReplacedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
         	
        String packageName = intent.getDataString().substring(8);
        if (packageName.equals(context.getPackageName())) 
        {
            ServiceManager.startServices(context);
        }
    }
}

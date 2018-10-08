package com.bjxapp.worker.service;

import android.content.Context;
import android.content.Intent;

public class ServiceManager {

    public static void startServices(Context context) {
        context.startService(getXAppServiceIntent(context));;
    }
    
    public static Intent getXAppServiceIntent(Context context) {
        return new Intent(context, XAppService.class);
    }
}

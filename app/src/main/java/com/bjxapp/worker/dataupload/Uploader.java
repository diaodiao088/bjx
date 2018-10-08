package com.bjxapp.worker.dataupload;

import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import android.content.Context;

public class Uploader {

	 /**
     * 在Application create时调用，执行初始化操作，传入的Context必须是ApplicationContext
     *
     * @param context
     */
    public static void onApplicationStart(Context context) {
    	StatService.setSessionTimeOut(10);
    	StatService.setLogSenderDelayed(10);
        StatService.setSendLogStrategy(context, SendStrategyEnum.APP_START, 1, true);
        StatService.setDebugOn(false);
    }
    
    public static void onActivityResume(Context context){
    	StatService.onResume(context);
    }
    
    public static void onActivityPause(Context context){
    	StatService.onPause(context);
    }
    
    public static void onPageStart(Context context,String pageName){
    	StatService.onPageStart(context, pageName);
    }
    
    public static void onPageEnd(Context context,String pageName){
    	StatService.onPageEnd(context, pageName);
    }
    
    public static void onEvent(Context context,String eventID,String label,int account){
    	StatService.onEvent(context, eventID, label, account);
    }
    
    public static void onEvent(Context context,String eventID,String label){
    	StatService.onEvent(context, eventID, label);
    }
    
    public static void onEventStart(Context context,String eventID,String label){
    	StatService.onEventStart(context, eventID, label);
    }
    
    public static void onEventEnd(Context context,String eventID,String label){
    	StatService.onEventEnd(context, eventID, label);
    }
    
    public static void onEventDuration(Context context,String eventID,String label,int milliseconds){
    	StatService.onEventDuration(context, eventID, label, milliseconds);
    }
    
}

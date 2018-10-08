package com.bjxapp.worker.push;

import java.util.ArrayList;
import java.util.List;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.bjxapp.worker.utils.Utils;

import android.content.Context;

public class XPushManager {

    public static void startPush(Context context) {
    	String apiKey = "gRf9cSBNKO9QikILWySDrFADlKm0hd2d";
    	PushManager.startWork(context, PushConstants.LOGIN_TYPE_API_KEY, apiKey);
    }
    
    public static void stopPush(Context context){
    	PushManager.stopWork(context);
    }
    
    public static void resumePush(Context context){
    	PushManager.resumeWork(context);
    }
    
    public static void setTags(Context context, String tagString){
    	if(!Utils.isNotEmpty(tagString))
    		return;
    	
    	String[] stringTags = tagString.split(",");
    	List<String> listTags = new ArrayList<String>();
    	for(int i=0;i<stringTags.length;i++){
    		listTags.add(stringTags[i]);
    	}
    	
    	PushManager.setTags(context, listTags);
    }
    
    public static void deleteTags(Context context, String tagString){
    	if(!Utils.isNotEmpty(tagString))
    		return;
    	
    	String[] stringTags = tagString.split(",");
    	List<String> listTags = new ArrayList<String>();
    	for(int i=0;i<stringTags.length;i++){
    		listTags.add(stringTags[i]);
    	}
    	
    	PushManager.delTags(context, listTags);
    }

    public static void showTags(Context context){
    	PushManager.listTags(context);
    }
    
    /** 
     Push: 设置免打扰时段
     startHour startMinute：开始 时间 ，24小时制，取值范围 0~23 0~59
     endHour endMinute：结束 时间 ，24小时制，取值范围 0~23 0~59
    */
    public static void setNoDisturbMode(Context context, int startHour, int startMinute, int endHour, int endMinute){
    	PushManager.setNoDisturbMode(context, startHour, startMinute, endHour, endMinute);
    }
    
}

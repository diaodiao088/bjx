package com.bjxapp.worker.push;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.bjxapp.worker.MainActivity;
import com.bjxapp.worker.SplashActivity;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.ui.view.activity.MessageDetailActivity;
import com.bjxapp.worker.ui.view.activity.WebViewActivity;
import com.bjxapp.worker.ui.view.activity.order.OrderPaySuccessActivity;
import com.bjxapp.worker.utils.Utils;
import com.bjx.master.R;;

import android.content.Context;
import android.content.Intent;

public class XPushUtils {

    public static void parsePushNotificationArrived(Context context, String customContent) {
        JSONObject customJson = null;
        try {
            customJson = new JSONObject(customContent);
            int pushType = 0;
            if (!customJson.isNull("push_type")) {
                pushType = Integer.parseInt(customJson.getString("push_type"));
            }
            
            String businessID = "";
            if (!customJson.isNull("business_id")) {
            	businessID = customJson.getString("business_id");
            }
            
            String extra = "";
            if (!customJson.isNull("extra")) {
            	extra = customJson.getString("extra");
            }
            
            String description = "";
            if (!customJson.isNull("description")) {
            	description = customJson.getString("description").trim();
            }
            
            String content = "";
            switch(pushType){	
        	case Constant.PUSH_TYPE_MESSAGE_NEW:
        		content = Utils.isNotEmpty(description) ? description : "您有一条新通知！";
        		sendPushBroadcast(context, Constant.PUSH_ACTION_MESSAGE_MODIFIED, content, extra, pushType, businessID);
        		break;
        	case Constant.PUSH_TYPE_ORDER_NEW:
        		content = Utils.isNotEmpty(description) ? description : "您有一个新订单！";
        		sendPushBroadcast(context, Constant.PUSH_ACTION_ORDER_MODIFIED, content, extra, pushType, businessID);
        		break;	
        	case Constant.PUSH_TYPE_ORDER_PAY:
        		content = Utils.isNotEmpty(description) ? description : "您有一个订单支付成功了！";
        		sendPushBroadcast(context, Constant.PUSH_ACTION_ORDER_MODIFIED, content, extra, pushType, businessID);
        		break;
        	case Constant.PUSH_TYPE_ORDER_SCORE:
        		content = Utils.isNotEmpty(description) ? description : "您有一个订单用户评价了！";
        		sendPushBroadcast(context, Constant.PUSH_ACTION_ORDER_MODIFIED, content, extra, pushType, businessID);
        		break;
        	case Constant.PUSH_TYPE_ORDER_CANCEL:
        		content = Utils.isNotEmpty(description) ? description : "您有一个订单被取消了！";
        		sendPushBroadcast(context, Constant.PUSH_ACTION_ORDER_MODIFIED, content, extra, pushType, businessID);
        		break;
        	case Constant.PUSH_TYPE_ORDER_EXCEPTION:
        		content = Utils.isNotEmpty(description) ? description : "您有一个订单出现了异常！";
        		sendPushBroadcast(context, Constant.PUSH_ACTION_ORDER_MODIFIED, content, extra, pushType, businessID);
        		break;
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void parsePushNotificationClicked(Context context, String customContent) {
        JSONObject customJson = null;
        try {
            customJson = new JSONObject(customContent);
            int pushType = 0;
            if (!customJson.isNull("push_type")) {
                pushType = Integer.parseInt(customJson.getString("push_type"));
            }
          
            String businessID = "";
            if (!customJson.isNull("business_id")) {
            	businessID = customJson.getString("business_id");
            }
            
            String extra = "";
            if (!customJson.isNull("extra")) {
            	extra = customJson.getString("extra");
            }
            
            switch(pushType){
        	case Constant.PUSH_TYPE_NORMAL:
        		startPushActivity(context,businessID,extra,MainActivity.class.getName(),"",pushType);
        		break;
        	case Constant.PUSH_TYPE_WEB_ACTIVITY:
        		startPushActivity(context,businessID,extra,WebViewActivity.class.getName(),MainActivity.class.getName(),pushType);
        		break;
        	case Constant.PUSH_TYPE_MESSAGE_NEW:
        		startPushActivity(context,businessID,extra,MessageDetailActivity.class.getName(),MainActivity.class.getName(),pushType);
        		break;
        	case Constant.PUSH_TYPE_ORDER_PAY:
        		startPushActivity(context,businessID,extra,OrderPaySuccessActivity.class.getName(),MainActivity.class.getName(),pushType);
        		break;
        	default:
        		startPushActivity(context,businessID,extra,MainActivity.class.getName(),"",pushType);
        		break;
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void parsePushMessage(Context context, String message) {
        JSONObject messageJson= null;
    	JSONObject customJson = null;
    	
        try {
        	messageJson = new JSONObject(message);
            if (!messageJson.isNull("custom_content")) {
            	customJson = messageJson.getJSONObject("custom_content");
            }
            else {
				return;
			}

            int notify = 1;
            if (!customJson.isNull("notify")) {
            	notify = Integer.parseInt(customJson.getString("notify"));
            }
            
            int pushType = 0;
            if (!customJson.isNull("push_type")) {
                pushType = Integer.parseInt(customJson.getString("push_type"));
            }
            
            String toUsers = "";
            if (!customJson.isNull("to_users")) {
            	toUsers = customJson.getString("to_users");
            }
            
            String businessID = "";
            if (!customJson.isNull("business_id")) {
            	businessID = customJson.getString("business_id");
            }
            
            String extra = "";
            if (!customJson.isNull("extra")) {
            	extra = customJson.getString("extra");
            }
            
            String description = "";
            if (!customJson.isNull("description")) {
            	description = customJson.getString("description").trim();
            }
            
            if(sendToMine(context, toUsers)){
            	handlePushMessage(context,notify,pushType,businessID,extra,description);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void sendPushBroadcast(Context context, String action, String message, String extra, int pushType, String businessID){
        final Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("business_id", businessID); 
        intent.putExtra("push_type", pushType);
        intent.putExtra("extra", extra);
        intent.putExtra("message", message);
        context.sendBroadcast(intent);
    }
    
    private static void startPushActivity(Context context, String businessID, String extra, String goClassName, String returnClassName, int pushType){
        final Intent intent = SplashActivity.getSecurityIntent(context,SplashActivity.class);
        intent.putExtra(Constant.EXTRA_KEY_ENTER_IN_APP_METHOD,Constant.EXTRA_VALUE_ENTER_IN_APP_FROM_PUSH);
        intent.putExtra(Constant.EXTRA_KEY_CLASS_NAME, goClassName);
        intent.putExtra("extra", extra);
        
        switch (pushType) {
		case Constant.PUSH_TYPE_NORMAL:
			intent.putExtra(Constant.LOCATE_MAIN_ACTIVITY_INDEX, 0);
	        intent.putExtra("business_id", businessID);
			break;
		case Constant.PUSH_TYPE_WEB_ACTIVITY:
			intent.putExtra(Constant.EXTRA_RETURN_KEY_CLASS_NAME, returnClassName);
        	intent.putExtra("url", extra);
        	intent.putExtra("title", "");
			break;
		case Constant.PUSH_TYPE_MESSAGE_NEW:
			intent.putExtra(Constant.LOCATE_MAIN_ACTIVITY_INDEX, 2);
	        intent.putExtra(Constant.EXTRA_RETURN_KEY_CLASS_NAME, returnClassName);
	        intent.putExtra("message_id", businessID);
			break;
		case Constant.PUSH_TYPE_ORDER_PAY:
			intent.putExtra(Constant.LOCATE_MAIN_ACTIVITY_INDEX, 0);
	        intent.putExtra(Constant.EXTRA_RETURN_KEY_CLASS_NAME, returnClassName);
	        intent.putExtra("order_id", businessID);
	        intent.putExtra("money", extra);
			break;
		default:
			intent.putExtra(Constant.LOCATE_MAIN_ACTIVITY_INDEX, 0);
	        intent.putExtra("business_id", businessID);
			break;
		}
        
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.getApplicationContext().startActivity(intent);
    }
    
    private static void handlePushMessage(Context context,int notify,int pushType,String businessID,String extra,String description){
    	String title = context.getString(R.string.app_name);
    	String content = "";
    	
    	switch(pushType){
    	case Constant.PUSH_TYPE_NORMAL:
    		content = Utils.isNotEmpty(description) ? description : "您有一条普通消息！";
    		break;
    	case Constant.PUSH_TYPE_WEB_ACTIVITY:
    		content = Utils.isNotEmpty(description) ? description : "您有一条需要webview打开的新闻！";
    		break;	
    	case Constant.PUSH_TYPE_MESSAGE_NEW:
    		content = Utils.isNotEmpty(description) ? description : "您有一条新通知！";
    		sendPushBroadcast(context, Constant.PUSH_ACTION_MESSAGE_MODIFIED, content, extra, pushType, businessID);
    		break;
    	case Constant.PUSH_TYPE_ORDER_NEW:
    		content = Utils.isNotEmpty(description) ? description : "您有一个新订单！";
    		sendPushBroadcast(context, Constant.PUSH_ACTION_ORDER_MODIFIED, content, extra, pushType, businessID);
    		break;	
    	case Constant.PUSH_TYPE_ORDER_PAY:
    		content = Utils.isNotEmpty(description) ? description : "您有一个订单支付成功了！";
    		sendPushBroadcast(context, Constant.PUSH_ACTION_ORDER_MODIFIED, content, extra, pushType, businessID);
    		break;
    	case Constant.PUSH_TYPE_ORDER_SCORE:
    		content = Utils.isNotEmpty(description) ? description : "您有一个订单用户评价了！";
    		sendPushBroadcast(context, Constant.PUSH_ACTION_ORDER_MODIFIED, content, extra, pushType, businessID);
    		break;
    	case Constant.PUSH_TYPE_ORDER_CANCEL:
    		content = Utils.isNotEmpty(description) ? description : "您有一个订单被取消了！";
    		sendPushBroadcast(context, Constant.PUSH_ACTION_ORDER_MODIFIED, content, extra, pushType, businessID);
    		break;
    	case Constant.PUSH_TYPE_ORDER_EXCEPTION:
    		content = Utils.isNotEmpty(description) ? description : "您有一个订单出现了异常！";
    		sendPushBroadcast(context, Constant.PUSH_ACTION_ORDER_MODIFIED, content, extra, pushType, businessID);
    		break;
        }
        
        Map<String, String> extras = new HashMap<String, String>();
        String goClassName = MainActivity.class.getName();
        String returnClassName = "";
        
        //create notification
        if(notify == 1){
            if(pushType == Constant.PUSH_TYPE_NORMAL){
            	extras.put(Constant.LOCATE_MAIN_ACTIVITY_INDEX, "0");
            	extras.put("business_id", businessID);
            	goClassName = MainActivity.class.getName();
            }
            
            if(pushType == Constant.PUSH_TYPE_WEB_ACTIVITY){
            	extras.put("url", extra);
            	extras.put("title", title);
            	
            	goClassName = WebViewActivity.class.getName();
            	returnClassName = MainActivity.class.getName();
            }
            
            if(pushType == Constant.PUSH_TYPE_MESSAGE_NEW){
            	extras.put(Constant.LOCATE_MAIN_ACTIVITY_INDEX, "2");
            	extras.put("message_id", businessID);
            	
            	goClassName = MessageDetailActivity.class.getName();
            	returnClassName = MainActivity.class.getName();
            }
            
            if(pushType == Constant.PUSH_TYPE_ORDER_PAY){
            	extras.put(Constant.LOCATE_MAIN_ACTIVITY_INDEX, "0");
            	extras.put("order_id", businessID);
            	extras.put("money", extra);
            	
            	goClassName = OrderPaySuccessActivity.class.getName();
            	returnClassName = MainActivity.class.getName();
            }
            
            LogicFactory.getNotificationLogic(context).showPushNotifybar(title, content, goClassName, returnClassName, extras);
        }
    }
    
    private static boolean sendToMine(Context context, String users){
    	boolean result = true;
    	
    	if(Utils.isNotEmpty(users)){
    		if(users.toLowerCase().equals("all") == false){
    			result = false;
    			
        		String userCode = ConfigManager.getInstance(context).getUserCode();
        		String[] userArray = users.split(",");
        		
        		for(int i = 0; i < userArray.length; i++){
        			if(userArray[i].equals(userCode)){
        				result = true;
        			}
        		}	
    		}
    	}
    	
    	return result;
    }
    
}

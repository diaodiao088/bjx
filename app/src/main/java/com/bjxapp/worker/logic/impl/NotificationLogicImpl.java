package com.bjxapp.worker.logic.impl;

import java.util.Map;
import java.util.Set;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.bjxapp.worker.MainActivity;
import com.bjxapp.worker.SplashActivity;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.logic.INotificationLogic;
import com.bjxapp.worker.ui.view.activity.WebViewActivity;
import com.bjx.master.R;;

public class NotificationLogicImpl implements INotificationLogic {
    private static INotificationLogic instance;
    private Context mContext;

    private NotificationLogicImpl(Context context) {
        mContext = context.getApplicationContext();
    }

    public static INotificationLogic getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationLogicImpl(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void showUpdateNotification() 
    {
        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //定义Notification的各种属性   
        Notification notification =new Notification(R.drawable.icon, "百家修师傅端升级了！", System.currentTimeMillis()); 
        notification.flags = Notification.FLAG_AUTO_CANCEL; 
        notification.defaults = Notification.DEFAULT_ALL; 
          
        //设置通知的事件消息   
        CharSequence contentTitle ="百家修";
        CharSequence contentText ="百家修师傅端升级了，新版本将带给您更好的接单体验！";
        Intent notificationIntent =new Intent(mContext, SplashActivity.class);  
        PendingIntent contentItent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);   
        // notification.setLatestEventInfo(mContext, contentTitle, contentText, contentItent);
          
        //把Notification传递给NotificationManager   
        notificationManager.notify(Constant.NOTIFY_ID_UPDATE_APP, notification);   
    }

    @Override
    public void showCommonNotifybar() 
    {
        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //定义Notification的各种属性 
        Notification notification =new Notification(R.drawable.icon, "百度出大事了...", System.currentTimeMillis()); 
        notification.flags = Notification.FLAG_AUTO_CANCEL; 
        notification.defaults = Notification.DEFAULT_ALL; 
          
        //设置通知的事件消息   
        CharSequence contentTitle ="百度新闻";
        CharSequence contentText ="某度科技公司，程序员捅死产品经理...";
        
        final Intent notifyIntent = SplashActivity.getSecurityIntent(mContext,SplashActivity.class);
        notifyIntent.putExtra(Constant.EXTRA_KEY_ENTER_IN_APP_METHOD,Constant.EXTRA_VALUE_ENTER_IN_APP_FROM_NOTIFY);
        notifyIntent.putExtra(Constant.EXTRA_KEY_CLASS_NAME, WebViewActivity.class.getName());
        //采用return classname来防止单个intent返回时直接退出主界面
        notifyIntent.putExtra(Constant.EXTRA_RETURN_KEY_CLASS_NAME, MainActivity.class.getName());
        notifyIntent.putExtra("url", "https://www.baidu.com");
        notifyIntent.putExtra("title", "百度");  
           
        //自动单个intent
        PendingIntent appIntent = PendingIntent.getActivity(mContext,Constant.NOTIFY_ID_COMMON, notifyIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        
       //  notification.setLatestEventInfo(mContext, contentTitle, contentText, appIntent);
        
        //把Notification传递给NotificationManager   
        notificationManager.notify(Constant.NOTIFY_ID_COMMON, notification);   
    }
    
    public void showPushNotifybar(String title,String content,String goClassName,String returnClassName,Map<String, String> extras) 
    {
        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //定义Notification的各种属性 
        Notification notification =new Notification(R.drawable.icon, content, System.currentTimeMillis()); 
        notification.flags = Notification.FLAG_AUTO_CANCEL; 
        notification.defaults = Notification.DEFAULT_ALL; 
        
        final Intent notifyIntent = SplashActivity.getSecurityIntent(mContext,SplashActivity.class);
        notifyIntent.putExtra(Constant.EXTRA_KEY_ENTER_IN_APP_METHOD,Constant.EXTRA_VALUE_ENTER_IN_APP_FROM_PUSH);
        notifyIntent.putExtra(Constant.EXTRA_KEY_CLASS_NAME, goClassName);
        
        //采用return classname来防止单个intent返回时直接退出主界面
        notifyIntent.putExtra(Constant.EXTRA_RETURN_KEY_CLASS_NAME, returnClassName);
        
        Set<String> keys = extras.keySet();
        for (String key : keys) {
            String value = extras.get(key);
            if(value != null) {
            	notifyIntent.putExtra(key, value);
            }
        }
        
        //compute request code
        String codeTime = String.valueOf(System.currentTimeMillis());
        codeTime = codeTime.substring(codeTime.length()-8);
        int requestCode = Integer.parseInt(codeTime);
        
        //自动单个intent
        PendingIntent appIntent = PendingIntent.getActivity(mContext,requestCode,notifyIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        
       // notification.setLatestEventInfo(mContext, title, content, appIntent);
        
        //把Notification传递给NotificationManager   
        notificationManager.notify(requestCode, notification);   
    }
    
    @Override
    public void showUserDefineNotification() {
        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(),R.layout.layout_notify);
        remoteView.setImageViewResource(R.id.notify_icon, R.drawable.icon);
        remoteView.setTextViewText(R.id.notify_title, mContext.getString(R.string.notify_push_title));
        remoteView.setTextViewText(R.id.notify_content, mContext.getString(R.string.notify_push_content));
        remoteView.setViewVisibility(R.id.notify_icon, View.VISIBLE);
        final Intent notifyIntent = SplashActivity.getSecurityIntent(mContext,SplashActivity.class);
        notifyIntent.putExtra(Constant.EXTRA_KEY_ENTER_IN_APP_METHOD,Constant.EXTRA_VALUE_ENTER_IN_APP_FROM_NOTIFY);
        notifyIntent.putExtra(Constant.EXTRA_KEY_CLASS_NAME, WebViewActivity.class.getName());
        //采用return classname来防止单个intent返回时直接退出主界面
        notifyIntent.putExtra(Constant.EXTRA_RETURN_KEY_CLASS_NAME, MainActivity.class.getName());
        notifyIntent.putExtra("url", "https://www.baidu.com");
        notifyIntent.putExtra("title", "百度");
        
        //自动单个intent
        PendingIntent appIntent = PendingIntent.getActivity(mContext,Constant.NOTIFY_ID_SELF_DEFINE, notifyIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        Notification n = new Notification();

        n.icon = R.drawable.icon;
        n.tickerText = mContext.getString(R.string.notify_push_title);
        n.flags = Notification.FLAG_AUTO_CANCEL;
        n.contentIntent = appIntent;
        n.contentView = remoteView;
        n.defaults = Notification.DEFAULT_ALL;
        NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(Constant.NOTIFY_ID_SELF_DEFINE, n);
    }
    
}

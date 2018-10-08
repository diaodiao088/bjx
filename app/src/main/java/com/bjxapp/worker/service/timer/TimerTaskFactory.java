package com.bjxapp.worker.service.timer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;

import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.utils.TimeUtils;

public class TimerTaskFactory {

    /**
     * 定时轮询任务，1天
     */   
    public static BackgroundTask getOneDayPollingTask(final Context context) {
    	long startTime = System.currentTimeMillis();
    	long gapUnit = TimeUtils.ONE_DAY_MILLIS;
    	String name = "ONE_DAY_POLLING";
    	BackgroundTask oneDayPollingTask = new BackgroundTask(startTime, gapUnit, name) {
            @Override
            void run() 
            {
            	//检查是否apk有更新
            	LogicFactory.getUpdateLogic(context).checkNeedUpdate();
            	if(LogicFactory.getUpdateLogic(context).isNeedUpdate(context,true))
            	{
            		LogicFactory.getNotificationLogic(context).showUpdateNotification();
            	}
            }
        };
        
        return oneDayPollingTask;
    }
    
    /**
     * 定时轮询任务，1小时
     */   
    public static BackgroundTask getOneHourPollingTask(final Context context) {
    	long startTime = System.currentTimeMillis();
    	long gapUnit = TimeUtils.ONE_MINUTE_MILLIS * 60;
    	String name = "ONE_HOUR_POLLING";
    	BackgroundTask minutesPollingTask = new BackgroundTask(startTime, gapUnit, name) {
            @Override
            void run() 
            {
            	//get desktop red dot
            	LogicFactory.getDesktopLogic(context).getRedDots();
            	
            	//upload push channelid
            	if(!ConfigManager.getInstance(context).getUserChannelUploaded()){
            		int result = LogicFactory.getAccountLogic(context).updateChannelID();
            		if(result == APIConstants.RESULT_CODE_SUCCESS){
            			ConfigManager.getInstance(context).setUserChannelUploaded(true);
            		}
            	}
            }
        };
        
        return minutesPollingTask;
    }
    
    /**
     * 定时轮询任务，4小时
     */   
    public static BackgroundTask getFourHoursPollingTask(final Context context) {
    	long startTime = System.currentTimeMillis();
    	long gapUnit = TimeUtils.ONE_HOUR_MILLIS * 4;
    	String name = "FOUR_HOURS_POLLING";
    	BackgroundTask somePollingTask = new BackgroundTask(startTime, gapUnit, name) {
            @Override
            void run() 
            {
            	//Show CommonNotify Bar Example...
            	//LogicFactory.getNotificationLogic(context).showCommonNotifybar();
            }
        };
        
        return somePollingTask;
    }
    
    /**
     * 具体时间点做某事
     */
    public static BackgroundTask getSpecialTimeTask(final Context context) {
    	long startTime = getMillisOfDate();
    	//-1 代表单次
    	long gapUnit = -1;
    	String name = "SPECIAL_TIME_POLLING";
    	BackgroundTask specialTimeTask = new BackgroundTask(startTime, gapUnit, name) {
            @Override
            void run() 
            {

            }
        };
        
        return specialTimeTask;
    }
    
    /*
     * 定时执行的计算
     */
	private static long getMillisOfDate()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date =new Date();
		String strDate = format.format(date);
		try {
			Date d = format.parse(strDate);
			Calendar c = Calendar.getInstance();  
            c.setTime(d);  
            c.add(Calendar.DATE, 2);
            c.add(Calendar.HOUR, 12);
            c.add(Calendar.MINUTE, 0);
            c.add(Calendar.SECOND, 0);
			return c.getTimeInMillis();
			
		} catch (ParseException e) 
		{
			return 0;
		}
	}
    	
    /**
     * 注意：实现runable的方法有耗时操作，也别另开线程
     */
    public abstract static class BackgroundTask {
        public long startTime;
        public long gapUnit;
        public String name;

        public BackgroundTask(long startTime, long gapUnit, String name) {
            this.startTime = startTime;
            this.gapUnit = gapUnit;
            this.name = name;
        }

        abstract void run();
    }

}

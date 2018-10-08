package com.bjxapp.worker.service.timer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

import com.bjxapp.worker.utils.WakeLockHelper;

/**
 * 后台任务管理类
 * 使用AlarmManager做定时轮询
 */
public class TimerTaskManager {

    private static TimerTaskManager sInstance;
    private Context mContext;
    private AlarmManager mAlarmManager;

    private TimerTaskManager(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    public static TimerTaskManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TimerTaskManager(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * 注册轮询任务
     *
     * @param task
     */
    public void registerTask(final TimerTaskFactory.BackgroundTask task) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                runTask(task);
            }
        };
        //TODO:reveiver需要取消注册吗
        mContext.registerReceiver(receiver, new IntentFilter(getIntentName(task.name)));

        PendingIntent pendingIntent = buildPendingIntent(mContext, task.name);
        mAlarmManager.cancel(pendingIntent);
        //由于对其机制的原因，第一次启动任务的时间会在设定时间的左右浮动
        if(task.gapUnit >= 0)
        {
        	//轮询
        	mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, task.startTime, task.gapUnit, pendingIntent);
        }
        else
        {
        	//单次
        	mAlarmManager.set(AlarmManager.RTC_WAKEUP, task.startTime, pendingIntent);
        }
    }

    private String getIntentName(String name) {
        return "com.zdworks.android.zdclock.ACTION_ALARM_MANAGER_" + name;
    }

    /**
     * 定时执行任务
     */
    private void runTask(final TimerTaskFactory.BackgroundTask task) {
        final PowerManager.WakeLock wakeLock = WakeLockHelper.getWakeLock(mContext);
        wakeLock.acquire();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    task.run();
                } finally {
                    wakeLock.release();//确保一定能够释放唤醒锁
                }
            }
        }).start();
    }

    private PendingIntent buildPendingIntent(Context context, String name) {
        Intent intent = new Intent(getIntentName(name));
        return PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


}

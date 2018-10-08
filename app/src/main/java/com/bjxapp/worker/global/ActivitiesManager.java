package com.bjxapp.worker.global;

import java.util.Stack;
import android.app.Activity;

public class ActivitiesManager {

	private static ActivitiesManager instance;
	private Stack<Activity> activityStack;
	private ActivitiesManager() {
	}
	
	//单例模式
	public static ActivitiesManager getInstance() {
	    if (instance == null) {
	        instance = new ActivitiesManager();
	    }
	    return instance;
	}
	
	//把一个activity压入栈中
	public void pushOneActivity(Activity actvity) {
	    if (activityStack == null) {
	        activityStack = new Stack<Activity>();
	    }
	    activityStack.add(actvity);
	}
	
	//获取栈顶的activity，先进后出原则
	public Activity getLastActivity() {
	    return activityStack.lastElement();
	}
	
	//移除一个activity
	public void popOneActivity(Activity activity) {
	    if (activityStack != null && activityStack.size() > 0) {
	        if (activity != null) {
	            activity.finish();
	            if(activityStack.contains(activity)){
	            	activityStack.remove(activity);
	            }
	            activity = null;
	        }
	    }
	}
	
	//退出所有activities
	public void finishAllActivities() {
	    if (activityStack != null) {
	        while (activityStack.size() > 0) {
	            Activity activity = getLastActivity();
	            if (activity == null) break;
	            popOneActivity(activity);
	        }
	    }
	}
}

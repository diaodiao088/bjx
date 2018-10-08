package com.bjxapp.worker.logic;

import java.util.Map;

public interface INotificationLogic {

    /**
     * 检查更新的notification
     */
    void showUpdateNotification();
    
    /**
     * 展示common的通知
     */
    void showCommonNotifybar();
    
    /**
     * 展示自定义view的通知
     */
    void showUserDefineNotification();
    
    /**
     * 展示push的通知
     */
    void showPushNotifybar(String title, String content, String goClassName, String returnClassName, Map<String, String> extras);
}

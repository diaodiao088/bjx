package com.bjxapp.worker.logic;

import android.content.Context;

/**
 * APP升级逻辑
 *
 * @author jason
 */
public interface IUpdateLogic {

    public Boolean isNeedUpdate(Context context, Boolean isWifi);

    /**
     * 更新APK
     */
    public void showUpdateDialog(Context context);

}

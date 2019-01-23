package com.bjxapp.worker.logic.impl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.logic.IUpdateLogic;
import com.bjxapp.worker.update.UpdateManager;
import com.bjxapp.worker.utils.Utils;

/**
 * 更新APK逻辑实现
 *
 * @author jason
 */
public class UpdateLogicImpl implements IUpdateLogic {
    private static UpdateLogicImpl sInstance;
    private Context mContext;

    private UpdateLogicImpl(Context context) {
        mContext = context.getApplicationContext();
    }

    public static IUpdateLogic getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new UpdateLogicImpl(context);
        }

        return sInstance;
    }

    @Override
    public Boolean isNeedUpdate(Context context, Boolean isWifi) {
        if (isWifi) {
            if (Utils.isWifiConnected(context)) {
                return ConfigManager.getInstance(mContext).getNeedUpdate();
            } else {
                return false;
            }
        } else {
            if (Utils.isNetworkAvailable(context)) {
                return ConfigManager.getInstance(mContext).getNeedUpdate();
            } else {
                return false;
            }
        }
    }

    @Override
    public void showUpdateDialog(final Context context) {
        String version = ConfigManager.getInstance(mContext).getUpdateVersion();
        String description = ConfigManager.getInstance(mContext).getUpdateDescription();
        String versionName = ConfigManager.getInstance(mContext).getUpdateVersionName();
        boolean isForce = ConfigManager.getInstance(mContext).isForceShowUpdateDialog();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("新版本" + versionName + "更新提醒");
        builder.setMessage(description);
        builder.setCancelable(false);
        builder.setPositiveButton("升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    downloadFile(context);
                } else {
                    Toast.makeText(mContext, "sdcard not exists!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (!isForce){
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }
        builder.create().show();
    }

    private void downloadFile(Context context) {
        String url = ConfigManager.getInstance(mContext).getUpdateURL();
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("更新提醒");
        progressDialog.setMessage("正在下载更新包,请稍候...");
        progressDialog.setProgress(0);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Handler handler = new Handler();
        UpdateManager updateManager = new UpdateManager(mContext);
        updateManager.downLoadFile(url, progressDialog, handler);

        ConfigManager.getInstance(mContext).setNeedUpdate(false);
    }

    /*
     * 获取本地版本号
     */
    public String getLocalVersion() {
        String localVersion = "";
        try {
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (Exception e) {
            localVersion = "";
        }
        return localVersion;
    }
}

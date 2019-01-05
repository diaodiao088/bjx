package com.bjxapp.worker.push;

import android.content.Context;
import android.util.Log;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;

/**
 * Created by zhangdan on 2018/10/31.
 * comments:
 */

public class PushIntentService extends GTIntentService {

    public PushIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {

        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();

        if (payload != null) {
            String content = new String(payload);
            PushParser.onMessageArrived(content);
        }

    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {

    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.d(TAG, "cmdMsg : " + cmdMessage.toString());
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage msg) {
        Log.d(TAG, "notification : " + msg);
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage msg) {
    }
}

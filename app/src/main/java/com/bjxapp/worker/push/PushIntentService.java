package com.bjxapp.worker.push;

import android.content.Context;
import android.util.Log;

import com.baidu.android.pushservice.message.PublicMsg;
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

        Log.d(TAG , "msg : " + msg.getMessageId() + ": " + msg.getPayloadId());

        Log.d(TAG , "TOUCHUAN : " + new String(msg.getPayload()));

    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.d(TAG , "cmdMsg : " + cmdMessage.toString());
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage msg) {
        Log.d(TAG , "notification : " + msg);
}

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage msg) {
    }
}

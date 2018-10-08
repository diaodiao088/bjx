package com.bjxapp.worker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.service.XAppService;

/**
 * 接收网络状态变化的广播
 *
 * @author jason
 */
public class NetworkStateChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Intent service = new Intent(context, XAppService.class);
        service.putExtra(Constant.EXTRA_KEY_RECEIVER_ID, Constant.RECEIVER_ID_VALUE_NETWORK_STATE_CHANGED);
        context.startService(service);
    }

}

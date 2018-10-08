package com.bjxapp.worker.receiver;

import com.bjxapp.worker.global.Constant;

import android.content.Context;
import android.content.Intent;

public class ReceiverHandler {

	/**
	 * 所有receiver都通过XAppService去具体实现
	 * @param intent
	 * @param context
	 */
	public static void handleLongTimeConsumingReceiver(Intent intent, Context context) {
		if (intent == null) {
			return;
		}

		int receiverId = intent.getIntExtra(Constant.EXTRA_KEY_RECEIVER_ID, Constant.RECEIVER_ID_VALUE_ERROR);
		if (receiverId == Constant.RECEIVER_ID_VALUE_ERROR)
			return;
		
		switch (receiverId) {
		case Constant.RECEIVER_ID_VALUE_NETWORK_STATE_CHANGED:
			handleNetworkStateChangedMessage(context,intent);
			break;
		}
	}

	//handler network state changed
    private static void handleNetworkStateChangedMessage(Context context, Intent intent) 
    {
    	//Toast.makeText(context, "handleNetworkStateChangedMessage:OK",2);
	}
    
}

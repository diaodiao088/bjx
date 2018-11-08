package com.bjxapp.worker.push;

import android.content.Intent;
import android.text.TextUtils;

import com.bjxapp.worker.App;
import com.bjxapp.worker.global.Constant;

import org.json.JSONObject;

/**
 * Created by zhangdan on 2018/11/8.
 * <p>
 * comments:
 */

public class PushParser {

    public static void onMessageArrived(String content) {

        if (TextUtils.isEmpty(content)) {
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(content);

            int type = jsonObject.getInt("type");
            boolean isVoice = jsonObject.getBoolean("isVoice");
            String contentStr = jsonObject.getString("content");

            switch (type) {

                case 11:
                    sendSuccBroadcast(type);
                    break;
                case 10:
                    sendSuccBroadcast(type);
                    break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void sendSuccBroadcast(int type) {
        Intent intent = new Intent();
        intent.setAction(Constant.PUSH_ACTION_ORDER_MODIFIED);
        intent.putExtra("push_type", Constant.PUSH_TYPE_ORDER_PAY);
        intent.putExtra("pay_type", type);
        App.getInstance().sendBroadcast(intent);
    }


}

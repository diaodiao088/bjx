package com.bjxapp.worker.push;

import android.content.Intent;
import android.media.MediaPlayer;
import android.text.TextUtils;

import com.bjxapp.worker.App;
import com.bjxapp.worker.R;
import com.bjxapp.worker.db.BjxInfo;
import com.bjxapp.worker.db.DBManager;
import com.bjxapp.worker.global.Constant;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by zhangdan on 2018/11/8.
 * <p>
 * comments:
 */

public class PushParser {

    private static DBManager mDbManager = new DBManager(App.getInstance());

    public static void onMessageArrived(String content) {

        if (TextUtils.isEmpty(content)) {
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(content);

            int type = jsonObject.getInt("type");
            boolean isVoice = jsonObject.getBoolean("isVoice");
            String contentStr = jsonObject.getString("content");
            String title = jsonObject.getString("title");
            String remark = jsonObject.getString("remark");
            String createTime = jsonObject.getString("createTime");

            switch (type) {

                // 付款成功
                case 11:
                    sendSuccBroadcast(type);
                    getMoneySuccess(11, contentStr, title, remark, createTime);
                    break;
                // 预付成功
                case 10:
                    sendSuccBroadcast(type);
                    getMoneySuccess(10, contentStr, title, remark, createTime);
                    break;
                // 新订单到来
                case 0:
                    newBillCome(0, contentStr, title, remark, createTime);
                    break;
                // 提现成功
                case 20:
                    withDrawSuccess(20, contentStr, title, remark, createTime);
                    break;
                // 提现失败
                case 21:
                    withDrawFailed(21, contentStr, title, remark, createTime);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getMoneySuccess(int type, String contentStr, String title, String remark, String createTime) {

        MediaPlayer mediaPlayer = MediaPlayer.create(App.getInstance(), R.raw.pay_succ);

        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.start();

        BjxInfo info = new BjxInfo(type, contentStr, title, remark, Long.parseLong(createTime));

        mDbManager.add(info);

    }

    private static void newBillCome(int type, String contentStr, String title, String remark, String createTime) {

        MediaPlayer mediaPlayer = MediaPlayer.create(App.getInstance(), R.raw.new_bill);

        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.start();

        BjxInfo info = new BjxInfo(type, contentStr, title, remark, Long.parseLong(createTime));

        mDbManager.add(info);
    }


    private static void getMoneySuccess() {


    }

    private static void withDrawSuccess(int type, String contentStr, String title, String remark, String createTime) {

        BjxInfo info = new BjxInfo(type, contentStr, title, remark, Long.parseLong(createTime));

        mDbManager.add(info);
    }

    private static void withDrawFailed(int type, String contentStr, String title, String remark, String createTime) {

        BjxInfo info = new BjxInfo(type, contentStr, title, remark, Long.parseLong(createTime));

        mDbManager.add(info);
    }


    private static void newBillCome() {


    }

    private static void sendSuccBroadcast(int type) {
        Intent intent = new Intent();
        intent.setAction(Constant.PUSH_ACTION_ORDER_MODIFIED);
        intent.putExtra("push_type", Constant.PUSH_TYPE_ORDER_PAY);
        intent.putExtra("pay_type", type);
        App.getInstance().sendBroadcast(intent);
    }


}

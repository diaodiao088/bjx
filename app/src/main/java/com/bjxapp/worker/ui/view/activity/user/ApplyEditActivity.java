package com.bjxapp.worker.ui.view.activity.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bjxapp.worker.R;

/**
 * Created by zhangdan on 2018/10/23.
 * <p>
 * comments:
 */

public class ApplyEditActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
    }

    public static void goToActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ApplyEditActivity.class);
        context.startActivity(intent);
    }


}

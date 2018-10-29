package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bjxapp.worker.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhangdan on 2018/10/29.
 * comments:
 */

public class JoinUsActivity extends Activity {

    @OnClick(R.id.join_us_back)
    void onBack() {
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_us);
        ButterKnife.bind(this);
    }

    public static void goToActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, JoinUsActivity.class);
        context.startActivity(intent);
    }


}

package com.bjxapp.worker;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.bjxapp.worker.global.ActivitiesManager;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.service.ServiceManager;
import com.bjxapp.worker.ui.view.activity.user.ApplyActivity;
import com.bjxapp.worker.ui.view.activity.user.LoginActivity;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.R;

public class SplashActivity extends BaseActivity {
    protected static final String TAG = "启动封面";

    private static final int LOGIN_REQUEST_CODE = 1;
    private String mStrClassName = null;
    private final static String MAIN_ACTION = "android.intent.action.MAIN";
    private int mEnterAppMethod = -1;
    private boolean mLogined = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        handleExtraParam();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //启动service
        ServiceManager.startServices(this.getApplicationContext());
        Intent intent = this.getIntent();
        mStrClassName = intent.getStringExtra(Constant.EXTRA_KEY_CLASS_NAME);

        getLogin();
    }

    private void getLogin() {
        String name = ConfigManager.getInstance(SplashActivity.this).getUserCode();
        String session = ConfigManager.getInstance(SplashActivity.this).getUserSession();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(session)) {
            mLogined = false;
        } else {
            mLogined = true;
        }
        mHandler.sendEmptyMessageDelayed(0, 1500);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Intent intent = new Intent();
            if (mLogined) {
                gotoTargetActivity();
                /*Intent intent1 = new Intent();
                intent1.setClass(SplashActivity.this , ApplyActivity.class);
                startActivity(intent1);*/
            } else {
                intent.setClass(SplashActivity.this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case LOGIN_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    gotoTargetActivity();
                   /* Intent intent1 = new Intent();
                    intent1.setClass(SplashActivity.this , ApplyActivity.class);
                    startActivity(intent1);*/
                } else {
                    ActivitiesManager.getInstance().finishAllActivities();
                    Utils.finishWithoutAnim(this);

                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, intent);
                Utils.finishWithoutAnim(this);
                break;
        }
    }

    /**
     * 启动主界面，再利用传入的类名做跳转
     * 所有通知栏进入某个页面都需要通过此处做跳转，不可以直接进入该页面
     *
     * @param context
     * @param clazz
     */
    public static Intent getSecurityIntent(Context context, Class<? extends Activity> clazz) {
        Intent intent = new Intent();
        String packageName = context.getPackageName();
        ComponentName componentName = new ComponentName(packageName, getMainComponentName(context));
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String className = clazz.getName();
        if (!className.equals(SplashActivity.class.getName())) {
            intent.putExtra(Constant.EXTRA_KEY_CLASS_NAME, clazz.getName());
        }
        return intent;
    }

    private static String getMainComponentName(Context context) {
        return "com.bjxapp.worker.SplashActivity";
    }

    private void handleExtraParam() {
        mEnterAppMethod = getIntent().getIntExtra(Constant.EXTRA_KEY_ENTER_IN_APP_METHOD, -1);
        if (mEnterAppMethod == -1) {
            String action = getIntent().getAction();
            if (action != null && action.contains(MAIN_ACTION)) {
                mEnterAppMethod = Constant.EXTRA_VALUE_ENTER_IN_APP_FROM_ICON;
            } else if (getIntent().getDataString() != null) {
                mEnterAppMethod = Constant.EXTRA_VALUE_ENTER_IN_APP_FROM_WAP;
            } else {
                mEnterAppMethod = Constant.EXTRA_VALUE_ENTER_IN_APP_FROM_OTHER;
            }
        }
    }

    /**
     * 跳到目标界面
     */
    private void gotoTargetActivity() {
        if (!Utils.isNotEmpty(mStrClassName) || mStrClassName.equals(this.getClass().getName())) {
            mStrClassName = MainActivity.class.getName();
        }
        Intent it = new Intent();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            it.putExtras(extras);
        }
        if (mStrClassName != null && (mStrClassName.equals(MainActivity.class.getName()))) {
            setIntentFlag(it);
            if (it.hasExtra(Constant.LOCATE_MAIN_ACTIVITY_INDEX) == false) {
                it.putExtra(Constant.LOCATE_MAIN_ACTIVITY_INDEX, 0);
            }
        } else {
            it.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        String packageName = getApplicationInfo().packageName;
        it.setClassName(packageName == null ? Constant.APP_PACKAGE_NAME : packageName, mStrClassName);
        mStrClassName = null;
        startActivity(it);
        Utils.finishWithoutAnim(this);
    }

    /**
     * 针对启动的方式做不同的处理
     * 从通知栏的就用singleTop, 效果是每次都会进入MainActivity
     * 其他的用newTask 效果是每次进入已经启动的Activity
     *
     * @param intent
     */
    private void setIntentFlag(Intent intent) {
        if (mEnterAppMethod == Constant.EXTRA_VALUE_ENTER_IN_APP_FROM_NOTIFY
                || mEnterAppMethod == Constant.EXTRA_VALUE_ENTER_IN_APP_FROM_WAP
                || mEnterAppMethod == Constant.EXTRA_VALUE_ENTER_IN_APP_FROM_PUSH) {
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
    }

    @Override
    protected void initControl() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected String getPageName() {
        return TAG;
    }

}

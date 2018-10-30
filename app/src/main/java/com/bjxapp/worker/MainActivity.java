package com.bjxapp.worker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ActivitiesManager;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.RedDot;
import com.bjxapp.worker.push.XPushManager;
import com.bjxapp.worker.ui.titlemenu.ActionItem;
import com.bjxapp.worker.ui.titlemenu.TitlePopup;
import com.bjxapp.worker.ui.titlemenu.TitlePopup.OnItemOnClickListener;
import com.bjxapp.worker.ui.view.activity.JoinUsActivity;
import com.bjxapp.worker.ui.view.activity.user.ApplyActivity;
import com.bjxapp.worker.ui.view.base.BaseFragmentActivity;
import com.bjxapp.worker.ui.view.fragment.Fragment_Main_First;
import com.bjxapp.worker.ui.view.fragment.Fragment_Main_Fourth;
import com.bjxapp.worker.ui.view.fragment.Fragment_Main_Second;
import com.bjxapp.worker.ui.view.fragment.Fragment_Main_Third;
import com.bjxapp.worker.utils.TimeUtils;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.zxing.CaptureActivity;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseFragmentActivity implements OnClickListener {

    protected static final String TAG = "主界面";

    private XTextView mFirstReminder, mThirdReminder;
    private XTextView mTitleTextView;
    private XImageView mRightImageView;
    private TitlePopup mTitlePopup;
    private Fragment[] mFragments;
    public Fragment_Main_First mMainFirstFragment;
    private Fragment_Main_Second mMainSecondFragment;
    private Fragment_Main_Third mMainThirdFragment;
    private Fragment_Main_Fourth mMainFourthFragment;
    private XImageView[] mImageButtons;
    private XTextView[] mTextViews;
    private int mIndex;
    private int mCurrentTabIndex;
    private int mKeyBackClickCount = 0;

    private LocationClient mLocationClient;
    private MyLocationListener listener;
    private XWaitingDialog mWaitingDialog;

    @BindView(R.id.title_right_small_tv)
    TextView mTitleRightTv;

    @BindView(R.id.title_text_tv)
    TextView mTitleTv;

    @BindView(R.id.title_image_back)
    XImageView mBackIv;

    @OnClick(R.id.title_right_small_tv)
    void onRightClick(){
        JoinUsActivity.goToActivity(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //初始化推送
        XPushManager.startPush(getApplicationContext());

        findViewById();
        initViews();
        initTabView();
        setOnListener();
        initPopWindow();

        mWaitingDialog = new XWaitingDialog(MainActivity.this);

        if (!Utils.isNetworkAvailable(MainActivity.this)) {
            this.showStatusDialog(99);
            return;
        }

        //获取用户注册状态
        getUserStatus();

        //显示红点
        displayRedDot();

        //测试定位
        initLocation();

        //注册推送ID
        // registerPush();

        //检查最新APK版本
        checkNewVersion();
    }

    @Override
    public void onStart() {
        super.onStart();

        //检查是否有软件更新
        checkUpdateInfo();

        //检查是否有红点显示
        setRedDotState();

        int index = getIntent().getIntExtra(Constant.LOCATE_MAIN_ACTIVITY_INDEX, -1);
        getIntent().removeExtra(Constant.LOCATE_MAIN_ACTIVITY_INDEX);

        int enterType = getIntent().getIntExtra(Constant.EXTRA_KEY_ENTER_IN_APP_METHOD, 0);
        getIntent().removeExtra(Constant.EXTRA_KEY_ENTER_IN_APP_METHOD);

        switch (index) {
            case 0:
                showFragment(R.id.main_tab_first, enterType);
                break;
            case 1:
                showFragment(R.id.main_tab_second, enterType);
                break;
            case 2:
                showFragment(R.id.main_tab_third, enterType);
                break;
            case 3:
                showFragment(R.id.main_tab_fourth, enterType);
                break;
            default:
                break;
        }
    }

    //检查更新
    private void checkUpdateInfo() {
        if (LogicFactory.getUpdateLogic(MainActivity.this).isNeedUpdate(MainActivity.this, true)) {
            LogicFactory.getUpdateLogic(MainActivity.this).showUpdateDialog(MainActivity.this);
        }
    }

    private void initTabView() {
        mMainFirstFragment = new Fragment_Main_First();
        mMainSecondFragment = new Fragment_Main_Second();
        mMainThirdFragment = new Fragment_Main_Third();
        mMainFourthFragment = new Fragment_Main_Fourth();
        mFragments = new Fragment[]{mMainFirstFragment, mMainSecondFragment, mMainThirdFragment, mMainFourthFragment};
        mImageButtons = new XImageView[4];
        mImageButtons[0] = (XImageView) findViewById(R.id.main_tab_first_image);
        mImageButtons[1] = (XImageView) findViewById(R.id.main_tab_second_image);
        mImageButtons[2] = (XImageView) findViewById(R.id.main_tab_third_image);
        mImageButtons[3] = (XImageView) findViewById(R.id.main_tab_fourth_image);

        mImageButtons[0].setSelected(true);
        mTextViews = new XTextView[4];
        mTextViews[0] = (XTextView) findViewById(R.id.main_tab_first_text);
        mTextViews[1] = (XTextView) findViewById(R.id.main_tab_second_text);
        mTextViews[2] = (XTextView) findViewById(R.id.main_tab_third_text);
        mTextViews[3] = (XTextView) findViewById(R.id.main_tab_fourth_text);
        mTextViews[0].setTextColor(0xFF45C01A);

        mFirstReminder = (XTextView) findViewById(R.id.main_tab_first_reminder);
        mThirdReminder = (XTextView) findViewById(R.id.main_tab_third_reminder);

        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mMainFirstFragment)
                .add(R.id.fragment_container, mMainSecondFragment)
                .add(R.id.fragment_container, mMainFourthFragment)
                .add(R.id.fragment_container, mMainThirdFragment)
                .hide(mMainSecondFragment).hide(mMainFourthFragment)
                .hide(mMainThirdFragment).show(mMainFirstFragment).commit();
    }

    public void onTabClicked(View view) {
        showFragment(view.getId(), 0);
    }

    private void showFragment(int tabID, int enterType) {
        mRightImageView.setVisibility(View.GONE);
        switch (tabID) {
            case R.id.main_tab_first:
                //mRightImageView.setVisibility(View.VISIBLE);
                mIndex = 0;
                if (mMainFirstFragment != null) {
                    mMainFirstFragment.refresh(enterType);
                }
                mFirstReminder.setVisibility(View.GONE);
                mTitleTextView.setText(getString(R.string.main_tab_first_text));
                mTitleRightTv.setVisibility(View.VISIBLE);
                mRightImageView.setImageResource(R.drawable.icon_menu_add);
                mRightImageView.setPadding(0, 0, 0, 0);
                break;
            case R.id.main_tab_second:
                mIndex = 1;
                mTitleRightTv.setVisibility(View.GONE);
                mTitleTextView.setText(getString(R.string.main_tab_second_text));
                if (mMainSecondFragment != null) {
                    mMainSecondFragment.refresh(enterType);
                }
                break;
            case R.id.main_tab_third:
                mIndex = 2;
                mTitleTextView.setText(getString(R.string.main_tab_third_text));
                mTitleRightTv.setVisibility(View.GONE);
                if (mMainThirdFragment != null) {
                    mMainThirdFragment.refresh(enterType);
                }
                mThirdReminder.setVisibility(View.GONE);
                break;
            case R.id.main_tab_fourth:
                mIndex = 3;
                mTitleTextView.setText(getString(R.string.main_tab_fourth_text));
                mTitleRightTv.setVisibility(View.GONE);
                if (mMainFourthFragment != null) {
                    mMainFourthFragment.refresh(enterType);
                }
                break;
        }
        if (mCurrentTabIndex != mIndex) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(mFragments[mCurrentTabIndex]);
            if (!mFragments[mIndex].isAdded()) {
                trx.add(R.id.fragment_container, mFragments[mIndex]);
            }
            trx.show(mFragments[mIndex]).commit();
        }
        mImageButtons[mCurrentTabIndex].setSelected(false);

        // 把当前tab设为选中状态
        mImageButtons[mIndex].setSelected(true);
        mTextViews[mCurrentTabIndex].setTextColor(0xFF999999);
        mTextViews[mIndex].setTextColor(0xFF45C01A);
        mCurrentTabIndex = mIndex;
    }

    private void initPopWindow() {
        // 实例化标题栏弹窗
        mTitlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mTitlePopup.setItemOnClickListener(onitemClick);
        // 给标题栏弹窗添加子类
        mTitlePopup.addAction(new ActionItem(this, "客服", R.drawable.icon_menu_call));
        mTitlePopup.addAction(new ActionItem(this, "扫一扫", R.drawable.icon_menu_scan));
    }

    private OnItemOnClickListener onitemClick = new OnItemOnClickListener() {

        @Override
        public void onItemClick(ActionItem item, int position) {
            switch (position) {
                case 0:
                    callService();
                    //test();
                    break;
                case 1:
                    Utils.startActivity(MainActivity.this, CaptureActivity.class);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        switch (mCurrentTabIndex) {
            case 0:
                showFragment(R.id.main_tab_first, 0);
                break;
            case 1:
                showFragment(R.id.main_tab_second, 0);
                break;
            case 2:
                showFragment(R.id.main_tab_third, 0);
                break;
            case 3:
                showFragment(R.id.main_tab_fourth, 0);
                break;
            default:
                break;
        }
    }

    @Override
    protected String getPageName() {
        return TAG;
    }

    private void findViewById() {
        mTitleTextView = (XTextView) findViewById(R.id.title_text_tv);
        mRightImageView = (XImageView) findViewById(R.id.title_image_right);
        mTitleTv.setText("首页");
        mTitleRightTv.setText("加入我们");
    }

    private void initViews() {
        mRightImageView.setVisibility(View.GONE);
        mRightImageView.setImageResource(R.drawable.icon_menu_main);
        mBackIv.setVisibility(View.GONE);
        mTitleRightTv.setVisibility(View.VISIBLE);
    }

    private void setOnListener() {
        mRightImageView.setOnClickListener(this);
    }

    private AsyncTask<Void, Void, RedDot> mDisplayRedDotTask;

    private void displayRedDot() {
        if (!Utils.isNetworkAvailable(MainActivity.this)) {
            Utils.showShortToast(MainActivity.this, getString(R.string.common_no_network_message));
            return;
        }

        mDisplayRedDotTask = new AsyncTask<Void, Void, RedDot>() {
            @Override
            protected RedDot doInBackground(Void... params) {
                return LogicFactory.getDesktopLogic(MainActivity.this).getRedDots();
            }

            @Override
            protected void onPostExecute(RedDot result) {
                if (result == null) {
                    return;
                }

                setRedDotState();
            }
        };
        mDisplayRedDotTask.execute();
    }

    private void setRedDotState() {
        if (ConfigManager.getInstance(MainActivity.this).getDesktopOrdersDot() < ConfigManager.getInstance(MainActivity.this).getDesktopOrdersDotServer()) {
            //此功能保留，因为主页每次进来都会刷新，所以此功能暂不需要
            //mFirstReminder.setVisibility(View.VISIBLE);
        }
        if (ConfigManager.getInstance(MainActivity.this).getDesktopMessagesDot() < ConfigManager.getInstance(MainActivity.this).getDesktopMessagesDotServer()) {
            mThirdReminder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (mKeyBackClickCount++) {
                case 0:
                    Toast.makeText(this, getString(R.string.common_exit_message), Toast.LENGTH_SHORT).show();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mKeyBackClickCount = 0;
                        }
                    }, 3000);
                    break;
                case 1:
                    ActivitiesManager.getInstance().finishAllActivities();
                    break;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_image_right:
                if (mIndex == 0) {
                    mTitlePopup.show(findViewById(R.id.layout_bar));
                } else if (mIndex == 1) {

                }
                break;
            default:
                break;
        }
    }

    /**
     * 百度定位测试
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            if (location != null) {
                if (Utils.isNotEmpty(location.getAddrStr()) && Utils.isNotEmpty(location.getCity())) {


                    Log.d("slog_zd","lat : " + location.getLatitude() + " , longti : " + location.getLongitude());

                    Log.d("slog_zd","addr : " + location.getAddrStr() + " , city : " + location.getCity());


                    Constant.USER_LOCATION_LATITUDE = location.getLatitude();
                    Constant.USER_LOCATION_LONGITUDE = location.getLongitude();
                    Constant.USER_LOCATION_ADDRESS = location.getAddrStr();
                    Constant.USER_LOCATION_CITY = location.getCity();
                }
                mLocationClient.stop();
            }
        }
    }

    private void initLocation() {
        mLocationClient = new LocationClient(MainActivity.this);
        listener = new MyLocationListener();
        mLocationClient.registerLocationListener(listener);

        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        //option.setLocationMode(LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系
        //option.setCoorType("gcj02");
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan((int) TimeUtils.ONE_SECOND_MILLIS * 5);
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        //option.setLocationNotify(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        //option.setIgnoreKillProcess(true);
        //可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        //option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        //option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        //option.setIsNeedLocationPoiList(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    private AsyncTask<Void, Void, Integer> mRegisterPushTask;

    private void registerPush() {
        if (ConfigManager.getInstance(MainActivity.this).getUserChannelUploaded()) {
            //return;
        }

        mRegisterPushTask = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                int result = LogicFactory.getAccountLogic(MainActivity.this).updateChannelID();
                if (isCancelled()) {
                    return 0;
                }
                return result;
            }

            @Override
            protected void onPostExecute(Integer result) {
                if (result == APIConstants.RESULT_CODE_SUCCESS) {
                    ConfigManager.getInstance(MainActivity.this).setUserChannelUploaded(true);
                }
            }
        };

        mRegisterPushTask.execute();
    }

    private AsyncTask<Void, Void, Integer> mCheckNewVersionTask;

    private void checkNewVersion() {
        mCheckNewVersionTask = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                LogicFactory.getUpdateLogic(MainActivity.this).checkNeedUpdate();
                return 1;
            }

            @Override
            protected void onPostExecute(Integer result) {

            }
        };

        mCheckNewVersionTask.execute();
    }

    private AsyncTask<Void, Void, Integer> mGetStatusTask;

    private void getUserStatus() {
        mWaitingDialog.show("正在查询注册信息，请稍候...", false);
        mGetStatusTask = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                int result = LogicFactory.getAccountLogic(MainActivity.this).getRegisterStatus();
                if (isCancelled()) {
                    return -2;
                }
                return result;
            }

            @Override
            protected void onPostExecute(Integer result) {
                mWaitingDialog.dismiss();
                if (result != -2) {
                    ConfigManager.getInstance(MainActivity.this).setUserStatus(result);
                }
                showStatusDialog(result);
            }
        };

        mGetStatusTask.execute();
    }

    private void showStatusDialog(int status) {
        String message = "";

        switch (status) {
            case -2:
                break;
            case -1:
                callWorkerApply();
                return;
            case 0:
                message = "您的资料正在审核中，请保持手机畅通，收到确认短信后，请重新打开【百家修】，咨询电话:" + getString(R.string.service_telephone_display);
                break;
            case 1:
                return;
            case 2:
                message = "您的资料审核没有通过！\n" + "咨询电话:" + getString(R.string.service_telephone_display);
                break;
            case 3:
                message = "您的账户已被禁用！\n" + "咨询电话:" + getString(R.string.service_telephone_display);
                break;
            case 11:
                message = "您的资料已提交，请保持手机畅通，收到确认短信后，请重新打开【百家修】，咨询电话:" + getString(R.string.service_telephone_display);
                break;
            case 12:
                message = "您没有完成资料提交！\n" + "咨询电话:" + getString(R.string.service_telephone_display);
                break;
            case 99:
                message = "网络不通，请检查网络后重新进入！\n" + "咨询电话:" + getString(R.string.service_telephone_display);
                break;
            default:
                break;
        }

        if (!Utils.isNotEmpty(message)) {
            message = "您的账户出现异常！\n" + "咨询电话:" + getString(R.string.service_telephone_display);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("工人注册通知");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivitiesManager.getInstance().finishAllActivities();
            }
        });

        if (status == 2) {
            builder.setNeutralButton("完善注册信息", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Utils.startActivityForResult(MainActivity.this, ApplyActivity.class, Constant.ACTIVITY_APPLY_RESULT_CODE);
                }
            });
        } else {
            builder.setNeutralButton("电话询问", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    callService();
                    ActivitiesManager.getInstance().finishAllActivities();
                }
            });
        }

        builder.create().show();
    }

    private void callWorkerApply() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("百家修");
        builder.setMessage("为保证服务质量，所有的师傅都必须实名认证，请您先注册！");
        builder.setCancelable(false);
        builder.setPositiveButton("注册", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.startActivityForResult(MainActivity.this, ApplyActivity.class, Constant.ACTIVITY_APPLY_RESULT_CODE);
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.ACTIVITY_APPLY_RESULT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                showStatusDialog(11);
            } else {
                showStatusDialog(12);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void callService() {
        String mobile = getString(R.string.service_telephone);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + mobile));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        try {
            if (mDisplayRedDotTask != null) {
                mDisplayRedDotTask.cancel(true);
            }
            if (mRegisterPushTask != null) {
                mRegisterPushTask.cancel(true);
            }
            if (mGetStatusTask != null) {
                mGetStatusTask.cancel(true);
            }
            if (mCheckNewVersionTask != null) {
                mCheckNewVersionTask.cancel(true);
            }
        } catch (Exception e) {
        }

        super.onDestroy();
    }
}	


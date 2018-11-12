package com.bjxapp.worker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.BillApi;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.db.BjxInfo;
import com.bjxapp.worker.db.DBManager;
import com.bjxapp.worker.global.ActivitiesManager;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.RedDot;
import com.bjxapp.worker.push.BJXPushService;
import com.bjxapp.worker.push.PushIntentService;
import com.bjxapp.worker.push.PushParser;
import com.bjxapp.worker.push.XPushManager;
import com.bjxapp.worker.ui.titlemenu.ActionItem;
import com.bjxapp.worker.ui.titlemenu.TitlePopup;
import com.bjxapp.worker.ui.titlemenu.TitlePopup.OnItemOnClickListener;
import com.bjxapp.worker.ui.view.activity.JoinUsActivity;
import com.bjxapp.worker.ui.view.activity.PushDetailActivity;
import com.bjxapp.worker.ui.view.activity.user.ApplyActivity;
import com.bjxapp.worker.ui.view.activity.user.LoginActivity;
import com.bjxapp.worker.ui.view.activity.widget.dialog.SimpleConfirmDialog;
import com.bjxapp.worker.ui.view.base.BaseFragmentActivity;
import com.bjxapp.worker.ui.view.fragment.Fragment_Main_First;
import com.bjxapp.worker.ui.view.fragment.Fragment_Main_Fourth;
import com.bjxapp.worker.ui.view.fragment.Fragment_Main_Second;
import com.bjxapp.worker.ui.view.fragment.Fragment_Main_Third;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.zxing.CaptureActivity;
import com.google.gson.JsonObject;
import com.igexin.sdk.PushManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private DBManager mDbManager;

    private XWaitingDialog mWaitingDialog;

    @BindView(R.id.title_right_small_tv)
    TextView mTitleRightTv;

    @BindView(R.id.title_text_tv)
    TextView mTitleTv;

    @BindView(R.id.title_image_back)
    XImageView mBackIv;

    @BindView(R.id.main_ring)
    View mRingView;


    @OnClick(R.id.title_image_back)
    void onRing() {
        mRingView.setVisibility(View.GONE);
        Intent intent = new Intent();
        intent.setClass(this, PushDetailActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.title_right_small_tv)
    void onRightClick() {
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

        mDbManager = new DBManager(this);

        mWaitingDialog = new XWaitingDialog(MainActivity.this);

        if (!Utils.isNetworkAvailable(MainActivity.this)) {
            this.showStatusDialog(99);
            return;
        }

        //获取用户注册状态
        getUserStatus();

        //显示红点
        displayRedDot();

        //注册推送ID
        initPush();

        //检查最新APK版本
        checkNewVersion();
    }


    private void checkRingRedot() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<BjxInfo> list = (ArrayList<BjxInfo>) mDbManager.query(1, 0);
                    if (list != null && list.size() > 0) {
                        BjxInfo info = list.get(0);
                        if (!info.isRead()) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mRingView.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }
                } catch (Exception e) {

                }
            }
        }).start();
    }


    private void initPush() {
        PushManager.getInstance().initialize(this.getApplicationContext(), BJXPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), PushIntentService.class);

        String clientId = PushManager.getInstance().getClientid(this);

        if (!TextUtils.isEmpty(clientId)) {
            initPushToServer(clientId);
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String clientId = PushManager.getInstance().getClientid(MainActivity.this);
                    if (!TextUtils.isEmpty(clientId)) {
                        initPushToServer(clientId);
                    } else {
                        mHandler.postDelayed(this, 5000);
                    }
                }
            }, 5000);
        }

    }

    private void initPushToServer(String clientId) {

        BillApi billApi = KHttpWorker.ins().createHttpService(LoginApi.URL, BillApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("clientId", clientId);

        Call<JsonObject> call = billApi.bindPush(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                Log.d("slog_zd", "bind push : " + response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.d("slog_zd", "bind push fail : " + t.getLocalizedMessage());
            }
        });
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

                // mRingView.setVisibility(View.VISIBLE);
                mBackIv.setVisibility(View.VISIBLE);

                mRightImageView.setImageResource(R.drawable.icon_menu_add);
                mRightImageView.setPadding(0, 0, 0, 0);
                break;
            case R.id.main_tab_second:
                mIndex = 1;
                mTitleRightTv.setVisibility(View.GONE);
                mTitleTextView.setText(getString(R.string.main_tab_second_text));
                mRingView.setVisibility(View.GONE);
                mBackIv.setVisibility(View.GONE);
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
                mRingView.setVisibility(View.GONE);
                mBackIv.setVisibility(View.GONE);
                mThirdReminder.setVisibility(View.GONE);
                break;
            case R.id.main_tab_fourth:
                mIndex = 3;
                mTitleTextView.setText(getString(R.string.main_tab_fourth_text));
                mTitleRightTv.setVisibility(View.GONE);
                mRingView.setVisibility(View.GONE);
                mBackIv.setVisibility(View.GONE);
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

        /*mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                long time = System.currentTimeMillis();

                String content = "{'content':'您有新的工单，请注意接单','createTime':" + time + ",'isVoice':true,'title':'新订单','type':0}";
                PushParser.onMessageArrived(content);
            }
        }, 10000);*/

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

        //检查ring红点
        checkRingRedot();
    }

    @Override
    protected String getPageName() {
        return TAG;
    }

    private void findViewById() {
        mTitleTextView = (XTextView) findViewById(R.id.title_text_tv);
        mRightImageView = (XImageView) findViewById(R.id.title_image_right);
        mTitleTv.setText("首页");
        mTitleRightTv.setText("邀请关注");
    }

    private void initViews() {
        mRightImageView.setVisibility(View.GONE);
        mRightImageView.setImageResource(R.drawable.icon_menu_main);
        mBackIv.setVisibility(View.VISIBLE);
        mBackIv.setImageResource(R.drawable.ring);
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

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private void getUserStatus() {

        mWaitingDialog.show("正在查询注册信息，请稍候...", false);

        BillApi billApi = KHttpWorker.ins().createHttpService(LoginApi.URL, BillApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("token", ConfigManager.getInstance(this).getUserSession());

        Call<JsonObject> request = billApi.getServiceStatus(params);
        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mWaitingDialog != null) {
                            mWaitingDialog.dismiss();
                        }
                    }
                });

                JsonObject jsonObject = response.body();


                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {

                    final int code = jsonObject.get("code").getAsInt();
                    final String msg = jsonObject.get("msg").getAsString();
                    if (code == 0) {
                        int status = jsonObject.get("status").getAsInt();
                        toStatusDialog(status);

                        final int serviceStat = jsonObject.get("serviceState").getAsInt();

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mMainFirstFragment != null) {
                                    mMainFirstFragment.changeServiceStatusReal(serviceStat == 1);
                                }
                            }
                        });

                    } else if (code == 20001) {
                        showStatusDialog(msg, code);
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showStatusDialog(msg, code);
                            }
                        });
                    }
                } else {
                    toStatusDialog(99);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mWaitingDialog != null) {
                            mWaitingDialog.dismiss();
                            showStatusDialog(99);
                        }
                    }
                });
            }
        });
    }

    private void toStatusDialog(final int code) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showStatusDialog(code);
            }
        });
    }

    private void showStatusDialog(String msg, int status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("师傅注册通知");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivitiesManager.getInstance().finishAllActivities();
            }
        });

        if (status == 20001) {
            builder.setNeutralButton("重新登录", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // callService();
                    // ActivitiesManager.getInstance().finishAllActivities();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra("from", 0x01);
                    MainActivity.this.startActivity(intent);
                    finish();
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


    private void showStatusDialog(final int status) {
        String message = "";

        switch (status) {
            case 1:
                return;
            case 2:
                callWorkerApply();
                return;
            case 3:
                message = "您的资料正在审核中，请保持手机畅通，收到确认短信后，请重新打开【百家修】，咨询电话:" + getString(R.string.service_telephone_display);
                break;
            case 7:
                message = "您的账户已被禁用！\n" + "咨询电话:" + getString(R.string.service_telephone_display);
                break;
            case 6:
                message = "您的账户已被停用！\n" + "咨询电话:" + getString(R.string.service_telephone_display);
                break;
            case 5:
                message = "您的账户已被冻结！\n" + "咨询电话:" + getString(R.string.service_telephone_display);
                break;
            case 4:
                message = "你的资料未通过 ！ 请重新提交";
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

        /*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

        if (status == 4) {
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

        builder.create().show();*/


        final SimpleConfirmDialog dialog = new SimpleConfirmDialog(this);

        dialog.setTitleVisible(View.VISIBLE);
        dialog.setTitle("师傅注册通知");

        dialog.setContent(message);

        String negTxt = (status == 4) ? "完善注册信息" : "电话询问";

        dialog.setOnNegativeListener(negTxt, new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status == 4) {
                    Utils.startActivityForResult(MainActivity.this, ApplyActivity.class, Constant.ACTIVITY_APPLY_RESULT_CODE);
                } else {
                    callService();
                    ActivitiesManager.getInstance().finishAllActivities();
                }
            }
        });


        dialog.setOnPositiveListener("确认", new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status == 6) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } else {
                    ActivitiesManager.getInstance().finishAllActivities();
                }
            }
        });

        dialog.show();

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

            if (mCheckNewVersionTask != null) {
                mCheckNewVersionTask.cancel(true);
            }
        } catch (Exception e) {
        }

        super.onDestroy();
    }
}	


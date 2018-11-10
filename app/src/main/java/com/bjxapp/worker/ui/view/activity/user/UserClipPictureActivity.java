package com.bjxapp.worker.ui.view.activity.user;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;

import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.global.OurContext;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.DeviceUtils;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.R;

/**
 * 用户裁剪头像
 *
 * @author jason
 */
public class UserClipPictureActivity extends BaseActivity implements OnClickListener {
	protected static final String TAG = "头像裁剪界面";
    private ClipView mClipView;
    private boolean mIsNeedCutFunction = true;
    private final int FILE_SAVE_SUCCESS = 0;
    private final int FILE_SAVE_FAILED = 1;
    private int mClipStyle = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_clip);
        init();
    }

    private void init() {
        initViews();
        initBitmap();
    }

    private void initViews() {
        mClipView = (ClipView) findViewById(R.id.clipview);
        findViewById(R.id.rotate_left).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mClipView.rotate(-90.0f);
                    }
                }
        );

        findViewById(R.id.rotate_right).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mClipView.rotate(90.0f);
                    }
                }
        );
        findViewById(R.id.sure).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
    }

    private void initBitmap() {
        Uri imgUri = (Uri) getIntent().getParcelableExtra(Constant.EXTRA_KEY_USER_BITMAP_URI);
        String imgPath = getIntent().getStringExtra(Constant.EXTRA_KEY_USER_BITMAP);
        mClipStyle = getIntent().getIntExtra(Constant.EXTRA_KEY_CLIP_WHAT, ClipView.CLIP_FOR_OTHER_BACKGROUND);
        mIsNeedCutFunction = getIntent().getBooleanExtra(Constant.EXTRA_KEY_CLIP_VIEW_NEED_CUT, true);
        if (mClipStyle == ClipView.CLIP_FOR_MAIN_BACKGROUND) {
            mIsNeedCutFunction = false;
        }
        
        Rect clipRect = initClipFrame();
        if (imgPath != null && checkFilePath(imgPath)) {
            mClipView.setImage(imgPath, clipRect, mClipStyle);
        } else if (imgUri != null) {
            mClipView.setImage(imgUri, clipRect, mClipStyle);
        } else {
            this.finish();
        }
    }

    private boolean checkFilePath(String imgPath) {
        try {
            File f = new File(imgPath);
            if (f.exists())
                return true;
        } catch (Exception e) {

        }
        return false;
    }

    private Rect initClipFrame() {
        int top = 10;
        int screenWidth = OurContext.getScreenWidth(getApplicationContext());
        int screenHeight = OurContext.getScreenHeight(getApplicationContext());
        Rect clipRect = new Rect(0, top, screenWidth, top + screenWidth);
        ImageView imageView = findViewById(R.id.clip_frame);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.height = screenWidth;
        params.width = screenWidth;
        findViewById(R.id.clip_frame).setLayoutParams(params);
        if (!mIsNeedCutFunction) {
            findViewById(R.id.clip_frame).setVisibility(View.INVISIBLE);
            findViewById(R.id.top_shade).setVisibility(View.INVISIBLE);
            LinearLayout rotateLayout = (LinearLayout) findViewById(R.id.rotate_layout);
            rotateLayout.setBackgroundResource(R.color.transparent);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rotateLayout.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            return new Rect(0, 0, screenWidth, screenHeight - DeviceUtils.getStatusBarHeight(this));
        }
        return clipRect;
    }

    @Override
    protected void onDestroy() {
        mClipView.onDestroy();
        super.onDestroy();
    }

    private void clipBitmap() {
        String imgName = mClipView.getBitmap();
        Intent intent = new Intent();
        if (imgName != null)
            intent.putExtra(Constant.EXTRA_KEY_USER_BITMAP, imgName);
        if (mClipStyle == ClipView.CLIP_FOR_MAIN_BACKGROUND) {
            checkStatus(imgName);
        } else {
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void checkStatus(String imgName) {
        if (!Utils.isNotEmpty(imgName)) {
            mFileHandler.sendEmptyMessage(FILE_SAVE_FAILED);
        } else {
            //ConfigManager config = ConfigManager.getInstance(this.getApplicationContext());
            //config.setMainBgResId(Constant.MAIN_BG_NOT_RES);
            //config.setMainBgChanged(true);
            mFileHandler.sendEmptyMessage(FILE_SAVE_SUCCESS);
        }
    }

    private void goToHome() {
    	/*
        for (Activity a : sActivityInStack) {
            String className = a.getClass().getName();
            if (className.contains(MainBgSettingActivity.class.getName())|| className.contains(CityEgSettingActivity.class.getName())) {
                a.finish();
            }
        }
        */
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure:
                clipBitmap();
                break;
            case R.id.cancel:
                finish();
                break;
        }
    }

    private Handler mFileHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FILE_SAVE_SUCCESS:
                    Utils.showLongToast(UserClipPictureActivity.this,"Change main background success!");
                    //Intent intent = new Intent(Constant.ACTION_REFRESH_MAIN_BG);
                    //sendBroadcast(intent);
                    goToHome();
                    break;
                case FILE_SAVE_FAILED:
                	Utils.showLongToast(UserClipPictureActivity.this,"Change main background fail!");
                    finish();
                    break;
            }
        }

    };

	@Override
	protected void initControl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getPageName() {
		return TAG;
	}
}
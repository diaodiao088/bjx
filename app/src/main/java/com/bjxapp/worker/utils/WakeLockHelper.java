package com.bjxapp.worker.utils;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ContentResolver;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

/**
 * 唤醒屏幕功能的辅助类
 * 
 * @author KidFeng <kid.stargazer@gmail.com>
 * 
 */
public class WakeLockHelper {
	private static final String TAG = "WakeLockHelper";

	private KeyguardLock mKeyguardLock;
	// 唤醒屏幕亮度、键盘亮度
	private WakeLock mWakeLock;
	// 部分机型CPU休眠后无法唤醒，需要单独锁唤醒
	// private WakeLock mPartialWakeLock;
	private ScreenAnimationUtils mAnimationUtils;

	private WakeLockHelper(Context context) {
		mAnimationUtils = new ScreenAnimationUtils(context);
		load(context);
	}

	private void load(Context context) {

		final KeyguardManager km = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);
		mKeyguardLock = km.newKeyguardLock(context.getPackageName());

		// mPartialWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |
		// PowerManager.ACQUIRE_CAUSES_WAKEUP |
		// PowerManager.ON_AFTER_RELEASE, "zclock");
		setWakeLockMode(context, PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE);

	}

	public static synchronized WakeLockHelper getInstance(Context context) {
		return new WakeLockHelper(context);
	}

	public synchronized void disableKeyguard() {
		mAnimationUtils.turnOff();
		Log.i(TAG, "aquire wake lock");
		mWakeLock.acquire();
		Log.i(TAG, "aquire keyguard");
		mKeyguardLock.disableKeyguard();
	}

	/**
	 * 存在多个{@link WakeLockHelper}实例同时解锁的情况下, 所有实例都释放锁时系统才会上锁
	 */
	public synchronized void enableKeyguard() {
        try {
            mAnimationUtils.recover();
            if (mWakeLock.isHeld()) {
                Log.d(TAG, "enableKeyguard isheld");
                Log.i(TAG, "release wake lock");
                mWakeLock.release();
            }
            Log.i(TAG, "release keygurad");
            mKeyguardLock.reenableKeyguard();
        }catch(Exception e) {}
	}

	public synchronized boolean isHeld() {
		return mWakeLock.isHeld();
	}

	public static synchronized WakeLock getWakeLock(Context context) {
		WakeLock waker = ((PowerManager) context
				.getSystemService(Context.POWER_SERVICE)).newWakeLock(1,
				context.getPackageName());
		return waker;
	}

	private synchronized void setWakeLockMode(Context context, int nMode) {
		// Log.d(TAG, "setWakeLockMode wake lock");

		boolean isHeld = false;
		if (mWakeLock != null) {
			if (mWakeLock.isHeld()) {
				isHeld = true;
				mWakeLock.release();
			}
			mWakeLock = null;
		}

		final PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);

		mWakeLock = pm.newWakeLock(nMode, context.getPackageName());
		if (isHeld) {
			mWakeLock.acquire();
		}
	}

	public synchronized void releaseWakeLock() {
		if (mWakeLock != null) {
			if (mWakeLock.isHeld()) {
				Log.i(TAG, "release wake lock");
				mWakeLock.release();
			}
		}
	}

	/**
	 * 部分rom存在开屏动画，所以响铃时将其先禁用，然后再还原，适用CM7和MIUI，其他如果存在同样的问题， 只需要找到
	 * com.android.providers下的数据库文件，将其key取出
	 * 
	 * @author dushengjun
	 * 
	 */
	private class ScreenAnimationUtils {
		private int mLastOnStateValue = -1;
		private int mLastOffStateValue = -1;

		private static final String ON_KEY = "electron_beam_animation_on";
		private static final String OFF_KEY = "electron_beam_animation_off";
		private Context mContext;

		public ScreenAnimationUtils(Context context) {
			mContext = context;
		}

		public void turnOff() {
			ContentResolver cr = mContext.getContentResolver();
			try {
				mLastOnStateValue = Settings.System.getInt(cr, ON_KEY);
				mLastOffStateValue = Settings.System.getInt(cr, OFF_KEY);
			} catch (SettingNotFoundException e) {
			}
			if (mLastOffStateValue > 0) {
				Settings.System.putInt(cr, ON_KEY, 0);
			}
			if (mLastOnStateValue > 0) {
				Settings.System.putInt(cr, OFF_KEY, 0);
			}
		}

		public void recover() {
			ContentResolver cr = mContext.getContentResolver();
			if (mLastOnStateValue > 0) {
				Settings.System.putInt(cr, ON_KEY, mLastOnStateValue);
			}
			if (mLastOffStateValue > 0) {
				Settings.System.putInt(cr, OFF_KEY, mLastOffStateValue);
			}
		}
	}
}

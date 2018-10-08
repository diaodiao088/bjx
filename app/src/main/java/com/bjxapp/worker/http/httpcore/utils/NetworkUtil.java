package com.bjxapp.worker.http.httpcore.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;

import com.bjxapp.worker.http.keyboard.commonutils.CommonUtilsEnv;
import com.bjxapp.worker.http.keyboard.commonutils.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO 兼容8.0 {@link ConnectivityManager#registerNetworkCallback(NetworkRequest, ConnectivityManager.NetworkCallback)}
 */
public class NetworkUtil {
    private static final String TAG = "NetWorkUtil";
    public static final int NO_NETWORK = -1;
    private static boolean sIsListening = false;
    private static Integer sNetworkType = null;
    private static BroadcastReceiver receiver = null;
    private static ConnectivityManager connectivityManager = null;
    private static final List<OnConnectivityChangeListener> sConnectivityListener = new ArrayList<>();

    public static synchronized void init(Context context) {
        if (sIsListening) {
            return;
        }
        if (receiver != null) {
            try {
                context.unregisterReceiver(receiver);
            } catch (Exception e) {
                Log.w(TAG, "init: "+e.getMessage() );
            }
        }
        KLog.d("NetworkUtil", "init");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) {
                    sNetworkType = NO_NETWORK;
                    notifyNetworkChanged();
                    return;
                }
                Bundle extras = intent.getExtras();
                if (extras == null) {
                    sNetworkType = NO_NETWORK;
                    notifyNetworkChanged();
                    return;
                }
                NetworkInfo info = (NetworkInfo) extras.getParcelable(ConnectivityManager.EXTRA_NETWORK_INFO);

                if (info == null) {
                    sNetworkType = NO_NETWORK;
                    notifyNetworkChanged();
                    return;
                }
                KLog.d("NetworkUtil", "onRecieve " + info.getType() + " " + info.getState());
                State state = info.getState();
                if (state == State.CONNECTED
                        && (info.getType() == ConnectivityManager.TYPE_ETHERNET
                        || info.getType() == ConnectivityManager.TYPE_WIFI
                        || info.getType() == ConnectivityManager.TYPE_MOBILE
                        || info.getType() == ConnectivityManager.TYPE_MOBILE_DUN)) {
                    sNetworkType = info.getType();
                    notifyNetworkChanged();
                } else {
                    sNetworkType = NO_NETWORK;
                    notifyNetworkChanged();
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        try {
            context.registerReceiver(receiver, filter);
            sIsListening = true;
        } catch (Exception e) {
            Log.w(TAG, "init: "+e.getMessage());
        }
    }

    public static void unInit(Context context) {
        if (sIsListening && receiver != null) {
            try {
                context.getApplicationContext().unregisterReceiver(receiver);
                sIsListening = false;
                sNetworkType = NO_NETWORK;
            } catch (Exception e) {
                Log.w(TAG, "unInit: "+e.getMessage() );
            }
        }
    }

    private static void notifyNetworkChanged() {
        synchronized (sConnectivityListener) {
            for (OnConnectivityChangeListener l : sConnectivityListener) {
                if (l != null) {
                    l.OnConnectivityChange(sNetworkType);
                }
            }
        }
    }

    public static void addCallback(OnConnectivityChangeListener listener) {
        synchronized (sConnectivityListener) {
            if (listener != null && !sConnectivityListener.contains(listener)) {
                sConnectivityListener.add(listener);
            }
        }
    }

    public static void removeCallback(OnConnectivityChangeListener listener) {
        synchronized (sConnectivityListener) {
            sConnectivityListener.remove(listener);
        }
    }

    public interface OnConnectivityChangeListener {
        public void OnConnectivityChange(int type);
    }

    /**
     * 判断网络是否可用
     */
    public static boolean isNetworkAvailable() {
        if (!sIsListening || sNetworkType == null) {
            sNetworkType = IsNetworkAvailable(CommonUtilsEnv.getInstance().getApplicationContext());
        }
        return sNetworkType != NO_NETWORK;
    }

    /**
     * 判断Wifi网络是否可用
     */
    public static boolean isWifiNetworkAvailable() {
        if (!sIsListening || sNetworkType == null) {
            sNetworkType = IsNetworkAvailable(CommonUtilsEnv.getInstance().getApplicationContext());
        }
        return sNetworkType == ConnectivityManager.TYPE_WIFI || sNetworkType == ConnectivityManager.TYPE_ETHERNET;
    }

    public static boolean isMobileNetworkAvailable() {
        if (!sIsListening || sNetworkType == null) {
            sNetworkType = IsNetworkAvailable(CommonUtilsEnv.getInstance().getApplicationContext());
        }
        return sNetworkType == ConnectivityManager.TYPE_MOBILE || sNetworkType == ConnectivityManager.TYPE_MOBILE_DUN;
    }

    public static int getNetworkConnectionType() {
        if (!sIsListening || sNetworkType == null) {
            sNetworkType = IsNetworkAvailable(CommonUtilsEnv.getInstance().getApplicationContext());
        }
        return sNetworkType;
    }

    private static int IsNetworkAvailable(Context context) {
        if (context == null) {
            return NO_NETWORK;
        }
        if (connectivityManager == null) {
            try {
                connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            } catch (Exception e) {
                Log.w(TAG, "IsNetworkAvailable: "+e.getMessage() );
            }
        }

        if (connectivityManager == null) {
            return NO_NETWORK;
        }

        try {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                return info.getType();
            }
            // 修改解决判断网络时的崩溃
            // mobile 3G Data Network
            NetworkInfo net3g = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (net3g != null) {
                State mobile = net3g.getState();// 显示3G网络连接状态
                if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
                    return ConnectivityManager.TYPE_MOBILE;
                }
            }

            NetworkInfo netwifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (netwifi != null) {
                State wifi = netwifi.getState(); // wifi
                // 如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
                if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
                    return ConnectivityManager.TYPE_WIFI;
                }
            }
            return NO_NETWORK;
        } catch (Throwable e) {
            ///< 异常了，没法了，返回默认值吧...
            return NO_NETWORK;
        }
    }

}

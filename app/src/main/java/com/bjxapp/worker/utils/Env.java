package com.bjxapp.worker.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Env {

	public static final int ANDROID_1_5 = 3;
	public static final int ANDROID_1_6 = 4;
	public static final int ANDROID_2_0 = 5;
	public static final int ANDROID_2_0_1 = 6;
	public static final int ANDROID_2_1 = 7;
	public static final int ANDROID_2_2 = 8;
	public static final int ANDROID_2_3 = 9;
	public static final int ANDROID_2_3_3 = 10;
	public static final int ANDROID_3_0 = 11;
	public static final int ANDROID_4_0 = 14;
	public static final int ANDROID_4_1 = 16;
	public static final int ANDROID_4_2 = 17;
    public static final int ANDROID_4_4 = 19;
    public static final int ANDROID_5_0 = 20;

	public static final int ROOT_STATUS_UNCHECK = -2;
	public static final int ROOT_STATUS_UNKNOWN = -1;
	public static final int ROOT_STATUS_FALSE = 0;
	public static final int ROOT_STATUS_TRUE = 1;

	private static final String TAG = "Env";
	private static final int SDK_LEVEL = Integer.parseInt(Build.VERSION.SDK);
    private static int rootStatus = ROOT_STATUS_UNCHECK;

	public static String getVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException nnfe) {
			return "null";
		} catch (Exception e) {
			return "null";
		}
	}

	public static int getVersionCode(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException nnfe) {
			return -1;
		} catch (Exception e) {
			return -1;
		}
	}

	

	public static String getAndroidId(Context context) {
		return Settings.Secure.getString(context.getContentResolver(),
				 Settings.Secure.ANDROID_ID);
	}
	
	public static String getLocalIpAddress() {
		try {
			String ipv4;
			Enumeration<NetworkInterface> listOfInterfaces = NetworkInterface.getNetworkInterfaces();
			if (listOfInterfaces == null) return "null";
			
			ArrayList<NetworkInterface> mylist = Collections
					.list(listOfInterfaces);
			for (NetworkInterface ni : mylist) {

				ArrayList<InetAddress> ialist = Collections.list(ni
						.getInetAddresses());
				for (InetAddress address : ialist) {
					if (!address.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(ipv4 = address
									.getHostAddress())) {
						return ipv4;
					}
				}
			}
		} catch (SocketException ex) {}
		
		return "null";
	}
	
	public static String getLocalMacAdress(Context context) {
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			return info.getMacAddress();
	}
	
	public static String getSystemVersion() {
		return Build.VERSION.RELEASE;
	}

	public static int getSDKLevel() {
		return SDK_LEVEL;
	}

	public static String getSDK() {
		if (Build.VERSION.SDK != null)
			return Build.VERSION.SDK;
		return "null";
	}

	/**
	 * model截断小于30个字符
	 * 
	 * @return
	 */
	public static String getModels() {
		final int maxLength = 30;
		String model = Build.MODEL;
		if (model == null) {
			model = "null";
		}
		if (model.length() > maxLength) {
			model = model.substring(0, maxLength);
		}
		return model;
	}

	public static int getTimeZone() {
		TimeZone timezone = TimeZone.getDefault();
		return timezone.getOffset(System.currentTimeMillis());
	}

	public static String getCounty(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String countryCode = tm.getSimCountryIso();
		if (countryCode == null)
			countryCode = "null";
		return countryCode;
	}

	/**
	 * 获得小于64个字符的rom信息
	 * 
	 * @return
	 */
	public static String getRom64() {
		StringBuilder sb = new StringBuilder();
		sb.append(getRom());
		sb.append(" ");
		sb.append(getExtraRom());
		if (sb.length() > 64) {
			return sb.substring(0, 63);
		}
		return sb.toString();
	}

	public static String getRom() {
		if (Build.BOARD != null)
			return Build.BOARD;
		return "null";
	}

	private static String getExtraRom() {
		if (Build.DISPLAY != null) {
			return Build.DISPLAY;
		}
		return "null";
	}

    public static boolean hasGooglePlay(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=".concat(context
                        .getPackageName())));
        List<ResolveInfo> infos = context.getPackageManager().queryIntentActivities(intent, 0);
        if(infos != null && !infos.isEmpty()) {
            for(ResolveInfo info : infos) {
                if(info != null) {
                    ActivityInfo ai = info.activityInfo;
                    if(ai != null) {
                        if(ai.packageName != null && ai.packageName.toLowerCase().equals("com.android.vending")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static List<String> getMarketList(Context context) {
        List<String> marketList = new ArrayList<String>(5);
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=".concat(context
                        .getPackageName())));
        List<ResolveInfo> infos = context.getPackageManager().queryIntentActivities(intent, 0);
        if(infos != null && !infos.isEmpty()) {
            for(ResolveInfo info : infos) {
                if(info != null) {
                    ActivityInfo ai = info.activityInfo;
                    if(ai != null && ai.packageName != null) {
                        if(ai.packageName.contains("inputmethod")) continue;
                        marketList.add(ai.packageName);
                    }
                }
            }
        }
        return marketList;
    }

	public static int getRootStatus() {
		if (rootStatus == ROOT_STATUS_UNCHECK) {
			String pathStr = System.getenv("PATH");
			String[] paths;

			if (pathStr == null) {
				paths = new String[] { "/sbin", "/system/sbin", "/system/bin",
						"/system/xbin" };
				rootStatus = ROOT_STATUS_UNKNOWN;
			} else {
				paths = pathStr.split(File.pathSeparator);
				rootStatus = ROOT_STATUS_FALSE;
			}

			for (String path : paths) {
				File suFile = new File(path + File.separatorChar + "su");
				try {
					if (suFile.isFile()) {
						rootStatus = ROOT_STATUS_TRUE;
						break;
					}
				} catch (SecurityException e) {
					Log.w(TAG, "", e);
					rootStatus = ROOT_STATUS_UNKNOWN;
				}
			}
		}
		return rootStatus;
	}
	
	/**
	 * 应用内切换到指定语言
	 * 
	 * @param context
	 * @param language 如：Locale.ENGLISH 
	 */
	public static void changeToSpecificLanguage(Context context, Locale language) {
		Resources res = context.getResources();
    	Configuration config = res.getConfiguration();
    	config.locale = language;
    	res.updateConfiguration(config, null);
	}
}

package com.bjxapp.worker.global;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.Locale;

public class OurContext {
	private static OurContext instance;

	public static synchronized OurContext getInstance() {
		if (instance == null)
			instance = new OurContext();
		return instance;
	}

	private static float density = -1;
	private static int screenWidth_px = 480;
	private static int screenHeight_px = 800;
	public static String locale = "";

	private final static String En_1 = "en";
	// private final static String En_2 = "en_GB";
	// private final static String En_3 = "en_US";
	private final static String Zh_1 = "zh"; // Simplified prc china chinese
	private final static String Zh_2 = "zh_TW"; // Traditional tw
	private final static String Zh_3 = "zh_HK"; // Traditional tw hk
	private final static String Zh_4 = "zh_CN";
	private final static String Ja_1 = "ja";
	private final static String Ja_2 = "ja_JP";
	private final static String Ko_1 = "ko";
	private final static String Ko_2 = "ko_KR";

	private OurContext() {
		localeFind();
	}

	private static void checkInit(Context context) {
		//if (density != -1)
		//	return;
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(dm);

		OurContext.screenWidth_px = dm.widthPixels;
		OurContext.screenHeight_px = dm.heightPixels;
		OurContext.density = dm.density;
	}

	public String getLocalLanguage() {
		return locale;
	}

	public static float getDensity(Context context) {
		checkInit(context);
		return density;
	}

	public static int getScreenWidth(Context context) {
		checkInit(context);
		return screenWidth_px;
	}

	public static int getScreenHeight(Context context) {
		checkInit(context);
		return screenHeight_px;
	}

	private static void localeFind() {
		Locale myLocale = Locale.getDefault();
		locale = myLocale.toString();
	}

	public static boolean isSimplified() {
		localeFind();
		if (locale.equals(Zh_1) || locale.equals(Zh_4))
			return true;
		return false;
	}

	public static boolean isTraditional_TW() {
		localeFind();
		if (locale.equals(Zh_2))
			return true;
		return false;
	}

	public static boolean isTraditional_HK() {
		localeFind();
		if (locale.equals(Zh_3))
			return true;
		return false;
	}

	public static boolean isTraditional() {
		localeFind();
		if (locale.equals(Zh_2) || locale.equals(Zh_3))
			return true;
		return false;
	}

	public static boolean isChinese() {
		localeFind();
		if (locale.equals(Zh_2) || locale.equals(Zh_3) || locale.equals(Zh_1)
				|| locale.equals(Zh_4))
			return true;
		return false;
	}

	public static boolean isEnglish() {
		localeFind();
		if (locale.contains(En_1))
			return true;
		return false;
	}

	public static boolean isJapan() {
		localeFind();
		if (locale.equals(Ja_1) || locale.equals(Ja_2))
			return true;
		return false;
	}

	public static boolean isKorea() {
		localeFind();
		if (locale.equals(Ko_1) || locale.equals(Ko_2))
			return true;
		return false;
	}

}

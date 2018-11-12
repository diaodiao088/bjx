package com.bjxapp.worker.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.message.BasicNameValuePair;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import com.bjxapp.worker.global.ActivitiesManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.listener.OnEditDialogListener;
import com.bjxapp.worker.ui.view.activity.map.MapActivityNew;
import com.bjxapp.worker.ui.view.activity.search.SearchActivity;
import com.bjxapp.worker.ui.view.activity.search.SearchActivityNew;
import com.bjxapp.worker.ui.view.activity.search.SearchSingleActivity;
import com.bjxapp.worker.ui.view.activity.user.UserClipPictureActivity;
import com.bjx.master.R;;

public class Utils {
	
	public static boolean isNotEmpty(String str) {
		return str != null && false == str.trim().equals("");
	}

	public static boolean isNotEmpty(CharSequence str) {
		return str != null && false == str.toString().trim().equals("");
	}
	
	public static void showLongToast(Context context, String pMsg) {
		Toast.makeText(context, pMsg, Toast.LENGTH_LONG).show();
	}

	public static void showShortToast(Context context, String pMsg) {
		Toast.makeText(context, pMsg, Toast.LENGTH_SHORT).show();
	}
	
	public static double getDouble(String value){
		double result = 0;
		if(isNotEmpty(value)){
			try{
				result = Double.parseDouble(value);
			}
			catch(Exception e){}
		}
		return result;
	}
	
	public static String getDoubleFormat(String value){
		double result = 0;
		DecimalFormat decimalFormat = new DecimalFormat("###.00");
		if(isNotEmpty(value)){
			try{
				result = Double.parseDouble(value);
			}
			catch(Exception e){}
		}
		return decimalFormat.format(result);
	}
	
	public static String getDoubleFormat(double value){
		DecimalFormat decimalFormat = new DecimalFormat("###.00");
		return decimalFormat.format(value);
	}
	
	/**
	 * 隐藏软键盘
	 * 
	 * @param context
	 * @param view
	 */
	public static void hideSoftInput(Context context, View view) {
		if (view == null)
			return;
		((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 软键盘显示
	 * 
	 * @param context
	 */
	public static void showSoftInput(final Context context) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				InputMethodManager m = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 500);
	}

	/**
	 * 关闭 Activity
	 * 
	 * @param activity
	 */
	public static void finishActivity(Activity activity) {
		ActivitiesManager.getInstance().popOneActivity(activity);
		activity.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
	}
	
	public static void finishWithoutAnim(Activity activity) {
		ActivitiesManager.getInstance().popOneActivity(activity);
	}

	/**
	 * 打开Activity
	 * 
	 * @param activity
	 * @param cls
	 * @param name
	 */
	public static void startActivity(Activity activity, Class<?> clazz,BasicNameValuePair... name) {
		Intent intent = new Intent();
		intent.setClass(activity, clazz);
		if (name != null)
			for (int i = 0; i < name.length; i++) {
				intent.putExtra(name[i].getName(), name[i].getValue());
			}
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
	}
	
	/**
	 * 打开Activity
	 * 
	 * @param activity
	 * @param cls
	 * @param name
	 */
	public static void startActivity(Context context, Class<?> clazz,BasicNameValuePair... name) {
		Activity activity = (Activity)context;
		Intent intent = new Intent();
		intent.setClass(activity, clazz);
		if (name != null)
			for (int i = 0; i < name.length; i++) {
				intent.putExtra(name[i].getName(), name[i].getValue());
			}
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
	}
	
	/**
	 * 打开Activity
	 * 
	 * @param activity
	 * @param cls
	 * @param name
	 */
	public static void startActivityForResult(Activity activity, Class<?> clazz,int type) {
		Intent intent = new Intent();
		intent.setClass(activity, clazz);
		activity.startActivityForResult(intent,type);
		activity.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
	}
	
	public static void startActivityForResult(Activity activity, Fragment fragment, Class<?> clazz,int type) {
		Intent intent = new Intent();
		intent.setClass(activity, clazz);
		fragment.startActivityForResult(intent,type);
		activity.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
	}
	
	public static void startMapSelectActivity(Context context, Class<?> clazz,double latitude,double longitude,String address,String city) {
		Activity activity = (Activity)context;
		Intent intent = new Intent();
		intent.setClass(activity, clazz);
		intent.putExtra(MapActivityNew.USER_LATITUDE, latitude);
		intent.putExtra(MapActivityNew.USER_LONGTITUDE, longitude);
		intent.putExtra(MapActivityNew.USER_ADDRESS, address);
		activity.startActivityForResult(intent,Constant.CONSULT_WORK_MAP);
		activity.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
	}
	
	public static void startConsultActivity(Activity activity, int type, Map<String, String> params, String code) {
		Intent intent = new Intent();
		intent.setClass(activity, SearchActivityNew.class);
		
		intent.putExtra("type", type);
		intent.putExtra("code", code);
		
		if (params != null){
			Iterator<String> iter = params.keySet().iterator();  
			while (iter.hasNext()) {  
			    String key = iter.next();  
			    String value = params.get(key);
			    intent.putExtra(key, value);
			} 
		}
		
		activity.startActivityForResult(intent,type);
		activity.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
	}
	
	public static void startSingleConsultActivity(Activity activity, int type) {
		Intent intent = new Intent();
		intent.setClass(activity, SearchSingleActivity.class);
		
		intent.putExtra("type", type);
		
		activity.startActivityForResult(intent,type);
		activity.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
	}
	
    /**
     * 跳转到选择本地图片的页面
     *
     * @param context
     * @param requestCode
     */
    public static void startChooseLocalPictureActivity(Context context, int requestCode) {
    	
    	/*
        Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
        getImage.addCategory(Intent.CATEGORY_OPENABLE);
        getImage.setType("image/*");
        ((Activity) context).startActivityForResult(getImage, requestCode);
        */
        
    	//据说此种方式支持4.4以后的获取文件路径，没有验证
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        ((Activity) context).startActivityForResult(intent, requestCode);

    }
    
    /**
     * 跳转到选择本地图片的页面
     *
     * @param context
     * @param requestCode
     */
    public static void startChooseLocalPictureActivity(Fragment fragment, int requestCode) {
    	/*
        Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
        getImage.addCategory(Intent.CATEGORY_OPENABLE);
        getImage.setType("image/*");
        fragment.startActivityForResult(getImage, requestCode);
        */
    	
    	//据说此种方式支持4.4以后的获取文件路径，没有验证
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        fragment.startActivityForResult(intent, requestCode);
    }
    
    /**
     * 跳转到照相页面
     *
     * @param context
     * @param requestCode
     */
    public static void startTakePhotoActivity(Context context, int requestCode, final Uri photoUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }
    
    /**
     * 跳转到照相页面
     *
     * @param context
     * @param requestCode
     */
    public static void startTakePhotoActivity(Fragment fragment, int requestCode, final Uri photoUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        fragment.startActivityForResult(intent, requestCode);
    }
    
	public static String getFilePathFromIntentData(Uri uri, Activity activity) {
		/*
		if (Env.getSDKLevel() >= Env.ANDROID_4_4 || uri == null) {
			return null;
		}
		*/
		String img_path = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualImagecursor = activity.managedQuery(uri, proj, null, null,
				null);
		if (actualImagecursor != null) {
			int actual_image_column_index = actualImagecursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			actualImagecursor.moveToFirst();
			img_path = actualImagecursor.getString(actual_image_column_index);
		}
		if (img_path == null) {
			img_path = uri.getPath();
		}
		Logger.i("ImageFile", img_path);
		return img_path;
	}

    /**
     * 跳转到图片剪裁页面
     *
     * @param context
     * @param imgPath
     */
    public static void startClipPictureActivity(Context context, String imgPath, int clipWhat, boolean needCut) throws IOException {
        Intent intent = new Intent(context, UserClipPictureActivity.class);
        if (imgPath == null)
            return;
        intent.putExtra(Constant.EXTRA_KEY_USER_BITMAP, imgPath);
        intent.putExtra(Constant.EXTRA_KEY_CLIP_WHAT, clipWhat);
        intent.putExtra(Constant.EXTRA_KEY_CLIP_VIEW_NEED_CUT, needCut);
        ((Activity) context).startActivityForResult(intent, Constant.REQUEST_CODE_CLOCK_CLIP_IMG);
    }

    /**
     * 跳转到图片剪裁页面
     *
     * @param context
     * @param imgPath
     */
    public static void startClipPictureActivity(Context context, Fragment fragment, String imgPath, int clipWhat, boolean needCut) throws IOException {
        Intent intent = new Intent(context, UserClipPictureActivity.class);
        if (imgPath == null)
            return;
        intent.putExtra(Constant.EXTRA_KEY_USER_BITMAP, imgPath);
        intent.putExtra(Constant.EXTRA_KEY_CLIP_WHAT, clipWhat);
        intent.putExtra(Constant.EXTRA_KEY_CLIP_VIEW_NEED_CUT, needCut);
        fragment.startActivityForResult(intent, Constant.REQUEST_CODE_CLOCK_CLIP_IMG);
    }
    
	 /**
     * 跳转到图片剪裁页面
     *
     * @param context
     */
    public static void startClipPictureActivity(Context context, String imgPath, Uri uri, int clipWhat, boolean needCut)
            throws IOException {
        Intent intent = new Intent(context, UserClipPictureActivity.class);
        if (imgPath != null) {
            intent.putExtra(Constant.EXTRA_KEY_USER_BITMAP, imgPath);
        } else if (uri != null) {
            intent.putExtra(Constant.EXTRA_KEY_USER_BITMAP_URI, uri);
        } else {
            return;
        }
        intent.putExtra(Constant.EXTRA_KEY_CLIP_WHAT, clipWhat);
        intent.putExtra(Constant.EXTRA_KEY_CLIP_VIEW_NEED_CUT, needCut);
        ((Activity) context).startActivityForResult(intent, Constant.REQUEST_CODE_CLOCK_CLIP_IMG);
    }

    public static void startClipPictureActivity(Context context, Fragment fragment, String imgPath, Uri uri, int clipWhat, boolean needCut)
            throws IOException {
        Intent intent = new Intent(context, UserClipPictureActivity.class);
        if (imgPath != null) {
            intent.putExtra(Constant.EXTRA_KEY_USER_BITMAP, imgPath);
        } else if (uri != null) {
            intent.putExtra(Constant.EXTRA_KEY_USER_BITMAP_URI, uri);
        } else {
            return;
        }
        intent.putExtra(Constant.EXTRA_KEY_CLIP_WHAT, clipWhat);
        intent.putExtra(Constant.EXTRA_KEY_CLIP_VIEW_NEED_CUT, needCut);
        fragment.startActivityForResult(intent, Constant.REQUEST_CODE_CLOCK_CLIP_IMG);
    }

	/**
	 * 解析用户拍照图片路径
	 */
	public static String getPhotoUrl(Context context, Uri photoUri, Intent data) {
		String photoPath = "";
		ContentResolver cr = context.getContentResolver();
		if (photoUri != null) {
			Cursor cursor = cr.query(photoUri, null, null, null, null);
			cursor.moveToFirst();
			if (cursor != null) {
				photoPath = cursor.getString(1); // 这个就是我们想要的原图的路径
				cursor.close();
			}
		} else {
			if (data != null) {
				Uri uri = data.getData();
				if (uri != null && Utils.isNotEmpty(uri.getPath())) {
					photoPath = uri.getPath();
					if (!FileUtils.isExist(photoPath)) {
						String[] proj = { MediaStore.Images.Media.DATA };
						Cursor actualimagecursor = cr.query(uri, proj, null,
								null, null);
						int actual_image_column_index = actualimagecursor
								.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						actualimagecursor.moveToFirst();
						if (actualimagecursor != null)
							photoPath = actualimagecursor
									.getString(actual_image_column_index);
					}
				}
			}
		}
		return photoPath;
	}

	/**
	 * 判断是否有网络
	 */
	public static boolean isNetworkAvailable(Context context) {
		if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			return false;
		} else {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

			if (connectivity == null) {
				Log.w("Utility", "couldn't get connectivity manager");
			} else {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							Log.d("Utility", "network is available");
							return true;
						}
					}
				}
			}
		}
		Log.d("Utility", "network is not available");
		return false;
	}
	
	/**
	 * 是否连接WIFI
	 * @param context
	 * @return
	 */
    public static boolean isWifiConnected(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null) 
        	return false;
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo == null) 
        	return false;
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }
     
        return false ;
    }

	public static Date stringToDate(String str) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		Date date = null;
		try {
			// Fri Feb 24 00:00:00 CST 2012
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 验证邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);

		return m.matches();
	}

	/**
	 * 验证手机号
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobile(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(17[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 验证是否是数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher match = pattern.matcher(str);
		if (match.matches() == false) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public static String getVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private static float sDensity = 0;

	/**
	 * DP转换为像素
	 * 
	 * @param context
	 * @param nDip
	 * @return
	 */
	public static int dipToPixel(Context context, int nDip) {
		if (sDensity == 0) {
			final WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics dm = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(dm);
			sDensity = dm.density;
		}
		return (int) (sDensity * nDip);
	}
	
	/**
	 * 判断一个activity是否在运行
	 * @param mContext
	 * @param activityClassName
	 * @return
	 */
	public static boolean isActivityRunning(Context mContext,String activityClassName){  
	    ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);  
	    List<RunningTaskInfo> info = activityManager.getRunningTasks(1);  
	    if(info != null && info.size() > 0){  
	        ComponentName component = info.get(0).topActivity;  
	        if(activityClassName.equals(component.getClassName())){  
	            return true;  
	        }  
	    }  
	    return false;  
	}
	
	/**
	 * 弹出输入的dialog
	 * @param context
	 * @param type
	 * @param title
	 * @param value
	 * @param textView
	 */
	public static void showEditDialog(Context context, int type, String title, String value, final TextView textView){
		LayoutInflater factory = LayoutInflater.from(context);
		final View textEntryView = factory.inflate(R.layout.dialog_common_edit_text, null);
		final EditText input = (EditText) textEntryView.findViewById(R.id.editTextName);
		
		switch(type){
		case Constant.EDIT_DIALOG_TYPE_NORMAL:
			 input.setSingleLine(true);
			break;
		case Constant.EDIT_DIALOG_TYPE_REMARK:
		    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			break;
		case Constant.EDIT_DIALOG_TYPE_NUMBER:
		    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		    input.setSingleLine(true);
			break;
		}
	    input.setBackgroundDrawable(context.getResources().getDrawable(android.R.color.white));
	    input.setText(value);
	    
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
        .setIcon(android.R.drawable.ic_menu_edit)
        .setView(textEntryView)
        .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	textView.setText(input.getText().toString());
             }
        });
        builder.setCancelable(false);
        builder.show();
        showSoftInput(context);
	}
	
	public static void showEditDialog(Context context, int type, String title, String value, final OnEditDialogListener listener){
		LayoutInflater factory = LayoutInflater.from(context);
		final View textEntryView = factory.inflate(R.layout.dialog_common_edit_text, null);
		final EditText input = (EditText) textEntryView.findViewById(R.id.editTextName);
		
		switch(type){
		case Constant.EDIT_DIALOG_TYPE_NORMAL:
			 input.setSingleLine(true);
			break;
		case Constant.EDIT_DIALOG_TYPE_REMARK:
		    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			break;
		case Constant.EDIT_DIALOG_TYPE_NUMBER:
		    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		    input.setSingleLine(true);
			break;
		}
	    input.setBackgroundDrawable(context.getResources().getDrawable(android.R.color.white));
	    input.setText(value);
	    
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
        .setIcon(android.R.drawable.ic_menu_edit)
        .setView(textEntryView);
        
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	listener.onNagative();
            }
        });
        
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	listener.onPositive(input.getText().toString());
            }
        });
        builder.setCancelable(false);
        builder.show();
        showSoftInput(context);
	}
	
}

package com.bjxapp.worker.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class SDCardUtils {
	public static class SDCardNotFoundExcetpion extends Exception{
		private static final long serialVersionUID = -8064867879682883571L;
	}
	
	public static boolean exist() {
        try {
            String state = Environment.getExternalStorageState();
            if (state == null) return false;
            return state.equals(
                    Environment.MEDIA_MOUNTED);
        }catch(Exception e) {
            return false;
        }
	}

	/**
	 * 创建文件夹
	 * 
	 * @param path
	 * @throws IOException
	 * @throws SDCardNotFoundExcetpion
	 */
	public static void makeSureDirExist(String path) throws IOException,
			SDCardNotFoundExcetpion {
		if (exist() == false) {
			throw new SDCardNotFoundExcetpion();
		}
		FileUtils.makeDirs(path);
	}
	
	/**
	 * 获取sdcard上的路径，如 /sdcard/xxx，会自动修改参数前缀为合适的设备路径
	 * @param path
	 * @return
	 */
	public static String getPath(String path) {
		String root = Environment.getExternalStorageDirectory().getPath();
		root = makeSureFilePath(root);
		if(path == null) {
			return root;
		}
		if(path.length() > 1 && path.startsWith(File.separator)) {
			path = path.substring(1);
		}
		if(!path.endsWith(File.separator)) path = path.concat(File.separator);
		return root.concat(path);
	}
	
	/**
	 * 确保以文件路径分隔符结束
	 * @param filePath
	 * @return
	 */
	public static String makeSureFilePath(String filePath) {
		if(filePath == null) return null;
		
		if(filePath.endsWith(File.separator)) {
			return filePath;
		} else {
			return filePath + File.separator;
		}
	}
	
	public static void registerSDCardBroadcast(Context context, BroadcastReceiver receiver) {
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		intentFilter.addDataScheme("file");
		context.registerReceiver(receiver, intentFilter);
	}
	
	/**
	 * Returns whether the device has an external storage device which is
	 * emulated. If true, the device does not have real external storage, and
	 * the directory returned by getExternalStorageDirectory() will be allocated
	 * using a portion of the internal storage system.
	 * 
	 * Certain system services, such as the package manager, use this to
	 * determine where to install an application.
	 * 
	 * since ANDROID_3_0 = 11
	 * @return true 表示应用详情页面的移动到sd卡的按钮为不可见状态。
	 */
	public static boolean isExternalStorageEmulated() {
		if (Env.getSDKLevel() >= Env.ANDROID_3_0) {
			return Environment.isExternalStorageEmulated();
		}
		return false;
		
	}
	
	/**
	 * 应用是否安装在sdcard上
	 * @param context
	 * @return
	 */
	public static boolean isSourceInExternalStorage(Context context) {
		ApplicationInfo info = context.getApplicationContext().getApplicationInfo();
		String dataPath = Environment.getDataDirectory().getPath();
        String systemPath = Environment.getRootDirectory().getPath();
		return !info.publicSourceDir.startsWith(dataPath) && !info.publicSourceDir.startsWith(systemPath);
	}

	/**
	 * 如果不存在，返回null
	 * @param context
	 * @param filePath 不要以/开头
	 * @return
	 */
	public static File getSDCardFile(Context context, String filePath) {
		try {
			String completePath = SDCardUtils.getPath(filePath);
			File file = new File(completePath);
			if(file.exists()) return file;

			String path = "/storage/sdcard0/"+filePath;
			file = new File(path);
			if(file.exists()) return file;

			path = "/storage/sdcard1/"+filePath;
			file = new File(path);
			if(file.exists()) return file;

			return null;

		}catch(Exception e) {
			return null;
		}
	}

}

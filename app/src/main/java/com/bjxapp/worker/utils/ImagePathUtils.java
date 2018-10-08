package com.bjxapp.worker.utils;

import java.io.File;

import android.content.Context;

import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;

public class ImagePathUtils {
	/**
	 * 获取头像的服务器下载地址
	 */
	public static String getUserHeadImagePath(Context context) {
		String userMobile = ConfigManager.getInstance(context).getUserCode();
		String imageUrl = APIConstants.IMAGE_HEAD_DOWNLOAD_URL + File.separator + Constant.UPLOAD_URL_SERVER_DIR_USER + File.separator + userMobile + Constant.UPLOAD_IMAGE_EXT;
		return imageUrl;
	}
	
	/**
	 * 获取用户证件照服务器上传目录
	 */
	public static String getUserIDUploadPath(Context context) {
		String userMobile = ConfigManager.getInstance(context).getUserCode();
		String imageUrl = Constant.UPLOAD_URL_SERVER_DIR_USER_ID + File.separator + userMobile;
		return imageUrl;
	}

}

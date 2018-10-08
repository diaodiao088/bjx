package com.bjxapp.worker.api.impl;

import java.util.Map;
import android.content.Context;

import com.bjxapp.worker.api.APIUtils;
import com.bjxapp.worker.api.IUploadImagesAPI;

/**
 * 上传图片逻辑实现
 * @author jason
 */
public class UploadImagesAPIImpl implements IUploadImagesAPI {
	private static UploadImagesAPIImpl sInstance;
	private Context mContext;
	
	private UploadImagesAPIImpl(Context context) {
		mContext = context.getApplicationContext();
	}
	
	public static IUploadImagesAPI getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new UploadImagesAPIImpl(context);
		}
		
		return sInstance;
	}

	@Override
	public Boolean deleteUploadedImages(String dir, String data) {		
		Map<String, String> params = getParams(mContext);
		params.put("dir", dir);
		params.put("images", data);
		
		/*
		HttpEntity entity = HttpUtils.getHttpEntityByPost(APIConstants.IMAGE_DELETED_URL, params);
		return entity != null;
		*/
		
		return true;
	}

	/**
	 * 组装请求参数
	 * @param context
	 * @return 请求参数键值对
	 */
	private static Map<String, String> getParams(Context context) {
		Map<String, String> params = APIUtils.getBasicParams(context);
		return params;
	}
}

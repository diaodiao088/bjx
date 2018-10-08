package com.bjxapp.worker.api.impl;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.api.APIUtils;
import com.bjxapp.worker.api.IUpdateAPI;
import com.bjxapp.worker.model.UpdateInfo;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.http.HttpUtils;

/**
 * 用户 Http API
 * @author jason
 */
public class UpdateAPIImpl implements IUpdateAPI{
	private static UpdateAPIImpl sInstance;
	private Context mContext;
	
	private UpdateAPIImpl(Context context) {
		mContext = context.getApplicationContext();
	}
	
	public static IUpdateAPI getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new UpdateAPIImpl(context);
		}
		
		return sInstance;
	}
	
	/**
	 * 解析从服务端获取的升级信息json字符串
	 * @param context
	 * @param result
	 * @return 升级信息
	 */
	public UpdateInfo getUpdateInfo() {
		Map<String, String> params = getParams(mContext);
		
		String result = HttpUtils.getStringByGet(APIConstants.UPDATE_INFO_API, params);
		if(!Utils.isNotEmpty(result))
			return null;
		
		try {
			JSONObject json = new JSONObject(result);
			
			int resultCode = 0;
			if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
				resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
				if(resultCode != APIConstants.RESULT_CODE_SUCCESS)
					return null;
			}
			
			if (json.isNull("update_info")) {
				return null;
			}

			JSONObject updateJson = json.getJSONObject("update_info");
			UpdateInfo updateInfo = new UpdateInfo();
			
			if (updateJson.isNull("version"))
				return null;
			else
				updateInfo.setVersion(updateJson.getString("version"));
			
			if (updateJson.isNull("description"))
				updateInfo.setDescription("");
			else
				updateInfo.setDescription(updateJson.getString("description"));
			
			if (updateJson.isNull("url"))
				updateInfo.setUrl("");
			else
				updateInfo.setUrl(updateJson.getString("url"));

			return updateInfo;
			
		}
		catch (JSONException ignore) 
		{
			return null;
		}
	}
	
	/**
	 * 组装请求参数
	 * @param context
	 * @return 请求参数键值对
	 */
	private Map<String, String> getParams(Context context) {
		Map<String, String> params = APIUtils.getBasicParams(context);
		return params;
	}
	
}

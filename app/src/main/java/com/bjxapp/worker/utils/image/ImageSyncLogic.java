package com.bjxapp.worker.utils.image;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.utils.Env;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class ImageSyncLogic{


	private static ImageSyncLogic mImageSyncLogic;
	private Context mContext;

	private ImageSyncLogic(Context context) {
		mContext = context;
	}

	public static ImageSyncLogic getInstance(Context context) {
		if (mImageSyncLogic == null)
			mImageSyncLogic = new ImageSyncLogic(context.getApplicationContext());
		return mImageSyncLogic;
	}
	
	public void broadcastDownloadPictureSuccess(Context context)
	{
		//Intent intent=new Intent();
		//intent.setAction(Constant.ACTION_SYNC_PIC_SUCCESS);
		//以后有需要可以加额外字段
		//intent.addCategory(Constant.APP_CATEGORY);
		//context.sendBroadcast(intent);
	}

	public String constructUrlAppends(String url, String dir) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		String userId = ConfigManager.getInstance(mContext).getUserCode();
		sb.append(url)
				.append("?dir=" + dir)
				.append("&app_ver=" + URLEncoder.encode(Env.getVersionCode(mContext) + "", "UTF-8"))
				.append("&language=" + URLEncoder.encode(Locale.getDefault().toString(),"UTF-8"))
				.append("&platform=" + 0)
				.append("&user_id=" + userId);
		
		return sb.toString();
	}

	public Map<String, Object> parseResult(InputStream inStream) {
		int result = -1;
		String description = "";
		String imgs = "";
		try {
			InputStreamReader isr = new InputStreamReader(new GZIPInputStream(
					inStream));
			BufferedReader reader = new BufferedReader(isr);
			String tempString = null;
			StringBuilder sb = new StringBuilder();
			while ((tempString = reader.readLine()) != null) {
				sb.append(tempString);
				sb.append("\r\n");
			}
			isr.close();
			reader.close();
			String json = sb.toString();
			JSONObject jsObject = new JSONObject(json);
			if (!jsObject.isNull("result_code")) {
				result = jsObject.getInt("result_code");
			}
			if (!jsObject.isNull("description")) {
				description = jsObject.getString("description");
			}
			if (!jsObject.isNull("imgs")) {
				imgs = jsObject.getString("imgs");
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result_code", result);
		map.put("description", description);
		map.put("imgs", imgs);
		return map;
	}

}

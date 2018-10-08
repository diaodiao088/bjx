package com.bjxapp.worker.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.api.APIUtils;
import com.bjxapp.worker.api.IMessageAPI;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.model.Message;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.http.HttpUtils;

/**
 * 用户逻辑实现
 * @author jason
 */
public class MessageAPIImpl implements IMessageAPI {
	private static MessageAPIImpl sInstance;
	private Context mContext;
	
	private MessageAPIImpl(Context context) {
		mContext = context.getApplicationContext();
	}
	
	public static IMessageAPI getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new MessageAPIImpl(context);
		}
		
		return sInstance;
	}

	@Override
	public List<Message> getMessages(int batch) {
		String userCode = ConfigManager.getInstance(mContext).getUserCode();
		String session = ConfigManager.getInstance(mContext).getUserSession();
		
		Map<String, String> params = getParams(mContext);
		params.put("user_code", userCode);
		params.put("session", session);
		params.put("batch", String.valueOf(batch));
		
		String result = HttpUtils.getStringByGet(APIConstants.ACCOUNT_MESSAGE_LIST_API, params);
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
			
			if (json.isNull("messages")) {
				return null;
			}

			JSONArray messagesJsonArray = json.getJSONArray("messages");
			List<Message> messages = new ArrayList<Message>(messagesJsonArray.length());
			for (int i = 0; i < messagesJsonArray.length(); i++) {
				JSONObject messageJson = (JSONObject)messagesJsonArray.get(i);
				
				Message message = new Message();

				if (messageJson.isNull("id")) 
					continue;
				else
					message.setId(messageJson.getInt("id"));
				
				if (messageJson.isNull("date")) 
					message.setDate("");
				else
					message.setDate(messageJson.getString("date"));
				
				if (messageJson.isNull("title")) 
					message.setTitle("");
				else
					message.setTitle(messageJson.getString("title"));
				
				message.setContent("");
				messages.add(message);
			}

			return messages;
			
		}
		catch (JSONException ignore) 
		{
			return null;
		}
	}

	@Override
	public Message getMessageDetail(int messageID) {
		String userCode = ConfigManager.getInstance(mContext).getUserCode();
		String session = ConfigManager.getInstance(mContext).getUserSession();
		
		Map<String, String> params = getParams(mContext);
		params.put("user_code", userCode);
		params.put("session", session);
		params.put("message_id", String.valueOf(messageID));
		
		String result = HttpUtils.getStringByGet(APIConstants.ACCOUNT_MESSAGE_DETAIL_API, params);
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
			
			if (json.isNull("message")) {
				return null;
			}

			JSONObject messageJson = json.getJSONObject("message");
			Message message = new Message();
			
			if (messageJson.isNull("id"))
				return null;
			else
				message.setId(messageJson.getInt("id"));
			
			if (messageJson.isNull("date"))
				message.setDate("");
			else
				message.setDate(messageJson.getString("date"));
			
			if (messageJson.isNull("title"))
				message.setTitle("");
			else
				message.setTitle(messageJson.getString("title"));
			
			if (messageJson.isNull("content"))
				message.setContent("");
			else
				message.setContent(messageJson.getString("content"));

			return message;
			
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
	private static Map<String, String> getParams(Context context) {
		Map<String, String> params = APIUtils.getBasicParams(context);
		return params;
	}
}

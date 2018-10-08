package com.bjxapp.worker.logic.impl;

import java.util.List;

import android.content.Context;

import com.bjxapp.worker.api.APIFactory;
import com.bjxapp.worker.logic.IMessageLogic;
import com.bjxapp.worker.model.Message;

/**
 * 消息逻辑实现
 * @author jason
 */
public class MessageLogicImpl implements IMessageLogic {
	private static MessageLogicImpl sInstance;
	private Context mContext;
	
	private MessageLogicImpl(Context context) {
		mContext = context.getApplicationContext();
	}
	
	public static IMessageLogic getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new MessageLogicImpl(context);
		}
		
		return sInstance;
	}

	@Override
	public List<Message> getMessages(int batch) {
		return APIFactory.getMessageAPI(mContext).getMessages(batch);
	}

	@Override
	public Message getMessageDetail(int messageID) {
		return APIFactory.getMessageAPI(mContext).getMessageDetail(messageID);
	}

}

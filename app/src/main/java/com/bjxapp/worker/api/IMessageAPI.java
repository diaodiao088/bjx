package com.bjxapp.worker.api;

import java.util.List;

import com.bjxapp.worker.model.Message;

/**
 * 消息API
 * @author jason
 */
public interface IMessageAPI {
	/**
	 * 获取消息列表
	 */
	public List<Message> getMessages(int batch);
	
	/**
	 * 获取消息详情
	 */
	public Message getMessageDetail(int messageID);

}

package com.bjxapp.worker.logic;

import java.util.List;

import com.bjxapp.worker.model.Message;

/**
 * 消息功能逻辑
 * @author jason
 */
public interface IMessageLogic {
	/**
	 * 获取消息列表
	 */
	public List<Message> getMessages(int batch);
	
	/**
	 * 获取消息详情
	 */
	public Message getMessageDetail(int messageID);

}

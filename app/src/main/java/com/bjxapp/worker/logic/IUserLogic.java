package com.bjxapp.worker.logic;

import com.bjxapp.worker.model.User;

/**
 * 用户功能逻辑
 * @author jason
 */
public interface IUserLogic {
	/**
	 * 更新用户信息
	 */
	public void updateUser(User user);
	
	/**
	 * 获取用户
	 * @return 用户信息
	 */
	public User getUser();

}

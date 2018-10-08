package com.bjxapp.worker.dao;

import com.bjxapp.worker.model.User;

/**
 * 用户数据存取
 * @author jason
 */
public interface IUserDAO {
	/**
	 * 获取用户
	 * @return 用户
	 */
	public User getUser();
	
	/**
	 * 保存用户
	 * @param 用户
	 */
	public void saveUser(User user);
}

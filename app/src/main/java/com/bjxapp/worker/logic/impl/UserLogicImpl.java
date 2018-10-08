package com.bjxapp.worker.logic.impl;

import android.content.Context;

import com.bjxapp.worker.dao.DAOFactory;
import com.bjxapp.worker.dao.IUserDAO;
import com.bjxapp.worker.logic.IUserLogic;
import com.bjxapp.worker.model.User;

/**
 * 用户逻辑实现
 * @author jason
 */
public class UserLogicImpl implements IUserLogic {
	private static UserLogicImpl sInstance;
	private Context mContext;
	private IUserDAO mUserDAO;
	
	private UserLogicImpl(Context context) {
		mContext = context.getApplicationContext();
		mUserDAO = DAOFactory.getUserDAO(mContext);
	}
	
	public static IUserLogic getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new UserLogicImpl(context);
		}
		
		return sInstance;
	}
	
	@Override
	public void updateUser(User user) {
		mUserDAO.saveUser(user);
	}
	
	@Override
	public User getUser() {
		return mUserDAO.getUser();
	}
}

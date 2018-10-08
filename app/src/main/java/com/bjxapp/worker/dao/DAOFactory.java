package com.bjxapp.worker.dao;

import com.bjxapp.worker.dao.IUserDAO;
import com.bjxapp.worker.dao.impl.UserDAOImpl;

import android.content.Context;
import android.os.Build;

public class DAOFactory {
	
	public static final int SDK_LEVEL = Integer.parseInt(Build.VERSION.SDK);
	
	public static synchronized IUserDAO getUserDAO(Context context) {
		return new UserDAOImpl(context);
	}

}

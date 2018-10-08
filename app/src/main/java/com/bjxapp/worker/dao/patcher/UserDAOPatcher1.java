package com.bjxapp.worker.dao.patcher;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bjxapp.worker.dao.base.BaseDAO;
import com.bjxapp.worker.dao.base.IPatcher;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.model.User;

public class UserDAOPatcher1 implements IPatcher<User> {
	@Override
	public int getSupportMaxVersion() {
		return 1;
	}
	
	@Override
	public void execute(BaseDAO<User> baseDAO, SQLiteDatabase database, Context context) {
		Map<String, String> columns = new HashMap<String, String>();
		columns.put(Constant.COL_USER_PASSWORD, BaseDAO.COL_TYPE_TEXT);
		baseDAO.addColumns(database, columns);
	}
}

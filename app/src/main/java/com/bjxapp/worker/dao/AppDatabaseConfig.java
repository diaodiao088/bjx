package com.bjxapp.worker.dao;

import android.content.Context;

import com.bjxapp.worker.dao.base.DatabaseConfig;
import com.bjxapp.worker.dao.base.SQLiteManager.SQLiteTable;
import com.bjxapp.worker.dao.impl.UserDAOImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * MoShop数据库配置类
 * 
 * @author DuShengjun<dushengjun@gmail.com>
 * 
 */
public final class AppDatabaseConfig implements DatabaseConfig {
	// 数据库名
	private static final String DB_NAME = "xapp.db";

    /**
     * 注意：此处打补丁的时候，补丁的maxSupport=oldVersion
     * 即如果数据库从1升级到2那么补丁的maxSupport=1.
     */
    public static final int DB_VERSION = 2;

	private static AppDatabaseConfig instance;

	public static AppDatabaseConfig getInstance() {
		if (instance == null) {
			instance = new AppDatabaseConfig();
		}
		return instance;
	}

	@Override
	public String getDatabaseName() {
		return DB_NAME;
	}

	@Override
	public int getDatabaseVersion() {
		return DB_VERSION;
	}

	@Override
	public List<Class<? extends SQLiteTable>> getTables(Context context) {
		List<Class<? extends SQLiteTable>> ret = new ArrayList<Class<? extends SQLiteTable>>();
		
		ret.add(UserDAOImpl.class);
		
		return ret;
	}
}

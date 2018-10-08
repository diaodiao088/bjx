package com.bjxapp.worker.dao.base;

import android.content.Context;

import com.bjxapp.worker.dao.base.SQLiteManager.SQLiteTable;

import java.util.List;

public interface DatabaseConfig {
	/**
	 * 获取数据库名
	 * @author dushengjun
	 * @return
	 */
	public String getDatabaseName();
	/**
	 * 获取数据库版本号
	 * @author dushengjun
	 * @return
	 */
	public int getDatabaseVersion();
	/**
	 * 获取数据库库中所有表的类
	 * @author dushengjun
	 * @return
	 */
	public List<Class<? extends SQLiteTable>> getTables(Context context);
}

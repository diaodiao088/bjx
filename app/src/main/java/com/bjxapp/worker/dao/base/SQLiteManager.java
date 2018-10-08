package com.bjxapp.worker.dao.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bjxapp.worker.utils.Logger;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * sqlite数据库管理类，负责表的初始化和表的更
 * 
 * @author Administrator
 * 
 */
public class SQLiteManager extends SQLiteOpenHelper {
	private static volatile SQLiteManager instance;
	private Context mContext;

	private static DatabaseConfig mConfig;
	private static volatile SQLiteDatabase db;

	public SQLiteManager(Context context, String name, int version) {
		super(context, name, null, version);
		mContext = context;
	}

	public static synchronized SQLiteDatabase getDB(Context context,
			DatabaseConfig config) {
		if (db == null) {
			mConfig = config;
			if (instance == null) {
				instance = new SQLiteManager(context, config.getDatabaseName(),
						config.getDatabaseVersion());
			}
			db = instance.getWritableDatabase();
		}
		return db;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		List<Class<? extends SQLiteTable>> classes = mConfig.getTables(mContext);
		for (Class<? extends SQLiteTable> clazz : classes) {
			try {
				Constructor<? extends SQLiteTable> con = clazz
						.getConstructor(Context.class);
				SQLiteTable table = con.newInstance(mContext);
				table.onCreate(db);
			} catch (Exception e) {
				Logger.e("SQLiteManager.onCreate",
						"create table  " + clazz.getName(), e);
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		List<Class<? extends SQLiteTable>> classes = mConfig.getTables(mContext);
		for (Class<? extends SQLiteTable> clazz : classes) {
			try {
                Logger.i("now onUpgrade Sqlite!");
				Constructor<? extends SQLiteTable> con = clazz
						.getConstructor(Context.class);
				SQLiteTable table = con.newInstance(mContext);
				table.onUpdate(db, oldVersion, newVersion);
			} catch (Exception e) {
				Logger.e("SQLiteManager.onUpdate",
						"update table " + clazz.getName(), e);
			}
		}
	}

	/**
	 * DAO表接口
	 * 
	 * @author DuShengjun<dushengjun@gmail.com>
	 */
	public interface SQLiteTable {
		public final static String COL_TYPE_AUTO_ID = "INTEGER PRIMARY KEY";
		public final static String COL_TYPE_FLOAT = "FLOAT";
		public final static String COL_TYPE_TEXT = "TEXT";
		public final static String COL_TYPE_INT = "INT";
		public final static String COL_TYPE_LONG = "LONG";

		public void onCreate(SQLiteDatabase database);

		public void onUpdate(SQLiteDatabase database, int oldVersion,
                             int newVersion);
	}

}

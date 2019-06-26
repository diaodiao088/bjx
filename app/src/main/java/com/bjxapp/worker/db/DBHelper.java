package com.bjxapp.worker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhangdan on 2018/11/9.
 * <p>
 * comments:
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bjx.db";
    private static final int DATABASE_VERSION = 3;

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS bjx_new" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " content VARCHAR, " +
                "createTime VARCHAR, " +
                "title VARCHAR , " +
                "type INTEGER , " +
                "orderId VARCHAR , " +
                "noticeId VARCHAR ," +
                "read INTEGER)");

        db.execSQL("CREATE TABLE IF NOT EXISTS device_info" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " realId VARCHAR, " +
                " situation VARCHAR," +
                "createTime VARCHAR, " +
                "title VARCHAR , " +
                "type INTEGER , " +
                "orderId VARCHAR , " +
                "noticeId VARCHAR ," +
                "read INTEGER)");
    }

    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("CREATE TABLE IF NOT EXISTS bjx_new" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " content VARCHAR, " +
                "createTime VARCHAR, " +
                "title VARCHAR , " +
                "type INTEGER , " +
                "orderId VARCHAR , " +
                "noticeId VARCHAR ," +
                "read INTEGER)");

        db.execSQL("CREATE TABLE IF NOT EXISTS bjx_new" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " content VARCHAR, " +
                "createTime VARCHAR, " +
                "title VARCHAR , " +
                "type INTEGER , " +
                "orderId VARCHAR , " +
                "noticeId VARCHAR ," +
                "read INTEGER)");

    }


}

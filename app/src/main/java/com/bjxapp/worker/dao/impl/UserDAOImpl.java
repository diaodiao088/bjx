package com.bjxapp.worker.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bjxapp.worker.dao.AppDatabaseConfig;
import com.bjxapp.worker.dao.IUserDAO;
import com.bjxapp.worker.dao.base.BaseDAO;
import com.bjxapp.worker.dao.patcher.UserDAOPatcher1;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.model.User;
import com.bjxapp.worker.utils.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户数据存取实现
 *
 * @author jason
 */
public class UserDAOImpl extends BaseDAO<User> implements IUserDAO {
    public static final String TABLE_NAME = "user";

    public UserDAOImpl(Context context) {
        super(TABLE_NAME, context, AppDatabaseConfig.getInstance());
        this.registerPatcher(UserDAOPatcher1.class);
    }

    @Override
    public User getUser() {
        Cursor cursor = getDatabase().query(TABLE_NAME, ALL_COLS, null, null, null, null, null, "1");

        List<User> userList= findListByCursor(cursor);
        if(userList != null && userList.size() > 0){
        	return userList.get(0);
        }
        else{
        	return null;
        }
    }

    @Override
    public void saveUser(User user) {
        
    	getDatabase().beginTransaction();

        try {
            deleteUserById(user.getId());
            saveOperation(user, getDatabase());
            getDatabase().setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e.toString());
        } finally {
            getDatabase().endTransaction();
        }
    }

    public ContentValues getContentValues(User user) {
        ContentValues cv = new ContentValues();
        cv.put(Constant.COL_USER_ID, user.getId());
        cv.put(Constant.COL_USER_NAME, user.getName());
        cv.put(Constant.COL_USER_PASSWORD, user.getPassword());
        return cv;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Map<String, String> columns = new HashMap<String, String>();
        columns.put(Constant.COL_USER_ID, COL_TYPE_INT);
        columns.put(Constant.COL_USER_NAME, COL_TYPE_TEXT);
        columns.put(Constant.COL_USER_PASSWORD, COL_TYPE_TEXT);
        createTable(database, columns);
    }

    private void saveOperation(User user, SQLiteDatabase db) {
        db.insert(getTableName(), null, getContentValues(user));
    }

    @Override
    protected User findByCursor(Cursor cursor, int i) {
        User user = new User();
        int idx = cursor.getColumnIndex(Constant.COL_USER_ID);
        user.setId(cursor.getInt(idx));
        idx = cursor.getColumnIndex(Constant.COL_USER_NAME);
        user.setName(cursor.getString(idx));
        idx = cursor.getColumnIndex(Constant.COL_USER_PASSWORD);
        user.setPassword(cursor.getString(idx));
        
        return user;
    }

    private void deleteUserById(int userId) {
        getDatabase().delete(TABLE_NAME, Constant.COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)});
    }
}

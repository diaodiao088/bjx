package com.bjxapp.worker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by zhangdan on 2018/11/9.
 * <p>
 * comments:
 */

public class DBManager {

    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }


    /**
     * 添加订单消息
     */
    public void add(BjxInfo info) {
        db.beginTransaction();    //开始事务
        try {
            db.execSQL("INSERT INTO bjx ( content , createTime , title , type ,read ) VALUES (? , ? , ? , ? , ?)", new Object[]{info.getContent(), info.getCreateTime(),
                    info.getTitle(), info.getType(), 0});
            db.setTransactionSuccessful();    //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }


}

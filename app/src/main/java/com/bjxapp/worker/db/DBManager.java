package com.bjxapp.worker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bjxapp.worker.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

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


    public void addDeviceInfo(String realId, String situation, String needMaintain,
                              String remark, String imgUrls, String score, String scoreId) {

        db.beginTransaction();

        try {
            db.execSQL("INSERT INTO device_info ( realId , situation , needMaintain , remark ,imgUrls , scroce ,scoreId) VALUES (? , ? , ? , ? , ? , ? , ?)",
                    new Object[]{realId, situation,
                            needMaintain, remark, imgUrls, score, scoreId});
            db.setTransactionSuccessful();    //设置事务成功完成

            LogUtils.log("add to db success ");
        } catch (Exception e) {

            LogUtils.log("add to db fail : " + e.getLocalizedMessage());
        } finally {
            db.endTransaction();    //结束事务
        }

    }


    /**
     * 添加订单消息
     */
    public void add(BjxInfo info) {

        LogUtils.log("start to add to db " + info.toString());

        db.beginTransaction();    //开始事务
        try {
            db.execSQL("INSERT INTO bjx_new ( content , createTime , title , type ,read , orderId , noticeId ) VALUES (? , ? , ? , ? , ? , ? , ?)", new Object[]{info.getContent(), info.getCreateTime(),
                    info.getTitle(), info.getType(), 0, info.getOrderId(), info.getNoticeId()});
            db.setTransactionSuccessful();    //设置事务成功完成

            LogUtils.log("add to db success ");
        } catch (Exception e) {

            LogUtils.log("add to db fail : " + e.getLocalizedMessage());
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public List<BjxInfo> queryBill(int limit, int offset) {
        ArrayList<BjxInfo> list = new ArrayList<BjxInfo>();
        Cursor c = queryBillCursor(limit, offset);
        while (c.moveToNext()) {
            BjxInfo item = new BjxInfo();
            item.setType(c.getInt(c.getColumnIndex("type")));
            item.setContent(c.getString(c.getColumnIndex("content")));
            item.setTitle(c.getString(c.getColumnIndex("title")));
            item.setCreateTime(c.getString(c.getColumnIndex("createTime")));
            int isRead = c.getInt(c.getColumnIndex("read"));
            item.setOrderId(c.getString(c.getColumnIndex("orderId")));
            item.setNoticeId(c.getString(c.getColumnIndex("noticeId")));
            item.setId(c.getInt(c.getColumnIndex("_id")));
            item.setRead(isRead == 1);
            list.add(item);
        }
        c.close();
        return list;
    }


    public List<BjxInfo> query(int limit, int offset) {
        ArrayList<BjxInfo> list = new ArrayList<BjxInfo>();
        Cursor c = queryTheCursor(limit, offset);
        while (c.moveToNext()) {
            BjxInfo item = new BjxInfo();
            item.setType(c.getInt(c.getColumnIndex("type")));
            item.setContent(c.getString(c.getColumnIndex("content")));
            item.setTitle(c.getString(c.getColumnIndex("title")));
            item.setCreateTime(c.getString(c.getColumnIndex("createTime")));
            int isRead = c.getInt(c.getColumnIndex("read"));
            item.setId(c.getInt(c.getColumnIndex("_id")));
            item.setOrderId(c.getString(c.getColumnIndex("orderId")));
            item.setNoticeId(c.getString(c.getColumnIndex("noticeId")));
            item.setRead(isRead == 1);
            list.add(item);
        }
        c.close();
        return list;
    }


    public void updateAsRead(int id) {

       /* Cursor c = null;
        try {
            String updateStr = "UPDATE bjx SET read = 1 where _id > 0";
            c = db.rawQuery(updateStr, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (Exception e) {
            }
        }*/

        try {
            ContentValues cv = new ContentValues();
            cv.put("read", 1);
            db.update("bjx_new", cv, "_id = " + id, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }


    public long getAllRedotNum() {
        String sql = "select count(*) from bjx_new where read = 0";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        return count;
    }


    public long getCompanyRedotNum() {
        String sql = "select count(*) from bjx_new where read = 0 and type=70";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        return count;
    }

    public long getBillRedotNum() {
        String sql = "select count(*) from bjx_new where read = 0 and type not in (70)";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        return count;
    }


    /**
     * query all persons, return cursor
     *
     * @return Cursor
     */
    public Cursor queryTheCursor(int limit, int offset) {
        Cursor c = db.rawQuery("SELECT * FROM bjx_new where type=70 order by _id desc limit " + limit + " offset " + offset, null);
        return c;
    }

    /**
     * query all persons, return cursor
     *
     * @return Cursor
     */
    public Cursor queryBillCursor(int limit, int offset) {
        Cursor c = db.rawQuery("SELECT * FROM bjx_new where type not in (70) order by _id desc limit " + limit + " offset " + offset, null);
        return c;
    }

}

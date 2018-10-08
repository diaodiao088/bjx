package com.bjxapp.worker.http.httpcore.model;

import com.google.gson.annotations.SerializedName;


/**
 * Created by general on 14/09/2017.
 */

public class Result<T> {

    public static final int STATUS_OK = 100;

    @SerializedName(value = "status")
    public int code = Integer.MIN_VALUE;

    @SerializedName(value = "msg")
    public String message;

    @SerializedName(value = "timestamp")
    public long stime;

    @SerializedName(value = "data")
    public T data;


    /**
     * 是否来自缓存
     */
    public boolean fromCache = false;

    /**
     * 是否是标准的JSON格式
     * @return
     */
    public boolean isNormalJsonFormat() {
        return code != Integer.MIN_VALUE && stime > 0;
    }

    public boolean isStatusOk() {
        return code == STATUS_OK;
    }
}

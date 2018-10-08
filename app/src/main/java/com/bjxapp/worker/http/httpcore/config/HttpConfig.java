package com.bjxapp.worker.http.httpcore.config;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

/**
 * 网络请求相关的常量类
 * @since 2017.09.18 11:10
 * @author renwenjie
 * @version 1.0
 */

public class HttpConfig {

    public static final String TAG = "khttp";

    public static final String BASE_URL_HTTP = "http://10.60.109.200:8888";

    public static final String BASE_URL_HTTPS = "https://api-keyboard.cmcm.com/";

    public static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS; //时间单位

    public static final long MAX_READ_TIMEOUT = 60 * 1000; // max read time

    public static final long MAX_WRITE_TIMEOUT = 60 * 1000; // max write time

    public static final long MAX_CONNECTION_TIMEOUT = 60 * 1000; // max connection time

    public static final int MAX_RETRY_COUNT = 5; //最大重试次数

    public static final String CACHE_HEADER_VALUE_HOLDER = "cache_holder";

    public static final int HTTP_RESP_OK = 1;
    public static final int HTTP_RESP_ERROR = 0;
    public static final int HTTP_RESP_ERROR_0 = 2000;
    public static final int HTTP_RESP_ERROR_1 = 2001; //请求方法错误
    public static final int HTTP_RESP_ERROR_2 = 2002; //不合格版本号
    public static final int HTTP_RESP_ERROR_3 = 2003;
    public static final int HTTP_RESP_ERROR_4 = 2004;
    public static final int HTTP_RESP_ERROR_5 = 2005;
    public static final int HTTP_RESP_ERROR_6 = 2006;
    public static final int HTTP_RESP_ERROR_7 = 2007;
    public static final int HTTP_RESP_ERROR_8 = 2008;
    public static final int HTTP_RESP_ERROR_9 = 2009;

    @IntDef({
            HTTP_RESP_OK,
            HTTP_RESP_ERROR,
            HTTP_RESP_ERROR_0,
            HTTP_RESP_ERROR_1,
            HTTP_RESP_ERROR_2,
            HTTP_RESP_ERROR_3,
            HTTP_RESP_ERROR_4,
            HTTP_RESP_ERROR_5,
            HTTP_RESP_ERROR_6,
            HTTP_RESP_ERROR_7,
            HTTP_RESP_ERROR_8,
            HTTP_RESP_ERROR_9})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HttpStatus { }

    public static final String DOWNLOAD_TEMP_POSTFIX = ".tmp";

    public static final int DOWNLOAD_ERROR_CODE = 100;

    public static final long DEFAULT_MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; //默认最大缓存容量 50MB

    public static final int DEFAULT_CACHE_TIMEOUT = 10 * 60 * 60; //默认最大缓存时间10小时

}

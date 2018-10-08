package com.bjxapp.worker.http.keyboard.commonutils.job;

/**
 * Created by kanyingkuang on 2018/1/16.
 */
public class HttpThreadUtils extends BaseThreadUtils {
    private static final String THREAD_NAME_PREFIX = "HttpThreadUtils-";
    private static final int HttpThread_MAXIMUM_AUTOMATIC_THREAD_COUNT = 3;

    private static final class LazyHolder {
        private static final HttpThreadUtils INSTANCE = new HttpThreadUtils();
    }

    public static HttpThreadUtils getInstance() {
        return LazyHolder.INSTANCE;
    }

    protected HttpThreadUtils() {
        super(THREAD_NAME_PREFIX, HttpThread_MAXIMUM_AUTOMATIC_THREAD_COUNT,
                HttpThread_MAXIMUM_AUTOMATIC_THREAD_COUNT);
    }
}

package com.bjxapp.worker.http.keyboard.commonutils.job;

/**
 * Created by general on 09/11/2017.
 */

public final class ThreadUtils extends BaseThreadUtils {
    private static final String THREAD_NAME_PREFIX = "ThreadUtils-";
    private static final int MAXIMUM_AUTOMATIC_THREAD_COUNT = 3;

    private static final class LazyHolder {
        private static final ThreadUtils INSTANCE = new ThreadUtils();
    }

    public static ThreadUtils getInstance() {
        return LazyHolder.INSTANCE;
    }

    private ThreadUtils() {
        super(THREAD_NAME_PREFIX, MAXIMUM_AUTOMATIC_THREAD_COUNT, MAXIMUM_AUTOMATIC_THREAD_COUNT);
    }

}

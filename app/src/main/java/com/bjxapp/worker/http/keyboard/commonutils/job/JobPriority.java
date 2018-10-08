package com.bjxapp.worker.http.keyboard.commonutils.job;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by general on 09/11/2017.
 * @author renwenjie
 * @date 2017/11/09
 */

public class JobPriority {

    public static final int JOB_PRIORITY_LOW = 1 << 0;
    public static final int JOB_PRIORITY_NORMAL = 1 << 1;
    public static final int JOB_PRIORITY_HIGH = 1 << 2;
    public static final int JOB_PRIORITY_IMMEDIATE = 1 << 3;

    @IntDef({
            JOB_PRIORITY_LOW,
            JOB_PRIORITY_NORMAL,
            JOB_PRIORITY_HIGH,
            JOB_PRIORITY_IMMEDIATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface JobPriorityAnnotation { }
}

package com.bjxapp.worker.http.keyboard.commonutils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public final class Constants {


    public static final int TYPE_GOOGLE = 1;
    public static final int TYPE_FACEBOOK = 2;
    public static final int TYPE_TWITTER = 3;
    public static final int TYPE_NONE = -1;
    @IntDef({TYPE_GOOGLE, TYPE_FACEBOOK, TYPE_TWITTER , TYPE_NONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LOGIN_TYPE {}
}

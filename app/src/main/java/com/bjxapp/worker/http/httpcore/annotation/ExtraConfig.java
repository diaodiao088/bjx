package com.bjxapp.worker.http.httpcore.annotation;


import com.bjxapp.worker.http.httpcore.config.HttpConfig;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 用于定义API接口额外的配置
 * @since 2017.09.18 11:10
 * @author renwenjie
 * @version 1.0
 */

@Retention(RUNTIME)
@Target(METHOD)
public @interface ExtraConfig {

    /**
     * 是否需要重试
     * @return
     */
    boolean isRetry() default false;

    /**
     * 重试次数
     */
    int maxRetryCount() default 0;

    /**
     * 最大读取超时时间
     * @return
     */
    long maxReadTimeOut() default HttpConfig.MAX_READ_TIMEOUT;

    /**
     * 最大写超时间
     * @return
     */
    long maxWriteTimeOut() default HttpConfig.MAX_WRITE_TIMEOUT;

    /**
     * 最大连接超时时间
     * @return
     */
    long maxConnectTimeOut() default HttpConfig.MAX_CONNECTION_TIMEOUT;
}

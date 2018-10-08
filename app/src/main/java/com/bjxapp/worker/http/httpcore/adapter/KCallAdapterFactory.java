package com.bjxapp.worker.http.httpcore.adapter;

import android.support.annotation.Nullable;

import com.bjxapp.worker.http.httpcore.annotation.ExtraConfig;
import com.bjxapp.worker.http.httpcore.config.HttpConfig;
import com.bjxapp.worker.http.httpcore.interceptor.RetryInterceptor;
import com.bjxapp.worker.http.httpcore.utils.ObjectSupplier;
import com.bjxapp.worker.http.keyboard.commonutils.ReflectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;


/**
 * 处理所有API的个性化请求
 *
 * @since 2017.09.167 18:00
 * @author renwenjie
 * @version 1.0
 */

public class KCallAdapterFactory extends CallAdapter.Factory {

    private static final KCallAdapterFactory INSTANCE = new KCallAdapterFactory();

    public static KCallAdapterFactory create() {
        return INSTANCE;
    }

    @Nullable
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Class clazz = getRawType(returnType);
        if (clazz == Call.class || clazz == Observable.class) {
            if (annotations == null || annotations.length == 0) {
                return retrofit.nextCallAdapter(this, returnType, annotations);
            }
            ExtraConfig extraConfig = null;
            for (Annotation annotation : annotations) {
                if (annotation instanceof ExtraConfig) {
                    extraConfig = (ExtraConfig) annotation;
                    break;
                }
            }
            if (extraConfig == null) {
                return retrofit.nextCallAdapter(this, returnType, annotations);
            }
            boolean isRetry = extraConfig.isRetry();
            int maxRetryCount = extraConfig.maxRetryCount();
            long maxReadTimeOut = extraConfig.maxReadTimeOut();
            long maxWriteTimeOut = extraConfig.maxWriteTimeOut();
            long maxConnectTimeOut = extraConfig.maxConnectTimeOut();
            OkHttpClient newOkHttpClient = null;
            //处理最大超时时间
            if (maxReadTimeOut != HttpConfig.MAX_READ_TIMEOUT
                    || maxWriteTimeOut != HttpConfig.MAX_WRITE_TIMEOUT
                    || maxConnectTimeOut != HttpConfig.MAX_CONNECTION_TIMEOUT) {
                OkHttpClient.Builder okHttpBuilder = ObjectSupplier.okHttpClient().newBuilder();
                okHttpBuilder.readTimeout(Math.min(maxReadTimeOut > 0 ? maxReadTimeOut : HttpConfig.MAX_READ_TIMEOUT, maxConnectTimeOut), HttpConfig.TIME_UNIT);
                okHttpBuilder.writeTimeout(Math.min(maxWriteTimeOut > 0 ? maxWriteTimeOut : HttpConfig.MAX_WRITE_TIMEOUT, maxConnectTimeOut), HttpConfig.TIME_UNIT);
                okHttpBuilder.connectTimeout(maxConnectTimeOut > 0 ? maxConnectTimeOut : HttpConfig.MAX_CONNECTION_TIMEOUT, HttpConfig.TIME_UNIT);
                newOkHttpClient = okHttpBuilder.build();
            }
            //处理重试
            if (isRetry || maxRetryCount > 0) {
                OkHttpClient.Builder okHttpBuilder;
                if (newOkHttpClient != null) {
                    okHttpBuilder = newOkHttpClient.newBuilder();
                } else {
                    okHttpBuilder = ObjectSupplier.okHttpClient().newBuilder();
                }
                okHttpBuilder.retryOnConnectionFailure(true);

                if (maxRetryCount <= 0) {
                    maxRetryCount = HttpConfig.MAX_RETRY_COUNT;
                }
                okHttpBuilder.addInterceptor(new RetryInterceptor(maxRetryCount));
                newOkHttpClient = okHttpBuilder.build();
            }
            if (newOkHttpClient != null) {
                updateDelegate(retrofit, newOkHttpClient);
            }
            return retrofit.nextCallAdapter(this, returnType, annotations);
        }
        return null;
    }


    private void updateDelegate(Retrofit retrofit, okhttp3.Call.Factory delegate) {
        try {
            ReflectUtil.fieldSet(retrofit, "callFactory", delegate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

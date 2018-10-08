package com.bjxapp.worker.http.httpcore.converter;

import android.support.annotation.Nullable;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;


/**
 * File类型的文件转换工厂，便于将此类实例配置到Retrofit的对象中
 * @since 2017.09.18 11:10
 * @author renwenjie
 * @version 1.0
 */

public class KFileModelConvertorFactory extends Converter.Factory {

    private static final KFileModelConvertorFactory INSTANCE = new KFileModelConvertorFactory();

    public static KFileModelConvertorFactory create() {
        return INSTANCE;
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        Class clazz = getRawType(type);
        if (clazz == File.class) {
            if (annotations == null || annotations.length <= 0) {
                return null;
            }
            return new KFileModelConverter();
        }
        return null;
    }

    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }
}

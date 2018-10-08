package com.bjxapp.worker.http.httpcore.converter;

import android.support.annotation.Nullable;


import com.bjxapp.worker.http.httpcore.model.Result;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by general on 15/09/2017.
 */

public class KResultModelConverterFactory extends Converter.Factory {

    static final KResultModelConverterFactory INSTANCE = new KResultModelConverterFactory();

    public static final KResultModelConverterFactory create() {
        return INSTANCE;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        Type rawType = getRawType(type);
        if (rawType == Result.class) {
            return new KResultModelConverter(type);
        }
        return null;
    }

    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }
}

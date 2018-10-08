package com.bjxapp.worker.http.httpcore.converter;


import android.text.TextUtils;

import com.bjxapp.worker.http.httpcore.body.ResponseBodyProxy;
import com.bjxapp.worker.http.httpcore.model.Result;
import com.bjxapp.worker.http.httpcore.utils.GsonUtil;
import com.bjxapp.worker.http.keyboard.commonutils.Preconditions;
import com.bjxapp.worker.http.keyboard.commonutils.ReflectUtil;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 公共响应类转换器：用于将正常的JSON格式转成Result<T>,非正常JSON格式也可以转换到Result类中的data对象
 * 正常的JSON格式：
 * {
    "ret": 1,
    "msg": "OK",
    "stime": 1505876563,
    "data":{} /"data":[],
    "pagination":{
            "hasMore": 1,
            "count": 12,
            "offset": 12,
            "total": 18
                }
   }
 *
 * @since 2017.09.19 24:00
 * @author renwenjie
 * @version 1.0
 */

public class KResultModelConverter<T> implements Converter<ResponseBody, Result<T>> {

    private Type mType;

    public KResultModelConverter(Type type) {
        this.mType = type;
    }

    @Override
    public Result<T> convert(ResponseBody value) throws IOException {
        value = Preconditions.checkNotNull(value);
        mType = Preconditions.checkNotNull(mType);
        String jsonBody = value.string();
        if (TextUtils.isEmpty(jsonBody)) {
            return new Result<>();
        }
        Result<T> result = null;
        Class<?> classT = null;
        try {
            boolean isArrayOrList = false;
            boolean isMap = false;
            Type tempType = mType;
            do {
                if (tempType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) tempType;
                    Type type = ReflectUtil.getParameterUpperBound(0, parameterizedType);
                    Class clazz = ReflectUtil.getRawType(type);
                    if (List.class.isAssignableFrom(clazz)) {
                        tempType = type;
                        isArrayOrList = true;
                        continue;
                    } else if (Map.class.isAssignableFrom(clazz)) {
                        isMap = true;
                        classT = ReflectUtil.getRawType(ReflectUtil.getParameterUpperBound(1, (ParameterizedType) type));
                        break;
                    } else {
                        classT = clazz;
                        break;
                    }
                } else {
                    break;
                }
            } while (true);

            if (classT == null) {
                classT = ReflectUtil.getRawType(ReflectUtil.getParameterUpperBound(0, (ParameterizedType) mType));
            }
            if (isArrayOrList) {
                result = (Result<T>) GsonUtil.fromJsonArray(jsonBody, classT);
            } else if (isMap) {
                result = (Result<T>) GsonUtil.fromHashMap(jsonBody, classT);
            } else {
                result = (Result<T>) GsonUtil.fromJsonObject(jsonBody, classT);
            }

            try {
                Object object = ReflectUtil.fieldGet(value, "delegate");
                if (object instanceof ResponseBodyProxy) {
                    ResponseBodyProxy proxy = (ResponseBodyProxy) object;
                   // KLog.d(HttpConfig.TAG, "---isFromCache--:" + proxy.isFromCache());
                    result.fromCache = proxy.isFromCache();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } finally {
            if (result == null) {
                result = new Result<T>();
            }
        }
        return result;
    }
}

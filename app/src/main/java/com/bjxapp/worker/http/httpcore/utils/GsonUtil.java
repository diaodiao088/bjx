package com.bjxapp.worker.http.httpcore.utils;

import android.text.TextUtils;

import com.bjxapp.worker.http.httpcore.model.Result;
import com.bjxapp.worker.http.httpcore.supplier.Supplier;
import com.bjxapp.worker.http.httpcore.supplier.Suppliers;
import com.bjxapp.worker.http.httpcore.utils.type.TypeBuilder;
import com.bjxapp.worker.http.keyboard.commonutils.Preconditions;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by general on 14/09/2017.
 */

public class GsonUtil {

    private static Supplier<Gson> gsonSupplier = Suppliers.memoize(new Supplier<Gson>() {
        @Override
        public Gson get() {
            return new Gson();
        }
    });

    public static <T> Result<List<T>> fromJsonArray(JsonReader reader, Class<T> clazz) {
        Preconditions.checkNotNull(reader);
        Preconditions.checkNotNull(clazz);
        Type type = TypeBuilder
                .newInstance(Result.class)
                .beginSubType(List.class)
                .addTypeParam(clazz)
                .endSubType()
                .build();
        return gsonSupplier.get().fromJson(reader, type);
    }

    public static <T> Result<T> fromJsonObject(String json, Class<T> clazz) {
        Preconditions.checkNotNull(json);
        Preconditions.checkNotNull(clazz);
        Type type = TypeBuilder
                .newInstance(Result.class)
                .addTypeParam(clazz)
                .build();
        Gson gson = gsonSupplier.get();
        Result<T> result =  gson.fromJson(json, type);
        if (result.data == null || result.code == Integer.MIN_VALUE  || TextUtils.isEmpty(result.message)) {
            result.data = gson.fromJson(json, clazz);
        }
        return result;
    }

    public static <T> Result<List<T>> fromJsonArray(String json, Class<T> clazz) {
        Preconditions.checkNotNull(json);
        Preconditions.checkNotNull(clazz);
        Type type = TypeBuilder
                .newInstance(Result.class)
                .beginSubType(List.class)
                .addTypeParam(clazz)
                .endSubType()
                .build();
        return gsonSupplier.get().fromJson(json, type);
    }

    public static <T> Result<Map<String, T>> fromHashMap(String json, Class<T> clazz) {
        Preconditions.checkNotNull(json);
        Preconditions.checkNotNull(clazz);
        Type type = TypeBuilder
                .newInstance(Result.class)
                .beginSubType(HashMap.class)
                .addTypeParam(String.class)
                .addTypeParam(clazz)
                .endSubType()
                .build();
        return gsonSupplier.get().fromJson(json, type);
    }
}

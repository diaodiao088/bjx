package com.bjxapp.worker.apinew;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by zhangdan on 2018/11/2.
 * <p>
 * comments:
 */

public interface RegisterApi {

    @POST("/region/list")
    Call<JsonObject> getCity();

    @FormUrlEncoded
    @POST("/serviceItem/list")
    Call<JsonObject> getProject(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/register/do")
    Call<JsonObject> getRegister(@FieldMap Map<String, String> params);

}

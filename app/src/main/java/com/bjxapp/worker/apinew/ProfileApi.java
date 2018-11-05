package com.bjxapp.worker.apinew;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by zhangdan on 2018/11/4.
 * <p>
 * comments:
 */
public interface ProfileApi {

    @FormUrlEncoded
    @POST("/profile/mine")
    Call<JsonObject> getProfileDetail(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/withdrawal/bindBank")
    Call<JsonObject> bindBank(@FieldMap Map<String, String> params);



}

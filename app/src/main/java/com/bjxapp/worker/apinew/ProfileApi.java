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

    @FormUrlEncoded
    @POST("/bankInfo/get")
    Call<JsonObject> getBankInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/withdrawal/apply")
    Call<JsonObject> applyCash(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/config/guaranteePeriod")
    Call<JsonObject> getGuarPeriod(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/config/withdrawalDay")
    Call<JsonObject> getWithDrawDay(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/withdrawal/list")
    Call<JsonObject> getWithDrawHistory(@FieldMap Map<String , String> params);

    @FormUrlEncoded
    @POST("/register/info")
    Call<JsonObject> getRegisterInfo(@FieldMap Map<String , String> params);

}

package com.bjxapp.worker.apinew;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by zhangdan on 2018/11/6.
 * <p>
 * comments:
 */

public interface EnterpriseApi {

    @FormUrlEncoded
    @POST("/equipmentComponent/list")
    Call<JsonObject> getComponentList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/dict/info")
    Call<JsonObject> getDicList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/order/saveMaintainPlan")
    Call<JsonObject> saveMainTain(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/order/completeMaintainPlan")
    Call<JsonObject> completePlan(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/order/commentMaintainPlan")
    Call<JsonObject> commentPlan(@FieldMap Map<String, String> params);



}

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

public interface BillApi {

    @FormUrlEncoded
    @POST("/profile/status")
    Call<JsonObject> getServiceStatus(@FieldMap Map<String ,String> params);

    @FormUrlEncoded
    @POST("/profile/serviceState/0")
    Call<JsonObject> denyBill(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/profile/serviceState/1")
    Call<JsonObject> receiveBill(@FieldMap Map<String, String> params);

}

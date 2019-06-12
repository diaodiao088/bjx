package com.bjxapp.worker.apinew;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by zhangdan on 2018/11/6.
 * <p>
 * comments:
 */

public interface BillApi {

    @FormUrlEncoded
    @POST("/profile/status")
    Call<JsonObject> getServiceStatus(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/profile/serviceState/0")
    Call<JsonObject> denyBill(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/profile/serviceState/1")
    Call<JsonObject> receiveBill(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/order/list/0")
    Call<JsonObject> getOrderList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/order/list/1")
    Call<JsonObject> getCompleteList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/order/receive")
    Call<JsonObject> acceptOrder(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/order/noContact")
    Call<JsonObject> sendMessage(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/order/changeAppointment")
    Call<JsonObject> changeTime(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/order/confirmAppointment")
    Call<JsonObject> confirmAppoinment(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/profile/bindPushService")
    Call<JsonObject> bindPush(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/order/prepay")
    Call<JsonObject> prepay(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/order/pay/url")
    Call<JsonObject> getPayUrl(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/order/complete")
    Call<JsonObject> completePay(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/order/saveMaintain")
    Call<JsonObject> saveMaintain(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/order/visit")
    Call<JsonObject> signBill(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/notice/info/{shopNo}")
    Call<JsonObject> getNoticeInfo(@Path("shopNo") String shopNum, @FieldMap Map<String, String> params);

}

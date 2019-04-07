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

public interface RecordApi {

    @FormUrlEncoded
    @POST("/shop/info/{shopNo}")
    Call<JsonObject> getRecordInfo(@Path("shopNo") String shopNum,
                                   @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/equipment/list")
    Call<JsonObject> getRecordTypeInfo(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/equipmentCategory/list")
    Call<JsonObject> getCategoryList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/equipment/delete")
    Call<JsonObject> deleteDevice(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/equipment/save")
    Call<JsonObject> addDevice(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/equipment/submit")
    Call<JsonObject> submitDevice(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/equipment/info/{deviceId}")
    Call<JsonObject> getDeviceInfo(@Path("deviceId") String deviceId, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/equipment/update")
    Call<JsonObject> updateInfo(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/enterpriseOrder/list")
    Call<JsonObject> getCheckList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/enterpriseOrder/info/{checkId}")
    Call<JsonObject> getCheckInfo(@Path("checkId") String checkId,
                                  @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/enterpriseOrder/update")
    Call<JsonObject> updateOrder(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/enterpriseOrder/submit")
    Call<JsonObject> submitOrder(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/enterpriseOrderEquipment/info")
    Call<JsonObject> getOrderEquip(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/enterpriseOrderEquipment/update")
    Call<JsonObject> updateEquip(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/enterpriseOrder/quantity")
    Call<JsonObject> getEnterRedotCount(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/order/quantity")
    Call<JsonObject> getRepairRedotCount(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/feedback/save")
    Call<JsonObject> doFeedBack(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("/enterpriseOrderEquipment/info/v2")
    Call<JsonObject> getOrderEquipV2(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/enterpriseOrderEquipment/updateAgain")
    Call<JsonObject> updateOrderAgain(@FieldMap Map<String, String> params);


}

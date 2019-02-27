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





}

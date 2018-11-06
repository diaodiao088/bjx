package com.bjxapp.worker.apinew;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by zhangdan on 2018/11/6.
 * comments:
 */

public interface NotificationApi {

    @FormUrlEncoded
    @POST("/notice/list")
    Call<JsonObject> getNoticeList(@FieldMap Map<String, String> params);


}

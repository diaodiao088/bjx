package com.bjxapp.worker.apinew;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.POST;

/**
 * Created by zhangdan on 2018/11/13.
 * <p>
 * comments:
 */
public interface UpdateApi {

    @POST("/app/update")
    Call<JsonObject> checkUpdate();

}

package com.bjxapp.worker.apinew;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by zhangdan on 2018/11/1.
 * <p>n
 * comments:
 */

public interface LoginApi {

    //http://master-test.100jiaxiu.com
    //http://master.100jiaxiu.com
    String TEST_URL = "http://master.100jiaxiu.com";
    String FORMAL_URL = "http://master.100jiaxiu.com";

    String URL = com.bjx.master.BuildConfig.DEBUG ? TEST_URL : FORMAL_URL;

    @POST("/login/key")
    Call<JsonObject> getLoginKey();

    @FormUrlEncoded
    @POST("/login/authCode")
    Call<JsonObject> getAuthCode(@FieldMap Map<String, String> params); // 获取验证码

    @FormUrlEncoded
    @POST("/login/authCodeLogin")
    Call<JsonObject> authCodeLogin(@FieldMap Map<String, String> params); // 验证码登录

    @FormUrlEncoded
    @POST("/login/passwordLogin")
    Call<JsonObject> pwdLogin(@FieldMap Map<String, String> params); // 账号密码登录

    @FormUrlEncoded
    @POST("/profile/password")
    Call<JsonObject> changePwd(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/logout")
    Call<JsonObject> logOut(@FieldMap Map<String, String> params); // 退出登录

}

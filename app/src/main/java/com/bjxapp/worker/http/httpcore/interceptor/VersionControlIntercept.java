package com.bjxapp.worker.http.httpcore.interceptor;

import com.bjxapp.worker.App;
import com.bjxapp.worker.utils.Env;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhangdan on 2018/10/11.
 * <p>
 * comments:
 */

public class VersionControlIntercept implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        try {
            Request request = chain.request();

            Request.Builder requestBuilder = request.newBuilder().addHeader("apkVersion",
                    String.valueOf(Env.getVersion(App.getInstance())));

            Request newRequest = requestBuilder.build();

            return chain.proceed(newRequest);

        } catch (Throwable e) {
            throw new IOException(e);
        }
    }
}

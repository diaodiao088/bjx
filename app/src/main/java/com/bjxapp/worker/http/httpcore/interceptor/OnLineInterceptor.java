package com.bjxapp.worker.http.httpcore.interceptor;

import android.text.TextUtils;

import com.bjxapp.worker.http.httpcore.config.HttpConfig;
import com.bjxapp.worker.http.httpcore.utils.HttpUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by general on 26/09/2017.
 */

public class OnLineInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String cache = request.header(HttpConfig.CACHE_HEADER_VALUE_HOLDER);
        boolean cacheable = false;
        if (!TextUtils.isEmpty(cache)) {
            cacheable = true;
            request = request.newBuilder().removeHeader(HttpConfig.CACHE_HEADER_VALUE_HOLDER).build();
        }
        Response response = chain.proceed(request);
        if (cacheable) {
            return response.newBuilder().header("Expires", HttpUtils.formatDate(Long.parseLong(cache), TimeUnit.SECONDS)).build();
        }
        return response;
    }
}

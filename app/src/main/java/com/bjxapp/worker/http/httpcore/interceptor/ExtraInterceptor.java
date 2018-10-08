package com.bjxapp.worker.http.httpcore.interceptor;

import com.bjxapp.worker.http.httpcore.utils.HttpUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by general on 14/11/2017.
 */

public class ExtraInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (request.url() != null) {
            String urlString = request.url().toString();
            if (urlString != null && urlString.contains("https://api.giphy.com/v1/gifs/search")) {
                response = response.newBuilder().removeHeader("cache-control").build();
            }

            if (urlString != null && urlString.contains("https://hotword.ksmobile.net/getHotWord")) {
                response = response.newBuilder().header("Expires", HttpUtils.formatDate(43200, TimeUnit.SECONDS)).removeHeader("cache-control").build();
            }

            if (urlString != null && urlString.contains("https://api.qwant.com/api/search/ia")) {
                response = response.newBuilder().header("Expires", HttpUtils.formatDate(43200, TimeUnit.SECONDS)).removeHeader("Cache-Control").build();
            }

            if (urlString != null && urlString.contains("https://api.qwant.com/api/search/news")) {
                response = response.newBuilder().header("Expires", HttpUtils.formatDate(43200, TimeUnit.SECONDS)).removeHeader("Cache-Control").build();
            }

            if (urlString != null && urlString.contains("https://www.googleapis.com/youtube/v3/videos")) {
                response = response.newBuilder().header("Expires", HttpUtils.formatDate(43200, TimeUnit.SECONDS)).removeHeader("cache-control").build();
            }

            if (urlString != null && urlString.contains("https://www.googleapis.com/youtube/v3/search")) {
                response = response.newBuilder().header("Expires", HttpUtils.formatDate(43200, TimeUnit.SECONDS)).removeHeader("cache-control").build();
            }
        }
        return response;
    }
}

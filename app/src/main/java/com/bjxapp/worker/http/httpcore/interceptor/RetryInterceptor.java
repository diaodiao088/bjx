package com.bjxapp.worker.http.httpcore.interceptor;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by general on 14/09/2017.
 */

public class RetryInterceptor implements Interceptor {

    private int retryNum = 0;

    private final int maxRetryCount;

    public RetryInterceptor(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        while (!response.isSuccessful() && retryNum < maxRetryCount) {
            retryNum++;
            response = chain.proceed(request);
        }
        return response;
    }
}

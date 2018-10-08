package com.bjxapp.worker.http.httpcore.interceptor;

import com.bjxapp.worker.http.httpcore.config.HttpConfig;
import com.bjxapp.worker.http.keyboard.commonutils.CommonUtilsEnv;
import com.bjxapp.worker.http.keyboard.commonutils.KLog;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 用于记录网络请求日志
 * @since 2017.09.14 19:50
 * @author renwenjie
 * @version 1.0
 */

public class LoggerInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long t1 = System.nanoTime();
        if (CommonUtilsEnv.sDEBUG) {
            KLog.i(HttpConfig.TAG, String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));
        }
        Response response = chain.proceed(request);
        if (CommonUtilsEnv.sDEBUG) {
            long t2 = System.nanoTime();
            KLog.i(HttpConfig.TAG, String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
        }
        return response;
    }
}

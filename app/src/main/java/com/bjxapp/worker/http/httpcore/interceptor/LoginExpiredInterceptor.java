package com.bjxapp.worker.http.httpcore.interceptor;

import android.content.Intent;

import com.bjxapp.worker.App;
import com.bjxapp.worker.global.Constant;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;

import static okhttp3.internal.Util.UTF_8;

/**
 * Created by zhangdan on 2018/11/13.
 * <p>
 * comments:
 */

public class LoginExpiredInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Response originalResponse = chain.proceed(request);

        try {
            if (originalResponse.isSuccessful()) {
                BufferedSource source = originalResponse.body().source();
                source.request(Integer.MAX_VALUE);
                Buffer buffer = source.buffer();
                Charset charset = UTF_8;
                String bodyString = buffer.clone().readString(charset);

                JSONObject object = new JSONObject(bodyString);

                int code = object.getInt("code");
                String msg = object.getString("msg");

                if (code == 20000 || code == 20001) {
                    sendExpiredBroadCast(msg);
                }

            }
        } catch (Exception e) {
        }

        return originalResponse;

    }

    private void sendExpiredBroadCast(String msg) {
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_USER_EXPIRED);
        intent.putExtra("msg", msg);
        App.getInstance().sendBroadcast(intent);
    }

}

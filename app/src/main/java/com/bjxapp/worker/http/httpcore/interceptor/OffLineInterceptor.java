package com.bjxapp.worker.http.httpcore.interceptor;

import android.text.TextUtils;

import com.bjxapp.worker.http.httpcore.body.ResponseBodyProxy;
import com.bjxapp.worker.http.httpcore.download.DownloadResponseBody;
import com.bjxapp.worker.http.httpcore.tag.TagEntity;
import com.bjxapp.worker.http.httpcore.upload.UploadRequestBody;
import com.bjxapp.worker.http.httpcore.utils.HttpUtils;
import com.bjxapp.worker.http.keyboard.commonutils.CommonUtilsEnv;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by general on 26/09/2017.
 */

public class OffLineInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        try {
            Request request = chain.request();
            if (!HttpUtils.isNetWorkEnable(CommonUtilsEnv.getInstance().getApplicationContext())) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            } else {
                Object tag = request.tag();
                if (tag instanceof TagEntity) {
                    TagEntity entity = (TagEntity) tag;
                    if (entity.forceNetwork) {
                        request = request.newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build();
                    }
                    if (entity.downloadWatcher != null) {
                        Response response = chain.proceed(request);
                        return response.newBuilder().body(new DownloadResponseBody(entity.downloadWatcher, response.body(), request.url().toString())).build();
                    }

                    if (entity.uploadWatcher != null) {
                        RequestBody body = request.body();
                        UploadRequestBody realBody = new UploadRequestBody(body, entity.uploadWatcher);
                        Request newRequest = request.newBuilder().post(realBody).build();
                        return chain.proceed(newRequest);
                    }
                }
            }
            Response rawResponse = chain.proceed(request);
            String expires = rawResponse.header("Expires");
            if (TextUtils.isEmpty(expires)) {
                return rawResponse;
            } else {
                boolean fromCache = false;
                try {
                    Date date = HttpUtils.parseDate(expires);
                    fromCache = date.compareTo(new Date()) >= 0
                            && (!request.cacheControl().noCache())
                            && (rawResponse.networkResponse() == null)
                            && HttpUtils.isNetWorkEnable(CommonUtilsEnv.getInstance().getApplicationContext());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return rawResponse.newBuilder().body(new ResponseBodyProxy(rawResponse.body(), fromCache)).build();
            }
        } catch (Throwable throwable) {
            throw new IOException(throwable);
        }
    }
}

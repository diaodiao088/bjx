package com.bjxapp.worker.http.httpcore.body;

import android.support.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;

/**
 * Created by general on 28/09/2017.
 */

public class ResponseBodyProxy extends ResponseBody {

    private ResponseBody delegate;

    private boolean isFromCache = false;

    public ResponseBodyProxy(ResponseBody body, boolean fromCache) {
        delegate = body;
        isFromCache = fromCache;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() {
        return delegate.contentLength();
    }

    @Override
    public BufferedSource source() {
        return delegate.source();
    }

    public boolean isFromCache() {
        return this.isFromCache;
    }
}

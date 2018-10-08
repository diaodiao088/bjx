package com.bjxapp.worker.http.httpcore.watcher;

import okhttp3.ResponseBody;

/**
 * Created by general on 15/09/2017.
 */

public interface IUploadWatcher {

    void onUploadStart();

    void onUploadProgress(long hasUploadSize, long totalUploadSize);

    void onUploadComplete(ResponseBody body);

    void onUploadError(int code, String message);
}

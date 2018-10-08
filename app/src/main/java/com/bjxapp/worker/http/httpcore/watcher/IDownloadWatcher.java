package com.bjxapp.worker.http.httpcore.watcher;

import java.io.File;

/**
 * Created by general on 15/09/2017.
 */

public interface IDownloadWatcher {

    void onDownloadStart();

    void onDownloadProgress(long downloaded, long total);

    void onDownloadComplete(File file);

    void onDownloadError(int code, String msg);
}

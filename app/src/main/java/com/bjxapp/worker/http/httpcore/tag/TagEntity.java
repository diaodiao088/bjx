package com.bjxapp.worker.http.httpcore.tag;

import com.bjxapp.worker.http.httpcore.watcher.IDownloadWatcher;
import com.bjxapp.worker.http.httpcore.watcher.IUploadWatcher;
import com.bjxapp.worker.http.keyboard.commonutils.job.JobPriority;

/**
 * Created by general on 10/11/2017.
 */

public class TagEntity {

    public boolean forceNetwork = false;

    public @JobPriority.JobPriorityAnnotation int requestPriority = JobPriority.JOB_PRIORITY_NORMAL;

    public IUploadWatcher uploadWatcher;

    public IDownloadWatcher downloadWatcher;

    public TagEntity(boolean forceNetwork, @JobPriority.JobPriorityAnnotation int requestPriority, IUploadWatcher uploadWatcher, IDownloadWatcher downloadWatcher) {
        this.forceNetwork = forceNetwork;
        this.requestPriority = requestPriority;
        this.uploadWatcher = uploadWatcher;
        this.downloadWatcher = downloadWatcher;
    }
}

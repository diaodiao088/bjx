package com.bjxapp.worker.http.httpcore.wrapper;

import android.support.annotation.NonNull;

import com.bjxapp.worker.http.httpcore.tag.TagEntity;
import com.bjxapp.worker.http.httpcore.watcher.IDownloadWatcher;
import com.bjxapp.worker.http.httpcore.watcher.IUploadWatcher;
import com.bjxapp.worker.http.keyboard.commonutils.ReflectUtil;
import com.bjxapp.worker.http.keyboard.commonutils.job.JobPriority;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Call对象包装器
 */
public class CallWrapper<T> implements Call {

    private Call mDelegate;

    private boolean mForceUpdate = false;

    private boolean mDownload = false;

    private boolean mUpload = false;

    private @JobPriority.JobPriorityAnnotation int mPriority = JobPriority.JOB_PRIORITY_HIGH;

    private IDownloadWatcher mDownloadWatcher;

    private IUploadWatcher mUploadWatcher;


    public CallWrapper(Call<T> delegate, boolean forceUpdate, boolean download, boolean upload, @JobPriority.JobPriorityAnnotation int priority) {
        mDelegate = delegate;
        mForceUpdate = forceUpdate;
        mDownload = download;
        mUpload = upload;
        if (mDownload && mUpload) {
            throw new IllegalArgumentException("KHttpWorker can't handle upload and download simultaneously.");
        }
        mPriority = priority;
    }

    public CallWrapper(Call<T> delegate, boolean forceUpdate, boolean download, boolean upload) {
        this(delegate, forceUpdate, download, upload, JobPriority.JOB_PRIORITY_HIGH);
    }

    @Override
    public Response execute() throws IOException {
        modifyOriginRequest();
        return mDelegate.execute();
    }

    @Override
    public void enqueue(Callback callback) {
        modifyOriginRequest();
        mDelegate.enqueue(callback);
    }

    private void modifyOriginRequest() {
        Request originReqest = request();
        try {
            TagEntity entity = buildTagEntity();
            if (mForceUpdate) {
                ReflectUtil.fieldSet(originReqest, "tag", entity);
            } else if (mUpload) {
                ReflectUtil.fieldSet(originReqest, "tag", entity);
            } else if (mDownload) {
                CacheControl.Builder builder = new CacheControl.Builder();
                builder.noStore();
                ReflectUtil.fieldSet(originReqest, "cacheControl", builder.build());
                ReflectUtil.fieldSet(originReqest, "tag", entity);
            } else {
                ReflectUtil.fieldSet(originReqest, "tag", entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private TagEntity buildTagEntity() {
        return new TagEntity(mForceUpdate, mPriority, mUploadWatcher, mDownloadWatcher);
    }

    @Override
    public boolean isExecuted() {
        return mDelegate.isExecuted();
    }

    @Override
    public void cancel() {
        mDelegate.cancel();
    }

    @Override
    public boolean isCanceled() {
        return mDelegate.isCanceled();
    }

    @Override
    public Call clone() {
        try {
            return (Call) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Request request() {
        return mDelegate.request();
    }

    public void setDownloadWatcher(IDownloadWatcher  watcher) {
        if (!mDownload) {
            throw new IllegalArgumentException("mDownload must be true");
        }
        mDownloadWatcher = watcher;
    }

    public void setUploadWatcher(IUploadWatcher watcher) {
        if (!mUpload) {
            throw new IllegalArgumentException("mUpload must be true");
        }
        mUploadWatcher = watcher;
    }

}

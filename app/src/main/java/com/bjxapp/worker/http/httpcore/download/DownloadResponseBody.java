package com.bjxapp.worker.http.httpcore.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;


import com.bjxapp.worker.http.httpcore.watcher.IDownloadWatcher;
import com.bjxapp.worker.http.keyboard.commonutils.Preconditions;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by general on 15/09/2017.
 */

public class DownloadResponseBody extends ResponseBody implements Handler.Callback {

    private WeakReference<IDownloadWatcher> mDownloadWatcher;
    private ResponseBody mResponseBody;
    private BufferedSource mBufferedSource;

    private String mDownloadUrl;

    private Handler mMainHandler;

    private long mTotal = 0L;

    private static final int MSG_WHAT_DOWNLOAD_START = 0x11;

    private static final int MSG_WHAT_DOWNLOAD_PROGRESS = 0x12;

    public DownloadResponseBody(IDownloadWatcher downloadWatcher, ResponseBody responseBody, String downloadUrl) {
        this.mDownloadWatcher = new WeakReference<IDownloadWatcher>(downloadWatcher);
        this.mResponseBody = Preconditions.checkNotNull(responseBody);
        this.mDownloadUrl = downloadUrl;
        mMainHandler = new Handler(Looper.getMainLooper(), this);
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }


    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {

            long totalBytesRead = 0L;
            Object mLock = new Object();
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                synchronized (mLock) {
                    if (mDownloadWatcher != null) {
                        if (mTotal <= 0) {
                            mTotal = mResponseBody.contentLength();
                        }
                        if (totalBytesRead <= 0L) {
                            Message message = Message.obtain();
                            message.what = MSG_WHAT_DOWNLOAD_START;
                            mMainHandler.sendMessage(message);
                        }
                        totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                        if (totalBytesRead > 0 && totalBytesRead <= mTotal) {
                            if (mMainHandler.hasMessages(MSG_WHAT_DOWNLOAD_PROGRESS)) {
                                mMainHandler.removeMessages(MSG_WHAT_DOWNLOAD_PROGRESS);
                            }
                            Message message = Message.obtain();
                            message.what = MSG_WHAT_DOWNLOAD_PROGRESS;
                            message.obj = totalBytesRead;
                            mMainHandler.sendMessage(message);
                        } else {
                            mMainHandler.removeCallbacksAndMessages(null);
                        }
                    }
                }
                return bytesRead;
            }
        };
    }



    @Override
    public boolean handleMessage(Message msg) {
        if (mDownloadWatcher == null) {
            return false;
        }
        switch (msg.what) {
            case MSG_WHAT_DOWNLOAD_START:
                if (mDownloadWatcher.get() != null) {
                    mDownloadWatcher.get().onDownloadStart();
                }
                break;
            case MSG_WHAT_DOWNLOAD_PROGRESS:
                if (mDownloadWatcher.get() != null) {
                    mDownloadWatcher.get().onDownloadProgress((long) msg.obj, mTotal);
                }
                break;

            default:
                break;
        }
        return false;
    }

}

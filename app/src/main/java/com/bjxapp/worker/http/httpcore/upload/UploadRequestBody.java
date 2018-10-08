package com.bjxapp.worker.http.httpcore.upload;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.bjxapp.worker.http.httpcore.watcher.IUploadWatcher;
import com.bjxapp.worker.http.keyboard.commonutils.Preconditions;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicLong;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by general on 17/09/2017.
 */

public class UploadRequestBody extends RequestBody {

    private final WeakReference<IUploadWatcher> mUploadWatcherRef;
    private final RequestBody mDelegate;

    private volatile CountingSink mSink;
    public UploadRequestBody(RequestBody delegate, IUploadWatcher watcher) {
        mDelegate = delegate;
        mUploadWatcherRef = new WeakReference<IUploadWatcher>(watcher);
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mDelegate.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mDelegate.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (mSink == null) {
            mSink = new CountingSink(sink, mUploadWatcherRef, contentLength());
        }
        BufferedSink bufferedSink = Okio.buffer(mSink);
        mDelegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }


    private static class CountingSink extends ForwardingSink implements Handler.Callback {

        private AtomicLong mByteWrited = new AtomicLong(0L);
        private final long mTotalUpload; //总需上传的字节数
        private final Handler mHandler;

        private static final int MSG_WHAT_UPLOAD_START = 0x13;
        private static final int MSG_WHAT_UPLOAD_PROGRESS = 0x14;
        private static final int MSG_WHAT_UPLOAD_ERROR = 0x15;
        private final WeakReference<IUploadWatcher> mUploadWatcherRef;

        CountingSink(Sink delegate, WeakReference<IUploadWatcher> uploadWatcherRef, long total) {
            super(delegate);
            mUploadWatcherRef = Preconditions.checkNotNull(uploadWatcherRef);
            if (total <= 0) {
                throw new IllegalArgumentException("total is equeal 0.");
            }
            mTotalUpload = total;
            mHandler = new Handler(Looper.getMainLooper(), this);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            String errorMsg = "";
            boolean hasError = false;
            try {
                super.write(source, byteCount);
                if (mByteWrited.get() <= 0) {
                    Message message = Message.obtain();
                    message.what = MSG_WHAT_UPLOAD_START;
                    mHandler.sendMessage(message);
                }
                mByteWrited.addAndGet(byteCount);
                Message message = Message.obtain();
                message.what = MSG_WHAT_UPLOAD_PROGRESS;
                message.obj = mByteWrited.get();
                mHandler.sendMessage(message);
            } catch (Exception e) {
                hasError = true;
                errorMsg = e.getMessage();
                throw new IOException(e);
            } finally {
                if (hasError) {
                    Message message = Message.obtain();
                    message.what = MSG_WHAT_UPLOAD_ERROR;
                    message.obj = errorMsg;
                    mHandler.sendMessage(message);
                }
            }
        }

        @Override
        public boolean handleMessage(Message msg) {
            IUploadWatcher watcher = mUploadWatcherRef.get();
            if (watcher == null) {
                return false;
            }
            switch (msg.what) {
                case MSG_WHAT_UPLOAD_START:
                    watcher.onUploadStart();
                    break;
                case MSG_WHAT_UPLOAD_PROGRESS:
                    long hasUpload = (long) msg.obj;
                    if (hasUpload <= mTotalUpload) {
                        watcher.onUploadProgress(hasUpload, mTotalUpload);
                    }
                    break;
                case MSG_WHAT_UPLOAD_ERROR:
                    watcher.onUploadError(-1, msg.obj != null ? msg.obj.toString() : "");
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}

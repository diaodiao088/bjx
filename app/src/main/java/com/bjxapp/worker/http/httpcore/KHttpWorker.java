package com.bjxapp.worker.http.httpcore;

import android.text.TextUtils;

import com.bjxapp.worker.http.httpcore.config.HttpConfig;
import com.bjxapp.worker.http.httpcore.model.Result;
import com.bjxapp.worker.http.httpcore.utils.HttpUtils;
import com.bjxapp.worker.http.httpcore.utils.ObjectSupplier;
import com.bjxapp.worker.http.httpcore.watcher.IDownloadWatcher;
import com.bjxapp.worker.http.httpcore.watcher.INetWorkWatcher;
import com.bjxapp.worker.http.httpcore.watcher.IUploadWatcher;
import com.bjxapp.worker.http.httpcore.wrapper.CallWrapper;
import com.bjxapp.worker.http.keyboard.commonutils.Preconditions;
import com.bjxapp.worker.http.keyboard.commonutils.job.JobPriority;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 网络请求类
 *
 * @author renwenjie
 * @version 1.0
 * @since 2017.09.14 19:50
 */
public final class KHttpWorker {

    private static class LazyHolder {
        static final KHttpWorker INSTANCE = new KHttpWorker();
    }

    private KHttpWorker() {
    }

    public static KHttpWorker ins() {
        return LazyHolder.INSTANCE;
    }

    public <S> S createHttpService(Class<S> service) {
        return createHttpService("", service);
    }

    public <S> S createHttpService(HttpUrl baseUrl, Class<S> service) {
        baseUrl = Preconditions.checkNotNull(baseUrl);
        service = Preconditions.checkNotNull(service);
        return createHttpService(baseUrl.toString(), service);
    }

    public <S> S createHttpService(String baseUrl, Class<S> service) {
        String originBaseUrl = Preconditions.checkNotNull(ObjectSupplier.retrofit().baseUrl().toString());
        if (!TextUtils.isEmpty(baseUrl) && !originBaseUrl.equals(baseUrl)) {
            Retrofit base = ObjectSupplier.retrofit();
            Retrofit.Builder builder = base.newBuilder();
            builder.baseUrl(HttpUrl.parse(baseUrl));
            return builder.build().create(service);
        }
        return ObjectSupplier.retrofit().create(service);
    }

    public <T> Result<T> requestSync(Call<Result<T>> call) {
        return request(call, null, false, false, JobPriority.JOB_PRIORITY_HIGH);
    }

    public <T> Result<T> requestSync(Call<Result<T>> call, boolean forceUpdate) {
        return request(call, null, false, forceUpdate, JobPriority.JOB_PRIORITY_HIGH);
    }

    public <T> Result<T> requestSync(Call<Result<T>> call, boolean forceUpdate, @JobPriority.JobPriorityAnnotation int requestPriority) {
        return request(call, null, false, forceUpdate, requestPriority);
    }

    public <T> void requestAsync(Call<Result<T>> call, INetWorkWatcher<Result<T>> watcher) {
        request(call, watcher, true, false, JobPriority.JOB_PRIORITY_HIGH);
    }

    public <T> void requestAsync(Call<Result<T>> call, INetWorkWatcher<Result<T>> watcher, boolean forceUpdate) {
        request(call, watcher, true, forceUpdate, JobPriority.JOB_PRIORITY_HIGH);
    }

    public <T> void requestAsync(Call<Result<T>> call, INetWorkWatcher<Result<T>> watcher, boolean forceUpdate, @JobPriority.JobPriorityAnnotation int requestPriority) {
        request(call, watcher, true, forceUpdate, requestPriority);
    }

    private <T> Result<T> request(Call<Result<T>> call, final INetWorkWatcher<Result<T>> watcher, boolean isAsync, boolean forceUpdate, @JobPriority.JobPriorityAnnotation int requestPriority) {
        call = Preconditions.checkNotNull(call);
        CallWrapper wrapper = new CallWrapper(call, forceUpdate, false, false, requestPriority);
        if (isAsync) {
            wrapper.enqueue(new Callback<Result<T>>() {
                @Override
                public void onResponse(Call<Result<T>> call, Response<Result<T>> response) {
                    if (response.isSuccessful()) {
                        Result<T> body = response.body();
                        if (body != null) {
                            if (watcher == null) {
                                return;
                            }

                            if (body.isNormalJsonFormat()) {
                                if (body.code == HttpConfig.HTTP_RESP_OK) {
                                    watcher.onSuccess(body, response);
                                } else {
                                    watcher.onFail(HttpUtils.parseHttpStatus(body.code), response);
                                }
                            } else {
                                watcher.onSuccess(body, response);
                            }
                        }
                    } else {
                        if (watcher != null) {
                            watcher.onFail(HttpConfig.HTTP_RESP_ERROR, response);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Result<T>> call, Throwable t) {
                    if (watcher != null) {
                        watcher.onFail(HttpConfig.HTTP_RESP_ERROR, null);
                    }
                }
            });
            return null;
        } else {
            try {
                Response<Result<T>> rawResponse = wrapper.execute();
                return rawResponse.body();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public <T> Disposable requestWithRxJava(Observable<Result<T>> observable, final INetWorkWatcher<Result<T>> watcher) {
        observable = Preconditions.checkNotNull(observable, "observable == null");
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Result<T>>() {
                    @Override
                    public void accept(Result<T> result) throws Exception {
                        if (result != null) {
                            if (watcher != null) {
                                watcher.onSuccess(result, null);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (watcher != null) {
                            watcher.onFail(HttpConfig.HTTP_RESP_ERROR, null);
                        }
                    }
                });
    }


    public void download(Call<File> call, final IDownloadWatcher watcher) {
        call = Preconditions.checkNotNull(call);
        CallWrapper wrapper = new CallWrapper(call, false, true, false, JobPriority.JOB_PRIORITY_HIGH);
        wrapper.setDownloadWatcher(watcher);
        wrapper.enqueue(new Callback<File>() {
            @Override
            public void onResponse(Call<File> call, Response<File> response) {
                if (response.isSuccessful()) {
                    if (watcher != null) {
                        watcher.onDownloadComplete(response.body());
                    }
                } else {
                    if (watcher != null) {
                        watcher.onDownloadError(response.code(), response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<File> call, Throwable t) {
                if (watcher != null) {
                    watcher.onDownloadError(HttpConfig.DOWNLOAD_ERROR_CODE, t.getMessage());
                }
            }
        });
    }

    public void upload(Call<ResponseBody> call, final IUploadWatcher watcher) {
        call = Preconditions.checkNotNull(call, "call == null");
        CallWrapper wrapper = new CallWrapper(call, false, false, true, JobPriority.JOB_PRIORITY_HIGH);
        wrapper.setUploadWatcher(watcher);

        wrapper.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (watcher != null) {
                        watcher.onUploadComplete(response.body());
                    }
                } else {
                    if (watcher != null) {
                        watcher.onUploadError(response.code(), response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (watcher != null) {
                    watcher.onUploadError(-1, t.getMessage());
                }
            }
        });
    }

    public <T> void requestWithOrigin(Call<T> call, Callback<T> callBack) {
        requestWithOrigin(call, callBack, false, JobPriority.JOB_PRIORITY_HIGH);
    }

    public <T> void requestWithOrigin(Call<T> call, Callback<T> callBack, boolean forceUpdate) {
        requestWithOrigin(call, callBack, forceUpdate, JobPriority.JOB_PRIORITY_HIGH);
    }

    public <T> void requestWithOrigin(Call<T> call, Callback<T> callBack, boolean forceUpdate, @JobPriority.JobPriorityAnnotation int requestPriority) {
        call = Preconditions.checkNotNull(call);
        callBack = Preconditions.checkNotNull(callBack);
        CallWrapper wrapper = new CallWrapper(call, forceUpdate, false, false, requestPriority);
        wrapper.enqueue(callBack);
    }


    public <T> T requestWithOriginSync(Call<T> call, boolean forceUpdate, @JobPriority.JobPriorityAnnotation int requestPriority) {
        call = Preconditions.checkNotNull(call);
        CallWrapper wrapper = new CallWrapper(call, forceUpdate, false, false, requestPriority);
        try {
            Response<T> response = wrapper.execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T requestWithOriginSync(Call<T> call, boolean forceUpdate) {
        call = Preconditions.checkNotNull(call);
        CallWrapper wrapper = new CallWrapper(call, forceUpdate, false, false, JobPriority.JOB_PRIORITY_HIGH);
        try {
            Response<T> response = wrapper.execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

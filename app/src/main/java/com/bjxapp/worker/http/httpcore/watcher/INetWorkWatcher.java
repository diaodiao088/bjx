package com.bjxapp.worker.http.httpcore.watcher;


import com.bjxapp.worker.http.httpcore.config.HttpConfig;
import com.bjxapp.worker.http.httpcore.model.Result;

import retrofit2.Response;

/**
 * Created by general on 15/09/2017.
 */

public interface INetWorkWatcher<T extends Result> {

    void onSuccess(T result, Response<T> response);

    void onFail(@HttpConfig.HttpStatus int code, Response response);
}

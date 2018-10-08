package com.bjxapp.worker.utils.http;

@SuppressWarnings("serial")
public class HttpResponseException extends Exception {

    public int responseCode;

    public HttpResponseException(int responseCode) {
        this.responseCode = responseCode;
    }

}



package com.bjxapp.worker.http.httpcore.utils;

import android.content.Context;
import android.text.TextUtils;

import com.bjxapp.worker.http.httpcore.config.HttpConfig;

import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by general on 25/09/2017.
 */

public class HttpUtils {

    public static final @HttpConfig.HttpStatus
    int parseHttpStatus(int code) {
        switch (code) {
            case 0:
                return HttpConfig.HTTP_RESP_ERROR;
            case 1:
                return HttpConfig.HTTP_RESP_OK;
            case 2000:
                return HttpConfig.HTTP_RESP_ERROR_0;
            case 2001:
                return HttpConfig.HTTP_RESP_ERROR_1;
            case 2002:
                return HttpConfig.HTTP_RESP_ERROR_2;
            case 2003:
                return HttpConfig.HTTP_RESP_ERROR_3;
            case 2004:
                return HttpConfig.HTTP_RESP_ERROR_4;
            case 2005:
                return HttpConfig.HTTP_RESP_ERROR_5;
            case 2006:
                return HttpConfig.HTTP_RESP_ERROR_6;
            case 2007:
                return HttpConfig.HTTP_RESP_ERROR_7;
            case 2008:
                return HttpConfig.HTTP_RESP_ERROR_8;
            case 2009:
                return HttpConfig.HTTP_RESP_ERROR_9;
            default:
                return HttpConfig.HTTP_RESP_ERROR;
        }
    }

    public static final boolean isNetWorkEnable(Context context) {
        return NetworkUtil.isNetworkAvailable();
    }

    public static final boolean isWIFINetWorkEnable(Context context) {
        return NetworkUtil.isWifiNetworkAvailable();
    }

    public static final String formatDate(long delta, TimeUnit timeUnit) {
        Date date = new Date(System.currentTimeMillis() + timeUnit.toMillis(delta));
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(date);
    }

    public static final Date parseDate(String date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.parse(date);
    }

    public static final SSLSocketFactory createSSLSocketFactory(TrustManager trustManager) {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[] {trustManager}, null);
            return context.getSocketFactory();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public static final X509TrustManager createX509TrustManager() {
        try {
            return new X509TrustManager() {
                @Override public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final HostnameVerifier createInsecureHostnameVerifier() {
        return new HostnameVerifier() {
            @Override public boolean verify(String hostName, SSLSession sslSession) {

                if (TextUtils.isEmpty(hostName)) {
                    return false;
                }

                if (!Arrays.asList(VERIFY_HOST_NAME_ARRAY).contains(hostName)) {
                    return true;
                }
                return false;
            }
        };
    }

    private static String[] VERIFY_HOST_NAME_ARRAY = new String[] {};
}

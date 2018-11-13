package com.bjxapp.worker.http.httpcore.utils;

import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.http.httpcore.adapter.KCallAdapterFactory;
import com.bjxapp.worker.http.httpcore.config.HttpConfig;
import com.bjxapp.worker.http.httpcore.converter.KFileModelConvertorFactory;
import com.bjxapp.worker.http.httpcore.converter.KResultModelConverterFactory;
import com.bjxapp.worker.http.httpcore.dispatcher.OkHttpExecutorService;
import com.bjxapp.worker.http.httpcore.interceptor.ExtraInterceptor;
import com.bjxapp.worker.http.httpcore.interceptor.LoggerInterceptor;
import com.bjxapp.worker.http.httpcore.interceptor.LoginExpiredInterceptor;
import com.bjxapp.worker.http.httpcore.interceptor.OffLineInterceptor;
import com.bjxapp.worker.http.httpcore.interceptor.OnLineInterceptor;
import com.bjxapp.worker.http.httpcore.supplier.Supplier;
import com.bjxapp.worker.http.httpcore.supplier.Suppliers;
import com.bjxapp.worker.http.keyboard.CommonUtils;
import com.bjxapp.worker.http.keyboard.commonutils.KSystemUtils;
import com.bjxapp.worker.http.keyboard.commonutils.job.HttpThreadUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 单例对象提供者
 * @since 2017.09.15 11:10
 * @author renwenjie
 * @version 1.0
 */

public class ObjectSupplier {

    private static final Supplier<OkHttpClient> okHttpClientSupplier = Suppliers.memoize(new Supplier<OkHttpClient>() {
        @Override
        public OkHttpClient get() {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.readTimeout(HttpConfig.MAX_READ_TIMEOUT, HttpConfig.TIME_UNIT);
            builder.writeTimeout(HttpConfig.MAX_WRITE_TIMEOUT, HttpConfig.TIME_UNIT);
            builder.connectTimeout(HttpConfig.MAX_CONNECTION_TIMEOUT, HttpConfig.TIME_UNIT);
            builder.cache(new Cache(KSystemUtils.getCacheDirForNetwork(CommonUtils.getGlobalContext()), HttpConfig.DEFAULT_MAX_DISK_CACHE_SIZE));
            builder.addInterceptor(new OffLineInterceptor());

            builder.addNetworkInterceptor(new OnLineInterceptor());
            builder.addNetworkInterceptor(new ExtraInterceptor());
            builder.addNetworkInterceptor(new LoggerInterceptor());
            builder.addNetworkInterceptor(new LoginExpiredInterceptor());

            builder.dispatcher(new Dispatcher(new OkHttpExecutorService(HttpThreadUtils.getInstance().getExecutor())));
            try {
                X509TrustManager manager = HttpUtils.createX509TrustManager();
                SSLSocketFactory factory = HttpUtils.createSSLSocketFactory(manager);
                builder.sslSocketFactory(factory, manager);
                builder.hostnameVerifier(HttpUtils.createInsecureHostnameVerifier());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return builder.build();
        }
    });

    private static final Supplier<Retrofit> retrofitSupplier = Suppliers.memoize(new Supplier<Retrofit>() {
        @Override
        public Retrofit get() {
            Retrofit.Builder builder = new Retrofit.Builder();
            builder.baseUrl(HttpUrl.parse(LoginApi.URL))
                    .callFactory(okHttpClient())
                    .addCallAdapterFactory(KCallAdapterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create());
            return builder.build();
        }
    });


    private static final Supplier<Gson> gsonSupplier = Suppliers.memoize(new Supplier<Gson>() {
        @Override
        public Gson get() {
            GsonBuilder builder = new GsonBuilder();
            return builder.create();
        }
    });

    public static final OkHttpClient okHttpClient() {
        return okHttpClientSupplier.get();
    }

    public static final Retrofit retrofit() {
        return retrofitSupplier.get().newBuilder().build();
    }

    public static final Gson gson() {
        return gsonSupplier.get();
    }
}

package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.bjx.master.BuildConfig;
import com.bjx.master.R;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.ui.view.activity.widget.WebViewEX;
import com.bjxapp.worker.utils.BASE64;
import com.bjxapp.worker.utils.SignUtil;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhangdan on 2018/10/29.
 * comments:
 */

public class JoinUsActivity extends Activity {

    @BindView(R.id.webview_ly)
    LinearLayout wvLy;

    @BindView(R.id.title_text_tv)
    XTextView mTitleTv;

    @BindView(R.id.title_image_right)
    XImageView mTitleRightIv;

    public static final String URL = BuildConfig.DEBUG ? "http://app-test.100jiaxiu.com/profile/invite_an.html" :
            "https://app.100jiaxiu.com/profile/invite_an.html";

    public static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAINPaTf8qLJV" +
            "9ICgVZNlEGWiW9O3iRbamqcolv/5k6fxngXMXY1BDOFe7kpOjZai+M+rfRcCxDN5TWs0qDTFiBFe4Kek7Dt+bzpr6lDPyS" +
            "Gt+clKVCyCIcw5fYbwDmkqo95EFfttdeZ7t+Eo8TxLer7E3Y9DKJX5ZpszFf+KNpeXAgMBAAECgYAS0uh/iS3Z2u6hHdsGtT9" +
            "DgqHtWOtDJnVzLyNucKXVNpLzu9dRb5jcGhLI/jfX92b5xli5WsErq+EIVFUks4iM2F1sKOx5dPKkLVcywBCA4lwF9dM1bGewmp6" +
            "gd6nV8C3fpGQ/768Y1T1FyZQKY5PRsH8nXfDg0ysQXSG4a39pQQJBALoIDIVch8I+Fzlh4BOx/EJ+QhfSv2IGLoxJ55IpVhytstUoe" +
            "MNlVm6yCQUd8igmnXPYSXCwdA0WaF7mpsiUuKECQQC0so19VWE+B069tVD0rcAPHM2xgaMycWvbmYCfXWM4JcAfELTimsDRC8Gp+X4xc" +
            "A14j/+ujIxM1KOh6LsQKs03AkBvUrW7cJ3E/UcEja972bytRcTjYLgqPqzIQsmSy62+R6uGp0ttEk1gvKVtLHhm0oTSeBGqn80y" +
            "r8ARWlFgIq9BAkEAs48KkpUZQG46jMF0ZO+TfnD85XsRFLsoltt+uN4wDkdVFomfdd6Z5CFunN1TzQSwHjHZvTXk5SQYRnfiCzq9GQJ" +
            "AB2sHoA4L5mBhFCWHdIs/1Nc7BkAhANNgF+Pi6BT/hmUEW8YsCPNIvRFSAqo1MnIbgh8bug33Pyjj3xD7HdKOqw==";


    @OnClick(R.id.title_image_back)
    void onClickBack() {
        onBackPressed();
    }

    @OnClick(R.id.title_image_right)
    void onClickRefresh() {
        loadUrl();
    }

    WebViewEX mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_us);
        ButterKnife.bind(this);
        mTitleTv.setText("邀请好友");
        mTitleRightIv.setImageResource(R.drawable.refresh);
        mTitleRightIv.setVisibility(View.VISIBLE);
        initView();
        loadUrl();
    }

    private void initView() {
        mWebView = new WebViewEX(this);
        wvLy.addView(mWebView);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView.setLayoutParams(param);
        initWebView();
    }

    private void initWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        WebViewClient webViewClient = new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (handler != null) {
                    handler.proceed();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }


            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return super.shouldInterceptRequest(view, url);
            }
        };

        mWebView.setWebViewClient(webViewClient);
        if (Build.VERSION.SDK_INT > 10 && Build.VERSION.SDK_INT < 17) {
            mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        }
        mWebView.resumeTimersAndClearSsl();
    }

    private void loadUrl() {

        StringBuilder builder = new StringBuilder();

        String userCode = ConfigManager.getInstance(this).getUserCode();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String params = "timestamp=" + timestamp + "&userCode=" + userCode;

        String signStr = "";

        try {
            byte[] signResult = SignUtil.sign256(params, getPrivateKey(PRIVATE_KEY));
            signStr = SignUtil.encodeBase64(signResult);
        } catch (Exception e) {

        }

        builder.append(URL).append("?timestamp=")
                .append(timestamp)
                .append("&userCode=")
                .append(userCode)
                .append("&sign=")
                .append(signStr);

        mWebView.loadUrl(builder.toString());
    }

    public static void goToActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, JoinUsActivity.class);
        context.startActivity(intent);
    }

    public static PrivateKey getPrivateKey(final String rsaPrivateKey) throws Exception {
        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final byte[] encodedKey = BASE64.decode(rsaPrivateKey);
        final PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(encodedKey);
        final PrivateKey priKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        return priKey;
    }


}

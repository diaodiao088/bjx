package com.bjxapp.worker.ui.view.activity;

import java.lang.reflect.InvocationTargetException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.R;

//浏览器
public class WebViewActivity extends BaseActivity {
	protected static final String TAG = "浏览器";
	private XTextView mTitleTextView;
	private XImageView mBackImageView;
	private WebView mWebView;
	private ProgressBar mProgressbar;
	private String mURL = "";
	private MyTimer mTimer;
	private int mProgress = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_webview);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			mWebView.getClass().getMethod("onResume").invoke(mWebView, (Object[]) null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			mWebView.getClass().getMethod("onPause").invoke(mWebView, (Object[]) null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void initControl() {
		mTitleTextView = (XTextView) findViewById(R.id.title_text_title);
		mBackImageView = (XImageView) findViewById(R.id.title_image_back);
		mBackImageView.setVisibility(View.VISIBLE);
		mWebView = (WebView) findViewById(R.id.webview_webview);
		mProgressbar = (ProgressBar) findViewById(R.id.webview_progressbar);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void initView() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && Utils.isNotEmpty(bundle.getString("url"))) {
			mURL = "";
			mURL = bundle.getString("url");
		}
		if (bundle != null && Utils.isNotEmpty(bundle.getString("title"))) {
			mTitleTextView.setText(bundle.getString("title"));
		}
		if (!TextUtils.isEmpty(mURL)) {
			mWebView.getSettings().setJavaScriptEnabled(true); 
			mWebView.getSettings().setLoadsImagesAutomatically(true);
			mWebView.getSettings().setSupportZoom(true);
			mWebView.getSettings().setUseWideViewPort(true); 
			mWebView.getSettings().setLoadWithOverviewMode(true); 
			mWebView.getSettings().setBuiltInZoomControls(true);
			mWebView.setWebViewClient(new WeiboWebViewClient());
			mWebView.setWebChromeClient(new WebChromeClient());
			mWebView.loadUrl(mURL);
		}
	}

	private class WeiboWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			if (mTimer == null) {
				mTimer = new MyTimer(15000, 50);
			}
			mTimer.start();
			mProgressbar.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mTimer.cancel();
			mProgress = 0;
			mProgressbar.setProgress(100);
			mProgressbar.setVisibility(View.GONE);
		}
	}

	@Override
	protected void initData() {

	}

	private class MyTimer extends CountDownTimer {
		public MyTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			mProgress = 100;
			mProgressbar.setVisibility(View.GONE);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			if (mProgress == 100) {
				mProgressbar.setVisibility(View.GONE);
			} else {
				mProgressbar.setProgress(mProgress++);
			}
		}
	}

	@Override
	protected void setListener() {
		mBackImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mWebView.canGoBack())
					mWebView.goBack();
				else{
					Bundle bundle = getIntent().getExtras();
					if (bundle != null && bundle.getString(Constant.EXTRA_RETURN_KEY_CLASS_NAME) != null) {
						String returnClassName = bundle.getString(Constant.EXTRA_RETURN_KEY_CLASS_NAME);
						Intent it = new Intent();
						it.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						String packageName = context.getPackageName();
				        it.setClassName(packageName == null ? Constant.APP_PACKAGE_NAME : packageName, returnClassName);
				        startActivity(it);
					}
					Utils.finishActivity(WebViewActivity.this);
				}
			}
		});
	}

	@Override
	protected String getPageName() {
		return TAG;
	}

}

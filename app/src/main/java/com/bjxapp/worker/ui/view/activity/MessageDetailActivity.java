package com.bjxapp.worker.ui.view.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.Message;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;
import com.bjx.master.R;;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MessageDetailActivity extends BaseActivity implements OnClickListener {
    protected static final String TAG = "消息详情界面";
    private XTextView mTitleTextView;
    private XImageView mBackImageView;

    private XTextView mMessageDetialDate;
    private XTextView mMessageDetailTitle;
    private WebView mMessageDetailContent;

    public static final String MSG_TITLE = "msg_title";
    public static final String MSG_CONTENT = "msg_content";
    public static final String MSG_TIME = "time";

    private String title = "";
    private String content = "";
    private String time = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_message_detail);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initControl() {
        mTitleTextView = (XTextView) findViewById(R.id.title_text_tv);
        mTitleTextView.setText("通知详情");
        mBackImageView = (XImageView) findViewById(R.id.title_image_back);
        mBackImageView.setVisibility(View.VISIBLE);

        mMessageDetialDate = (XTextView) findViewById(R.id.message_detail_date);
        mMessageDetailTitle = (XTextView) findViewById(R.id.message_detail_title);
        mMessageDetailContent = findViewById(R.id.message_detail_content);

        WebSettings wSet = mMessageDetailContent.getSettings();
        wSet.setJavaScriptEnabled(true);
        wSet.setDefaultTextEncodingName("utf-8") ;

        //	mMessageDetailContent.setMovementMethod(ScrollingMovementMethod.getInstance());

        handleIntent();

    }

    private void handleIntent() {

        Intent intent = getIntent();

        if (intent != null) {

            time = intent.getStringExtra(MSG_TIME);
            content = intent.getStringExtra(MSG_CONTENT);
            title = intent.getStringExtra(MSG_TITLE);

            mMessageDetailContent.loadData(content, "text/html; charset=UTF-8", null);
            mMessageDetailTitle.setText(title);
            mMessageDetialDate.setText(getFormatDateString(time));

        }
    }

    public String getFormatDateString(String dateString) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            return format.format(Double.parseDouble(dateString));
        } catch (Exception e) {
            return dateString;
        }
    }


    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        loadData();
    }

    @Override
    protected void setListener() {
        mBackImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_image_back:
                Bundle bundle = getIntent().getExtras();
                if (bundle != null && bundle.getString(Constant.EXTRA_RETURN_KEY_CLASS_NAME) != null) {
                    String returnClassName = bundle.getString(Constant.EXTRA_RETURN_KEY_CLASS_NAME);
                    Intent it = new Intent();
                    it.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    String packageName = context.getPackageName();
                    it.setClassName(packageName == null ? Constant.APP_PACKAGE_NAME : packageName, returnClassName);
                    startActivity(it);
                }
                Utils.finishActivity(MessageDetailActivity.this);
                break;
            default:
                break;
        }
    }

    private AsyncTask<String, Void, Message> mLoadDataTask;

    private void loadData() {
        String messageID = getIntent().getStringExtra("message_id");
        if (!Utils.isNotEmpty(messageID)) {
            return;
        }

        mLoadDataTask = new AsyncTask<String, Void, Message>() {
            @Override
            protected Message doInBackground(String... params) {
                int id = Integer.valueOf(params[0]);
                return LogicFactory.getMessageLogic(context).getMessageDetail(id);
            }

            @Override
            protected void onPostExecute(Message result) {
                if (result == null) {
                    return;
                }

                mMessageDetialDate.setText(result.getDate());
                mMessageDetailTitle.setText(result.getTitle());
                //	mMessageDetailContent.setText(result.getContent());
            }
        };
        mLoadDataTask.execute(messageID);
    }

    @Override
    protected String getPageName() {
        return TAG;
    }

    @Override
    public void onDestroy() {
        try {
            if (mLoadDataTask != null) {
                mLoadDataTask.cancel(true);
            }
        } catch (Exception e) {
        }

        super.onDestroy();
    }

}

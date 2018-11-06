package com.bjxapp.worker.ui.view.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

import com.bjxapp.worker.adapter.MessageAdapter;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.NotificationApi;
import com.bjxapp.worker.apinew.ProfileApi;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.controls.listview.XListView;
import com.bjxapp.worker.controls.listview.XListView.IXListViewListener;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.Message;
import com.bjxapp.worker.model.WithdrawInfo;
import com.bjxapp.worker.ui.view.activity.MessageDetailActivity;
import com.bjxapp.worker.ui.view.base.BaseFragment;
import com.bjxapp.worker.utils.Logger;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Main_Third extends BaseFragment implements OnClickListener, IXListViewListener {
    protected static final String TAG = "通知";
    private RelativeLayout mLoadAgainLayout;
    private XWaitingDialog mWaitingDialog;
    private ArrayList<Message> mMessagesArray = new ArrayList<Message>();
    private MessageAdapter mMessageAdapter;
    private XListView mXListView;
    private int mCurrentBatch = 0;

    @Override
    protected void initView() {
        registerUpdateUIBroadcast();
        mLoadAgainLayout = (RelativeLayout) findViewById(R.id.message_list_load_again);
        mLoadAgainLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onFirstLoadData(true);
            }
        });

        mXListView = (XListView) findViewById(R.id.message_list_listview);
        mMessageAdapter = new MessageAdapter(mActivity, mMessagesArray);
        mXListView.setAdapter(mMessageAdapter);
        mXListView.setCacheColorHint(Color.TRANSPARENT);
        mXListView.setPullLoadEnable(true);
        mXListView.setPullRefreshEnable(true);
        mXListView.setXListViewListener(this);

        mXListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message message = (Message) mXListView.getItemAtPosition(position);
                //  Utils.startActivity(getActivity(), MessageDetailActivity.class, new BasicNameValuePair("message_id", String.valueOf(message.getId())));

                Intent intent = new Intent();
                intent.setClass(getActivity(), MessageDetailActivity.class);
                intent.putExtra(MessageDetailActivity.MSG_CONTENT, message.getContent());
                intent.putExtra(MessageDetailActivity.MSG_TIME, message.getDate());
                intent.putExtra(MessageDetailActivity.MSG_TITLE, message.getTitle());

                getActivity().startActivity(intent);

            }
        });

        mWaitingDialog = new XWaitingDialog(mActivity);

        setOnListener();
        onFirstLoadData(false);
    }

    @Override
    protected void finish() {

    }

    @Override
    protected int onCreateContent() {
        return R.layout.fragment_main_third;
    }

    @Override
    public void refresh(int enterType) {
        if (enterType != 0) {
            onFirstLoadData(false);
        }
    }

    private void setOnListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case 0:
                break;
            default:
                break;
        }
    }

    private void onLoadFinished() {
        mXListView.stopRefresh();
        mXListView.stopLoadMore();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String refreshTimeString = format.format(new Date());
        mXListView.setRefreshTime(refreshTimeString);
    }

    private String mCreateTime;

    private String getFormatedTime() {

        if (TextUtils.isEmpty(mCreateTime)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mCreateTime = format.format(new Date());
        }

        return mCreateTime;
    }

    private String updateFormatedTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mCreateTime = format.format(new Date());
        return mCreateTime;
    }

    private AsyncTask<Void, Void, List<Message>> mFirstLoadTask;

    private void onFirstLoadData(final Boolean loading) {

        if (!Utils.isNetworkAvailable(mActivity)) {
            Utils.showShortToast(mActivity, getString(R.string.common_no_network_message));
            return;
        }

        if (loading && mWaitingDialog != null) {
            mWaitingDialog.show("正在加载中，请稍候...", false);
        }

        NotificationApi notificationApi = KHttpWorker.ins().createHttpService(LoginApi.URL, NotificationApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(getActivity()).getUserSession());
        params.put("userCode", ConfigManager.getInstance(getActivity()).getUserCode());
        params.put("pageNum", String.valueOf(1));
        params.put("pageSize", String.valueOf(20));
        params.put("endCreateTime", getFormatedTime());

        Call<JsonObject> call = notificationApi.getNoticeList(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (loading) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mWaitingDialog != null) {
                                mWaitingDialog.dismiss();
                            }
                        }
                    });
                }

                JsonObject jsonObject = response.body();

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    String msg = jsonObject.get("msg").getAsString();
                    int code = jsonObject.get("code").getAsInt();

                    if (code == 0) {
                        JsonObject pageObject = jsonObject.getAsJsonObject("page");
                        JsonArray itemArray = pageObject.getAsJsonArray("list");

                        if (itemArray != null && itemArray.size() > 0) {
                            mMessagesArray.clear();
                            for (int i = 0; i < itemArray.size(); i++) {
                                JsonObject item = (JsonObject) itemArray.get(i);
                                /*WithdrawInfo withdrawInfoItem = new WithdrawInfo();
                                withdrawInfoItem.setDate(item.get("createTime").getAsString());
                                withdrawInfoItem.setMoney(item.get("amount").getAsString());
                                withdrawInfoItem.setStatus(item.get("status").getAsInt());
                                mMessagesArray.add(withdrawInfoItem);*/

                                Message messageItem = new Message();
                                messageItem.setTitle(item.get("title").getAsString());
                                messageItem.setContent(item.get("content").getAsString());
                                messageItem.setDate(item.get("createTime").getAsString());

                                mMessagesArray.add(messageItem);
                            }
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mCurrentBatch = 1;
                                mMessageAdapter = new MessageAdapter(mActivity, mMessagesArray);
                                mXListView.setAdapter(mMessageAdapter);
                                mCurrentBatch++;

                                if (mMessagesArray.size() > 0) {
                                    //  ConfigManager.getInstance(mActivity).setDesktopMessagesDot(ConfigManager.getInstance(mActivity).getDesktopMessagesDotServer());
                                    mLoadAgainLayout.setVisibility(View.GONE);
                                    mXListView.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    } else {

                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (loading) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mWaitingDialog != null) {
                                mWaitingDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    private AsyncTask<Void, Void, List<Message>> mRefreshTask;

    @Override
    public void onRefresh() {
        /*mRefreshTask = new AsyncTask<Void, Void, List<Message>>() {
            @Override
            protected List<Message> doInBackground(Void... params) {
                return LogicFactory.getMessageLogic(mActivity).getMessages(0);
            }

            @Override
            protected void onPostExecute(List<Message> result) {
                if (result == null) {
                    try {
                        onLoadFinished();
                    } catch (Exception e) {
                        Logger.i(e.getMessage());
                    }
                    return;
                }

                mCurrentBatch = 0;
                mMessagesArray.clear();
                mMessagesArray.addAll(result);
                mMessageAdapter = new MessageAdapter(mActivity, mMessagesArray);
                mXListView.setAdapter(mMessageAdapter);
                onLoadFinished();
                mCurrentBatch++;
            }
        };
        mRefreshTask.execute();*/


        if (!Utils.isNetworkAvailable(mActivity)) {
            Utils.showShortToast(mActivity, getString(R.string.common_no_network_message));
            return;
        }

        NotificationApi notificationApi = KHttpWorker.ins().createHttpService(LoginApi.URL, NotificationApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(getActivity()).getUserSession());
        params.put("userCode", ConfigManager.getInstance(getActivity()).getUserCode());
        params.put("pageNum", String.valueOf(1));
        params.put("pageSize", String.valueOf(20));
        params.put("endCreateTime", updateFormatedTime());

        Call<JsonObject> call = notificationApi.getNoticeList(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject jsonObject = response.body();

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    String msg = jsonObject.get("msg").getAsString();
                    int code = jsonObject.get("code").getAsInt();

                    if (code == 0) {
                        JsonObject pageObject = jsonObject.getAsJsonObject("page");
                        JsonArray itemArray = pageObject.getAsJsonArray("list");

                        if (itemArray != null && itemArray.size() > 0) {
                            mMessagesArray.clear();
                            for (int i = 0; i < itemArray.size(); i++) {
                                JsonObject item = (JsonObject) itemArray.get(i);

                                Message messageItem = new Message();
                                messageItem.setTitle(item.get("title").getAsString());
                                messageItem.setContent(item.get("content").getAsString());
                                messageItem.setDate(item.get("createTime").getAsString());

                                mMessagesArray.add(messageItem);
                            }
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mCurrentBatch = 1;

                                mMessageAdapter = new MessageAdapter(mActivity, mMessagesArray);
                                mXListView.setAdapter(mMessageAdapter);

                                mCurrentBatch++;

                                onLoadFinished();

                            }
                        });

                    } else {

                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });

    }

    @Override
    public void onLoadMore() {

        if (!Utils.isNetworkAvailable(mActivity)) {
            Utils.showShortToast(mActivity, getString(R.string.common_no_network_message));
        }

        NotificationApi notificationApi = KHttpWorker.ins().createHttpService(LoginApi.URL, NotificationApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(getActivity()).getUserSession());
        params.put("userCode", ConfigManager.getInstance(getActivity()).getUserCode());
        params.put("pageNum", String.valueOf(mCurrentBatch));
        params.put("pageSize", String.valueOf(2));
        params.put("endCreateTime", getFormatedTime());

        Call<JsonObject> call = notificationApi.getNoticeList(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject jsonObject = response.body();

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    String msg = jsonObject.get("msg").getAsString();
                    int code = jsonObject.get("code").getAsInt();

                    if (code == 0) {
                        JsonObject pageObject = jsonObject.getAsJsonObject("page");
                        JsonArray itemArray = pageObject.getAsJsonArray("list");

                        final ArrayList<Message> list = new ArrayList<>();
                        if (itemArray != null && itemArray.size() > 0) {

                            for (int i = 0; i < itemArray.size(); i++) {
                                JsonObject item = (JsonObject) itemArray.get(i);

                                Message messageItem = new Message();
                                messageItem.setTitle(item.get("title").getAsString());
                                messageItem.setContent(item.get("content").getAsString());
                                messageItem.setDate(item.get("createTime").getAsString());

                                list.add(messageItem);
                            }
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Utils.showShortToast(mActivity, getString(R.string.common_no_more_data_message));
                                    onLoadFinished();
                                }
                            });
                            return;
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mMessagesArray.addAll(list);
                                mMessageAdapter.notifyDataSetChanged();
                                onLoadFinished();
                                mCurrentBatch++;
                            }
                        });

                    } else {

                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });


    }

    @Override
    protected String getPageName() {
        return TAG;
    }

    @Override
    public void onDestroy() {
        try {
            if (mFirstLoadTask != null) {
                mFirstLoadTask.cancel(true);
            }
            if (mRefreshTask != null) {
                mRefreshTask.cancel(true);
            }
            //注销广播
            mActivity.unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
        }

        super.onDestroy();
    }

    /**
     * 定义广播接收器（内部类）
     *
     * @author Jason
     */
    private UpdateUIBroadcastReceiver broadcastReceiver;

    private class UpdateUIBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.showLongToast(mActivity, "您有新通知，请查看！");
            onFirstLoadData(false);
        }
    }

    /**
     * 动态注册广播
     */
    private void registerUpdateUIBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.PUSH_ACTION_MESSAGE_MODIFIED);
        broadcastReceiver = new UpdateUIBroadcastReceiver();
        mActivity.registerReceiver(broadcastReceiver, filter);
    }

}

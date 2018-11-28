package com.bjxapp.worker.ui.view.activity.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.RegisterApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.ui.view.activity.widget.treeview.Node;
import com.bjxapp.worker.ui.view.activity.widget.treeview.SimpleTreeRecyclerAdapter;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zhangdan on 2018/9/13.
 * <p>
 * comments:
 */
public class SearchActivityNew extends BaseActivity implements View.OnClickListener {

    public static final String TAG = SearchActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private SimpleTreeRecyclerAdapter mAdapter;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private XButton mOkBtn;
    private XImageView mBackBtn;

    @BindView(R.id.title_text_tv)
    public XTextView mTitleTv;

    @OnClick(R.id.title_image_back)
    void onBack() {
        onBackPressed();
    }

    Call<JsonObject> getProjectRequest;

    protected List<Node> mDatas = new ArrayList<Node>();

    private List<String> mSelectedList = new ArrayList<>();

    public static final String SELECT_LIST = "code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_search_new);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mTitleTv.setText("选择维修领域");
        handleIntent();
    }

    private void handleIntent() {

        Intent intent = getIntent();

        if (intent != null) {
            String selectIds = intent.getStringExtra(SELECT_LIST);
            if (!TextUtils.isEmpty(selectIds)) {
                String[] ids = selectIds.split(",");
                if (ids.length > 0) {
                    Collections.addAll(mSelectedList, ids);
                }
            }
        }
    }

    @Override
    protected void initControl() {
        RegisterApi httpService = KHttpWorker.ins().createHttpService(LoginApi.URL, RegisterApi.class);

        Map params = new HashMap();
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        getProjectRequest = httpService.getProject(params);

        getProjectRequest.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {

                    JsonObject jsonObject = response.body();
                    Set<Map.Entry<String, JsonElement>> set = jsonObject.entrySet();
                    JsonArray projectList = jsonObject.getAsJsonArray("list");

                    for (int i = 0; i < projectList.size(); i++) {
                        JsonObject item = (JsonObject) projectList.get(i);
                        String name = item.get("name").getAsString();
                        String pId = item.get("parentServiceId").getAsString();
                        String serviceId = item.get("serviceId").getAsString();
                        Node node = new Node(serviceId, pId, name);
                        mDatas.add(node);
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter = new SimpleTreeRecyclerAdapter(mRecyclerView, SearchActivityNew.this, mDatas, 1, R.drawable.xiala, R.drawable.sohuqi);
                            for (int i = 0; i < mDatas.size(); i++) {
                                if (isChecked(mDatas.get(i))) {
                                    mAdapter.setChecked(mDatas.get(i), true);
                                }
                            }
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


    private boolean isChecked(Node node) {

        if (mSelectedList == null || mSelectedList.size() <= 0) {
            return false;
        }

        String id = (String) node.getId();

        for (int i = 0; i < mSelectedList.size(); i++) {
            if (id.equals(mSelectedList.get(i))) {
                return true;
            }
        }

        return false;
    }


    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);

        mOkBtn = findViewById(R.id.layout_search_common_ok_button);
        mBackBtn = findViewById(R.id.title_image_back);

        mOkBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void initData() {
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected String getPageName() {
        return TAG;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_image_back:
                Utils.finishActivity(this);
                break;
            case R.id.layout_search_common_ok_button:
                clickShow();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 显示选中数据
     */
    public void clickShow() {
        StringBuilder sb = new StringBuilder();
        final List<Node> allNodes = mAdapter.getAllNodes();

        String ids = "";
        String names = "";

        for (int i = 0; i < allNodes.size(); i++) {

            Node node = allNodes.get(i);
            if (allNodes.get(i).isChecked() && allNodes.get(i).getChildren().size() == 0) {
                // sb.append(allNodes.get(i).getName()+",");
                ids = ids + node.getId() + ",";
                names = names + node.getName() + "、";
            }
        }

        if (ids.length() == 0) {
            Utils.showShortToast(context, "请至少选择一个项目！");
            return;
        }

        ids = ids.substring(0, ids.length() - 1);
        names = names.substring(0, names.length() - 1);

        Intent intent = new Intent();
        intent.putExtra("code", ids);
        intent.putExtra("name", names);
        setResult(RESULT_OK, intent);
        Utils.finishActivity(this);
    }

}

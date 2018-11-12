package com.bjxapp.worker.ui.view.activity.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bjx.master.R;;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.RegisterApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.CityInfo;
import com.bjxapp.worker.ui.view.activity.widget.treeview.Node;
import com.bjxapp.worker.ui.view.activity.widget.treeview.SimpleTreeRecyclerAdapter;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.internal.operators.maybe.MaybeDefer;
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

    protected List<Node> mDatas = new ArrayList<Node>();
    private List<Node> mSelectedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_search_new);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mTitleTv.setText("选择维修领域");
    }

    Call<JsonObject> getProjectRequest;

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

                    /*for (Map.Entry<String, JsonElement> entry : projectList.entrySet()) {

                    }*/
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter = new SimpleTreeRecyclerAdapter(mRecyclerView, SearchActivityNew.this, mDatas, 1, R.drawable.xiala, R.drawable.sohuqi);
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

    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);

        mOkBtn = findViewById(R.id.layout_search_common_ok_button);
        mBackBtn = findViewById(R.id.title_image_back);

        mOkBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);

        initDatas();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void initDatas() {
       /* mDatas.add(new Node("1", "-1", "文件管理系统"));

        mDatas.add(new Node(2 + "", 1 + "", "游戏"));
        mDatas.add(new Node(3 + "", 1 + "", "文档"));
        mDatas.add(new Node(4 + "", 1 + "", "程序"));
        mDatas.add(new Node(5 + "", 2 + "", "war3"));
        mDatas.add(new Node(6 + "", 2 + "", "刀塔传奇"));

        mDatas.add(new Node(7 + "", 4 + "", "面向对象"));
        mDatas.add(new Node(8 + "", 4 + "", "非面向对象"));

        mDatas.add(new Node(9 + "", 7 + "", "C++"));
        mDatas.add(new Node(10 + "", 7 + "", "JAVA"));
        mDatas.add(new Node(11 + "", 7 + "", "Javascript"));
        mDatas.add(new Node(12 + "", 8 + "", "C"));
        mDatas.add(new Node(13 + "", 12 + "", "C"));
        mDatas.add(new Node(14 + "", 13 + "", "C"));
        mDatas.add(new Node(15 + "", 14 + "", "C"));
        mDatas.add(new Node(16 + "", 15 + "", "C"));*/
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
            if (allNodes.get(i).isChecked()) {
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

    /*private void save(){
        if(mSourceDataList == null || mSourceDataList.size() == 0){
            Utils.showShortToast(context, "请至少选择一个项目！");
            return;
        }

        String ids = "";
        String names = "";
        for (SearchModel model : mSourceDataList) {
            if(model.getCheck() == 1){
                ids = ids + model.getCode() + ",";
                names = names + model.getName() + "、";
            }
        }

        if(ids.length() == 0){
            Utils.showShortToast(context, "请至少选择一个项目！");
            return;
        }

        ids = ids.substring(0, ids.length()-1);
        names = names.substring(0, names.length()-1);

        Intent intent = new Intent();
        intent.putExtra("code", ids);
        intent.putExtra("name", names);
        setResult(RESULT_OK, intent);
        Utils.finishActivity(this);
    }*/

}

package com.bjxapp.worker.ui.view.activity.category;

import com.bjxapp.worker.App;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.RecordApi;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.ui.view.activity.bean.RecordBean;
import com.bjxapp.worker.ui.view.activity.bean.RecordItemBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryDataManager {

    public static CategoryDataManager categoryDataManager;

    public OnCategoryLoadListener mLoadListener;

    private ArrayList<RecordBean> mList = new ArrayList<>();

    private ArrayList<RecordBean> mListWithOutItem = new ArrayList<>();


    private CategoryDataManager() {
    }

    public static CategoryDataManager getIns() {
        if (categoryDataManager == null) {
            categoryDataManager = new CategoryDataManager();
        }
        return categoryDataManager;
    }

    public interface OnCategoryLoadListener {

        void onDataLoadSuccess();

        void onDataLoadFail();

    }

    public void setListener(OnCategoryLoadListener listener) {
        this.mLoadListener = listener;
    }

    public void loadDataIfNeed(OnCategoryLoadListener listener) {
        this.mLoadListener = listener;
        if (mList.size() > 0) {
            if (mLoadListener != null) {
                mLoadListener.onDataLoadSuccess();
            }
        } else {
            loadDataReal();
        }
    }

    private void loadDataReal() {

        RecordApi recordApi = KHttpWorker.ins().createHttpService(LoginApi.URL, RecordApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(App.getInstance()).getUserSession());
        params.put("userCode", ConfigManager.getInstance(App.getInstance()).getUserCode());

        Call<JsonObject> categoryCall = recordApi.getCategoryList(params);

        categoryCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {

                    JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {
                        parseCategoryData(object);
                    } else {
                        mLoadListener.onDataLoadFail();
                    }
                } else {
                    if (mLoadListener != null) {
                        mLoadListener.onDataLoadFail();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mLoadListener != null) {
                    mLoadListener.onDataLoadFail();
                }
            }
        });
    }


    private void parseCategoryData(JsonObject object) {

        JsonArray categoryArray = object.getAsJsonArray("list");

        for (int i = 0; i < categoryArray.size(); i++) {
            JsonObject categoryObject = categoryArray.get(i).getAsJsonObject();

            RecordBean recordBean = new RecordBean();

            mList.add(recordBean);
            mListWithOutItem.add(recordBean);

            recordBean.setTypeName(categoryObject.get("name").getAsString());
            recordBean.setTypeId(categoryObject.get("id").getAsString());

            JsonArray itemArray = categoryObject.getAsJsonArray("subList");

            for (int j = 0; j < itemArray.size(); j++) {
                JsonObject itemObject = itemArray.get(i).getAsJsonObject();
                RecordItemBean recordItemBean = new RecordItemBean();
                recordBean.getmItemList().add(recordItemBean);
                recordItemBean.setParentId(itemObject.get("parentId").getAsString());
                recordItemBean.setName(itemObject.get("name").getAsString());
                recordItemBean.setCategoryId(itemObject.get("id").getAsString());
            }

            if (mLoadListener != null) {
                mLoadListener.onDataLoadSuccess();
            }
        }
    }


    public ArrayList<RecordBean> getCategoryListWithOutList() {

        return new ArrayList<>(mListWithOutItem);
    }

    public ArrayList<RecordBean> getCategoryList() {

        return mList;
    }


}

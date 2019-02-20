package com.bjxapp.worker.ui.view.activity.bean;

import android.support.annotation.Keep;

import java.util.ArrayList;

@Keep
public class RecordBean {

    private ArrayList<RecordItemBean> mItemList = new ArrayList<>();

    private String typeName;

    private int status;

    public ArrayList<RecordItemBean> getmItemList() {
        return mItemList;
    }

    public void setmItemList(ArrayList<RecordItemBean> mItemList) {
        this.mItemList = mItemList;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public class RecordItemBean{

        private int status;

        private String name;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


}

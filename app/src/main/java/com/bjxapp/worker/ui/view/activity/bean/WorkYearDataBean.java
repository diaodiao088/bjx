package com.bjxapp.worker.ui.view.activity.bean;

/**
 * Created by zhangdan on 2018/11/27.
 * <p>
 * comments:
 */

public class WorkYearDataBean {

    public boolean isChecked;

    public String mContextTv;

    public WorkYearDataBean(boolean isChecked, String mContextTv) {
        this.isChecked = isChecked;
        this.mContextTv = mContextTv;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getmContextTv() {
        return mContextTv;
    }

    public void setmContextTv(String mContextTv) {
        this.mContextTv = mContextTv;
    }
}

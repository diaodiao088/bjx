package com.bjxapp.worker.ui.view.fragment.ctrl;

import com.bjxapp.worker.model.UserInfo;

/**
 * Created by zhangdan on 2018/11/5.
 * <p>
 * comments:
 */

public class UserInfoManagerCtrl {

    public static UserInfoManagerCtrl sIns;

    private UserInfo mUserInfo = new UserInfo();

    private boolean isUserInfoDirty;

    private UserInfoManagerCtrl(){}

    public static UserInfoManagerCtrl getsIns(){
        if (sIns == null){
            synchronized (UserInfoManagerCtrl.class){
                if (sIns == null){
                    sIns = new UserInfoManagerCtrl();
                }
            }
        }
        return sIns;
    }

    public UserInfo getmUserInfo() {
        return mUserInfo;
    }

    public void setmUserInfo(UserInfo mUserInfo) {
        this.mUserInfo = mUserInfo;
    }

    public boolean isUserInfoDirty() {
        return isUserInfoDirty;
    }

    public void setUserInfoDirty(boolean userInfoDirty) {
        isUserInfoDirty = userInfoDirty;
    }

}

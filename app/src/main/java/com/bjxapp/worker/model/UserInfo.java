package com.bjxapp.worker.model;

/**
 * Created by zhangdan on 2018/11/5.
 * comments:
 */

public class UserInfo {

    public static final int SERVICE_STATUS_RECEIVE = 0x00;
    public static final int SERVICE_STATUS_DENY = 0x01;

    AccountInfo accountInfo;

    EvaluationStatInfo evaluationStatInfo;

    UserInfoDetail infoDetail;

    private String masterId;

    private String mPhoneNum;

    private int mServiceStat;

    private String mUserName;

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public String getmPhoneNum() {
        return mPhoneNum;
    }

    public void setmPhoneNum(String mPhoneNum) {
        this.mPhoneNum = mPhoneNum;
    }

    public int getmServiceStat() {
        return mServiceStat;
    }

    public void setmServiceStat(int mServiceStat) {
        this.mServiceStat = mServiceStat;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public UserInfoDetail getInfoDetail() {
        return infoDetail;
    }

    public void setInfoDetail(UserInfoDetail infoDetail) {
        this.infoDetail = infoDetail;
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }

    public EvaluationStatInfo getEvaluationStatInfo() {
        return evaluationStatInfo;
    }

    public void setEvaluationStatInfo(EvaluationStatInfo evaluationStatInfo) {
        this.evaluationStatInfo = evaluationStatInfo;
    }
}

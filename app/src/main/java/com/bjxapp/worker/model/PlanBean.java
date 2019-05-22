package com.bjxapp.worker.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PlanBean implements Parcelable {

    private int applicationType;

    private int id;

    private ArrayList<CommentBean> mCommentList = new ArrayList<>();

    private ArrayList<OtherPriceBean> mOtherPriceList = new ArrayList<>();

    private ArrayList<MainTainBean> mMaintainList = new ArrayList<>();

    private ArrayList<String> mPlanImgList = new ArrayList<>();
    private ArrayList<String> mResultImgList = new ArrayList<>();

    private String coordinateNextHandleStartTime;

    private String coordinateNextHandleEndTime;

    private String coordinateReason;

    private String extraCost;

    private String fault;

    public PlanBean() {

    }


    protected PlanBean(Parcel in) {
        applicationType = in.readInt();
        id = in.readInt();
        mCommentList = in.createTypedArrayList(CommentBean.CREATOR);
        mOtherPriceList = in.createTypedArrayList(OtherPriceBean.CREATOR);
        mMaintainList = in.createTypedArrayList(MainTainBean.CREATOR);
        mPlanImgList = in.createStringArrayList();
        mResultImgList = in.createStringArrayList();
        coordinateNextHandleStartTime = in.readString();
        coordinateNextHandleEndTime = in.readString();
        coordinateReason = in.readString();
        extraCost = in.readString();
        fault = in.readString();
        plan = in.readString();
        payAmount = in.readString();
        mPlanImgUrls = in.createStringArrayList();
        mResultImgUrls = in.createStringArrayList();
        status = in.readInt();
        totalCost = in.readString();
    }

    public static final Creator<PlanBean> CREATOR = new Creator<PlanBean>() {
        @Override
        public PlanBean createFromParcel(Parcel in) {
            return new PlanBean(in);
        }

        @Override
        public PlanBean[] newArray(int size) {
            return new PlanBean[size];
        }
    };

    public ArrayList<CommentBean> getmCommentList() {
        return mCommentList;
    }

    public void setmCommentList(ArrayList<CommentBean> mCommentList) {
        this.mCommentList = mCommentList;
    }

    public ArrayList<OtherPriceBean> getmOtherPriceList() {
        return mOtherPriceList;
    }

    public void setmOtherPriceList(ArrayList<OtherPriceBean> mOtherPriceList) {
        this.mOtherPriceList = mOtherPriceList;
    }

    public ArrayList<MainTainBean> getmMaintainList() {
        return mMaintainList;
    }

    public void setmMaintainList(ArrayList<MainTainBean> mMaintainList) {
        this.mMaintainList = mMaintainList;
    }

    public String getCoordinateNextHandleStartTime() {
        return coordinateNextHandleStartTime;
    }

    public void setCoordinateNextHandleStartTime(String coordinateNextHandleStartTime) {
        this.coordinateNextHandleStartTime = coordinateNextHandleStartTime;
    }

    public String getCoordinateNextHandleEndTime() {
        return coordinateNextHandleEndTime;
    }

    public void setCoordinateNextHandleEndTime(String coordinateNextHandleEndTime) {
        this.coordinateNextHandleEndTime = coordinateNextHandleEndTime;
    }

    public String getCoordinateReason() {
        return coordinateReason;
    }

    public void setCoordinateReason(String coordinateReason) {
        this.coordinateReason = coordinateReason;
    }

    public String getExtraCost() {
        return extraCost;
    }

    public void setExtraCost(String extraCost) {
        this.extraCost = extraCost;
    }

    private String plan;

    private String payAmount;

    private ArrayList<String> mPlanImgUrls = new ArrayList<>();

    private ArrayList<String> mResultImgUrls = new ArrayList<>();

    private int status;

    private String totalCost;

    public int getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(int applicationType) {
        this.applicationType = applicationType;
    }

    public String getFault() {
        return fault;
    }

    public void setFault(String fault) {
        this.fault = fault;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public ArrayList<String> getmPlanImgUrls() {
        return mPlanImgUrls;
    }

    public void setmPlanImgUrls(ArrayList<String> mPlanImgUrls) {
        this.mPlanImgUrls = mPlanImgUrls;
    }

    public ArrayList<String> getmResultImgUrls() {
        return mResultImgUrls;
    }

    public void setmResultImgUrls(ArrayList<String> mResultImgUrls) {
        this.mResultImgUrls = mResultImgUrls;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getmPlanImgList() {
        return mPlanImgList;
    }

    public void setmPlanImgList(ArrayList<String> mPlanImgList) {
        this.mPlanImgList = mPlanImgList;
    }

    public ArrayList<String> getmResultImgList() {
        return mResultImgList;
    }

    public void setmResultImgList(ArrayList<String> mResultImgList) {
        this.mResultImgList = mResultImgList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(applicationType);
        parcel.writeInt(id);
        parcel.writeTypedList(mCommentList);
        parcel.writeTypedList(mOtherPriceList);
        parcel.writeTypedList(mMaintainList);
        parcel.writeStringList(mPlanImgList);
        parcel.writeStringList(mResultImgList);
        parcel.writeString(coordinateNextHandleStartTime);
        parcel.writeString(coordinateNextHandleEndTime);
        parcel.writeString(coordinateReason);
        parcel.writeString(extraCost);
        parcel.writeString(fault);
        parcel.writeString(plan);
        parcel.writeString(payAmount);
        parcel.writeStringList(mPlanImgUrls);
        parcel.writeStringList(mResultImgUrls);
        parcel.writeInt(status);
        parcel.writeString(totalCost);
    }
}

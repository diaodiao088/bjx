package com.bjxapp.worker.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;

public class ThiOtherBean implements Parcelable {

    private String remark = "";

    private String cost;

    private int id;

    private String model = "";

    private String name;

    private String number;

    private String unit;

    private String renGongCost = "0";

    private ArrayList<String> imgList = new ArrayList<>();

    private boolean isOther;

    public ThiOtherBean() {

    }

    protected ThiOtherBean(Parcel in) {
        remark = in.readString();
        cost = in.readString();
        id = in.readInt();
        model = in.readString();
        name = in.readString();
        number = in.readString();
        unit = in.readString();
        renGongCost = in.readString();
        imgList = in.createStringArrayList();
        isOther = in.readByte() != 0;
    }

    public static final Creator<ThiOtherBean> CREATOR = new Creator<ThiOtherBean>() {
        @Override
        public ThiOtherBean createFromParcel(Parcel in) {
            return new ThiOtherBean(in);
        }

        @Override
        public ThiOtherBean[] newArray(int size) {
            return new ThiOtherBean[size];
        }
    };

    public boolean isOther() {
        return isOther;
    }

    public void setOther(boolean other) {
        isOther = other;
    }

    public ArrayList<String> getImgList() {
        return imgList;
    }

    public void setImgList(ArrayList<String> imgList) {
        this.imgList = imgList;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRenGongCost() {
        return renGongCost;
    }

    public void setRenGongCost(String renGongCost) {

        if (!TextUtils.isEmpty(renGongCost)){
            this.renGongCost = renGongCost;
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(remark);
        parcel.writeString(cost);
        parcel.writeInt(id);
        parcel.writeString(model);
        parcel.writeString(name);
        parcel.writeString(number);
        parcel.writeString(unit);
        parcel.writeString(renGongCost);
        parcel.writeStringList(imgList);
        parcel.writeByte((byte) (isOther ? 1 : 0));
    }
}

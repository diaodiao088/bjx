package com.bjxapp.worker.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MainTainBean implements Parcelable{

    public String cost;

    public int componentId;

    public String model;

    public String componentName;

    public int quantity;

    public String unit;

    public int id;

    public boolean isOthers;

    private ThiOtherBean thiOtherBean;

    public MainTainBean() {

    }

    public ThiOtherBean getThiOtherBean() {
        return thiOtherBean;
    }

    public void setThiOtherBean(ThiOtherBean thiOtherBean) {
        this.thiOtherBean = thiOtherBean;
    }

    protected MainTainBean(Parcel in) {
        cost = in.readString();
        componentId = in.readInt();
        model = in.readString();
        componentName = in.readString();
        quantity = in.readInt();
        unit = in.readString();
        id = in.readInt();
        isOthers = in.readByte() != 0;
        thiOtherBean = in.readParcelable(ThiOtherBean.class.getClassLoader());
    }

    public static final Creator<MainTainBean> CREATOR = new Creator<MainTainBean>() {
        @Override
        public MainTainBean createFromParcel(Parcel in) {
            return new MainTainBean(in);
        }

        @Override
        public MainTainBean[] newArray(int size) {
            return new MainTainBean[size];
        }
    };

    public boolean isOthers() {
        return isOthers;
    }

    public void setOthers(boolean others) {
        isOthers = others;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public int getComponentId() {
        return componentId;
    }

    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(cost);
        parcel.writeInt(componentId);
        parcel.writeString(model);
        parcel.writeString(componentName);
        parcel.writeInt(quantity);
        parcel.writeString(unit);
        parcel.writeInt(id);
        parcel.writeByte((byte) (isOthers ? 1 : 0));
        parcel.writeParcelable(thiOtherBean, i);
    }
}

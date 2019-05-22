package com.bjxapp.worker.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MainTainBean implements Parcelable {

    public String cost;

    public int componentId;

    public String model;

    public String componentName;

    public int quantity;

    public String unit;

    public int id;

    public boolean isOthers;

    private String laborCost;

    private String rengongCost;

    private ThiOtherBean thiOtherBean;

    public MainTainBean() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cost);
        dest.writeInt(componentId);
        dest.writeString(model);
        dest.writeString(componentName);
        dest.writeInt(quantity);
        dest.writeString(unit);
        dest.writeInt(id);
        dest.writeByte((byte) (isOthers ? 1 : 0));
        dest.writeString(laborCost);
        dest.writeString(rengongCost);
        dest.writeParcelable(thiOtherBean, flags);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getLaborCost() {
        return laborCost;
    }

    public void setLaborCost(String laborCost) {
        this.laborCost = laborCost;
    }

    public String getRengongCost() {
        return rengongCost;
    }

    public void setRengongCost(String rengongCost) {
        this.rengongCost = rengongCost;
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

}

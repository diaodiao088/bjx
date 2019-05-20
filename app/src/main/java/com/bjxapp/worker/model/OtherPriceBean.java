package com.bjxapp.worker.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OtherPriceBean implements Parcelable {

    private String price = "0";

    private String name ;

    public OtherPriceBean(){

    }

    protected OtherPriceBean(Parcel in) {
        price = in.readString();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(price);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OtherPriceBean> CREATOR = new Creator<OtherPriceBean>() {
        @Override
        public OtherPriceBean createFromParcel(Parcel in) {
            return new OtherPriceBean(in);
        }

        @Override
        public OtherPriceBean[] newArray(int size) {
            return new OtherPriceBean[size];
        }
    };

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

package com.bjxapp.worker.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ShopInfoBean implements Parcelable{

    private String detailAddress;

    private String enterpriseId;

    private String enterpriseName;

    private String id;

    private String latitude;

    private String longitude;

    private String name;

    private String shopNum;

    private String locationAddress;

    private String contactNumber;

    private String contactPerson;

    private String serviceImgUrl;

    public ShopInfoBean() {

    }

    public ShopInfoBean(String detailAddress, String enterpriseId, String enterpriseName,
                        String id, String latitude, String longitude, String name, String shopNum, String locationAddress) {
        this.detailAddress = detailAddress;
        this.enterpriseId = enterpriseId;
        this.enterpriseName = enterpriseName;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.shopNum = shopNum;
        this.locationAddress = locationAddress;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(detailAddress);
        dest.writeString(enterpriseId);
        dest.writeString(enterpriseName);
        dest.writeString(id);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(name);
        dest.writeString(shopNum);
        dest.writeString(locationAddress);
        dest.writeString(contactNumber);
        dest.writeString(contactPerson);
        dest.writeString(serviceImgUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShopInfoBean> CREATOR = new Creator<ShopInfoBean>() {
        @Override
        public ShopInfoBean createFromParcel(Parcel in) {
            return new ShopInfoBean(in);
        }

        @Override
        public ShopInfoBean[] newArray(int size) {
            return new ShopInfoBean[size];
        }
    };

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShopNum() {
        return shopNum;
    }

    public void setShopNum(String shopNum) {
        this.shopNum = shopNum;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    protected ShopInfoBean(Parcel in) {
        detailAddress = in.readString();
        enterpriseId = in.readString();
        enterpriseName = in.readString();
        id = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        name = in.readString();
        shopNum = in.readString();
        locationAddress = in.readString();
        contactNumber = in.readString();
        contactPerson = in.readString();
    }

    public String getServiceImgUrl() {
        return serviceImgUrl;
    }

    public void setServiceImgUrl(String serviceImgUrl) {
        this.serviceImgUrl = serviceImgUrl;
    }
}

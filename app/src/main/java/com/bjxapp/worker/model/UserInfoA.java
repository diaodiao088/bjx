package com.bjxapp.worker.model;

import java.util.ArrayList;

/**
 * Created by zhangdan on 2018/11/7.
 * comments:
 */

public class UserInfoA {

    private String avatarUrl;

    private String identityCardBehindImgUrl;

    private String identityCardFrontImgUrl;

    private String identityCardNo;

    private String latitude;

    private String longitude;

    private String locationAddress;

    private String name;

    private int regionId;

    private String regionName;

    private int workingYear;

    private ArrayList<String> mServiceList;

    private ArrayList<String> mServiceIdList;

    public UserInfoA(String avatarUrl, String identityCardBehindImgUrl, String identityCardFrontImgUrl, String identityCardNo,
                     String latitude, String longitude, String locationAddress,
                     String name, int regionId, String regionName, int workingYear) {
        this.avatarUrl = avatarUrl;
        this.identityCardBehindImgUrl = identityCardBehindImgUrl;
        this.identityCardFrontImgUrl = identityCardFrontImgUrl;
        this.identityCardNo = identityCardNo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationAddress = locationAddress;
        this.name = name;
        this.regionId = regionId;
        this.regionName = regionName;
        this.workingYear = workingYear;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getIdentityCardBehindImgUrl() {
        return identityCardBehindImgUrl;
    }

    public void setIdentityCardBehindImgUrl(String identityCardBehindImgUrl) {
        this.identityCardBehindImgUrl = identityCardBehindImgUrl;
    }

    public String getIdentityCardFrontImgUrl() {
        return identityCardFrontImgUrl;
    }

    public void setIdentityCardFrontImgUrl(String identityCardFrontImgUrl) {
        this.identityCardFrontImgUrl = identityCardFrontImgUrl;
    }

    public String getIdentityCardNo() {
        return identityCardNo;
    }

    public void setIdentityCardNo(String identityCardNo) {
        this.identityCardNo = identityCardNo;
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

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public int getWorkingYear() {
        return workingYear;
    }

    public void setWorkingYear(int workingYear) {
        this.workingYear = workingYear;
    }

    public ArrayList<String> getmServiceList() {
        return mServiceList;
    }

    public void setmServiceList(ArrayList<String> mServiceList) {
        this.mServiceList = mServiceList;
    }

    public ArrayList<String> getmServiceIdList() {
        return mServiceIdList;
    }

    public void setmServiceIdList(ArrayList<String> mServiceIdList) {
        this.mServiceIdList = mServiceIdList;
    }
}

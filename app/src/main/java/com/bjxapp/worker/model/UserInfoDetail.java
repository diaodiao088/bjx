package com.bjxapp.worker.model;

/**
 * Created by zhangdan on 2018/11/5.
 * comments:
 */

public class UserInfoDetail {

    public String avatarUrl;

    private String identityCardBehindImgUrl;

    private String identityCardFrontImgUrl;

    private String latitude;

    private String locationAddress;

    private String longitude;

    private String name;

    private String regionId;

    private String regionName;

    private int mWorkingYear;

    public UserInfoDetail(String avatarUrl, String identityCardBehindImgUrl,
                          String identityCardFrontImgUrl, String latitude,
                          String locationAddress, String longitude,
                          String name, String regionId,
                          String regionName, int mWorkingYear) {
        this.avatarUrl = avatarUrl;
        this.identityCardBehindImgUrl = identityCardBehindImgUrl;
        this.identityCardFrontImgUrl = identityCardFrontImgUrl;
        this.latitude = latitude;
        this.locationAddress = locationAddress;
        this.longitude = longitude;
        this.name = name;
        this.regionId = regionId;
        this.regionName = regionName;
        this.mWorkingYear = mWorkingYear;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
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

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public int getmWorkingYear() {
        return mWorkingYear;
    }

    public void setmWorkingYear(int mWorkingYear) {
        this.mWorkingYear = mWorkingYear;
    }
}

package com.bjxapp.worker.model;

/**
 * Created by zhangdan on 2018/9/12.
 * <p>
 * comments:
 */

public class CityInfo {

    private boolean isShowCategory;

    private String categoryId;

    private String cityName;

    public String getmCityId() {
        return mCityId;
    }

    public void setmCityId(String mCityId) {
        this.mCityId = mCityId;
    }

    private String mCityId;

    private int id;

    public boolean isShowCategory() {
        return isShowCategory;
    }

    public void setShowCategory(boolean showCategory) {
        isShowCategory = showCategory;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

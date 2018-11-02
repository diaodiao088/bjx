package com.bjxapp.worker.ui.view.activity.bean;

/**
 * Created by zhangdan on 2018/11/3.
 * comments:
 */

public class CityBean {

    public String firstLetter;

    public boolean shouldLetterVisible;

    public String cityName;

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public boolean isShouldLetterVisible() {
        return shouldLetterVisible;
    }

    public void setShouldLetterVisible(boolean shouldLetterVisible) {
        this.shouldLetterVisible = shouldLetterVisible;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}

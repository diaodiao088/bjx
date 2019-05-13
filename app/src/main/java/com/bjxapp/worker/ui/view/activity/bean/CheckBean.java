package com.bjxapp.worker.ui.view.activity.bean;

import com.bjxapp.worker.model.ShopInfoBean;

public class CheckBean {

    public static final int TYPE_TITLE = 0x01;

    private String time;

    private int processStatus;

    private String day;

    private String serviceName;

    private String orderId;

    private ShopInfoBean shopInfoBean;

    private int status;

    private int type = 0;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(int processStatus) {
        this.processStatus = processStatus;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public ShopInfoBean getShopInfoBean() {
        return shopInfoBean;
    }

    public void setShopInfoBean(ShopInfoBean shopInfoBean) {
        this.shopInfoBean = shopInfoBean;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean isDateMatched(int year, int month, int day) {
        String formatedTime = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
        return formatedTime.equals(this.day);
    }

    public int getYear(){
        return Integer.parseInt(this.day.substring(0,4));
    }

    public int getMonth(){
        return Integer.parseInt(this.day.substring(5 , 7));
    }

    public int getDays(){
        return Integer.parseInt(this.day.substring(8 , 10));
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

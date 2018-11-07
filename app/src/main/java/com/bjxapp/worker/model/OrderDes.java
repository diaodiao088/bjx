package com.bjxapp.worker.model;

import java.util.ArrayList;

/**
 * Created by zhangdan on 2018/11/6.
 * comments:
 */

public class OrderDes {

    private String orderId;

    private int processStatus;

    private int status;  //状态*，1-正常，2-超时，3-取消，4-异常

    private String serviceName; // 订单名称

    private String appointmentDay;

    private String appointmentEndTime;

    private String appointmentStartTime;

    private String locationAddress;

    private String serviceVisitCost;

    private String contactPhone;

    private ArrayList<String> mCustomImageUrls;

    private String mSelectTime;

    private String mRemarkDes;

    private String personName;

    private String mLatitude;

    private String mLongtitude;

    public OrderDes(String orderId, int processStatus, int status, String serviceName,
                    String appointmentDay, String appointmentEndTime,
                    String appointmentStartTime, String locationAddress,
                    String serviceVisitCost) {
        this.orderId = orderId;
        this.processStatus = processStatus;
        this.status = status;
        this.serviceName = serviceName;
        this.appointmentDay = appointmentDay;
        this.appointmentEndTime = appointmentEndTime;
        this.appointmentStartTime = appointmentStartTime;
        this.locationAddress = locationAddress;
        this.serviceVisitCost = serviceVisitCost;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getmRemarkDes() {
        return mRemarkDes;
    }

    public void setmRemarkDes(String mRemarkDes) {
        this.mRemarkDes = mRemarkDes;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public ArrayList<String> getmCustomImageUrls() {
        return mCustomImageUrls;
    }

    public void setmCustomImageUrls(ArrayList<String> mCustomImageUrls) {
        this.mCustomImageUrls = mCustomImageUrls;
    }

    public String getmSelectTime() {
        return mSelectTime;
    }

    public void setmSelectTime(String mSelectTime) {
        this.mSelectTime = mSelectTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(int processStatus) {
        this.processStatus = processStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAppointmentDay() {
        return appointmentDay;
    }

    public void setAppointmentDay(String appointmentDay) {
        this.appointmentDay = appointmentDay;
    }

    public String getAppointmentEndTime() {
        return appointmentEndTime;
    }

    public void setAppointmentEndTime(String appointmentEndTime) {
        this.appointmentEndTime = appointmentEndTime;
    }

    public String getAppointmentStartTime() {
        return appointmentStartTime;
    }

    public void setAppointmentStartTime(String appointmentStartTime) {
        this.appointmentStartTime = appointmentStartTime;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getServiceVisitCost() {
        return serviceVisitCost;
    }

    public void setServiceVisitCost(String serviceVisitCost) {
        this.serviceVisitCost = serviceVisitCost;
    }

    public String getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(String mLatitude) {
        this.mLatitude = mLatitude;
    }

    public String getmLongtitude() {
        return mLongtitude;
    }

    public void setmLongtitude(String mLongtitude) {
        this.mLongtitude = mLongtitude;
    }
}

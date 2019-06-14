package com.bjxapp.worker.model;

import java.util.ArrayList;

/**
 * Created by zhangdan on 2018/11/6.
 * comments:
 */

public class OrderDes {

    public static final int BILL_TYPE_NORMAL = 0x00;
    public static final int BILL_TYPE_EMERGENCY = 0X01;

    private String orderId;

    private int processStatus;

    private int status;  //状态*，1-正常，2-超时，3-取消，4-异常

    private String serviceName; // 订单名称

    private String appointmentDay;

    private String appointmentEndTime;

    private String appointmentStartTime;

    private String locationAddress;

    private String serviceVisitCost = "";

    private String contactPhone;

    private ArrayList<String> mCustomImageUrls;

    private String mSelectTime;

    private String mRemarkDes;

    private String personName;

    private String mLatitude;

    private String mLongtitude;

    private String esCost;

    private boolean inGuaranteePeriod;

    private String orderTime;

    private String orderNum = "";

    private String detailAddress = "";

    private String selectMasterTime = "";

    private String payAmount;

    private int billType;

    private int originType;

    private String mServiceType;

    private String enterpriseId;
    private String enterpriseOrderId;

    private String shopServiceImgUrl;

    String detailId;

    private boolean isBussiness;

    private int businessType;

    private String mShopName = "";
    private String mEnterpriseName = "";

    private int xietiaoStatus;

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    public String getEnterpriseOrderId() {
        return enterpriseOrderId;
    }

    public String getmShopName() {
        return mShopName;
    }

    public void setmShopName(String mShopName) {
        this.mShopName = mShopName;
    }

    public String getmEnterpriseName() {
        return mEnterpriseName;
    }

    public void setmEnterpriseName(String mEnterpriseName) {
        this.mEnterpriseName = mEnterpriseName;
    }

    public void setEnterpriseOrderId(String enterpriseOrderId) {
        this.enterpriseOrderId = enterpriseOrderId;
    }

    private boolean isFree;

    private boolean isTwiceServed;

    private int settleStatus;

    public int getBusinessType() {
        return businessType;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }

    public String getmServiceType() {
        return mServiceType;
    }

    public boolean isBussiness() {
        return isBussiness;
    }

    public void setBussiness(boolean bussiness) {
        isBussiness = bussiness;
    }

    public void setmServiceType(String mServiceType) {
        this.mServiceType = mServiceType;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

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

    public int getSettleStatus() {
        return settleStatus;
    }

    public void setSettleStatus(int settleStatus) {
        this.settleStatus = settleStatus;
    }

    public int getBillType() {
        return billType;
    }

    public void setBillType(int billType) {
        this.billType = billType;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public boolean isInGuaranteePeriod() {
        return inGuaranteePeriod;
    }

    public void setInGuaranteePeriod(boolean inGuaranteePeriod) {
        this.inGuaranteePeriod = inGuaranteePeriod;
    }

    public String getSelectMasterTime() {
        return selectMasterTime;
    }

    public void setSelectMasterTime(String selectMasterTime) {
        this.selectMasterTime = selectMasterTime;
    }

    public int getOriginType() {
        return originType;
    }

    public void setOriginType(int originType) {
        this.originType = originType;
    }

    public String getEsCost() {
        return esCost;
    }

    public void setEsCost(String esCost) {
        this.esCost = esCost;
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

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public boolean isTwiceServed() {
        return isTwiceServed;
    }

    public void setTwiceServed(boolean twiceServed) {
        isTwiceServed = twiceServed;
    }

    public int getXietiaoStatus() {
        return xietiaoStatus;
    }

    public void setXietiaoStatus(int xietiaoStatus) {
        this.xietiaoStatus = xietiaoStatus;
    }

    public String getShopServiceImgUrl() {
        return shopServiceImgUrl;
    }

    public void setShopServiceImgUrl(String shopServiceImgUrl) {
        this.shopServiceImgUrl = shopServiceImgUrl;
    }
}

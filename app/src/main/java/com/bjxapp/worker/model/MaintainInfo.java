package com.bjxapp.worker.model;

import java.util.ArrayList;

/**
 * Created by zhangdan on 2018/11/6.
 * comments:
 */

public class MaintainInfo {

    private String costDetail; // 报价明细

    private String fault; // 故障原因

    private ArrayList<String> masterImgUrls;

    private boolean isPaid; // 是否支付

    private String payAmount; // 付款金额

    private String plan; //维修方案

    private String prePaid; // 是否预付

    private String preCost; // 预付金额

    private ArrayList<String> prepayImgUrls; // 预付照片

    private String prePayService; // 预付内容

    private String totalAmount; // 总计金额

    private String totalCost;  // 总报价

    public MaintainInfo(String costDetail, String fault, boolean isPaid,
                        String payAmount, String plan, String prePaid,
                        String preCost, String prePayService,
                        String totalAmount, String totalCost) {
        this.costDetail = costDetail;
        this.fault = fault;
        this.isPaid = isPaid;
        this.payAmount = payAmount;
        this.plan = plan;
        this.prePaid = prePaid;
        this.preCost = preCost;
        this.prePayService = prePayService;
        this.totalAmount = totalAmount;
        this.totalCost = totalCost;
    }

    public String getCostDetail() {
        return costDetail;
    }

    public void setCostDetail(String costDetail) {
        this.costDetail = costDetail;
    }

    public String getFault() {
        return fault;
    }

    public void setFault(String fault) {
        this.fault = fault;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getPrePaid() {
        return prePaid;
    }

    public void setPrePaid(String prePaid) {
        this.prePaid = prePaid;
    }

    public String getPreCost() {
        return preCost;
    }

    public void setPreCost(String preCost) {
        this.preCost = preCost;
    }

    public String getPrePayService() {
        return prePayService;
    }

    public void setPrePayService(String prePayService) {
        this.prePayService = prePayService;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public ArrayList<String> getMasterImgUrls() {
        return masterImgUrls;
    }

    public void setMasterImgUrls(ArrayList<String> masterImgUrls) {
        this.masterImgUrls = masterImgUrls;
    }

    public ArrayList<String> getPrepayImgUrls() {
        return prepayImgUrls;
    }

    public void setPrepayImgUrls(ArrayList<String> prepayImgUrls) {
        this.prepayImgUrls = prepayImgUrls;
    }
}
package com.bjxapp.worker.ui.view.activity.bean;

import com.bjxapp.worker.model.ShopInfoBean;

import java.util.ArrayList;

public class CheckDetailBean {

    private String id;

    private ShopInfoBean shopInfoBean;

    private String actualTime;

    private String time;

    private int processState;

    private int status;

    public String getActualTime() {
        return actualTime;
    }

    public void setActualTime(String actualTime) {
        this.actualTime = actualTime;
    }

    private ArrayList<CategoryBean> categoryList = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ShopInfoBean getShopInfoBean() {
        return shopInfoBean;
    }

    public void setShopInfoBean(ShopInfoBean shopInfoBean) {
        this.shopInfoBean = shopInfoBean;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<CategoryBean> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(ArrayList<CategoryBean> categoryList) {
        this.categoryList = categoryList;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class CategoryBean {

        ArrayList<DeviceBean> deviceList = new ArrayList<>();

        private String id;

        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<DeviceBean> getDeviceList() {
            return deviceList;
        }

        public void setDeviceList(ArrayList<DeviceBean> deviceList) {
            this.deviceList = deviceList;
        }
    }

    public static class DeviceBean {

        private int status;

        private String id;

        private String equipName;

        private String situation;

        private String needMaintain;

        private String remark;

        private String imgUrls;

        private String score;

        private String scoreId;

        public String getSituation() {
            return situation;
        }

        public void setSituation(String situation) {
            this.situation = situation;
        }

        public String getNeedMaintain() {
            return needMaintain;
        }

        public void setNeedMaintain(String needMaintain) {
            this.needMaintain = needMaintain;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getImgUrls() {
            return imgUrls;
        }

        public void setImgUrls(String imgUrls) {
            this.imgUrls = imgUrls;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getScoreId() {
            return scoreId;
        }

        public void setScoreId(String scoreId) {
            this.scoreId = scoreId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEquipName() {
            return equipName;
        }

        public void setEquipName(String equipName) {
            this.equipName = equipName;
        }
    }

    public int getProcessState() {
        return processState;
    }

    public void setProcessState(int processState) {
        this.processState = processState;
    }
}

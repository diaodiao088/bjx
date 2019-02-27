package com.bjxapp.worker.ui.view.activity.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class RecordItemBean implements Parcelable {

    private int status;  // 设备状态

    private String name;

    private String parentId;

    private String id; // 自身 id

    private String categoryId;

    private String brandName;

    private String equipmentNo;

    private ArrayList<String> mImgUrls = new ArrayList<>();

    private String model ; // 设备型号

    private String productTime; // 生产时间

    private int recordStatus ; // 录入状态

    private String remark; //备注

    private String shopId;

    private int enableStatus;

    private String enterId;

    public RecordItemBean(){}

    protected RecordItemBean(Parcel in) {
        status = in.readInt();
        name = in.readString();
        parentId = in.readString();
        id = in.readString();
        categoryId = in.readString();
        brandName = in.readString();
        equipmentNo = in.readString();
        mImgUrls = in.createStringArrayList();
        model = in.readString();
        productTime = in.readString();
        recordStatus = in.readInt();
        remark = in.readString();
        shopId = in.readString();
        enableStatus = in.readInt();
        enterId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeString(name);
        dest.writeString(parentId);
        dest.writeString(id);
        dest.writeString(categoryId);
        dest.writeString(brandName);
        dest.writeString(equipmentNo);
        dest.writeStringList(mImgUrls);
        dest.writeString(model);
        dest.writeString(productTime);
        dest.writeInt(recordStatus);
        dest.writeString(remark);
        dest.writeString(shopId);
        dest.writeInt(enableStatus);
        dest.writeString(enterId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecordItemBean> CREATOR = new Creator<RecordItemBean>() {
        @Override
        public RecordItemBean createFromParcel(Parcel in) {
            return new RecordItemBean(in);
        }

        @Override
        public RecordItemBean[] newArray(int size) {
            return new RecordItemBean[size];
        }
    };

    public String getEnterId() {
        return enterId;
    }

    public void setEnterId(String enterId) {
        this.enterId = enterId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getEquipmentNo() {
        return equipmentNo;
    }

    public void setEquipmentNo(String equipmentNo) {
        this.equipmentNo = equipmentNo;
    }

    public ArrayList<String> getmImgUrls() {
        return mImgUrls;
    }

    public void setmImgUrls(ArrayList<String> mImgUrls) {
        this.mImgUrls = mImgUrls;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProductTime() {
        return productTime;
    }

    public void setProductTime(String productTime) {
        this.productTime = productTime;
    }

    public int getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(int recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public int getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(int enableStatus) {
        this.enableStatus = enableStatus;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}

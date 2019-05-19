package com.bjxapp.worker.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FollowUpBean implements Parcelable {


    private long createTime;

    private String content;

    private int applicationType;

    protected FollowUpBean(Parcel in) {
        createTime = in.readLong();
        content = in.readString();
        applicationType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(createTime);
        dest.writeString(content);
        dest.writeInt(applicationType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FollowUpBean> CREATOR = new Creator<FollowUpBean>() {
        @Override
        public FollowUpBean createFromParcel(Parcel in) {
            return new FollowUpBean(in);
        }

        @Override
        public FollowUpBean[] newArray(int size) {
            return new FollowUpBean[size];
        }
    };

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(int applicationType) {
        this.applicationType = applicationType;
    }

    public FollowUpBean(long createTime, String content, int applicationType) {
        this.createTime = createTime;
        this.content = content;
        this.applicationType = applicationType;
    }

    public FollowUpBean(){

    }


}

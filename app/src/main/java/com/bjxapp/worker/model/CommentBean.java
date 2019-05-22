package com.bjxapp.worker.model;


import android.os.Parcel;
import android.os.Parcelable;

public class CommentBean implements Parcelable {

    private int applicationType ;

    private String content;

    private long createTime;

    private String userName;

    public CommentBean(){}

    protected CommentBean(Parcel in) {
        applicationType = in.readInt();
        content = in.readString();
        createTime = in.readLong();
        userName = in.readString();
    }

    public static final Creator<CommentBean> CREATOR = new Creator<CommentBean>() {
        @Override
        public CommentBean createFromParcel(Parcel in) {
            return new CommentBean(in);
        }

        @Override
        public CommentBean[] newArray(int size) {
            return new CommentBean[size];
        }
    };

    public int getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(int applicationType) {
        this.applicationType = applicationType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(applicationType);
        parcel.writeString(content);
        parcel.writeLong(createTime);
        parcel.writeString(userName);
    }
}

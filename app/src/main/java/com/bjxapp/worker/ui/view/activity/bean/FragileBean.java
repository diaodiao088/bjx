package com.bjxapp.worker.ui.view.activity.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import java.util.ArrayList;

@Keep
public class FragileBean implements Parcelable {

    private String fragileName = "";

    private ArrayList<ImageBean> imageList = new ArrayList<>();

    private ArrayList<String> urls = new ArrayList<>();

    public FragileBean() {
    }


    protected FragileBean(Parcel in) {
        fragileName = in.readString();
        urls = in.createStringArrayList();
    }

    public static final Creator<FragileBean> CREATOR = new Creator<FragileBean>() {
        @Override
        public FragileBean createFromParcel(Parcel in) {
            return new FragileBean(in);
        }

        @Override
        public FragileBean[] newArray(int size) {
            return new FragileBean[size];
        }
    };

    public String getFragileName() {
        return fragileName;
    }

    public void setFragileName(String fragileName) {
        this.fragileName = fragileName;
    }

    public ArrayList<ImageBean> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<ImageBean> imageList) {
        this.imageList = imageList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fragileName);
        dest.writeStringList(urls);
    }

    public class ImageBean {

        public int type;
        public String url;

        public static final int TYPE_ADD = 0x01;
        public static final int TYPE_IMAGE = 0x02;

        public ImageBean() {

        }

        public ImageBean(int type, String url) {
            this.type = type;
            this.url = url;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

}

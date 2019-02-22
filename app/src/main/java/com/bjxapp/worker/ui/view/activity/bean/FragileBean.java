package com.bjxapp.worker.ui.view.activity.bean;

import android.support.annotation.Keep;

import java.util.ArrayList;

@Keep
public class FragileBean {

    private String fragileName = "";

    private ArrayList<ImageBean> imageList = new ArrayList<>();

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

    public class ImageBean {

        private int type;
        private String url;

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

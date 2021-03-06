package com.bjxapp.worker.db;

/**
 * Created by zhangdan on 2018/11/9.
 * <p>
 * comments:
 */

public class BjxInfo {

    private boolean isVoice;

    private int type;

    private String content;

    private String title;

    private String remark;

    private String createTime;

    private boolean isRead;

    public BjxInfo() {

    }

    public BjxInfo(int type, String content, String title, String remark, String createTime) {
        this.type = type;
        this.content = content;
        this.title = title;
        this.remark = remark;
        this.createTime = createTime;
    }

    public boolean isVoice() {
        return isVoice;
    }

    public void setVoice(boolean voice) {
        isVoice = voice;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public String toString() {
        return "BjxInfo{" +
                "isVoice=" + isVoice +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", remark='" + remark + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}

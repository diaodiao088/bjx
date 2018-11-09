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

    private long createTime;

    public BjxInfo(int type, String content, String title, String remark, long createTime) {
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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
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
}

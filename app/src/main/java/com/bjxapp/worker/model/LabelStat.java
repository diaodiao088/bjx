package com.bjxapp.worker.model;

/**
 * Created by zhangdan on 2018/11/5.
 * comments:
 */

public class LabelStat {

    private String labelName;

    private int quantity;

    public LabelStat(String labelName, int quantity) {
        this.labelName = labelName;
        this.quantity = quantity;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

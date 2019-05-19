package com.bjxapp.worker.model;

import java.util.ArrayList;

/**
 * Created by zhangdan on 2018/11/6.
 * <p>
 * comments:
 */

public class OrderDetailInfo {

    private OrderDes orderDes;

    private MaintainInfo maintainInfo;

    private ArrayList<FollowUpBean> mFollowUpList = new ArrayList<>();

    public OrderDes getOrderDes() {
        return orderDes;
    }

    public void setOrderDes(OrderDes orderDes) {
        this.orderDes = orderDes;
    }

    public MaintainInfo getMaintainInfo() {
        return maintainInfo;
    }

    public void setMaintainInfo(MaintainInfo maintainInfo) {
        this.maintainInfo = maintainInfo;
    }

    public ArrayList<FollowUpBean> getmFollowUpList() {
        return mFollowUpList;
    }

    public void setmFollowUpList(ArrayList<FollowUpBean> mFollowUpList) {
        this.mFollowUpList = mFollowUpList;
    }
}

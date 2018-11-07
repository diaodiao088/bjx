package com.bjxapp.worker.model;

/**
 * Created by zhangdan on 2018/11/6.
 * comments:
 */

public class OrderDetailInfo {

    private OrderDes orderDes;

    private MaintainInfo maintainInfo;

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
}

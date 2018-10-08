package com.bjxapp.worker.logic;

import java.util.List;

import com.bjxapp.worker.model.FirstPageResult;
import com.bjxapp.worker.model.OrderDetail;
import com.bjxapp.worker.model.ReceiveOrder;
import com.bjxapp.worker.model.RedDot;
import com.bjxapp.worker.model.XResult;

/**
 * 主界面逻辑
 * @author jason
 */
public interface IDesktopLogic {
	/**
	 * 获取首页数据
	 */
	public FirstPageResult getFirstPageData();
	
	/**
	 * 获取红点信息
	 */
	public RedDot getRedDots();

	/**
	 * 设置订单接收状态
	 */
	public int setOrderReceiveState(String date, String type, String flag);
	
	/**
	 * 获取历史订单数据
	 */
	public List<ReceiveOrder> getHistoryOrders(int batch, int size);
	
	/**
	 * 获取订单详情数据
	 */
	public OrderDetail getOrderDetail(int orderID);
	
	/**
	 * 保存订单接单状态
	 */
	public int saveOrderReceiveState(int orderID);
	
	/**
	 * 保存订单增项单
	 */
	public int saveOrderAddition(int orderID, String addItem, String addMoney);
	
	/**
	 * 保存订单完成状态
	 */
	public int saveOrderFinishState(int orderID);
	
	/**
	 * 获取订单支付链接
	 */
	public XResult getOrderPayUrl(int orderID);
}

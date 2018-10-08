package com.bjxapp.worker.logic.impl;


import java.util.List;

import android.content.Context;

import com.bjxapp.worker.api.APIFactory;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.logic.IDesktopLogic;
import com.bjxapp.worker.model.FirstPageResult;
import com.bjxapp.worker.model.OrderDetail;
import com.bjxapp.worker.model.ReceiveOrder;
import com.bjxapp.worker.model.RedDot;
import com.bjxapp.worker.model.XResult;

/**
 * 主界面逻辑实现
 * @author jason
 */
public class DesktopLogicImpl implements IDesktopLogic {
	private static DesktopLogicImpl sInstance;
	private Context mContext;
	
	private DesktopLogicImpl(Context context) {
		mContext = context.getApplicationContext();
	}
	
	public static IDesktopLogic getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new DesktopLogicImpl(context);
		}
		
		return sInstance;
	}

	@Override
	public RedDot getRedDots() {
		RedDot redDot = APIFactory.getDesktopAPI(mContext).getRedDots();
		if(redDot == null){
			return null;
		}
		
		ConfigManager.getInstance(mContext).setDesktopMessagesDotServer(redDot.getMessages());
		ConfigManager.getInstance(mContext).setDesktopOrdersDotServer(redDot.getOrders());
		return redDot;
	}

	@Override
	public int setOrderReceiveState(String date, String type, String flag) {
		return APIFactory.getDesktopAPI(mContext).setOrderReceiveState(date, type, flag);
	}

	@Override
	public FirstPageResult getFirstPageData() {
		return APIFactory.getDesktopAPI(mContext).getFirstPageData();
	}

	@Override
	public List<ReceiveOrder> getHistoryOrders(int batch, int size) {
		return APIFactory.getDesktopAPI(mContext).getHistoryOrders(batch, size);
	}

	@Override
	public OrderDetail getOrderDetail(int orderID) {
		return APIFactory.getDesktopAPI(mContext).getOrderDetail(orderID);
	}

	@Override
	public int saveOrderReceiveState(int orderID) {
		return APIFactory.getDesktopAPI(mContext).saveOrderReceiveState(orderID);
	}

	@Override
	public int saveOrderAddition(int orderID, String addItem, String addMoney) {
		return APIFactory.getDesktopAPI(mContext).saveOrderAddition(orderID, addItem, addMoney);
	}

	@Override
	public int saveOrderFinishState(int orderID) {
		return APIFactory.getDesktopAPI(mContext).saveOrderFinishState(orderID);
	}

	@Override
	public XResult getOrderPayUrl(int orderID) {
		return APIFactory.getDesktopAPI(mContext).getOrderPayUrl(orderID);
	}

}

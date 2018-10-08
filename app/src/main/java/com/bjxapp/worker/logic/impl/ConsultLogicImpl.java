package com.bjxapp.worker.logic.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.logic.IConsultLogic;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.CommonConsult;
import com.bjxapp.worker.model.ServiceSubItem;

/**
 * 参照逻辑实现
 * @author jason
 */
public class ConsultLogicImpl implements IConsultLogic {
	private static ConsultLogicImpl sInstance;
	private Context mContext;
	
	private ConsultLogicImpl(Context context) {
		mContext = context.getApplicationContext();
	}
	
	public static IConsultLogic getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new ConsultLogicImpl(context);
		}
		
		return sInstance;
	}

	@Override
	public List<CommonConsult> getConsultData(int type, Map<String, String> params) {
		List<CommonConsult> resultList = null;
		switch (type) {
		case Constant.CONSULT_TYPE_YEARS:
			resultList = getWorkYearsItems();
			break;	
		case Constant.CONSULT_WORK_SORTS:
			resultList = getWorkSortsItems();
			break;	
		}
		return resultList;
	}

	@Override
	public String getConsultTitle(int type) {
		String result = "选择项目";
		switch (type) {
		case Constant.CONSULT_TYPE_YEARS:
			result = "选择工作年限";
			break;
		case Constant.CONSULT_WORK_SORTS:
			result = "选择维修领域";
			break;	
		}
		return result;
	}
	
	private List<CommonConsult> getWorkYearsItems(){
		List<CommonConsult> items = new ArrayList<CommonConsult>();
		for(int i=1;i<51;i++){
			CommonConsult consult = new CommonConsult();
			consult.setCode(String.valueOf(i));
			consult.setName(i+"年");
			items.add(consult);
		}
		
		return items;
	}

	private List<CommonConsult> getWorkSortsItems(){
		List<CommonConsult> items = new ArrayList<CommonConsult>();

		List<ServiceSubItem> serviceSubItems = LogicFactory.getAccountLogic(mContext).getServiceSubItems();
		if(serviceSubItems == null) return null;
		for (ServiceSubItem item : serviceSubItems) {
			CommonConsult consult = new CommonConsult();
			consult.setCode(String.valueOf(item.getId()));
			consult.setName(item.getName());
			items.add(consult);
		}
	
		return items;
	}
	
}

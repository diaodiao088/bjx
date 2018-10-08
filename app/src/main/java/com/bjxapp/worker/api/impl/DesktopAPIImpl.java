package com.bjxapp.worker.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.api.APIUtils;
import com.bjxapp.worker.api.IDesktopAPI;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.model.FirstPageResult;
import com.bjxapp.worker.model.OrderDetail;
import com.bjxapp.worker.model.ReceiveButton;
import com.bjxapp.worker.model.ReceiveOrder;
import com.bjxapp.worker.model.RedDot;
import com.bjxapp.worker.model.XResult;
import com.bjxapp.worker.ui.view.fragment.ctrl.DataManagerCtrl;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.http.HttpUtils;

/**
 * 主界面逻辑实现
 * @author jason
 */
public class DesktopAPIImpl implements IDesktopAPI {
	private static DesktopAPIImpl sInstance;
	private Context mContext;
	
	private DesktopAPIImpl(Context context) {
		mContext = context.getApplicationContext();
	}
	
	public static IDesktopAPI getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new DesktopAPIImpl(context);
		}
		
		return sInstance;
	}

	@Override
	public RedDot getRedDots() {
		Map<String, String> params = getParams(mContext);
		params.put("user_code", ConfigManager.getInstance(mContext).getUserCode());
		params.put("session", ConfigManager.getInstance(mContext).getUserSession());
		
		String result = HttpUtils.getStringByGet(APIConstants.DESKTOP_DOT_GET_URL, params);
		if(!Utils.isNotEmpty(result))
			return null;
		
		try {
			JSONObject json = new JSONObject(result);
			
			int resultCode = 0;
			if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
				resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
				if(resultCode != APIConstants.RESULT_CODE_SUCCESS)
					return null;
			}
			
			if (json.isNull("dots")) {
				return null;
			}

			JSONObject dotJson = json.getJSONObject("dots");
			RedDot redDot = new RedDot();
			
			if (dotJson.isNull("orders"))
				redDot.setOrders(0);
			else
				redDot.setOrders(dotJson.getLong("orders"));
			
			if (dotJson.isNull("messages"))
				redDot.setMessages(0);
			else
				redDot.setMessages(dotJson.getLong("messages"));

			return redDot;	
		}
		catch (JSONException ignore) 
		{
			return null;
		}
	}

	@Override
	public int setOrderReceiveState(String date,String type,String flag) {
		Map<String, String> params = getParams(mContext);
		params.put("user_code", ConfigManager.getInstance(mContext).getUserCode());
		params.put("session", ConfigManager.getInstance(mContext).getUserSession());
		params.put("sign_date", date);
		params.put("sign_type", type);
		params.put("stop_flag", flag);
		
		String result = HttpUtils.getStringByGet(APIConstants.DESKTOP_ORDER_RECEIVE_SETTING, params);
		if(!Utils.isNotEmpty(result))
			return 0;
		
		try {
			JSONObject json = new JSONObject(result);
			
			int resultCode = 0;
			if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
				resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
			}

			return resultCode;
		}
		catch (JSONException ignore) 
		{
			return 0;
		}
	}

	@Override
	public FirstPageResult getFirstPageData() {
		String userCode = ConfigManager.getInstance(mContext).getUserCode();
		String session = ConfigManager.getInstance(mContext).getUserSession();
		
		Map<String, String> params = getParams(mContext);
		params.put("user_code", userCode);
		params.put("session", session);
		
		String result = HttpUtils.getStringByGet(APIConstants.DESKTOP_INDEX_GET, params);

		if(!Utils.isNotEmpty(result))
			return null;
		
		try {
			JSONObject json = new JSONObject(result);
			
			int resultCode = 0;
			if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
				resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
				if(resultCode != APIConstants.RESULT_CODE_SUCCESS)
					return null;
			}
			
			FirstPageResult firstPageResult = new FirstPageResult();
			firstPageResult.setResultCode(resultCode);
			
			if (!json.isNull("person_sign")) {
				JSONArray signJsonArray = json.getJSONArray("person_sign");
				if(signJsonArray.length()>0){
					List<ReceiveButton> buttons = new ArrayList<ReceiveButton>();
					for (int i = 0; i < signJsonArray.length(); i++) {
						JSONObject itemJson = (JSONObject)signJsonArray.get(i);
						ReceiveButton button = new ReceiveButton();
						if (!itemJson.isNull("sign_date")) {
							button.setDate(itemJson.getString("sign_date"));
						}
						if (!itemJson.isNull("sign_type")) {
							button.setType(itemJson.getInt("sign_type"));
						}
						button.setFlag(1);
						buttons.add(button);
					}
					firstPageResult.setSignObject(buttons);
				}
			}

			if (!json.isNull("orders")) {
				JSONArray orderJsonArray = json.getJSONArray("orders");
				if(orderJsonArray.length()>0){
					List<ReceiveOrder> orders = new ArrayList<ReceiveOrder>();
					for (int i = 0; i < orderJsonArray.length(); i++) {
						JSONObject orderJson = (JSONObject)orderJsonArray.get(i);
						ReceiveOrder order = new ReceiveOrder();
						if (!orderJson.isNull("order_id")) {
							order.setOrderID(orderJson.getInt("order_id"));
						}
						if (!orderJson.isNull("node_status")) {
							order.setOrderStatus(orderJson.getInt("node_status"));
						}
						if (!orderJson.isNull("order_date")) {
							order.setOrderDate(orderJson.getString("order_date"));
						}
						if (!orderJson.isNull("order_time")) {
							order.setOrderTime(orderJson.getString("order_time"));
						}
						if (!orderJson.isNull("address")) {
							order.setAddress(orderJson.getString("address"));
						}
						if (!orderJson.isNull("house_number")) {
							order.setHouseNumber(orderJson.getString("house_number"));
						}
						if (!orderJson.isNull("contacts")) {
							order.setContacts(orderJson.getString("contacts"));
						}
						if (!orderJson.isNull("telephone")) {
							order.setTelephone(orderJson.getString("telephone"));
						}
						if (!orderJson.isNull("service_sub_name")) {
							order.setServiceSubName(orderJson.getString("service_sub_name"));
						}
						if (!orderJson.isNull("total_money")) {
							order.setTotalMoney(orderJson.getString("total_money"));
						}
						
						orders.add(order);
					}
					firstPageResult.setOrderObject(orders);
				}
			}

			return firstPageResult;
			
		}
		catch (JSONException ignore) 
		{
			return null;
		}
	}
	

	@Override
	public List<ReceiveOrder> getHistoryOrders(int batch, int size) {
		String userCode = ConfigManager.getInstance(mContext).getUserCode();
		String session = ConfigManager.getInstance(mContext).getUserSession();
		
		Map<String, String> params = getParams(mContext);
		params.put("user_code", userCode);
		params.put("session", session);
		params.put("page_index", String.valueOf(batch));
		params.put("page_size", String.valueOf(size));
		
		
		String result = HttpUtils.getStringByGet(APIConstants.DESKTOP_ORDER_LIST_GET, params);
		if(!Utils.isNotEmpty(result))
			return null;
		
		try {
			JSONObject json = new JSONObject(result);
			
			int resultCode = 0;
			if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
				resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
				if(resultCode != APIConstants.RESULT_CODE_SUCCESS)
					return null;
			}
			
			List<ReceiveOrder> orders = null;

			if (!json.isNull("orders")) {
				JSONArray orderJsonArray = json.getJSONArray("orders");
				if(orderJsonArray.length()>0){
					orders = new ArrayList<ReceiveOrder>();
					for (int i = 0; i < orderJsonArray.length(); i++) {
						JSONObject orderJson = (JSONObject)orderJsonArray.get(i);
						ReceiveOrder order = new ReceiveOrder();
						if (!orderJson.isNull("order_id")) {
							order.setOrderID(orderJson.getInt("order_id"));
						}
						if (!orderJson.isNull("node_status")) {
							order.setOrderStatus(orderJson.getInt("node_status"));
						}
						if (!orderJson.isNull("order_date")) {
							order.setOrderDate(orderJson.getString("order_date"));
						}
						if (!orderJson.isNull("order_time")) {
							order.setOrderTime(orderJson.getString("order_time"));
						}
						if (!orderJson.isNull("address")) {
							order.setAddress(orderJson.getString("address"));
						}
						if (!orderJson.isNull("house_number")) {
							order.setHouseNumber(orderJson.getString("house_number"));
						}
						if (!orderJson.isNull("contacts")) {
							order.setContacts(orderJson.getString("contacts"));
						}
						if (!orderJson.isNull("telephone")) {
							order.setTelephone(orderJson.getString("telephone"));
						}
						if (!orderJson.isNull("service_sub_name")) {
							order.setServiceSubName(orderJson.getString("service_sub_name"));
						}
						if (!orderJson.isNull("total_money")) {
							order.setTotalMoney(orderJson.getString("total_money"));
						}
						
						orders.add(order);
					}
				}
			}

			return orders;
			
		}
		catch (JSONException ignore) 
		{
			return null;
		}
	}
	
	@Override
	public OrderDetail getOrderDetail(int orderID) {
		String userCode = ConfigManager.getInstance(mContext).getUserCode();
		String session = ConfigManager.getInstance(mContext).getUserSession();
		
		Map<String, String> params = getParams(mContext);
		params.put("user_code", userCode);
		params.put("session", session);
		params.put("order_id", String.valueOf(orderID));
		
		
		String result = HttpUtils.getStringByGet(APIConstants.DESKTOP_ORDER_DETAIL_GET, params);
		if(!Utils.isNotEmpty(result))
			return null;
		
		try {
			JSONObject json = new JSONObject(result);
			
			int resultCode = 0;
			if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
				resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
				if(resultCode != APIConstants.RESULT_CODE_SUCCESS)
					return null;
			}
			
			OrderDetail order = null;

			if (!json.isNull("order")) {
				JSONObject orderJson = json.getJSONObject("order");
				order = new OrderDetail();
				if (!orderJson.isNull("order_id")) {
					order.setOrderID(orderJson.getInt("order_id"));
				}
				if (!orderJson.isNull("order_code")) {
					order.setOrderCode(orderJson.getString("order_code"));
				}
				if (!orderJson.isNull("node_status")) {
					order.setOrderStatus(orderJson.getInt("node_status"));
				}
				if (!orderJson.isNull("order_type")) {
					order.setOrderType(orderJson.getInt("order_type"));
				}
				if (!orderJson.isNull("order_date")) {
					order.setOrderDate(orderJson.getString("order_date"));
				}
				if (!orderJson.isNull("order_time")) {
					order.setOrderTime(orderJson.getString("order_time"));
				}
				if (!orderJson.isNull("address")) {
					order.setAddress(orderJson.getString("address"));
				}
				if (!orderJson.isNull("house_number")) {
					order.setHouseNumber(orderJson.getString("house_number"));
				}
				if (!orderJson.isNull("contacts")) {
					order.setContacts(orderJson.getString("contacts"));
				}
				if (!orderJson.isNull("telephone")) {
					order.setTelephone(orderJson.getString("telephone"));
				}
				if (!orderJson.isNull("service_sub_name")) {
					order.setServiceSubName(orderJson.getString("service_sub_name"));
				}
				if (!orderJson.isNull("remark")) {
					order.setRemark(orderJson.getString("remark"));
				}
				if (!orderJson.isNull("image_url_one")) {
					order.setImageOne(orderJson.getString("image_url_one"));
				}
				if (!orderJson.isNull("image_url_two")) {
					order.setImageTwo(orderJson.getString("image_url_two"));
				}
				if (!orderJson.isNull("image_url_three")) {
					order.setImageThree(orderJson.getString("image_url_three"));
				}
				if (!orderJson.isNull("add_item")) {
					order.setAddItem(orderJson.getString("add_item"));
				}
				if (!orderJson.isNull("add_money")) {
					order.setAddMoney(orderJson.getString("add_money"));
				}
				if (!orderJson.isNull("home_money")) {
					order.setHomeMoney(orderJson.getString("home_money"));
				}
				if (!orderJson.isNull("fast_money")) {
					order.setFastMoney(orderJson.getString("fast_money"));
				}
				if (!orderJson.isNull("discount_money")) {
					order.setDiscountMoney(orderJson.getString("discount_money"));
				}
				if (!orderJson.isNull("total_money")) {
					order.setTotalMoney(orderJson.getString("total_money"));
				}
			}

			return order;
		}
		catch (JSONException ignore) 
		{
			return null;
		}
	}
	
	@Override
	public int saveOrderReceiveState(int orderID) {
		Map<String, String> params = getParams(mContext);
		params.put("user_code", ConfigManager.getInstance(mContext).getUserCode());
		params.put("session", ConfigManager.getInstance(mContext).getUserSession());
		params.put("order_id", String.valueOf(orderID));
		
		String result = HttpUtils.getStringByGet(APIConstants.DESKTOP_ORDER_RECEIVE_SAVE, params);
		if(!Utils.isNotEmpty(result))
			return 0;
		
		try {
			JSONObject json = new JSONObject(result);
			
			int resultCode = 0;
			if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
				resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
			}

			return resultCode;
		}
		catch (JSONException ignore) 
		{
			return 0;
		}
	}

	@Override
	public int saveOrderAddition(int orderID, String addItem, String addMoney) {
		Map<String, String> params = getParams(mContext);
		params.put("user_code", ConfigManager.getInstance(mContext).getUserCode());
		params.put("session", ConfigManager.getInstance(mContext).getUserSession());
		params.put("order_id", String.valueOf(orderID));
		params.put("add_item", addItem);
		params.put("add_money", addMoney);
		
		String result = HttpUtils.getStringByGet(APIConstants.DESKTOP_ORDER_ADDITION_SAVE, params);
		if(!Utils.isNotEmpty(result))
			return 0;
		
		try {
			JSONObject json = new JSONObject(result);
			
			int resultCode = 0;
			if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
				resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
			}

			return resultCode;
		}
		catch (JSONException ignore) 
		{
			return 0;
		}
	}

	@Override
	public int saveOrderFinishState(int orderID) {
		Map<String, String> params = getParams(mContext);
		params.put("user_code", ConfigManager.getInstance(mContext).getUserCode());
		params.put("session", ConfigManager.getInstance(mContext).getUserSession());
		params.put("order_id", String.valueOf(orderID));
		
		String result = HttpUtils.getStringByGet(APIConstants.DESKTOP_ORDER_FINISH_SAVE, params);
		if(!Utils.isNotEmpty(result))
			return 0;
		
		try {
			JSONObject json = new JSONObject(result);
			
			int resultCode = 0;
			if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
				resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
			}

			return resultCode;
		}
		catch (JSONException ignore) 
		{
			return 0;
		}
	}
	
	@Override
	public XResult getOrderPayUrl(int orderID) {
		Map<String, String> params = getParams(mContext);
		params.put("user_code", ConfigManager.getInstance(mContext).getUserCode());
		params.put("session", ConfigManager.getInstance(mContext).getUserSession());
		params.put("order_id", String.valueOf(orderID));
		
		String result = HttpUtils.getStringByGet(APIConstants.DESKTOP_ORDER_PAY_URL_GET, params);
		if(!Utils.isNotEmpty(result))
			return null;
		
		try {
			JSONObject json = new JSONObject(result);
			
			int resultCode = 0;
			if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
				resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
			}
			
			String url = "";
			if (!json.isNull("url")) {
				url = json.getString("url");
			}
			
			XResult xResult = new XResult();
			xResult.setResultCode(resultCode);
			xResult.setDataObject(url);
			
			return xResult;
			
		}
		catch (JSONException ignore) 
		{
			return null;
		}
	}
	
	/**
	 * 组装请求参数
	 * @param context
	 * @return 请求参数键值对
	 */
	private static Map<String, String> getParams(Context context) {
		Map<String, String> params = APIUtils.getBasicParams(context);
		return params;
	}
	
}

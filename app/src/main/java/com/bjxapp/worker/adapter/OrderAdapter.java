package com.bjxapp.worker.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.model.ReceiveOrder;
import com.bjxapp.worker.R;

public class OrderAdapter extends BaseAdapter{

    private LayoutInflater mInflater;
    private ArrayList<ReceiveOrder> aInfo;
	
	public OrderAdapter(Context context,ArrayList<ReceiveOrder> info) 
	{
		mInflater = LayoutInflater.from(context);
		aInfo=info;
	}

	public void setReceiverInfo(ArrayList<ReceiveOrder> list){
		if (list == null || list.isEmpty()){
			return;
		}
		this.aInfo = list;
	}

    public int getCount() {
        return aInfo.size();
    }

    public Object getItem(int position) {
        return aInfo.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		
		if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_order_item, null);

            holder = new ViewHolder();
            holder.textViewService = (XTextView) convertView.findViewById(R.id.order_receive_textview_service);
            holder.textViewStatus = (XTextView) convertView.findViewById(R.id.order_receive_textview_status);
            holder.textViewOrderDate = (XTextView) convertView.findViewById(R.id.order_receive_textview_orderdate);
            holder.textViewAddress = (XTextView) convertView.findViewById(R.id.order_receive_textview_address);
            holder.textViewContact = (XTextView) convertView.findViewById(R.id.order_receive_textview_contact);
            holder.textViewMoney = (XTextView) convertView.findViewById(R.id.order_receive_textview_money);
            
            convertView.setTag(holder);
        } 
        else 
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textViewService.setText(aInfo.get(position).getServiceSubName());    
        holder.textViewOrderDate.setText(aInfo.get(position).getOrderDate() + " " + aInfo.get(position).getOrderTime());
        holder.textViewAddress.setText(aInfo.get(position).getAddress()+aInfo.get(position).getHouseNumber());
        holder.textViewContact.setText(aInfo.get(position).getContacts() + " / " + aInfo.get(position).getTelephone());
        
        String statusString = "";
        String feeInfo = "";
        switch (aInfo.get(position).getOrderStatus()) {
		case 0:
			statusString = "新订单";
			feeInfo = "费用预估：";
			break;
		case 1:
			statusString = "已接单";
			feeInfo = "费用：";
			break;
		case 2:
			statusString = "待支付";
			feeInfo = "费用：";
			break;
		case 3:
			statusString = "已结算";
			feeInfo = "费用：";
			break;
		case 4:
			statusString = "已结算";
			feeInfo = "费用：";
			break;
		case 98:
			statusString = "已取消";
			feeInfo = "费用：";
			break;
		case 99:
			statusString = "异常";
			feeInfo = "费用：";
			break;	
		default:
			break;
		}
        holder.textViewMoney.setText(feeInfo + aInfo.get(position).getTotalMoney() + "元");
        holder.textViewStatus.setText(statusString);
        
		return convertView;
	}

    class ViewHolder {
    	XTextView textViewService;
    	XTextView textViewStatus;
    	XTextView textViewOrderDate;
    	XTextView textViewAddress;
    	XTextView textViewContact;
    	XTextView textViewMoney;
    }
}

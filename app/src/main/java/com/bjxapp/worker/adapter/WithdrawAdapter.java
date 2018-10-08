package com.bjxapp.worker.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.model.WithdrawInfo;
import com.bjxapp.worker.R;

public class WithdrawAdapter extends BaseAdapter{

    private LayoutInflater mInflater;
    private ArrayList<WithdrawInfo> aInfo;
	
	public WithdrawAdapter(Context context,ArrayList<WithdrawInfo> info) 
	{
		mInflater = LayoutInflater.from(context);
		aInfo=info;
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
            convertView = mInflater.inflate(R.layout.activity_balance_withdraw_item, null);

            holder = new ViewHolder();
            holder.textViewStatus = (XTextView) convertView.findViewById(R.id.balance_withdraw_item_status);
            holder.textViewDate = (XTextView) convertView.findViewById(R.id.balance_withdraw_item_date);
            holder.textViewMoney = (XTextView) convertView.findViewById(R.id.balance_withdraw_item_money_edit);
            
            convertView.setTag(holder);
        } 
        else 
        {
            holder = (ViewHolder) convertView.getTag();
        }
		
        String statusString = "";
        switch (aInfo.get(position).getStatus()) {
		case 0:
			statusString = "待处理";
			break;
		case 1:
			statusString = "提现成功";
			break;
		case 2:
			statusString = "提现失败";
			break;	
		default:
			break;
		}
        
        holder.textViewDate.setText(getFormatDateString(aInfo.get(position).getDate()));
        holder.textViewMoney.setText(aInfo.get(position).getMoney() + "元");
        holder.textViewStatus.setText(statusString);
        
		return convertView;
	}
	
	public String getFormatDateString(String dateString) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		DateFormat toFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		Date date = null;
		try {
			date = format.parse(dateString);
			return toFormat.format(date);
		} 
		catch (Exception e) {
			return dateString;
		}
	}

    class ViewHolder {
    	XTextView textViewStatus;
    	XTextView textViewDate;
    	XTextView textViewMoney;
    }
}

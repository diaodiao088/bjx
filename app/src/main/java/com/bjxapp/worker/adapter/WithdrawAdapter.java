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
import android.widget.TextView;

import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.model.WithdrawInfo;
import com.bjxapp.worker.R;

public class WithdrawAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<WithdrawInfo> aInfo;

    public WithdrawAdapter(Context context, ArrayList<WithdrawInfo> info) {
        mInflater = LayoutInflater.from(context);
        aInfo = info;
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
            holder.textViewStatus = (TextView) convertView.findViewById(R.id.withdraw_history_content);
            holder.textViewMoney = (TextView) convertView.findViewById(R.id.withdraw_history_money);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textViewMoney.setText((aInfo.get(position).getStatus() == 1 ? "+" : "-") + aInfo.get(position).getMoney());

        String statusText = "";
        if (aInfo.get(position).getStatus() == 1){
            statusText = "于 [" + getFormatDateString(aInfo.get(position).getDate()) + "] 提现" + aInfo.get(position).getMoney() + "成功";
        }else{
            statusText = "于 [" + getFormatDateString(aInfo.get(position).getDate()) + "] 提现" + aInfo.get(position).getMoney() + "失败，请联系客服！";
        }

        holder.textViewStatus.setText(statusText);

        return convertView;
    }

    public String getFormatDateString(String dateString) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            return format.format(Double.parseDouble(dateString));
        } catch (Exception e) {
            return dateString;
        }
    }

    class ViewHolder {
        TextView textViewStatus;
        TextView textViewMoney;
    }
}

package com.bjxapp.worker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.db.BjxInfo;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

;

public class PushBillAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<BjxInfo> aInfo;

    public PushBillAdapter(Context context, ArrayList<BjxInfo> info) {
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
            convertView = mInflater.inflate(R.layout.item_notification_bill, null);

            holder = new ViewHolder();
            holder.dateTv = (TextView) convertView.findViewById(R.id.push_date);
            holder.titleTv = (TextView) convertView.findViewById(R.id.title);
            holder.contentTv = convertView.findViewById(R.id.content);

            holder.redotView = convertView.findViewById(R.id.redot);
            holder.mLookTv = convertView.findViewById(R.id.look_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BjxInfo item = aInfo.get(position);

        holder.dateTv.setText(getFormatDateString(item.getCreateTime()));
        holder.contentTv.setText(item.getContent());
        holder.titleTv.setText(item.getTitle());

        if (item.isRead()){
            holder.redotView.setVisibility(View.INVISIBLE);
        }else{
            holder.redotView.setVisibility(View.VISIBLE);
        }


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
        TextView dateTv;
        TextView mLookTv;
        View redotView;
        TextView titleTv;
        TextView contentTv;
    }
}

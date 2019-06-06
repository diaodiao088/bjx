package com.bjxapp.worker.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjxapp.worker.model.Message;
import com.bjx.master.R;;

public class MessageAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Message> aInfo;

    public MessageAdapter(Context context, ArrayList<Message> info) {
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
            convertView = mInflater.inflate(R.layout.item_notification, null);
            holder = new ViewHolder();
            holder.mTitleTv = convertView.findViewById(R.id.title);
            holder.mContentTv = convertView.findViewById(R.id.content);
            holder.mRedotView = convertView.findViewById(R.id.redot);
            holder.mArrowIv = convertView.findViewById(R.id.arrow);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mContentTv.setText(getFormatDateString(aInfo.get(position).getDate()));
        holder.mTitleTv.setText(aInfo.get(position).getTitle());

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
        View mRedotView;
        TextView mTitleTv;
        TextView mContentTv;
        ImageView mArrowIv;

    }
}

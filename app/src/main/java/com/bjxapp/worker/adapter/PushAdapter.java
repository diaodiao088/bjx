package com.bjxapp.worker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bjxapp.worker.R;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.db.BjxInfo;
import com.bjxapp.worker.model.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PushAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<BjxInfo> aInfo;

    public PushAdapter(Context context, ArrayList<BjxInfo> info) {
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
            convertView = mInflater.inflate(R.layout.push_message_list, null);

            holder = new ViewHolder();
            holder.dateTv = (XTextView) convertView.findViewById(R.id.push_date);
            holder.titleTv = (XTextView) convertView.findViewById(R.id.push_tv);
            holder.contentTv = convertView.findViewById(R.id.push_content);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //  holder.textViewDate.setText(getFormatDateString(aInfo.get(position).getDate()));

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
        XTextView dateTv;
        XTextView titleTv;
        XTextView contentTv;
    }
}

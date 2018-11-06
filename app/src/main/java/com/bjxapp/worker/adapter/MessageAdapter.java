package com.bjxapp.worker.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.model.Message;
import com.bjxapp.worker.R;

public class MessageAdapter extends BaseAdapter{

    private LayoutInflater mInflater;
    private ArrayList<Message> aInfo;
	
	public MessageAdapter(Context context,ArrayList<Message> info) 
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
            convertView = mInflater.inflate(R.layout.activity_message_list, null);

            holder = new ViewHolder();
            holder.textViewDate = (XTextView) convertView.findViewById(R.id.message_textview_date);
            holder.textViewTitle = (XTextView) convertView.findViewById(R.id.message_textview_title);
                            
            convertView.setTag(holder);
        } 
        else 
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textViewDate.setText(getFormatDateString(aInfo.get(position).getDate()));
        holder.textViewTitle.setText(aInfo.get(position).getTitle());    
		
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
    	XTextView textViewDate;
    	XTextView textViewTitle;
    }
}

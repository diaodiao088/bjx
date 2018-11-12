package com.bjxapp.worker.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.model.CCheckBox;
import com.bjx.master.R;;

public class CommonCheckBoxAdapter extends BaseAdapter{

    private LayoutInflater mInflater;
    private ArrayList<CCheckBox> aInfo;
	
	public CommonCheckBoxAdapter(Context context,ArrayList<CCheckBox> info) 
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
            convertView = mInflater.inflate(R.layout.layout_common_checkbox_detail, null);

            holder = new ViewHolder();
            holder.textView = (XTextView) convertView.findViewById(R.id.common_checkbox_detail_text);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.common_checkbox_detail_check);
                            
            convertView.setTag(holder);
        } 
        else 
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(aInfo.get(position).getName());
        if(aInfo.get(position).getCheck() == 0){
        	holder.checkBox.setChecked(false);
        }
        else{
        	holder.checkBox.setChecked(true);
        }    
		
		return convertView;
	}

    class ViewHolder {
    	XTextView textView;
    	CheckBox checkBox;
    }
}

package com.bjxapp.worker.ui.view.activity.search;

import java.util.List;

import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SearchSingleAdapter extends BaseAdapter{
	private List<SearchSingleModel> list = null;
	private Context mContext;
	
	public SearchSingleAdapter(Context mContext, List<SearchSingleModel> list) {
		this.mContext = mContext;
		this.list = list;
	}

    /** 
     * 当ListView数据发生变化时,调用此方法来更新ListView 
     * @param list 
     */
	public void updateListView(List<SearchSingleModel> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;

		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.layout_search_single_common_item, null);
			viewHolder.tvTitle = (XTextView) view.findViewById(R.id.search_simple_common_item_title);
			view.setTag(viewHolder);
		} 
		else {
			viewHolder = (ViewHolder) view.getTag();
		}
	
		viewHolder.tvTitle.setText(this.list.get(position).getName());
		viewHolder.tvTitle.setTag(this.list.get(position).getCode());
		
		return view;

	}
	
	final static class ViewHolder {
		XTextView tvTitle;
	}
}
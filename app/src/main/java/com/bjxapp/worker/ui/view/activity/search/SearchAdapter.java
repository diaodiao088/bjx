package com.bjxapp.worker.ui.view.activity.search;

import java.util.List;

import com.bjxapp.worker.controls.XCheckBox;
import com.bjxapp.worker.controls.XTextView;
import com.bjx.master.R;;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;

public class SearchAdapter extends BaseAdapter implements SectionIndexer{
	private List<SearchModel> list = null;
	private Context mContext;
	
	public SearchAdapter(Context mContext, List<SearchModel> list) {
		this.mContext = mContext;
		this.list = list;
	}

    /** 
     * 当ListView数据发生变化时,调用此方法来更新ListView 
     * @param list 
     */
	public void updateListView(List<SearchModel> list){
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
		final SearchModel mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.layout_search_common_item, null);
			viewHolder.tvTitle = (XTextView) view.findViewById(R.id.search_common_item_title);
			viewHolder.tvLetter = (XTextView) view.findViewById(R.id.search_common_item_catalog);
			viewHolder.checkBox = (XCheckBox) view.findViewById(R.id.search_common_item_check);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		//根据position获取分类的首字母的char ascii值 
		int section = getSectionForPosition(position);
		
		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现 
		if(position == getPositionForSection(section)){
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		}else{
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
	
		viewHolder.tvTitle.setText(this.list.get(position).getName());
		
        if(list.get(position).getCheck() == 0){
        	viewHolder.checkBox.setChecked(false);
        }
        else{
        	viewHolder.checkBox.setChecked(true);
        } 
		
		return view;

	}
	
	final static class ViewHolder {
		XTextView tvLetter;
		XTextView tvTitle;
		XCheckBox checkBox;
	}


	 /** 
     * 根据ListView的当前位置获取分类的首字母的char ascii值 
     */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

    /** 
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置 
     */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}
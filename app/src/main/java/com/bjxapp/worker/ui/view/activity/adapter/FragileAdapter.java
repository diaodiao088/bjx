package com.bjxapp.worker.ui.view.activity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bjx.master.R;
import com.bjxapp.worker.ui.view.activity.bean.FragileBean;

import java.util.ArrayList;

public class FragileAdapter extends RecyclerView.Adapter<FragileHolder> {

    private ArrayList<FragileBean> mItemList = new ArrayList<>();

    @Override
    public FragileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragile_item_layout, parent, false);
        FragileHolder holder = new FragileHolder(view);
        holder.setOnItemClickListener(mClickListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(FragileHolder holder, int position) {
        holder.setData(mItemList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public void setItems(ArrayList<FragileBean> itemList) {
        this.mItemList = itemList;
        notifyDataSetChanged();
    }

    public FragileBean getSpecFragBean(int position){
        return mItemList.get(position);
    }

    public interface OnItemClickListener {

        void onItemDelete(int position);

        void addImage(int position);

    }

    public OnItemClickListener mClickListener;

    public void setListener(OnItemClickListener listener) {
        this.mClickListener = listener;
    }


}

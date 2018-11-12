package com.bjxapp.worker.ui.view.activity.map.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.search.sug.SuggestionResult;
import com.bjx.master.R;;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangdan on 2018/10/26.
 * comments:
 */

public class MapSearchAdapter extends RecyclerView.Adapter<MapSearchAdapter.VH> {

    private ArrayList<SuggestionResult.SuggestionInfo> mList = new ArrayList<>();

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map, parent, false);

        return new VH(view);
    }

    public void setDatas(List<SuggestionResult.SuggestionInfo> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(final VH holder, int position) {

        SuggestionResult.SuggestionInfo info = mList.get(position);

        holder.getmShortName().setText(info.getAddress());

        holder.getmLongName().setText(info.getCity() + info.district + info.key);

        holder.getmRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    int position = holder.getAdapterPosition();
                    mListener.onItemClick(v, holder, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class VH extends RecyclerView.ViewHolder {

        private TextView mShortName;
        private TextView mLongName;

        private View mRootView;

        public VH(View itemView) {
            super(itemView);
            this.mShortName = itemView.findViewById(R.id.im_bigtv);
            this.mLongName = itemView.findViewById(R.id.im_migtv);
            this.mRootView = itemView;
        }

        public View getmRootView() {
            return mRootView;
        }

        public void setmRootView(View mRootView) {
            this.mRootView = mRootView;
        }

        public TextView getmShortName() {
            return mShortName;
        }

        public void setmShortName(TextView mShortName) {
            this.mShortName = mShortName;
        }

        public TextView getmLongName() {
            return mLongName;
        }

        public void setmLongName(TextView mLongName) {
            this.mLongName = mLongName;
        }
    }

    public SuggestionResult.SuggestionInfo getItem(int position) {

        if (mList == null || mList.isEmpty()) {
            return null;
        }

        return mList.get(position);
    }

    public OnItemClickListener mListener;

    public void setItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);

    }


}

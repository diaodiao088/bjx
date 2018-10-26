package com.bjxapp.worker.ui.view.activity.map.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.bjxapp.worker.App;
import com.bjxapp.worker.R;
import com.bjxapp.worker.ui.view.activity.widget.dialog.ICFunSimpleAlertDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xz on 2017/8/9 0009.
 * 地图 地址列表 适配器
 *
 * @author xz
 */

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.VH> {
    private int mIndexTag = 0;
    private PoiInfo mUserPoiInfo;

    private ArrayList<PoiInfo> mList = new ArrayList<>();

    public int getmIndexTag() {
        return mIndexTag;
    }

    public void setmUserPoiInfo(PoiInfo userPoiInfo) {
        this.mUserPoiInfo = userPoiInfo;
    }

    public void setmIndexTag(int mIndexTag) {
        this.mIndexTag = mIndexTag;
        notifyDataSetChanged();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map, parent, false);

        return new VH(view);
    }

    @Override
    public void onBindViewHolder(final VH holder, int position) {
        PoiInfo poiInfo = mList.get(position);

        holder.getmLongName().setText(poiInfo.address);
        holder.getmShortName().setText(poiInfo.name);

        holder.getmRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    int position = holder.getAdapterPosition();
                    mListener.onItemClick(v , holder , position);
                }
            }
        });

        if (mIndexTag == position) {
            holder.getmLongName().setTextColor(ContextCompat.getColor(App.getInstance(), R.color.app_sub_color));
            holder.getmShortName().setTextColor(ContextCompat.getColor(App.getInstance(), R.color.app_sub_color));
        } else {
            holder.getmShortName().setTextColor(ContextCompat.getColor(App.getInstance(), R.color.app_txt_black));
            holder.getmLongName().setTextColor(ContextCompat.getColor(App.getInstance(), R.color.app_txt_gray_light));
        }
    }

    /**
     * 重写此方法，每次更新数据后，item为第一个
     *
     * @param datas     数据
     * @param isRefresh 是否刷新
     */
    public void setDatas(List<PoiInfo> datas, boolean isRefresh) {

        if (datas == null){
            datas = new ArrayList<>();
        }

        mList.clear();
        if (mUserPoiInfo != null && datas != null) {
            datas.add(0, mUserPoiInfo);
        }
        mList.addAll(datas);
        mIndexTag = 0;
        notifyDataSetChanged();
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

    public PoiInfo getItem(int position){

        if (mList == null || mList.isEmpty()){
            return null;
        }

        return mList.get(position);
    }


    public OnItemClickListener mListener;

    public void setItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);

    }

}

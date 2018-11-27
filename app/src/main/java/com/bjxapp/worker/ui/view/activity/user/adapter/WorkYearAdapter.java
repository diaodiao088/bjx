package com.bjxapp.worker.ui.view.activity.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.ui.view.activity.bean.WorkYearDataBean;

import java.util.List;


public class WorkYearAdapter extends BaseAdapter {

    private Context mContext;
    private List<WorkYearDataBean> list;
    private ClickCallback mClickCallback;
    int currentSelectCommuType = 0;

    public WorkYearAdapter(Context context) {
        mContext = context;
    }

    public void setFeedBackDataList(List<WorkYearDataBean> dataList) {
        this.list = dataList;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public WorkYearDataBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder mViewHolder = null;
        WorkYearDataBean data = list.get(position);
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.feedback_list_view, parent, false);
            mViewHolder.tv = (TextView) convertView.findViewById(R.id.tv);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.tv.setText(data.getmContextTv());

        final ImageView mIvItemBtn = (ImageView) convertView.findViewById(R.id.iv);

        if (data.isChecked()) {
            mIvItemBtn.setImageResource(R.drawable.work_year_checked);
        } else {
            mIvItemBtn.setImageResource(R.drawable.work_year_unchecked);
        }

        mIvItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIvItemBtn.setImageResource(R.drawable.work_year_checked);
                setCheckChanged(position);
                if (mClickCallback != null) {
                    mClickCallback.callBack(position);
                }
                notifyDataSetChanged();
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIvItemBtn.setImageResource(R.drawable.work_year_checked);
                setCheckChanged(position);
                if (mClickCallback != null)
                    mClickCallback.callBack(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView tv;
    }

    public WorkYearDataBean getCheckedFeedBackBean() {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isChecked) {
                    return list.get(i);
                }
            }
        }
        return null;
    }

    public void setCheckChanged(int position) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (i == position) {
                    list.get(i).isChecked = true;
                } else {
                    list.get(i).isChecked = false;
                }
            }
        }
    }

    private String getStringByStringId(int stringid) {
        String str = "";
        try {
            str = mContext.getResources().getString(stringid);
        } catch (Exception e) {

        }
        return str;
    }

    public void setClickCallBack(ClickCallback clickCallback) {
        mClickCallback = clickCallback;
    }

    public interface ClickCallback {
        public void callBack(int position);
    }
}

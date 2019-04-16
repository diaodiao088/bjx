package com.bjxapp.worker.ui.view.activity.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.ui.view.activity.widget.MaxHeightRecyclerView;

import java.util.ArrayList;

/**
 * Created by zhangdan on 2018/11/7.
 * comments:
 */

public class ManfulDialog {

    private Context mCtx;

    private CustomLayoutDialog mDialog = null;

    private View mRootView;

    private MyAdapter myAdapter;

    private MaxHeightRecyclerView mRecyclerView;

    private ArrayList<String> mStringList = new ArrayList<>();

    public ManfulDialog(Context ctx) {
        this.mCtx = ctx;
        mDialog = new CustomLayoutDialog(ctx, R.layout.manuful_dialog);
        initView();
    }

    private void initView() {
        mRootView = mDialog.getView();
        if (mRootView != null) {
            mRecyclerView = mRootView.findViewById(R.id.recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mCtx));
            myAdapter = new MyAdapter();
            mRecyclerView.setAdapter(myAdapter);
        }
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public boolean isShow() {
        if (mDialog == null)
            return false;
        return mDialog.isShowing();
    }

    public void setData(ArrayList<String> data){
        this.mStringList = data;
        myAdapter.notifyDataSetChanged();
    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manful_item_layout , parent ,false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            String name = mStringList.get(position);
            holder.setData(name , position);
        }

        @Override
        public int getItemCount() {
            return mStringList.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView mNameTv;

        private View mRootView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.mRootView = itemView;
            mNameTv = mRootView.findViewById(R.id.manful_reason_tv);
        }

        public void setData(final String name , final int position){
            this.mNameTv.setText(name);

            this.mNameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null){
                        mClickListener.onClick(name , position);
                    }
                }
            });
        }
    }

    public interface OnManClickListener{

        void onClick(String name , int index);

    }

    private OnManClickListener mClickListener;


    public void setClickListener(OnManClickListener listener){
        this.mClickListener = listener;
    }

}

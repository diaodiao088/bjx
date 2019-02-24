package com.bjxapp.worker.ui.view.activity.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.ui.view.activity.bean.FragileBean;
import com.bjxapp.worker.ui.view.activity.widget.SpaceItemDecoration;
import com.bjxapp.worker.ui.widget.RoundImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FragileHolder extends RecyclerView.ViewHolder {

    private RecyclerView mRecyclerView;

    private EditText mEditText;

    private FragileBean mFragBean;

    private TextView mDeleteTv;

    private MyAdapter myAdapter;

    private FragileAdapter.OnItemClickListener mOnItemClickListener;

    public FragileHolder(View itemView) {
        super(itemView);
        mEditText = itemView.findViewById(R.id.frag_name_tv);
        mRecyclerView = itemView.findViewById(R.id.fragile_item_recycler);
        mDeleteTv = itemView.findViewById(R.id.delete_fragile_tv);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(itemView.getContext(), 4);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(4, 50, true));
        myAdapter = new MyAdapter();
        mRecyclerView.setAdapter(myAdapter);
    }

    public void setData(FragileBean fragileBean, final int position) {
        this.mFragBean = fragileBean;
        if (TextUtils.isEmpty(fragileBean.getFragileName())) {
            mEditText.setText(fragileBean.getFragileName());
        }

        mDeleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemDelete(position);
                }
            }
        });

        myAdapter.setList(fragileBean.getImageList());

    }


    private class MyAdapter extends RecyclerView.Adapter {

        private ArrayList<FragileBean.ImageBean> mList = new ArrayList<>();

        public MyAdapter() {

        }

        public void setList(ArrayList<FragileBean.ImageBean> imageBean) {
            this.mList = imageBean;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder mHolder = null;

            if (viewType == FragileBean.ImageBean.TYPE_ADD) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_image_item, parent, false);
                mHolder = new VH_IMAGE_ITEM(view);
            } else if (viewType == FragileBean.ImageBean.TYPE_IMAGE) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_image_item_cl, parent, false);
                mHolder = new VH_DELETE_ITEM(view);
            }

            return mHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            final FragileBean.ImageBean bean = mList.get(position);

            if (holder instanceof VH_IMAGE_ITEM) {

                Glide.with(mRecyclerView.getContext()).load(bean.getUrl()).into(((VH_IMAGE_ITEM) holder).mIv);

                ((VH_IMAGE_ITEM) holder).mDeleteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteImage(position);
                    }
                });

                ((VH_IMAGE_ITEM) holder).mIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            } else if (holder instanceof VH_DELETE_ITEM) {

                if (mList.size() >= 5) {
                    holder.itemView.setVisibility(View.GONE);
                } else {
                    holder.itemView.setVisibility(View.VISIBLE);
                }

                ((VH_DELETE_ITEM) holder).mDeleteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.addImage(position);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mList.get(position).getType();
        }

        public void deleteImage(int position) {
            mList.remove(position);
            notifyDataSetChanged();
        }
    }

    private class VH_IMAGE_ITEM extends RecyclerView.ViewHolder {

        private RoundImageView mIv;
        private ImageView mDeleteIv;

        public VH_IMAGE_ITEM(View itemView) {
            super(itemView);
            mDeleteIv = itemView.findViewById(R.id.deleteImageView);
            mIv = itemView.findViewById(R.id.screenShotImageView);
        }
    }

    private class VH_DELETE_ITEM extends RecyclerView.ViewHolder {

        private ImageView mDeleteIv;

        public VH_DELETE_ITEM(View itemView) {
            super(itemView);
            mDeleteIv = itemView.findViewById(R.id.screenShotImageView);
        }
    }

    public void setOnItemClickListener(FragileAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


}
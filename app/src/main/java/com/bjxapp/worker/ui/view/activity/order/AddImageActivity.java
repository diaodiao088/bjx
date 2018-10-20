package com.bjxapp.worker.ui.view.activity.order;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bjxapp.worker.App;
import com.bjxapp.worker.R;
import com.bjxapp.worker.ui.view.activity.widget.SpaceItemDecoration;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.RoundImageView;
import com.bjxapp.worker.utils.UploadFile;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by zhangdan on 2018/10/14.\
 * <p>
 * comments:
 */

public class AddImageActivity extends Activity {

    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private MyAdapter mAdapter;

    private static final int MAX_PIC_COUNT = 20;
    private static final int FEEDBACK_LOAD_IMAGES_RESULT = 1;

    private ArrayList<ImageBean> mList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        mRecyclerView = findViewById(R.id.recycler_view);
        mGridLayoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(4 , 50 ,true));
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
        initData();
    }

    private void initData() {
        ImageBean bean = new ImageBean(ImageBean.TYPE_IMAGE, "");
        mList.add(bean);
        mAdapter.setList(mList);
        mAdapter.notifyDataSetChanged();
    }

    /**
     *
     */
    public OnOperationListener mListener = new OnOperationListener() {
        @Override
        public void addImage() {
            loadImages();
        }

        @Override
        public void deleteImage(int position) {
            mList.remove(position);
            mAdapter.setList(mList);
            mAdapter.notifyDataSetChanged();
        }
    };

    private class MyAdapter extends RecyclerView.Adapter {

        private ArrayList<ImageBean> mList;

        public MyAdapter() {

        }

        public void setList(ArrayList<ImageBean> imageBean) {
            this.mList = imageBean;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder mHolder = null;

            if (viewType == ImageBean.TYPE_ADD) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_image_item, parent, false);
                mHolder = new VH_IMAGE_ITEM(view);
            } else if (viewType == ImageBean.TYPE_IMAGE) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_image_item_cl, parent, false);
                mHolder = new VH_DELETE_ITEM(view);
            }

            return mHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            ImageBean bean = mList.get(position);

            if (holder instanceof VH_IMAGE_ITEM) {

                Glide.with(AddImageActivity.this).load(bean.getUrl()).into(((VH_IMAGE_ITEM) holder).mIv);

                ((VH_IMAGE_ITEM) holder).mDeleteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.deleteImage(position);
                        }
                    }
                });

            } else if (holder instanceof VH_DELETE_ITEM) {

                if (mList.size() >= MAX_PIC_COUNT) {
                    holder.itemView.setVisibility(View.GONE);
                } else {
                    holder.itemView.setVisibility(View.VISIBLE);
                }

                ((VH_DELETE_ITEM) holder).mDeleteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.addImage();
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


    private class ImageBean {

        private int type;
        private String url;

        public static final int TYPE_ADD = 0x01;
        public static final int TYPE_IMAGE = 0x02;

        public ImageBean() {

        }

        public ImageBean(int type, String url) {
            this.type = type;
            this.url = url;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FEEDBACK_LOAD_IMAGES_RESULT) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = null;
                try {
                    cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        final String imagePath = cursor.getString(columnIndex);
                        //根据手机屏幕设置图片宽度
                        Bitmap bitmap = UploadFile.createImageThumbnail(imagePath, getScreenShotWidth(), true);
                        if (bitmap != null) {
                            insertImg(bitmap, imagePath, true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //KLog.error(KLog.KLogFeature.toulan,"load image failed, msg:"+e.getMessage());
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }
    }

    public void insertImg(Bitmap bitmap, final String imagePath,
                          boolean showDelImg) {

        ImageBean bean = new ImageBean(ImageBean.TYPE_ADD, imagePath);
        mList.add(0 , bean);
        mAdapter.setList(mList);
        mAdapter.notifyDataSetChanged();
    }


    private int getScreenShotWidth() {
        int screenWidth = DimenUtils.getScreenWidth(this);
        screenWidth = screenWidth - (int) (screenWidth * 1.0 / 9); // 除掉边框
        return screenWidth / 4;
    }

    protected void loadImages() {
        Uri imageURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        if (imageURI != null) {
            try {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, imageURI);
                    startActivityForResult(intent, FEEDBACK_LOAD_IMAGES_RESULT);
                }
            } catch (Exception e) {
                Log.w("FeedbackPresenter", "loadImages: " + e.getMessage());
            }
        }
    }

    public interface OnOperationListener {

        void addImage();


        void deleteImage(int position);

    }


    public static void goToActivity(Context ctx){

        if (ctx == null){
            ctx = App.getInstance();
        }

        Intent intent = new Intent();
        intent.setClass(ctx , AddImageActivity.class);
        ctx.startActivity(intent);
    }



}

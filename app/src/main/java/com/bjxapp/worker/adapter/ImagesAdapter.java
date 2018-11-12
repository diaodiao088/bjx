package com.bjxapp.worker.adapter;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.model.ImageInfo;
import com.bjxapp.worker.ui.view.activity.PublicImagesActivity;
import com.bjxapp.worker.utils.BitmapUtils;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager.DataType;
import com.bjxapp.worker.utils.image.BitmapManager;
import com.bjxapp.worker.utils.image.ImageCache;
import com.bjx.master.R;;

public class ImagesAdapter extends BaseAdapter{

    private LayoutInflater mInflater;
    private ArrayList<ImageInfo> mDataArray;
    private Context mContext;
    private ImageCache mImageCache;
    private PublicImagesActivity mAct;

    private int[] mImageSize = {480,800};
	
	public ImagesAdapter(Context context,ArrayList<ImageInfo> dataArray) 
	{
		mImageCache = ImageCache.findOrCreateCache(context);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mDataArray = dataArray;
		mAct = (PublicImagesActivity) context;
	}

	public void addImage(ImageInfo selectedImage) 
	{        
		mDataArray.add(selectedImage);        
		notifyDataSetChanged();    
	}
	
	public void deleteImage(int index) 
	{
		mDataArray.remove(index);     
		notifyDataSetChanged();    
	}
	
    public int getCount() {
        return mDataArray.size();
    }

    public Object getItem(int position) {
        return mDataArray.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		
		if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_images_list, null);

            holder = new ViewHolder();
            holder.imageSelected = (XImageView) convertView.findViewById(R.id.images_list_image);

            modifyParams(holder.imageSelected);
                            
            convertView.setTag(holder);
        } 
        else 
        {
            holder = (ViewHolder) convertView.getTag();
        }

		final String imageUrl = mDataArray.get(position).getUrl();
		holder.imageSelected.setTag(imageUrl);
		holder.imageSelected.setImageResource(R.drawable.head);
		
		int flag = mDataArray.get(position).getFlag();
		if(flag == 0)
		{
			Bitmap bitmap = mImageCache.getBitmapFromMemCache(imageUrl);
			if(bitmap == null)
			{
				bitmap = BitmapUtils.getBitmapFromPath(imageUrl,mContext);
				mImageCache.addBitmapToMemCache(imageUrl, bitmap);
			}
			holder.imageSelected.setImageBitmap(bitmap);
		}
		
		if(flag == 1)
		{
			MyOnBitmapLoadListener mOnBitmapLoadListener =new MyOnBitmapLoadListener(position, holder.imageSelected);
	    	BitmapManager.getInstance(mContext).loadBitmap(imageUrl, DataType.UserData, mOnBitmapLoadListener, mImageSize);
		}
		
		return convertView;
	}

	private void modifyParams(XImageView mView){
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mView.getLayoutParams();

		if (mAct.getImageHeight() != 0){
			params.height = mAct.getImageHeight();
		}
	}

	private String getImagePath(DataType dataType, String url){
		File file = DiskCacheManager.getInstance(mContext).getFile(dataType, url);
		if(file == null){
			return null;
		}
		else {
			return file.getAbsolutePath();
		}
	}
	
    class ViewHolder {
        XImageView imageSelected;
    }
    
	public void clearCache()
	{
		mImageCache.clearMemCache();
	}
	
    class MyOnBitmapLoadListener implements BitmapManager.OnBitmapLoadListener{
    	private int position;
    	private XImageView imageView;
    	
    	public MyOnBitmapLoadListener(int position, XImageView imageView) {
    		this.position = position;
    		this.imageView = imageView;
    	}

		@Override
		public void onLoaded(String url, Bitmap bitmap, boolean isSuccessful) {
			if(imageView.getTag() !=null && !imageView.getTag().equals(url)) return;
			
			if(isSuccessful && bitmap != null)
			{
				imageView.setImageBitmap(bitmap);
				String filePath = getImagePath(DataType.UserData, url);
				if(filePath != null){
					mDataArray.get(position).setUrl(filePath);
					imageView.setTag(filePath);
				}
			}
		}
    }
}

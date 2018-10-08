package com.bjxapp.worker.utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 封装了获取、回收ImageView的bitmap的一些方法， 同时更换bitmap时，会及时回收前一个（标志其为recycled）
 */
public class RecyclableImageView extends ImageView {

    private Bitmap mBitmap;
    private ImageScaler mImageScaler;
    private String mUrl;

    public RecyclableImageView(Context context) {
        super(context.getApplicationContext());
    }

    public RecyclableImageView(Context context, AttributeSet attrs) {
        super(context.getApplicationContext(), attrs);
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    /**
     * 参数的bm不可是成员变量，必须是局部的
     */
    @Override
    public void setImageBitmap(Bitmap bm) {
        try {
            if (bm != null && mImageScaler != null) {
                bm = mImageScaler.scaleBitmap(bm);
            }
            super.setImageBitmap(bm);
            if (mBitmap != null && mBitmap != bm) {
                mBitmap.recycle();
            }
            mBitmap = bm;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    public void setImageBitmapWithoutRecycle(Bitmap bm) {
        try {
            if (bm != null && mImageScaler != null) {
                bm = mImageScaler.scaleBitmap(bm);
            }
            super.setImageBitmap(bm);

            mBitmap = bm;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void setImageResource(int id) {
        try {
            super.setImageResource(id);
            if (mBitmap != null && !mBitmap.isRecycled()) {
                mBitmap.recycle();
            }
            mBitmap = null;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void setImageResourceWithoutRecycle(int id) {
        try {
            super.setImageResource(id);

            mBitmap = null;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    public Bitmap getImageBitmap() {
        return mBitmap;
    }

    public void recycleImageBitmap() {
        setImageBitmap(null);
    }

    public void setImageScaler(ImageScaler imageScaler) {
        mImageScaler = imageScaler;
    }

    public interface ImageScaler {
        Bitmap scaleBitmap(Bitmap bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}

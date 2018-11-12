package com.bjxapp.worker.ui.view.activity.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileDescriptor;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.OurContext;
import com.bjxapp.worker.utils.BitmapUtils;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager.DataType;
import com.bjx.master.R;;

public class ClipView extends RelativeLayout implements OnTouchListener {

    public static int MAX_CLOCK_IMG_WIDTH = 640;
    public static int MAX_USER_HEAD_IMG_WIDTH = 100;
    
    public static int CLIP_FOR_OTHER_BACKGROUND = 1;
    public static int CLIP_FOR_USER_HEAD = 2;
    public static int CLIP_FOR_MAIN_BACKGROUND = 3;

    // We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;

    private ImageView mImageView;
    private Bitmap mSrcBitmap;
    private int mClipWhat = CLIP_FOR_OTHER_BACKGROUND;

    private int mClipViewX;
    private int mClipViewY;
    private int mClipViewWidth;
    private int mClipViewHeight;

    private Matrix mMatrix;
    private Matrix mSavedMatrix;
    private int mMode = NONE;

    // Remember some things for zooming
    private PointF mStart;
    private PointF mMid;
    private float mOldDist = 1f;

    private boolean mIsRollback = false;
    private boolean mIsBeginTracking = false;
    private Matrix mBeforeTrackMatrix = new Matrix();
    private static final int MSG_ANIM_ON = 0;
    private static final int MSG_ANIM_END = 1;

    private Handler mRollbackHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Matrix matrix = (Matrix) msg.obj;
            mImageView.setImageMatrix(matrix);
            if (msg.arg1 == MSG_ANIM_END) {
                mIsRollback = false;
                mMatrix.set(matrix);
                mSavedMatrix.set(matrix);
                mMode = NONE;
            }
            return true;
        }
    });

    public ClipView(Context context) {
        super(context);
        init(context);
    }

    public ClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ClipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void onDestroy() {
        if (mSrcBitmap != null && !mSrcBitmap.isRecycled()) {
            mSrcBitmap.recycle();
        }
    }

    private void init(Context context) {
        mMatrix = new Matrix();
        mSavedMatrix = new Matrix();
        mStart = new PointF();
        mMid = new PointF();
        LayoutInflater.from(getContext()).inflate(R.layout.layout_clip_picture,this);
        mImageView = (ImageView) findViewById(R.id.image_view);
        mImageView.setOnTouchListener(this);
    }

    /**
     * 设置裁剪的范围
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void setClipViewParams(int x, int y, int width, int height) {
        mClipViewX = x;
        mClipViewY = y;
        mClipViewWidth = width;
        mClipViewHeight = height;
    }

    /**
     * 设置图片
     *
     * @param imagePath
     * @param topTranslate
     */
    public void setImage(String imagePath, Rect rect, int clipWhat) {
        try {
            mClipWhat = clipWhat;
            mClipViewX = rect.left;
            mClipViewY = rect.top;
            mClipViewWidth = rect.width();
            mClipViewHeight = rect.height();
            mSrcBitmap = BitmapUtils.getBitmapFromPath(imagePath,
                    (Activity) getContext());
            if (mSrcBitmap == null) return;
            int srcWidth = mSrcBitmap.getWidth();
            int srcHeight = mSrcBitmap.getHeight();
            float scaleWidth = ((float) rect.width())
                    / srcWidth;
            float scaleHeight = ((float) rect.height())
                    / srcHeight;
            float scale = Math.max(scaleWidth, scaleHeight);
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap bitmap = Bitmap.createBitmap(mSrcBitmap, 0, 0, srcWidth,
                    srcHeight, matrix, true);
            if(bitmap != mSrcBitmap) {
                mSrcBitmap.recycle();
                mSrcBitmap = bitmap;
            }
            mImageView.setImageBitmap(mSrcBitmap);
            int picHeight = (int) (srcHeight * scale);
            int picWidth = (int) (srcWidth * scale);
            int clipFrameHeight = rect.height();
            int clipFrameWidth = rect.width();
            translate(-(picWidth - clipFrameWidth) / 2, rect.top
                    - (picHeight - clipFrameHeight) / 2);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void setImage(Uri uri, Rect rect, int clipWhat) {
        try {
            mClipWhat = clipWhat;
            mClipViewX = rect.left;
            mClipViewY = rect.top;
            mClipViewWidth = rect.width();
            mClipViewHeight = rect.height();
            mSrcBitmap = getBitmapFromUri(uri, getContext());
            if (mSrcBitmap == null) return;
            int srcWidth = mSrcBitmap.getWidth();
            int srcHeight = mSrcBitmap.getHeight();
            float scaleWidth = ((float) rect.width())
                    / srcWidth;
            float scaleHeight = ((float) rect.height())
                    / srcHeight;
            float scale = Math.max(scaleWidth, scaleHeight);
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap bitmap = Bitmap.createBitmap(mSrcBitmap, 0, 0, srcWidth,
                    srcHeight, matrix, true);
            if (bitmap != mSrcBitmap) {
                mSrcBitmap.recycle();
                mSrcBitmap = bitmap;
            }
            mImageView.setImageBitmap(mSrcBitmap);
            int picHeight = (int) (srcHeight * scale);
            int picWidth = (int) (srcWidth * scale);
            int clipFrameHeight = rect.height();
            int clipFrameWidth = rect.width();
            translate(-(picWidth - clipFrameWidth) / 2, rect.top
                    - (picHeight - clipFrameHeight) / 2);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private Bitmap getBitmapFromUri(Uri uri, Context context) {
        Bitmap result = null;
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            int screenWidth = OurContext.getScreenWidth(getContext());
            int screenHeight = OurContext.getScreenHeight(getContext());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            options.inJustDecodeBounds = true;
            result = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            options.inSampleSize = BitmapUtils.calculateInSampleSize(options, screenWidth, screenHeight);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            result = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            parcelFileDescriptor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 这里实现了多点触摸放大缩小，和单点移动图片的功能
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mIsRollback)
            return true;
        Matrix saveMatrix = new Matrix();
        saveMatrix.set(mMatrix);
        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mSavedMatrix.set(mMatrix);
                // 設置初始點位置
                mStart.set(event.getX(), event.getY());
                mMode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mOldDist = spacing(event);
                if (mOldDist > 10f) {
                    mSavedMatrix.set(mMatrix);
                    midPoint(mMid, event);
                    mMode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mMode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == DRAG) {
                    mMatrix.set(mSavedMatrix);
                    mMatrix.postTranslate(event.getX() - mStart.x, event.getY()
                            - mStart.y);
                } else if (mMode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        mMatrix.set(mSavedMatrix);
                        float scale = newDist / mOldDist;
                        mMatrix.postScale(scale, scale, mMid.x, mMid.y);
                    }
                }
                break;
        }
        mImageView.setImageMatrix(mMatrix);
        if (isMatrixOutFrame(mMatrix)) {
            mIsBeginTracking = true;
            Matrix m = new Matrix();
            m.set(mMatrix);
        } else {
            mIsBeginTracking = false;
            mBeforeTrackMatrix.set(saveMatrix);
        }

        if (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_POINTER_UP) {
            if (mIsBeginTracking) {
                mIsBeginTracking = false;
                animate(mMatrix, mBeforeTrackMatrix);
            }
        }
        return true;
    }

    public void translate(int dx, int dy) {
        mMatrix.postTranslate(dx, dy);
        mImageView.setImageMatrix(mMatrix);
        mBeforeTrackMatrix.set(mMatrix);
    }

    public void rotate(float degree) {
        Rect rect = getVisibleRect(mImageView.getImageMatrix());
        mMatrix.postRotate(degree, (rect.left + rect.right) / 2,
                (rect.top + rect.bottom) / 2);
        mImageView.setImageMatrix(mMatrix);
        mBeforeTrackMatrix.set(mMatrix);
    }

    /**
     * Determine the space between the first two fingers
     */
    @SuppressLint("NewApi")
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    @SuppressLint("NewApi")
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /* 获取矩形区域内的截图 */
    public String getBitmap() {
        return getBitmapWithCut();
    }

    private String getBitmapWithCut() {
        if (mSrcBitmap == null) return "";
        Rect rect = getClipRect();
        if (rect == null || rect.left >= rect.right || rect.top >= rect.bottom) {
            rect = new Rect(0, 0, 1, 1);
        }
        int screenWidth = OurContext.getScreenWidth(getContext());
        int length;
        if (mClipWhat == CLIP_FOR_OTHER_BACKGROUND || mClipWhat == CLIP_FOR_MAIN_BACKGROUND) {
            length = Math.max(screenWidth, MAX_CLOCK_IMG_WIDTH);
        } else {
            length = MAX_USER_HEAD_IMG_WIDTH;
        }
        float scale = 1;
        int maxEdge = Math.max(rect.width(), rect.height());
        if (maxEdge > length) {
            scale = (float) length / (float) maxEdge;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(getDegrees());
        matrix.postScale(scale, scale);
        Bitmap bitmap = Bitmap.createBitmap(mSrcBitmap, rect.left, rect.top,
                rect.right - rect.left, rect.bottom - rect.top, matrix, false);
        String name;
        if (mClipWhat == CLIP_FOR_OTHER_BACKGROUND) {
            name = saveOtherBitmap(bitmap);
        } else if (mClipWhat == CLIP_FOR_USER_HEAD) {
            name = saveHeadBitmap(bitmap);
        } else {
            name = saveMainBitmap(bitmap);
        }
        if (mSrcBitmap != bitmap)
            bitmap.recycle();
        return name;
    }

    /**
     * todo:保存用户头像图片,返回完整地址
     *
     * @param bitmap
     * @return
     */
    private String saveHeadBitmap(Bitmap bitmap) {
    	try {
            String userID = ConfigManager.getInstance(getContext().getApplicationContext()).getUserCode();
            DiskCacheManager dcm = DiskCacheManager.getInstance(getContext().getApplicationContext());
            dcm.putBitmapToDisk(DataType.UserData, userID, bitmap, CompressFormat.JPEG, 100);
            File imgFile = dcm.getFile(DataType.UserData, userID);
            return imgFile == null ? "" : imgFile.getPath();
		} catch (Exception e) {
			return "";
		}
    }

    private String saveOtherBitmap(Bitmap bitmap) {
    	return "";
    }
    
    private String saveMainBitmap(Bitmap bitmap) {
        return "";
    }

    // 平移回弹修正
    private void fixMatrix(Matrix fromMatrix, Matrix toMatrix) {
        Rect insert = getInsertRect(fromMatrix);
        Rect snapRect = new Rect(mClipViewX, mClipViewY, mClipViewX
                + mClipViewWidth, mClipViewY + mClipViewHeight);

        boolean hasInsert = insert.width() * insert.height() > 0;

        float[] fromValues = new float[9];
        fromMatrix.getValues(fromValues);
        float[] toValues = new float[9];
        toMatrix.getValues(toValues);

        int fromLeft = (int) fromValues[Matrix.MTRANS_X];
        int fromTop = (int) fromValues[Matrix.MTRANS_Y];
        int toLeft = fromLeft;
        int toTop = fromTop;

        if (isInside(snapRect, new Point(insert.left, insert.top))) {
            toLeft += snapRect.left - insert.left;
            toTop += snapRect.top - insert.top;
        } else if (isInside(snapRect, new Point(insert.right, insert.top))) {
            toLeft += snapRect.right - insert.right;
            toTop += snapRect.top - insert.top;

        } else if (isInside(snapRect, new Point(insert.left, insert.bottom))) {
            toLeft += snapRect.left - insert.left;
            toTop += snapRect.bottom - insert.bottom;

        } else if (isInside(snapRect, new Point(insert.right, insert.bottom))) {
            toLeft += snapRect.right - insert.right;
            toTop += snapRect.bottom - insert.bottom;
        } else if (insert.left > snapRect.left && insert.left <= snapRect.right
                && insert.top <= snapRect.top
                && insert.bottom >= snapRect.bottom) {
            toLeft += snapRect.left - insert.left;
        } else if (hasInsert && insert.right < snapRect.right
                && insert.right >= snapRect.left && insert.top <= snapRect.top
                && insert.bottom >= snapRect.bottom) {
            toLeft += snapRect.right - insert.right;
        } else if (hasInsert && insert.top > snapRect.top
                && insert.top <= snapRect.bottom
                && insert.left <= snapRect.left
                && insert.right >= snapRect.right) {
            toTop += snapRect.top - insert.top;
        } else if (hasInsert && insert.bottom < snapRect.bottom
                && insert.bottom >= snapRect.top
                && insert.left <= snapRect.left
                && insert.right >= snapRect.right) {
            toTop += snapRect.bottom - insert.bottom;
        } else {
            toLeft = (int) toValues[Matrix.MTRANS_X];
            toTop = (int) toValues[Matrix.MTRANS_Y];

        }
        toValues[Matrix.MTRANS_X] = toLeft;
        toValues[Matrix.MTRANS_Y] = toTop;
        toMatrix.setValues(toValues);
    }

    private boolean isInside(Rect rect, Point point) {
        return point.x < rect.right && point.x > rect.left
                && point.y < rect.bottom && point.y > rect.top;
    }

    /**
     * @param fromMatrix
     * @param toMatrix
     */
    private void animate(Matrix fromMatrix, Matrix toMatrix) {
        mIsRollback = true;
        Rect fromRect = getVisibleRect(fromMatrix);
        Rect toRect = getVisibleRect(toMatrix);
        boolean isTranslate = fromRect.width() == toRect.width();
        if (isTranslate) {
            fixMatrix(fromMatrix, toMatrix);
        }
        float[] fromValues = new float[9];
        fromMatrix.getValues(fromValues);
        int fromLeft = (int) fromValues[2];
        int fromTop = (int) fromValues[5];

        float[] toValues = new float[9];
        toMatrix.getValues(toValues);
        int toLeft = (int) toValues[2];
        int toTop = (int) toValues[5];

        int gap = 15;

        if (isTranslate) {
            // 只是坐标平移变换
            int maxTimes = 10;
            // int minDis=10;
            for (int i = 0; i < maxTimes; i++) {
                int dx = (toLeft - fromLeft) * (i + 1) / maxTimes;
                int dy = (toTop - fromTop) * (i + 1) / maxTimes;
                Matrix matrix = new Matrix(fromMatrix);
                matrix.postTranslate(dx, dy);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = matrix;
                msg.arg1 = MSG_ANIM_ON;
                mRollbackHandler.sendMessageDelayed(msg, gap * i);
            }
            Message lastMsg = new Message();
            lastMsg.what = 0;
            lastMsg.obj = new Matrix(toMatrix);
            lastMsg.arg1 = MSG_ANIM_END;
            mRollbackHandler.sendMessageDelayed(lastMsg, gap * maxTimes);

        } else {
            //只是拉伸变換
            int maxTimes = 10;
            float base = (float) toRect.width() / (float) fromRect.width();
            float delta = (float) Math.pow(base, (float) 1 / (float) maxTimes);
            Matrix matrix = new Matrix(fromMatrix);
            for (int i = 0; i < maxTimes; i++) {
                /*
                 * matrix.postScale(delta, delta, fromRect.left + fromRect.right
				 * / 2, toRect.top + toRect.bottom / 2);
				 */
                matrix.postScale(delta, delta, mMid.x, mMid.y);
                Matrix sendMatrix = new Matrix(matrix);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = sendMatrix;
                msg.arg1 = MSG_ANIM_ON;
                mRollbackHandler.sendMessageDelayed(msg, gap * i);
            }
            Message lastMsg = new Message();
            lastMsg.what = 0;
            lastMsg.obj = new Matrix(toMatrix);
            lastMsg.arg1 = MSG_ANIM_END;
            mRollbackHandler.sendMessageDelayed(lastMsg, gap * maxTimes);

        }

    }

    private int getDegrees() {
        Rect scale = getScaleRect();
        int degrees = 0;
        if (scale.left > scale.right && scale.top < scale.bottom)
            degrees = 90;
        else if (scale.left > scale.right && scale.top > scale.bottom)
            degrees = 180;
        else if (scale.left < scale.right && scale.top > scale.bottom)
            degrees = 270;
        else
            degrees = 0;
        return degrees;
    }

    /**
     * 获取截图后，针对于原始图片的区域
     *
     * @return
     */
    private Rect getClipRect() {
        if (mSrcBitmap == null) return new Rect(0, 0, 1, 1);
        Rect scale = getScaleRect();
        Rect insert = getInsertRect(mImageView.getImageMatrix());

        int originalWidth = mSrcBitmap.getWidth();
        int originalHeight = mSrcBitmap.getHeight();

        Point top = getCloest(new Point(scale.left, scale.top), new Point(
                        insert.left, insert.top),
                new Point(insert.left, insert.bottom), new Point(insert.right,
                        insert.top), new Point(insert.right, insert.bottom)
        );
        Point bottom = getFarthest(new Point(scale.left, scale.top), new Point(
                        insert.left, insert.top),
                new Point(insert.left, insert.bottom), new Point(insert.right,
                        insert.top), new Point(insert.right, insert.bottom)
        );

        // TODO：可否考虑矩阵求逆（以后再讨论）
        int scaleWidth = Math.abs(scale.right - scale.left);
        int scaleHeight = Math.abs(scale.bottom - scale.top);
        float startXPercent = (float) Math.abs((top.x - scale.left))
                / (float) scaleWidth;
        float endXPercent = (float) Math.abs((bottom.x - scale.left))
                / (float) scaleWidth;
        float startYPercent = (float) Math.abs((top.y - scale.top))
                / (float) scaleHeight;
        float endYPercent = (float) Math.abs((bottom.y - scale.top))
                / (float) scaleHeight;
        int degrees = getDegrees();
        if (degrees == 90 || degrees == 270) {
            scaleWidth = Math.abs(scale.bottom - scale.top);
            scaleHeight = Math.abs(scale.right - scale.left);
            startXPercent = (float) Math.abs((top.y - scale.top))
                    / (float) scaleWidth;
            endXPercent = (float) Math.abs((bottom.y - scale.top))
                    / (float) scaleWidth;
            startYPercent = (float) Math.abs((top.x - scale.left))
                    / (float) scaleHeight;
            endYPercent = (float) Math.abs((bottom.x - scale.left))
                    / (float) scaleHeight;
        }

        Rect resultRec = new Rect((int) (originalWidth * startXPercent),
                (int) (originalHeight * startYPercent),
                (int) (originalWidth * endXPercent),
                (int) (originalHeight * endYPercent));
        return resultRec;
    }

    /**
     * 改变化矩阵是否超出框架
     *
     * @param matrix
     * @return
     */
    private boolean isMatrixOutFrame(final Matrix matrix) {
        Rect visibleRect = getVisibleRect(matrix);
        Rect snapRect = new Rect(mClipViewX, mClipViewY, mClipViewX
                + mClipViewWidth, mClipViewY + mClipViewHeight);
        return !visibleRect.contains(snapRect);
    }

    /**
     * 返回交叉区域
     *
     * @return
     */
    private Rect getInsertRect(final Matrix matrix) {
        Rect visibleRect = getVisibleRect(matrix);
        Rect snapRect = new Rect(mClipViewX, mClipViewY, mClipViewX
                + mClipViewWidth, mClipViewY + mClipViewHeight);
        visibleRect.intersect(snapRect);
        return visibleRect;
    }

    /**
     * 获取图片可视区域，原圖左上角为top，右下脚为bottom
     *
     * @return
     */
    private Rect getScaleRect() {
        if (mImageView.getDrawable() == null) return new Rect(0, 0, 1, 1);
        Rect rect = mImageView.getDrawable().getBounds();
        int width = rect.width();
        int height = rect.height();
        final Matrix matrix = mImageView.getImageMatrix();
        float[] values = new float[9];
        matrix.getValues(values);

        Rect visibleRect = new Rect();
        width = (int) (width * values[8]);
        height = (int) (height * values[8]);
        visibleRect.left = (int) values[2];
        visibleRect.top = (int) values[5];
        visibleRect.right = (int) (visibleRect.left + width * values[0] + height
                * values[1]);
        visibleRect.bottom = (int) (visibleRect.top + height * values[0] - width
                * values[1]);

        return visibleRect;
    }

    /**
     * 获取图片可视区域，屏幕左上角为top，右下脚为bottom
     *
     * @return
     */
    private Rect getVisibleRect(final Matrix matrix) {
        Drawable drawable = mImageView.getDrawable();
        if (drawable == null)//崩溃日志显示可能为空，故加上null判断
        {
            return new Rect(0, 0, 0, 0);
        }
        Rect rect = drawable.getBounds();
        int width = rect.width();
        int height = rect.height();
        float[] values = new float[9];
        matrix.getValues(values);
        Rect visibleRect = new Rect();
        width = (int) (width * values[8]);
        height = (int) (height * values[8]);
        visibleRect.left = (int) values[2];
        visibleRect.top = (int) values[5];
        visibleRect.right = (int) (visibleRect.left + width * values[0] + height
                * values[1]);
        visibleRect.bottom = (int) (visibleRect.top + height * values[0] - width
                * values[1]);

        Rect newRect = new Rect();
        newRect.left = Math.min(visibleRect.left, visibleRect.right);
        newRect.top = Math.min(visibleRect.top, visibleRect.bottom);
        newRect.right = Math.max(visibleRect.left, visibleRect.right);
        newRect.bottom = Math.max(visibleRect.top, visibleRect.bottom);

        return newRect;
    }

    private Point getFarthest(final Point target, Point... points) {

        Comparator<Point> comparator = new Comparator<Point>() {

            @Override
            public int compare(Point o1, Point o2) {
                double p1_Top = Math.pow(o1.x - target.x, 2)
                        + Math.pow(o1.y - target.y, 2);
                double p2_Top = Math.pow(o2.x - target.x, 2)
                        + Math.pow(o2.y - target.y, 2);

                if (p1_Top < p2_Top)
                    return 1;
                else if (p1_Top == p2_Top)
                    return 0;
                else
                    return -1;
            }
        };

        List<Point> list = Arrays.asList(points);
        Collections.sort(list, comparator);
        return list.get(0);
    }

    private Point getCloest(final Point target, Point... points) {

        Comparator<Point> comparator = new Comparator<Point>() {

            @Override
            public int compare(Point o1, Point o2) {
                double p1_Top = Math.pow(o1.x - target.x, 2)
                        + Math.pow(o1.y - target.y, 2);
                double p2_Top = Math.pow(o2.x - target.x, 2)
                        + Math.pow(o2.y - target.y, 2);

                if (p1_Top < p2_Top)
                    return -1;
                else if (p1_Top == p2_Top)
                    return 0;
                else
                    return 1;
            }
        };

        List<Point> list = Arrays.asList(points);
        Collections.sort(list, comparator);
        return list.get(0);
    }

}

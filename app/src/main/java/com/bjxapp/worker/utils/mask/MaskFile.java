package com.bjxapp.worker.utils.mask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.Environment;
import android.text.TextUtils;

import com.bjxapp.worker.App;
import com.bjxapp.worker.ui.widget.DimenUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MaskFile {

    private static MaskFile maskFile;

    private void MaskFile() {

    }

    public static void addMask(final String imagePath, final String currentAddress, final String shopName,
                               final String enterpriseName, final String modelName) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Bitmap bitmap1 = BitmapFactory.decodeStream(new FileInputStream(imagePath));

                    int degree = readPictureDegree(imagePath);

                    //中间高度位置添加水印文字。
                    Bitmap bitmap2 = addTextWatermark(bitmap1,
                            0, bitmap1.getHeight(), true, degree,
                            currentAddress, shopName, enterpriseName, modelName);
                    save(bitmap2, new File(imagePath), Bitmap.CompressFormat.JPEG, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static boolean save(Bitmap src, File file, Bitmap.CompressFormat format, boolean recycle) {
        if (isEmptyBitmap(src))
            return false;

        OutputStream os;
        boolean ret = false;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            ret = src.compress(format, 100, os);
            if (recycle && !src.isRecycled())
                src.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }


    public static Bitmap addTextWatermark(Bitmap src, float x, float y, boolean recycle, int degree,
                                          String currentAddress, final String shopName, final String enterpriseName, String modelName) {
        if (isEmptyBitmap(src))
            return null;


        Bitmap ret = src.copy(src.getConfig(), true);

        Bitmap degreeBM;

        if (degree != 0) {
            //旋转图片 动作
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            // 创建新的图片
            degreeBM = Bitmap.createBitmap(ret, 0, 0,
                    ret.getWidth(), ret.getHeight(), matrix, true);
        } else {
            degreeBM = ret;
        }

        int ratio = ret.getHeight() / DimenUtils.getScreenHeight(App.getInstance());

        int left = DimenUtils.dp2px(20, App.getInstance()) * ratio;
        int bottom_1 = (int) ((y - left) * ratio);
        int textSize = DimenUtils.dp2px(20, App.getInstance()) * ratio;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas(degreeBM);
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);


        Rect boundsBottom_add = new Rect();
        paint.getTextBounds(currentAddress, 0, currentAddress.length(), boundsBottom_add);
        canvas.drawText(currentAddress, left, bottom_1, paint);

        Rect boundsBottom = new Rect();
        String bottomStr = enterpriseName + "-" + shopName;
        paint.getTextBounds(bottomStr, 0, bottomStr.length(), boundsBottom);
        canvas.drawText(bottomStr, left, bottom_1 - textSize - left, paint);

        Rect boundsBottom_time = new Rect();
        paint.getTextBounds(getFormatedTime(), 0, getFormatedTime().length(), boundsBottom_time);
        canvas.drawText(getFormatedTime(), left, bottom_1 - textSize * 2 - left * 2, paint);

        Rect boundsBottom_model = new Rect();
        paint.getTextBounds(modelName, 0, modelName.length(), boundsBottom_model);
        canvas.drawText(modelName, left, bottom_1 - textSize * 3 - left * 3, paint);

//        Rect bounds = new Rect();
//        paint.getTextBounds(modelName, 0, modelName.length(), bounds);
//        canvas.drawText(modelName, x + 40, y - 300, paint);


        if (recycle && !src.isRecycled())
            src.recycle();
        return degreeBM;
    }

    private static String getFormatedTime() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        return format.format(new Date());
    }

    public static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }


    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return degree;
        }
        return degree;
    }


}

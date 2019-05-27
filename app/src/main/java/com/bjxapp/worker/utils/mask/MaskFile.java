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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MaskFile {

    private static MaskFile maskFile;

    private void MaskFile() {

    }

    public static void addMask(final String imagePath) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Bitmap bitmap1 = BitmapFactory.decodeStream(new FileInputStream(imagePath));

                    int degree = readPictureDegree(imagePath);
//
//                    File zhangphil = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "zhangphil.jpg");
//                    if (!zhangphil.exists())
//                        zhangphil.createNewFile();

                    int textSize = 60;

                    //中间高度位置添加水印文字。
                    Bitmap bitmap2 = addTextWatermark(bitmap1, "blog.csdn.net/zhangphil", textSize,
                            Color.WHITE, 0, bitmap1.getHeight() / 2, true, degree);
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


    public static Bitmap addTextWatermark(Bitmap src, String content, int textSize,
                                          int color, float x, float y, boolean recycle, int degree) {
        if (isEmptyBitmap(src) || content == null)
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
        }else{
            degreeBM = ret;
        }

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas(degreeBM);
        paint.setColor(color);
        paint.setTextSize(textSize);
        Rect bounds = new Rect();
        paint.getTextBounds(content, 0, content.length(), bounds);
        canvas.drawText(content, x, y, paint);
        if (recycle && !src.isRecycled())
            src.recycle();
        return degreeBM;
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

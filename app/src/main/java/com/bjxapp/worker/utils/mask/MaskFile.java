package com.bjxapp.worker.utils.mask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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

                    File zhangphil = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "zhangphil.jpg");
                    if (!zhangphil.exists())
                        zhangphil.createNewFile();

                    int textSize = 60;

                    //中间高度位置添加水印文字。
                    Bitmap bitmap2 = addTextWatermark(bitmap1, "blog.csdn.net/zhangphil", textSize, Color.RED, 0, bitmap1.getHeight() / 2, true);
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


    public static Bitmap addTextWatermark(Bitmap src, String content, int textSize, int color, float x, float y, boolean recycle) {
        if (isEmptyBitmap(src) || content == null)
            return null;
        Bitmap ret = src.copy(src.getConfig(), true);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas(ret);
        paint.setColor(color);
        paint.setTextSize(textSize);
        Rect bounds = new Rect();
        paint.getTextBounds(content, 0, content.length(), bounds);
        canvas.drawText(content, x, y, paint);
        if (recycle && !src.isRecycled())
            src.recycle();
        return ret;
    }

    public static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }


}

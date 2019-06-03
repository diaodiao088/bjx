package com.bjxapp.worker.utils.mask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
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
                    Bitmap bitmap2 = addTextWatermarkNew(bitmap1,
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

        if (TextUtils.isEmpty(modelName)) {
            modelName = "";
        }

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

        float heightRatio = (float) ret.getHeight() / DimenUtils.getScreenHeight(App.getInstance());

        float widthRatio = (float) ret.getWidth() / DimenUtils.getScreenWidth(App.getInstance());

        float ratio = heightRatio > widthRatio ? heightRatio : widthRatio;

        if (degree == 90 || degree == 270) {
            ratio = (float) ret.getWidth() / DimenUtils.getScreenWidth(App.getInstance());
        }

        if (ratio <= 0) {
            ratio = 1;
        }

        int left = (int) (DimenUtils.dp2px(17, App.getInstance()) * ratio);
        int bottom_1 = (int) (y - left * ratio);
        int textSize = (int) (DimenUtils.dp2px(16, App.getInstance()) * ratio);
        int small_textSize = (int) (DimenUtils.dp2px(14, App.getInstance()) * ratio);
        int large_textSize = (int) (DimenUtils.dp2px(20, App.getInstance()) * ratio);


        int specPart = textSize + left;


        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setShadowLayer(2, 1, 1, Color.parseColor("#70000000"));

        Canvas canvas = new Canvas(degreeBM);
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);

        String currentAddr_part1 = "";
        String currentAddr_part2 = "";

        if (currentAddress.length() > 20) {
            currentAddr_part1 = currentAddress.substring(0, 20);
            currentAddr_part2 = currentAddress.substring(20);
        } else {
            currentAddr_part1 = currentAddress;
            specPart = 0;
        }

        if (!TextUtils.isEmpty(currentAddr_part2)) {

            Rect boundsBottom_add = new Rect();
            paint.getTextBounds(currentAddr_part2, 0, currentAddr_part2.length(), boundsBottom_add);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            canvas.drawText(currentAddr_part2, left, bottom_1, paint);
        }


        Rect boundsBottom_add = new Rect();
        paint.getTextBounds(currentAddr_part1, 0, currentAddr_part1.length(), boundsBottom_add);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText(currentAddr_part1, left, bottom_1 - specPart, paint);

        Rect boundsBottom = new Rect();
        String bottomStr = enterpriseName + "-" + shopName;
        paint.getTextBounds(bottomStr, 0, bottomStr.length(), boundsBottom);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText(bottomStr, left, bottom_1 - textSize - left - specPart, paint);

        Rect boundsBottom_time = new Rect();
        paint.setTextSize(small_textSize);
        paint.setTypeface(Typeface.DEFAULT);
        paint.getTextBounds(getFormatedYear(), 0, getFormatedYear().length(), boundsBottom_time);
        canvas.drawText(getFormatedYear(), left, bottom_1 - textSize * 2 - left * 2 - specPart, paint);

        Rect boundsBottom_model = new Rect();
        paint.setTextSize(large_textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.getTextBounds(modelName, 0, modelName.length(), boundsBottom_model);
        canvas.drawText(modelName, left, bottom_1 - textSize * 3 - left * 3 - specPart, paint);

//        Rect bounds = new Rect();
//        paint.getTextBounds(modelName, 0, modelName.length(), bounds);
//        canvas.drawText(modelName, x + 40, y - 300, paint);


        if (recycle && !src.isRecycled())
            src.recycle();
        return degreeBM;
    }


    public static Bitmap addTextWatermarkNew(Bitmap src, float x, float y, boolean recycle, int degree,
                                             String currentAddress, final String shopName, final String enterpriseName, String modelName) {
        if (isEmptyBitmap(src))
            return null;

        if (TextUtils.isEmpty(modelName)) {
            modelName = "";
        }

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

        float heightRatio = (float) ret.getHeight() / DimenUtils.getScreenHeight(App.getInstance());

        float widthRatio = (float) ret.getWidth() / DimenUtils.getScreenWidth(App.getInstance());

        float ratio = heightRatio > widthRatio ? heightRatio : widthRatio;

        if (degree == 90 || degree == 270) {
            ratio = (float) ret.getWidth() / DimenUtils.getScreenWidth(App.getInstance());
        }

        if (ratio <= 0) {
            ratio = 1;
        }

        int left = (int) (DimenUtils.dp2px(10, App.getInstance()) * ratio);
        int bottom_1 = (int) (y - left * ratio);
        int textSize = (int) (DimenUtils.dp2px(16, App.getInstance()) * ratio);
        int small_textSize = (int) (DimenUtils.dp2px(14, App.getInstance()) * ratio);
        int large_textSize = (int) (DimenUtils.dp2px(20, App.getInstance()) * ratio);

        int margin_top = (int) (DimenUtils.dp2px(25, App.getInstance()) * ratio);
        int text_margin = (int) (DimenUtils.dp2px(14, App.getInstance()) * ratio);

        int specPart = textSize + left;


        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setShadowLayer(2, 1, 1, Color.parseColor("#70000000"));

        Canvas canvas = new Canvas(degreeBM);
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);

        String currentAddr_part1 = "";
        String currentAddr_part2 = "";

        if (currentAddress.length() > 15) {
            currentAddr_part1 = currentAddress.substring(0, 15);
            currentAddr_part2 = currentAddress.substring(15);
        } else {
            currentAddr_part1 = currentAddress;
            specPart = 0;
        }


        Rect boundsBottom_model = new Rect();
        paint.setTextSize(large_textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.getTextBounds(modelName, 0, modelName.length(), boundsBottom_model);
        canvas.drawText(modelName, left, margin_top, paint);

        Rect boundsBottom = new Rect();
        String bottomStr = enterpriseName + "-" + shopName;
        paint.setTextSize(textSize);
        paint.getTextBounds(bottomStr, 0, bottomStr.length(), boundsBottom);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText(bottomStr, left, text_margin * 2 + large_textSize, paint);


        if (!TextUtils.isEmpty(currentAddr_part2)) {

            Rect boundsBottom_add = new Rect();
            paint.getTextBounds(currentAddr_part2, 0, currentAddr_part2.length(), boundsBottom_add);
            paint.setTypeface(Typeface.DEFAULT_BOLD);

            canvas.drawText(currentAddr_part2, (ret.getWidth() - boundsBottom_add.right) / 2, bottom_1, paint);
        }

        currentAddr_part1 = getFormatedYear() + "  " + currentAddr_part1;

        Rect boundsBottom_add = new Rect();
        paint.getTextBounds(currentAddr_part1, 0, currentAddr_part1.length(), boundsBottom_add);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText(currentAddr_part1, (ret.getWidth() - boundsBottom_add.right) / 2, bottom_1 - specPart, paint);

        Rect boundsBottom_time = new Rect();
        paint.setTextSize(small_textSize * 3);
        paint.setTypeface(Typeface.DEFAULT);
        paint.getTextBounds(getFormatedHour(), 0, getFormatedHour().length(), boundsBottom_time);
        canvas.drawText(getFormatedHour(), (ret.getWidth() - boundsBottom_time.right) / 2, bottom_1 - textSize - left - specPart, paint);


        if (recycle && !src.isRecycled())
            src.recycle();
        return degreeBM;
    }

    private static String getFormatedYear() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        return format.format(new Date());
    }

    private static String getFormatedHour() {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

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

package com.haibin.calendarview.simple;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MonthView;

/**
 * 高仿魅族日历布局
 * Created by huanghaibin on 2017/11/15.
 */

public class SimpleMonthView extends MonthView {

    private int mRadius;

    Paint paint = new Paint();

    public SimpleMonthView(Context context) {
        super(context);
        //兼容硬件加速无效的代码
        setLayerType(View.LAYER_TYPE_SOFTWARE, mSelectedPaint);
        //4.0以上硬件加速会导致无效
        paint.setColor(Color.WHITE);
    }

    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 5 * 2;
        // mSchemePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onLoopStart(int x, int y) {

    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        // canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
        canvas.drawRoundRect(new RectF(x + 3, y + 3, x + mItemWidth - 3, y + mItemHeight - 3), 10, 10, mSelectedPaint);
        return false;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        // canvas.drawCircle(cx, cy, mRadius, mSchemePaint);
        // canvas.drawRect(new Rect(x, y, x + mItemWidth, y + mItemHeight), mSelectedPaint);
        canvas.drawRoundRect(new RectF(x + 3, y + 3, x + mItemWidth - 3, y + mItemHeight - 3), 10, 10, mSchemePaint);
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine + y;
        int cx = x + mItemWidth / 2;




        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    mSelectTextPaint);
        } else if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);

        } else {

            canvas.drawRoundRect(new RectF(x + 3, y + 3, x + mItemWidth - 3, y + mItemHeight - 3), 10, 10, paint);

            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);


        }

        if (calendar.isCurrentDay() && calendar.isCurrentMonth()){

            Paint paint = new Paint();
            paint.setTextSize(24);

            if (isSelected){
                paint.setColor(Color.parseColor("#ffffff"));
            }else{
                paint.setColor(Color.parseColor("#545454"));
            }

            canvas.drawText("今天",
                    cx + 10,
                    baselineY + 35,
                    paint);
        }
    }
}

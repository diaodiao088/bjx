package com.bjxapp.worker.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

public class RecTextView extends TextView {

    public RecTextView(Context context) {
        super(context);
    }

    public RecTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(getMeasuredWidth() , getMeasuredWidth());
    }
}

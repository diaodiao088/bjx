package com.bjxapp.worker.ui.view.activity.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.bjxapp.worker.ui.widget.DimenUtils;

public class MaxHeightRecyclerView extends RecyclerView {

    public MaxHeightRecyclerView(Context context) {
        super(context);
    }

    public MaxHeightRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxHeightRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {

        int maxHeight = DimenUtils.dp2px(170 ,getContext());

        int heightSpecMod = MeasureSpec.makeMeasureSpec(maxHeight , MeasureSpec.AT_MOST);

        super.onMeasure(widthSpec, heightSpecMod);
    }
}

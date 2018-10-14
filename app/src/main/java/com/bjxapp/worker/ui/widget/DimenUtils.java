package com.bjxapp.worker.ui.widget;

import android.content.Context;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * Created by zhangdan on 2018/9/20.
 * comments:
 */

public class DimenUtils {

    public static int dp2px(int dpVal, Context ctx) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, ctx.getResources().getDisplayMetrics());
    }

    public static int getScreenWidth(Context ctx) {
        WindowManager wm = (WindowManager) ctx
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();

        return width;
    }


}

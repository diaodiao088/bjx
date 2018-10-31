package com.bjxapp.worker.utils;

import android.content.ActivityNotFoundException;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

public class HandleUrlLinkMovementMethod extends LinkMovementMethod {


    private static HandleUrlLinkMovementMethod sInstance;

    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();


            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
            if (link.length != 0) {
                String url = link[0].getURL();
                if (mLinkCallBack != null) {
                    mLinkCallBack.onClick(url);
                    return true;
                }
            }
        }

        try {
            return super.onTouchEvent(widget, buffer, event);
        } catch (ActivityNotFoundException e) {
            Log.e("", "Link action error : " + e.getMessage());
            return true;
        }
    }

    public static HandleUrlLinkMovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new HandleUrlLinkMovementMethod();
        }
        return sInstance;
    }

    private HandleUrlLinkMovementMethod.OnLinkCallBack mLinkCallBack;

    public void setOnLinkCallBack(HandleUrlLinkMovementMethod.OnLinkCallBack linkCallBack) {
        this.mLinkCallBack = linkCallBack;
    }

    public interface OnLinkCallBack {

        /**
         * use term or privacy policy..
         */
        void onClick(String url);


    }
}

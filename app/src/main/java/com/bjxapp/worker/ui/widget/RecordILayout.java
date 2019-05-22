package com.bjxapp.worker.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.model.CommentBean;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordILayout extends LinearLayout {

    private CommentBean commentBean;

    @BindView(R.id.name_tv)
    TextView mNameTv;

    @BindView(R.id.time)
    TextView mTimeTv;

    @BindView(R.id.content)
    TextView mContentTv;

    public RecordILayout(Context context) {
        super(context);
        init();
    }

    public RecordILayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordILayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.contact_left_layout, this);
        ButterKnife.bind(this);
    }

    public void bindData(CommentBean commentBean) {
        this.commentBean = commentBean;

        mNameTv.setText("客服");

        mContentTv.setText(commentBean.getContent());

        mTimeTv.setText(getFormatTime(commentBean.getCreateTime()));
    }

    private String getFormatTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/ HH:mm");
        java.util.Date dt = new Date(time);
        String sDateTime = sdf.format(dt);  //得到精确到秒的表示：08/31/2006 21:08:00
        return sDateTime;
    }


}

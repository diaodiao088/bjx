package com.bjxapp.worker.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.model.MainTainBean;
import com.bjxapp.worker.model.PlanBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MaintainCallItemLayout extends LinearLayout {

    private View mRootView;

    @BindView(R.id.issue_reason_tv)
    XTextView mReasonTv;

    @BindView(R.id.strategy_content_tv)
    XTextView mNextTimeTv;

    @BindView(R.id.guzhang_content_tv)
    XTextView mGuZhangTv;

    @BindView(R.id.modify_content_tv)
    XTextView mModifyTv;

    @BindView(R.id.price_content)
    TextView mPriceTv;

    @BindView(R.id.main_container_ly)
    LinearLayout mPriceContainerLy;

    private PlanBean planBean;

    public MaintainCallItemLayout(Context context) {
        super(context);
        init();
    }

    public MaintainCallItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaintainCallItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.maintain_xietiao_layout, this);

        ButterKnife.bind(this);

    }

    public void bindData(PlanBean planBean) {
        this.planBean = planBean;

        mGuZhangTv.setText(planBean.getFault());
        mModifyTv.setText(planBean.getPlan());
        mPriceTv.setText(planBean.getTotalCost());

        if (planBean.getStatus() == 0) {
            mReasonTv.setVisibility(GONE);
            mNextTimeTv.setVisibility(GONE);

        } else if (planBean.getStatus() == 3) {
            mReasonTv.setVisibility(VISIBLE);
            mNextTimeTv.setVisibility(VISIBLE);

            mReasonTv.setText(planBean.getCoordinateReason());
            try {
                mNextTimeTv.setText(getFormatTime(Long.parseLong(planBean.getCoordinateNextHandleStartTime())));
            } catch (Exception e) {

            }
        }


        addPriceList();

    }

    private void addPriceList() {

        ArrayList<MainTainBean> list = planBean.getmMaintainList();

        mPriceContainerLy.removeAllViews();

        for (int i = 0; i < list.size(); i++) {

            PeijianUILayout peijianUILayout = new PeijianUILayout(getContext());
            peijianUILayout.bindData(list.get(i));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(0, DimenUtils.dp2px(10, getContext()), 0, 0);

            mPriceContainerLy.addView(peijianUILayout, layoutParams);

        }
    }


    private String getFormatTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/ HH:mm");
        java.util.Date dt = new Date(time);
        String sDateTime = sdf.format(dt);  //得到精确到秒的表示：08/31/2006 21:08:00
        return sDateTime;
    }


    public interface OnOperationListener {

        void onDelete(MainTainBean mainTainBean);

        void onPriceChange();

        void onCountChange();

    }

    public OnOperationListener listener;

    public void setOperationListener(OnOperationListener listener) {
        this.listener = listener;
    }


}

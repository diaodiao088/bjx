package com.bjxapp.worker.ui.view.fragment.ctrl;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.bjxapp.worker.R;

/**
 * Created by zhangdan on 2018/9/25.
 * <p>
 * comments:
 */

public class PageSlipingCtrl {

    private View mTotalDivTv;
    private TextView mRedotTotalTv;
    private View mNewBillDivTv;
    private TextView mRedotNewBillTv;
    private View mWaitingContactDivTv;
    private TextView mRedotContactTv;
    private View mWaitingRoomDivTv;
    private TextView mRedotWaitingRoomTv;
    private View mAlreadyEnterDivTv;
    private TextView mRedotAlreadyEnterTv;
    private View mWaitingPayDivTv;
    private TextView mRedotWaitingPayTv;

    private View mCtx;

    public PageSlipingCtrl(View ctx) {
        this.mCtx = ctx;
    }

    public void init(){

        mTotalDivTv = mCtx.findViewById(R.id.total_divider);
        mRedotTotalTv = mCtx.findViewById(R.id.total_reddot_tv);

        mNewBillDivTv = mCtx.findViewById(R.id.new_bill_divider);
        mRedotNewBillTv = mCtx.findViewById(R.id.new_bill_reddot_tv);

        mWaitingContactDivTv = mCtx.findViewById(R.id.waiting_contact_divider);
        mRedotContactTv = mCtx.findViewById(R.id.waiting_contact_redot_tv);

        mWaitingRoomDivTv = mCtx.findViewById(R.id.waiting_room_divider);
        mRedotWaitingRoomTv = mCtx.findViewById(R.id.waiting_room_redot);

        mAlreadyEnterDivTv = mCtx.findViewById(R.id.already_enter_divider);
        mRedotAlreadyEnterTv = mCtx.findViewById(R.id.already_enter_redot);

        mWaitingPayDivTv = mCtx.findViewById(R.id.waiting_pay_divider);
        mRedotWaitingPayTv = mCtx.findViewById(R.id.waiting_pay_redot);

    }


    public void updateUnderLineUi(int type){

        switch (type){
            case 0:
                mTotalDivTv.setVisibility(View.VISIBLE);
                mNewBillDivTv.setVisibility(View.GONE);
                mWaitingContactDivTv.setVisibility(View.GONE);
                mWaitingRoomDivTv.setVisibility(View.GONE);
                mAlreadyEnterDivTv.setVisibility(View.GONE);
                mWaitingPayDivTv.setVisibility(View.GONE);
                break;
            case 1:
                mTotalDivTv.setVisibility(View.GONE);
                mNewBillDivTv.setVisibility(View.VISIBLE);
                mWaitingContactDivTv.setVisibility(View.GONE);
                mWaitingRoomDivTv.setVisibility(View.GONE);
                mAlreadyEnterDivTv.setVisibility(View.GONE);
                mWaitingPayDivTv.setVisibility(View.GONE);
                break;
            case 2:
                mTotalDivTv.setVisibility(View.GONE);
                mNewBillDivTv.setVisibility(View.GONE);
                mWaitingContactDivTv.setVisibility(View.VISIBLE);
                mWaitingRoomDivTv.setVisibility(View.GONE);
                mAlreadyEnterDivTv.setVisibility(View.GONE);
                mWaitingPayDivTv.setVisibility(View.GONE);
                break;
            case 3:
                mTotalDivTv.setVisibility(View.GONE);
                mNewBillDivTv.setVisibility(View.GONE);
                mWaitingContactDivTv.setVisibility(View.GONE);
                mWaitingRoomDivTv.setVisibility(View.VISIBLE);
                mAlreadyEnterDivTv.setVisibility(View.GONE);
                mWaitingPayDivTv.setVisibility(View.GONE);
                break;
            case 4:
                mTotalDivTv.setVisibility(View.GONE);
                mNewBillDivTv.setVisibility(View.GONE);
                mWaitingContactDivTv.setVisibility(View.GONE);
                mWaitingRoomDivTv.setVisibility(View.GONE);
                mAlreadyEnterDivTv.setVisibility(View.VISIBLE);
                mWaitingPayDivTv.setVisibility(View.GONE);
                break;
            case 5:
                mTotalDivTv.setVisibility(View.GONE);
                mNewBillDivTv.setVisibility(View.GONE);
                mWaitingContactDivTv.setVisibility(View.GONE);
                mWaitingRoomDivTv.setVisibility(View.GONE);
                mAlreadyEnterDivTv.setVisibility(View.GONE);
                mWaitingPayDivTv.setVisibility(View.VISIBLE);
                break;
        }
    }






}

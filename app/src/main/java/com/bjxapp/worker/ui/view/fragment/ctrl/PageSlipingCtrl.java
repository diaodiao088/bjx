package com.bjxapp.worker.ui.view.fragment.ctrl;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.bjxapp.worker.R;
import com.bjxapp.worker.model.OrderDes;

import java.util.ArrayList;

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

    public void init() {

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


    public void updateUnderLineUi(int type) {

        switch (type) {
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

    public static final int TOTAL_COUNT = 0x00;
    public static final int NEW_BILL_COUNT = 0x01;
    public static final int WAITING_CONTACT_COUNT = 0x02;
    public static final int WAITING_ROOM = 0x03;
    public static final int ALREADY_ROOM = 0X04;
    public static final int WAITING_PAY = 0x05;

    public void updateRedot(ArrayList<OrderDes> list) {

        if (list == null || list.size() <= 0) {
            mRedotTotalTv.setVisibility(View.GONE);
            mRedotNewBillTv.setVisibility(View.GONE);
            mRedotAlreadyEnterTv.setVisibility(View.GONE);
            mRedotWaitingPayTv.setVisibility(View.GONE);
            mRedotContactTv.setVisibility(View.GONE);
            mRedotWaitingRoomTv.setVisibility(View.GONE);
            return;
        }

        mRedotTotalTv.setText(String.valueOf(list.size()));
        mRedotTotalTv.setVisibility(View.VISIBLE);

        int newBillCount = getSpecItemCount(NEW_BILL_COUNT, list);
        if (newBillCount > 0) {
            mRedotNewBillTv.setVisibility(View.VISIBLE);
            mRedotNewBillTv.setText(String.valueOf(newBillCount));
        }

        int alreadyEnterCount = getSpecItemCount(ALREADY_ROOM, list);
        if (alreadyEnterCount > 0) {
            mRedotAlreadyEnterTv.setVisibility(View.VISIBLE);
            mRedotAlreadyEnterTv.setText(String.valueOf(alreadyEnterCount));
        }

        int waitPayCount = getSpecItemCount(WAITING_PAY, list);
        if (waitPayCount > 0) {
            mRedotWaitingPayTv.setVisibility(View.VISIBLE);
            mRedotWaitingPayTv.setText(String.valueOf(waitPayCount));
        }

        int contactCount = getSpecItemCount(WAITING_CONTACT_COUNT, list);
        if (contactCount > 0) {
            mRedotContactTv.setVisibility(View.VISIBLE);
            mRedotContactTv.setText(String.valueOf(contactCount));
        }

        int waitingRoomCount = getSpecItemCount(WAITING_ROOM, list);
        if (waitingRoomCount > 0) {
            mRedotWaitingRoomTv.setVisibility(View.VISIBLE);
            mRedotWaitingRoomTv.setText(String.valueOf(waitingRoomCount));
        }

    }

    public int getSpecItemCount(int type, ArrayList<OrderDes> list) {

        int count = 0;

        for (int i = 0; i < list.size(); i++) {
            OrderDes item = list.get(i);
            if (item.getProcessStatus() == type) {
                count++;
            }
        }

        return count;
    }

}

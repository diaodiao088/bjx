package com.bjxapp.worker.ui.view.fragment.subfragment;

import android.os.Bundle;

import com.bjxapp.worker.R;
import com.bjxapp.worker.model.OrderDes;
import com.bjxapp.worker.ui.view.fragment.ctrl.PageSlipingCtrl;

import java.util.ArrayList;

/**
 * Created by zhangdan on 2018/9/25.
 * <p>
 * comments:
 */

public class WaitingPayFragment extends BillBaseFragment {

    public static WaitingPayFragment getIns() {
        WaitingPayFragment fragment = new WaitingPayFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_total;
    }

    @Override
    protected ArrayList<OrderDes> getOrderArray() {

        ArrayList<OrderDes> orderList = new ArrayList<>();
        for (int i = 0; i < mOrdersArray.size(); i++) {
            OrderDes item = mOrdersArray.get(i);
            if (item.getProcessStatus() == PageSlipingCtrl.WAITING_PAY){
                orderList.add(item);
            }
        }

        return sortArray(orderList);
    }
}

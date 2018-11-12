package com.bjxapp.worker.ui.view.fragment.subfragment;

import android.os.Bundle;

import com.bjx.master.R;;
import com.bjxapp.worker.model.OrderDes;
import com.bjxapp.worker.ui.view.fragment.ctrl.PageSlipingCtrl;

import java.util.ArrayList;

/**
 * Created by zhangdan on 2018/9/25.
 * comments:
 */

public class WaitingContactFragment extends BillBaseFragment {

    public static WaitingContactFragment getIns() {
        WaitingContactFragment fragment = new WaitingContactFragment();
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
            if (item.getProcessStatus() == PageSlipingCtrl.WAITING_CONTACT_COUNT){
                orderList.add(item);
            }
        }

        return sortArray(orderList);
    }

}

package com.bjxapp.worker.ui.view.fragment.subfragment;

import android.os.Bundle;

import com.bjx.master.R;
import com.bjxapp.worker.model.OrderDes;

import java.util.ArrayList;

;

/**
 * Created by zhangdan on 2018/9/25.
 * <p>
 * comments:
 */

public class JieSuanFragment extends BillBaseFragment {

    public static JieSuanFragment getIns() {
        JieSuanFragment fragment = new JieSuanFragment();
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
            if (item.getSettleStatus() == 3){
                orderList.add(item);
            }
        }

        return sortArray(orderList);
    }
}

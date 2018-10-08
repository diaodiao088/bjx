package com.bjxapp.worker.ui.view.fragment.subfragment;

import android.os.Bundle;

import com.bjxapp.worker.R;

/**
 * Created by zhangdan on 2018/9/25.
 * comments:
 */

public class NewBillFragment extends BillBaseFragment {

    public static NewBillFragment getIns(){
        NewBillFragment fragment = new NewBillFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_total;
    }
}

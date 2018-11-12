package com.bjxapp.worker.ui.view.fragment.subfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bjx.master.R;;

/**
 * Created by zhangdan on 2018/9/25.
 * <p>
 * comments:
 */

public class TotalFragment extends BillBaseFragment {

    public static TotalFragment getIns() {
        TotalFragment fragment = new TotalFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_total;
    }
}

package com.bjxapp.worker.ui.view.fragment.subfragment;

import android.os.Bundle;

import com.bjxapp.worker.R;

/**
 * Created by zhangdan on 2018/9/25.
 * <p>
 * comments:
 */

public class WaitingRoomFragment extends BillBaseFragment {

    public static WaitingRoomFragment getIns() {
        WaitingRoomFragment fragment = new WaitingRoomFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_total;
    }
}

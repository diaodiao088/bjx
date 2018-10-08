package com.bjxapp.worker.ui.view.fragment.subfragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by zhangdan on 2018/9/25.
 * comments:
 */

public class BillAdapter extends FragmentPagerAdapter {

    private ArrayList<BillBaseFragment> mFragmentList = new ArrayList<>();

    public void addFragment(BillBaseFragment fragment) {
        if (fragment != null) {
            mFragmentList.add(fragment);
        }
    }

    public BillAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}

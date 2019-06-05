package com.bjxapp.worker.ui.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.ui.view.base.BaseFragment;
import com.bjxapp.worker.ui.view.fragment.subfragment.BillBaseFragment;
import com.bjxapp.worker.ui.widget.BlockableViewPager;
import com.bjxapp.worker.utils.Utils;

import java.util.ArrayList;

public class Fragment_Main_Third_New extends BaseFragment implements OnClickListener {

    protected static final String TAG = "通知";

    private BlockableViewPager mViewPager;

    private View mBillView;
    private TextView mBillTv;
    private TextView mBillRedotTv;

    private View mCompanyView;
    private TextView mCompanyTv;
    private TextView mCompanyRedotTv;

    private NotifyAdapter mAdapter;


    @Override
    protected void initView() {
        registerUpdateUIBroadcast();

        mViewPager = (BlockableViewPager) findViewById(R.id.main_viewpager);
        mViewPager.setOffscreenPageLimit(2);

        mAdapter = new NotifyAdapter(getChildFragmentManager());
        mAdapter.addFragment(new Fragment_Main_Third());
        mAdapter.addFragment(new Fragment_Main_Third());

        mViewPager.setAdapter(mAdapter);

        mBillView = findViewById(R.id.bill_divider);
        mBillTv = (TextView) findViewById(R.id.title_bill_tv);
        mBillRedotTv = (TextView) findViewById(R.id.bill_redot);

        mCompanyRedotTv = (TextView) findViewById(R.id.company_redot);
        mCompanyTv = (TextView) findViewById(R.id.title_company_tv);
        mCompanyView = findViewById(R.id.company_divider);

    }

    private void changeState(boolean isCompany) {

        if (isCompany) {
            mBillView.setVisibility(View.GONE);
            mCompanyView.setVisibility(View.VISIBLE);

            mCompanyTv.setTextColor(Color.parseColor("#00a551"));
            mBillTv.setTextColor(Color.parseColor("#545454"));

        } else {
            mBillView.setVisibility(View.VISIBLE);
            mCompanyView.setVisibility(View.GONE);

            mBillTv.setTextColor(Color.parseColor("#00a551"));
            mCompanyTv.setTextColor(Color.parseColor("#545454"));
        }

    }

    @Override
    protected void finish() {

    }

    @Override
    protected int onCreateContent() {
        return R.layout.fragment_main_third_new;
    }

    @Override
    public void refresh(int enterType) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case 0:
                break;
            default:
                break;
        }
    }


    @Override
    protected String getPageName() {
        return TAG;
    }

    @Override
    public void onDestroy() {
        try {

            //注销广播
            mActivity.unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
        }

        super.onDestroy();
    }

    /**
     * 定义广播接收器（内部类）
     *
     * @author Jason
     */
    private UpdateUIBroadcastReceiver broadcastReceiver;

    private class UpdateUIBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.showLongToast(mActivity, "您有新通知，请查看！");
        }
    }

    /**
     * 动态注册广播
     */
    private void registerUpdateUIBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.PUSH_ACTION_MESSAGE_MODIFIED);
        broadcastReceiver = new UpdateUIBroadcastReceiver();
        mActivity.registerReceiver(broadcastReceiver, filter);
    }



    class NotifyAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> mFragmentList = new ArrayList<>();

        public void addFragment(Fragment fragment) {
            if (fragment != null) {
                mFragmentList.add(fragment);
            }
        }

        public NotifyAdapter(FragmentManager fm) {
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

}

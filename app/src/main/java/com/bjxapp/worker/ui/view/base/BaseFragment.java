package com.bjxapp.worker.ui.view.base;

import com.bjxapp.worker.MainActivity;
import com.bjxapp.worker.dataupload.Uploader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {
    protected View mRoot;
    protected MainActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setActivity();
        if (mRoot == null) {
            mRoot = inflater.inflate(onCreateContent(), container, false);
            mRoot.setClickable(true);
            try {
                initView();
            }catch(Exception e) {
                finish();
            }
        } else {
            ViewGroup parent = (ViewGroup) mRoot.getParent();
            if (parent != null) {
                parent.removeView(mRoot);
            }
        }
        ButterKnife.bind(this , mRoot);
        return mRoot;
    }
 
    @Override
    public void onResume() {
        super.onResume();
        Uploader.onPageStart(getActivity(), getPageName());
    }
    
    @Override
    public void onPause() {
        super.onPause();
        Uploader.onPageEnd(getActivity(), getPageName());
    }
    
    @Override
    public void onStop() {
        super.onStop();
    }
    
    public Activity getBaseActivity() {
        return mActivity;
    }

    protected void setActivity() {
        mActivity = (MainActivity) getActivity();
    }

    protected void initView() {
    }

    protected void finish() {

    }
    
    protected int onCreateContent() {
        return 0;
    }
    
	/**
	 * 设置fragment name
	 */
	protected abstract String getPageName();

	protected abstract void refresh(int enterType);
	
    protected View findViewById(int id) {
        return mRoot.findViewById(id);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}

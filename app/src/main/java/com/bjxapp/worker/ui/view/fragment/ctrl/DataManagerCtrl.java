package com.bjxapp.worker.ui.view.fragment.ctrl;

import com.bjxapp.worker.model.FirstPageResult;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

/**
 * Created by zhangdan on 2018/9/26.
 * <p>
 * comments:
 */

public class DataManagerCtrl {

    private static DataManagerCtrl sIns;

    private FirstPageResult pageResult;

    private ArrayList<OnDataLoadFinishListener> mListenerList = new ArrayList<>();

    private DataManagerCtrl() {

    }

    public static DataManagerCtrl getIns() {
        if (sIns == null) {
            sIns = new DataManagerCtrl();
        }
        return sIns;
    }

    public void setPageResult(FirstPageResult result) {
        this.pageResult = result;
        notifyFragment();
    }

    private void notifyFragment() {
        for (OnDataLoadFinishListener listener : mListenerList) {
            if (listener != null) {
                listener.onLoadFinish();
            }
        }
    }

    public FirstPageResult getPageResult() {
        return pageResult;
    }

    public interface OnDataLoadFinishListener {

        void onLoadFinish();

    }

    public synchronized void registerListener(OnDataLoadFinishListener listener) {
        mListenerList.add(listener);
    }

    public synchronized void unRegisterListener(OnDataLoadFinishListener listener){
        try{
            mListenerList.remove(listener);
        }catch (Exception e){
            e.printStackTrace();
        }

    }



}

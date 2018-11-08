package com.bjxapp.worker.ui.view.fragment.ctrl;

import com.bjxapp.worker.model.FirstPageResult;
import com.bjxapp.worker.model.OrderDes;

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

    private boolean isDataDirty;

    private ArrayList<OrderDes> mList;

    private ArrayList<OnDataLoadFinishListener> mListenerList = new ArrayList<>();

    private DataManagerCtrl() {

    }

    public static DataManagerCtrl getIns() {
        if (sIns == null) {
            sIns = new DataManagerCtrl();
        }
        return sIns;
    }

    public void setPageResult(ArrayList<OrderDes> result) {
        this.mList = result;
        markDataDirty(false);
        notifyFragment();
    }


    private void notifyFragment() {
        for (OnDataLoadFinishListener listener : mListenerList) {
            if (listener != null) {
                listener.onLoadFinish();
            }
        }
    }

    public ArrayList<OrderDes> getPageResult() {
        return mList;
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

    public void markDataDirty(boolean isDataDirty){
        this.isDataDirty = isDataDirty;
    }

    public boolean isDataDirty() {
        return isDataDirty;
    }

    public void setDataDirty(boolean dataDirty) {
        isDataDirty = dataDirty;
    }
}

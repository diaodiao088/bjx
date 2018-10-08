package com.bjxapp.worker.utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.bjxapp.worker.utils.diskcache.DiskCacheManager.DataType;
import com.bjxapp.worker.utils.http.HttpUtils;

public class BitmapManager {
	private Context mContext;
    private static BitmapManager instance;
    private ImageCache imageCache;
    private Semaphore mutext = new Semaphore(1);
    private List<BitmapDownloadInfo> bitmapDownloads = new ArrayList<BitmapDownloadInfo>();
    private Thread downdlingThread;
    private Handler bitmapLoadedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            String url = msg.getData().getString("url");
            BitmapDownloadInfo bdi = getBitmapDownload(url);
            synchronized (mutext) {
                if (bdi != null) {
                    traversalListener(bitmap, bdi);
                    bitmapDownloads.remove(bdi);
                }
                if (bdi != null && bdi.size != null) {
                	doBitmapDownload(bdi.size);
                } else {
                    doBitmapDownload();
                }
            }
        }
    };

    private BitmapManager(Context context) {
    	mContext = context;
        imageCache = ImageCache.findOrCreateCache(context);
    }

    public static BitmapManager getInstance(Context context) {
        if (instance == null) {
            instance = new BitmapManager(context.getApplicationContext());
        }
        return instance;
    }

    // 遍历监听器
    private void traversalListener(Bitmap bitmap, BitmapDownloadInfo bdi) {
        if (!bdi.listeners.isEmpty()) {
            for (OnBitmapLoadListener listener : bdi.listeners) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    try {
                        Config con = bitmap.getConfig();
                        if (con == null) {
                            con = Config.ARGB_8888;
                        }
                        
                        Bitmap desBitmap = bitmap.copy(con, false);
                        listener.onLoaded(bdi.url, desBitmap, true);
                    } catch (Throwable th) {
                        //防止OutOfMemory
                    }
                } else {
                    listener.onLoaded(bdi.url, null, false);
                }
            }
        }
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    private void doBitmapDownload() {
        if (bitmapDownloads.isEmpty()) {
            return;
        }
        BitmapDownloadInfo bdi = bitmapDownloads.get(bitmapDownloads.size() - 1);
        final String url = bdi.url;
        final DataType savePlace = bdi.savePlace;
        downdlingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = imageCache.getBitmapFromDiskCache(url, savePlace);
                if (bitmap == null) {
                    bitmap = HttpUtils.getURLBitmap(url);
                }
                imageCache.addBitmapToCache(url, bitmap, savePlace);
                Message msg = new Message();
                msg.obj = bitmap;
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                msg.setData(bundle);
                bitmapLoadedHandler.sendMessage(msg);
            }
        });
        downdlingThread.start();
    }

    private void doBitmapDownload(final int[] size) {
        if (bitmapDownloads.isEmpty()) {
            return;
        }
        BitmapDownloadInfo bdi = bitmapDownloads.get(bitmapDownloads.size() - 1);
        final String url = bdi.url;
        final DataType savePlace = bdi.savePlace;
        downdlingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = imageCache.getBitmapFromDiskCache(url, savePlace, size[0], size[1]);
                if (bitmap == null) {
                    String filePath = HttpUtils.getURLFile(mContext, url,savePlace);
                    if(filePath != null){
                    	bitmap = imageCache.getBitmapFromDiskCache(filePath, savePlace, size[0], size[1]);
                    }
                }
                imageCache.addBitmapToMemCache(url, bitmap);
                Message msg = new Message();
                msg.obj = bitmap;
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                msg.setData(bundle);
                bitmapLoadedHandler.sendMessage(msg);
            }
        });
        downdlingThread.start();
    }

    private BitmapDownloadInfo getBitmapDownload(String url) {
        for (BitmapDownloadInfo bd : bitmapDownloads) {
            if (bd.url.equals(url))
                return bd;
        }
        return null;
    }

    private void putBitmapDownload(String url, DataType savePlace, OnBitmapLoadListener listener) {
        synchronized (mutext) {
            BitmapDownloadInfo bd = getBitmapDownload(url);
            if (bd == null) {
                bd = new BitmapDownloadInfo();
                bd.url = url;
                bd.savePlace = savePlace;
                bd.listeners.add(listener);
                bitmapDownloads.add(bd);
            } else {
                bd.listeners.add(listener);
            }
        }
        if (downdlingThread == null || !downdlingThread.isAlive()) {
            doBitmapDownload();
        }
    }

    private void putBitmapDownload(String url, DataType savePlace, OnBitmapLoadListener listener, int[] size) {
        synchronized (mutext) {
            BitmapDownloadInfo bd = getBitmapDownload(url);
            if (bd == null) {
                bd = new BitmapDownloadInfo();
                bd.url = url;
                bd.savePlace = savePlace;
                bd.listeners.add(listener);
                bd.size = size;
                bitmapDownloads.add(bd);
            } else {
                bd.listeners.add(listener);
            }
        }
        if (downdlingThread == null || !downdlingThread.isAlive()) {
            doBitmapDownload(size);
        }
    }

    /**
     * 通过url获得bitmap对象
     * <p/>
     * 获取SDCard上资源和网络资源均异步处理，回调方式实现
     *
     * @param url      资源url
     * @param type     存储类型：缓存或闹钟数据资源
     * @param listener 回调接口
     */
    //TODO:loadLocalOnly never used
    public void loadBitmap(String url, DataType saveToType,
                           OnBitmapLoadListener listener) {
        if (url == null) {
            return;
        }
        Bitmap bitmap = imageCache.getBitmapFromCache(url, saveToType);
        if (bitmap != null) {
            if (listener != null) {
                listener.onLoaded(url, bitmap, true);
            }
        } else {
            putBitmapDownload(url, saveToType, listener);
        }
    }

    public void loadBitmap(String url, DataType saveToType,
                           OnBitmapLoadListener listener, int[] size) {
        if (url == null) {
            return;
        }
        Bitmap bitmap = imageCache.getBitmapFromCache(url, saveToType);
        if (bitmap != null) {
            if (listener != null) {
                listener.onLoaded(url, bitmap, true);
            }
        } else {
            putBitmapDownload(url, saveToType, listener, size);
        }
    }

    /**
     * 直接获取内存bitmap，可能会返回null
     */
    public Bitmap getBitmapFromMemCache(String url) {
        return imageCache.getBitmapFromMemCache(url);
    }

    /**
     * 获取SDCard bitmap，可能会返回null
     */
    public Bitmap getBitmapFromDiskCache(String url, DataType type) {
        return imageCache.getBitmapFromDiskCache(url, type);
    }

    /**
     * 获取SDCard bitmap，可能会返回null
     */
    public Bitmap getBitmapFromDiskCache(String url) {
        return imageCache.getBitmapFromDiskCache(url);
    }

    /**
     * 直接获取本地bitmap，可能会返回null
     * 注：该操作为同一线程中，取sdcard资源时可能会卡， 建议列表等这种需要快速滑动的控件获取bitmap，使用自定义缓存图片View
     */
    public Bitmap getBitmapFromLocal(String url, DataType type) {
        Bitmap bitmap = getBitmapFromMemCache(url);
        if (bitmap == null) {
            bitmap = getBitmapFromDiskCache(url, type);
        }
        return bitmap;
    }

    /**
     * 直接获取本地bitmap，可能会返回null
     * 注：该操作为同一线程中，取sdcard资源时可能会卡， 建议列表等这种需要快速滑动的控件获取bitmap，使用自定义缓存图片View
     * 在imgcache无法找到，则直接去Local，Live，Server三个文件夹中寻找
     */
    public Bitmap getBitmapFromLocal(String url) {
        Bitmap bitmap = getBitmapFromMemCache(url);
        return bitmap;
    }

    /**
     * 保存bitmap到本地(内存和sdcard)
     */

    public void saveLocalBitmap(String data, Bitmap bitmap, DataType type) {
        imageCache.addBitmapToCache(data, bitmap, type);
    }

    public void clearMemCache() {
        if (imageCache != null) {
            imageCache.clearMemCache();
        }
    }

    /**
     * 保存bitmap到sdcard
     */

    public void saveDiskBitmap(String data, Bitmap bitmap, DataType type) {
        imageCache.addBitmapToDiskCache(data, bitmap, type);
    }

    /**
     * 查找本地是否保存该url的资源（用户手动删除资源后会引起判断准确性，不过影响不大）
     */
    public boolean hasLocalCache(String url) {
        return imageCache.hasLocalCache(url);
    }

    public boolean isLoadingUrl(String url) {
        BitmapDownloadInfo bdi = getBitmapDownload(url);
        return bdi != null;
    }

    public interface OnBitmapLoadListener {
        /**
         * 4.4增加isSuccessful 用以判断调用是否成功或者失败。
         * 如果isSuccessful==false,bitmap为null
         * isSuccessful==true,btimap有效
         */
        void onLoaded(final String url, final Bitmap bitmap, boolean isSuccessful);
    }

    class BitmapDownloadInfo {
        String url;
        DataType savePlace;
        List<OnBitmapLoadListener> listeners = new ArrayList<OnBitmapLoadListener>();
        int[] size = null;
    }

}

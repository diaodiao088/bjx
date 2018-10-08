/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bjxapp.worker.utils.image;

import java.io.File;

import com.bjxapp.worker.utils.Env;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager.DataType;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.support.v4.util.LruCache;

/**
 * This class holds our bitmap caches (memory and disk).
 */
public class ImageCache {

    //private static final String TAG = "ImageCache";
    private static ImageCache instance;
    private DiskCacheManager mDiskManager;
    private LruCache<String, Bitmap> mMemoryCache;
    private static final int DEFAULT_MEMERY_SIZE = 32;
    private static final int MAX_MEMORY_SIZE = 64;

    /**
     * Creating a new ImageCache object using the specified parameters.
     *
     * @param context     The context to use
     * @param cacheParams The cache parameters to use to initialize the cache
     */
    private ImageCache(Context context) {
        init(context);
    }

    /**
     * Find and return an existing ImageCache
     *
     * @param cacheParams The cache parameters to use if creating the ImageCache
     * @return An existing retained ImageCache object or a new one if one did
     * not exist
     */
    public static ImageCache findOrCreateCache(Context context) {
        if (instance == null) {
            instance = new ImageCache(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Initialize the cache, providing all parameters.
     *
     * @param context     The context to use
     * @param cacheParams The cache parameters to initialize the cache
     */
    private void init(Context context) {

        int memClass = 0;
        if (Env.getSDKLevel() >= 5) {
            memClass = ((ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
        } else {
            memClass = DEFAULT_MEMERY_SIZE;
        }

        if (memClass > MAX_MEMORY_SIZE) memClass = MAX_MEMORY_SIZE;
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = 1024 * 1024 * memClass / 8;
//		final int cacheSize = 1;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            /**
             * Measure item size in bytes rather than units which is more
             * practical for a bitmap cache
             *
             * 根据bitmap的大小度量比根据bitmap个数度量更适合
             */
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                int size = bitmap.getRowBytes() * bitmap.getHeight();
                return size;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key,Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                /*
                if (oldValue != null && !oldValue.isRecycled()) {
                    oldValue.recycle();
                    oldValue = null;
                }
                */
            }

        };

        // Set up disk cache
        mDiskManager = DiskCacheManager.getInstance(context);

    }

    public void clearMemCache() {
        if (mMemoryCache != null) {
            mMemoryCache.evictAll();
        }
    }

    /**
     * 无论添加或者从缓存取 的bitmap对象必须和界面使用的bitmap对象分开，即复制一份新的对象
     *
     * @param data
     * @param bitmap
     * @param type
     */
    public void addBitmapToCache(String data, Bitmap bitmap, DataType type) {
        try {
            if (data == null || bitmap == null) {
                return;
            }
            Config con = bitmap.getConfig();
            if (con == null) {
                con = Config.ARGB_8888;
            }
            bitmap = bitmap.copy(con, false);
            addBitmapToMemCache(data, bitmap);
            addBitmapToDiskCache(data, bitmap, type);
        } catch (Throwable t) {
            clearMemCache();
        }
    }

    // 添加到内存
    public void addBitmapToMemCache(String data, Bitmap bitmap) {
    	if(data == null || bitmap == null) return;
    	
        if (mMemoryCache != null && mMemoryCache.get(data) == null) {
            mMemoryCache.put(data, bitmap);
        }
    }

    // 添加到sdcard
    public void addBitmapToDiskCache(String data, Bitmap bitmap, DataType type) {
        if (mDiskManager != null && !mDiskManager.containsKey(type, data)) {
            mDiskManager.putBitmap(type, data, bitmap);
        }
    }

    /**
     * Get from memory cache. 无论添加或者从缓存取 的bitmap对象必须和界面使用的bitmap对象分开，即复制一份新的对象
     *
     * @param data Unique identifier for which item to get
     * @return The bitmap if found in cache, null otherwise
     */
    public Bitmap getBitmapFromMemCache(String data) {
        try {
            if (mMemoryCache != null && data != null) {
                final Bitmap memBitmap = mMemoryCache.get(data);
                if (memBitmap != null && memBitmap.isRecycled() == false) {
                    Config con = memBitmap.getConfig();
                    if (con == null) {
                        con = Config.ARGB_8888;
                    }
                    return memBitmap.copy(con, false);
                }
            }
        } catch (Throwable t) {
            clearMemCache();
        }
        return null;
    }
    
    public Bitmap getBitmapFromMemCacheWithoutCopy(String data) {
        try {
            if (mMemoryCache != null && data != null) {
                final Bitmap memBitmap = mMemoryCache.get(data);
                if (memBitmap != null && memBitmap.isRecycled() == false) {
                    Config con = memBitmap.getConfig();
                    if (con == null) {
                        con = Config.ARGB_8888;
                    }
                    return memBitmap;
                }
            }
        } catch (Throwable t) {
            clearMemCache();
        }
        return null;
    }
    
    /**
     * clear bitmapMemCache
     */

    /**
     * Get from disk cache.
     *
     * @param data Unique identifier for which item to get
     * @return The bitmap if found in cache, null otherwise
     */
    public Bitmap getBitmapFromDiskCache(String key, DataType type) {
        if (mDiskManager != null && key != null) {
            return mDiskManager.getBitmap(type, key);
        }
        return null;
    }

    public Bitmap getBitmapFromDiskCache(String key) {
        if (mDiskManager != null && key != null) {
            return mDiskManager.getBitmap(key);
        }
        return null;
    }

    /**
     * 获取指定大小的bitmap对象
     *
     * @param key
     * @param type
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap getBitmapFromDiskCache(String key, DataType type, int reqWidth, int reqHeight) {
        if (mDiskManager != null && key != null) {
            return mDiskManager.getBitmap(type, key, reqWidth, reqHeight);
        }
        return null;
    }
	
    /**
     * 获取url的本地bitmap
     *
     * @param url
     * @return url对应的bitmap，不存在则返回null
     */
    public Bitmap getBitmapFromCache(String url) {
        return getBitmapFromCache(url, DataType.LiveCache);
    }

    /**
     * 获取url的本地bitmap
     *
     * @param url
     * @return url对应的bitmap，不存在则返回null
     */
    public Bitmap getBitmapFromCache(String url, DataType type) {
        try {
            Bitmap cache = getBitmapFromMemCache(url);
            if (cache == null) {
                cache = getBitmapFromDiskCache(url, type);
            }
            return cache;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    /**
     * 获取url指定大小的本地bitmap
     *
     * @param url
     * @return url对应的bitmap，不存在则返回null
     */
    public Bitmap getBitmapFromCache(String url, DataType type, int reqWidth,
                                     int reqHeight) {
        Bitmap cache = getBitmapFromMemCache(url);
        if (cache == null) {
            cache = getBitmapFromDiskCache(url, type, reqWidth, reqHeight);
        }
        return cache;
    }

    public File getDiskCacheFile(String url, DataType type){
    	return mDiskManager.getFile(type, url);	
    }

    public boolean hasLocalCache(String key) {
        boolean isExitInSdcard = mDiskManager.containsKey(DataType.LiveCache,
                key);
        Bitmap bitmap = getBitmapFromMemCache(key);
        boolean isExitInMem = bitmap != null;
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return isExitInSdcard || isExitInMem;
    }

    public void clearCaches(DataType type) {
        mDiskManager.clearCache(type);
        mMemoryCache.evictAll();
    }

}

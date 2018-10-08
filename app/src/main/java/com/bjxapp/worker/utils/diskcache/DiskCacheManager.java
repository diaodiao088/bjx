package com.bjxapp.worker.utils.diskcache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.bjxapp.worker.utils.SDCardUtils;

/**
 * sdcard 及 手机应用缓存资源管理类
 * 
 */
public class DiskCacheManager {

	// 此处加入枚举类型，一种类型对应一个文件夹路径，文件夹名即为枚举类型名字小写
	public enum DataType {
		LiveCache, UserData, Local
	}

	// sdcard上每个文件夹默认大小50M
	private static final long DISK_CACHE_DEF_SIZE = 1024 * 1024 * 50;
	// 应用缓存下每个文件夹默认大小5M
	private static final long APP_CACHE_DEF_SIZE = 1024 * 1024 * 5;

	private static DiskCacheManager instance;

	private Context context;
	/** 手机存储卡 map，优先使用 */
	private Map<DataType, DiskLruCache> diskCacheMap;
	/** 应用缓存 map，无sdcard或sdcard被占用时使用 */
	private Map<DataType, DiskLruCache> appCacheMap;

	// TODO:之后需要还原此接口
	// private DiskCacheManager(Context context, FlushStratage flushStratage) {
	// this.context = context.getApplicationContext();
	// flushDiskCache(flushStratage);
	// }
	//
	// /**
	// * 获取sdcard 及 手机应用缓存资源管理类
	// *
	// * @param flushStratage
	// * 如果不关心清理，可以赋值null
	// */
	// public static DiskCacheManager getInstance(Context context,
	// FlushStratage flushStratage) {
	// if (instance == null) {
	// instance = new DiskCacheManager(context, flushStratage);
	// }
	// return instance;
	// }

	private DiskCacheManager(Context context) {
		this.context = context.getApplicationContext();
	}

	/**
	 * 获取sdcard 及 手机应用缓存资源管理类
	 * 
	 * @param flushStratage
	 *            如果不关心清理，可以赋值null
	 */
	public static DiskCacheManager getInstance(Context context) {
		if (instance == null) {
			instance = new DiskCacheManager(context);
		}
		return instance;
	}

	/**
	 * 管理类中是否包含该key
	 * 
	 * 注：仅通过key判断是否存在文件不准确，文件被手动（通过第三方文件管理器）删除后，key不会马上变化
	 * 
	 * @param type
	 * @param url
	 * @return
	 */
	public boolean containsKey(DataType type, String key) {
		DiskLruCache diskLruCache = getDiskLruCacheFromDataType(type);
		if (diskLruCache != null) {
			return diskLruCache.containsKey(key);
		}
		return false;
	}

	/**
	 * 通过查找文件返回该文件是否存在，精确查找，速度可能会慢点
	 * 
	 * @param type
	 * @param url
	 * @return
	 */

	public boolean containsFile(DataType type, String name) {
		DiskLruCache diskLruCache = getDiskLruCacheFromDataType(type);
		if (diskLruCache != null) {
			return diskLruCache.containsFile(name);
		}
		return false;
	}

	public boolean containsFile(String name) {
		return containsFile(DataType.UserData, name)
				|| containsFile(DataType.Local, name)
				|| containsFile(DataType.LiveCache, name);
	}

	/**
	 * 清除该类型（对应目录下）的所有图片
	 * 
	 * @param type
	 */
	public void clearCache(DataType type) {
		DiskLruCache diskLruCache = getDiskLruCacheFromDataType(type);
		if (diskLruCache != null) {
			diskLruCache.clearCache();
		}
	}

	/**
	 * 存储bitmap到对应类型的目录下
	 * 
	 * @param type
	 * @param url
	 * @param bitmap
	 */
	public void putBitmap(DataType type, String key, Bitmap bitmap) {
		DiskLruCache diskLruCache = getDiskLruCacheFromDataType(type);
		if (diskLruCache != null) {
			diskLruCache.put(key, bitmap);
		}
	}

	/**
	 * 存储bitmap到对应类型的目录下 自己设置压缩方式何质量。
	 */
	public void putBitmap(DataType type, String key, Bitmap bitmap,
			CompressFormat cf, int quarlity) {
		if (bitmap == null)
			return;
		DiskLruCache diskLruCache = getDiskLruCacheFromDataType(type);
		if (diskLruCache != null) {
			diskLruCache.put(key, bitmap);
		}
	}
	
	/**
	 * 强制存储bitmap到对应类型的目录下，忽略linkedmap中已存在的。
	 */
	public void putBitmapToDisk(DataType type, String key, Bitmap bitmap,CompressFormat cf, int quarlity) {
		if (bitmap == null)
			return;
		DiskLruCache diskLruCache = getDiskLruCacheFromDataType(type);
		if (diskLruCache != null) {
			diskLruCache.putToDisk(key, bitmap, cf, quarlity);
		}
	}

	/**
	 * 返回源文件原始大小的Bitmap，为防止OOM异常，建议调用
	 * {@link #getBitmap(DataType type, String key, int reqWidth, int reqHeight)}
	 * 
	 * @param type
	 * @param key
	 * @return
	 */
	public Bitmap getBitmap(DataType type, String key) {
		Bitmap bitmap = null;
		DiskLruCache diskLruCache = getDiskLruCacheFromDataType(type);
		if (diskLruCache != null) {
			bitmap = diskLruCache.get(context, key);
		}
		return bitmap;
	}

	public Bitmap getBitmap(String key) {
		Bitmap bitmap = getBitmap(DataType.Local, key);
		if (bitmap == null)
			bitmap = getBitmap(DataType.UserData, key);
		if (bitmap == null)
			bitmap = getBitmap(DataType.LiveCache, key);
		return bitmap;

	}

	/**
	 * 获取指定大小的bitmap 对象 ，防止OOM异常
	 * 
	 * @param type
	 * @param key
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public Bitmap getBitmap(DataType type, String key, int reqWidth, int reqHeight) {
		Bitmap bitmap = null;
		DiskLruCache diskLruCache = getDiskLruCacheFromDataType(type);
		if (diskLruCache != null) {
			bitmap = diskLruCache.get(key, reqWidth, reqHeight);
		}
		// TODO:非热门缓存中的取不到bitmap，尝试从热门缓存中取
		if (bitmap == null && type != DataType.LiveCache
				&& getDiskLruCacheFromDataType(DataType.LiveCache) != null) {
			bitmap = getDiskLruCacheFromDataType(DataType.LiveCache).get(key,
					reqWidth, reqHeight);
		}
		return bitmap;
	}

	/**
	 * 返回文件对应的路径
	 * 
	 * @param type
	 *            文件保存类型
	 * @param url
	 */
	public String createFilePath(DataType type, String name) {
		DiskLruCache diskLruCache = getDiskLruCacheFromDataType(type);
		if (diskLruCache != null) {
			return diskLruCache.createFilePath(name);
		}
		return null;
	}

	/**
	 * 从一个文件夹拷贝图片到另外一个文件夹
	 */
	public boolean transateFile(DataType from, DataType to, String name) {
		Bitmap bitmap = getBitmap(from, name);
		if (bitmap != null) {
			putBitmap(to, name, bitmap, CompressFormat.JPEG, 100);
			deleteFile(from, name);
			return true;
		}
		return false;
	}

	/**
	 * 返回对应的文件
	 * 
	 * @param type
	 *            文件保存类型
	 * @param key
	 */
	public File getFile(DataType type, String key) {
		DiskLruCache diskLruCache = getDiskLruCacheFromDataType(type);
		if (diskLruCache != null) {
			return diskLruCache.getFile(key);
		}
		return null;
	}

	/**
	 * 保存文件到对应路径下
	 * 
	 * @param type
	 * @param key
	 * @param file
	 */
	public void putFile(DataType type, String key, File file) {
		DiskLruCache diskLruCache = getDiskLruCacheFromDataType(type);
		if (diskLruCache != null) {
			diskLruCache.putFile(key, file);
		}
	}

	/**
	 * 删除指定DataType下的文件
	 * 
	 * @param type
	 *            文件保存类型
	 * @param key
	 */
	public void deleteFile(DataType type, String key) {
		DiskLruCache diskLruCache = getDiskLruCacheFromDataType(type);
		if (diskLruCache != null) {
			diskLruCache.deleteFile(key);
		}
	}

	/**
	 * 获取对应类型（目录下）的 DiskLruCache
	 * 
	 * @param type
	 * @return
	 */
	private synchronized DiskLruCache getDiskLruCacheFromDataType(DataType type) {
		DiskLruCache cache = null;
		Map<DataType, DiskLruCache> map = null;
		boolean existSDCard = SDCardUtils.exist();
		// 每次取DiskLruCache时候，根据sdcard是否可用取对用map中的disk对象
		if (existSDCard) {
			if (diskCacheMap == null) {
				diskCacheMap = new HashMap<DataType, DiskLruCache>();
			}
			map = diskCacheMap;
		} else {
			if (appCacheMap == null) {
				appCacheMap = new HashMap<DataType, DiskLruCache>();
			}
			map = appCacheMap;
		}

		File path = DiskCacheUtil.getDiskCacheDir(context, type.toString()
				.toLowerCase());
		if (map.containsKey(type) && DiskLruCache.isCacheDirExists(path)) {
			cache = map.get(type);
		} else {
			long size = existSDCard ? DISK_CACHE_DEF_SIZE : APP_CACHE_DEF_SIZE;
			cache = DiskLruCache.openCache(path, size);
			if (cache != null) {
				map.put(type, cache);
			}
		}
		return cache;
	}

	/**
	 * 资源清理策略类
	 * 
	 * @author MrFucking
	 */
	public interface FlushStratage {
		/**
		 * 返回不需要被清理的资源
		 * 
		 * @return
		 */
		TreeSet<String> getUncleanNames(DataType dataType);

		/**
		 * 每个文件夹的最大容量
		 */
		long getMaxSize(DataType dataType);

	}

}

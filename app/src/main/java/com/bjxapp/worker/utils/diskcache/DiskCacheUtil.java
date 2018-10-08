package com.bjxapp.worker.utils.diskcache;

import android.content.Context;
import android.os.StatFs;

import java.io.File;

import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.utils.SDCardUtils;

public class DiskCacheUtil {

	public static final int IO_BUFFER_SIZE = 8 * 1024;

	/** SDCard下的缓存路径，系统不会自动清理 */

	/**
	 * Removes all disk cache entries from the application cache directory in
	 * the uniqueName sub-directory.
	 * 
	 * @param context
	 *            The context to use
	 * @param uniqueName
	 *            A unique cache directory name to append to the app cache
	 *            directory
	 */
	static void clearCache(Context context, String uniqueName) {
		File cacheDir = getDiskCacheDir(context, uniqueName);
		clearCache(cacheDir);
	}

	/** 
	 * 存在sdcard的，创建的文件夹路径为sdcard/.xapp/包名/uniqueName
	 * 不存在sdcard的，创建的文件夹路径为data/data/包名/uniqueName
	 * @param context
	 * @param uniqueName 文件夹名字
	 * @return
	 */
	static File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath = null;
		if (SDCardUtils.exist()) {
			final String path = Constant.SDCARD_CACHE_DIR + context.getPackageName();
			cachePath = SDCardUtils.getPath(path);
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * Removes all disk cache entries from the given directory. This should not
	 * be called directly, call {@link DiskLruCache#clearCache(Context, String)}
	 * or {@link DiskLruCache#clearCache()} instead.
	 * 
	 * @param cacheDir
	 *            The directory to remove the cache files from
	 */
	public static void clearCache(File cacheDir) {
		final File[] files = cacheDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
	}

	/**
	 * Check how much usable space is available at a given path.
	 * 
	 * @param path
	 *            The path to check
	 * @return The space available in bytes
	 */
	public static long getUsableSpace(File path) {
		final StatFs stats = new StatFs(path.getPath());
		return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
	}

}

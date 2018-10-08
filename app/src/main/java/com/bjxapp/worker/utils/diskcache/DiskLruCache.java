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

package com.bjxapp.worker.utils.diskcache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

import com.bjxapp.worker.utils.BitmapUtils;
import com.bjxapp.worker.utils.DeviceUtils;
import com.bjxapp.worker.utils.FileUtils;

/**
 * A simple disk LRU bitmap cache to illustrate how a disk cache would be used
 * for bitmap caching. A much more robust and efficient disk LRU cache solution
 * can be found in the ICS source code
 * (libcore/luni/src/main/java/libcore/io/DiskLruCache.java) and is preferable
 * to this simple implementation.
 */
public class DiskLruCache {
	private static final String TAG = "DiskLruCache";

	public static final CompressFormat DEFAULT_COMPRESS_FORMAT = CompressFormat.JPEG;
	public static final int DEFAULT_QUALITY = 100;
	// private static final int MAX_REMOVALS = 4;
	private static final int INITIAL_CAPACITY = 32;
	private static final float LOAD_FACTOR = 0.75f;

	private final File mCacheDir;
	// private int cacheByteSize = 0;
	// private long maxCacheByteSize = 1024 * 1024 * 5; // 5MB default
	// private CompressFormat mCompressFormat = DEFAULT_COMPRESS_FORMAT;
	// private int mCompressQuality = DEFAULT_QUALITY;

	private final Map<String, String> mLinkedHashMap = Collections
			.synchronizedMap(new LinkedHashMap<String, String>(
					INITIAL_CAPACITY, LOAD_FACTOR, true));

	/**
	 * Used to fetch an instance of DiskLruCache.
	 * 
	 * @param context
	 * @param cacheDir
	 * @param maxByteSize
	 * @return
	 */
	static DiskLruCache openCache(File cacheDir, long maxByteSize) {
		if (!cacheDir.exists()) {
			// boolean result = cacheDir.mkdir();
			try {
				FileUtils.makeDirs(cacheDir.getPath());
			} catch (IOException e) {
				Log.d("DiskLruCache", "mk dir failed");
				e.printStackTrace();
			}
			// Log.d("DiskLruCache", "mkdir  " + result + cacheDir.getPath());
		}
		if (cacheDir.isDirectory() && cacheDir.canWrite()) {
			long usableSpace = DiskCacheUtil.getUsableSpace(cacheDir);
			maxByteSize = usableSpace > maxByteSize ? maxByteSize
					: usableSpace / 2;
			return new DiskLruCache(cacheDir, maxByteSize);
		}
		return null;
	}

	static boolean isCacheDirExists(File cacheDir) {
		return cacheDir.exists();
	}

	/**
	 * Constructor that should not be called directly, instead use
	 * {@link DiskLruCache#ache(Context, File, long)} which runs some extra
	 * checks before creating a DiskLruCache instance.
	 * 
	 * @param cacheDir
	 * @param maxByteSize
	 */
	private DiskLruCache(File cacheDir, long maxByteSize) {
		mCacheDir = cacheDir;
		// maxCacheByteSize = maxByteSize;
	}

	/**
	 * 保存图片到磁盘上
	 * 
	 */
	public void put(String key, Bitmap data) {
		CompressFormat compressFormat = getFormatFromFileExt(key);
		int compressQuality = DEFAULT_QUALITY;
		put(key, data, compressFormat, compressQuality);
	}

	/**
	 * 保存图片到磁盘上
	 * 
	 */
	public void put(String key, Bitmap data, CompressFormat compressFormat,
			int compressQuality) {
		synchronized (mLinkedHashMap) {
			if (mLinkedHashMap.get(key) == null) {
				try {
					final String localFilePath = createFilePath(mCacheDir, key);
					if (writeBitmapToFile(data, localFilePath, compressFormat,
							compressQuality)) {
						put(key, localFilePath);
					}
				} catch (final FileNotFoundException e) {
					Log.e(TAG, "Error in put: " + e.getMessage());
				} catch (final IOException e) {
					Log.e(TAG, "Error in put: " + e.getMessage());
				}
			}
		}
	}
	
	/**
	 * 强制写入磁盘，忽略mLinkedHashMap已存在
	 * @param key
	 * @param data
	 * @param compressFormat
	 * @param compressQuality
	 */
	public void putToDisk(String key, Bitmap data, CompressFormat compressFormat,
			int compressQuality) {
		try {
			final String localFilePath = createFilePath(mCacheDir, key);
			if (writeBitmapToFile(data, localFilePath, compressFormat,
					compressQuality)) {
				put(key, localFilePath);
			}
		} catch (final FileNotFoundException e) {
			Log.e(TAG, "Error in put: " + e.getMessage());
		} catch (final IOException e) {
			Log.e(TAG, "Error in put: " + e.getMessage());
		}
	}

	private void put(String key, String file) {
		mLinkedHashMap.put(key, file);
		// cacheByteSize += new File(file).length();
	}

	// /**
	// * Flush the cache, removing oldest entries if the total size is over the
	// * specified cache size. Note that this isn't keeping track of stale files
	// * in the cache directory that aren't in the HashMap. If the images and
	// keys
	// * in the disk cache change often then they probably won't ever be
	// removed.
	// */
	// private void flushCache() {
	// Entry<String, String> eldestEntry;
	// File eldestFile;
	// long eldestFileSize;
	// int count = 0;
	//
	// while (count < MAX_REMOVALS && cacheByteSize > maxCacheByteSize) {
	// eldestEntry = mLinkedHashMap.entrySet().iterator().next();
	// eldestFile = new File(eldestEntry.getValue());
	// eldestFileSize = eldestFile.length();
	// mLinkedHashMap.remove(eldestEntry.getKey());
	// eldestFile.delete();
	// cacheByteSize -= eldestFileSize;
	// count++;
	//
	// }
	// }

	/**
	 * 返回尺寸不超过屏幕大小的bitmap 若原始尺寸小于屏幕大小，则返回原始尺寸 若原始尺寸大于屏幕大小，则返回以比例压缩后返回
	 * 
	 * @param key
	 * @return
	 */
	public Bitmap get(Context context, String key) {
		int[] size = DeviceUtils.getScreenSize(context);
		int screenWidth = size[0];
		int screenHeight = size[1];
		return get(key, screenWidth, screenHeight);
	}

	/**
	 * 返回指定大小的bitmap
	 * 
	 * @param key
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public Bitmap get(String key, int reqWidth, int reqHeight) {
		synchronized (mLinkedHashMap) {
			final String file = mLinkedHashMap.get(key);
			if (file != null) {
				return BitmapUtils.decodeFile(file, reqWidth, reqHeight);
				// return BitmapFactory.decodeFile(file);
			} else {
				final String existingFile = createFilePath(mCacheDir, key);
				if (new File(existingFile).exists()) {
					put(key, existingFile);
					return BitmapUtils.decodeFile(existingFile, reqWidth,
							reqHeight);
					// return BitmapFactory.decodeFile(existingFile);
				}
			}
			return null;
		}
	}

	/**
	 * Checks if a specific key exist in the cache.
	 * 
	 * @param key
	 *            The unique identifier for the bitmap
	 * @return true if found, false otherwise
	 */
	public boolean containsKey(String key) {
		// See if the key is in our HashMap
		if (mLinkedHashMap.containsKey(key)) {
			return true;
		}

		// Now check if there's an actual file that exists based on the key
		final String existingFile = createFilePath(mCacheDir, key);
		if (new File(existingFile).exists()) {
			// File found, add it to the HashMap for future use
			put(key, existingFile);
			return true;
		}
		return false;
	}

	public boolean containsFile(String key) {
		// Now check if there's an actual file that exists based on the key
		final String existingFile = createFilePath(mCacheDir, key);
		if (new File(existingFile).exists()) {
			// File found, add it to the HashMap for future use
			return true;
		}
		return false;
	}

	public File getFile(String key) {
		// Now check if there's an actual file that exists based on the key
		final String existingFile = createFilePath(mCacheDir, key);
		File file = new File(existingFile);
		if (file.exists()) {
			// File found, add it to the HashMap for future use
			return file;
		}
		return null;
	}

	/**
	 * Removes all disk cache entries from this instance cache dir
	 */
	public void clearCache() {
		DiskCacheUtil.clearCache(mCacheDir);
	}

	public static String createFilePath(File cacheDir, String key) {
		StringBuilder sb = new StringBuilder();
		sb.append(cacheDir.getAbsolutePath()).append(File.separator);
		sb.append(FileUtils.getImgNameWithXMExt(key));
		return sb.toString();
	}

	/**
	 * Create a constant cache file path using the current cache directory and
	 * an image key.
	 * 
	 * @param key
	 * @return
	 */
	public String createFilePath(String key) {
		return createFilePath(mCacheDir, key);
	}

	// /**
	// * Sets the target compression format and quality for images written to
	// the
	// * disk cache.
	// *
	// * @param compressFormat
	// * @param quality
	// */
	// public void setCompressParams(CompressFormat compressFormat, int quality)
	// {
	// mCompressFormat = compressFormat;
	// mCompressQuality = quality;
	// }

	/**
	 * Writes a bitmap to a file. Call
	 * {@link DiskLruCache#setCompressParams(CompressFormat, int)} first to set
	 * the target bitmap compression and format.
	 * 
	 * @param bitmap
	 * @param file
	 * @return
	 */
	private boolean writeBitmapToFile(Bitmap bitmap, String file,
			CompressFormat compressFormat, int compressQuality)
			throws IOException, FileNotFoundException {

		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file),
					DiskCacheUtil.IO_BUFFER_SIZE);
			return bitmap.compress(compressFormat, compressQuality, out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	private CompressFormat getFormatFromFileExt(String filePath) {
		CompressFormat cm = CompressFormat.JPEG;
		String fileExt = FileUtils.getFileExt(filePath);
		Log.d("DiskLruCache", filePath + "	" + fileExt);
		if (fileExt != null && fileExt.equals("png")) {
			cm = CompressFormat.PNG;
		}
		return cm;
	}

	/**
	 * 加入指定文件到该管理类中
	 * 
	 * @param file
	 */
	public void putFile(String key, File file) {
		String newPath = createFilePath(key);
		if (!new File(newPath).exists()
				&& FileUtils.copyFile(file.getPath(), newPath)) {
			put(key, newPath);
		}
	}

	/**
	 * 删掉文件
	 * 
	 * @param key
	 *            对应的url
	 */
	public void deleteFile(String key) {
		synchronized (mLinkedHashMap) {
			final String fileString = createFilePath(mCacheDir, key);
			File file = new File(fileString);
			if (!file.exists()) {
				return;
			}
			if (file.delete() && mLinkedHashMap.get(key) != null) {
				mLinkedHashMap.remove(key);
				// cacheByteSize -= file.length();
			}
		}
	}

	/**
	 * 获取DiskCache的实际大小
	 * 
	 * @return
	 */
	public long getDiskCacheSize() {
		return FileUtils.getFileSize(mCacheDir);
	}

	/**
	 * 删除不要的资源
	 * 
	 * @param uncleanFileNames
	 *            不需要删除的资源
	 */
	public void flushCache(TreeSet<String> uncleanFileNames) {
		deleteTraversal(mCacheDir, uncleanFileNames);
	}

	private void deleteTraversal(File file, TreeSet<String> uncleanFileNames) {
		if (file == null || !file.exists())
			return;

		if (file.isFile()) {
			delete(file, uncleanFileNames);
		} else {
			File[] files = file.listFiles();
			for (File f : files) {
				delete(f, uncleanFileNames);
			}
		}
	}

	/**
	 * 删除文件，如果文件不包含在uncleanFileNames
	 * 
	 * @param file
	 * @param uncleanFileNames
	 */
	private void delete(File file, final TreeSet<String> uncleanFileNames) {
		String fileName = FileUtils.getFileName(file.getName());
		for (String s : uncleanFileNames) {
			if (fileName.equals(s))
				return;
		}
		deleteFile(fileName);
	}
}

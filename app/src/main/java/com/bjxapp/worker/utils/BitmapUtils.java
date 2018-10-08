package com.bjxapp.worker.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.bjxapp.worker.global.OurContext;
import com.bjxapp.worker.utils.SDCardUtils.SDCardNotFoundExcetpion;

import junit.framework.Assert;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class BitmapUtils {
	public static final float LARGE_PIC_SIZE_RATE = (float) 205 / (float) 300;
	public static final float GET_UP_LARGE_PIC_SIZE_RATE = (float) 150 / (float) 300;

	/**
	 * 从url加载bitmap对象
	 * 
	 * @param urlString
	 * @return
	 */
	public static Bitmap load(String urlString) {
		BufferedInputStream bis = null;
		try {
			URL url = new URL(urlString);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(30000);
			InputStream is = con.getInputStream();
			bis = new BufferedInputStream(is);
			return BitmapFactory.decodeStream(bis);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	public static byte[] readStream(InputStream inStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;
	}

	/**
	 * 返回指定大小的bitmap
	 * 
	 * @return 如果图片没有变化，则返回的bitmap同传入的bitmap指向同一块内存 否则新建一个bitmap
	 */
	public static Bitmap getBitmapBySpecificSize(Bitmap bitmap, int width,
			int height) {
		Bitmap tempBitmap = Bitmap.createScaledBitmap(bitmap, width, height,
				true);
		return tempBitmap;
	}

	public static Bitmap getBitmapFromUri(Context context, Uri data) {
		ContentResolver resolver = context.getContentResolver();
		byte[] bytes = null;
		try {
			bytes = readStream(resolver.openInputStream((Uri.parse(data
					.toString()))));
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		if (bytes != null) {
			return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		}
		return null;
	}

	public static Bitmap getBitmapScale(Context context, byte[] bitmapBytes,
			float scale) {
		Bitmap src = BitmapFactory.decodeByteArray(bitmapBytes, 0,
				bitmapBytes.length);
		int width = src.getWidth();
		int height = src.getHeight();
		if (scale < 1) {
			width = (int) (width * scale);
			height = (int) (height * scale);
		}
		Bitmap result = Bitmap.createScaledBitmap(src, width, height, false);
		src.recycle();
		src = null;
		return result;
	}

	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 70, baos);
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	public static Bitmap getBitmapFromPath(String pathName, int width,
			int height) {
		Bitmap bitmap = getBitmapFromPath(pathName);
		bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
		return bitmap;
	}

	public static int computeSampleSize(Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static Bitmap getBitmapFromPath(String pathName, Activity activity) {
		try {
			int screenWidth = OurContext.getScreenWidth(activity);
			int screenHeight = OurContext.getScreenHeight(activity);
			Options options = new Options();
			options.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
			if (options.outWidth > screenWidth
					&& options.outHeight > screenHeight) {
				options.inSampleSize = computeSampleSize(options, -1,
						screenWidth * screenHeight);
			}
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(pathName, options);
			return bitmap;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	public static Bitmap getBitmapFromPath(String pathName, Context context) {
		int screenWidth = OurContext.getScreenWidth(context);
		int screenHeight = OurContext.getScreenHeight(context);
		Options options = new Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
		if (options.outWidth > screenWidth && options.outHeight > screenHeight) {
			options.inSampleSize = computeSampleSize(options, -1, screenWidth
					* screenHeight);
		}
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(pathName, options);
		
		return bitmap;
	}

	public static Bitmap getBitmapFromPath(String path) {
		try {
			File file = new File(path);
			if (!file.exists() || !file.isFile()) {
				return null;
			}
			Options bfOptions = new Options();
			bfOptions.inPreferredConfig = Config.RGB_565;
			bfOptions.inInputShareable = true;
			bfOptions.inDither = false;
			bfOptions.inPurgeable = true;
			bfOptions.inTempStorage = new byte[32 * 1024];
			FileInputStream fs = null;
			try {
				fs = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			Bitmap bmp = null;
			if (fs != null)
				try {
					bmp = BitmapFactory.decodeFileDescriptor(fs.getFD(), null,
							bfOptions);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (fs != null) {
						try {
							fs.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			return bmp;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	/**
	 * @param bytes
	 * @param size
	 *            如果不想返回指定大小的bitmap,size 设为 <= 0 即可
	 * @return
	 */
	public static Bitmap getBitmapFromByte(byte[] bytes, int size) {
		if (bytes != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
					bytes.length);
			// BitmapFactory.Options options = new BitmapFactory.Options();
			// options.inJustDecodeBounds = true;
			// Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
			// bytes.length ,options);// 此时返回bm为空
			// options.inSampleSize = computeSampleSize(options, -1, size *
			// size);;
			// options.inJustDecodeBounds = false;
			// // 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
			// bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length
			// ,options);
			if (size <= 0) {
				return bitmap;
			} else {
				Bitmap result = getBitmapBySpecificSize(bitmap, size, size);
				if (result != bitmap) {
					bitmap.recycle();
					bitmap = null;
				}
				return result;
			}
		}
		return null;
	}

	/**
	 * @param file
	 * @param size
	 *            指定返回的bitmap大小,如果不想返回指定大小的bitmap,size 设为 <= 0 即可
	 * @return
	 */
	public static Bitmap getBitmapFromFile(File file, int size) {
		try {
			byte[] b = FileUtils.getBytesFromFile(file);
			return getBitmapFromByte(b, size);
		} catch (IOException e) {
			return null;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}

	}

	public static boolean save(Bitmap bitmap, String path) {
		if (bitmap == null || path == null)
			return false;
		OutputStream out = null;
		try {
			out = new FileOutputStream(path);
			CompressFormat format = null;
			String ext = FileUtils.getFileExt(path);
			if (ext != null) {
				if (ext.equals("jpg")) {
					format = CompressFormat.JPEG;
				}
			}
			if (format == null) {
				format = CompressFormat.PNG;
			}
			return bitmap.compress(format, 100, out);
		} catch (FileNotFoundException e) {
		} catch (Throwable t) {

		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
		}
		return false;
	}

	private static Options getBlurBitmapOption() {
		Options opt = new Options();
		opt.inSampleSize = 12;
		return opt;
	}

	public static Drawable getBlurDrawable(Context context, String path) {
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(path,
					getBlurBitmapOption());
			return getBlurDrawable(context, bitmap);
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	public static Drawable getBlurDrawable(Context context, int resId) {
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				resId, getBlurBitmapOption());
		return getBlurDrawable(context, bitmap);
	}

	public static Drawable getBlurDrawable(Context context, Bitmap bitmap) {
		int sw = OurContext.getScreenWidth(context);
		int sh = OurContext.getScreenHeight(context);

		int iw = bitmap.getWidth();
		int ih = bitmap.getHeight();

		int offw = Math.abs(sw - iw);
		int offh = Math.abs(sh - ih);
		float scale;
		int nw, nh;
		if (offh > offw) {
			nh = ih;
			scale = (iw * 1.0f / sw);
			nw = (int) (iw * scale);
		} else {
			nw = sw;
			scale = (ih * 1.0f / sh);
			nh = (int) (ih * scale);
		}
		if (nw == 0 || nh == 0) {
			nw = iw;
			nh = ih;
		}

		int left = Math.abs(iw - nw) / 2;
		int top = Math.abs(ih - nh) / 2;

		Matrix matrix = new Matrix();
		// matrix.postScale(1 / (scale * 5), 1 / (scale * 5));
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, left, top,
				Math.min(bitmap.getWidth() - left, nw),
				Math.min(bitmap.getHeight() - top, nh), matrix, false);
		return new BitmapDrawable(newBitmap);
	}

	public static Bitmap compressBitmap(Context context, String path,
			long maxSize) {
		// TODO:
		try {
			File file = new File(path);
			long size = file.length();
			if (size == 0)
				return null;
			int index = 1;
			while (size > maxSize) {
				size = size / 2;
				index = index * 2;
			}
			Options op = new Options();
			op.inSampleSize = index;
			return BitmapFactory.decodeFile(path, op);
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
	
	/*
	 * 循环压缩到指定大小
	 */
	public static Bitmap compressImageSize(Bitmap image) {
		int kCount = 200;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 80, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > kCount) {
            options -= 10;
            baos.reset();
            image.compress(CompressFormat.JPEG, options, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
	}
	
	public static byte[] compressImageWithoutScale(String filePath, long maxSize) {
		Bitmap image = getBitmapFromPath(filePath);
		
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 80, baos);
        int options = 100;
        while (baos.toByteArray().length > maxSize) {
            options -= 10;
            baos.reset();
            image.compress(CompressFormat.JPEG, options, baos);
        }
        
        image.recycle();
        return baos.toByteArray();
	}

	/**
	 * 根据 请求宽度和高度，返回压缩参数inSampleSize
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private static int calculateSpecialInSampleSize(
			Options options, int reqWidth, int reqHeight) {
		// 资源的原始宽度、高度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}

			final float totalPixels = width * height;

			// Anything more than 2x the requested pixels we'll sample down
			// further.
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}

	// /**
	// * 返回默认压缩参数inSampleSize（不超过屏幕的宽、高）
	// *
	// * @param context
	// * @param options
	// * @return
	// */
	// private static int calculateDefaultInSampleSize(Context context,
	// BitmapFactory.Options options) {
	// int[] size = DeviceUtils.getScreenSize(context);
	// int screenWidth = size[0];
	// int screenHeight = size[1];
	// return calculateSpecialInSampleSize(options, screenWidth, screenHeight);
	// }

	/**
	 * 返回从资源获取，经过压缩处理（如资源尺寸比请求尺寸大）的bitmap
	 *
	 * @param res
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeResource(Resources res, int resId, int reqWidth,
										int reqHeight, boolean isMutable) {
		try {
			// First decode with inJustDecodeBounds=true to check dimensions
			final Options options = new Options();
			// 标记inJustDecodeBounds为true，省略此步则操作无效
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(res, resId, options);

			// 设置 inSampleSize
			options.inSampleSize = calculateSpecialInSampleSize(options,
					reqWidth, reqHeight);

			// 标记使用inSampleSize进行解码
			options.inJustDecodeBounds = false;
			options.inMutable = isMutable;
			return BitmapFactory.decodeResource(res, resId, options);
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	/**
	 * 返回从资源获取，采用默认压缩处理（图片大小不超过屏幕）的bitmap
	 *
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap decodeResourceForDefaultSize(Context context, int resId, boolean isMutable) {
		int[] size = DeviceUtils.getScreenSize(context);
		int screenWidth = size[0];
		int screenHeight = size[1];
		return decodeResource(context.getResources(), resId, screenWidth,
				screenHeight, isMutable);
	}

	/**
	 * 返回从资源获取，经过压缩处理（如资源尺寸比请求尺寸大）的bitmap
	 * 
	 * @param res
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeResource(Resources res, int resId, int reqWidth,
			int reqHeight) {
		try {
			// First decode with inJustDecodeBounds=true to check dimensions
			final Options options = new Options();
			// 标记inJustDecodeBounds为true，省略此步则操作无效
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(res, resId, options);

			// 设置 inSampleSize
			options.inSampleSize = calculateSpecialInSampleSize(options,
					reqWidth, reqHeight);

			// 标记使用inSampleSize进行解码
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeResource(res, resId, options);
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	/**
	 * 返回从资源获取，经过压缩处理（如资源尺寸比请求尺寸大）的bitmap
	 * 
	 * @param res
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeResource(Bitmap bitmap, int reqWidth,
			int reqHeight) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final Options options = new Options();
			options.inJustDecodeBounds = true;
			options.inSampleSize = calculateSpecialInSampleSize(options,
					reqWidth, reqHeight);
			options.inJustDecodeBounds = false;
			bitmap.compress(CompressFormat.PNG, options.inSampleSize,
					baos);
			ByteArrayInputStream isBm = new ByteArrayInputStream(
					baos.toByteArray());
			return BitmapFactory.decodeStream(isBm, null, null);
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	/**
	 * 返回从资源获取，采用默认压缩处理（图片大小不超过屏幕）的bitmap
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap decodeResourceForDefaultSize(Context context, int resId) {
		int[] size = DeviceUtils.getScreenSize(context);
		int screenWidth = size[0];
		int screenHeight = size[1];
		return decodeResource(context.getResources(), resId, screenWidth,
				screenHeight);
	}

	/**
	 * 返回从文件资源获取，经过压缩处理（如资源尺寸比请求尺寸大）的bitmap
	 * 
	 * @param filename
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeFile(String filename, int reqWidth, int reqHeight) {
		try {
			// First decode with inJustDecodeBounds=true to check dimensions
			final Options options = new Options();
			// 标记inJustDecodeBounds为true，省略此步则操作无效
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filename, options);

			// 设置 inSampleSize
			options.inSampleSize = calculateSpecialInSampleSize(options,
					reqWidth, reqHeight);

			// 标记使用inSampleSize进行解码
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeFile(filename, options);
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	/**
	 * 返回从文件获取，采用默认压缩处理（图片大小不超过屏幕）的bitmap
	 * 
	 * @param context
	 * @param filename
	 * @return
	 */
	public static Bitmap decodeFileForDefaultSize(Context context,
			String filename) {
		try {
			int[] size = DeviceUtils.getScreenSize(context);
			int screenWidth = size[0];
			int screenHeight = size[1];
			return decodeFile(filename, screenWidth, screenHeight);
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	/**
	 * 从Assets中的文件获取bitmap
	 * 
	 * @param context
	 *            建议传入appContext
	 * @param resName
	 *            assets 目录下的文件名，若assets下再分文件夹，则文件名前加上"文件夹名/"
	 * @return
	 */
	public static Bitmap decodeAssets(Context context, String resName,
			int reqWidth, int reqHeight) {
		InputStream is = null;
		try {
			is = context.getResources().getAssets().open(resName);
			Options bfOptions = new Options();
			bfOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is, null, bfOptions);
			// 设置 inSampleSize
			bfOptions.inSampleSize = calculateSpecialInSampleSize(bfOptions,
					reqWidth, reqHeight);
			// 标记使用inSampleSize进行解码
			bfOptions.inJustDecodeBounds = false;
			bfOptions.inPreferredConfig = Config.RGB_565;
			bfOptions.inInputShareable = true;
			bfOptions.inDither = false;
			bfOptions.inPurgeable = true;
			bfOptions.inTempStorage = new byte[32 * 1024];
			Bitmap btp = BitmapFactory.decodeStream(is, null, bfOptions);
			return btp;
		} catch (Throwable e) {
			// 抛出异常，则返回null
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static boolean saveIconImg(Bitmap img, String path, String dir) {
		try {
			SDCardUtils.makeSureDirExist(dir);
			return save(img, path);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SDCardNotFoundExcetpion e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 返回从文件获取，采用默认压缩处理（图片大小不超过屏幕）的bitmap
	 * 
	 * @param context
	 * @param filename
	 * @return
	 */
	public static Bitmap decodeAssetsForDefaultSize(Context context,
			String filename) {
		int[] size = DeviceUtils.getScreenSize(context);
		int screenWidth = size[0];
		int screenHeight = size[1];
		return decodeAssets(context, filename, screenWidth, screenHeight);
	}

	/**
	 * resid对应的bitmap对象，该对象由系统管理，负责回收，不能手动recycle
	 * 
	 * @param context
	 * @param resid
	 * @return
	 */
	public static Bitmap getDrawableBitmap(Context context, int resid) {
		Context appContext = context.getApplicationContext();
		BitmapDrawable bitmapDrawable = (BitmapDrawable) appContext
				.getResources().getDrawable(resid);
		return bitmapDrawable.getBitmap();
	}

	/**
	 * 标志bitmap为可回收
	 * 
	 * @param bitmap
	 */
	public static void recycleBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
	}

	/**
	 * 按照width/height的比例截取图片， 如果原图就是这个比例，直接返回
	 * 返回的bitmap对象可能和原对象是同一个，可能不是，在调用之后进行前后对比，看是否需要回收原对象
	 */
	public static Bitmap getPartBitmap(Context context, Bitmap bitmap,
			int width, int height) {
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		int widthStart = 0, heightStart = 0, resultWidth = bitmapWidth, resultHeight = bitmapHeight;
		float bitmapRate = (float) bitmapWidth / (float) bitmapHeight;
		float rate = (float) width / (float) height;
		if (bitmapRate > rate) {
			// cut width
			if (bitmapWidth > rate * (float) bitmapHeight + 100) {
				widthStart = 100;
			}
			resultWidth = (int) (rate * (float) bitmapHeight);
		} else if (bitmapRate < rate) {
			// cut height
			if (bitmapHeight > (float) bitmapWidth / rate + 100) {
				heightStart = 100;
			}
			resultHeight = (int) ((float) bitmapWidth / rate);
		} else {
			return bitmap;
		}
		Bitmap source = null;
		try {
			source = Bitmap.createBitmap(bitmap, widthStart, heightStart,
					resultWidth, resultHeight);
		} catch (Error e) {
			return bitmap;
		}
		return source;
	}

	/**
	 * 按照width 和height截取，如果小于width，保持原来的宽度，高度同样
	 */
	public static Bitmap getBitmapBySize(Context context, Bitmap bitmap,
			int width, int height) {
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		if (bitmapWidth < width) {
			width = bitmapWidth;
		}
		if (bitmapHeight < height) {
			height = bitmapHeight;
		}
		Bitmap source = null;
		try {
			source = Bitmap.createBitmap(bitmap, 0, 0, width, height);
		} catch (Error e) {
			return bitmap;
		}
		if (source == null) {
			return bitmap;
		}
		return source;
	}

	// TODO copy

	/**
	 * 详情大图的尺寸按{@link Constant#LARGE_PIC_SIZE_RATE}比例固定
	 * 
	 * @param context
	 * @param bitmap
	 * @param needRecycleSource
	 *            是否需要回收原来的bitmap,如果是成员对象，就不需要，局部的就需要
	 * @return 会新建一个bitmap返回
	 */
	public static Bitmap getSuitableSizeBitmapForDetail(Context context,
			Bitmap bitmap, boolean needRecycleSource) {
		if (bitmap == null)
			return null;
		try {
			Bitmap tempBitmap = bitmap;
			bitmap = getPartBitmap(context, tempBitmap, tempBitmap.getWidth(),
					(int) (tempBitmap.getWidth() * LARGE_PIC_SIZE_RATE));
			if (tempBitmap == bitmap) {
				bitmap = bitmap.copy(Config.ARGB_8888, false);
			}
			if (needRecycleSource) {
				tempBitmap.recycle();
				tempBitmap = null;
			}
			return bitmap;
		} catch (Exception e) {
			return bitmap;
		} catch (Error e) {
			return bitmap;
		}
	}

	//
	// /**
	// * 获取imageview内的bitmap对象
	// *
	// * @param imageView
	// * @return
	// */
	// public static Bitmap getImageViewBitmap(ImageView imageView) {
	// Drawable drawable = imageView.getDrawable();
	// if (drawable != null && drawable instanceof BitmapDrawable) {
	// return ((BitmapDrawable) drawable).getBitmap();
	// }
	// return null;
	// }

	public static Bitmap combineBitmap(Bitmap topBitmap, Bitmap bottomBitmap)
			throws Exception {
		int topWidth = topBitmap.getWidth();
		int topHeight = topBitmap.getHeight();
		int bottomHeight = bottomBitmap.getHeight();
		Bitmap newbmp = Bitmap.createBitmap(topWidth, topHeight + bottomHeight,
				topBitmap.getConfig());

		Canvas cv = new Canvas(newbmp);
		cv.drawBitmap(topBitmap, 0, 0, null);
		cv.drawBitmap(bottomBitmap, 0, topHeight, null);
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		cv.restore();// 存储
		/*
		 * if(topBitmap != null && !topBitmap.isRecycled()) {
		 * topBitmap.recycle(); topBitmap = null; } if(bottomBitmap != null &&
		 * !bottomBitmap.isRecycled()) { bottomBitmap.recycle(); bottomBitmap =
		 * null; }
		 */
		return newbmp;
	}

	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;

	public static Bitmap extractThumbNail(final String path, final int height,
			final int width, final boolean crop) {
		Assert.assertTrue(path != null && !path.equals("") && height > 0
				&& width > 0);

		Options options = new Options();

		try {
			options.inJustDecodeBounds = true;
			Bitmap tmp = BitmapFactory.decodeFile(path, options);
			if (tmp != null) {
				tmp.recycle();
				tmp = null;
			}

			final double beY = options.outHeight * 1.0 / height;
			final double beX = options.outWidth * 1.0 / width;
			options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY)
					: (beY < beX ? beX : beY));
			if (options.inSampleSize <= 1) {
				options.inSampleSize = 1;
			}

			// NOTE: out of memory error
			while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
				options.inSampleSize++;
			}

			int newHeight = height;
			int newWidth = width;
			if (crop) {
				if (beY > beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			} else {
				if (beY < beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			}

			options.inJustDecodeBounds = false;

			Bitmap bm = BitmapFactory.decodeFile(path, options);
			if (bm == null) {
				return null;
			}

			final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth,
					newHeight, true);
			if (scale != null) {
				bm.recycle();
				bm = scale;
			}

			if (crop) {
				final Bitmap cropped = Bitmap.createBitmap(bm,
						(bm.getWidth() - width) >> 1,
						(bm.getHeight() - height) >> 1, width, height);
				if (cropped == null) {
					return bm;
				}

				bm.recycle();
				bm = cropped;
			}
			return bm;

		} catch (final OutOfMemoryError e) {
			options = null;
		}

		return null;
	}

	public static Bitmap fastBlur(Bitmap sentBitmap, int radius) {
		// Stack Blur v1.0 from
		// http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
		//
		// Java Author: Mario Klingemann <mario at quasimondo.com>
		// http://incubator.quasimondo.com
		// created Feburary 29, 2004
		// Android port : Yahel Bouaziz <yahel at kayenko.com>
		// http://www.kayenko.com
		// ported april 5th, 2012

		// This is a compromise between Gaussian Blur and Box blur
		// It creates much better looking blurs than Box Blur, but is
		// 7x faster than my Gaussian Blur implementation.
		//
		// I called it Stack Blur because this describes best how this
		// filter works internally: it creates a kind of moving stack
		// of colors whilst scanning through the image. Thereby it
		// just has to add one new block of color to the right side
		// of the stack and remove the leftmost color. The remaining
		// colors on the topmost layer of the stack are either added on
		// or reduced by one, depending on if they are on the right or
		// on the left side of the stack.
		//
		// If you are using this algorithm in your code please add
		// the following line:
		//
		// Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

		Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

		if (radius < 1) {
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
						| (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}
		bitmap.setPixels(pix, 0, w, 0, 0, w, h);
		return (bitmap);
	}

	public static Bitmap getSmallBitmap(String filePath) {
		try {
			final Options options = new Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, 480, 800);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;

			return BitmapFactory.decodeFile(filePath, options);
		} catch (Throwable e) {
			return null;
		}
	}

	// 计算图片的缩放值
	public static int calculateInSampleSize(Options options,
			int reqWidth, int reqHeight) {
		boolean isOptionWidthLarger = options.outWidth > options.outHeight;
		boolean isReqWidthLarger = reqWidth > reqHeight;
		final int height = isOptionWidthLarger ? options.outHeight
				: options.outWidth;
		final int width = isOptionWidthLarger ? options.outWidth
				: options.outHeight;
		int reqW = isReqWidthLarger ? reqWidth : reqHeight;
		int reqH = isReqWidthLarger ? reqHeight : reqWidth;
		int inSampleSize = 1;

		if (height > reqH || width > reqW) {
			final int heightRatio = Math.round((float) height / (float) reqH);
			final int widthRatio = Math.round((float) width / (float) reqW);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * 检测是否可以解析成位图
	 * 
	 * @param input
	 * @return
	 */
	public static boolean verifyBitmap(InputStream input) {
		if (input == null) {
			return false;
		}
		final Options options = new Options();
		options.inJustDecodeBounds = true;
		input = input instanceof BufferedInputStream ? input
				: new BufferedInputStream(input);
		BitmapFactory.decodeStream(input, null, options);
		try {
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (options.outHeight > 0) && (options.outWidth > 0);
	}

	/**
	 * 检测是否可以解析成位图
	 * 
	 * @param path
	 * @return
	 */
	public static boolean verifyBitmap(String path) {
		try {
			return verifyBitmap(new FileInputStream(path));
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 得到白色背景图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getWhiteBgBmp(Bitmap bitmap) {
		if (bitmap == null)
			return bitmap;
		Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), bitmap.getConfig());
		Canvas canvas = new Canvas(newBitmap);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(bitmap,
				new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
				new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), null);
		canvas.save();
		if (bitmap != null) {
			bitmap.recycle();
		}
		return newBitmap;
	}

	/**
	 * 获取和保存当前屏幕的截图
	 */
	public static Bitmap getCurrentImage(Activity activity, View view,int height) {
		int size = 300*1024;
		View v = view.getRootView();
		v.setDrawingCacheEnabled(true);
		v.buildDrawingCache();
		Bitmap bitmap = v.getDrawingCache();
		Bitmap b = getNoTitleBitmap(bitmap, height);
		if (b != null) {
			if(b.getByteCount()<size){
				return bitmap;
			}
			System.out.println("bitmap got!");
			String fname = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ File.separator
					+ System.currentTimeMillis() + ".jpg";
			try {
				
				save(b, fname);
				return getBitmapFromPath(fname,size);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("bitmap is NULL!");
		}
		return null;

	}
	
	public static Bitmap getNoTitleBitmap(Bitmap bitmap,int height){
		if(null==bitmap){
			return null;
		}
		if(bitmap.getHeight()-height<0){
			return null;
		}
		Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, height, bitmap.getWidth(), bitmap.getHeight()-height);
		return createBitmap;
	}
	
	
	public static Bitmap getBitmapFromPath(String path,int size) {
		try {
			File file = new File(path);
			if (!file.exists() || !file.isFile()) {
				return null;
			}
			Options bfOptions = new Options();
			bfOptions.inPreferredConfig = Config.RGB_565;
			bfOptions.inInputShareable = true;
			bfOptions.inDither = false;
			bfOptions.inPurgeable = true;
			bfOptions.inTempStorage = new byte[size];
			Bitmap bitmap = BitmapFactory.decodeFile(path, bfOptions);
			bfOptions.inSampleSize = calculateSpecialInSampleSize(bfOptions,
					bitmap.getWidth()/2, bitmap.getHeight()/2);
			
			recycleBitmap(bitmap);
			FileInputStream fs = null;
			try {
				fs = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			Bitmap bmp = null;
			if (fs != null)
				try {
					bmp = BitmapFactory.decodeFileDescriptor(fs.getFD(), null,
							bfOptions);
					if(file.exists()){
						file.delete();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (fs != null) {
						try {
							fs.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			return bmp;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}


}

package com.bjxapp.worker.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UploadFile {

	private static final int FEEDBACK_IMAGE_SINGLE_MAX_SIZE = 1 * 1024 *1024 ;
	private static final int FEEDBACK_IMAGE_WIDTH_SIZE = 800;


	public final static String KEY = "g!irbRCb2zc%e&NK";							// 上报dump的KEY值
	
	private static void uploadWriteByte(DataOutputStream ds, String formName, String fileName, byte uploadBytes[]) throws IOException {
		String enterNewline = "\r\n";
		String fix = "--";
		String boundary = "---------7d4a6d158c9";
		ds.writeBytes(fix + boundary + enterNewline);
		ds.writeBytes("Content-Disposition: form-data; " + "name=\"" + formName + "\"" + "; filename=\"" + fileName + "\"" + enterNewline);
		ds.writeBytes("Content-Type: application/octet-stream");
		ds.writeBytes(enterNewline);
		ds.writeBytes(enterNewline);
		ds.write(uploadBytes);
		ds.writeBytes(enterNewline);
	}
		
	private static byte[] Bitmap2Bytes(Bitmap bitmap, int qualit) {
		if (bitmap != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, qualit, baos);
			return baos.toByteArray();
		} else {
			return null;
		}
	}

	private static int calculateInSampleSize(float outWidth, float outHeight,
											 float reqWidth, float reqHeight) {
		int inSampleSize = 1;
		if (reqHeight == 0 || reqWidth == 0) {
			return inSampleSize;
		}
		if (outHeight > reqHeight || outWidth > reqWidth) {
			final int heightRatio = Math.round(outHeight / reqHeight);
			final int widthRatio = Math.round(outWidth / reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		final float totalPixels = outWidth * outHeight;
		final float totalReqPixelsCap = reqWidth * reqHeight * 2;

		while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
			inSampleSize++;
		}

		return inSampleSize;
	}

	private static Matrix getMatrix(int orientation) {
		Matrix matrix = new Matrix();
		if (orientation == 90 || orientation == 180 || orientation == 270) {
			matrix.postRotate(orientation);
		}
		return matrix;
	}

	private interface IBitMapReaderCallBack {
		public Bitmap getBitMap(BitmapFactory.Options options);
	}

	public static Bitmap createImageThumbnail(final String filePath, int size, boolean isLow) {
		return compressImage(size, 0, false, isLow,new IBitMapReaderCallBack() {
			@Override
			public Bitmap getBitMap(BitmapFactory.Options options) {
				return BitmapFactory.decodeFile(filePath, options);
			}
		});
	}

	public static Bitmap createImageThumbnailScale(final String filePath,
												   int size, boolean isLow) {
		return compressImage(size, 0, true, isLow, new IBitMapReaderCallBack() {
			@Override
			public Bitmap getBitMap(BitmapFactory.Options options) {
				return BitmapFactory.decodeFile(filePath, options);
			}
		});
	}

	private static Bitmap compressImage(float size, int orientation,
										boolean scale,boolean isLow,IBitMapReaderCallBack bitMapReaderCallBack) {
		Bitmap bmp = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bmp = bitMapReaderCallBack.getBitMap(options);

			float actualHeight = options.outHeight;
			float actualWidth = options.outWidth;

			float destHeight = size;
			float destWidth = size;
			// 解析过程出错，options.outHeight = -1
			if (actualHeight <= 0 || actualWidth <= 0 || size <= 0) {
				return null;
			}
			if (scale) {
				if (actualHeight > actualWidth) {
					destWidth = (actualWidth * size) / actualHeight;
					destHeight = size;
				} else if (actualWidth > actualHeight) {
					destHeight = (actualHeight * size) / actualWidth;
					destWidth = size;
				}
			}

			options.inSampleSize = calculateInSampleSize(
					options.outWidth, options.outHeight,
					destWidth, destHeight);
			options.inJustDecodeBounds = false;
			options.inPurgeable = true;
			options.inInputShareable = true;
			options.inTempStorage = new byte[16 * 1024];

			Bitmap scaledBitmap = null;

			bmp = bitMapReaderCallBack.getBitMap(options);
			if (bmp == null) {
				return bmp;
			}
			scaledBitmap = Bitmap.createBitmap((int) destWidth, (int) destHeight, isLow?Bitmap.Config.RGB_565:Bitmap.Config.ARGB_8888);

			float ratioX = destWidth / options.outWidth;
			float ratioY = destHeight / options.outHeight;
			float middleX = destWidth / 2.0f;
			float middleY = destHeight / 2.0f;
			if (!scale) {
				// 计算非缩放下的放大倍数及放大后中心点
				ratioX = ratioX > ratioY ? ratioX : ratioY;
				ratioY = ratioX;
				middleX = ((options.outWidth) * ratioX) / 2.0f;
				middleY = ((options.outHeight) * ratioY) / 2.0f;
			}

			Matrix scaleMatrix = new Matrix();
			scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

			ColorMatrix mSaturationMatrix = new ColorMatrix();
			mSaturationMatrix.reset(); // 设为默认值
			// mLightnessMatrix.setRotate(0, 100); // 控制让红色区在色轮上旋转的角度
			// mLightnessMatrix.setRotate(1, 100); // 控制让绿红色区在色轮上旋转的角度
			// mLightnessMatrix.setRotate(2, 100); // 控制让蓝色区在色轮上旋转的角度
			mSaturationMatrix.setSaturation(1.3f);

			Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
			paint.setColorFilter(new ColorMatrixColorFilter(mSaturationMatrix));

			Canvas canvas = new Canvas(scaledBitmap);
			canvas.setMatrix(scaleMatrix);

			float x = middleX - (bmp.getWidth()) / 2.0f;
			float y = middleY - (bmp.getHeight()) / 2.0f;
			if (!scale) {
				// 计算截取图片中心缩放中心坐标
				if (bmp.getWidth() > bmp.getHeight()) {
					x = x - ((bmp.getWidth() - bmp.getHeight()) / 2.0f);
				} else {
					y = y - ((bmp.getHeight() - bmp.getWidth()) / 2.0f);
				}
			}
			canvas.drawBitmap(bmp, x, y, paint);
			Matrix matrix = getMatrix(orientation);
			scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(),matrix, true);

			return scaledBitmap;
		} catch (Throwable t) {
			if (t != null) {
//				KLog.debug(KLog.KLogFeature.alone,
//						"BitmapUtil.decodeBitmapFromPath(String path, Activity activity) Exception "
//								+ t.getMessage());
			}
			return null;
		}finally{
			if(bmp !=null&&!bmp.isRecycled()){
				bmp.recycle();
				bmp = null;
				System.gc();
			}
		}
	}
}

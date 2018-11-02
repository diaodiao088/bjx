package com.bjxapp.worker.utils.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import org.json.JSONObject;

import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.utils.BitmapUtils;
import com.bjxapp.worker.utils.FileUtils;
import com.bjxapp.worker.utils.Logger;
import com.bjxapp.worker.utils.NetworkUtils;
import com.bjxapp.worker.utils.SHA1Utils;
import com.bjxapp.worker.utils.TimeUtils;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager.DataType;
import com.bjxapp.worker.utils.http.HttpUtils;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.igexin.sdk.GTServiceManager.context;
import static java.lang.String.valueOf;

public class PictureUploadUtils {
	public static final String TAG = "PictureUploadUtils";
    public static final String PIC_SUFFIX = ".jpg";

    public static void startPicLoadPolling(Timer pollingTimer,final Context context)
    {
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                Logger.i(TAG,"do upload and download pic period");
                if (NetworkUtils.isWifi(context)) {

                }
            }
        };
        pollingTimer.schedule(task, (long)(TimeUtils.ONE_MINUTE_MILLIS * 1 * Math.random()), TimeUtils.ONE_HOUR_MILLIS*4);
    }

	public static List<String> getUploadFiles(Context context) {
		List<String> fileList = new ArrayList<String>();

		//todo:添加需要上传的文件
		
		return fileList;
	}

	/**
	 * 取出需要下载的图片,ImageDownloadTask使用
	 */
	public static List<String> getDownloadFiles(Context context) {
		List<String> downloadList = new ArrayList<String>();

		//todo:添加需要下载的文件
		
		return downloadList;
	}

	/**
	 * 构造网络上传文件路径
	 */
	public static String constructNetUploadPath(String uploadUrl, String name) {
		if(Utils.isNotEmpty(name)){
			return uploadUrl + File.separator + name;
		}
		
		return uploadUrl;
	}

	/**
	 * 构造网络下载文件路径
	 */
	public static String constructNetDownloadPath(String downloadUrl, String name) {
		return downloadUrl + File.separator + name;
	}

	/**
	 * 构造图片的Sha1名字
	 */
	public static String getPicSha1Name(Bitmap bitmap) throws NoSuchAlgorithmException, IOException {
		return getPicSha1(bitmap) + PIC_SUFFIX;
	}

	/**
	 * 计算图片的SHA1值
	 */
	private static String getPicSha1(Bitmap bitmap)
			throws NoSuchAlgorithmException, IOException {
		String sha1Str;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, baos);
		sha1Str = SHA1Utils.SHA1(baos.toByteArray());
		baos.close();
		return sha1Str;
	}

	public static String getBitmapContentType(String name) {
		String suffix = FileUtils.getFileExt(name);
		if (suffix.endsWith("jpg") || suffix.endsWith("jpeg"))
			return "image/jpeg";
		else if (suffix.endsWith("png"))
			return "image/png";
		else if (suffix.endsWith("bmp"))
			return "image/bmp";
		else if (suffix.endsWith("gif"))
			return "image/gif";
		else
			return "";
	}

	public static String uploadImage(String uploadUrl, String key, Context context, String serverDir) {

		post_file(uploadUrl , null ,new File(key));

		return null;
	}


	public static void post_file(final String url, final Map<String, Object> map, File file) {
		OkHttpClient client = new OkHttpClient();
		MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
		if (file != null) {
			RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
			requestBody.addFormDataPart("file", file.getName(), body);
		}

		if (map != null) {
			for (Map.Entry entry : map.entrySet()) {
				requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
			}
		}
		Request request = new Request.Builder().url(url).post(requestBody.build()).tag(context).build();
		// readTimeout("请求超时时间" , 时间单位);
		client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (response.isSuccessful()) {
					String str = response.body().string();

					Log.d("slog_zd","body: " + str + " , msg: " + response.message().toString());

				} else {
				}
			}
		});
	}

	public static String uploadImage(String uploadUrl, String key, Context context, String serverDir, String fileName, long maxSize) {
		String returnUrl = ""; 
		
		String filename = FileUtils.getImgNameWithImageExt(fileName);
		
		//String url = constructNetUploadPath(uploadUrl,filename);
		String url = uploadUrl;
		String contentType = getBitmapContentType(filename);
		try {
			url = ImageSyncLogic.getInstance(context).constructUrlAppends(url, serverDir);
			
			/*
			 * 图片尺寸会成比例压缩
			 */
    		Bitmap bitmap = BitmapUtils.compressBitmap(context, key, maxSize);
    		byte[] bitmapBytes = BitmapUtils.bmpToByteArray(bitmap, true);
    		
			
			/*
			 * 只压缩size，图片尺寸不变，效果还不错，但是加载图片内存过大，不适合显示，只适合上传
			 */
    		//byte[] bitmapBytes = BitmapUtils.compressImageWithoutScale(key, maxSize);
    		
			HttpURLConnection conn = HttpUtils.sendFormdata(url, filename, contentType, bitmapBytes);
			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
				String encoding = conn.getContentEncoding();
				InputStream in = conn.getInputStream();
	            if (encoding != null && encoding.equals("gzip")) {
	                in = new GZIPInputStream(in);
	            }
	            String result = HttpUtils.inputStream2String(in);
	            try{
		            JSONObject json = new JSONObject(result);
					if (!json.isNull("file_path")) {
						returnUrl = json.getString("file_path");
					}
	            }
	            catch(Exception ex){
	            	
	            }
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return returnUrl;
	}
	
	public static boolean uploadImage(String uploadUrl, String key, DataType dataType, Activity activity) {
		String filename = FileUtils.getImgNameWithXMExt(key);
		//String url = constructNetUploadPath(uploadUrl,filename);
		String url = uploadUrl;
		String contentType = getBitmapContentType(filename);
		try {
			url = ImageSyncLogic.getInstance(activity).constructUrlAppends(url, Constant.UPLOAD_URL_SERVER_DIR_USER);
			DiskCacheManager dm = DiskCacheManager.getInstance(activity);
			File file = dm.getFile(dataType, key);
			HttpURLConnection conn = HttpUtils.sendFormdata(url, filename, contentType, FileUtils.getBytesFromFile(file));
			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
				return true;
			}

			return false;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public static class RecycleBitmap {
		public Bitmap bitmap;
		// 由路径创建的bitmap可回收，
		// 由drawable创建的bitmap不可回收
		public boolean canRecycle = false;

		public RecycleBitmap() {
		}

		public RecycleBitmap(Bitmap bitmap, boolean canRecycle) {
			this.bitmap = bitmap;
			this.canRecycle = canRecycle;
		}

		public void recycle() {
			if (bitmap != null && canRecycle && !bitmap.isRecycled())
				bitmap.recycle();
		}
	}

}

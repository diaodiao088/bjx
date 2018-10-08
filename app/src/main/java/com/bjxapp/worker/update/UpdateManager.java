package com.bjxapp.worker.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.bjxapp.worker.global.Constant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

public class UpdateManager {
	ProgressDialog progressDialog;
	Handler handler;
	Context context;
	
	public UpdateManager(Context context){
		this.context=context;
	}
	
	public void downLoadFile(final String url,final ProgressDialog inProgressDialog,Handler inHandler){
		progressDialog=inProgressDialog;
		handler=inHandler;
		new Thread() {
			public void run() {        
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					int length = (int) entity.getContentLength();
					progressDialog.setMax(length);
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						File file = new File(Environment.getExternalStorageDirectory(),Constant.APP_UPDATE_FILENAME);
						fileOutputStream = new FileOutputStream(file);
                        byte[] buf = new byte[2048];
						int ch = -1;
						int process = 0;
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							process += ch;
							progressDialog.setProgress(process); 
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					down();
				} catch (ClientProtocolException e) {
					progressDialog.cancel();
					e.printStackTrace();
					progressDialog.dismiss();
				} catch (IOException e) {
					progressDialog.cancel();
					progressDialog.dismiss();
					e.printStackTrace();
				}
			}

		}.start();
	}
	
	void down() {
		handler.post(new Runnable() {
			public void run() {
				progressDialog.cancel();
				progressDialog.dismiss();
				update();
			}
		});
	}

	void update() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), Constant.APP_UPDATE_FILENAME)),"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}	
}

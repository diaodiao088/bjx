package com.bjxapp.worker.utils.example;

public class Examples {
	//上传图片示例
	/*
    private AsyncTask<String, Void, Void> mHeadImageTask;
    private void uploadHeadImage(String filename)
    {
    	mHeadImageTask = new AsyncTask<String, Void, Void>(){
    		
			@Override
			protected void onPreExecute() {

			}
        
			@Override
			protected Void doInBackground(String... params) {
				String url = params[0];
				PictureUploadUtils.uploadImage(url, mActivity);
				
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				Utils.showShortToast(mActivity,"图片上传成功");
			}
		};

		mHeadImageTask.execute(filename);
    }
    */
    
	//下载图片示例
	/*
	private BitmapManager mBitmapManager;
	private RecyclableImageView mRecycleImageTest;
	
	mBitmapManager = BitmapManager.getInstance(mActivity);
	mRecycleImageTest = (RecyclableImageView)findViewById(R.id.recyclableImageTest);
	String imageUrl = "http://weixin.huashukuaixiu.com/images/server/1430605572579.jpg";
	int[] size = {OurContext.getScreenWidth(mActivity), 500};
	mBitmapManager.loadBitmap(imageUrl, DataType.Local, mOnBitmapLoadListener,size);
  	
    BitmapManager.OnBitmapLoadListener mOnBitmapLoadListener = new BitmapManager.OnBitmapLoadListener() {
		@Override
		public void onLoaded(String url, Bitmap bitmap, boolean isSuccessful) {
			if(isSuccessful && bitmap != null)
			{
				mRecycleImageTest.setImageBitmap(bitmap);
			}
		}
    };
	*/
	
	//异步多线程处理方案 1
	/*
	private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what) {
            case 0:
            	Utils.showLongToast(context, msg.obj.toString());
                break;
            case 1:                
                break;
            }
        }
    };
    
	Runnable task = new Runnable() {
		@Override
		public void run() {
			String result = com.bjxapp.worker.api.TestAPI.getTest(context);
			mHandler.obtainMessage(0,result).sendToTarget();
		}
	};
	new Thread(task).start();
	*/
	
	//异步多线程处理方案 2
	/*
    new AsyncTask<Void, Void, String>() {
        @Override
        protected String doInBackground(Void... params) {
            return com.bjxapp.worker.api.TestAPI.getTest(context);
        }

        @Override
        protected void onPostExecute(String result) {
        	Utils.showLongToast(context, result);
        }

    }.execute();
    */
	
	////异步多线程处理方案 3
	/*
	MyAsyncTask testAsyncTask = new MyAsyncTask();
	testAsyncTask.execute("");
	
	private class MyAsyncTask extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... params) {
            return com.bjxapp.worker.api.TestAPI.getTest(context);
        }

        @Override
        protected void onPostExecute(String result) {
        	Utils.showLongToast(context, result);
        	et_usertel.setText("00000000000");
        }
		
		@Override
		protected void onCancelled() {
			
		}
		
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			
		}
	}
	*/	
}

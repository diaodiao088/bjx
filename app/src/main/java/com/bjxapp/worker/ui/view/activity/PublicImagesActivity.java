package com.bjxapp.worker.ui.view.activity;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjxapp.worker.adapter.ImagesAdapter;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.controls.listview.XListView;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.ImageInfo;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.FileUtils;
import com.bjxapp.worker.utils.ImagePathUtils;
import com.bjxapp.worker.utils.SDCardUtils;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager.DataType;
import com.bjxapp.worker.R;

public class PublicImagesActivity extends BaseActivity implements OnClickListener {
	protected static final String TAG = "照片界面";
	private XTextView mTitleTextView;
	private XImageView mBackImageView;
	private XImageView mAddImageView;
	private XButton mUploadButton;
	
	private ArrayList<ImageInfo> mImagesArray = new ArrayList<ImageInfo>();
    private ImagesAdapter mImagesAdapter;
    private XListView  mXListView;
    private TextView mSmallTv;
    public LinearLayout mTipLy;
    
    private Uri mPhotoUri;
    private ArrayList<String> mImagesURL;
    private ArrayList<String> mUploadedFilenames = null;
    private ArrayList<String> mDeletedFilenames = null;
    private int mOperationFlag = 0;
    private String mTitle = "";
    private int mCount = 2;
    private int mType = 1;
    
    private XWaitingDialog mWaitingDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_images);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initControl() {
		mWaitingDialog = new XWaitingDialog(context);
		
		mTitleTextView = (XTextView) findViewById(R.id.title_text_tv);
		mTitle = getIntent().getStringExtra("title");
		if(Utils.isNotEmpty(mTitle)){
			mTitleTextView.setText(mTitle);
		}
		else {
			mTitleTextView.setText("上传照片");
		}
		
		mCount = getIntent().getIntExtra("count",2);
		mType = getIntent().getIntExtra("type",1);
		
		mUploadButton = (XButton) findViewById(R.id.images_button_upload);
		mAddImageView = (XImageView) findViewById(R.id.title_image_right);
		mAddImageView.setVisibility(View.GONE);
		mSmallTv = findViewById(R.id.title_right_small_tv);
		mSmallTv.setVisibility(View.VISIBLE);
		mTipLy = findViewById(R.id.tip_bg);
		mAddImageView.setImageResource(R.drawable.icon_menu_add);
		
		mOperationFlag = Integer.parseInt(getIntent().getStringExtra("operation_flag"));
		if(mOperationFlag == 2){
			mUploadButton.setVisibility(View.GONE);
			mAddImageView.setVisibility(View.GONE);
		}
		mImagesURL = getIntent().getStringArrayListExtra("urls");
		
		mBackImageView = (XImageView) findViewById(R.id.title_image_back);
		mBackImageView.setVisibility(View.VISIBLE);
		
		mXListView = (XListView) findViewById(R.id.images_upload_listview);
		mImagesAdapter= new ImagesAdapter(context, mImagesArray);
        mXListView.setAdapter(mImagesAdapter);
        mXListView.setCacheColorHint(Color.TRANSPARENT);
        mXListView.setPullRefreshEnable(false);
        mXListView.setPullLoadEnable(false);
		mXListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ImageInfo imageInfo = (ImageInfo)mXListView.getItemAtPosition(position);
				File file = new File(imageInfo.getUrl());
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file), "image/*");
				startActivity(intent);
			}
		});
		
		mXListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
 		{ 
            @Override 
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) 
            {
            	if(mOperationFlag == 2){
            		return true;
            	}
            	
            	Dialog alertDialog = new AlertDialog.Builder(PublicImagesActivity.this).
        		     setTitle("移除图片").
        		     setMessage("是否要移除此图片？").
        		     setPositiveButton("移除", new DialogInterface.OnClickListener() {
        		     @Override
        		     public void onClick(DialogInterface dialog, int which) {
        		    	 int index = position-1;
        		    	 addDeletedFilename(mImagesArray.get(index).getUrl());
        		    	 mImagesAdapter.deleteImage(index);
        		      }
        		     }).
        		     setNegativeButton("取消", new DialogInterface.OnClickListener() {
        		     @Override
        		     public void onClick(DialogInterface dialog, int which) {
        		     }
        		     }).
        		     create();
            	alertDialog.show();
            		 
                return true;
            } 
      });
	}

	@Override
	protected void initView() {

	}

	@Override
	protected void initData() {
		if(mImagesURL == null) return;
		
		for(int i=0;i<mImagesURL.size();i++){
			addImageToList(mImagesURL.get(i), 1);
		}
	}

	@Override
	protected void setListener() {
		mBackImageView.setOnClickListener(this);
		mAddImageView.setOnClickListener(this);
		mUploadButton.setOnClickListener(this);
		mSmallTv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_image_back:
			//Utils.finishActivity(PublicImagesActivity.this);
			onBackPressed();
			break;
		case R.id.title_image_right:
		case R.id.title_right_small_tv:
			showSelectImageDialog();
			break;
		case R.id.images_button_upload:
			uploadImages();
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		//super.onBackPressed();

		if (mImagesArray.size() < mCount) {
			Utils.showShortToast(context, "请至少上传两张照片！");
		}else{
			super.onBackPressed();
		}
	}

	private void showSelectImageDialog() {
		if(mImagesAdapter.getCount() >= mCount){
			Utils.showShortToast(context, "只能上传两张照片！");
			return;
		}
		
        final CharSequence[] items = getResources().getStringArray(R.array.user_select_image_items);
        AlertDialog dlg = new AlertDialog.Builder(context)
                .setTitle("选择图片")
                .setNegativeButton("取消", null)
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        try {
                            if (!SDCardUtils.exist()) {
                                Utils.showShortToast(context,"SD卡被占用或不存在");
                            } else {
                                if (item == 0) {
                                    Utils.startChooseLocalPictureActivity(context, Constant.REQUEST_CODE_CLOCK_CHOOSE_LOCAL_IMG);
                                } else {
                                	//todo:小米此方法不行，暂不处理
                                    ContentValues contentValues = new ContentValues();
                                    mPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
                                    Utils.startTakePhotoActivity(context, Constant.REQUEST_CODE_CLOCK_TAKE_PHOTO, mPhotoUri);
                                }
                            }
                        } catch (Exception e) {
                        	Utils.showShortToast(context,"SD卡被占用或不存在");
                        }
                    }
                }).create();
        dlg.show();
    }
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	try {
	        switch (requestCode) {
			    case Constant.REQUEST_CODE_CLOCK_CHOOSE_LOCAL_IMG:
	                if (data != null) {
	                    Uri originalUri = data.getData();
	                    String imagePath = Utils.getFilePathFromIntentData(originalUri, context);
	                    addImageToList(imagePath, 0);
	                }
			        break;
			    case Constant.REQUEST_CODE_CLOCK_TAKE_PHOTO:
                    if (resultCode == Activity.RESULT_OK) {
                        String photoPath = Utils.getPhotoUrl(context, mPhotoUri, data);
                        if (Utils.isNotEmpty(photoPath)) {
                        	addImageToList(photoPath, 0);
                        }
                    }
			        break;
			}
    	}
    	catch(Exception e){
    		Utils.showShortToast(context,"选择图片失败！");
    	}
    }
    
    private void addImageToList(String imagePath, int flag){
		ImageInfo imageInfo=new ImageInfo();
		imageInfo.setUrl(imagePath);
		imageInfo.setFlag(flag);
		mImagesAdapter.addImage(imageInfo);
		mTipLy.setVisibility(View.GONE);
		mUploadButton.setVisibility(View.VISIBLE);
    }
    
    private void addDeletedFilename(String filePath){
    	if(mDeletedFilenames == null){
    		mDeletedFilenames = new ArrayList<String>();
    	}
    	mDeletedFilenames.add(FileUtils.getImgNameWithImageExt(filePath));
    }
    
    private AsyncTask<Void, Void, Boolean> mUploadImagesTask;
    private void uploadImages()
    {
    	if(!Utils.isNetworkAvailable(context)){
    		Utils.showShortToast(context, getString(R.string.common_no_network_message));
    		return;
    	}
    	
    	if(mImagesArray.size() == mCount){
    		mUploadedFilenames = new ArrayList<String>();
    		mUploadedFilenames.clear();
    	}
    	else {
    		Utils.showShortToast(context,"需要" + mCount + "张图片才能保存！");
			return;
		}
    	mWaitingDialog.show(getString(R.string.images_upload_waiting_message), false);
    	
    	mUploadImagesTask = new AsyncTask<Void, Void, Boolean>(){
    		
			@Override
			protected void onPreExecute() {

			}
        
			@Override
			protected Boolean doInBackground(Void... params) {
				String uploadUrl = APIConstants.IMAGE_IDS_UPLOAD_URL;
				String serverPath = "";
				if(mType == Constant.UPLOAD_IMAGE_ID){
					serverPath = ImagePathUtils.getUserIDUploadPath(context);
				}
				
				Boolean result = LogicFactory.getUploadImagesLogic(context).uploadImages(uploadUrl,serverPath, mImagesArray, mUploadedFilenames);
				if(result){
					LogicFactory.getUploadImagesLogic(context).deleteUploadedImages(serverPath, mDeletedFilenames);
					LogicFactory.getUploadImagesLogic(context).deleteLocalImages(DataType.UserData, mDeletedFilenames);
				}
				return result;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				mWaitingDialog.dismiss();
				
				if(result){
					Utils.showShortToast(context,"图片保存成功！");
					Intent intent = new Intent();
		            intent.putStringArrayListExtra("result", mUploadedFilenames);
					setResult(RESULT_OK, intent);
					Utils.finishWithoutAnim(PublicImagesActivity.this);
				}
				else{
					Utils.showShortToast(context,"抱歉，保存失败，请重试！");
				}
			}
		};

		mUploadImagesTask.execute();
    }
    
	@Override
	protected String getPageName() {
		return TAG;
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if(mImagesAdapter!=null)
		{
			mImagesAdapter.clearCache();
		}
	}
}

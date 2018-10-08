package com.bjxapp.worker.ui.view.fragment;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;

import com.bjxapp.worker.SplashActivity;
import com.bjxapp.worker.controls.XCircleImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ActivitiesManager;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.UserApplyInfo;
import com.bjxapp.worker.ui.view.activity.WebViewActivity;
import com.bjxapp.worker.ui.view.activity.user.ApplyActivity;
import com.bjxapp.worker.ui.view.activity.user.BalanceBankActivity;
import com.bjxapp.worker.ui.view.activity.user.BalanceBankWithdrawActivity;
import com.bjxapp.worker.ui.view.base.BaseFragment;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager.DataType;
import com.bjxapp.worker.utils.image.BitmapManager;
import com.bjxapp.worker.R;

public class Fragment_Main_Fourth extends BaseFragment implements OnClickListener {
	protected static final String TAG = "我的";

	private XTextView mUserMobile;
	private XTextView mUserName;
	private XTextView mUserWorkYears;
	private XCircleImageView mHeadImage;
	private XTextView mTotalOrders,mTotalMoney,mMoneyOrder,mWorkLevel,mBalanceMoney;
	private XWaitingDialog mWaitingDialog;
	
    @Override
    protected void initView() {
		initViews();
		setOnListener();
		
		mUserName.setText(ConfigManager.getInstance(mActivity).getUserName());
		displayHeadImage();
		//loadData();
    }
    
    @Override
    protected void finish() {
        
    }
    
    @Override
    protected int onCreateContent() {
        return R.layout.fragment_main_fourth;
    }
    
    @Override
    public void refresh(int enterType) {
    	loadData();
    }
    
	private void initViews() {
		mUserMobile = (XTextView)findViewById(R.id.profile_user_mobile);
		mUserName = (XTextView)findViewById(R.id.profile_user_name);
		mUserWorkYears = (XTextView)findViewById(R.id.profile_user_work_years);
		mHeadImage = (XCircleImageView) findViewById(R.id.profile_user_head);
		
		mTotalOrders = (XTextView)findViewById(R.id.profile_user_total_orders_edit);
		mTotalMoney = (XTextView)findViewById(R.id.profile_user_total_money_edit);
		mMoneyOrder = (XTextView)findViewById(R.id.profile_user_money_order_edit);
		mWorkLevel = (XTextView)findViewById(R.id.profile_user_worker_level_edit);
		mBalanceMoney = (XTextView)findViewById(R.id.profile_user_balance_money_edit);
		
		String userMobile = ConfigManager.getInstance(mActivity).getUserCode();
		mUserMobile.setText(userMobile);
		mUserName.setText(ConfigManager.getInstance(mActivity).getUserName());
		
		mWaitingDialog = new XWaitingDialog(mActivity);
	}

	private void setOnListener() {
		findViewById(R.id.profile_user_balance_money_get_button).setOnClickListener(this);
		findViewById(R.id.profile_user_info).setOnClickListener(this);
		findViewById(R.id.profile_about).setOnClickListener(this);
		findViewById(R.id.profile_service_call).setOnClickListener(this);
		findViewById(R.id.profile_check_update).setOnClickListener(this);
		findViewById(R.id.profile_logout).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.profile_user_balance_money_get_button:
			showWithdraw();
			break;
		case R.id.profile_user_info:
			Utils.startActivityForResult(mActivity, Fragment_Main_Fourth.this, ApplyActivity.class,Constant.ACTIVITY_APPLY_RESULT_CODE);
			break;
		case R.id.profile_about:
			Utils.startActivity(mActivity, WebViewActivity.class, 
					new BasicNameValuePair("title", "关于百家修"), 
					new BasicNameValuePair("url",getString(R.string.service_about_url)));
			break;
		case R.id.profile_service_call:
			callService();
			break;
		case R.id.profile_check_update:
			if(LogicFactory.getUpdateLogic(mActivity).isNeedUpdate(mActivity, false))
			{
				LogicFactory.getUpdateLogic(mActivity).showUpdateDialog(mActivity);
			}
			else 
			{
				Utils.showLongToast(mActivity, "已经是最新版本了，无需更新！");
			}
			break;
		case R.id.profile_logout:
			ConfigManager.getInstance(mActivity).setUserCode("");
			ConfigManager.getInstance(mActivity).setUserSession("");
			ConfigManager.getInstance(mActivity).setUserName("");
			ConfigManager.getInstance(mActivity).setUserStatus(-1);
			ConfigManager.getInstance(mActivity).setUserHeadImageUrl("");
			ConfigManager.getInstance(mActivity).setDesktopMessagesDot(0);
			ConfigManager.getInstance(mActivity).setDesktopMessagesDotServer(0);
			ConfigManager.getInstance(mActivity).setDesktopOrdersDot(0);
			ConfigManager.getInstance(mActivity).setDesktopOrdersDotServer(0);
			ActivitiesManager.getInstance().finishAllActivities();
			Utils.startActivity(mActivity, SplashActivity.class);
			break;	
		default:
			break;
		}
	}

	private void callService(){
		String mobile = getString(R.string.service_telephone);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + mobile));
		startActivity(intent);
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
    	switch (requestCode) {
		    case Constant.ACTIVITY_APPLY_RESULT_CODE:
		        if (resultCode == Activity.RESULT_OK) {
		        	mUserWorkYears.setText(data.getIntExtra("workyears",1) + "年经验");
		        	mUserName.setText(ConfigManager.getInstance(mActivity).getUserName());
		        	displayHeadImage();
		        }
		        break;
		}
    }
	
    private void displayHeadImage(){
    	String imageUrl = ConfigManager.getInstance(mActivity).getUserHeadImageUrl();
    	if(!Utils.isNotEmpty(imageUrl)) return;
    	
		try {
	        BitmapManager.OnBitmapLoadListener mOnBitmapLoadListener = new BitmapManager.OnBitmapLoadListener() {
	    		@Override
	    		public void onLoaded(String url, Bitmap bitmap, boolean isSuccessful) {
	    			if(isSuccessful && bitmap != null)
	    			{
	    				mHeadImage.setImageBitmap(bitmap);
	    			}
	    		}
	        };
	        
	    	BitmapManager.getInstance(mActivity).loadBitmap(imageUrl, DataType.UserData, mOnBitmapLoadListener);
		}
		catch (Exception e) {
			
		}
    }
    
	private AsyncTask<String, Void, UserApplyInfo> mLoadDataTask;
	private void loadData() {
		mLoadDataTask = new AsyncTask<String, Void, UserApplyInfo>() {
	        @Override
	        protected UserApplyInfo doInBackground(String... params) {
	            return LogicFactory.getAccountLogic(mActivity).getRegisterInfo();
	        }

	        @Override
	        protected void onPostExecute(UserApplyInfo result) {
	        	if(result == null){
	        		return;
	        	}
	        	
	        	mUserName.setText(result.getPersonName());
	        	mUserWorkYears.setText(result.getWorkYear() + "年经验");
	        	mTotalOrders.setText(result.getOrderCount() + "单");
	        	mTotalMoney.setText(result.getTotalMoney() + "元");
	        	mMoneyOrder.setText("第" + result.getMoneyOrder() + "名");
	        	mWorkLevel.setText(result.getRank());
	        	mBalanceMoney.setText(result.getBalanceMoney()+"元");
	        	
	        	ConfigManager.getInstance(mActivity).setUserHeadImageUrl(result.getHeadImageUrl());
	        	displayHeadImage();
	        }
	    };
	    mLoadDataTask.execute();
	}
	
    private AsyncTask<Void, Void, Integer> mGetBankStatusTask;
    private void showWithdraw()
    {
    	mWaitingDialog.show("正在查询银行信息，请稍候...", false);
    	mGetBankStatusTask = new AsyncTask<Void, Void, Integer>(){
			@Override
			protected Integer doInBackground(Void... params) {
				int result = LogicFactory.getAccountLogic(mActivity).getBalanceBankStatus();
				if(isCancelled()){
					return -1;
				}
				return result;
			}

			@Override
			protected void onPostExecute(Integer result) {
				mWaitingDialog.dismiss();
				if(result != -1){
					if(result == 0){
						Utils.startActivity(mActivity, BalanceBankActivity.class);
					}
					else {
						Utils.startActivity(mActivity, BalanceBankWithdrawActivity.class);
					}
				}
				else {
					Utils.showShortToast(mActivity, "未知错误，请稍候重试！");
				}
			}
		};

		mGetBankStatusTask.execute();
    }
    
	@Override
	protected String getPageName() {
		return TAG;
	}
	
	@Override
	public void onDestroy(){
		try{
	        if (mLoadDataTask != null){
	        	mLoadDataTask.cancel(true);
	        }
	        if (mGetBankStatusTask != null){
	        	mGetBankStatusTask.cancel(true);
	        }
		}
		catch(Exception e){}
		
        super.onDestroy();
	}

}
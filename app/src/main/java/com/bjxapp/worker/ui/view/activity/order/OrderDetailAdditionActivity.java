package com.bjxapp.worker.ui.view.activity.order;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XEditText;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.R;

public class OrderDetailAdditionActivity extends BaseActivity implements OnClickListener {
	protected static final String TAG = "增项单界面";
	private XTextView mTitleTextView;
	private XImageView mBackImageView;
	
	private XEditText mAddItemEdit,mAddMoneyEdit;
	private XButton mSaveButton;
	
	private XWaitingDialog mWaitingDialog;
	
	private String mOrderID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_order_detail_addition);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initControl() {
		mTitleTextView = (XTextView) findViewById(R.id.title_text_tv);
		mTitleTextView.setText("增项单");
		mBackImageView = (XImageView) findViewById(R.id.title_image_back);
		mBackImageView.setVisibility(View.VISIBLE);
		
		mAddItemEdit = (XEditText) findViewById(R.id.order_detail_addition_content_edit);
		mAddMoneyEdit = (XEditText) findViewById(R.id.order_detail_addition_fee_edit);
		mSaveButton = (XButton) findViewById(R.id.order_detail_addition_save);
		
		mWaitingDialog = new XWaitingDialog(context);
	}

	@Override
	protected void initView() {

	}

	@Override
	protected void initData() {
		mOrderID = getIntent().getStringExtra("order_id");
		String addItem = getIntent().getStringExtra("add_item");
		String addMoney = getIntent().getStringExtra("add_money");
		mAddItemEdit.setText(addItem);
		mAddMoneyEdit.setText(addMoney);
	}

	@Override
	protected void setListener() {
		mBackImageView.setOnClickListener(this);
		mSaveButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_image_back:
			Utils.finishActivity(OrderDetailAdditionActivity.this);
			break;
		case R.id.order_detail_addition_save:
			saveOperation();
		default:
			break;
		}
	}
	
    private void saveOperation(){
    	if(!Utils.isNetworkAvailable(context)){
    		Utils.showShortToast(context, getString(R.string.common_no_network_message));
    		return;
    	}
    	
    	String content = mAddItemEdit.getText().toString().trim();
    	String money = mAddMoneyEdit.getText().toString().trim();
    	
    	if(!Utils.isNotEmpty(money)){
    		money = "0.0";
    	}
    	
		mWaitingDialog.show("正在保存，请稍候...", false);
	    new AsyncTask<String, Void, Integer>() {
	        @Override
	        protected Integer doInBackground(String... params) {
	        	int orderID = Integer.valueOf(params[0]);
	        	return LogicFactory.getDesktopLogic(context).saveOrderAddition(orderID, params[1], params[2]);
	        }
	        @Override
	        protected void onPostExecute(Integer result) {
	        	mWaitingDialog.dismiss();
				if(result == APIConstants.RESULT_CODE_SUCCESS)
				{
			    	Intent intent = new Intent();
					setResult(RESULT_OK, intent);
					Utils.finishWithoutAnim(OrderDetailAdditionActivity.this);
				}
				else {
					Utils.showShortToast(context, "保存失败，请重试！");
				}
	        }

	    }.execute(mOrderID,content,money);
    }

	@Override
	protected String getPageName() {
		return TAG;
	}

}

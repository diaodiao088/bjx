package com.bjxapp.worker.ui.view.activity.search;

import java.util.ArrayList;
import java.util.List;

import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.listview.XListView;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.CommonConsult;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.R;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchSingleActivity extends BaseActivity implements OnClickListener {
	protected static final String TAG = "单选参照界面";
	private XTextView mTitleTextView;
	private XImageView mBackImageView;
	
	private XListView mListView;
	private SearchSingleAdapter mSearchSingleAdapter;
	private List<SearchSingleModel> mSourceDataList;
	private int mConsultType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.layout_search_single_common);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void initControl() {
		Bundle bundle=this.getIntent().getExtras();
		mConsultType = bundle.getInt("type");
		
		String title = LogicFactory.getConsultLogic(context).getConsultTitle(mConsultType);
		mTitleTextView = (XTextView) findViewById(R.id.title_text_title);
		mTitleTextView.setText(title);
		mBackImageView = (XImageView) findViewById(R.id.title_image_back);
		mBackImageView.setVisibility(View.VISIBLE);
		
		mListView = (XListView) findViewById(R.id.layout_search_single_common_listview);
		mListView.setCacheColorHint(Color.TRANSPARENT);
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(false);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int index = position - 1;
				SearchSingleModel searchModel = (SearchSingleModel)mSearchSingleAdapter.getItem(index);
				selectAndBack(searchModel);
			}
		});
	}

	@Override
	protected void initView() {
		
	}
	
	@Override
	protected void initData() {
		loadData();
	}

	@Override
	protected void setListener() {
		mBackImageView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_image_back:
			Utils.finishActivity(SearchSingleActivity.this);
			break;	
		default:
			break;
		}
	}

	private void selectAndBack(SearchSingleModel searchModel){
		Intent intent = new Intent();  
		intent.putExtra("code", searchModel.getCode());  
		intent.putExtra("name", searchModel.getName());  
		setResult(RESULT_OK, intent);
		Utils.finishActivity(SearchSingleActivity.this);
	}
	
	private void loadData() {
		List<CommonConsult> consultData = LogicFactory.getConsultLogic(context).getConsultData(mConsultType, null);
		mSourceDataList = fillData(consultData);
		mSearchSingleAdapter = new SearchSingleAdapter(SearchSingleActivity.this, mSourceDataList);
		mListView.setAdapter(mSearchSingleAdapter);
	}
	
	private List<SearchSingleModel> fillData(List<CommonConsult> data){
		List<SearchSingleModel> dataList = new ArrayList<SearchSingleModel>();
		
		for(CommonConsult item : data){
			SearchSingleModel searchModel = new SearchSingleModel();
			searchModel.setCode(item.getCode());
			searchModel.setName(item.getName());
			dataList.add(searchModel);
		}
		
		return dataList;
	}

	@Override
	protected String getPageName() {
		return TAG;
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
}

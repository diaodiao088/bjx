package com.bjxapp.worker.ui.view.activity.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.listview.XListView;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.CommonConsult;
import com.bjxapp.worker.ui.view.activity.search.SearchSideBar.OnTouchingLetterChangedListener;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;
import com.bjx.master.R;;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends BaseActivity implements OnClickListener {
	protected static final String TAG = "多选参照界面";
	private XTextView mTitleTextView;
	private XImageView mBackImageView;
	private XButton mSaveButton;
	
	private XListView mSortListView;
	private SearchSideBar mSearchSideBar;
	private XTextView mLetterDialog;
	private SearchAdapter mSearchAdapter;
	private SearchEditText mClearEditText;
	private ChineseLetterParser mChineseLetterParser;
	private List<SearchModel> mSourceDataList;
	private LetterComparator mLetterComparator;
	
	private int mConsultType = 0;
	private Map<String, String> mConsultParams = new HashMap<String, String>();
	private String mSelectedCode = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.layout_search_common);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void initControl() {
		Bundle bundle=this.getIntent().getExtras();
		mConsultType = bundle.getInt("type");
		mSelectedCode = bundle.getString("code");
		bundle.remove("type");

		Iterator<String> iter = bundle.keySet().iterator();  
		while (iter.hasNext()) {  
		    String key = iter.next();  
		    String value = (String)bundle.get(key);
		    mConsultParams.put(key, value);
		}
        
		String title = LogicFactory.getConsultLogic(context).getConsultTitle(mConsultType);
		mTitleTextView = (XTextView) findViewById(R.id.title_text_tv);
		mTitleTextView.setText(title);
		mSaveButton = (XButton) findViewById(R.id.layout_search_common_ok_button);
		mBackImageView = (XImageView) findViewById(R.id.title_image_back);
		mBackImageView.setVisibility(View.VISIBLE);
		
		mChineseLetterParser = ChineseLetterParser.getInstance();
		mLetterComparator = new LetterComparator();
		mSearchSideBar = (SearchSideBar) findViewById(R.id.layout_search_common_side_bar);
		mSearchSideBar.setVisibility(View.GONE);
		mLetterDialog = (XTextView) findViewById(R.id.layout_search_common_letter);
		mSearchSideBar.setTextView(mLetterDialog);
		mSearchSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				int position = mSearchAdapter.getPositionForSection(s.charAt(0));
				position++;
				if(position != -1){
					mSortListView.setSelection(position);
				}				
			}
		});
		
		mSortListView = (XListView) findViewById(R.id.layout_search_common_listview);
		mSortListView.setCacheColorHint(Color.TRANSPARENT);
		mSortListView.setPullRefreshEnable(false);
		mSortListView.setPullLoadEnable(false);
		mSortListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int index = position - 1;
				SearchModel searchModel = (SearchModel)mSearchAdapter.getItem(index);
				if(searchModel.getCheck() == 0){
					searchModel.setCheck(1);
				}
				else {
					searchModel.setCheck(0);
				}
				mSearchAdapter.notifyDataSetChanged();
			}
		});
		
		mSortListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { 
            @Override 
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            	int index = position - 1;
            	String result = "编码：" + ((SearchModel)mSearchAdapter.getItem(index)).getCode() + "\n" +
            			"名称：" + ((SearchModel)mSearchAdapter.getItem(index)).getName() + "\n" +
            			"拼音：" + ((SearchModel)mSearchAdapter.getItem(index)).getSortString();
            	Dialog alertDialog = new AlertDialog.Builder(SearchActivity.this).
        		setTitle("项目信息").
       		    setMessage(result).
       		    setPositiveButton("确定", new DialogInterface.OnClickListener() {
           		    @Override
           		    public void onClick(DialogInterface dialog, int which) {
 
           		    }
       		    }).
       		    create();
               	alertDialog.show();
                return true;
            } 
      });
		
		mClearEditText = (SearchEditText) findViewById(R.id.layout_search_common_edit);
		mClearEditText.addTextChangedListener(new TextWatcher() {		
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				filterData(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		
		//hide controls
		mClearEditText.setVisibility(View.GONE);
		mSearchSideBar.setVisibility(View.GONE);
		mLetterDialog.setVisibility(View.GONE);
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
		mSaveButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_image_back:
			Utils.finishActivity(SearchActivity.this);
			break;
		case R.id.layout_search_common_ok_button:
			save();
			break;	
		default:
			break;
		}
	}

	private void save(){
		if(mSourceDataList == null || mSourceDataList.size() == 0){
			Utils.showShortToast(context, "请至少选择一个项目！");
			return;
		}
		
		String ids = "";
		String names = "";
		for (SearchModel model : mSourceDataList) {
			if(model.getCheck() == 1){
				ids = ids + model.getCode() + ",";
				names = names + model.getName() + "、";
			}
		}
		
		if(ids.length() == 0){
			Utils.showShortToast(context, "请至少选择一个项目！");
			return;
		}
		
		ids = ids.substring(0, ids.length()-1);
		names = names.substring(0, names.length()-1);
		
		Intent intent = new Intent();  
		intent.putExtra("code", ids);
		intent.putExtra("name", names);  
		setResult(RESULT_OK, intent);
		Utils.finishActivity(SearchActivity.this);
	}
	
	private AsyncTask<String, Void, List<CommonConsult>> mLoadDataTask;
	private void loadData() {
		if(!Utils.isNetworkAvailable(context)){
			Utils.showShortToast(context, getString(R.string.common_no_network_message));
			return;
		}
		
		mLoadDataTask = new AsyncTask<String, Void, List<CommonConsult>>() {
	        @Override
	        protected List<CommonConsult> doInBackground(String... params) {
	            return LogicFactory.getConsultLogic(context).getConsultData(mConsultType, mConsultParams);
	        }

	        @Override
	        protected void onPostExecute(List<CommonConsult> result) {
	        	if(result == null){
	        		return;
	        	}
	        	
	    		mSourceDataList = fillData(result);
	    		Collections.sort(mSourceDataList, mLetterComparator);
	    		mSearchAdapter = new SearchAdapter(SearchActivity.this, mSourceDataList);
	    		mSortListView.setAdapter(mSearchAdapter);
	    		setSideLetters();
	        }
	    };
	    mLoadDataTask.execute();
	}

	private void setSideLetters(){
		HashMap<String, String> hashLetter = new HashMap<String, String>();
		String letterString = "";
		for(SearchModel searchModel : mSourceDataList){
			String value = searchModel.getSortLetters();
			if(hashLetter.containsKey(value)){
				continue;
			}
			else {
				hashLetter.put(value, value);
			}
			
			if(Utils.isNotEmpty(letterString)){
				letterString = letterString + "," + value;
			}
			else {
				letterString = value;
			}
		}
		String[] letters = letterString.split(",");
		mSearchSideBar.setLetters(letters);
		//mSearchSideBar.setVisibility(View.VISIBLE);
	}
	
	private List<SearchModel> fillData(List<CommonConsult> data){
		List<SearchModel> mSortList = new ArrayList<SearchModel>();
		
		for(CommonConsult item : data){
			SearchModel searchModel = new SearchModel();
			searchModel.setName(item.getName());
			String pinyin = mChineseLetterParser.getSpelling(item.getName()).toUpperCase();
			searchModel.setSortString(pinyin);
			String sortString = pinyin.substring(0, 1).toUpperCase();
			if(sortString.matches("[A-Z]")){
				searchModel.setSortLetters(sortString.toUpperCase());
			}else{
				searchModel.setSortLetters("#");
			}
			searchModel.setCode(item.getCode());
			searchModel.setCheck(0);
			
			if(mSelectedCode != null && mSelectedCode.length() > 0){
				String[] ids = mSelectedCode.split(",");
				for(String idString : ids){
					if(item.getCode().equalsIgnoreCase(idString)){
						searchModel.setCheck(1);
					}
				}
			}
			
			mSortList.add(searchModel);
		}
		return mSortList;
	}
	
	private void filterData(String filterStr){
		List<SearchModel> filterDataList = new ArrayList<SearchModel>();
		if(!Utils.isNotEmpty(filterStr)){
			filterDataList = mSourceDataList;
			//mSearchSideBar.setVisibility(View.VISIBLE);
		}
		else{
			//mSearchSideBar.setVisibility(View.INVISIBLE);
			filterStr = filterStr.toLowerCase();
			filterDataList.clear();
			for(SearchModel searchModel : mSourceDataList){
				String name = searchModel.getName().toLowerCase();
				String code = searchModel.getCode().toLowerCase();
				String pinyin = searchModel.getSortString().toLowerCase();
				if(name.indexOf(filterStr.toString()) != -1 || code.indexOf(filterStr.toString()) != -1 || pinyin.indexOf(filterStr.toString()) != -1){
					filterDataList.add(searchModel);
				}
			}
		}

		Collections.sort(filterDataList, mLetterComparator);
		mSearchAdapter.updateListView(filterDataList);
	}

	@Override
	protected String getPageName() {
		return TAG;
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}

package com.bjxapp.worker.ui.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.TimeUtils;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.R;

public class MapActivity extends BaseActivity implements OnClickListener {
	protected static final String TAG = "选择地图";
	private XTextView mTitleTextView;
	private XImageView mBackImageView;
	
	private MapView mMapView;
	private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private MyLocationListener listener;
    private Marker mUserLocationMarker;
    private Marker mMyLocationMarker;
	private BitmapDescriptor mUserLocationBD;
	private BitmapDescriptor mMyLocationBD;
 
	private double mUserLatitude;
	private double mUserLongitude; 
	private double mMyLatitude;
	private double mMyLongitude; 

	private RelativeLayout mMarkerInfo;
	private XTextView mGuideTextView;
	private XTextView mUserMobile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//初始化定位及地图sdk，已在App.java中添加，此处可以去掉
		SDKInitializer.initialize(getApplicationContext());
		
		setContentView(R.layout.activity_map);
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mUserLatitude = bundle.getDouble("user_latitude",0);
			mUserLongitude = bundle.getDouble("user_longitude",0);
		}
		else {
			//如果不显示地图，一般是经纬度数据搞错了...
			mUserLatitude = 40.0672250000;
			mUserLongitude = 116.36810600004;
		}
		
		//初始化百度定位
		initLocation();
		initUserLocationMaker();
	}

	@Override
	protected void initControl() {
		mTitleTextView = (XTextView) findViewById(R.id.title_text_title);
		mTitleTextView.setText("地图");
		mBackImageView = (XImageView) findViewById(R.id.title_image_back);
		mBackImageView.setVisibility(View.VISIBLE);
		
		//获取地图控件引用  
		mMarkerInfo = (RelativeLayout) findViewById(R.id.map_popup_marker_info);
		mGuideTextView = (XTextView) findViewById(R.id.map_popup_guide);
		mGuideTextView.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				mMarkerInfo.setVisibility(View.GONE);
				openBaiduMap();
			}
		});
		
		mUserMobile = (XTextView) findViewById(R.id.map_popup_mobile);
		mUserMobile.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mUserMobile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String mobile = (String) mUserMobile.getText();
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + mobile));
				startActivity(intent);
			}
		});
		
        mMapView = (MapView) findViewById(R.id.mapView);
        mBaiduMap = mMapView.getMap();
        
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {
				if (marker == mUserLocationMarker) {
					mMarkerInfo.setVisibility(View.VISIBLE);
				}
				
				if (marker == mMyLocationMarker) {
					Utils.showShortToast(MapActivity.this, "这是您现在的位置");
				}
				
				return true;
			}
		});
		
		mBaiduMap.setOnMapClickListener(new OnMapClickListener()
		{
			@Override
			public boolean onMapPoiClick(MapPoi arg0)
			{
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0)
			{
				mMarkerInfo.setVisibility(View.GONE);
				mBaiduMap.hideInfoWindow();

			}
		});
	}

	@Override
	protected void initView() {

	}

	@Override
	protected void initData() {
		
	}

	@Override
	protected void setListener() {
		mBackImageView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_image_back:
			Utils.finishActivity(MapActivity.this);
			break;
		default:
			break;
		}
	}

	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy(); 
        mMapView = null;
        
        mLocationClient.stop();
        
        if(mUserLocationBD != null) mUserLocationBD.recycle();
        if(mMyLocationBD != null ) mMyLocationBD.recycle();
    } 
	
    @Override  
    protected void onResume() {  
        super.onResume();   
        mMapView.onResume(); 
     
        mLocationClient.start();
    }
    
    @Override  
    public void onPause() {  
        super.onPause();   
        mMapView.onPause();  
        
        mLocationClient.stop();
    }  

	@Override
	protected String getPageName() {
		return TAG;
	}
	
   public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
        	if(location != null && mMapView != null)
        	{
        		initMyLocationMaker(location.getLatitude(),location.getLongitude());
				mLocationClient.stop();
        	}
        }
    }
    
    private void initLocation(){
        mLocationClient  = new LocationClient(MapActivity.this);
        listener = new MyLocationListener();
        mLocationClient.registerLocationListener(listener);
        
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan((int)TimeUtils.ONE_SECOND_MILLIS * 5);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }
    
    private void initUserLocationMaker(){
    	LatLng userLocationLL = new LatLng(mUserLatitude, mUserLongitude);

    	mUserLocationBD = BitmapDescriptorFactory.fromResource(R.drawable.icon_location_c);
		OverlayOptions userLocationOO = new MarkerOptions().position(userLocationLL).icon(mUserLocationBD).zIndex(9).draggable(false);
		mUserLocationMarker = (Marker) (mBaiduMap.addOverlay(userLocationOO));	
		
		LatLng ll = new LatLng(mUserLatitude,mUserLongitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u);
		float zoomScale = 16.00f;
		u = MapStatusUpdateFactory.zoomTo(zoomScale);
		mBaiduMap.animateMapStatus(u);
    }

    private void initMyLocationMaker(double latitude,double longitude){
    	mMyLatitude = latitude;
    	mMyLongitude = longitude;
    	LatLng myLocationLL = new LatLng(latitude, longitude);

    	mMyLocationBD = BitmapDescriptorFactory.fromResource(R.drawable.icon_location_a);
		OverlayOptions myLocationOO = new MarkerOptions().position(myLocationLL).icon(mMyLocationBD).zIndex(8).draggable(false);
		mMyLocationMarker = (Marker) (mBaiduMap.addOverlay(myLocationOO));

		LatLng southwest = new LatLng(latitude, longitude);
		LatLng northeast = new LatLng(mUserLatitude, mUserLongitude);
		LatLngBounds bounds = new LatLngBounds.Builder().include(northeast).include(southwest).build();

		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
		mBaiduMap.setMapStatus(u);
    }
    
    public void openBaiduMap(){
		LatLng pt_start = new LatLng(mMyLatitude, mMyLongitude);
		LatLng pt_end = new LatLng(mUserLatitude, mUserLongitude);										
		RouteParaOption para = new RouteParaOption()
		.startPoint(pt_start).endPoint(pt_end);
		
		try {
			BaiduMapRoutePlan.openBaiduMapDrivingRoute(para, MapActivity.this);	
		} catch (Exception e) {
			e.printStackTrace();
			showDialog();
		}
    }

	public void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				OpenClientUtil.getLatestBaiduMapApp(MapActivity.this);
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();
		
	}
}

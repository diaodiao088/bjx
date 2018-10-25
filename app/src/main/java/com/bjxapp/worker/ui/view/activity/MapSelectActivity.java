package com.bjxapp.worker.ui.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.R;

public class MapSelectActivity extends BaseActivity implements OnClickListener {

    protected static final String TAG = "选择地图";
    private XTextView mTitleTextView;
    private XImageView mBackImageView;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Marker mMyLocationMarker;
    private BitmapDescriptor mMyLocationBD;
    private XButton mSaveButton;

    private double mUserLatitude;
    private double mUserLongitude;
    private String mAddress = "";
    private String mCity = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_map_select);
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mUserLatitude = bundle.getDouble("latitude", 0.0);
            mUserLongitude = bundle.getDouble("longitude", 0.0);
            mAddress = bundle.getString("address", "");
            mCity = bundle.getString("city", "");
        } else {
            //如果不显示地图，一般是经纬度数据搞错了...
            mUserLatitude = 39.931755;
            mUserLongitude = 116.535819;
            mAddress = "";
            mCity = "";
        }

        if (mUserLatitude > 0.0) {
            initMyLocationMaker(mUserLatitude, mUserLongitude);
        }
    }

    @Override
    protected void initControl() {
        mTitleTextView = (XTextView) findViewById(R.id.title_text_title);
        mTitleTextView.setText("选择服务范围");
        mBackImageView = (XImageView) findViewById(R.id.title_image_back);
        mBackImageView.setVisibility(View.VISIBLE);

        mSaveButton = (XButton) findViewById(R.id.layout_map_select_save_button);

        mMapView = (MapView) findViewById(R.id.mapView);
        mBaiduMap = mMapView.getMap();

        initMapNew();


        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                if (marker == mMyLocationMarker) {
                    Utils.showShortToast(MapSelectActivity.this, "这是您现在的位置\n[" + mMyLocationMarker.getPosition().latitude + "," + mMyLocationMarker.getPosition().longitude + "]");
                }

                return true;
            }
        });

        mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0) {
                if (mMyLocationMarker == null) {
                    mMyLocationBD = BitmapDescriptorFactory.fromResource(R.drawable.icon_location_a);
                    OverlayOptions myLocationOO = new MarkerOptions().position(arg0).icon(mMyLocationBD).zIndex(8).draggable(true);
                    mMyLocationMarker = (Marker) (mBaiduMap.addOverlay(myLocationOO));
                }

                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(arg0);
                mMyLocationMarker.setPosition(arg0);
                mBaiduMap.animateMapStatus(u);

                getAddress();
            }
        });

        mBaiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {

            }

            public void onMarkerDragEnd(Marker marker) {
                getAddress();
            }

            public void onMarkerDragStart(Marker marker) {

            }
        });
    }

    private PoiSearch poiSearch;

    private void initMapNew(){

        poiSearch = PoiSearch.newInstance();

        poiSearch.setOnGetPoiSearchResultListener(poiSearchListener);

        MapStatus status = new MapStatus.Builder().target(new LatLng(mUserLatitude, mUserLongitude)).build();

        searchMoveFinish(status);

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                searchMoveFinish(mapStatus);
            }
        });

        MapStatus mapStatus = new MapStatus.Builder().zoom(18).build();
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
    }

    private void searchMoveFinish(MapStatus status) {
        GeoCoder geoCoder = GeoCoder.newInstance();
        ReverseGeoCodeOption reverCoder = new ReverseGeoCodeOption();
        reverCoder.location(status.target);

        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) { // TODO
                // stub
                if (arg0 != null && arg0.getPoiList() != null) {

                    Log.d("slog_zd","poiList : " + arg0.getPoiList());
                   // adapter.setData(arg0.getPoiList());
                   // mSearchResultList.setAdapter(adapter);
                   // adapter.notifyDataSetChanged();
                } else {
                    Log.d("slog_zd","没有更多了.");
                }
            }

            @Override
            public void onGetGeoCodeResult(GeoCodeResult arg0) { //

            }
        });

        geoCoder.reverseGeoCode(reverCoder); //
    }


    OnGetPoiSearchResultListener poiSearchListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {

        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };



    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

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
                Utils.finishActivity(MapSelectActivity.this);
                break;
            case R.id.layout_map_select_save_button:
                saveLocation();
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

        if (mMyLocationBD != null) mMyLocationBD.recycle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected String getPageName() {
        return TAG;
    }

    private void initMyLocationMaker(double latitude, double longitude) {

        LatLng myLocationLL = new LatLng(latitude, longitude);

        mMyLocationBD = BitmapDescriptorFactory.fromResource(R.drawable.icon_location_a);
        OverlayOptions myLocationOO = new MarkerOptions().position(myLocationLL).icon(mMyLocationBD).zIndex(8).draggable(true);
        mMyLocationMarker = (Marker) (mBaiduMap.addOverlay(myLocationOO));

        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(myLocationLL);
        mBaiduMap.animateMapStatus(u);
        float zoomScale = 16.00f;
        u = MapStatusUpdateFactory.zoomTo(zoomScale);
        mBaiduMap.animateMapStatus(u);
    }

    private void saveLocation() {
        if (mMyLocationMarker == null || !Utils.isNotEmpty(mAddress)) {
            Utils.showShortToast(context, "请选择您能服务的位置！");
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("latitude", mMyLocationMarker.getPosition().latitude);
        intent.putExtra("longitude", mMyLocationMarker.getPosition().longitude);
        intent.putExtra("address", mAddress);
        intent.putExtra("city", mCity);

        setResult(RESULT_OK, intent);

        Utils.finishActivity(MapSelectActivity.this);
    }

    private void getAddress() {
        /*GeoCoder geoCoder = GeoCoder.newInstance();
        ReverseGeoCodeOption op = new ReverseGeoCodeOption();
        op.location(mMyLocationMarker.getPosition());
        geoCoder.reverseGeoCode(op);
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult arg0) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
                mAddress = arg0.getAddress();
                mCity = arg0.getAddressDetail().city;
                Utils.showLongToast(context, mAddress + "\n" + mCity);
            }
        });*/
    }
}

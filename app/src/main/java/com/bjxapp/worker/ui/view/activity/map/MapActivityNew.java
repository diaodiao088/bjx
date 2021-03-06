package com.bjxapp.worker.ui.view.activity.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.baidu.mapapi.map.MapView;
import com.bjx.master.R;;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xz on 2017/8/8 0008.
 * 关于地图的activity
 *
 * @author xz
 */
public class MapActivityNew extends Activity implements View.OnClickListener {

    public static final String USER_LATITUDE = "latitude";
    public static final String USER_LONGTITUDE = "longtitude";
    public static final String USER_ADDRESS = "address";

    @BindView(R.id.am_map)
    public MapView mMapView;

    @BindView(R.id.am_rv)
    public RecyclerView mRecyclerView;

    @BindView(R.id.title_image_back)
    public XImageView mBackImg;

    @BindView(R.id.title_divider)
    public View mDivider;

    @BindView(R.id.title_text_tv)
    public XTextView mTitleTv;

    private MapService mSer;

    private double mUserLatitude = 0.0;
    private double mUserLongtitude = 0.0;
    private String mAddress = "";

    public static final int MAP_RESULT_CODE = 0x03;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_new);
        ButterKnife.bind(this);
        mTitleTv.setText("服务范围");
        handleIntent();
        mDivider.setVisibility(View.GONE);
        mSer = new MapService();
        mSer.setInitLocation(mUserLatitude, mUserLongtitude, mAddress);
        mSer.init(this);
    }

    private void handleIntent() {

        Intent intent = getIntent();

        if (intent != null) {
            mAddress = intent.getStringExtra(USER_ADDRESS);
            mUserLatitude = intent.getDoubleExtra(USER_LATITUDE, 0);
            mUserLongtitude = intent.getDoubleExtra(USER_LONGTITUDE, 0);
        }

    }


    @OnClick(R.id.title_image_back)
    void clickBack() {
        onBackPressed();
    }

    @OnClick(R.id.search_ly)
    void startSearch() {
        startMapSearchActivity();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.am_location:
                //重新定位用户位置
                mSer.initUserLocation();
                break;
            case R.id.am_search:
                //搜索按钮
                startMapSearchActivity();
                break;
            default:
                break;
        }
    }

    private void startMapSearchActivity() {
        Intent intent = new Intent();
        intent.setClass(this, MapSearchActivity.class);
        startActivityForResult(intent, AppStaticVariable.MAP_SEARCH_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppStaticVariable.MAP_SEARCH_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String address = data.getStringExtra(AppStaticVariable.MAP_SEARCH_ADDRESS);
                double lon = data.getDoubleExtra(AppStaticVariable.MAP_SEARCH_LONGITUDE, 0.0);
                double lat = data.getDoubleExtra(AppStaticVariable.MAP_SEARCH_LATITUDE, 0.0);
                mSer.searchStr(address, lon, lat);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSer.onExit();
        mMapView.onDestroy();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra(USER_LATITUDE, mSer.getSelectLat());
        intent.putExtra(USER_LONGTITUDE, mSer.getSelectLon());
        intent.putExtra(USER_ADDRESS, mSer.getSelectAddress());
        setResult(Activity.RESULT_OK, intent);

        super.onBackPressed();
    }

    /**
     * @return
     */
    public static final void goToActivityForResult(Activity context, double latitude, double longtitude, String address) {

        Intent intent = new Intent();
        intent.setClass(context, MapActivityNew.class);

        intent.putExtra(USER_LATITUDE, latitude);
        intent.putExtra(USER_LONGTITUDE, longtitude);
        intent.putExtra(USER_ADDRESS, address);

        context.startActivityForResult(intent, MAP_RESULT_CODE);
    }


}

package com.bjxapp.worker.ui.view.activity.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.bjx.master.R;;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.ProfileApi;
import com.bjxapp.worker.controls.XCircleImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.LocationInfo;
import com.bjxapp.worker.model.UserInfoA;
import com.bjxapp.worker.ui.view.activity.ChangeCityActivity;
import com.bjxapp.worker.ui.view.activity.PublicImagesActivity;
import com.bjxapp.worker.ui.view.activity.map.MapActivityNew;
import com.bjxapp.worker.utils.LogUtils;
import com.bjxapp.worker.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zhangdan on 2018/10/23.
 * <p>
 * comments:
 */

public class ApplyEditActivity extends Activity {

    /* title bar */
    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @BindView(R.id.user_edit_header_iv)
    XCircleImageView mHeadImage;

    @BindView(R.id.edit_name)
    TextView mNameTv;

    @BindView(R.id.city_tv)
    TextView mCityTv;

    @BindView(R.id.edit_service_tv)
    TextView mServiceTv;

    @BindView(R.id.edit_work_year_tv)
    TextView mWorkYearTv;

    @BindView(R.id.map_edit_tv)
    TextView mMapTv;

    private String mPwd;

    @OnClick(R.id.pwd_ly)
    void clickPwd() {
        ChangePwdActivity.goToActivityForResult(this, ChangePwdActivity.FROM_EDIT_APPLY);
    }

    @OnClick(R.id.edit_id_ly)
    void clickIdImg() {
        showIDImages();
    }

    @OnClick(R.id.service_ly)
    void onClickService() {
        String code = mServiceTv.getTag() == null ? "" : mServiceTv.getTag().toString();
        Utils.startConsultActivity(this, Constant.CONSULT_WORK_SORTS, null, code);
    }

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @OnClick(R.id.user_apply_button_save)
    void onConfirm() {

        if (!Utils.isNetworkAvailable(this)) {
            Utils.showShortToast(this, getString(R.string.common_no_network_message));
            return;
        }

        ProfileApi profileApi = KHttpWorker.ins().createHttpService(LoginApi.URL, ProfileApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());

        if (mMapTv.getTag() != null) {
            LocationInfo info = (LocationInfo) mMapTv.getTag();
            if (info != null) {
                params.put("locationAddress", info.getAddress());
                params.put("latitude", String.valueOf(info.getLatitude()));
                params.put("longitude", String.valueOf(info.getLongitude()));
            }
        }

        if (mServiceTv.getTag() != null) {
            params.put("serviceIds", mServiceTv.getTag().toString());
        }

        retrofit2.Call<JsonObject> call = profileApi.getRegisterInfo(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                LogUtils.log(response.body().toString());

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {

                    JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(ApplyEditActivity.this, "信息保存成功.");
                            }
                        });
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(ApplyEditActivity.this, msg + ":" + code);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }


    @OnClick(R.id.map_ly)
    void clickMap() {
        if (mMapTv.getTag() != null) {
            LocationInfo locationInfo = (LocationInfo) mMapTv.getTag();
            Utils.startMapSelectActivity(this, MapActivityNew.class, locationInfo.getLatitude(), locationInfo.getLongitude(), locationInfo.getAddress(), locationInfo.getCity());
        } else {
            Utils.startMapSelectActivity(this, MapActivityNew.class, Constant.USER_LOCATION_LATITUDE, Constant.USER_LOCATION_LONGITUDE, Constant.USER_LOCATION_ADDRESS, Constant.USER_LOCATION_CITY);
        }
    }

    private ArrayList<String> mIDImageUrls = new ArrayList<>();

    @OnClick(R.id.city_ly)
    void onClickCity() {
        ChangeCityActivity.goToActivityForResult(this);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        ButterKnife.bind(this);
        initView();
        loadData();
    }

    private void loadData() {

        ProfileApi profileApi = KHttpWorker.ins().createHttpService(LoginApi.URL, ProfileApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());

        retrofit2.Call<JsonObject> call = profileApi.getRegisterInfo(params);

        call.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {

                    JsonObject jsonObject = response.body();
                    int code = jsonObject.get("code").getAsInt();
                    if (code == 0) {

                        if (jsonObject.get("info") == null || jsonObject.get("info") instanceof JsonNull) {
                            return;
                        }

                        JsonObject info = jsonObject.get("info").getAsJsonObject();

                        if (info != null) {

                            String mImageAddress = info.get("avatarUrl").getAsString();
                            String identityCardBehindImgUrl = info.get("identityCardBehindImgUrl").getAsString();
                            String identityCardFrontImgUrl = info.get("identityCardFrontImgUrl").getAsString();
                            String identityCardNo = info.get("identityCardNo").getAsString();
                            String latitued = info.get("latitude").getAsString();
                            String longitude = info.get("longitude").getAsString();
                            String locationAddress = info.get("locationAddress").getAsString();
                            String name = info.get("name").getAsString();
                            int regionId = info.get("regionId").getAsInt();
                            String regionName = info.get("regionName").getAsString();
                            int workYear = info.get("workingYear").getAsInt();

                            JsonArray jsonArray = info.get("serviceList").getAsJsonArray();

                            ArrayList<String> serviceList = new ArrayList<>();

                            if (jsonArray.size() > 0) {
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    JsonObject item = (JsonObject) jsonArray.get(i);
                                    serviceList.add(item.get("name").getAsString());
                                }
                            }

                            final UserInfoA userInfoA = new UserInfoA(mImageAddress, identityCardBehindImgUrl, identityCardFrontImgUrl,
                                    identityCardNo, latitued, longitude, locationAddress, name, regionId, regionName, workYear);

                            userInfoA.setmServiceList(serviceList);

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateInfo(userInfoA);
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void showIDImages() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("urls", mIDImageUrls);
        intent.putExtra("operation_flag", "2");
        intent.putExtra("title", "身份证照片");
        intent.putExtra("count", 2);
        intent.putExtra("type", Constant.UPLOAD_IMAGE_ID);
        intent.setClass(this, PublicImagesActivity.class);
        startActivityForResult(intent, Constant.CONSULT_ID_IMAGES);
    }


    private void updateInfo(UserInfoA userInfoA) {
        try {
            Glide.with(this).load(userInfoA.getAvatarUrl()).into(mHeadImage);

            mNameTv.setText(userInfoA.getName());

            mCityTv.setText(userInfoA.getRegionName());
            mCityTv.setTag(userInfoA.getRegionId());

            mWorkYearTv.setText(String.valueOf(userInfoA.getWorkingYear()) + "年");

            mIDImageUrls = new ArrayList<>();
            mIDImageUrls.add(userInfoA.getIdentityCardBehindImgUrl());
            mIDImageUrls.add(userInfoA.getIdentityCardFrontImgUrl());

            LocationInfo locationInfo = new LocationInfo();
            locationInfo.setAddress(userInfoA.getLocationAddress());
            locationInfo.setLatitude(Double.parseDouble(userInfoA.getLatitude()));
            locationInfo.setLongitude(Double.parseDouble(userInfoA.getLongitude()));

            mMapTv.setText(locationInfo.getAddress());
            mMapTv.setTag(locationInfo);

            if (userInfoA.getmServiceList() != null && userInfoA.getmServiceList().size() > 0) {
                StringBuilder builder = new StringBuilder();

                ArrayList<String> list = userInfoA.getmServiceList();

                for (int i = 0; i < list.size(); i++) {
                    builder.append(list.get(i));
                    if (i != list.size() - 1) {
                        builder.append(",");
                    }
                }

                mServiceTv.setText(builder.toString());
            }

            mPwd = userInfoA.getPwd();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void initView() {
        mTitleTextView.setText("个人编辑");
    }


    public static void goToActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ApplyEditActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {

                case Constant.CONSULT_WORK_MAP:
                    if (resultCode == RESULT_OK) {
                        LocationInfo locationInfo = new LocationInfo();
                        locationInfo.setLatitude(data.getDoubleExtra(MapActivityNew.USER_LATITUDE, 0.0));
                        locationInfo.setLongitude(data.getDoubleExtra(MapActivityNew.USER_LONGTITUDE, 0.0));
                        locationInfo.setAddress(data.getStringExtra(MapActivityNew.USER_ADDRESS));
                        mMapTv.setTag(locationInfo);
                        mMapTv.setText(data.getStringExtra(MapActivityNew.USER_ADDRESS));
                    }
                    break;

                case Constant.CONSULT_WORK_CITY:
                    if (resultCode == RESULT_OK && data != null) {
                        String city = data.getStringExtra("city");
                        String regionId = data.getStringExtra("city_id");
                        if (!TextUtils.isEmpty(city)) {
                            mCityTv.setText(city);
                            mCityTv.setTag(regionId);
                        }
                    }
                    break;
                case Constant.CONSULT_WORK_SORTS:
                    if (resultCode == RESULT_OK) {
                        mServiceTv.setTag(data.getStringExtra("code"));
                        mServiceTv.setText(data.getStringExtra("name"));
                    }
                    break;
                case Constant.CONSULT_SETTING_PWD:
                    if (resultCode == RESULT_OK && data != null) {
                        String pwd = data.getStringExtra(ChangePwdActivity.KEY_TYPE);
                        mPwd = pwd;
                    }
                    break;
            }
        } catch (Exception e) {
            Utils.showShortToast(this, "选择图片失败！");
        }
    }

}

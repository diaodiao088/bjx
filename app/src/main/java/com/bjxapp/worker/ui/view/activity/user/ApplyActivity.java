package com.bjxapp.worker.ui.view.activity.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bjx.master.R;;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.apinew.ProfileApi;
import com.bjxapp.worker.apinew.RegisterApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XCircleImageView;
import com.bjxapp.worker.controls.XEditText;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.LocationInfo;
import com.bjxapp.worker.model.UserInfoA;
import com.bjxapp.worker.ui.view.activity.ChangeCityActivity;
import com.bjxapp.worker.ui.view.activity.PublicImagesActivity;
import com.bjxapp.worker.ui.view.activity.WebViewActivity;
import com.bjxapp.worker.ui.view.activity.map.MapActivityNew;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.BitmapUtils;
import com.bjxapp.worker.utils.SDCardUtils;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager.DataType;
import com.bjxapp.worker.utils.image.BitmapManager;
import com.bumptech.glide.Glide;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.String.valueOf;

public class ApplyActivity extends BaseActivity implements OnClickListener {
    protected static final String TAG = "注册界面";

    private Handler mHandler = new Handler(Looper.getMainLooper());

    /* title bar */
    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;
    @BindView(R.id.title_image_back)
    XImageView mBackImageView;

    @BindView(R.id.user_apply_head_image)
    XCircleImageView mHeadImage;

    @BindView(R.id.user_name_edit)
    XEditText mUserNameTv;

    @BindView(R.id.user_id_edit)
    XEditText mUserIDEdit;

    private XTextView mUserOrderAreaEdit, mUserWorkTypesEdit;
    private XTextView mCityEditTv;
    private EditText mUserWorkYearsEdit;
    private XButton mUserSaveButton;
    private ArrayList<String> mIDImageUrls;
    private LinearLayout mHeadImageLayout;

    private CheckBox mProtocolCheckBox;
    private XTextView mProtocalTextView;

    private RelativeLayout mRootView;

    //0:新增，1:修改，2:审核通过,9：只能浏览
    private int operationFlag = 0;

    private XWaitingDialog mWaitingDialog;

    private RelativeLayout mCityLy;
    private RelativeLayout mUploadImageLy;
    private RelativeLayout mPwdLy;

    private Uri mPhotoUri;

    private String mPwd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_apply);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initControl() {

        mTitleTextView.setText("师傅注册");

        mUserOrderAreaEdit = (XTextView) findViewById(R.id.user_apply_location_edit);
        mUserWorkYearsEdit = (EditText) findViewById(R.id.user_apply_work_years_edit);
        mUserWorkTypesEdit = (XTextView) findViewById(R.id.user_apply_work_sort_edit);
        mUserSaveButton = (XButton) findViewById(R.id.user_apply_button_save);
        mUploadImageLy = findViewById(R.id.user_apply_indetity_ly);
        mPwdLy = findViewById(R.id.user_apply_work_pwd_ly);

        mProtocolCheckBox = (CheckBox) findViewById(R.id.user_apply_protocol_check);
        mProtocolCheckBox.setChecked(true);
        mProtocalTextView = (XTextView) findViewById(R.id.user_apply_protocol_text);

        mCityLy = findViewById(R.id.user_apply_city_ly);
        mCityEditTv = findViewById(R.id.user_apply_city_edit);

        mRootView = findViewById(R.id.title_bar_root);
        mRootView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        mTitleTextView.setTextColor(Color.parseColor("#545454"));

        mWaitingDialog = new XWaitingDialog(context);

        loadData();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        //判断审核状态
        int status = ConfigManager.getInstance(context).getUserStatus();

        /*if (status == -1) {
            operationFlag = 0;
        } else if (status == 1) {
            operationFlag = 2;
        } else if (status == 2) {
            operationFlag = 1;
        } else {
            operationFlag = 9;
        }

        if (operationFlag == 0) {
            //handle the map position
            if (Constant.USER_LOCATION_LATITUDE > 0 && Utils.isNotEmpty(Constant.USER_LOCATION_ADDRESS)) {
                LocationInfo locationInfo = new LocationInfo();
                locationInfo.setLatitude(Constant.USER_LOCATION_LATITUDE);
                locationInfo.setLongitude(Constant.USER_LOCATION_LONGITUDE);
                locationInfo.setAddress(Constant.USER_LOCATION_ADDRESS);
                locationInfo.setCity(Constant.USER_LOCATION_CITY);
                mUserOrderAreaEdit.setTag(locationInfo);
                mUserOrderAreaEdit.setText(Constant.USER_LOCATION_ADDRESS);
            }

            //handle work years value
            mUserWorkYearsEdit.setTag(1);
            mUserWorkYearsEdit.setText("1");
        } else {
            displayHeadImage();
            loadData();
        }

        if (operationFlag == 2) {
            mUserNameTv.setEnabled(false);
            mUserIDEdit.setEnabled(false);
        }

        if (operationFlag == 9) {
            mUserSaveButton.setVisibility(View.GONE);
        }

        if (operationFlag == 2 || operationFlag == 9) {
            mHeadImageLayout.setVisibility(View.GONE);
        }*/
    }

    @Override
    protected void setListener() {
        mBackImageView.setOnClickListener(this);

        findViewById(R.id.user_apply_head_layout).setOnClickListener(this);
        findViewById(R.id.user_apply_location_layout).setOnClickListener(this);
        findViewById(R.id.user_apply_work_sort_layout).setOnClickListener(this);
        mCityLy.setOnClickListener(this);
        mProtocalTextView.setOnClickListener(this);
        mUserSaveButton.setOnClickListener(this);
        mUploadImageLy.setOnClickListener(this);
        mPwdLy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_image_back:
                Utils.finishActivity(ApplyActivity.this);
                break;
            case R.id.user_apply_head_layout:
                showSetUserImageDialog();
                break;
            case R.id.user_apply_location_layout:
                showWorkMap();
                break;
            case R.id.user_apply_indetity_ly:
                showIDImages();
                break;
            case R.id.user_apply_work_years_layout:
                showWorkYears();
                break;
            case R.id.user_apply_work_sort_layout:
                showWorkSort();
                break;
            case R.id.user_apply_protocol_text:
                Utils.startActivity(context, WebViewActivity.class,
                        new BasicNameValuePair("title", "接单须知"),
                        new BasicNameValuePair("url", getString(R.string.service_protocol_url)));
                break;
            case R.id.user_apply_button_save:
                saveOperation();
                break;
            case R.id.user_apply_city_ly:
                ChangeCityActivity.goToActivityForResult(this);
                break;
            case R.id.user_apply_work_pwd_ly:
                ChangePwdActivity.goToActivityForResult(this, ChangePwdActivity.FROM_REGISTER_PWD);
                break;
            default:
                break;
        }
    }

    private void showIDImages() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("urls", mIDImageUrls);
        if (operationFlag == 2 || operationFlag == 9) {
            intent.putExtra("operation_flag", "2");
        } else {
            intent.putExtra("operation_flag", "0");
        }
        intent.putExtra("title", "身份证照片");
        intent.putExtra("count", 2);
        intent.putExtra("type", Constant.UPLOAD_IMAGE_ID);
        intent.setClass(context, PublicImagesActivity.class);
        startActivityForResult(intent, Constant.CONSULT_ID_IMAGES);
    }

    private void showWorkSort() {
        if (operationFlag == 9) {
            Utils.showShortToast(context, "不能修改维修领域！");
            return;
        }

        String code = mUserWorkTypesEdit.getTag() == null ? "" : mUserWorkTypesEdit.getTag().toString();
        Utils.startConsultActivity(context, Constant.CONSULT_WORK_SORTS, null, code);
    }

    private void showWorkYears() {
        if (operationFlag == 2 || operationFlag == 9) {
            Utils.showShortToast(context, "已经审核通过，不能修改工作年限！");
            return;
        }
        Utils.startSingleConsultActivity(context, Constant.CONSULT_TYPE_YEARS);
    }

    private void showWorkMap() {
        if (operationFlag == 9) {
            Utils.showShortToast(context, "不能修改您的位置！");
            return;
        }

        if (mUserOrderAreaEdit.getTag() != null) {
            LocationInfo locationInfo = (LocationInfo) mUserOrderAreaEdit.getTag();
            Utils.startMapSelectActivity(context, MapActivityNew.class, locationInfo.getLatitude(), locationInfo.getLongitude(), locationInfo.getAddress(), locationInfo.getCity());
        } else {
            Utils.startMapSelectActivity(context, MapActivityNew.class, Constant.USER_LOCATION_LATITUDE, Constant.USER_LOCATION_LONGITUDE, Constant.USER_LOCATION_ADDRESS, Constant.USER_LOCATION_CITY);
        }
    }

    /*User head image setting begin*/
    private void showSetUserImageDialog() {
        if (operationFlag == 2 || operationFlag == 9) {
            Utils.showShortToast(context, "已经审核通过，不能修改头像！");
            return;
        }

        final CharSequence[] items = getResources().getStringArray(R.array.user_select_image_items);
        AlertDialog dlg = new AlertDialog.Builder(context)
                .setTitle("设置头像")
                .setNegativeButton("取消", null)
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        try {
                            if (!SDCardUtils.exist()) {
                                Utils.showShortToast(context, "SD卡被占用或不存在");
                            } else {
                                if (item == 0) {
                                    Utils.startChooseLocalPictureActivity(context, Constant.REQUEST_CODE_CLOCK_CHOOSE_LOCAL_IMG);
                                } else {
                                    ContentValues contentValues = new ContentValues();
                                    mPhotoUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                                    Utils.startTakePhotoActivity(context, Constant.REQUEST_CODE_CLOCK_TAKE_PHOTO, mPhotoUri);
                                }
                            }
                        } catch (Exception e) {
                            Utils.showShortToast(context, "SD卡被占用或不存在");
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
                        String imgPath = Utils.getFilePathFromIntentData(originalUri, context);
                        Utils.startClipPictureActivity(context, imgPath, originalUri, ClipView.CLIP_FOR_USER_HEAD, true);
                    }
                    break;
                case Constant.REQUEST_CODE_CLOCK_TAKE_PHOTO:
                    if (resultCode == Activity.RESULT_OK) {
                        String photoPath = Utils.getPhotoUrl(context, mPhotoUri, data);
                        if (Utils.isNotEmpty(photoPath)) {
                            Utils.startClipPictureActivity(context, photoPath, ClipView.CLIP_FOR_USER_HEAD, true);
                        }
                    }
                    break;
                case Constant.REQUEST_CODE_CLOCK_CLIP_IMG:
                    if (resultCode == Activity.RESULT_OK) {
                        String imageFile = data.getStringExtra(Constant.EXTRA_KEY_USER_BITMAP);
                        if (!Utils.isNotEmpty(imageFile)) {
                            return;
                        }
                        XCircleImageView headImage = (XCircleImageView) findViewById(R.id.user_apply_head_image);
                        headImage.setImageBitmap(BitmapUtils.getBitmapFromPath(imageFile));
                        if (!Utils.isNetworkAvailable(context)) {
                            Utils.showShortToast(context, getString(R.string.common_no_network_message));
                            return;
                        } else {
                            uploadHeadImage(imageFile);
                        }
                    }
                    break;
                case Constant.CONSULT_TYPE_YEARS:
                    if (resultCode == RESULT_OK) {
                        mUserWorkYearsEdit.setTag(data.getStringExtra("code"));
                        mUserWorkYearsEdit.setText(data.getStringExtra("name"));
                    }
                    break;
                case Constant.CONSULT_WORK_SORTS:
                    if (resultCode == RESULT_OK) {
                        mUserWorkTypesEdit.setTag(data.getStringExtra("code"));
                        mUserWorkTypesEdit.setText(data.getStringExtra("name"));
                    }
                    break;
                case Constant.CONSULT_WORK_MAP:
                    if (resultCode == RESULT_OK) {
                        LocationInfo locationInfo = new LocationInfo();
                        locationInfo.setLatitude(data.getDoubleExtra(MapActivityNew.USER_LATITUDE, 0.0));
                        locationInfo.setLongitude(data.getDoubleExtra(MapActivityNew.USER_LONGTITUDE, 0.0));
                        locationInfo.setAddress(data.getStringExtra(MapActivityNew.USER_ADDRESS));
                        mUserOrderAreaEdit.setTag(locationInfo);
                        mUserOrderAreaEdit.setText(data.getStringExtra(MapActivityNew.USER_ADDRESS));
                    }
                    break;
                case Constant.CONSULT_ID_IMAGES:
                    if (resultCode == RESULT_OK && data != null) {
                        mIDImageUrls = data.getStringArrayListExtra("result");
                        mUploadImageLy.setTag(mIDImageUrls);
                    }
                    break;
                case Constant.CONSULT_WORK_CITY:
                    if (resultCode == RESULT_OK && data != null) {
                        String city = data.getStringExtra("city");
                        String regionId = data.getStringExtra("city_id");
                        if (!TextUtils.isEmpty(city)) {
                            mCityEditTv.setText(city);
                            mCityEditTv.setTag(regionId);
                        }
                    }
                    break;
                case Constant.CONSULT_SETTING_PWD:
                    if (resultCode == RESULT_OK && data != null) {
                        String pwd = data.getStringExtra(ChangePwdActivity.KEY_TYPE);
                        mPwd = pwd;
                        mPwdLy.setTag(new Object());
                    }
                    break;
            }
        } catch (IOException e) {
            Utils.showShortToast(context, "选择图片失败！");
        }
    }

    private String mImageAddress = "";

    private void uploadHeadImage(String filename) {
        mWaitingDialog.show("正在上传头像，请稍候...", false);

        Map<String, String> map = new HashMap<>();
        map.put("userCode", ConfigManager.getInstance(this).getUserCode());
        map.put("token", ConfigManager.getInstance(this).getUserSession());

        post_file(LoginApi.URL + "/image/upload", map, new File(filename));
    }

    public void post_file(final String url, final Map<String, String> map, File file) {
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (file != null) {
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            requestBody.addFormDataPart("files", file.getName(), body);
        }

        if (map != null) {
            for (Map.Entry entry : map.entrySet()) {
                requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
            }
        }
        Request request = new Request.Builder().url(url).post(requestBody.build()).tag(ApplyActivity.this).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                ApplyActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (mWaitingDialog != null) {
                            mWaitingDialog.dismiss();
                        }

                        Utils.showShortToast(context, "头像上传失败，请重新选择头像！");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (mWaitingDialog != null) {
                    mWaitingDialog.dismiss();
                }

                if (response.isSuccessful()) {
                    String str = response.body().string();
                    try {
                        JSONObject object = new JSONObject(str);

                        JSONArray accessAddress = object.getJSONArray("list");

                        String img = accessAddress.get(0).toString();

                        if (TextUtils.isEmpty(img)) {

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Utils.showShortToast(context, "头像上传失败，请重新选择头像！");
                                }
                            });
                        } else {
                            mImageAddress = img;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mHeadImage.setTag(new Object());
                                    Utils.showShortToast(context, "头像上传成功！");
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Utils.showShortToast(context, "头像上传失败，请重新选择头像！");
                        }
                    });
                }
            }
        });
    }


    private void displayHeadImage() {
        String imageUrl = ConfigManager.getInstance(context).getUserHeadImageUrl();
        if (!Utils.isNotEmpty(imageUrl)) return;

        try {
            BitmapManager.OnBitmapLoadListener mOnBitmapLoadListener = new BitmapManager.OnBitmapLoadListener() {
                @Override
                public void onLoaded(String url, Bitmap bitmap, boolean isSuccessful) {
                    if (isSuccessful && bitmap != null) {
                        mHeadImage.setImageBitmap(bitmap);
                    }
                }
            };

            BitmapManager.getInstance(context).loadBitmap(imageUrl, DataType.UserData, mOnBitmapLoadListener);
        } catch (Exception e) {

        }
    }
    
	/*User head image setting begin*/


    private void displayIDImages() {
        /*if (mIDImageUrls == null || mIDImageUrls.size() < 2) return;
        try {
            BitmapManager.OnBitmapLoadListener idOneLoadListener = new BitmapManager.OnBitmapLoadListener() {
                @Override
                public void onLoaded(String url, Bitmap bitmap, boolean isSuccessful) {
                    if (isSuccessful && bitmap != null) {
                        mUserIDImageOneImage.setImageBitmap(bitmap);
                    }
                }
            };

            BitmapManager.OnBitmapLoadListener idTwoLoadListener = new BitmapManager.OnBitmapLoadListener() {
                @Override
                public void onLoaded(String url, Bitmap bitmap, boolean isSuccessful) {
                    if (isSuccessful && bitmap != null) {
                        mUserIDImageTwoImage.setImageBitmap(bitmap);
                    }
                }
            };

            mUserIDImageOneImage.setTag(mIDImageUrls.get(0));
            BitmapManager.getInstance(context).loadBitmap(mIDImageUrls.get(0), DataType.UserData, idOneLoadListener);

            mUserIDImageTwoImage.setTag(mIDImageUrls.get(1));
            BitmapManager.getInstance(context).loadBitmap(mIDImageUrls.get(1), DataType.UserData, idTwoLoadListener);
        } catch (Exception e) {

        }*/
    }

    private void updateInfo(UserInfoA userInfoA) {
        try {
            Glide.with(this).load(userInfoA.getAvatarUrl()).into(mHeadImage);
            mHeadImage.setTag(new Object());

            mCityEditTv.setText(userInfoA.getRegionName());
            mCityEditTv.setTag(userInfoA.getRegionId());

            mUserNameTv.setText(userInfoA.getName());
            mUserWorkYearsEdit.setText(String.valueOf(userInfoA.getWorkingYear()));

            mUserIDEdit.setText(userInfoA.getIdentityCardNo());

            mIDImageUrls = new ArrayList<>();
            mIDImageUrls.add(userInfoA.getIdentityCardBehindImgUrl());
            mIDImageUrls.add(userInfoA.getIdentityCardFrontImgUrl());
            mUploadImageLy.setTag(mIDImageUrls);

            LocationInfo locationInfo = new LocationInfo();
            locationInfo.setAddress(userInfoA.getLocationAddress());
            locationInfo.setLatitude(Double.parseDouble(userInfoA.getLatitude()));
            locationInfo.setLongitude(Double.parseDouble(userInfoA.getLongitude()));

            mUserOrderAreaEdit.setTag(locationInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void saveOperation() {
        if (!Utils.isNetworkAvailable(context)) {
            Utils.showShortToast(context, getString(R.string.common_no_network_message));
            return;
        }

        //验证协议是否checked
        if (mProtocolCheckBox.isChecked() == false) {
            Utils.showShortToast(context, "必须同意《接单须知》才能继续！");
            return;
        }

        //验证表单合法性sd
        if (mHeadImage.getTag() == null) {
            Utils.showShortToast(context, "请选择您的头像！");
            return;
        }

        if (mCityEditTv.getTag() == null) {
            Utils.showShortToast(context, "请选择接单城市");
            return;
        }

        if (!Utils.isNotEmpty(mUserNameTv.getText().toString())) {
            Utils.showShortToast(context, "请输入您的姓名！");
            return;
        }

        if (TextUtils.isEmpty(mUserWorkYearsEdit.getText().toString())) {
            Utils.showShortToast(context, "请输入工作年限！");
            return;
        }

        if (!Utils.isNotEmpty(mUserIDEdit.getText().toString())) {
            Utils.showShortToast(context, "请输入您的身份证号！");
            return;
        }
        if (mUserOrderAreaEdit.getTag() == null) {
            Utils.showShortToast(context, "请选择您的服务范围！");
            return;
        }
        if (mUploadImageLy.getTag() == null) {
            Utils.showShortToast(context, "请按要求上传您的身份证照片！");
            return;
        }

        if (mUserWorkTypesEdit.getTag() == null) {
            Utils.showShortToast(context, "请选择您的维修领域！");
            return;
        }

        if (mPwdLy.getTag() == null) {
            Utils.showShortToast(context, "你必须自己设置密码！");
            return;
        }

        //verify id card
        String idCard = mUserIDEdit.getText().toString().trim();

        if (idCard.length() != 18) {
            Utils.showShortToast(context, "身份证必须是18位");
            return;
        }
        /*String resultIDCard = IDCardValidate.verify(idCard);
        if (!resultIDCard.equalsIgnoreCase(idCard)) {
            Utils.showShortToast(context, resultIDCard);
            return;
        }*/

        // todo 工作年限校验
        mWaitingDialog.show("正在提交注册信息，请稍候...", false);

        RegisterApi httpService = KHttpWorker.ins().createHttpService(LoginApi.URL, RegisterApi.class);

        Map params = new HashMap();

        params.put("token", ConfigManager.getInstance(this).getUserSession());
        params.put("userCode", ConfigManager.getInstance(this).getUserCode());
        params.put("password", mPwd);
        params.put("avatarUrl", mImageAddress);
        params.put("name", mUserNameTv.getText().toString());
        params.put("identityCardNo", idCard);
        params.put("regionId", String.valueOf(mCityEditTv.getTag()));
        params.put("workingYear", mUserWorkYearsEdit.getText().toString());
        params.put("serviceIds", mUserWorkTypesEdit.getTag().toString());

        ArrayList<String> imgs = (ArrayList<String>) mUploadImageLy.getTag();

        if (imgs.size() >= 2) {
            params.put("identityCardFrontImgUrl", imgs.get(0));
            params.put("identityCardBehindImgUrl", imgs.get(1));
        }

        LocationInfo info = (LocationInfo) mUserOrderAreaEdit.getTag();

        if (info != null) {
            params.put("locationAddress", info.getAddress());
            params.put("latitude", info.getLatitude());
            params.put("longitude", info.getLongitude());
        }

        retrofit2.Call<JsonObject> registerRequest = httpService.getRegister(params);

        registerRequest.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if (mWaitingDialog != null) {
                    mWaitingDialog.dismiss();
                }

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (object != null && code == 0) {
                        Intent intent = new Intent();
                        intent.putExtra("workyears", mUserWorkYearsEdit.getText().toString());
                        ConfigManager.getInstance(context).setUserName(mUserNameTv.getText().toString());
                        setResult(RESULT_OK, intent);
                        Utils.finishWithoutAnim(ApplyActivity.this);
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showShortToast(context, msg + ":" + code);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {

                if (mWaitingDialog != null) {
                    mWaitingDialog.dismiss();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showShortToast(context, "提交注册信息失败，请重试！");
                    }
                });

            }
        });
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

                            mImageAddress = info.get("avatarUrl").getAsString();
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

                            final UserInfoA userInfoA = new UserInfoA(mImageAddress, identityCardBehindImgUrl, identityCardFrontImgUrl,
                                    identityCardNo, latitued, longitude, locationAddress, name, regionId, regionName, workYear);

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


    @Override
    protected String getPageName() {
        return TAG;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

}

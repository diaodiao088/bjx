package com.bjxapp.worker.ui.view.activity.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bjxapp.worker.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XCircleImageView;
import com.bjxapp.worker.controls.XEditText;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.LocationInfo;
import com.bjxapp.worker.model.UserApplyInfo;
import com.bjxapp.worker.ui.view.activity.ChangeCityActivity;
import com.bjxapp.worker.ui.view.activity.MapSelectActivity;
import com.bjxapp.worker.ui.view.activity.PublicImagesActivity;
import com.bjxapp.worker.ui.view.activity.WebViewActivity;
import com.bjxapp.worker.ui.view.activity.map.MapActivityNew;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.utils.BitmapUtils;
import com.bjxapp.worker.utils.IDCardValidate;
import com.bjxapp.worker.utils.SDCardUtils;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager.DataType;
import com.bjxapp.worker.utils.image.BitmapManager;
import com.bjxapp.worker.utils.image.PictureUploadUtils;

import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ApplyActivity extends BaseActivity implements OnClickListener {
    protected static final String TAG = "注册界面";

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
                ChangePwdActivity.goToActivityForResult(this);
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

        Intent intent = new Intent();
        intent.setClass(this, MapActivityNew.class);
        startActivity(intent);



        /*if (mUserOrderAreaEdit.getTag() != null) {
            LocationInfo locationInfo = (LocationInfo) mUserOrderAreaEdit.getTag();
            Utils.startMapSelectActivity(context, MapSelectActivity.class, locationInfo.getLatitude(), locationInfo.getLongitude(), locationInfo.getAddress(), locationInfo.getCity());
        } else {
            Utils.startMapSelectActivity(context, MapSelectActivity.class, Constant.USER_LOCATION_LATITUDE, Constant.USER_LOCATION_LONGITUDE, Constant.USER_LOCATION_ADDRESS, Constant.USER_LOCATION_CITY);
        }*/
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
                        locationInfo.setLatitude(data.getDoubleExtra("latitude", 0.0));
                        locationInfo.setLongitude(data.getDoubleExtra("longitude", 0.0));
                        locationInfo.setAddress(data.getStringExtra("address"));
                        locationInfo.setCity(data.getStringExtra("city"));
                        mUserOrderAreaEdit.setTag(locationInfo);
                        mUserOrderAreaEdit.setText(data.getStringExtra("address"));
                    }
                    break;
                case Constant.CONSULT_ID_IMAGES:
                    if (resultCode == RESULT_OK && data != null) {
                        mIDImageUrls = data.getStringArrayListExtra("result");
                        mUploadImageLy.setTag(new Object());
                        // displayIDImages();
                    }
                    break;
                case Constant.CONSULT_WORK_CITY:
                    if (resultCode == RESULT_OK && data != null) {
                        // TODO: 2018/9/18
                        String city = data.getStringExtra("city");
                        if (!TextUtils.isEmpty(city)) {
                            mCityEditTv.setText(city);
                            mCityEditTv.setTag(new Object());
                        }
                    }
                    break;
                case Constant.CONSULT_SETTING_PWD:
                    if (resultCode == RESULT_OK && data != null) {
                        mPwdLy.setTag(new Object());
                    }
                    break;
            }
        } catch (IOException e) {
            Utils.showShortToast(context, "选择图片失败！");
        }
    }

    private AsyncTask<String, Void, String> mHeadImageTask;

    private void uploadHeadImage(String filename) {
        mWaitingDialog.show("正在上传头像，请稍候...", false);

        mHeadImageTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String uploadUrl = APIConstants.IMAGE_HEAD_UPLOAD_URL;
                String result = PictureUploadUtils.uploadImage(uploadUrl, params[0], context, Constant.UPLOAD_URL_SERVER_DIR_USER);

                if (isCancelled()) {
                    return "";
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                mWaitingDialog.dismiss();

                if (Utils.isNotEmpty(result)) {
                    Utils.showShortToast(context, "头像上传成功！");
                    mHeadImage.setTag(result);
                    ConfigManager.getInstance(context).setUserHeadImageUrl(result);
                } else {
                    mHeadImage.setTag(null);
                    Utils.showShortToast(context, "头像上传失败，请重新选择头像！");
                }
            }
        };

        mHeadImageTask.execute(filename);
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

        //验证表单合法性
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
        String resultIDCard = IDCardValidate.verify(idCard);
        if (!resultIDCard.equalsIgnoreCase(idCard)) {
            Utils.showShortToast(context, resultIDCard);
            return;
        }

        //save user infomation
        final UserApplyInfo applyInfo = new UserApplyInfo();
        applyInfo.setHeadImageUrl(mHeadImage.getTag().toString());
        applyInfo.setPersonName(mUserNameTv.getText().toString().trim());
        applyInfo.setCardNo(mUserIDEdit.getText().toString().trim());

        LocationInfo locationInfo = (LocationInfo) mUserOrderAreaEdit.getTag();
        applyInfo.setCity(locationInfo.getCity());
        applyInfo.setAddress(locationInfo.getAddress());
        applyInfo.setLatitude(locationInfo.getLatitude());
        applyInfo.setLongitude(locationInfo.getLongitude());

        applyInfo.setWorkYear(Integer.parseInt(mUserWorkYearsEdit.getTag().toString()));
        applyInfo.setServiceSubIDs(mUserWorkTypesEdit.getTag().toString());

        mWaitingDialog.show("正在提交注册信息，请稍候...", false);
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                return LogicFactory.getAccountLogic(context).saveRegisterInfo(applyInfo);
            }

            @Override
            protected void onPostExecute(Integer result) {
                mWaitingDialog.dismiss();
                if (result == APIConstants.RESULT_CODE_SUCCESS) {
                    Intent intent = new Intent();
                    intent.putExtra("workyears", applyInfo.getWorkYear());
                    ConfigManager.getInstance(context).setUserName(applyInfo.getPersonName());
                    setResult(RESULT_OK, intent);
                    Utils.finishWithoutAnim(ApplyActivity.this);
                } else {
                    Utils.showShortToast(context, "提交注册信息失败，请重试！");
                }
            }

        }.execute();
    }

    private AsyncTask<String, Void, UserApplyInfo> mLoadDataTask;

    private void loadData() {
        mLoadDataTask = new AsyncTask<String, Void, UserApplyInfo>() {
            @Override
            protected UserApplyInfo doInBackground(String... params) {
                return LogicFactory.getAccountLogic(context).getRegisterInfo();
            }

            @Override
            protected void onPostExecute(UserApplyInfo result) {
                if (result == null) {
                    return;
                }

                mHeadImage.setTag(result.getHeadImageUrl());
                ConfigManager.getInstance(context).setUserHeadImageUrl(result.getHeadImageUrl());
                mUserNameTv.setText(result.getPersonName());
                mUserIDEdit.setText(result.getCardNo());

                LocationInfo locationInfo = new LocationInfo();
                locationInfo.setLatitude(result.getLatitude());
                locationInfo.setLongitude(result.getLongitude());
                locationInfo.setAddress(result.getAddress());
                locationInfo.setCity(result.getCity());
                mUserOrderAreaEdit.setTag(locationInfo);
                mUserOrderAreaEdit.setText(result.getAddress());

                mIDImageUrls = new ArrayList<String>();
                mIDImageUrls.add(result.getCardFrontImageUrl());
                mIDImageUrls.add(result.getCardBehindImageUrl());

                mUserWorkYearsEdit.setText(result.getWorkYear() + "年");
                mUserWorkYearsEdit.setTag(result.getWorkYear());
                mUserWorkTypesEdit.setText(result.getServiceSubNames());
                mUserWorkTypesEdit.setTag(result.getServiceSubIDs());

                displayIDImages();
            }
        };
        mLoadDataTask.execute();
    }

    @Override
    protected String getPageName() {
        return TAG;
    }

    @Override
    public void onDestroy() {
        try {
            if (mLoadDataTask != null) {
                mLoadDataTask.cancel(true);
            }
        } catch (Exception e) {
        }

        super.onDestroy();
    }

}

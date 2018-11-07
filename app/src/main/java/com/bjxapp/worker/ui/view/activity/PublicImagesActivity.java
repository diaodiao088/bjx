package com.bjxapp.worker.ui.view.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjxapp.worker.adapter.ImagesAdapter;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.controls.XButton;
import com.bjxapp.worker.controls.XImageView;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.controls.listview.XListView;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.logic.LogicFactory;
import com.bjxapp.worker.model.ImageInfo;
import com.bjxapp.worker.ui.view.activity.user.ApplyActivity;
import com.bjxapp.worker.ui.view.base.BaseActivity;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.utils.FileUtils;
import com.bjxapp.worker.utils.ImagePathUtils;
import com.bjxapp.worker.utils.SDCardUtils;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager.DataType;
import com.bjxapp.worker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class PublicImagesActivity extends BaseActivity implements OnClickListener {
    protected static final String TAG = "照片界面";
    private XTextView mTitleTextView;
    private XImageView mBackImageView;
    private XImageView mAddImageView;
    private XButton mUploadButton;

    private ArrayList<ImageInfo> mImagesArray = new ArrayList<ImageInfo>();
    private ImagesAdapter mImagesAdapter;
    private XListView mXListView;
    private TextView mSmallTv;
    public LinearLayout mTipLy;

    private Uri mPhotoUri;
    private ArrayList<String> mImagesURL = new ArrayList<>();
    private ArrayList<String> mUploadedFilenames = null;
    private ArrayList<String> mDeletedFilenames = null;
    private int mOperationFlag = 0;
    private String mTitle = "";
    private int mCount = 2;
    private int mType = 1;

    private XWaitingDialog mWaitingDialog;

    @BindView(R.id.tips)
    LinearLayout tipLy;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_images);
        handleIntent();
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);

    }

    private String flag_type = "";

    private void handleIntent(){

        Intent intent = getIntent();

        if (intent != null){

            flag_type = intent.getStringExtra("operation_flag");
        }
    }

    @Override
    protected void initControl() {
        mWaitingDialog = new XWaitingDialog(context);

        mTitleTextView = (XTextView) findViewById(R.id.title_text_tv);
        mTitle = getIntent().getStringExtra("title");
        if (Utils.isNotEmpty(mTitle)) {
            mTitleTextView.setText(mTitle);
        } else {
            mTitleTextView.setText("上传照片");
        }

        mCount = getIntent().getIntExtra("count", 2);
        mType = getIntent().getIntExtra("type", 1);

        mUploadButton = (XButton) findViewById(R.id.images_button_upload);
        mAddImageView = (XImageView) findViewById(R.id.title_image_right);
        mAddImageView.setVisibility(View.GONE);
        mSmallTv = findViewById(R.id.title_right_small_tv);
        mSmallTv.setVisibility(View.VISIBLE);
        mTipLy = findViewById(R.id.tip_bg);
        calTipLy();
        mAddImageView.setImageResource(R.drawable.icon_menu_add);

        mOperationFlag = Integer.parseInt(getIntent().getStringExtra("operation_flag"));
        if (mOperationFlag == 2) {
            mUploadButton.setVisibility(View.GONE);
            mAddImageView.setVisibility(View.GONE);
        }
        mImagesURL = getIntent().getStringArrayListExtra("urls");

        mBackImageView = (XImageView) findViewById(R.id.title_image_back);
        mBackImageView.setVisibility(View.VISIBLE);

        mXListView = (XListView) findViewById(R.id.images_upload_listview);
        mImagesAdapter = new ImagesAdapter(context, mImagesArray);
        mXListView.setAdapter(mImagesAdapter);
        mXListView.setCacheColorHint(Color.TRANSPARENT);
        mXListView.setPullRefreshEnable(false);
        mXListView.setPullLoadEnable(false);
        mXListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageInfo imageInfo = (ImageInfo) mXListView.getItemAtPosition(position);
                File file = new File(imageInfo.getUrl());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "image/*");
                startActivity(intent);
            }
        });

        mXListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (mOperationFlag == 2) {
                    return true;
                }

                Dialog alertDialog = new AlertDialog.Builder(PublicImagesActivity.this).
                        setTitle("移除图片").
                        setMessage("是否要移除此图片？").
                        setPositiveButton("移除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int index = position - 1;
                                addDeletedFilename(mImagesArray.get(index).getUrl());
                                mImagesAdapter.deleteImage(index);
                            }
                        }).
                        setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).
                        create();
                alertDialog.show();

                return true;
            }
        });

        if ("2".equals(flag_type)){
            tipLy.setVisibility(View.GONE);
            mXListView.setOnItemLongClickListener(null);
            mUploadButton.setVisibility(View.GONE);
            mSmallTv.setVisibility(View.GONE);
        }

    }

    private int imageHeight;

    private void calTipLy() {
        mTipLy.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                int height = mTipLy.getHeight();

                imageHeight = (height - DimenUtils.dp2px(10, PublicImagesActivity.this)) / 2;

                mTipLy.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    public int getImageHeight() {
        return imageHeight;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        if (mImagesURL == null) return;

        for (int i = 0; i < mImagesURL.size(); i++) {
            addImageToList(mImagesURL.get(i), 1);
        }
    }

    @Override
    protected void setListener() {
        mBackImageView.setOnClickListener(this);
        mAddImageView.setOnClickListener(this);
        mUploadButton.setOnClickListener(this);
        mSmallTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_image_back:
                //Utils.finishActivity(PublicImagesActivity.this);
                onBackPressed();
                break;
            case R.id.title_image_right:
            case R.id.title_right_small_tv:
                showSelectImageDialog();
                break;
            case R.id.images_button_upload:
                uploadImages();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if (mImagesArray.size() < mCount) {
            Utils.showShortToast(context, "请至少上传两张照片！");
        } else {
            super.onBackPressed();
        }
    }

    private void showSelectImageDialog() {
        if (mImagesAdapter.getCount() >= mCount) {
            Utils.showShortToast(context, "只能上传两张照片！");
            return;
        }

        final CharSequence[] items = getResources().getStringArray(R.array.user_select_image_items);
        AlertDialog dlg = new AlertDialog.Builder(context)
                .setTitle("选择图片")
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
                                    //todo:小米此方法不行，暂不处理
                                    ContentValues contentValues = new ContentValues();
                                    mPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
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
                        String imagePath = Utils.getFilePathFromIntentData(originalUri, context);
                        addImageToList(imagePath, 0);
                    }
                    break;
                case Constant.REQUEST_CODE_CLOCK_TAKE_PHOTO:
                    if (resultCode == Activity.RESULT_OK) {
                        String photoPath = Utils.getPhotoUrl(context, mPhotoUri, data);
                        if (Utils.isNotEmpty(photoPath)) {
                            addImageToList(photoPath, 0);
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            Utils.showShortToast(context, "选择图片失败！");
        }
    }

    private void addImageToList(String imagePath, int flag) {
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setUrl(imagePath);
        imageInfo.setFlag(flag);
        mImagesAdapter.addImage(imageInfo);
        mTipLy.setVisibility(View.GONE);
        if (!"2".equals(flag_type)){
            mUploadButton.setVisibility(View.VISIBLE);
        }
    }

    private void addDeletedFilename(String filePath) {
        if (mDeletedFilenames == null) {
            mDeletedFilenames = new ArrayList<String>();
        }
        mDeletedFilenames.add(FileUtils.getImgNameWithImageExt(filePath));
    }

    private void uploadImages() {
        if (!Utils.isNetworkAvailable(context)) {
            Utils.showShortToast(context, getString(R.string.common_no_network_message));
            return;
        }

        if (mImagesArray.size() == mCount) {
            mUploadedFilenames = new ArrayList<String>();
            mUploadedFilenames.clear();
        } else {
            Utils.showShortToast(context, "需要" + mCount + "张图片才能保存！");
            return;
        }
        mWaitingDialog.show(getString(R.string.images_upload_waiting_message), false);

        post_file(LoginApi.URL + "/image/upload", null);
    }

    private ArrayList<String> mImageUrl = new ArrayList<>();

    public void post_file(final String url, final Map<String, Object> map) {

        if (mImagesArray == null || mImagesArray.size() == 0) {
            return;
        }

        OkHttpClient client = new OkHttpClient();

        for (int i = 0; i < mImagesArray.size(); i++) {
            MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);

            ImageInfo imageInfo = mImagesArray.get(i);

            File file = new File(imageInfo.getUrl());

            if (!file.exists()) {
                return;
            }

            if (file != null) {
                RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
                requestBody.addFormDataPart("files", file.getName(), body);
            }

            if (map != null) {
                for (Map.Entry entry : map.entrySet()) {
                    requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
                }
            }
            Request request = new Request.Builder().url(url).post(requestBody.build()).tag(PublicImagesActivity.this).build();
            // readTimeout("请求超时时间" , 时间单位);
            client.newBuilder().readTimeout(5000 * 10, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {

                    PublicImagesActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (mWaitingDialog != null) {
                                mWaitingDialog.dismiss();
                            }

                            Utils.showShortToast(context, "头像上传失败，请重新选择头像！:" + e.getLocalizedMessage());
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (mImageUrl.size() >= 2 && mWaitingDialog != null) {
                        mWaitingDialog.dismiss();
                    }

                    if (response.isSuccessful()) {
                        String str = response.body().string();
                        try {
                            JSONObject object = new JSONObject(str);

                            JSONArray accessAddress = object.getJSONArray("list");

                            String img = accessAddress.get(0).toString();

                            final String msg = object.get("msg").toString();
                            final int code = (int) object.get("code");

                            if (TextUtils.isEmpty(img)) {

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utils.showShortToast(context, "头像上传失败，请重新选择头像！: code: " + code + "msg: " + msg);
                                    }
                                });
                            } else {

                                mImageUrl.add(img);

                                if (mImageUrl.size() >= 2) {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Utils.showShortToast(context, "图片保存成功！");
                                            Intent intent = new Intent();
                                            intent.putStringArrayListExtra("result", mImageUrl);
                                            setResult(RESULT_OK, intent);
                                            Utils.finishWithoutAnim(PublicImagesActivity.this);
                                        }
                                    });
                                }
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


    }

    @Override
    protected String getPageName() {
        return TAG;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImagesAdapter != null) {
            mImagesAdapter.clearCache();
        }
    }
}

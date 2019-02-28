package com.bjxapp.worker.ui.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bjx.master.R;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.controls.XWaitingDialog;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.ui.view.activity.adapter.FragileAdapter;
import com.bjxapp.worker.ui.view.activity.bean.FragileBean;
import com.bjxapp.worker.ui.view.activity.order.CompressUtil;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.utils.SDCardUtils;
import com.bjxapp.worker.utils.UploadFile;
import com.bjxapp.worker.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.bjxapp.worker.global.Constant.REQUEST_CODE_CLOCK_TAKE_PHOTO;
import static java.lang.String.valueOf;

public class FragileActivity extends Activity {

    @BindView(R.id.title_right_small_tv)
    TextView mTitleRightTv;

    @OnClick(R.id.title_right_small_tv)
    void onClickSmallTv() {
        FragileBean fragileBean = new FragileBean();
        FragileBean.ImageBean bean = fragileBean.new ImageBean(FragileBean.ImageBean.TYPE_IMAGE, "");
        fragileBean.getImageList().add(bean);
        mList.add(fragileBean);
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @BindView(R.id.fragile_recycler_view)
    RecyclerView mRecyclerView;


    @OnClick(R.id.add_confirm_btn)
    void onClickConfirm() {
        savePic();
    }

    public static final int REQUEST_CODE_RESULT = 0x04;

    public static final String TYPE_LIST = "type_list";

    private LinearLayoutManager mLayoutManager;

    private XWaitingDialog waitingDialog;

    private FragileAdapter mAdapter;

    private ArrayList<FragileBean> mList = new ArrayList<>();

    private static final int MAX_PIC_COUNT = 5;
    private static final int FEEDBACK_LOAD_IMAGES_RESULT = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragle_add_activity);
        ButterKnife.bind(this);
        initView();
        handleIntent();
        initData();

    }

    private void handleIntent() {

        Intent intent = getIntent();

        if (intent != null) {
            // mList = intent.getParcelableArrayListExtra(TYPE_LIST);
        }

        if (mList == null) {
            mList = new ArrayList<>();
        }

    }

    private void initView() {
        mTitleTextView.setText("易损件");
        mTitleRightTv.setVisibility(View.VISIBLE);
        mTitleRightTv.setText("添加");

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        waitingDialog = new XWaitingDialog(this);

    }

    private int currentPos;

    String name;

    private static final String PATH = Environment
            .getExternalStorageDirectory() + "/DCIM";

    protected void loadImages() {

        final CharSequence[] items = getResources().getStringArray(R.array.user_select_image_items);
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle("选取照片")
                .setNegativeButton("取消", null)
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        try {
                            if (!SDCardUtils.exist()) {
                                Utils.showShortToast(FragileActivity.this, "SD卡被占用或不存在");
                            } else {
                                if (item == 0) {
                                    Uri imageURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                    if (imageURI != null) {
                                        try {
                                            if (ContextCompat.checkSelfPermission(FragileActivity.this,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                                                    != PackageManager.PERMISSION_GRANTED) {
                                                ActivityCompat.requestPermissions(FragileActivity.this,
                                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                                            } else {
                                                Intent intent = new Intent(Intent.ACTION_PICK, imageURI);
                                                startActivityForResult(intent, FEEDBACK_LOAD_IMAGES_RESULT);
                                            }
                                        } catch (Exception e) {
                                            Log.w("FeedbackPresenter", "loadImages: " + e.getMessage());
                                        }
                                    }
                                } else {
                                    if (ContextCompat.checkSelfPermission(FragileActivity.this,
                                            Manifest.permission.READ_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(FragileActivity.this,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(FragileActivity.this,
                                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                                    } else {
                                        //  Intent intent = new Intent(Intent.ACTION_PICK, imageURI);
                                        //   startActivityForResult(intent, FEEDBACK_LOAD_IMAGES_RESULT);
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用系统相机
                                        name = android.text.format.DateFormat.format("yyyyMMdd_hhmmss",
                                                Calendar.getInstance(Locale.CHINA))
                                                + ".jpg";//以当前时间作为文件名
                                        Uri imageUri = Uri.fromFile(new File(PATH, name));

                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                                        startActivityForResult(intent, REQUEST_CODE_CLOCK_TAKE_PHOTO);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Utils.showShortToast(FragileActivity.this, "SD卡被占用或不存在");
                        }
                    }
                }).create();
        dlg.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FEEDBACK_LOAD_IMAGES_RESULT) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = null;
                try {
                    cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        final String imagePath = cursor.getString(columnIndex);
                        //根据手机屏幕设置图片宽度
                        Bitmap bitmap = UploadFile.createImageThumbnail(imagePath, getScreenShotWidth(), true);
                        if (bitmap != null) {
                            insertImg(bitmap, imagePath, true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //KLog.error(KLog.KLogFeature.toulan,"load image failed, msg:"+e.getMessage());
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        } else if (requestCode == REQUEST_CODE_CLOCK_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {

                try {
                    String filePath = PATH + "/" + name;
                    Bitmap bitmap = UploadFile.createImageThumbnail(filePath, getScreenShotWidth(), true);
                    if (bitmap != null) {
                        insertImg(bitmap, filePath, true);
                    }
                } catch (Exception e) {

                }
            }
        }
    }

    public void insertImg(Bitmap bitmap, final String imagePath,
                          boolean showDelImg) {

        FragileBean bean = mAdapter.getSpecFragBean(currentPos);

        FragileBean.ImageBean imageBean = bean.new ImageBean(FragileBean.ImageBean.TYPE_ADD, imagePath);
        bean.getImageList().add(0, imageBean);
        mAdapter.notifyDataSetChanged();

    }

    private int getScreenShotWidth() {
        int screenWidth = DimenUtils.getScreenWidth(this);
        screenWidth = screenWidth - (int) (screenWidth * 1.0 / 9); // 除掉边框
        return screenWidth / 4;
    }

    private void initData() {

        mAdapter = new FragileAdapter();
        mAdapter.setItems(mList);
        mAdapter.setListener(new FragileAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(int position) {
                mList.remove(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void addImage(int position) {
                currentPos = position;
                loadImages();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(15));

    }

    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        int mSpace;

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = mSpace;
        }

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }
    }

    public static void gotoActivity(Activity context) {
        Intent intent = new Intent();
        intent.setClass(context, FragileActivity.class);

        context.startActivityForResult(intent, REQUEST_CODE_RESULT);
    }

    private void savePic() {
        if (mList.size() <= 0) {
            Toast.makeText(this, "请添加易损件", Toast.LENGTH_SHORT).show();
        }

        for (int i = 0; i < mList.size(); i++) {
            if (TextUtils.isEmpty(mList.get(i).getFragileName())) {
                Toast.makeText(this, "请输入易损件名称", Toast.LENGTH_SHORT).show();
            }
        }

        savePicReal(0);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private void saveUrls() {
        for (int i = 0; i < mList.size(); i++) {
            FragileBean fragileBean = mList.get(i);

            for (int j = 0; j < fragileBean.getImageList().size(); j++) {
                FragileBean.ImageBean imageBean = fragileBean.getImageList().get(j);
                if (imageBean.getType() == FragileBean.ImageBean.TYPE_ADD) {
                    fragileBean.getUrls().add(imageBean.getUrl());
                }
            }
        }
    }

    private void savePicReal(final int index) {

        if (index > mList.size() - 1) {
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent();
            saveUrls();
            intent.putParcelableArrayListExtra(TYPE_LIST, mList);
            setResult(RESULT_OK, intent);

            finish();
            return;
        }

        ArrayList<FragileBean.ImageBean> mImageList = mList.get(index).getImageList();

        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);

        Map<String, String> map = new HashMap<>();
        map.put("userCode", ConfigManager.getInstance(this).getUserCode());
        map.put("token", ConfigManager.getInstance(this).getUserSession());

        boolean isFileValid = false;

        for (int i = 0; i < mImageList.size(); i++) {

            FragileBean.ImageBean item = mImageList.get(i);

            if (item.type == FragileBean.ImageBean.TYPE_ADD) {
                File file = new File(item.getUrl());

                if (!file.exists()) {
                    break;
                }

                String targetPath = getCacheDir() + file.getName();

                final String compressImage = CompressUtil.compressImage(item.getUrl(), targetPath, 30);

                final File compressFile = new File(compressImage);

                if (compressFile.exists()) {
                    isFileValid = true;
                    RequestBody body = RequestBody.create(MediaType.parse("image/*"), compressFile);
                    requestBody.addFormDataPart("files", compressFile.getName(), body);
                }
            }
        }

        if (!isFileValid) {
            savePicReal(index + 1);
            return;
        }

        for (Map.Entry entry : map.entrySet()) {
            requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
        }

        Request request = new Request.Builder().url(LoginApi.URL + "/image/upload").post(requestBody.build()).tag(FragileActivity.this).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000 * 100, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, final IOException e) {

                FragileActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (waitingDialog != null) {
                            waitingDialog.dismiss();
                        }

                        Utils.showShortToast(FragileActivity.this, "图片上传失败！:" + e.getLocalizedMessage());
                    }
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (waitingDialog != null) {
                            waitingDialog.dismiss();
                        }
                    }
                });

                if (response.isSuccessful()) {
                    String str = response.body().string();

                    final ArrayList<String> list = new ArrayList<>();
                    try {
                        JSONObject object = new JSONObject(str);
                        JSONArray accessAddress = object.getJSONArray("list");

                        FragileBean fragileBean = mList.get(index);

                        for (int i = 0; i < accessAddress.length(); i++) {
                            list.add(accessAddress.get(i).toString());

                            for (int j = 0; j < fragileBean.getImageList().size(); j++) {
                                if (!fragileBean.getImageList().get(j).getUrl().startsWith("http") && fragileBean.getImageList().get(j).getType() != FragileBean.ImageBean.TYPE_IMAGE) {
                                    fragileBean.getImageList().get(j).setUrl(accessAddress.get(i).toString());
                                    break;
                                }
                            }

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    savePicReal(index + 1);
                                }
                            });

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Utils.showShortToast(FragileActivity.this, "图片上传失败！");
                        }
                    });
                }
            }
        });
    }


}

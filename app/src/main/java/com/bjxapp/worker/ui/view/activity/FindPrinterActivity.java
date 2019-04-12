package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.PrintItemLayout;
import com.dothantech.lpapi.LPAPI;
import com.dothantech.printer.IDzPrinter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindPrinterActivity extends Activity {

    @BindView(R.id.find_printer_des_tv)
    TextView mDesTv;

    @BindView(R.id.find_printer_btn)
    TextView mSearchTv;

    @OnClick(R.id.find_printer_btn)
    void refreshPrint() {
        updateListLayout(mPrinterAddress);
    }

    @BindView(R.id.printer_list_ly)
    LinearLayout mPrintListLy;

    @BindView(R.id.title_text_tv)
    XTextView mTitleTv;

    @OnClick(R.id.title_image_back)
    void onClickBack() {
        onBackPressed();
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private IDzPrinter.PrinterAddress mPrinterAddress;

    public static final String TYPE_TITLE = "type_title";
    public static final String TYPE_NUM = "type_num";

    private String mTitle;
    private String mTypeNum;

    private LPAPI api;

    // 保存各种信息时的名称
    private static final String KeyPrintQuality = "PrintQuality";
    private static final String KeyPrintDensity = "PrintDensity";
    private static final String KeyPrintSpeed = "PrintSpeed";
    private static final String KeyGapType = "GapType";

    public static final String KeyLastPrinterMac = "LastPrinterMac";
    public static final String KeyLastPrinterName = "LastPrinterName";
    public static final String KeyLastPrinterType = "LastPrinterType";

    private static final String KeyDefaultText1 = "DefaultText1";
    private static final String KeyDefaultText2 = "DefaultText2";
    private static final String KeyDefault1dBarcode = "Default1dBarcode";
    private static final String KeyDefault2dBarcode = "Default2dBarcode";

    // 需要用到的各个控件对象
    private Button btnConnectDevice = null;
    private Button btnPrintQuality = null;
    private Button btnPrintDensity = null;
    private Button btnPrintSpeed = null;
    private Button btnGapType = null;
    private EditText et1 = null;
    private EditText et2 = null;

    // 打印参数
    private int printQuality = -1;
    private int printDensity = -1;
    private int printSpeed = -1;
    private int gapType = -1;

    // 打印数据
    private String defaultText1 = "";
    private String defaultText2 = "";
    private String default1dBarcode = "";
    private String default2dBarcode = "";

    // 用于填充的数组及集合列表
    private String[] printQualityList = null;
    private String[] printDensityList = null;
    private String[] printSpeedList = null;
    private String[] gapTypeList = null;

    private boolean isPrinted = false;

    private List<IDzPrinter.PrinterAddress> pairedPrinters = new ArrayList<IDzPrinter.PrinterAddress>();

    private List<Bitmap> printBitmaps = new ArrayList<Bitmap>();
    private int[] bitmapOrientations = null;


    // LPAPI 打印机操作相关的回调函数。
    private final LPAPI.Callback mCallback = new LPAPI.Callback() {

        /****************************************************************************************************************************************/
        // 所有回调函数都是在打印线程中被调用，因此如果需要刷新界面，需要发送消息给界面主线程，以避免互斥等繁琐操作。

        /****************************************************************************************************************************************/

        // 打印机连接状态发生变化时被调用
        @Override
        public void onStateChange(IDzPrinter.PrinterAddress arg0, IDzPrinter.PrinterState arg1) {
            final IDzPrinter.PrinterAddress printer = arg0;
            switch (arg1) {
                case Connected:
                case Connected2:
                    // 打印机连接成功，发送通知，刷新界面提示
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPrinterConnected(printer);
                        }
                    });
                    break;

                case Disconnected:
                    // 打印机连接失败、断开连接，发送通知，刷新界面提示
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPrinterDisconnected();
                        }
                    });
                    break;

                default:
                    break;
            }
        }

        // 蓝牙适配器状态发生变化时被调用
        @Override
        public void onProgressInfo(IDzPrinter.ProgressInfo arg0, Object arg1) {
        }

        @Override
        public void onPrinterDiscovery(IDzPrinter.PrinterAddress arg0, IDzPrinter.PrinterInfo arg1) {
        }

        // 打印标签的进度发生变化是被调用
        @Override
        public void onPrintProgress(IDzPrinter.PrinterAddress address, Object bitmapData, IDzPrinter.PrintProgress progress, Object addiInfo) {
            switch (progress) {
                case Success:
                    // 打印标签成功，发送通知，刷新界面提示
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPrintSuccess();
                        }
                    });
                    break;

                case Failed:
                    // 打印标签失败，发送通知，刷新界面提示
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPrintFailed();
                        }
                    });
                    break;

                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_find_printer_ly);
        ButterKnife.bind(this);
        handleIntent();
        initView();
        // 调用LPAPI对象的init方法初始化对象
        this.api = LPAPI.Factory.createInstance(mCallback);

        // 尝试连接上次成功连接的打印机
        if (mPrinterAddress != null) {
            if (api.openPrinterByAddress(mPrinterAddress)) {
                // 连接打印机的请求提交成功，刷新界面提示
                onPrinterConnecting(mPrinterAddress, false);
                return;
            }
        }

        searchAvailableDev();
    }

    private void handleIntent() {
        mTitle = getIntent().getStringExtra(TYPE_TITLE);
        mTypeNum = getIntent().getStringExtra(TYPE_NUM);
    }


    private void searchAvailableDev() {

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toast.makeText(FindPrinterActivity.this, "当前设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!btAdapter.isEnabled()) {
            Toast.makeText(FindPrinterActivity.this, "蓝牙适配器未打开", Toast.LENGTH_SHORT).show();
            return;
        }

        updateListLayout(null);
    }


    private void updateListLayout(IDzPrinter.PrinterAddress printerAddress) {

        pairedPrinters = api.getAllPrinterAddresses(null);

        if (pairedPrinters.size() > 0) {
            mPrintListLy.setVisibility(View.VISIBLE);
            mPrintListLy.removeAllViews();

            for (int i = 0; i < pairedPrinters.size(); i++) {

                IDzPrinter.PrinterAddress item = pairedPrinters.get(i);

                if (item == null || item.equals(printerAddress)) {
                    break;
                }

                PrintItemLayout itemLayout = new PrintItemLayout(this);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        DimenUtils.dp2px(45, this));

                layoutParams.setMargins(0, DimenUtils.dp2px(10, this), 0, 0);
                itemLayout.bindData(pairedPrinters.get(i));

                itemLayout.setOnBlueClickListener(new PrintItemLayout.OnBlueClickListener() {
                    @Override
                    public void tryToConnectPrinter(IDzPrinter.PrinterAddress printerAddress) {

                        if (printerAddress != null) {
                            // 连接选择的打印机
                            if (api.openPrinterByAddress(printerAddress)) {
                                // 连接打印机的请求提交成功，刷新界面提示
                                onPrinterConnecting(printerAddress, true);
                                return;
                            }
                        }

                        // 连接打印机失败，刷新界面提示
                        onPrinterDisconnected();
                    }
                });

                mPrintListLy.addView(itemLayout, layoutParams);
            }

        } else {
            mPrintListLy.setVisibility(View.GONE);
        }

    }


    private void initView() {
        mTitleTv.setText("连接打印设备");

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        String lastPrinterMac = sharedPreferences.getString(KeyLastPrinterMac, null);
        String lastPrinterName = sharedPreferences.getString(KeyLastPrinterName, null);
        String lastPrinterType = sharedPreferences.getString(KeyLastPrinterType, null);
        IDzPrinter.AddressType lastAddressType = TextUtils.isEmpty(lastPrinterType) ? null : Enum.valueOf(IDzPrinter.AddressType.class, lastPrinterType);
        if (lastPrinterMac == null || lastPrinterName == null || lastAddressType == null) {
            mPrinterAddress = null;
        } else {
            mPrinterAddress = new IDzPrinter.PrinterAddress(lastPrinterName, lastPrinterMac, lastAddressType);
        }
        printQuality = sharedPreferences.getInt(KeyPrintQuality, -1);
        printDensity = sharedPreferences.getInt(KeyPrintDensity, -1);
        printSpeed = sharedPreferences.getInt(KeyPrintSpeed, -1);
        gapType = sharedPreferences.getInt(KeyGapType, -1);
    }


    // 标签打印成功时操作
    private void onPrintSuccess() {
        // 标签打印成功时，刷新界面提示
        Toast.makeText(FindPrinterActivity.this, "标签打印成功", Toast.LENGTH_SHORT).show();
    }

    // 打印请求失败或标签打印失败时操作
    private void onPrintFailed() {
        Toast.makeText(FindPrinterActivity.this, "标签打印失败", Toast.LENGTH_SHORT).show();
    }

    // 连接打印机操作提交失败、打印机连接失败或连接断开时操作
    private void onPrinterDisconnected() {
        // 连接打印机操作提交失败、打印机连接失败或连接断开时，刷新界面提示
        Toast.makeText(FindPrinterActivity.this, "连接打印机失败！", Toast.LENGTH_SHORT).show();
        mDesTv.setText("无连接打印设备");
    }


    // 连接打印机成功时操作
    private void onPrinterConnected(IDzPrinter.PrinterAddress printer) {
        // 连接打印机成功时，刷新界面提示，保存相关信息
        Toast.makeText(FindPrinterActivity.this, "连接打印机成功", Toast.LENGTH_SHORT).show();
        mPrinterAddress = printer;
        // 调用LPAPI对象的getPrinterInfo方法获得当前连接的打印机信息
        String txt = "打印机：";
        txt += api.getPrinterInfo().deviceName + "\n";
        txt += api.getPrinterInfo().deviceAddress;
        mDesTv.setText(txt);

        if (!isPrinted) {
            printText1DBarcode(mTitle, mTypeNum, null);
        }

        updateListLayout(printer);

    }


    // 连接打印机请求成功提交时操作
    private void onPrinterConnecting(IDzPrinter.PrinterAddress printer, boolean showDialog) {
        // 连接打印机请求成功提交，刷新界面提示
        String txt = printer.shownName;
        if (TextUtils.isEmpty(txt))
            txt = printer.macAddress;
        txt = "正在连接" + '[' + txt + ']';
        txt += "打印机";
//        if (showDialog) {
//            showStateAlertDialog(txt);
//        }

        mDesTv.setText("正在连接打印机");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        api.quit();
        fini();
    }

    private void fini() {

        // 保存相关信息
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KeyPrintQuality, printQuality);
        editor.putInt(KeyPrintDensity, printDensity);
        editor.putInt(KeyPrintSpeed, printSpeed);
        editor.putInt(KeyGapType, gapType);
        if (mPrinterAddress != null) {
            editor.putString(KeyLastPrinterMac, mPrinterAddress.macAddress);
            editor.putString(KeyLastPrinterName, mPrinterAddress.shownName);
            editor.putString(KeyLastPrinterType, mPrinterAddress.addressType.toString());
        }
        if (defaultText1 != null) {
            editor.putString(KeyDefaultText1, defaultText1);
        }
        if (defaultText2 != null) {
            editor.putString(KeyDefaultText2, defaultText2);
        }
        if (default1dBarcode != null) {
            editor.putString(KeyDefault1dBarcode, default1dBarcode);
        }
        if (default2dBarcode != null) {
            editor.putString(KeyDefault2dBarcode, default2dBarcode);
        }
        editor.commit();

    }

    // 打印文本一维码
    private boolean printText1DBarcode(String text, String onedBarcde, Bundle param) {

        isPrinted = true;

        // 开始绘图任务，传入参数(页面宽度, 页面高度)
        api.startJob(48, 48, 0);

        // api.setItemOrientation(180);

        // 开始一个页面的绘制，绘制文本字符串
        // 传入参数(需要绘制的文本字符串, 绘制的文本框左上角水平位置, 绘制的文本框左上角垂直位置, 绘制的文本框水平宽度, 绘制的文本框垂直高度, 文字大小, 字体风格)
        api.drawText(text, 15, 4, 40, 20, 4);

        // 设置之后绘制的对象内容旋转180度
        //api.setItemOrientation(180);

        // 绘制一维码，此一维码绘制时内容会旋转180度，sdf
        // 传入参数(需要绘制的一维码的数据, 绘制的一维码左上角水平位置, 绘制的一维码左上角垂直位置, 绘制的一维码水平宽度, 绘制的一维码垂直高度)
        api.draw1DBarcode(onedBarcde, LPAPI.BarcodeType.AUTO, 4, 11, 40, 15, 3);

        // 结束绘图任务提交打印
        return api.commitJob();
    }

}

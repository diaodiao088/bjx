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

    private LPAPI api;

    // 保存各种信息时的名称
    private static final String KeyPrintQuality = "PrintQuality";
    private static final String KeyPrintDensity = "PrintDensity";
    private static final String KeyPrintSpeed = "PrintSpeed";
    private static final String KeyGapType = "GapType";

    private static final String KeyLastPrinterMac = "LastPrinterMac";
    private static final String KeyLastPrinterName = "LastPrinterName";
    private static final String KeyLastPrinterType = "LastPrinterType";

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

        pairedPrinters = api.getAllPrinterAddresses(null);

        updateListLayout();
    }


    private void updateListLayout() {

        if (pairedPrinters.size() > 0) {
            mPrintListLy.setVisibility(View.VISIBLE);
            mPrintListLy.removeAllViews();

            for (int i = 0; i < pairedPrinters.size(); i++) {
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

}

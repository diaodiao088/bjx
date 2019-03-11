package com.bjxapp.worker.ui.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.bjx.master.R;
import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.model.DateTime;
import com.bjxapp.worker.model.ReceiveButton;
import com.bjxapp.worker.ui.view.activity.CheckMainActivity;
import com.bjxapp.worker.ui.view.activity.RecordActivity;
import com.bjxapp.worker.ui.view.activity.RepairActivity;
import com.bjxapp.worker.ui.view.base.BaseFragment;

import java.util.List;

public class Fragment_main_first_new extends BaseFragment implements View.OnClickListener {

    protected static final String TAG = "首页";

    private CardView mRepairView, mCheckView, mRecordView, mMaintainView;

    @Override
    protected void initView() {
        initViews();
        initializeReceiveButton(null);
    }

    @Override
    protected void finish() {

    }

    @Override
    protected int onCreateContent() {
        return R.layout.fragment_main_first_new;
    }

    @Override
    public void refresh(int enterType) {

    }

    private void initViews() {

        mRepairView = (CardView) findViewById(R.id.service_entrance_ly);
        mCheckView = (CardView) findViewById(R.id.check_entrance_ly);
        mRecordView = (CardView) findViewById(R.id.record_entrance_ly);
        mMaintainView = (CardView) findViewById(R.id.maintain_entrance_ly);

        mRepairView.setOnClickListener(this);
        mCheckView.setOnClickListener(this);
        mRecordView.setOnClickListener(this);
        mMaintainView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.check_entrance_ly:
                CheckMainActivity.goToActivity(getActivity());
            case R.id.maintain_entrance_ly:
                Toast.makeText(getActivity(), "巡检/保养业务即将上线，敬请期待~", Toast.LENGTH_SHORT).show();
                break;

            case R.id.service_entrance_ly:
                RepairActivity.gotoActivity(getActivity());
                break;
            case R.id.record_entrance_ly:
                RecordActivity.gotoActivity(getActivity());
                break;

            default:
                break;
        }
    }

    private void initializeReceiveButton(List<ReceiveButton> buttons) {
        String today = DateTime.getTodayDateTimeString();
        String tomorrow = DateTime.getTomorrowDateTimeString();

        ReceiveButton receiveButton = new ReceiveButton();
        receiveButton.setDate(today);
        receiveButton.setType(0);
        receiveButton.setFlag(0);
        if (buttons != null && buttons.size() > 0) {
            for (ReceiveButton button : buttons) {
                if (button.getDate().equals(today) && button.getType() == 0) {
                    receiveButton.setFlag(1);
                }
            }
        }

        receiveButton = new ReceiveButton();
        receiveButton.setDate(tomorrow);
        receiveButton.setType(0);
        receiveButton.setFlag(0);
        if (buttons != null && buttons.size() > 0) {
            for (ReceiveButton button : buttons) {
                if (button.getDate().equals(tomorrow) && button.getType() == 0) {
                    receiveButton.setFlag(1);
                }
            }
        }

        receiveButton = new ReceiveButton();
        receiveButton.setDate(today);
        receiveButton.setType(1);
        receiveButton.setFlag(0);
        if (buttons != null && buttons.size() > 0) {
            for (ReceiveButton button : buttons) {
                if (button.getDate().equals(today) && button.getType() == 1) {
                    receiveButton.setFlag(1);
                }
            }
        }

        receiveButton = new ReceiveButton();
        receiveButton.setDate(tomorrow);
        receiveButton.setType(1);
        receiveButton.setFlag(0);
        if (buttons != null && buttons.size() > 0) {
            for (ReceiveButton button : buttons) {
                if (button.getDate().equals(tomorrow) && button.getType() == 1) {
                    receiveButton.setFlag(1);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {
                case Constant.ACTIVITY_ORDER_DETAIL_RESULT_CODE:
                    if (resultCode == Activity.RESULT_OK) {
                        // onFirstLoadData(true);
                    }
                    break;
            }
        } catch (Exception e) {

        }
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

package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.model.PlanBean;
import com.bjxapp.worker.ui.view.activity.order.OrderDetailActivity;
import com.bjxapp.worker.ui.view.activity.order.OrderDetailActivityNew;
import com.bjxapp.worker.ui.widget.DimenUtils;
import com.bjxapp.worker.ui.widget.MaintainCallItemLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryContactActivity extends Activity {

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @OnClick(R.id.title_image_back)
    void onBack() {
        finish();
    }

    @BindView(R.id.container)
    LinearLayout mContainer;

    public static final String TYPE_LIST = "plan_list";

    ArrayList<PlanBean> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_contact_activity);
        ButterKnife.bind(this);

        mTitleTextView.setText("历史维修方案");

        list = OrderDetailActivityNew.planList_static;

        if (list != null && list.size() > 0) {

           // list.remove(0);
            addUi();
        }

    }

    private void addUi() {

        for (int i = 0; i < list.size(); i++) {
            PlanBean planBean = list.get(i);

            if (planBean != null){
                MaintainCallItemLayout mXieTiaoLayout = new MaintainCallItemLayout(this);

                mXieTiaoLayout.bindData(null, planBean, "");
                mXieTiaoLayout.makeUnvisible();

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(0, DimenUtils.dp2px(5, this), 0, 0);

                mContainer.addView(mXieTiaoLayout, layoutParams);
            }

        }
    }


    public static void goToActivity(Activity act, ArrayList<PlanBean> list) {

        Intent intent = new Intent();

        intent.putParcelableArrayListExtra(TYPE_LIST, list);

        intent.setClass(act, HistoryContactActivity.class);

        act.startActivity(intent);
    }


}

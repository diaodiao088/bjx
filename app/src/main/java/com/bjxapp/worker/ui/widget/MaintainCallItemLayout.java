package com.bjxapp.worker.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bjx.master.R;
import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.apinew.EnterpriseApi;
import com.bjxapp.worker.apinew.LoginApi;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.http.httpcore.KHttpWorker;
import com.bjxapp.worker.model.CommentBean;
import com.bjxapp.worker.model.MainTainBean;
import com.bjxapp.worker.model.OtherPriceBean;
import com.bjxapp.worker.model.PlanBean;
import com.bjxapp.worker.ui.view.activity.ImageListActivity;
import com.bjxapp.worker.ui.view.activity.MaintainActivity;
import com.bjxapp.worker.ui.view.activity.order.OrderDetailActivityNew;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaintainCallItemLayout extends LinearLayout {

    private View mRootView;

    private String orderId;

    @BindView(R.id.issue_reason_tv)
    XTextView mReasonTv;

    @BindView(R.id.strategy_content_tv)
    XTextView mNextTimeTv;

    @BindView(R.id.guzhang_content_tv)
    XTextView mGuZhangTv;

    @BindView(R.id.modify_content_tv)
    XTextView mModifyTv;

    @BindView(R.id.price_content)
    TextView mPriceTv;

    @BindView(R.id.bottom_comit)
    LinearLayout mBottomLy;

    @BindView(R.id.main_container_ly)
    LinearLayout mPriceContainerLy;

    @BindView(R.id.edit_text)
    EditText mEditTv;

    @BindView(R.id.contact_record_ly)
    LinearLayout mContactRecordLy;

    @BindView(R.id.status_iv)
    ImageView mStatusIv;

    @BindView(R.id.other_price_ly)
    LinearLayout otherPriceLy;

    @OnClick(R.id.photo_look_tv)
    void onCliKPhoto() {
        ImageListActivity.goToActivity(getContext(), planBean.getmPlanImgList(), planBean.getmResultImgList());
    }

    @OnClick(R.id.commit_tv)
    void commit() {

        if (TextUtils.isEmpty(mEditTv.getText().toString())) {
            return;
        }

        EnterpriseApi enterpriseApi = KHttpWorker.ins().createHttpService(LoginApi.URL, EnterpriseApi.class);

        Call<JsonObject> call = null;

        Map<String, String> params = new HashMap<>();
        params.put("token", ConfigManager.getInstance(getContext()).getUserSession());
        params.put("userCode", ConfigManager.getInstance(getContext()).getUserCode());
        params.put("orderId", orderId);
        params.put("maintainPlanId", String.valueOf(planBean.getId()));
        params.put("content", mEditTv.getText().toString());

        call = enterpriseApi.commentPlan(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == APIConstants.RESULT_CODE_SUCCESS) {
                    final JsonObject object = response.body();

                    final String msg = object.get("msg").getAsString();
                    final int code = object.get("code").getAsInt();

                    if (code == 0) {

                        mReasonTv.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "提交成功", Toast.LENGTH_SHORT).show();
                                mEditTv.setText("");
                                if (actIns != null) {
                                    actIns.loadData(false);
                                }
                            }
                        });
                    } else {
                        mReasonTv.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), msg + ":" + code, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                mReasonTv.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "提交失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    private PlanBean planBean;

    public MaintainCallItemLayout(Context context) {
        super(context);
        init();
    }

    public MaintainCallItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaintainCallItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.maintain_xietiao_layout, this);

        ButterKnife.bind(this);

    }

    OrderDetailActivityNew actIns;

    public void makeUnvisible() {
        mBottomLy.setVisibility(GONE);
    }

    public void bindData(OrderDetailActivityNew act, PlanBean planBean, String orderId) {

        actIns = act;

        this.planBean = planBean;
        this.orderId = orderId;

        mGuZhangTv.setText(planBean.getFault());
        mModifyTv.setText(planBean.getPlan());
        mPriceTv.setText(planBean.getTotalCost());

        if (planBean.getStatus() == 0) {
            mReasonTv.setVisibility(GONE);
            mNextTimeTv.setVisibility(GONE);
            mContactRecordLy.setVisibility(GONE);
            mBottomLy.setVisibility(GONE);

            mStatusIv.setImageResource(R.drawable.pass);


        } else if (planBean.getStatus() == 3) {
            mReasonTv.setVisibility(VISIBLE);
            mNextTimeTv.setVisibility(VISIBLE);

            mReasonTv.setText(planBean.getCoordinateReason());
            try {
                mNextTimeTv.setText(getFormatTime(Long.parseLong(planBean.getCoordinateNextHandleStartTime())));
            } catch (Exception e) {

            }

            mStatusIv.setImageResource(R.drawable.pass_ing);

        } else if (planBean.getStatus() == 9) {
            mStatusIv.setImageResource(R.drawable.no_pass);
            mReasonTv.setVisibility(VISIBLE);
            mNextTimeTv.setVisibility(VISIBLE);

            mReasonTv.setText(planBean.getCoordinateReason());
            try {
                mNextTimeTv.setText(getFormatTime(Long.parseLong(planBean.getCoordinateNextHandleStartTime())));
            } catch (Exception e) {

            }
        } else if (planBean.getStatus() == 6) {
            mStatusIv.setImageResource(R.drawable.pass);
            mReasonTv.setVisibility(VISIBLE);
            mNextTimeTv.setVisibility(VISIBLE);

            mReasonTv.setText(planBean.getCoordinateReason());
            try {
                mNextTimeTv.setText(getFormatTime(Long.parseLong(planBean.getCoordinateNextHandleStartTime())));
            } catch (Exception e) {

            }
        }

        addPriceList();

     //   addOtherPriceList();

        addCommentList();

    }


    private void addOtherPriceList() {

        ArrayList<OtherPriceBean> list = planBean.getmOtherPriceList();

        otherPriceLy.removeAllViews();

        for (int i = 0; i < list.size(); i++) {
            OtherPriceUILayout peijianUILayout = new OtherPriceUILayout(getContext());
            peijianUILayout.bindData(list.get(i));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(0, DimenUtils.dp2px(10, getContext()), 0, 0);

            otherPriceLy.addView(peijianUILayout, layoutParams);
        }

    }


    private void addPriceList() {

        ArrayList<MainTainBean> list = planBean.getmMaintainList();

        mPriceContainerLy.removeAllViews();

        for (int i = 0; i < list.size(); i++) {

            PeijianUILayout peijianUILayout = new PeijianUILayout(getContext());
            peijianUILayout.bindData(list.get(i));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(0, DimenUtils.dp2px(10, getContext()), 0, 0);

            mPriceContainerLy.addView(peijianUILayout, layoutParams);

        }
    }


    private void addCommentList() {

        ArrayList<CommentBean> list = planBean.getmCommentList();

        mContactRecordLy.removeAllViews();

        for (int i = 0; i < list.size(); i++) {

            CommentBean item = list.get(i);

            if (item.getApplicationType() == 3) {
                RecordILayout leftLayout = new RecordILayout(getContext());
                leftLayout.bindData(list.get(i));

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        , ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(0, DimenUtils.dp2px(10, getContext()), 0, 0);

                mContactRecordLy.addView(leftLayout, 0, layoutParams);

            } else {
                RecordRLayout rightLayout = new RecordRLayout(getContext());
                rightLayout.bindData(list.get(i));

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        , ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(0, DimenUtils.dp2px(10, getContext()), 0, 0);

                mContactRecordLy.addView(rightLayout, 0, layoutParams);


            }

        }
    }


    private String getFormatTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/ HH:mm");
        java.util.Date dt = new Date(time);
        String sDateTime = sdf.format(dt);  //得到精确到秒的表示：08/31/2006 21:08:00
        return sDateTime;
    }


    public interface OnOperationListener {

        void onDelete(MainTainBean mainTainBean);

        void onPriceChange();

        void onCountChange();

    }

    public OnOperationListener listener;

    public void setOperationListener(OnOperationListener listener) {
        this.listener = listener;
    }


}

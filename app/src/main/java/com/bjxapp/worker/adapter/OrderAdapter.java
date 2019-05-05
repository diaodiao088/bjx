package com.bjxapp.worker.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.model.OrderDes;

import java.util.ArrayList;

public class OrderAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<OrderDes> aInfo;

    public OrderAdapter(Context context, ArrayList<OrderDes> info) {
        mInflater = LayoutInflater.from(context);
        aInfo = info;
    }

    public void setReceiverInfo(ArrayList<OrderDes> list) {
        if (list == null) {
            return;
        }
        this.aInfo = list;
    }

    public int getCount() {
        return aInfo.size();
    }

    public Object getItem(int position) {
        return aInfo.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_order_item, null);
            holder = new ViewHolder();
            holder.textViewService = (XTextView) convertView.findViewById(R.id.order_receive_textview_service);
            holder.textViewStatus = (XTextView) convertView.findViewById(R.id.order_receive_textview_status);
            holder.textViewOrderDate = (XTextView) convertView.findViewById(R.id.order_receive_textview_orderdate);
            holder.textViewAddress = (XTextView) convertView.findViewById(R.id.order_receive_textview_address);
            holder.textViewMoney = (XTextView) convertView.findViewById(R.id.order_receive_textview_money);
            holder.mOutTimeIv = convertView.findViewById(R.id.out_time_iv);
            holder.emergencyTv = convertView.findViewById(R.id.emergency_tv);
            holder.mShopLy = convertView.findViewById(R.id.shop_ly);
            holder.shopTv = convertView.findViewById(R.id.shop_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textViewService.setText(aInfo.get(position).getServiceName());

        holder.textViewAddress.setText(aInfo.get(position).getLocationAddress());

        String statusString = "";
        String feeInfo = "";

        boolean isOutTime = false;

        int status = aInfo.get(position).getStatus();

        int type = aInfo.get(position).getBillType();

        if (status == 4) {
            statusString = "异常";
            feeInfo = "费用";
        } else {
            switch (aInfo.get(position).getProcessStatus()) {
                case 5:
                    statusString = "待支付";
                    feeInfo = "费用：";
                    break;
                case 1:
                    statusString = "新订单";
                    String selectMasterTime = aInfo.get(position).getmSelectTime();

                    try {
                        double time = Double.parseDouble(selectMasterTime);
                        if (System.currentTimeMillis() - time > 30 * 60 * 1000) {
                            isOutTime = true;
                        }
                    } catch (Exception e) {

                    }

                    feeInfo = "费用：";
                    break;
                case 2:
                    statusString = "待联系";

                    String orderTime = aInfo.get(position).getSelectMasterTime();

                    try {
                        double time = Double.parseDouble(orderTime);
                        if (System.currentTimeMillis() - time > 30 * 60 * 1000) {
                            isOutTime = true;
                        }
                    } catch (Exception e) {

                    }

                    feeInfo = "费用：";
                    break;
                case 3:
                    statusString = "待上门";
                    feeInfo = "费用：";
                    break;
                case 4:
                    statusString = "已上门";
                    feeInfo = "费用：";
                    break;
                case 6:
                    statusString = "待评价";
                    feeInfo = "费用：";
                    break;
                case 7:
                    statusString = "已评价";
                    feeInfo = "费用：";
                    break;
                default:
                    break;
            }
        }

        if ("待支付".equals(statusString) || "待评价".equals(statusString) || "已评价".equals(statusString)) {
            String money = aInfo.get(position).getPayAmount();
            if (TextUtils.isEmpty(money)) {
                holder.textViewMoney.setText(feeInfo + aInfo.get(position).getServiceVisitCost() + "元");
            } else {
                holder.textViewMoney.setText(feeInfo + aInfo.get(position).getPayAmount() + "元");
            }

        } else {
            holder.textViewMoney.setText(feeInfo + aInfo.get(position).getServiceVisitCost() + "元");
        }

        if (type == 1) {
            holder.textViewOrderDate.setText("立即上门 ：" + aInfo.get(position).getAppointmentDay()
                    + " " + aInfo.get(position).getAppointmentEndTime());
        } else {
            holder.textViewOrderDate.setText(aInfo.get(position).getAppointmentDay() + " " + aInfo.get(position).getAppointmentEndTime());
        }

        if (type == 1) {

            if (aInfo.get(position).getBusinessType() != 1) {
                holder.emergencyTv.setVisibility(View.VISIBLE);
            } else {
                holder.emergencyTv.setVisibility(View.GONE);
            }

        } else {
            holder.emergencyTv.setVisibility(View.GONE);
        }

        // 显示门店
        if (aInfo.get(position).getBusinessType() == 1 && aInfo.get(position).getProcessStatus() < 6) {
            holder.mShopLy.setVisibility(View.VISIBLE);

            holder.shopTv.setText(aInfo.get(position).getmEnterpriseName()
                    + aInfo.get(position).getmShopName());

        } else {
            holder.mShopLy.setVisibility(View.GONE);
        }

        holder.textViewStatus.setBackgroundResource(R.drawable.layout_textview_radius);
        holder.textViewStatus.setText(statusString);

        if (isOutTime) {
            holder.mOutTimeIv.setVisibility(View.VISIBLE);
            holder.mOutTimeIv.setImageResource(R.drawable.out_time);
        } else {
            holder.mOutTimeIv.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder {
        XTextView textViewService;
        XTextView textViewStatus;
        XTextView textViewOrderDate;
        XTextView textViewAddress;
        XTextView textViewMoney;
        XTextView shopTv;
        ImageView mOutTimeIv;
        TextView emergencyTv;
        RelativeLayout mShopLy;
    }
}

package com.bjxapp.worker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bjx.master.R;
import com.bjxapp.worker.controls.XTextView;
import com.bjxapp.worker.model.OrderDes;

import java.util.ArrayList;

;

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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textViewService.setText(aInfo.get(position).getServiceName());
        holder.textViewOrderDate.setText(aInfo.get(position).getAppointmentDay() + " " + aInfo.get(position).getAppointmentEndTime());
        holder.textViewAddress.setText(aInfo.get(position).getLocationAddress());

        String statusString = "";
        String feeInfo = "";

        boolean isOutTime = false;

        int status = aInfo.get(position).getStatus();

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

                    feeInfo = "费用预估：";
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

        holder.textViewMoney.setText(feeInfo + aInfo.get(position).getServiceVisitCost() + "元");
        holder.textViewStatus.setBackgroundResource(R.drawable.layout_textview_radius);
        holder.textViewStatus.setText(statusString);

        if (isOutTime){

        }

        return convertView;
    }

    class ViewHolder {
        XTextView textViewService;
        XTextView textViewStatus;
        XTextView textViewOrderDate;
        XTextView textViewAddress;
        XTextView textViewMoney;
    }
}

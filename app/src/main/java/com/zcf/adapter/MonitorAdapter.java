package com.zcf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.zcf.R;
import com.zcf.bean.MonitorItem;
import com.zcf.util.StockUtils;

import java.util.List;

public class MonitorAdapter extends BaseAdapter {
    private Context context;
    private List<MonitorItem> data;

    public MonitorAdapter(Context context) {
        this.context = context;
    }

    public MonitorAdapter(Context context, List<MonitorItem> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<MonitorItem> getData() {
        return data;
    }

    public void setData(List<MonitorItem> data) {
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_monitor, parent, false);
            viewHolder.tv_stockName = convertView.findViewById(R.id.item_monitor_stock_name);
            viewHolder.tv_icon = convertView.findViewById(R.id.item_monitor_icon);
            viewHolder.tv_stockNum = convertView.findViewById(R.id.item_monitor_stock_num);
            viewHolder.tv_nowPrice = convertView.findViewById(R.id.item_monitor_now_price);
            viewHolder.tv_percentDiff = convertView.findViewById(R.id.item_monitor_percent_diff);
            viewHolder.tv_diffPrice = convertView.findViewById(R.id.item_monitor_diff_price);
            viewHolder.tv_remind = convertView.findViewById(R.id.item_monitor_remind);
            viewHolder.sw_monitor = convertView.findViewById(R.id.item_monitor_switch);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //UI赋值
        MonitorItem monitorItem = data.get(position);
        viewHolder.tv_stockName.setText(monitorItem.getStockName());
        viewHolder.tv_stockNum.setText(monitorItem.getStockNum());
        String stockType = StockUtils.getStockType(monitorItem.getStockNum());
        viewHolder.tv_icon.setText(stockType);
        viewHolder.tv_nowPrice.setText(monitorItem.getNowPrice());
        viewHolder.tv_percentDiff.setText(monitorItem.getPercentDiff());
        viewHolder.tv_diffPrice.setText(monitorItem.getDiffPrice());
        viewHolder.tv_remind.setText(monitorItem.getRemind());
        if (monitorItem.getNowPriceColor() != null) {
            viewHolder.tv_nowPrice.setTextColor(context.getResources().getColor(monitorItem.getNowPriceColor()));
        }
        if (monitorItem.getPercentDiffColor() != null) {
            viewHolder.tv_percentDiff.setTextColor(context.getResources().getColor(monitorItem.getPercentDiffColor()));
        }
        if (monitorItem.getDiffPriceColor() != null) {
            viewHolder.tv_diffPrice.setTextColor(context.getResources().getColor(monitorItem.getDiffPriceColor()));
        }
        if (monitorItem.getRemindColor() != null) {
            viewHolder.tv_remind.setTextColor(context.getResources().getColor(monitorItem.getRemindColor()));
        }

        Boolean isMonitor = monitorItem.getIsMonitor();
        if (isMonitor != null) {
            viewHolder.sw_monitor.setChecked(isMonitor);
        }

        return convertView;
    }

    private final class ViewHolder {
        TextView tv_stockName;
        TextView tv_icon;
        TextView tv_stockNum;
        TextView tv_nowPrice;
        TextView tv_percentDiff;
        TextView tv_diffPrice;
        TextView tv_remind;
        Switch sw_monitor;
    }

}

package com.zcf;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.zcf.activity.SearchActivity;
import com.zcf.activity.StockMonitorActivity;
import com.zcf.adapter.MonitorAdapter;
import com.zcf.base.BaseActivity;
import com.zcf.bean.Monitor;
import com.zcf.bean.MonitorItem;
import com.zcf.bean.Stock;
import com.zcf.dao.StockDao;
import com.zcf.service.StockMonitorService;
import com.zcf.util.StockUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private ServiceReceiver serviceReceiver;
    private AddMonitorReceiver addMonitorReceiver;

    private MonitorAdapter monitorAdapter = new MonitorAdapter(this);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTitleBar();
        initMonitorListView();
        startStockMonitorService();
        registServiceBroadcast();

    }

    /**
     * 注册广播
     */
    private void registServiceBroadcast() {
        serviceReceiver = new ServiceReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("superstock.service");
        registerReceiver(serviceReceiver, intentFilter);

        addMonitorReceiver = new AddMonitorReceiver();
        IntentFilter intentFilter_addMonitor = new IntentFilter();
        intentFilter_addMonitor.addAction("superstock.addStockMonitorTask");
        registerReceiver(addMonitorReceiver, intentFilter_addMonitor);

    }

    /**
     * 启动StockMonitor服务
     */
    private void startStockMonitorService() {
        Intent intent = new Intent(MainActivity.this, StockMonitorService.class);
        startService(intent);
    }

    /**
     * 初始化Monitor ListView
     */
    private void initMonitorListView() {
        final List<MonitorItem> monitorItemList = getAllMonitorItem();
        ListView listView = findViewById(R.id.main_listview);
        monitorAdapter.setData(monitorItemList);
        listView.setAdapter(monitorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, StockMonitorActivity.class);
                MonitorItem monitorItem = monitorItemList.get(position);
                intent.putExtra("stockNum", monitorItem.getStockNum());
                intent.putExtra("stockPrefix", monitorItem.getStockPrefix());
                intent.putExtra("isMonitor", monitorItem.getIsMonitor());
                startActivity(intent);

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

    }

    /**
     * 获取所有MonitorItem
     * @return 所有MonitorItem
     */
    private List<MonitorItem> getAllMonitorItem() {
        List<Monitor> monitorList = StockDao.getAllMonitor(this);
        List<MonitorItem> monitorItemList = new ArrayList<>();
        for (Monitor monitor : monitorList) {
            MonitorItem monitorItem = getMonitorItem(monitor);

            monitorItemList.add(monitorItem);
        }

        return monitorItemList;
    }

    /**
     * 获取MonitorItem
     * @param monitor monitor
     * @return MonitorItem
     */
    private MonitorItem getMonitorItem(Monitor monitor) {
        MonitorItem monitorItem = new MonitorItem();
        monitorItem.setStockName(monitor.getStockName());
        monitorItem.setStockNum(monitor.getStockNum());
        monitorItem.setStockPrefix(monitor.getStockPrefix());
        monitorItem.setIsMonitor(monitor.getisMonitor());

        return monitorItem;
    }

    /**
     * 初始化 title bar
     */
    private void initTitleBar() {
        View titleBar = findViewById(R.id.main_titlebar);
        ImageButton titleBarRightButton = titleBar.findViewById(R.id.titlebar_right_button);
        titleBarRightButton.setImageResource(R.drawable.ic_search);
        titleBarRightButton.setVisibility(View.VISIBLE);

        // 搜索
        titleBarRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (serviceReceiver != null) {
            unregisterReceiver(serviceReceiver);
        }
        if (addMonitorReceiver != null) {
            unregisterReceiver(addMonitorReceiver);
        }
        super.onDestroy();
    }

    private class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().startsWith("superstock.service")) {
                // 收到service的消息
                byte[] byteArrayExtra = intent.getByteArrayExtra("stock");
                ByteArrayInputStream bis = new ByteArrayInputStream(byteArrayExtra);

                Stock stock = null;
                try {
                    ObjectInputStream ois= new ObjectInputStream(bis);
                    stock = (Stock) ois.readObject();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if (stock == null) {
                    return;
                }
                // 更新UI
                updateUI(stock);

            }
        }
    }

    /**
     * 更新UI
     * @param stock stock
     */
    private void updateUI(Stock stock) {
        List<MonitorItem> monitorItemList = monitorAdapter.getData();
        for (MonitorItem monitorItem : monitorItemList) {
            if (stock.getStockNum().equals(monitorItem.getStockNum())) {
                monitorItem.setNowPrice(StockUtils.formatPrice(stock.getNowPrice()));
                monitorItem.setPercentDiff(StockUtils.formatPrice(stock.getPercentDiff()) + "%");
                monitorItem.setDiffPrice(StockUtils.formatPrice(stock.getPriceDiff()));
                monitorItem.setNowPriceColor(StockUtils.getStockColor(stock.getNowPrice(), stock.getYestodayPrice()));
                monitorItem.setPercentDiffColor(StockUtils.getStockColor(stock.getPercentDiff(), 0));
                monitorItem.setDiffPriceColor(StockUtils.getStockColor(stock.getPriceDiff(), 0));
                break;
            }
        }
        monitorAdapter.notifyDataSetChanged();

    }

    private class AddMonitorReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("superstock.addStockMonitorTask")) {
                // 收到service的消息
                String stockNum = intent.getStringExtra("stockNum");
                String stockPrefix = intent.getStringExtra("stockPrefix");
                String stockName = intent.getStringExtra("stockName");

                Monitor monitor = new Monitor();
                monitor.setStockNum(stockNum);
                monitor.setStockPrefix(stockPrefix);
                monitor.setStockName(stockName);
                monitor.setisMonitor(true);

                // 更新listview数据
                List<MonitorItem> data = monitorAdapter.getData();
                MonitorItem monitorItem = getMonitorItem(monitor);
                if (data.contains(monitorItem)) {
                    return;
                }

                data.add(monitorItem);
                monitorAdapter.notifyDataSetChanged();

            }

        }
    }

}

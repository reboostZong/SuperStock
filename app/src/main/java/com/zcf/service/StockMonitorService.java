package com.zcf.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.zcf.MainActivity;
import com.zcf.R;
import com.zcf.bean.Monitor;
import com.zcf.bean.Stock;
import com.zcf.constant.SuperStockConstant;
import com.zcf.dao.StockDao;
import com.zcf.util.MathUtils;
import com.zcf.util.StockUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StockMonitorService extends Service {
    private volatile boolean isRunning = true;

    private TaskReceiver taskReceiver;
    private ConnectionPool connectionPool = new ConnectionPool(1, 30, TimeUnit.SECONDS);
    private OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectionPool(connectionPool).build();

    private ArrayBlockingQueue<Monitor> queue = new ArrayBlockingQueue<>(10000);

    /**
     * 用于记录股票线程情况
     */
    private static List<String> stockThreadList = new ArrayList<>();

    /**
     * 买手数均值
     */
    private static Integer buyNumAvg = null;

    /**
     * 卖手数均值
     */
    private static Integer sellNumAvg = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * onCreate第一次启动执行
     */
    @Override
    public void onCreate() {
        super.onCreate();
        registServiceBroadcast();

    }

    /**
     * 注册广播
     */
    private void registServiceBroadcast() {
        taskReceiver = new TaskReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("superstock.addStockMonitorTask");
        registerReceiver(taskReceiver, intentFilter);

    }

    /**
     * onStartCommand每一次启动执行
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 执行任务
        doTask();
        // 设置为前台进程，提高进程优先级，提高存活机率
        setForeground();

        return START_STICKY;
    }

    /**
     * 后台执行任务
     */
    private void doTask() {
        // 获取所以的monitor, 并开启生产者线程
        List<Monitor> monitorList = StockDao.getAllMonitor(this);
        for (Monitor monitor : monitorList) {
            createStockThread(monitor);
        }

        // 开启消费者线程
        Thread consumerTask = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        doStockMonitorTask();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        consumerTask.start();

    }

    /**
     * 创建股票线程
     * @param monitor monitor
     */
    private void createStockThread(Monitor monitor) {
        // 已存在股票线程
        if (!monitor.getisMonitor() || stockThreadList.contains(monitor.getStockNum())) {
            return;
        }

        // 创建新股票线程
        Thread thread= new Thread(new StockTask(monitor), monitor.getStockNum());
        thread.start();

        stockThreadList.add(monitor.getStockNum());
    }

    /**
     * 后台任务
     */
    private class StockTask implements Runnable {
        Monitor monitor;

        public StockTask(Monitor monitor) {
            this.monitor = monitor;
        }

        @Override
        public void run() {
            while (isRunning) {
                // 将请求交给队列
                queue.offer(monitor);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 股票监控任务
     */
    private void doStockMonitorTask() throws InterruptedException {
        Monitor monitor = queue.take();
        final String stockNum = monitor.getStockNum();
        final String stockPrefix = monitor.getStockPrefix();
        String url = SuperStockConstant.STOCK_MAIN_URL + stockPrefix + stockNum;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("[HttpFailed]", "[SearchActivity][asyncHttpRequest] failed to http request.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(call, response, stockNum, stockPrefix);
            }
        });

        // 睡眠
        Thread.sleep(1000 / stockThreadList.size());
    }

    /**
     * 处理response信息并发消息给主线程更新UI
     * @param call call
     * @param response response
     */
    private void handleResponse(Call call, Response response, CharSequence stockNum, String stockPrefix) throws IOException {
        String responseMsg = response.body().string();
        Pattern pattern = Pattern.compile(SuperStockConstant.HTTP_RESPONSE_CONTENT);
        Matcher matcher = pattern.matcher(responseMsg);
        String result = null;
        if (matcher.find()) {
            result = matcher.group().replaceAll("\"", "");
        }
        Log.d("[HttpResponse]", result);

        if (result == null || result.trim().length() == 0) {
            return;
        }

        // 获取了股票信息实体
        Stock stock = StockUtils.getStock(result, String.valueOf(stockNum), stockPrefix);

        // 发送广播
        Intent intent = new Intent();
        ByteArrayOutputStream bos= new ByteArrayOutputStream();
        ObjectOutputStream oos= new ObjectOutputStream(bos);
        oos.writeObject(stock);
        intent.putExtra("stock", bos.toByteArray());
        intent.setAction("superstock.service."+stockNum);
        sendBroadcast(intent);
        intent.setAction("superstock.service");
        sendBroadcast(intent);

        // 数据处理
        handleData(stock);




    }

    /**
     * 处理数据
     * @param stock stock
     */
    private void handleData(Stock stock) {
        if (stock == null) {
            return;
        }

        Integer nowBuyNumAvg = MathUtils.average(stock.getBuy1Num(), stock.getBuy2Num(), stock.getBuy3Num(), stock.getBuy4Num(), stock.getBuy5Num());
        Integer nowSellNumAvg = MathUtils.average(stock.getSell1Num(), stock.getSell2Num(), stock.getSell3Num(), stock.getSell4Num(), stock.getSell5Num());

        // 更新买手数均值
        if (nowBuyNumAvg != null) {
            buyNumAvg = buyNumAvg == null ? nowBuyNumAvg : MathUtils.average(nowBuyNumAvg, buyNumAvg);
        }

        // 更新卖手数均值
        if (nowSellNumAvg != null) {
            sellNumAvg = sellNumAvg == null ? nowSellNumAvg : MathUtils.average(nowSellNumAvg, sellNumAvg);
        }




    }

    private void setForeground() {
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        Intent intent = new Intent(this, MainActivity.class);

        builder.setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .setPriority(Notification.PRIORITY_MAX)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("SuperStock")
                .setContentText("正在运行...")
                .setTicker("service正在后台运行...")
                .setWhen(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
        }

        Notification notification = builder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel("notification_id", "nnotification_name", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        if (taskReceiver != null) {
            unregisterReceiver(taskReceiver);
        }
        super.onDestroy();

    }

    private class TaskReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("superstock.addStockMonitorTask")) {
                // 收到service的消息
                String stockNum = intent.getStringExtra("stockNum");
                String stockPrefix = intent.getStringExtra("stockPrefix");
                Monitor monitor = new Monitor();
                monitor.setStockNum(stockNum);
                monitor.setStockPrefix(stockPrefix);
                monitor.setisMonitor(true);

                // 创建线程
                createStockThread(monitor);
            }

        }
    }


}

package com.zcf.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTabHost;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zcf.R;
import com.zcf.base.BaseActivity;
import com.zcf.bean.Stock;
import com.zcf.constant.SuperStockConstant;
import com.zcf.dao.StockDao;
import com.zcf.fragment.DailyKChartFragment;
import com.zcf.fragment.MonthlyKChartFragment;
import com.zcf.fragment.WeeklyKChartFragment;
import com.zcf.util.StockUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StockPreviewActivity extends BaseActivity {
    private static final int COMPLETE = 0;
    private volatile boolean isFresh = true;
    private String stockNum;
    private String stockPrefix;
    private volatile boolean isInitData = true;

    private Stock initStock;

    /**
     * 异步线程处理UI
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case COMPLETE:
                    showStock(msg.getData());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stockpreview);

        // 初始化
        initData();
        initTitleBar();
        initKChart();
        refreshData();

    }

    /**
     * 初始化K线图
     */
    private void initKChart() {
        View kChart = findViewById(R.id.stockpreview_kchart);
        final FragmentTabHost fragmentTabHost = kChart.findViewById(R.id.kchart_tabhost);
        fragmentTabHost.setup(StockPreviewActivity.this, getSupportFragmentManager(), R.id.kchart_content);
        fragmentTabHost.getTabWidget().setDividerDrawable(null);
        Bundle bundle = new Bundle();
        bundle.putString("stockPrefix", stockPrefix);
        bundle.putString("stockNum", stockNum);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("日K").setIndicator(createViewComponent("日K")), DailyKChartFragment.class, bundle);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("周K").setIndicator(createViewComponent("周K")), WeeklyKChartFragment.class, bundle);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("月K").setIndicator(createViewComponent("月K")), MonthlyKChartFragment.class, bundle);
        fragmentTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                changeTabTextColor(fragmentTabHost);
            }
        });

        fragmentTabHost.setCurrentTab(1);
        fragmentTabHost.setCurrentTab(0);
    }

    /**
     * 改变Tab文字颜色
     * @param fragmentTabHost fragmentTabHost
     */
    private void changeTabTextColor(FragmentTabHost fragmentTabHost) {
        if (fragmentTabHost == null) {
            return;
        }
        for (int i = 0; i < fragmentTabHost.getTabWidget().getChildCount(); i++) {
            TextView tv_tabTitle = fragmentTabHost.getTabWidget().getChildAt(i).findViewById(R.id.tabbutton_title);
            if (fragmentTabHost.getCurrentTab() == i) {
                tv_tabTitle.setTextColor(getResources().getColor(R.color.colorRed));
            } else {
                tv_tabTitle.setTextColor(getResources().getColor(R.color.colorBlack));
            }
        }

    }

    /**
     * 创建Tab按键视图
     * @param str str
     * @return Tab按键视图
     */
    private View createViewComponent(String str) {
        View tabButton = LayoutInflater.from(this).inflate(R.layout.tabbutton, null);
        TextView tv_tabTitle = tabButton.findViewById(R.id.tabbutton_title);
        tv_tabTitle.setText(str);

        return tabButton;

    }


    /**
     * 展示股票数据UI
     * @param data data
     */
    private void showStock(Bundle data) {
        String msg = data.getString("msg");
        Stock stock = StockUtils.getStock(msg, stockNum, stockPrefix);

        View titleBar = findViewById(R.id.stockpreview_titlebar);
        TextView tv_title = titleBar.findViewById(R.id.titlebar_title);
        // 设置股票名称
        tv_title.setText(stock.getStockName());

        View stockPreview = findViewById(R.id.stockpreview_stock_resume);
        TextView tv_nowPrice = stockPreview.findViewById(R.id.stockpreview_now_price);

        TextView tv_priceDiff = stockPreview.findViewById(R.id.stockpreview_price_diff);
        TextView tv_percentDiff = stockPreview.findViewById(R.id.stockpreview_percent_diff);
        TextView tv_highPrice = stockPreview.findViewById(R.id.stockpreview_high_price);
        TextView tv_lowPrice = stockPreview.findViewById(R.id.stockpreview_low_price);
        TextView tv_startPrice = stockPreview.findViewById(R.id.stockpreview_start_price);
        TextView tv_buyPrice = stockPreview.findViewById(R.id.stockpreview_buy_price);
        TextView tv_sellPrice = stockPreview.findViewById(R.id.stockpreview_sell_price);
        TextView tv_yestodayPrice = stockPreview.findViewById(R.id.stockpreview_yestoday_price);
        TextView tv_dealStockNum = stockPreview.findViewById(R.id.stockpreview_deal_stock_num);
        TextView tv_dealStockPrice = stockPreview.findViewById(R.id.stockpreview_deal_stock_price);
        View buy5Sell5 = findViewById(R.id.stockpreview_buy5sell5);
        TextView tv_buy5Num = buy5Sell5.findViewById(R.id.stockpreview_buy5_num);
        TextView tv_buy5Price = buy5Sell5.findViewById(R.id.stockpreview_buy5_price);
        TextView tv_buy4Num = buy5Sell5.findViewById(R.id.stockpreview_buy4_num);
        TextView tv_buy4Price = buy5Sell5.findViewById(R.id.stockpreview_buy4_price);
        TextView tv_buy3Num = buy5Sell5.findViewById(R.id.stockpreview_buy3_num);
        TextView tv_buy3Price = buy5Sell5.findViewById(R.id.stockpreview_buy3_price);
        TextView tv_buy2Num = buy5Sell5.findViewById(R.id.stockpreview_buy2_num);
        TextView tv_buy2Price = buy5Sell5.findViewById(R.id.stockpreview_buy2_price);
        TextView tv_buy1Num = buy5Sell5.findViewById(R.id.stockpreview_buy1_num);
        TextView tv_buy1Price = buy5Sell5.findViewById(R.id.stockpreview_buy1_price);
        TextView tv_sell1Num = buy5Sell5.findViewById(R.id.stockpreview_sell1_num);
        TextView tv_sell1Price = buy5Sell5.findViewById(R.id.stockpreview_sell1_price);
        TextView tv_sell2Num = buy5Sell5.findViewById(R.id.stockpreview_sell2_num);
        TextView tv_sell2Price = buy5Sell5.findViewById(R.id.stockpreview_sell2_price);
        TextView tv_sell3Num = buy5Sell5.findViewById(R.id.stockpreview_sell3_num);
        TextView tv_sell3Price = buy5Sell5.findViewById(R.id.stockpreview_sell3_price);
        TextView tv_sell4Num = buy5Sell5.findViewById(R.id.stockpreview_sell4_num);
        TextView tv_sell4Price = buy5Sell5.findViewById(R.id.stockpreview_sell4_price);
        TextView tv_sell5Num = buy5Sell5.findViewById(R.id.stockpreview_sell5_num);
        TextView tv_sell5Price = buy5Sell5.findViewById(R.id.stockpreview_sell5_price);
        // 分时图
        final ImageView iv_timeIndexChart = findViewById(R.id.stockpreview_time_index_chart);


        //设置值
        tv_nowPrice.setText(StockUtils.formatPrice(stock.getNowPrice()));
        tv_nowPrice.setTextColor(getResources().getColor(stock.getNowPriceColor()));
        tv_priceDiff.setText(StockUtils.formatPrice(stock.getPriceDiff()));
        tv_priceDiff.setTextColor(getResources().getColor(stock.getPriceDiffColor()));
        tv_percentDiff.setText(StockUtils.formatPrice(stock.getPercentDiff()) + "%");
        tv_percentDiff.setTextColor(getResources().getColor(stock.getPercentDiffColor()));
        tv_highPrice.setText(StockUtils.formatPrice(stock.getHighPrice()));
        tv_highPrice.setTextColor(getResources().getColor(stock.getHighPriceColor()));
        tv_lowPrice.setText(StockUtils.formatPrice(stock.getLowPrice()));
        tv_lowPrice.setTextColor(getResources().getColor(stock.getLowPriceColor()));
        tv_startPrice.setText(StockUtils.formatPrice(stock.getStartPrice()));
        tv_startPrice.setTextColor(getResources().getColor(stock.getStartPriceColor()));
        tv_buyPrice.setText(StockUtils.formatPrice(stock.getBuyPrice()));
        tv_buyPrice.setTextColor(getResources().getColor(stock.getBuyPriceColor()));
        tv_sellPrice.setText(StockUtils.formatPrice(stock.getSellPrice()));
        tv_sellPrice.setTextColor(getResources().getColor(stock.getSellPriceColor()));
        tv_yestodayPrice.setText(StockUtils.formatPrice(stock.getYestodayPrice()));
        tv_yestodayPrice.setTextColor(getResources().getColor(stock.getYestodayPriceColor()));
        tv_dealStockNum.setText(String.valueOf(stock.getDealStockNum()));
        tv_dealStockNum.setTextColor(getResources().getColor(stock.getDealStockNumColor()));
        tv_dealStockPrice.setText(StockUtils.formatPrice(stock.getDealStockPrice()));
        tv_dealStockPrice.setTextColor(getResources().getColor(stock.getDealStockPriceColor()));

        tv_buy5Num.setText(String.valueOf(stock.getBuy5Num()));
        tv_buy5Num.setTextColor(getResources().getColor(stock.getBuy5NumColor()));
        tv_buy5Price.setText(StockUtils.formatPrice(stock.getBuy5Price()));
        tv_buy5Price.setTextColor(getResources().getColor(stock.getBuy5PriceColor()));
        tv_buy4Num.setText(String.valueOf(stock.getBuy4Num()));
        tv_buy4Num.setTextColor(getResources().getColor(stock.getBuy4NumColor()));
        tv_buy4Price.setText(StockUtils.formatPrice(stock.getBuy4Price()));
        tv_buy4Price.setTextColor(getResources().getColor(stock.getBuy4PriceColor()));
        tv_buy3Num.setText(String.valueOf(stock.getBuy3Num()));
        tv_buy3Num.setTextColor(getResources().getColor(stock.getBuy3NumColor()));
        tv_buy3Price.setText(StockUtils.formatPrice(stock.getBuy3Price()));
        tv_buy3Price.setTextColor(getResources().getColor(stock.getBuy3PriceColor()));
        tv_buy2Num.setText(String.valueOf(stock.getBuy2Num()));
        tv_buy2Num.setTextColor(getResources().getColor(stock.getBuy2NumColor()));
        tv_buy2Price.setText(StockUtils.formatPrice(stock.getBuy2Price()));
        tv_buy2Price.setTextColor(getResources().getColor(stock.getBuy2PriceColor()));
        tv_buy1Num.setText(String.valueOf(stock.getBuy1Num()));
        tv_buy1Num.setTextColor(getResources().getColor(stock.getBuy1NumColor()));
        tv_buy1Price.setText(StockUtils.formatPrice(stock.getBuy1Price()));
        tv_buy1Price.setTextColor(getResources().getColor(stock.getBuy1PriceColor()));
        tv_sell1Num.setText(String.valueOf(stock.getSell1Num()));
        tv_sell1Num.setTextColor(getResources().getColor(stock.getSell1NumColor()));
        tv_sell1Price.setText(StockUtils.formatPrice(stock.getSell1Price()));
        tv_sell1Price.setTextColor(getResources().getColor(stock.getSell1PriceColor()));
        tv_sell2Num.setText(String.valueOf(stock.getSell2Num()));
        tv_sell2Num.setTextColor(getResources().getColor(stock.getSell2NumColor()));
        tv_sell2Price.setText(StockUtils.formatPrice(stock.getSell2Price()));
        tv_sell2Price.setTextColor(getResources().getColor(stock.getSell2PriceColor()));
        tv_sell3Num.setText(String.valueOf(stock.getSell3Num()));
        tv_sell3Num.setTextColor(getResources().getColor(stock.getSell3NumColor()));
        tv_sell3Price.setText(StockUtils.formatPrice(stock.getSell3Price()));
        tv_sell3Price.setTextColor(getResources().getColor(stock.getSell3PriceColor()));
        tv_sell4Num.setText(String.valueOf(stock.getSell4Num()));
        tv_sell4Num.setTextColor(getResources().getColor(stock.getSell4NumColor()));
        tv_sell4Price.setText(StockUtils.formatPrice(stock.getSell4Price()));
        tv_sell4Price.setTextColor(getResources().getColor(stock.getSell4PriceColor()));
        tv_sell5Num.setText(String.valueOf(stock.getSell5Num()));
        tv_sell5Num.setTextColor(getResources().getColor(stock.getSell5NumColor()));
        tv_sell5Price.setText(String.valueOf(stock.getSell5Price()));
        tv_sell5Price.setTextColor(getResources().getColor(stock.getSell5PriceColor()));

        String url = SuperStockConstant.TIME_INDEX_CHART_URL + stockPrefix + stockNum + ".gif";
        Glide.with(StockPreviewActivity.this)
                .load(url)
                .asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        iv_timeIndexChart.setImageBitmap(resource);
                    }
                });

    }

    /**
     * 反复刷新数据
     */
    private void refreshData() {
        Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                while (isFresh) {
                    freshData();
                    // 每隔一秒
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("[refresh]", "every 1s.");
                }
            }
        });
        thread.start();

    }

    /**
     * 刷新数据
     */
    private void freshData() {
        String url = SuperStockConstant.STOCK_MAIN_URL + stockPrefix + stockNum;

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("[HttpFailed]", "[SearchActivity][asyncHttpRequest] failed to http request.");
                // http请求重试
                httpRequestRetry(stockPrefix, stockNum);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(call, response, stockNum);
            }
        });
    }

    /**
     * 处理response信息并发消息给主线程更新UI
     * @param call call
     * @param response response
     */
    private void handleResponse(Call call, Response response, CharSequence stockNum) throws IOException {
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

        if (isInitData) {
            initStock = StockUtils.getStock(result, this.stockNum, stockPrefix);
            isInitData = !isInitData;
        }

        Message message = new Message();
        message.what = COMPLETE;
        Bundle bundle = new Bundle();
        bundle.putString("msg", result);
        message.setData(bundle);

        handler.sendMessage(message);

    }

    /**
     * http请求重试
     * @param stockPrefix stockPrefix
     * @param stockNum stockNum
     */
    private void httpRequestRetry(String stockPrefix, final CharSequence stockNum) {
        String url = SuperStockConstant.STOCK_MAIN_URL;
        if (stockPrefix.equals("sh")) {
            url = url + "sz" + stockNum;
        } else {
            url = url + "sh" + stockNum;
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        final String finalStockPrefix = stockPrefix;
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("[HttpFailed]", "[SearchActivity][asyncHttpRequest] failed to http request.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(call, response, stockNum);
            }
        });

    }


    /**
     * 初始化数据
     */
    private void initData() {
        // 获取股票代码
        this.stockNum = getIntent().getStringExtra("stockNum");
        // 获取股票前缀
        this.stockPrefix = StockUtils.getWhichStock(stockNum);
        if (this.stockPrefix == null || this.stockPrefix.trim().length() == 0) {
            this.stockPrefix = "sh";
        }

        freshData();
    }



    /**
     * 初始化title bar
     */
    private void initTitleBar() {
        View titleBar = findViewById(R.id.stockpreview_titlebar);
        TextView tv_subTitle = titleBar.findViewById(R.id.titlebar_sub_title);
        ImageButton imgbtn_left = titleBar.findViewById(R.id.titlebar_left_button);
        ImageButton imgbtn_right = titleBar.findViewById(R.id.titlebar_right_button);

        // UI股票代码
        tv_subTitle.setText(stockNum);
        tv_subTitle.setVisibility(View.VISIBLE);

        // 左按钮
        imgbtn_left.setImageResource(R.drawable.ic_back);
        imgbtn_left.setVisibility(View.VISIBLE);
        imgbtn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(StockPreviewActivity.this, SearchActivity.class);
                startActivity(intent);

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        // 右按钮
        imgbtn_right.setImageResource(R.drawable.ic_block_add);
        imgbtn_right.setVisibility(View.VISIBLE);
        imgbtn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 将监控数据添加至Monitor表
                boolean success = StockDao.insertMonitorTable(getApplicationContext(), stockNum, stockPrefix, initStock.getStockName());
                if (!success) {
                    return;
                }
                // 广播到service开启线程
                Intent intent = new Intent();
                intent.putExtra("stockNum", stockNum);
                intent.putExtra("stockPrefix", stockPrefix);
                intent.putExtra("stockName", initStock.getStockName());
                intent.setAction("superstock.addStockMonitorTask");
                sendBroadcast(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        if (!isFresh) {
            refreshData();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        isFresh = false;
        super.onStop();
    }

    @Override
    public void finish() {
        isFresh = false;
        super.finish();
    }

    @Override
    protected void onDestroy() {
        isFresh = false;
        super.onDestroy();
    }
}

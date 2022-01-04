package com.zcf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zcf.MainActivity;
import com.zcf.R;
import com.zcf.base.BaseActivity;
import com.zcf.bean.Stock;
import com.zcf.constant.SuperStockConstant;
import com.zcf.dao.StockDao;
import com.zcf.util.StockUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends BaseActivity {
    private static final int COMPLETE = 0;

    private String stockNum;
    private String stockPrefix;
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
                 default:
                     clearStock();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initSearchBar();

    }

    /**
     * 展示股票UI
     */
    private void showStock(Bundle bundle) {
        String msg = bundle.getString("msg");
        final String stockNum = bundle.getString("stockNum");
        final String stockPrefix = bundle.getString("stockPrefix");

        if (msg == null || stockNum == null) {
            return;
        }

        this.stockNum = stockNum;
        this.stockPrefix = stockPrefix;
        initStock = StockUtils.getStock(msg, stockNum, stockPrefix);
        String stockType = StockUtils.getStockType(stockNum);

        View searchItem = findViewById(R.id.searchactivity_item);
        View line = findViewById(R.id.searchactivity_line);

        TextView tv_stockName = searchItem.findViewById(R.id.searchstock_item_stock_name);
        TextView tv_stockNum = searchItem.findViewById(R.id.searchstock_item_stock_num);
        TextView tv_icon = searchItem.findViewById(R.id.searchstock_item_icon);
        ImageButton imgbtn_add = searchItem.findViewById(R.id.searchstock_item_add_button);

        tv_stockName.setText(initStock.getStockName());
        tv_stockNum.setText(stockNum);
        if (stockType != null) {
            tv_icon.setText(stockType);
        }
        searchItem.setVisibility(View.VISIBLE);
        line.setVisibility(View.VISIBLE);

        imgbtn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加按钮点击事件
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

        searchItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // search item点击事件
                Intent intent = new Intent();
                intent.setClass(SearchActivity.this, StockPreviewActivity.class);
                intent.putExtra("stockNum", stockNum);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    /**
     * 清除股票UI
     */
    private void clearStock() {
        View searchItem = findViewById(R.id.searchactivity_item);
        View line = findViewById(R.id.searchactivity_line);
        searchItem.setVisibility(View.INVISIBLE);
        line.setVisibility(View.INVISIBLE);

        // 清理内存
        stockNum = null;
        stockPrefix = null;
        initStock = null;
    }

    /**
     * 初始化 search bar
     */
    private void initSearchBar() {
        View searchBar = findViewById(R.id.search_searchbar);
        ImageButton backButton = searchBar.findViewById(R.id.searchbar_left_button);

        // 返回按钮
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SearchActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Edit text
        EditText editText = searchBar.findViewById(R.id.searchbar_edittext);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当股票代码长度完整
                if (s.length() == 6) {
                    // 判断是上海股票还是深圳股票
                    String stockPrefix = StockUtils.getWhichStock(s);
                    // 调用http请求
                    asyncHttpRequest(stockPrefix, s);

                } else if (s.length() < 6) {
                    // 用户回退了股票代码
                    clearStock();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            /**
             * 异步http请求
             * @param stockPrefix stockPrefix
             */
            private void asyncHttpRequest(String stockPrefix, final CharSequence stockNum) {
                if (stockPrefix == null || stockPrefix.trim().length() == 0) {
                    stockPrefix = "sh";
                }
                String url = SuperStockConstant.STOCK_MAIN_URL + stockPrefix + stockNum;
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
                        // http请求重试
                        httpRequestRetry(finalStockPrefix, stockNum);

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        handleResponse(call, response, stockNum, finalStockPrefix);
                    }
                });

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

                Message message = new Message();
                message.what = COMPLETE;
                Bundle bundle = new Bundle();
                bundle.putString("msg", result);
                bundle.putString("stockNum", String.valueOf(stockNum));
                bundle.putString("stockPrefix", stockPrefix);
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
                        handleResponse(call, response, stockNum, finalStockPrefix);
                    }
                });

            }

        });

    }
}

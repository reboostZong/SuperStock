package com.zcf.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zcf.R;
import com.zcf.constant.SuperStockConstant;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MonthlyKChartFragment extends Fragment {
    private static final int COMPLETE = 0;
    private View rootView;

    /**
     * 异步线程处理UI
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case COMPLETE:
                    showKChart(msg);
                    break;
            }
        }
    };

    /**
     * 展示K线图
     * @param msg msg
     */
    private void showKChart(Message msg) {
        byte[] pic_bytes = (byte[]) msg.obj;
        Bitmap bitmap = BitmapFactory.decodeByteArray(pic_bytes, 0, pic_bytes.length);

        ImageView iv_dailyKChart = rootView.findViewById(R.id.fragment_monthly_kchart);
        iv_dailyKChart.setImageBitmap(bitmap);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // 缓存优化
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_monthlykchart, null);
            initKChart();
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }

        return rootView;

    }

    /**
     * 初始化K线图
     */
    private void initKChart() {
        Bundle bundle = getArguments();
        String stockPrefix = bundle.getString("stockPrefix");
        String stockNum = bundle.getString("stockNum");
        String url = SuperStockConstant.MONTHLY_KCHART_URL + stockPrefix + stockNum + ".gif";

        // http请求图片
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
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
                messageKChart(call, response);
            }
        });


    }

    /**
     * 发生K线图消息
     * @param call call
     * @param response response
     */
    private void messageKChart(Call call, Response response) throws IOException {
        byte[] pic_bytes = response.body().bytes();
        Message message = new Message();
        message.what = COMPLETE;
        message.obj = pic_bytes;

        handler.sendMessage(message);
    }
}

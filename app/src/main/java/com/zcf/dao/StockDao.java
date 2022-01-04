package com.zcf.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.zcf.bean.Monitor;

import java.util.ArrayList;
import java.util.List;

public class StockDao {
    /**
     * 将监控数据添加至Monitor表
     * @param context context
     * @param stockNum stockNum
     * @param stockPrefix stockPrefix
     * @param stockName stockName
     */
    public static boolean insertMonitorTable(Context context, String stockNum, String stockPrefix, String stockName) {
        SQLiteHelper sqLiteHelper = SQLiteHelper.getInstance(context);
        // 添加进monitor表
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
        Cursor cursor = db.query("monitor", new String[]{"count(*)"}, "stock_num=?", new String[]{stockNum}, null, null, null);
        long count = 0;
        if (cursor.moveToNext()) {
            count = cursor.getLong(0);
        }
        if (count > 0) {
            // 已存在
            Toast.makeText(context, "已添加!", Toast.LENGTH_LONG).show();
            return false;
        }

        // 添加至数据库
        ContentValues dataValues = new ContentValues();
        dataValues.put("stock_num", stockNum);
        dataValues.put("stock_name", stockName);
        dataValues.put("stock_prefix", stockPrefix);
        dataValues.put("is_monitor", 1);

        db.insert("monitor", null, dataValues);
        Toast.makeText(context, "添加成功!", Toast.LENGTH_LONG).show();

        return true;
    }

    /**
     * 获取数据库中所有的Monitor
     * @param context context
     * @return List<Monitor>
     */
    public static List<Monitor> getAllMonitor(Context context) {
        SQLiteHelper sqLiteHelper = SQLiteHelper.getInstance(context);
        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();

        Cursor cursor = db.query("monitor",
                new String[]{"id", "stock_num", "stock_name", "stock_prefix", "is_monitor"},
                null,
                null,
                null,
                null,
                null);
        List<Monitor> monitorList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Monitor monitor = new Monitor();
            int id = cursor.getInt(0);
            String stockNum = cursor.getString(1);
            String stockName = cursor.getString(2);
            String stockPrefix = cursor.getString(3);
            int isMonitor = cursor.getInt(4);
            boolean boolMonitor = isMonitor == 1 ? true : false;

            monitor.setId(String.valueOf(id));
            monitor.setStockNum(stockNum);
            monitor.setStockName(stockName);
            monitor.setStockPrefix(stockPrefix);
            monitor.setisMonitor(boolMonitor);
            monitorList.add(monitor);
        }

        return monitorList;
    }
}

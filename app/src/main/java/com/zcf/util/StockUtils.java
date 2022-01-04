package com.zcf.util;

import com.zcf.R;
import com.zcf.bean.Stock;
import com.zcf.constant.SuperStockConstant;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

public class StockUtils {
    /**
     * 获取股票类型
     *
     * @param stockNum
     * @return
     */
    public static String getStockType(String stockNum) {
        if (stockNum.startsWith("00")) {
            return "深A";
        } else if (stockNum.startsWith("300")) {
            return "创";
        } else if (stockNum.startsWith("60")) {
            return "沪A";
        } else if (stockNum.startsWith("688")) {
            return "科创";
        } else if (stockNum.startsWith("200")) {
            return "深B";
        } else if (stockNum.startsWith("900")) {
            return "沪B";
        }
        return null;
    }

    /**
     * 获取股票前缀
     * @param stockNum stockNum
     * @return prefix
     */
    public static String getWhichStock(CharSequence stockNum) {
        String prefix = "";
        boolean matchesh = Pattern.matches(SuperStockConstant.SH_STOCK_PATTERN, stockNum);
        if (matchesh) {
            prefix = "sh";
        } else {
            boolean matchesz = Pattern.matches(SuperStockConstant.SZ_STOCK_PATTERN, stockNum);
            if (matchesz) {
                prefix = "sz";
            }
        }

        return prefix;
    }

    /**
     * 格式化价格，保留2位小数
     * @param price price
     * @return 格式化价格
     */
    public static String formatPrice(double price) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return decimalFormat.format(price);
    }

    /**
     * 格式化价格，保留2位小数
     * @param price price
     * @return 格式化价格
     */
    public static String formatPrice(String price) {
        double num_price = Double.parseDouble(price);
        return formatPrice(num_price);
    }

    /**
     * 格式化股票手数
     * @param num num
     * @return 股票手数
     */
    public static String formatStockNum(String num) {
        double dNum = Double.parseDouble(num);
        DecimalFormat decimalFormat = new DecimalFormat("0");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return decimalFormat.format(dNum / 100);
    }

    /**
     * 获取股票颜色， 红色或绿色
     * @param price price
     * @param startPrice startPrice
     * @return 股票颜色
     */
    public static int getStockColor(String price, String startPrice) {
        double dPrice = Double.parseDouble(price);
        double dStartPrice = Double.parseDouble(startPrice);

        return dPrice >= dStartPrice ? R.color.colorRed : R.color.colorGreen;
    }

    /**
     * 获取股票颜色， 红色或绿色
     * @param price price
     * @param startPrice startPrice
     * @return 股票颜色
     */
    public static int getStockColor(double price, double startPrice) {
        return price >= startPrice ? R.color.colorRed : R.color.colorGreen;
    }

    /**
     * 获取股票
     * @param msg msg
     * @param stockNum stockNum
     * @param stockPrefix stockPrefix
     * @return Stock
     */
    public static Stock getStock(String msg, String stockNum, String stockPrefix) {
        Stock stock = new Stock();
        if (msg == null || msg.trim().length() == 0) {
            return stock;
        }

        String[] fields = msg.split(",");
        if (fields.length < 30) {
            return stock;
        }

        stock.setStockNum(stockNum);
        stock.setStockPrefix(stockPrefix);
        String stockName = fields[0];
        stock.setStockName(stockName);
        String startPrice = fields[1];
        stock.setStartPrice(Double.parseDouble(startPrice));
        String yestodayPrice = fields[2];
        stock.setYestodayPrice(Double.parseDouble(yestodayPrice));
        String nowPrice = fields[3];
        stock.setNowPrice(Double.parseDouble(nowPrice));
        String highPrice = fields[4];
        stock.setHighPrice(Double.parseDouble(highPrice));
        String lowPrice = fields[5];
        stock.setLowPrice(Double.parseDouble(lowPrice));
        String buyPrice = fields[6];
        stock.setBuyPrice(Double.parseDouble(buyPrice));
        String sellPrice = fields[7];
        stock.setSellPrice(Double.parseDouble(sellPrice));
        String dealStockNum = fields[8];
        stock.setDealStockNum(Integer.parseInt(dealStockNum));
        String dealStockPrice = fields[9];
        stock.setDealStockPrice(Double.parseDouble(dealStockPrice));
        String buy1Num = fields[10];
        stock.setBuy1Num(Integer.parseInt(buy1Num)/100);
        String buy1Price = fields[11];
        stock.setBuy1Price(Double.parseDouble(StockUtils.formatPrice(buy1Price)));
        String buy2Num = fields[12];
        stock.setBuy2Num(Integer.parseInt(buy2Num)/100);
        String buy2Price = fields[13];
        stock.setBuy2Price(Double.parseDouble(StockUtils.formatPrice(buy2Price)));
        String buy3Num = fields[14];
        stock.setBuy3Num(Integer.parseInt(buy3Num)/100);
        String buy3Price = fields[15];
        stock.setBuy3Price(Double.parseDouble(StockUtils.formatPrice(buy3Price)));
        String buy4Num = fields[16];
        stock.setBuy4Num(Integer.parseInt(buy4Num)/100);
        String buy4Price = fields[17];
        stock.setBuy4Price(Double.parseDouble(StockUtils.formatPrice(buy4Price)));
        String buy5Num = fields[18];
        stock.setBuy5Num(Integer.parseInt(buy5Num)/100);
        String buy5Price = fields[19];
        stock.setBuy5Price(Double.parseDouble(StockUtils.formatPrice(buy5Price)));
        String sell1Num = fields[20];
        stock.setSell1Num(Integer.parseInt(sell1Num)/100);
        String sell1Price = fields[21];
        stock.setSell1Price(Double.parseDouble(StockUtils.formatPrice(sell1Price)));
        String sell2Num = fields[22];
        stock.setSell2Num(Integer.parseInt(sell2Num)/100);
        String sell2Price = fields[23];
        stock.setSell2Price(Double.parseDouble(StockUtils.formatPrice(sell2Price)));
        String sell3Num = fields[24];
        stock.setSell3Num(Integer.parseInt(sell3Num)/100);
        String sell3Price = fields[25];
        stock.setSell3Price(Double.parseDouble(StockUtils.formatPrice(sell3Price)));
        String sell4Num = fields[26];
        stock.setSell4Num(Integer.parseInt(sell4Num)/100);
        String sell4Price = fields[27];
        stock.setSell4Price(Double.parseDouble(StockUtils.formatPrice(sell4Price)));
        String sell5Num = fields[28];
        stock.setSell5Num(Integer.parseInt(sell5Num)/100);
        String sell5Price = fields[29];
        stock.setSell5Price(Double.parseDouble(StockUtils.formatPrice(sell5Price)));

        return stock;
    }



}

package com.zcf.bean;

import java.util.Objects;

public class MonitorItem {
    private String stockNum;
    private String stockName;
    private String stockPrefix;
    private Boolean isMonitor;
    private String nowPrice;
    private Integer nowPriceColor;
    private String percentDiff;
    private Integer percentDiffColor;
    private String diffPrice;
    private Integer diffPriceColor;
    private String remind;
    private Integer remindColor;

    public String getStockNum() {
        return stockNum;
    }

    public void setStockNum(String stockNum) {
        this.stockNum = stockNum;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockPrefix() {
        return stockPrefix;
    }

    public void setStockPrefix(String stockPrefix) {
        this.stockPrefix = stockPrefix;
    }

    public Boolean getIsMonitor() {
        return isMonitor;
    }

    public void setIsMonitor(Boolean monitor) {
        isMonitor = monitor;
    }

    public String getNowPrice() {
        return nowPrice;
    }

    public void setNowPrice(String nowPrice) {
        this.nowPrice = nowPrice;
    }

    public String getPercentDiff() {
        return percentDiff;
    }

    public void setPercentDiff(String percentDiff) {
        this.percentDiff = percentDiff;
    }

    public String getDiffPrice() {
        return diffPrice;
    }

    public void setDiffPrice(String diffPrice) {
        this.diffPrice = diffPrice;
    }

    public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
        this.remind = remind;
    }

    public Integer getNowPriceColor() {
        return nowPriceColor;
    }

    public void setNowPriceColor(Integer nowPriceColor) {
        this.nowPriceColor = nowPriceColor;
    }

    public Integer getPercentDiffColor() {
        return percentDiffColor;
    }

    public void setPercentDiffColor(Integer percentDiffColor) {
        this.percentDiffColor = percentDiffColor;
    }

    public Integer getDiffPriceColor() {
        return diffPriceColor;
    }

    public void setDiffPriceColor(Integer diffPriceColor) {
        this.diffPriceColor = diffPriceColor;
    }

    public Integer getRemindColor() {
        return remindColor;
    }

    public void setRemindColor(Integer remindColor) {
        this.remindColor = remindColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonitorItem that = (MonitorItem) o;
        return Objects.equals(stockNum, that.stockNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockNum);
    }

    @Override
    public String toString() {
        return "MonitorItem{" +
                "stockNum='" + stockNum + '\'' +
                ", stockName='" + stockName + '\'' +
                ", stockPrefix='" + stockPrefix + '\'' +
                ", isMonitor=" + isMonitor +
                ", nowPrice='" + nowPrice + '\'' +
                ", nowPriceColor=" + nowPriceColor +
                ", percentDiff='" + percentDiff + '\'' +
                ", percentDiffColor=" + percentDiffColor +
                ", diffPrice='" + diffPrice + '\'' +
                ", diffPriceColor=" + diffPriceColor +
                ", remind='" + remind + '\'' +
                ", remindColor=" + remindColor +
                '}';
    }
}

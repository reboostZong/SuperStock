package com.zcf.bean;

import java.util.Objects;

public class Monitor {
    private String id;
    private String stockNum;
    private String stockName;
    private String stockPrefix;
    private Boolean isMonitor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Boolean getisMonitor() {
        return isMonitor;
    }

    public void setisMonitor(Boolean monitor) {
        isMonitor = monitor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Monitor monitor = (Monitor) o;
        return Objects.equals(stockNum, monitor.stockNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockNum);
    }

    @Override
    public String toString() {
        return "Monitor{" +
                "id='" + id + '\'' +
                ", stockNum='" + stockNum + '\'' +
                ", stockName='" + stockName + '\'' +
                ", stockPrefix='" + stockPrefix + '\'' +
                ", isMonitor=" + isMonitor +
                '}';
    }
}

package com.zcf.bean;

import com.zcf.R;
import com.zcf.util.StockUtils;

import java.io.Serializable;
import java.util.Objects;

public class Stock implements Serializable {
    private static final long serialVersionUID = -4579629198277613036L;

    private Integer id;
    private String stockNum;
    private String stockName;
    private String stockPrefix;
    private Double startPrice;
    private Double yestodayPrice;
    private Double nowPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double buyPrice;
    private Double sellPrice;
    private Integer dealStockNum;
    private Double dealStockPrice;
    private Integer buy1Num;
    private Double buy1Price;
    private Integer buy2Num;
    private Double buy2Price;
    private Integer buy3Num;
    private Double buy3Price;
    private Integer buy4Num;
    private Double buy4Price;
    private Integer buy5Num;
    private Double buy5Price;
    private Integer sell1Num;
    private Double sell1Price;
    private Integer sell2Num;
    private Double sell2Price;
    private Integer sell3Num;
    private Double sell3Price;
    private Integer sell4Num;
    private Double sell4Price;
    private Integer sell5Num;
    private Double sell5Price;
    private Double priceDiff;
    private Double percentDiff;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Double getStartPrice() {
        return startPrice;
    }

    public int getStartPriceColor() {
        return StockUtils.getStockColor(startPrice, yestodayPrice);
    }

    public void setStartPrice(Double startPrice) {
        this.startPrice = startPrice;
    }

    public Double getYestodayPrice() {
        return yestodayPrice;
    }

    public int getYestodayPriceColor() {
        return R.color.colorGray;
    }

    public void setYestodayPrice(Double yestodayPrice) {
        this.yestodayPrice = yestodayPrice;
    }

    public Double getNowPrice() {
        return nowPrice;
    }

    public int getNowPriceColor() {
        return StockUtils.getStockColor(nowPrice, yestodayPrice);
    }

    public void setNowPrice(Double nowPrice) {
        this.nowPrice = nowPrice;
    }

    public Double getHighPrice() {
        return highPrice;
    }

    public int getHighPriceColor() {
        return StockUtils.getStockColor(highPrice, yestodayPrice);
}

    public void setHighPrice(Double highPrice) {
        this.highPrice = highPrice;
    }

    public Double getLowPrice() {
        return lowPrice;
    }

    public int getLowPriceColor() {
        return StockUtils.getStockColor(lowPrice, yestodayPrice);
    }

    public void setLowPrice(Double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public Double getBuyPrice() {
        return buyPrice;
    }

    public int getBuyPriceColor() {
        return StockUtils.getStockColor(buyPrice, yestodayPrice);
    }

    public void setBuyPrice(Double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    public int getSellPriceColor() {
        return StockUtils.getStockColor(sellPrice, yestodayPrice);
    }

    public void setSellPrice(Double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public Integer getDealStockNum() {
        return dealStockNum;
    }

    public int getDealStockNumColor() {
        return StockUtils.getStockColor(dealStockNum, 0);
    }


    public void setDealStockNum(Integer dealStockNum) {
        this.dealStockNum = dealStockNum;
    }

    public Double getDealStockPrice() {
        return dealStockPrice;
    }

    public int getDealStockPriceColor() {
        return R.color.colorRed;
    }

    public void setDealStockPrice(Double dealStockPrice) {
        this.dealStockPrice = dealStockPrice;
    }

    public Integer getBuy1Num() {
        return buy1Num;
    }

    public int getBuy1NumColor() {
        return StockUtils.getStockColor(buy1Price, yestodayPrice);
    }

    public void setBuy1Num(Integer buy1Num) {
        this.buy1Num = buy1Num;
    }

    public Double getBuy1Price() {
        return buy1Price;
    }

    public int getBuy1PriceColor() {
        return StockUtils.getStockColor(buy1Price, yestodayPrice);
    }

    public void setBuy1Price(Double buy1Price) {
        this.buy1Price = buy1Price;
    }

    public Integer getBuy2Num() {
        return buy2Num;
    }

    public int getBuy2NumColor() {
        return StockUtils.getStockColor(buy2Price, yestodayPrice);
    }

    public void setBuy2Num(Integer buy2Num) {
        this.buy2Num = buy2Num;
    }

    public Double getBuy2Price() {
        return buy2Price;
    }

    public int getBuy2PriceColor() {
        return StockUtils.getStockColor(buy2Price, yestodayPrice);
    }

    public void setBuy2Price(Double buy2Price) {
        this.buy2Price = buy2Price;
    }

    public Integer getBuy3Num() {
        return buy3Num;
    }

    public int getBuy3NumColor() {
        return StockUtils.getStockColor(buy3Price, yestodayPrice);
    }

    public void setBuy3Num(Integer buy3Num) {
        this.buy3Num = buy3Num;
    }

    public Double getBuy3Price() {
        return buy3Price;
    }

    public int getBuy3PriceColor() {
        return StockUtils.getStockColor(buy3Price, yestodayPrice);
    }

    public void setBuy3Price(Double buy3Price) {
        this.buy3Price = buy3Price;
    }

    public Integer getBuy4Num() {
        return buy4Num;
    }

    public int getBuy4NumColor() {
        return StockUtils.getStockColor(buy4Price, yestodayPrice);
    }

    public void setBuy4Num(Integer buy4Num) {
        this.buy4Num = buy4Num;
    }

    public Double getBuy4Price() {
        return buy4Price;
    }

    public int getBuy4PriceColor() {
        return StockUtils.getStockColor(buy4Price, yestodayPrice);
    }

    public void setBuy4Price(Double buy4Price) {
        this.buy4Price = buy4Price;
    }

    public Integer getBuy5Num() {
        return buy5Num;
    }

    public int getBuy5NumColor() {
        return StockUtils.getStockColor(buy5Price, yestodayPrice);
    }

    public void setBuy5Num(Integer buy5Num) {
        this.buy5Num = buy5Num;
    }

    public Double getBuy5Price() {
        return buy5Price;
    }

    public int getBuy5PriceColor() {
        return StockUtils.getStockColor(buy5Price, yestodayPrice);
    }


    public void setBuy5Price(Double buy5Price) {
        this.buy5Price = buy5Price;
    }

    public Integer getSell1Num() {
        return sell1Num;
    }

    public int getSell1NumColor() {
        return StockUtils.getStockColor(sell1Price, yestodayPrice);
    }

    public void setSell1Num(Integer sell1Num) {
        this.sell1Num = sell1Num;
    }

    public Double getSell1Price() {
        return sell1Price;
    }

    public int getSell1PriceColor() {
        return StockUtils.getStockColor(sell1Price, yestodayPrice);
    }

    public void setSell1Price(Double sell1Price) {
        this.sell1Price = sell1Price;
    }

    public Integer getSell2Num() {
        return sell2Num;
    }

    public int getSell2NumColor() {
        return StockUtils.getStockColor(sell2Price, yestodayPrice);
    }

    public void setSell2Num(Integer sell2Num) {
        this.sell2Num = sell2Num;
    }

    public Double getSell2Price() {
        return sell2Price;
    }

    public int getSell2PriceColor() {
        return StockUtils.getStockColor(sell2Price, yestodayPrice);
    }

    public void setSell2Price(Double sell2Price) {
        this.sell2Price = sell2Price;
    }

    public Integer getSell3Num() {
        return sell3Num;
    }

    public int getSell3NumColor() {
        return StockUtils.getStockColor(sell3Price, yestodayPrice);
    }

    public void setSell3Num(Integer sell3Num) {
        this.sell3Num = sell3Num;
    }

    public Double getSell3Price() {
        return sell3Price;
    }

    public int getSell3PriceColor() {
        return StockUtils.getStockColor(sell3Price, yestodayPrice);
    }

    public void setSell3Price(Double sell3Price) {
        this.sell3Price = sell3Price;
    }

    public Integer getSell4Num() {
        return sell4Num;
    }

    public int getSell4NumColor() {
        return StockUtils.getStockColor(sell4Price, yestodayPrice);
    }

    public void setSell4Num(Integer sell4Num) {
        this.sell4Num = sell4Num;
    }

    public Double getSell4Price() {
        return sell4Price;
    }

    public int getSell4PriceColor() {
        return StockUtils.getStockColor(sell4Price, yestodayPrice);
    }

    public void setSell4Price(Double sell4Price) {
        this.sell4Price = sell4Price;
    }

    public Integer getSell5Num() {
        return sell5Num;
    }

    public int getSell5NumColor() {
        return StockUtils.getStockColor(sell5Price, yestodayPrice);
    }

    public void setSell5Num(Integer sell5Num) {
        this.sell5Num = sell5Num;
    }

    public Double getSell5Price() {
        return sell5Price;
    }

    public int getSell5PriceColor() {
        return StockUtils.getStockColor(sell5Price, yestodayPrice);
    }

    public void setSell5Price(Double sell5Price) {
        this.sell5Price = sell5Price;
    }

    public Double getPriceDiff() {
        return nowPrice -yestodayPrice;
    }

    public int getPriceDiffColor() {
        return StockUtils.getStockColor(getPriceDiff(), 0);
    }

    public Double getPercentDiff() {
        return (nowPrice - yestodayPrice) / yestodayPrice * 100;
    }

    public int getPercentDiffColor() {
        return StockUtils.getStockColor(getPercentDiff(), 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return Objects.equals(stockNum, stock.stockNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockNum);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", stockNum='" + stockNum + '\'' +
                ", stockName='" + stockName + '\'' +
                ", stockPrefix='" + stockPrefix + '\'' +
                ", startPrice=" + startPrice +
                ", yestodayPrice=" + yestodayPrice +
                ", nowPrice=" + nowPrice +
                ", highPrice=" + highPrice +
                ", lowPrice=" + lowPrice +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                ", dealStockNum=" + dealStockNum +
                ", dealStockPrice=" + dealStockPrice +
                ", buy1Num=" + buy1Num +
                ", buy1Price=" + buy1Price +
                ", buy2Num=" + buy2Num +
                ", buy2Price=" + buy2Price +
                ", buy3Num=" + buy3Num +
                ", buy3Price=" + buy3Price +
                ", buy4Num=" + buy4Num +
                ", buy4Price=" + buy4Price +
                ", buy5Num=" + buy5Num +
                ", buy5Price=" + buy5Price +
                ", sell1Num=" + sell1Num +
                ", sell1Price=" + sell1Price +
                ", sell2Num=" + sell2Num +
                ", sell2Price=" + sell2Price +
                ", sell3Num=" + sell3Num +
                ", sell3Price=" + sell3Price +
                ", sell4Num=" + sell4Num +
                ", sell4Price=" + sell4Price +
                ", sell5Num=" + sell5Num +
                ", sell5Price=" + sell5Price +
                ", priceDiff=" + priceDiff +
                ", percentDiff=" + percentDiff +
                '}';
    }
}

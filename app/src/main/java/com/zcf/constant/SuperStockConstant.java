package com.zcf.constant;

public class SuperStockConstant {
    /**
     * 上海股票PATTERN
     */
    public static final String SH_STOCK_PATTERN = "^(600|601|603|688|900)\\d{3}$";

    /**
     * 深圳股票PATTERN
     */
    public static final String SZ_STOCK_PATTERN = "^(00|002|003|004|300|200)\\d{3,4}$";

    /**
     * 股票主url
     */
    public static final String STOCK_MAIN_URL = "https://hq.sinajs.cn/list=";

    /**
     * http返回体有效内容
     */
    public static final String HTTP_RESPONSE_CONTENT = "\"(.*)\"";

    /**
     * 分时走势图url
     */
    public static final String TIME_INDEX_CHART_URL = "http://image.sinajs.cn/newchart/min/n/";

    /**
     * 日K线
     */
    public static final String DAILY_KCHART_URL = "http://image.sinajs.cn/newchart/daily/n/";

    /**
     * 周K线
     */
    public static final String WEEKLY_KCHART_URL = "http://image.sinajs.cn/newchart/weekly/n/";

    /**
     * 月K线
     */
    public static final String MONTHLY_KCHART_URL = "http://image.sinajs.cn/newchart/monthly/n/";
}

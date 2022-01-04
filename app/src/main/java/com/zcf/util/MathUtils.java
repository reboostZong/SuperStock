package com.zcf.util;

/**
 * 数学计算工具
 */
public class MathUtils {


    /**
     * 计算平均值
     * @param nums nums
     * @return 平均值
     */
    public static Integer average(Integer... nums) {
        int sum = 0;
        int count = 0;
        for (Integer num : nums) {
            if (num == null) {
                continue;
            }
            count++;
            sum += num;
        }

        if (count == 0) {
            return null;
        }

        return sum / count;
    }

    /**
     * 计算标准差
     * @param nums nums
     * @return 标准差
     */
    public static Integer standardDeviation(Integer... nums) {
        // 计算均值
        Integer avg = average(nums);
        // 计算方差

        // 计算标准差
        return null;
    }
}

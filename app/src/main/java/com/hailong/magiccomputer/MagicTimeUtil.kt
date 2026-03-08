package com.hailong.magiccomputer

import java.util.Calendar

object MagicTimeUtil {

    // 获取当前的月日时分并拼接成数值（格式：MMddHHmm）
    fun getCurrentTimeNumber(): Long {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1  // 月份从0开始，所以要+1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY) // 24小时制
        val minute = calendar.get(Calendar.MINUTE)

        // 拼接成 MMddHHmm 格式
        return month * 1000000L + day * 10000L + hour * 100L + minute
    }

    // 格式化时间显示（用于调试或显示）
    fun getCurrentTimeString(): String {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return String.format("%02d月%02d日 %02d:%02d", month, day, hour, minute)
    }
}
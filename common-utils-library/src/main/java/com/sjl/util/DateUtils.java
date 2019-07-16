package com.sjl.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author songjiali
 * @version 1.0.0
 * @filename DateUtils.java
 * @time 2018/10/22 17:25
 * @copyright(C) 2018 song
 */
public class DateUtils {

    /**
     * 指定日期格式 yyyyMMddHHmmss
     */
    public static final String DATE_FORMAT_1 = "yyyyMMddHHmmss";

    /**
     * 指定日期格式 yyyy-MM-dd HH:mm:ss
     */
    public static final String DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";

    /**
     * 指定日期格式yyyy-MM-dd HH:mm
     */
    public static final String DATE_FORMAT_3 = "yyyy-MM-dd HH:mm";

    /**
     * 指定日期格式 yyyy-MM-dd
     */
    public static final String DATE_FORMAT_4 = "yyyy-MM-dd";

    /**
     * 指定日期格式 yyyy-MM-dd HH:mm:ss:SSS
     */
    public static final String DATE_FORMAT_5 = "yyyy-MM-dd HH:mm:ss:SSS";


    /**
     * 指定日期格式 yyMMdd,如180619
     */
    public static final String DATE_FORMAT_6 = "yyMMdd";

    /**
     * 指定日期格式 HHmmss,如113038
     */
    public static final String DATE_FORMAT_7 = "HHmmss";

    /**
     * 指定日期格式 yyMMdd,如20180619
     */
    public static final String DATE_FORMAT_8 = "yyyyMMdd";

    /**
     * date转String
     *
     * @param date   日期date
     * @param format 日期格式
     *               比如"yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static String formatDateTime(Date date, String format) {
        String dateStr = "";
        DateFormat sdf = new SimpleDateFormat(format);
        dateStr = sdf.format(date);
        return dateStr;
    }


    /**
     * String转date
     *
     * @param str    字符串日期
     * @param format 如"yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static Date stringToDate(String str, String format) {
        DateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }


    /**
     * 把字符时间按指定格式返回
     *
     * @param str    字符串日期
     * @param format 日期格式
     * @return
     */
    public static String formatTime(String str, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date d = null;
        String date = "";
        try {
            d = formatter.parse(str);
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 把字符时间按指定格式返回
     *
     * @param str          字符串日期
     * @param sourceFormat 原始日期格式
     * @param targetFormat 目标日期格式
     * @return
     */
    public static String formatTime(String str, String sourceFormat, String targetFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(sourceFormat);
        Date d = null;
        String date = "";
        try {
            d = formatter.parse(str);
            SimpleDateFormat sdf = new SimpleDateFormat(targetFormat);
            date = sdf.format(d);
        } catch (Exception e) {
            return str;
        }
        return date;
    }


    /**
     * 两个日期相差几天 （天数=endTime - startTime）
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static long dateDiff(Date startTime, Date endTime) {
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        long day = 0;
        // 获得两个时间的毫秒时间差异
        diff = endTime.getTime()
                - startTime.getTime();
        day = diff / nd;// 计算差多少天
        long hour = diff % nd / nh;// 计算差多少小时
        long min = diff % nd % nh / nm;// 计算差多少分钟
        long sec = diff % nd % nh % nm / ns;// 计算差多少秒
        // 输出结果
        LogUtils.i("时间相差：" + day + "天" + hour + "小时" + min
                + "分钟" + sec + "秒。");
        if (day >= 1) {
            return day;
        } else {
            if (day == 0) {
                return 1;
            } else {
                return 0;
            }

        }
    }

    /**
     * 和当前日期进行比较
     *
     * @param date
     * @param format
     * @return true已经超出当前日期，false没有超出当前日期
     */
    public static boolean compareCurrentDate(String date, String format) {
        DateFormat df = new SimpleDateFormat(format);
        try {
            Date dt1 = df.parse(date);
            Date dt2 = new Date();
            if (dt1.getTime() >= dt2.getTime()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

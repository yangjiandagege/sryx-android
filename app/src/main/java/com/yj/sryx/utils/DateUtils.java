package com.yj.sryx.utils;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * 日期时间工具类
 *
 * @author yangjian
 */
public class DateUtils extends android.text.format.DateUtils {
    /**
     * The enum Difference mode.
     */
    public enum DifferenceMode {
        Second,
        Minute,
        Hour,
        Day
    }

    /**
     * Calculate different second long.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the long
     */
    public static long calculateDifferentSecond(Date startDate, Date endDate) {
        return calculateDifference(startDate, endDate, DifferenceMode.Second);
    }

    /**
     * Calculate different minute long.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the long
     */
    public static long calculateDifferentMinute(Date startDate, Date endDate) {
        return calculateDifference(startDate, endDate, DifferenceMode.Minute);
    }

    /**
     * Calculate different hour long.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the long
     */
    public static long calculateDifferentHour(Date startDate, Date endDate) {
        return calculateDifference(startDate, endDate, DifferenceMode.Hour);
    }

    /**
     * Calculate different day long.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the long
     */
    public static long calculateDifferentDay(Date startDate, Date endDate) {
        return calculateDifference(startDate, endDate, DifferenceMode.Day);
    }

    /**
     * Calculate different second long.
     *
     * @param startTimeMillis the start time millis
     * @param endTimeMillis   the end time millis
     * @return the long
     */
    public static long calculateDifferentSecond(long startTimeMillis, long endTimeMillis) {
        return calculateDifference(startTimeMillis, endTimeMillis, DifferenceMode.Second);
    }

    /**
     * Calculate different minute long.
     *
     * @param startTimeMillis the start time millis
     * @param endTimeMillis   the end time millis
     * @return the long
     */
    public static long calculateDifferentMinute(long startTimeMillis, long endTimeMillis) {
        return calculateDifference(startTimeMillis, endTimeMillis, DifferenceMode.Minute);
    }

    /**
     * Calculate different hour long.
     *
     * @param startTimeMillis the start time millis
     * @param endTimeMillis   the end time millis
     * @return the long
     */
    public static long calculateDifferentHour(long startTimeMillis, long endTimeMillis) {
        return calculateDifference(startTimeMillis, endTimeMillis, DifferenceMode.Hour);
    }

    /**
     * Calculate different day long.
     *
     * @param startTimeMillis the start time millis
     * @param endTimeMillis   the end time millis
     * @return the long
     */
    public static long calculateDifferentDay(long startTimeMillis, long endTimeMillis) {
        return calculateDifference(startTimeMillis, endTimeMillis, DifferenceMode.Day);
    }

    /**
     * Calculate difference long.
     *
     * @param startTimeMillis the start time millis
     * @param endTimeMillis   the end time millis
     * @param mode            the mode
     * @return the long
     */
    public static long calculateDifference(long startTimeMillis, long endTimeMillis, DifferenceMode mode) {
        return calculateDifference(new Date(startTimeMillis), new Date(endTimeMillis), mode);
    }

    /**
     * Calculate difference long.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @param mode      the mode
     * @return the long
     */
    public static long calculateDifference(Date startDate, Date endDate, DifferenceMode mode) {
        long[] different = calculateDifference(startDate, endDate);
        if (mode.equals(DifferenceMode.Minute)) {
            return different[2];
        } else if (mode.equals(DifferenceMode.Hour)) {
            return different[1];
        } else if (mode.equals(DifferenceMode.Day)) {
            return different[0];
        } else {
            return different[3];
        }
    }

    /**
     * Calculate difference long [ ].
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the long [ ]
     */
    public static long[] calculateDifference(Date startDate, Date endDate) {
        return calculateDifference(endDate.getTime() - startDate.getTime());
    }

    /**
     * Calculate difference long [ ].
     *
     * @param differentMilliSeconds the different milli seconds
     * @return the long [ ]
     */
    public static long[] calculateDifference(long differentMilliSeconds) {
        long secondsInMilli = 1000;//1s==1000ms
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long elapsedDays = differentMilliSeconds / daysInMilli;
        differentMilliSeconds = differentMilliSeconds % daysInMilli;
        long elapsedHours = differentMilliSeconds / hoursInMilli;
        differentMilliSeconds = differentMilliSeconds % hoursInMilli;
        long elapsedMinutes = differentMilliSeconds / minutesInMilli;
        differentMilliSeconds = differentMilliSeconds % minutesInMilli;
        long elapsedSeconds = differentMilliSeconds / secondsInMilli;
        return new long[]{elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds};
    }

    /**
     * Calculate days in month int.
     *
     * @param month the month
     * @return the int
     */
    public static int calculateDaysInMonth(int month) {
        return calculateDaysInMonth(0, month);
    }

    /**
     * Calculate days in month int.
     *
     * @param year  the year
     * @param month the month
     * @return the int
     */
    public static int calculateDaysInMonth(int year, int month) {
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] bigMonths = {"1", "3", "5", "7", "8", "10", "12"};
        String[] littleMonths = {"4", "6", "9", "11"};
        List<String> bigList = Arrays.asList(bigMonths);
        List<String> littleList = Arrays.asList(littleMonths);
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (bigList.contains(String.valueOf(month))) {
            return 31;
        } else if (littleList.contains(String.valueOf(month))) {
            return 30;
        } else {
            if (year <= 0) {
                return 29;
            }
            // 是否闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                return 29;
            } else {
                return 28;
            }
        }
    }

    /**
     * 月日时分秒，0-9前补0
     *
     * @param number the number
     * @return the string
     */
    @NonNull
    public static String fillZero(int number) {
        return number < 10 ? "0" + number : "" + number;
    }

    /**
     * 功能：判断日期是否和当前date对象在同一天。
     * 参见：http://www.cnblogs.com/myzhijie/p/3330970.html
     *
     * @param date 比较的日期
     * @return boolean 如果在返回true，否则返回false。
     * @author 沙琪玛 QQ：862990787 Aug 21, 2013 7:15:53 AM
     */
    public static boolean isSameDay(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date is null");
        }
        Calendar nowCalendar = Calendar.getInstance();
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTime(date);
        return (nowCalendar.get(Calendar.ERA) == newCalendar.get(Calendar.ERA) &&
                nowCalendar.get(Calendar.YEAR) == newCalendar.get(Calendar.YEAR) &&
                nowCalendar.get(Calendar.DAY_OF_YEAR) == newCalendar.get(Calendar.DAY_OF_YEAR));
    }

    public static String formatDate(String dateString, String dateFormat){
        long date = Long.valueOf(dateString);
        return formatDate(new Date(date), dateFormat);
    }

    public static String formatDate(Date date, String dateFormat) {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            return sdf.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatDate(Date date) {
        return formatDate(date, "yyyy 年 MM 月 dd 日");
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss字符串转换成日期<br/>
     *
     * @param dateStr    时间字符串
     * @param dataFormat 当前时间字符串的格式。
     * @return Date 日期 ,转换异常时返回null。
     */
    public static Date parseDate(String dateStr, String dataFormat) {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat(dataFormat);
            Date date = dateFormat.parse(dateStr);
            return new Date(date.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss字符串转换成日期<br/>
     *
     * @param dateStr yyyy-MM-dd HH:mm:ss字符串
     * @return Date 日期 ,转换异常时返回null。
     */
    public static Date parseDate(String dateStr) {
        return parseDate(dateStr, "yyyy-MM-dd HH:mm:ss");
    }

    public static String convertTimeFormat(String time, String formatSrc, String formatDes) {
        if (formatSrc == null) {
            formatSrc = "yyyyMMddHHmmssSSS";
        }
        if (formatDes == null) {
            formatDes = "yy/MM/dd HH:mm:ss";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(formatSrc);
        String timeDes = null;
        try {
            Long lo = sdf.parse(time).getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat(formatDes);
            timeDes = dateFormat.format(lo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeDes;
    }

    /**
     * 获取更改时区后的日期
     *
     * @param date    日期
     * @param oldZone 旧时区对象
     * @param newZone 新时区对象
     * @return 日期
     */
    public static Date changeTimeZone(Date date, TimeZone oldZone, TimeZone newZone) {
        Date dateTmp = null;
        if (date != null) {
            int timeOffset = oldZone.getRawOffset() - newZone.getRawOffset();
            dateTmp = new Date(date.getTime() - timeOffset);
        }
        return dateTmp;
    }

    /**
     * 获取当期的日期时间
     * @return
     */
    public static int[] getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        int[] current = new int[3];
        current[0] = calendar.get(Calendar.YEAR);
        current[1] = calendar.get(Calendar.MONTH)+1;
        current[2] = calendar.get(Calendar.DAY_OF_MONTH);
        return  current;
    }

    /**
     * month的范围是 1~12
     * 计算某个 指定日期到目前的天数，测试通过
     * @param
     */
    public static long getDayDistance(int year,int month,int day){

        Calendar spe = Calendar.getInstance();
        spe.set(year,month -1,day);
        return (System.currentTimeMillis() - spe.getTimeInMillis())
                /(24*60*60*1000);
    }

    /**
     * 距离指定日期的相减的天数
     * @param year
     * @param month
     * @param day
     * @param n
     * @return
     */
    public static int[] getMinusNday(int year,int month,int day,int n){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month-1,day);
        calendar.add(Calendar.DAY_OF_MONTH,-n);
        int newYear = calendar.get(Calendar.YEAR);
        int newMonth = calendar.get(Calendar.MONTH) + 1 ;
        int newDay = calendar.get(Calendar.DAY_OF_MONTH);
        return new int[]{newYear,newMonth,newDay};
    }
}

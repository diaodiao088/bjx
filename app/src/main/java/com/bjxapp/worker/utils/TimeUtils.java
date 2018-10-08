package com.bjxapp.worker.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TimeUtils {
    public static final long ONE_SECOND_MILLIS = 1000;
    public static final long ONE_MINUTE_MILLIS = ONE_SECOND_MILLIS * 60;
    public static final long ONE_HOUR_MILLIS = ONE_MINUTE_MILLIS * 60;
    public static final long ONE_DAY_MILLIS = ONE_HOUR_MILLIS * 24;
    public static final long ONE_YEAR_MILLIS = ONE_DAY_MILLIS * 365;
    public static final long ONE_WEEK_MILLIS = ONE_DAY_MILLIS * 7;
    public static final long ONE_MONTH_MILLIS = ONE_DAY_MILLIS * 30;
    public static final long HALF_HOUR_MILLIS = ONE_MINUTE_MILLIS * 30;
    public static final long HALF_MONTH_MILLIS = ONE_DAY_MILLIS * 15;
    public static final long[] ALL_MONTH = {Calendar.JANUARY,
            Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY,
            Calendar.JUNE, Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER,
            Calendar.OCTOBER, Calendar.NOVEMBER, Calendar.DECEMBER};
    public static long[] ALL_DAYS_OF_WEEK = {Calendar.MONDAY,
            Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,
            Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};

    public static long[] WORK_DAYS_OF_WEEK = {Calendar.MONDAY,
            Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,
            Calendar.FRIDAY};

    public static long[] MONDAY_OF_WEEK = {Calendar.MONDAY
            };

    /**
     * 比较两个时间，hour1,minute1 >= hour2,minute2,则返回true
     *
     * @param hour1
     * @param minute1
     * @param hour2
     * @param minute2
     * @return
     */
    public static boolean compareTwoTime(int hour1, int minute1, int hour2,
                                         int minute2) {
        int result = compareTime(hour1, minute1, hour2, minute2);
        if (result == 0 || result == 1) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 比较两个时间
     *
     * @param hour1
     * @param minute1
     * @param hour2
     * @param minute2
     * @return
     */
    public static int compareTime(int hour1, int minute1, int hour2, int minute2) {
        if (hour1 > hour2) {
            return 0;
        } else if (hour1 == hour2) {
            if (minute1 > minute2) {
                return 0;
            } else if (minute1 == minute2) {
                return 1;
            } else {
                return 2;
            }
        } else {
            return 2;
        }
    }

    /**
     * 比较两个时间
     *
     * @param year1
     * @param month1
     * @param dayOfMonth1
     * @param hourOfDay1
     * @param minute1
     * @param year2
     * @param month2
     * @param dayOfMonth2
     * @param hourOfDay2
     * @param minute2
     * @return
     */
    public static int compareTime(int year1, int month1, int dayOfMonth1,
                                  int hourOfDay1, int minute1, int year2, int month2,
                                  int dayOfMonth2, int hourOfDay2, int minute2) {
        if (year1 == year2) {
            return compareTimeIngoreYear(month1, dayOfMonth1, hourOfDay1,
                    minute1, month2, dayOfMonth2, hourOfDay2, minute2);
        } else if (year1 > year2) {
            return 0;
        } else {
            return 2;
        }
    }

    /**
     * 比较两个Calendar，忽略秒和毫秒，参考
     * {@link #compareTime(int, int, int, int, int, int, int, int, int, int)}
     *
     * @param c1
     * @param c2
     * @return
     */
    public static int compareTime(Calendar c1, Calendar c2) {
        return compareTime(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
                c1.get(Calendar.DAY_OF_MONTH), c1.get(Calendar.HOUR_OF_DAY),
                c1.get(Calendar.MINUTE), c2.get(Calendar.YEAR),
                c2.get(Calendar.MONTH), c2.get(Calendar.DAY_OF_MONTH),
                c2.get(Calendar.HOUR_OF_DAY), c2.get(Calendar.MINUTE));
    }

    /**
     * 忽略年比较时间
     *
     * @param month1
     * @param dayOfMonth1
     * @param hourOfDay1
     * @param minute1
     * @param month2
     * @param dayOfMonth2
     * @param hourOfDay2
     * @param minute2
     * @return
     */
    public static int compareTimeIngoreYear(int month1, int dayOfMonth1,
                                            int hourOfDay1, int minute1, int month2, int dayOfMonth2,
                                            int hourOfDay2, int minute2) {

        if (month1 > month2) {
            return 0;
        } else if (month1 == month2) {
            if (dayOfMonth1 > dayOfMonth2) {
                return 0;
            } else if (dayOfMonth1 == dayOfMonth2) {
                return compareTime(hourOfDay1, minute1, hourOfDay2, minute2);
            } else {
                return 2;
            }
        } else {
            return 2;
        }
    }

    /**
     * 获取日期格式 ,用此格式化是为了解决某些rom上显示不出年，月，日中的一个或几个
     *
     * @param time
     * @param formatStr
     * @return
     */
    public static String getDateFormatStr(long time, String formatStr) {
        String timeStr = null;
        SimpleDateFormat setDateFormat = new SimpleDateFormat(formatStr);
        timeStr = setDateFormat.format(time);
        return timeStr;
    }

    /**
     * 获取年
     *
     * @param timeMillis
     * @return
     */
    public static int getYearFromTimeMillis(long timeMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeMillis);
        return c.get(Calendar.YEAR);
    }

    /**
     * 获取月
     *
     * @param timeMillis
     * @return
     */
    public static int getMonthFromTimeMillis(long timeMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeMillis);
        return c.get(Calendar.MONTH);
    }

    /**
     * 获取日
     *
     * @param timeMillis
     * @return
     */
    public static int getDayOfMonthFromTimeMillis(long timeMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeMillis);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static int getHourFromTimeMillis(long timeMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeMillis);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取特定日期的毫秒数
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @return
     */
    public static long getTimeMillsFromDate(int year, int month, int day,
                                            int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        return c.getTimeInMillis();
    }

    /**
     * 获取距离今年的天数
     *
     * @return
     */
    public static int getYearCount(int y4) {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR) - y4;
    }

    /**
     * 是否是今天
     *
     * @param timeMillis 距离1970的毫秒数
     * @return
     */
    public static boolean isToday(long timeMillis) {
        long[] t = getTodayTimeMillis();
        return timeMillis >= t[0] && timeMillis <= t[1];
    }

    /**
     * destineTime的时间是否是currentTime同一天
     *
     * @param currentTime
     * @param destineTime
     * @return
     */
    public static boolean isCurrentDay(long currentTime, long destineTime) {
        long[] t = getCurrentTimeMillis(currentTime);
        return destineTime >= t[0] && destineTime <= t[1];
    }

    /**
     * 获取time时间当天的开始和结束时间
     *
     * @param time
     * @return
     */
    public static long[] getCurrentTimeMillis(long time) {
        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(time);
        current.set(Calendar.MILLISECOND, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.HOUR_OF_DAY, 0);

        long start = current.getTimeInMillis();
        long end = start + ONE_DAY_MILLIS;
        return new long[]{start, end - 1};
    }

    /**
     * 获取今天的开始时间
     * @return
     */
    public static long getTodayStartTimeInMillis() {
    	Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        
        return cal.getTimeInMillis();
    }
    
    /**
     * 今天之前
     *
     * @param timeMillis
     * @return
     */
    public static boolean isBeforeToday(long timeMillis) {
        long[] t = getTodayTimeMillis();
        return timeMillis < t[0];
    }

    /**
     * 是否是两周前
     *
     * @param timeMillis
     * @return
     */
    public static boolean isTwoWeeksAgo(long timeMillis) {
        long[] t = getTodayTimeMillis();
        return timeMillis < t[0] - ONE_WEEK_MILLIS * 2;
    }

    /**
     * 获取今天的毫秒区间
     *
     * @return
     */
    public static long[] getTodayTimeMillis() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MILLISECOND, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.HOUR_OF_DAY, 0);

        long start = now.getTimeInMillis();
        long end = start + ONE_DAY_MILLIS;
        return new long[]{start, end - 1};
    }

    /**
     * second的时间是否在first时间的后一天
     *
     * @param first
     * @param second
     * @return
     */
    public static boolean isSecondDayAfterFirstDay(long first, long second) {
        return isCurrentDay(first, second - ONE_DAY_MILLIS);
    }

    /**
     * 获取明天毫秒区间
     *
     * @return
     */
    public static long[] getTomorrowTimeMillis() {
        long[] today = getTodayTimeMillis();
        return new long[]{today[0] + ONE_DAY_MILLIS,
                today[1] + ONE_DAY_MILLIS};
    }

    /**
     * 获取明天毫秒区间
     *
     * @return
     */
    public static long[] getYestodayTimeMillis() {
        long[] today = getTodayTimeMillis();
        return new long[]{today[0] - ONE_DAY_MILLIS,
                today[1] - ONE_DAY_MILLIS};
    }

    /**
     * 是否是昨天
     *
     * @param timeMillis 距离1970年的毫秒数
     * @return
     */
    public static boolean isYestoday(long timeMillis) {
        return isToday(timeMillis + ONE_DAY_MILLIS);
    }

    /*
     * 是否是明天
     *
     * @param timeMillis 距离1970年的毫秒数
     *
     * @return
     */
    public static boolean isTomorrow(long timeMillis) {
        return isToday(timeMillis - ONE_DAY_MILLIS);
    }

    /**
     * 是否是后天
     *
     * @param timeMillis
     * @return
     */
    public static boolean isTheDayAfterTomorrow(long timeMillis) {
        return isToday(timeMillis - 2 * ONE_DAY_MILLIS);
    }

    /**
     * 是否是两周内
     *
     * @param timeMillis 距离1970年的毫秒数
     * @return
     */
    public static boolean isInnerTwiweeks(long timeMillis) {
        Calendar now = Calendar.getInstance();
        int i = now.get(Calendar.DAY_OF_WEEK);
        i = 14 - i;
        return (timeMillis - ONE_DAY_MILLIS * i) < now.getTimeInMillis();
    }

    /**
     * 是否在
     *
     * @param timeMillis 距离1970年的毫秒数
     * @return
     */
    public static boolean isAfterTwiweeks(long timeMillis) {
        return timeMillis > now() + ONE_WEEK_MILLIS * 2;
    }

    /**
     * 是否在未来
     *
     * @param timeMillis 距离1970年的毫秒数
     * @return
     */
    public static boolean isFuture(long timeMillis) {
        return timeMillis > System.currentTimeMillis();
    }

    /**
     * 是否是今年
     *
     * @param timeMillis
     * @return
     */
    public static boolean isThisYear(long timeMillis) {
        Calendar now = Calendar.getInstance();
        int thisYear = now.get(Calendar.YEAR);
        now.setTimeInMillis(timeMillis);
        return now.get(Calendar.YEAR) == thisYear;
    }

    /**
     * 是否是同一天
     *
     * @param t1
     * @param t2
     * @return
     */
    public static boolean isSameDay(long t1, long t2) {
        boolean bRet = false;
        Date d1 = new Date(t1);
        Date d2 = new Date(t2);
        bRet = (d1.getYear() == d2.getYear() && d1.getMonth() == d2.getMonth() && d1
                .getDate() == d2.getDate());
        return bRet;
    }

    /**
     * 从毫秒中获取时间，忽略秒
     *
     * @param time
     * @return 如 03:20
     */
    public static String getTimeStringFromMillis(long time) {
        int[] ret = getTimeFromMillis(time);
        int hour = ret[0];
        int minute = ret[1];
        return (hour < 10 ? "0" + hour : hour) + ":"
                + (minute < 10 ? "0" + minute : minute);
    }

    /**
     *获取时分
     * @param time 距离当天0点时间
     * @return
     */
    public static int[] getTimeFromMillis(long time) {
        time = time % TimeUtils.ONE_DAY_MILLIS;
        long s = time / 1000;
        int hour = (int) (s / 3600);
        int minute = (int) (s - hour * 3600);
        minute = minute / 60;
        return new int[]{hour, minute};
    }

    /**
     * 获取时分
     * @param time 距离1970年1月1日的时间
     * @return
     */
    public static int[] getTimeFromLongMillis(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return new int[]{hour, minute};
    }

    public static long now() {
        return System.currentTimeMillis();
    }

    /**
     * 精确到分钟
     *
     * @return
     */
    public static long nowCorrectToMinute() {
        Calendar c = Calendar.getInstance();
        c.clear(Calendar.SECOND);
        c.clear(Calendar.MILLISECOND);
        return c.getTimeInMillis();
    }

    /**
     * 精确到分钟
     *
     * @return
     */
    public static long nowCorrectToSecond() {
        Calendar c = Calendar.getInstance();
        c.clear(Calendar.MILLISECOND);
        return c.getTimeInMillis();
    }

    public static long countDays(long millis) {
        final long offset = TimeZone.getDefault().getRawOffset();
        final long target = millis + offset;
        final long now = System.currentTimeMillis() + offset;
        final long subMillis = getZeroTimeOfDay(target) - getZeroTimeOfDay(now);

        return subMillis / ONE_DAY_MILLIS;
    }

    /**
     * 计算倒数日
     *
     * @param millis 被倒数的时间
     * @return
     */
    public static long getCountDownDays(long millis) {
        long days = countDays(millis);
        return days <= 0 ? 0 : days;
    }

    private static long getZeroTimeOfDay(long millis) {
        return millis / ONE_DAY_MILLIS * ONE_DAY_MILLIS;
    }

    /**
     * 获取这个星期几在这个月内总共有几个
     *
     * @return
     */
    public static int getCountOfDayOfWeek(int dayOfWeek, long timeMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeMillis);
        c.set(Calendar.DAY_OF_MONTH, 1);
        int max = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        int count = max % 7;
        if (count == 0) {
            return max / 7;
        }
        int dw = c.get(Calendar.DAY_OF_WEEK);
        if (dw == 7) {
            dw = 0;
        }
        if (dayOfWeek >= dw && dayOfWeek <= dw + count - 1) {
            return max / 7 + 1;
        } else
            return max / 7;
    }

    /**
     * 获取基于某个时间第几个周几
     *
     * @param timeMillis
     * @param dayOfWeek
     * @param which
     * @return
     */
    public static long getTimeByWhichDayOfWeek(long timeMillis, int dayOfWeek,
                                               int which) {
        return TimeUtils.getFirstDayOfWeek(timeMillis, dayOfWeek)
                + TimeUtils.ONE_WEEK_MILLIS * (which - 1);
    }

    /**
     * 获取第一个星期几
     *
     * @param timeMillis
     * @param dayOfWeek
     * @return
     */
    private static long getFirstDayOfWeek(long timeMillis, int dayOfWeek) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeMillis);
        c.set(Calendar.DAY_OF_MONTH, 1);
        int dw = c.get(Calendar.DAY_OF_WEEK);

        if (dw > dayOfWeek) {
            return c.getTimeInMillis() + TimeUtils.ONE_WEEK_MILLIS
                    - (dw - dayOfWeek) * TimeUtils.ONE_DAY_MILLIS;
        } else {
            return c.getTimeInMillis() + (dayOfWeek - dw)
                    * TimeUtils.ONE_DAY_MILLIS;
        }
    }

    /**
     * 获取当前时间戳的星期是当前月份的第几个星期
     *
     * @param timeMillis
     * @return
     */
    public static int getNumberOfWeek(long timeMillis) {
        Calendar according = Calendar.getInstance();
        according.setTimeInMillis(timeMillis);
        int month = according.get(Calendar.MONTH);
        if (month != 0) {
            month = 0;
        }
        int m = month;
        int which = 1;

        while (true) {
            according.setTimeInMillis(timeMillis - TimeUtils.ONE_WEEK_MILLIS
                    * which);
            m = according.get(Calendar.MONTH);
            if (m != month) {
                break;
            }
            which++;
        }
        return which;
    }

    /**
     * 获取一年中月份列表，列表有java.util.Calendar所定义的对象构成
     *
     * @return
     */
    public static List<Long> getAllMonthList() {
        List<Long> ret = new ArrayList<Long>();
        for (Long month : ALL_MONTH) {
            ret.add(month);
        }
        return ret;
    }

    /**
     * 得到距离当前时间最近的半点
     *
     * @return
     */
    public static Calendar getRecentHalfHour() {
        Calendar c = Calendar.getInstance();
        c.clear(Calendar.SECOND);
        c.clear(Calendar.MILLISECOND);
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        int minute = 0;
        if (c.get(Calendar.MINUTE) >= 30) {
            hourOfDay += 1;
            minute = 0;
        } else {
            minute = 30;
        }
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        return c;
    }

    /**
     * 从距离格林毫秒数中取时，分
     *
     * @param timeMillis
     * @return
     */
    public static int[] GetHoursAndMinutesFormMillis(long timeMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeMillis);
        int nHour = c.get(Calendar.HOUR_OF_DAY);
        int nMinute = c.get(Calendar.MINUTE);

        return new int[]{nHour, nMinute};
    }

    /**
     * 是否合理弹通知时间 9：00-21：00
     *
     * @return
     */
    public static boolean isNotifyReasonableTime() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        return hour > 8 && hour < 21;
    }

    /**
     * 获取年月日时分秒
     *
     * @param time
     * @return
     */
    public static int[] getDateFromMillis(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        return new int[]{year, month, day, hour, minute, second};
    }

    /**
     * 获取时+分的毫秒数
     *
     * @param time
     * @return
     */
    public static long getHourMinuteMillis(long time) {
        int[] data = GetHoursAndMinutesFormMillis(time);
        return (long) data[0] * ONE_HOUR_MILLIS + (long) data[1]
                * ONE_MINUTE_MILLIS;
    }

    /**
     * 获取当前时间戳的当前月份的第几个星期x
     *
     * @param time
     * @return 第几个星期几
     */
    public static int getNumberOfWeekRevise(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        return c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
    }

    /**
     * 获取当前时间戳的当前月份的星期x的总数
     *
     * @param time
     * @return
     */
    public static int getCountDayOfWeekRevise(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        return c.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH);
    }

    public static long getDayBeginMills(long sometime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(sometime);
        calendar.clear(Calendar.HOUR);
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar.getTimeInMillis();
    }

    public static long getDayEndMills(long sometime) {
        return getDayBeginMills(sometime) + TimeUtils.ONE_DAY_MILLIS - 1;
    }

    public static int handleWrongHour(long timeInMillis) {
        //处理部分手机当时间在标准时间1970年之前，即mills为负数时HOUR增加了1小时的问题
        long time = -30610252800000l;//1000年1月1日0点0分0秒
        long gapTime = timeInMillis - time;
        long day = gapTime/TimeUtils.ONE_DAY_MILLIS;
        long hour = (gapTime - day * TimeUtils.ONE_DAY_MILLIS) / TimeUtils.ONE_HOUR_MILLIS;
        return (int)hour;
    }

    public static Date newDate(String dateStr, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static long getTimeInMills(int[] hhmm) {
        return hhmm[0] * TimeUtils.ONE_HOUR_MILLIS + hhmm[1] * TimeUtils.ONE_MINUTE_MILLIS;
    }
}

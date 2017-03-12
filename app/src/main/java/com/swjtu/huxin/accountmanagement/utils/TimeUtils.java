package com.swjtu.huxin.accountmanagement.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by huxin on 2017/2/28.
 */

public class TimeUtils {
    public static final int YEAR = 1;
    public static final int MONTH = 2;
    public static final int DAY = 3;
    public static final int WEEK = 4;
    public static final int HOUR = 5;
    public static final int MINUTE = 6;
    public static final int SECOND = 7;

    public static Date getDate(int year,int month,int day) {
        Calendar calendar = Calendar.getInstance();// 创建一个日历对象
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    public static Date getIndexDate(Date date,int indexyear,int indexmonth,int indexday) {
        Calendar calendar = Calendar.getInstance();// 创建一个日历对象
        calendar.setTime(date);
        calendar.add(Calendar.YEAR,indexyear);
        calendar.add(Calendar.MONTH, indexmonth);
        calendar.add(Calendar.DAY_OF_MONTH, indexday);
        return calendar.getTime();
    }

    public static int getTime(Date time , int type) {
        Calendar calendar = Calendar.getInstance();// 创建一个日历对象
        calendar.setTime(time);//初始化日历时间
        switch (type) {
            case 1:return calendar.get(Calendar.YEAR);
            case 2:return calendar.get(Calendar.MONTH) + 1; //一年第一个月值为0
            case 3:return calendar.get(Calendar.DAY_OF_MONTH); //一个月第一天值为1
            case 4:return calendar.get(Calendar.DAY_OF_WEEK) - 1; //星期一值为2

            case 5:return calendar.get(Calendar.HOUR_OF_DAY);
            case 6:return calendar.get(Calendar.MINUTE);
            case 7:return calendar.get(Calendar.SECOND);
            default:return 0;
        }
    }

    public static int getTimeDistance(Date beginDate , Date endDate ) {
        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTime(beginDate);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        long beginTime = beginCalendar.getTime().getTime();
        long endTime = endCalendar.getTime().getTime();
        int betweenDays = (int)((endTime - beginTime) / (1000 * 60 * 60 *24));	//先算出两时间的毫秒数之差大于一天的天数

        endCalendar.add(Calendar.DAY_OF_MONTH, -betweenDays);	//使endCalendar减去这些天数，将问题转换为两时间的毫秒数之差不足一天的情况
        endCalendar.add(Calendar.DAY_OF_MONTH, -1);	//再使endCalendar减去1天
        if(beginCalendar.get(Calendar.DAY_OF_MONTH)==endCalendar.get(Calendar.DAY_OF_MONTH))	//比较两日期的DAY_OF_MONTH是否相等
            return betweenDays + 1;	//相等说明确实跨天了
        else
            return betweenDays + 0;	//不相等说明确实未跨天
    }

    public static Date getMaxDayDate(Date time) {
        Calendar calendar = Calendar.getInstance();// 创建一个日历对象
        calendar.setTime(time);//初始化日历时间
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    public static int getMaxDay(Date time , int indexMonth ,int indexYear) {
        Calendar calendar = Calendar.getInstance();// 创建一个日历对象
        calendar.setTime(time);//初始化日历时间
        calendar.add(Calendar.YEAR,indexYear);
        calendar.add(Calendar.MONTH, indexMonth);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static long getDayFirstMilliSeconds(int day,int indexMonth,int indexYear) {
        Calendar calendar = Calendar.getInstance();// 创建一个日历对象
        calendar.add(Calendar.YEAR,indexYear);
        calendar.add(Calendar.MONTH, indexMonth);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime().getTime();
    }

    public static long getDayLastMilliSeconds(int day,int indexMonth,int indexYear) {
        Calendar calendar = Calendar.getInstance();// 创建一个日历对象
        calendar.add(Calendar.YEAR,indexYear);
        calendar.add(Calendar.MONTH, indexMonth);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MILLISECOND,calendar.getActualMaximum(Calendar.MILLISECOND));
        return calendar.getTime().getTime();
    }

    public static long getMonthFirstMilliSeconds(int month,int indexYear) {
        Calendar calendar = Calendar.getInstance();// 创建一个日历对象
        calendar.add(Calendar.YEAR,indexYear);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime().getTime();
    }

    public static long getMonthLastMilliSeconds(int month,int indexYear) {
        Calendar calendar = Calendar.getInstance();// 创建一个日历对象
        calendar.add(Calendar.YEAR,indexYear);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MILLISECOND,calendar.getActualMaximum(Calendar.MILLISECOND));
        return calendar.getTime().getTime();
    }

    public static long getWeekFirstMilliSeconds() {
        Calendar calendar = Calendar.getInstance();// 创建一个日历对象
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime().getTime();
    }

    public static long getWeekLastMilliSeconds() {
        Calendar calendar = Calendar.getInstance();// 创建一个日历对象
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MILLISECOND,calendar.getActualMaximum(Calendar.MILLISECOND));
        return calendar.getTime().getTime();
    }

    public static long getYearFirstMilliSeconds(int year) {
        Calendar calendar = Calendar.getInstance();// 创建一个日历对象
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime().getTime();
    }

    public static long getYearLastMilliSeconds(int year) {
        Calendar calendar = Calendar.getInstance();// 创建一个日历对象
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MILLISECOND,calendar.getActualMaximum(Calendar.MILLISECOND));
        return calendar.getTime().getTime();
    }

//    public static String getTimeYMD(Date time) {
//        Calendar calendar = Calendar.getInstance();// 创建一个日历对象
//        calendar.setTime(time);//初始化日历时间
//        return calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月"
//                + calendar.get(Calendar.DAY_OF_MONTH) + "日";
//    }
//
//    public static String getTimeMD(Date time) {
//        Calendar calendar = Calendar.getInstance();// 创建一个日历对象
//        calendar.setTime(time);//初始化日历时间
//        return  (calendar.get(Calendar.MONTH) + 1) + "月"
//                + calendar.get(Calendar.DAY_OF_MONTH) + "日";
//    }
//
//    public static String getTimeHM(Date time) {
//        Calendar calendar = Calendar.getInstance();// 创建一个日历对象
//        calendar.setTime(time);//初始化日历时间
//        return  (calendar.get(Calendar.HOUR_OF_DAY) + 1) + "月"
//                + calendar.get(Calendar.DAY_OF_MONTH) + "日";
//    }

//    public static String getTimeYMDistance(Date oldtime , Date newtime) {
//        if(newtime.getTime() > oldtime.getTime()) {
//            int numYearDistance = getTimeDistance(oldtime, newtime);
//            int numMonthDistance = getTimeDistance(oldtime, newtime);
//            if(numMonthDistance > 0) {
//                if(numYearDistance > 0) return numYearDistance + "年" + numMonthDistance + "个月";
//                else return numMonthDistance + "个月";
//            }
//            else {
//                if(numYearDistance > 2) return (numYearDistance - 1) + "年" + (numMonthDistance + 12) + "个月";
//                else return (numMonthDistance + 12) + "个月";
//            }
//        }
//        return "";
//    }
}

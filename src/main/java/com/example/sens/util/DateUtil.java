package com.example.sens.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author xxx
 * @date 2021/4/5 4:36 下午
 */

public class DateUtil {

    public static final String FORMAT = "yyyy-MM-dd";
    public static final ThreadLocal<SimpleDateFormat> THREAD_LOCAL = new ThreadLocal<>();


    public static List<String> getBetweenDates(String start, int count) {
        Date startDate = null;
        SimpleDateFormat sdf = THREAD_LOCAL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(FORMAT);
        }
        try {
            startDate = sdf.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.add(Calendar.DAY_OF_MONTH, count);
        Date endDate = c.getTime();
        String end = sdf.format(endDate);
        return getBetweenDates(start, end);
    }

    public static List<String> getBetweenDates(String start, String end) {

        List<String> result = new ArrayList<>();
        try {
            SimpleDateFormat sdf = THREAD_LOCAL.get();
            if (sdf == null) {
                sdf = new SimpleDateFormat(FORMAT);
            }
            Date start_date = sdf.parse(start);
            Date end_date = sdf.parse(end);
            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start_date);
            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end_date);
            while (tempStart.before(tempEnd)) {
                result.add(sdf.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 计算两个日期之间相差的天数
     */
    public static int daysBetween(Date startDate, Date endDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(endDate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }
}
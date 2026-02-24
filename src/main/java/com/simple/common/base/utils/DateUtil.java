package com.simple.common.base.utils;

import static com.simple.common.base.key.Const.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author 高振中
 * @summary 【日期时间】工具
 * @date 2024-05-10 21:45:31
 **/
public final class DateUtil {
    private DateUtil() {
    } // Cannot be constructed
    /**
     *  (LocalDate)-->String(yyyyMMdd)
     */
    public static String formatShortYmd(LocalDate date) { return FORMAT_SHORT_YMD.format(date); }
    public static String formatYear(LocalDate date) { return FORMAT_YEAR.format(date); }
    public static String formatShortAll(LocalDateTime time) { return FORMAT_ALL_SHORT.format(time); }
    /**
     * (LocalDateTime)-->String(yyyy-MM-dd HH:mm:ss)
     */
    public static String formatAll(LocalDateTime time) {
        return FORMAT_ALL.format(time);
    }

    /**
     * (LocalDateTime)-->String(yyyy-MM-dd)
     */
    public static String formatYmd(LocalDateTime time) {
        return FORMAT_YMD.format(time);
    }

    /**
     * (LocalDateTime)-->String(HH:mm:ss)
     */
    public static String formatHms(LocalDateTime time) {
        return FORMAT_HMS.format(time);
    }

    /**
     * (LocalTime)-->String(HH:mm:ss)
     */
    public static String formatHms(LocalTime time) {
        return FORMAT_HMS.format(time);
    }

    /**
     * (LocalDate)-->String(yyyy-MM-dd)
     */
    public static String formatYmd(LocalDate date) {
        return FORMAT_YMD.format(date);
    }

    /**
     * String(yyyy-MM-dd HH:mm:ss)-->(LocalDateTime)
     */
    public static LocalDateTime parse(String dateTime) {
        return LocalDateTime.parse(dateTime, FORMAT_ALL);
    }

    /**
     * String(yyyy-MM-dd)-->(LocalDate)
     */
    public static LocalDate parseYmd(String date) {
        return LocalDate.parse(date, FORMAT_YMD);
    }

    /**
     * (LocalDateTime)-->(LocalDate)
     */
    public static LocalDate toLocalDate(LocalDateTime dateTime) {
        return dateTime.toLocalDate();
    }

    /**
     * (LocalDateTime)-->(LocalTime)
     */
    public static LocalTime toLocalTime(LocalDateTime dateTime) {
        return dateTime.toLocalTime();
    }

    /**
     * (LocalDate)-->(LocalDateTime)
     */
    public static LocalDateTime toLocalDateTime(LocalDate date) {
        return date.atStartOfDay();
    }
}

package org.example.logs.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * 时间工具类
 *
 * @author 李昆城
 */
@Data
@Slf4j
@Component
public class DateUtils {


    public static String NOW = "yyyy-MM-dd HH:mm:ss";
    public static String NOW_H = "yyyy-MM-dd HH:mm";
    public static String DATE = "yyyy-MM-dd";
    public static String TIME = "HH:mm:ss";
    public static String NOW_SLASH = "yyyy/MM/dd HH/mm/ss";
    public static String DATE_SLASH = "yyyy/MM/dd";
    public static String TIME_SLASH = "HH/mm/ss";
    public static String NOW_STRING_CHINA = "yyyy年MM月dd日 HH时mm分ss秒";
    /**
     * 一天的毫秒数
     */
    public static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L;


    public Date date = new Date();
    public SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
    public Calendar calendar;


    /**
     * 得到当前系统时间 yyyy-mm-dd hh:mm:ss
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public String nowString() {
        return getTimeByParse(DateUtils.NOW);
    }

    /**
     * 得到当前系统时间 yyyy年MM月dd日 HH时mm分ss秒
     *
     * @return yyyy年MM月dd日 HH时mm分ss秒
     */
    public String nowStringChina() {
        return getTimeByParse(DateUtils.NOW_STRING_CHINA);
    }


    /**
     * 得到当前系统时间 yyyy/mm/dd hh/mm/ss
     *
     * @return yyyy/MM/dd HH/mm/ss
     */
    public String nowStringSlash() {
        return getTimeByParse(DateUtils.NOW_SLASH);
    }

    /**
     * 得到当前时间 yyyy-mm-dd
     *
     * @return yyyy-mm-dd 类型时间
     */
    public String curDateString() {
        return getTimeByParse(DateUtils.DATE);
    }

    /**
     * 得到当前时间 yyyy/mm/dd
     *
     * @return yyyy/mm/dd 类型时间
     */
    public String curDateStringSlash() {
        return getTimeByParse(DateUtils.DATE_SLASH);
    }

    /**
     * 得到当前时间 hh:mm:ss
     *
     * @return hh:mm:ss 类型时间
     */
    public String curTimeString() {
        return getTimeByParse(DateUtils.TIME);
    }

    /**
     * 得到当前时间 hh/mm/ss
     *
     * @return hh/mm/ss 类型时间
     */
    public String curTimeStringSlash() {
        return getTimeByParse(DateUtils.TIME_SLASH);
    }

    /**
     * 时间转换为字符串
     *
     * @param date 需要转换的时间
     * @return yyyy-mmm-dd HH:mm:ss 类型的时间
     */
    public String dateFormat(Date date) {
        return parseDateFormat(date, DateUtils.NOW);
    }

    /**
     * 时间转换为字符串
     *
     * @param date 需要转换的时间
     * @return yyyy/mmm/dd HH/mm/ss 类型的时间
     */
    public String dateFormatSlash(Date date) {
        return parseDateFormat(date, DateUtils.NOW_SLASH);
    }

    /**
     * 指定时间时间转换字符串
     *
     * @param date  时间
     * @param parse 转换类型
     * @return 指定类型的时间字符串
     */
    public String parseDateFormat(Date date, String parse) {
        try {
            if (date == null || StringUtils.isEmpty(parse)) {
                throw new RuntimeException("请传递正确的时间或者需要转换的类型");
            }
            simpleDateFormat.applyPattern(parse);
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            throw new RuntimeException("转换时间发生致命错误：" + e.getMessage());
        }

    }

    /**
     * 返回格式化时间
     *
     * @param parse 时间类型格式
     * @return 当前系统指定格式时间
     */
    public String getTimeByParse(String parse) {
        try {
            long currentTimeMillis = System.currentTimeMillis();
            date.setTime(currentTimeMillis);
            simpleDateFormat.applyPattern(parse);
            String format = simpleDateFormat.format(date);
            return format;
        } catch (Exception e) {
            throw new RuntimeException("请正确传递需要转换的时间格式");
        }
    }

    /**
     * 设置时间
     *
     * @param args 年 月 日 时 分 秒 的顺序进行传递参数
     * @return 设置的Date类型的时间
     */
    public Date diyTime(int... args) {
        try {
            if (args.length <= 0) {
                throw new RuntimeException("错误的时间类型参数，请按照：年 月 日 时 分 秒 的顺序进行传递参数");
            }
            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    calendar.set(Calendar.YEAR, args[i]);
                } else if (i == 1) {
                    calendar.set(Calendar.MONTH, args[i] - 1);
                } else if (i == 2) {
                    calendar.set(Calendar.DAY_OF_MONTH, args[i]);
                } else if (i == 3) {
                    calendar.set(Calendar.HOUR_OF_DAY, args[i]);
                } else if (i == 4) {
                    calendar.set(Calendar.MINUTE, args[i]);
                } else if (i == 5) {
                    calendar.set(Calendar.SECOND, args[i]);
                } else {
                    break;
                }
            }
            return calendar.getTime();
        } catch (Exception e) {
            throw new RuntimeException("设置时间发生致命错误：" + e.getMessage());
        }
    }

    /**
     * 根据类型获得当前系统时间值
     *
     * @param type 需要的类型
     * @return int类型的值
     */
    public Object getDateNum(String type) {
        try {
            Assert.hasLength(type, "关键参数不能为空");
            calendar = Calendar.getInstance();
            Object number;
            switch (type) {
                case "year":
                    number = calendar.get(Calendar.YEAR);
                    break;
                case "month":
                    number = calendar.get(Calendar.MONTH) + 1;
                    break;
                case "day":
                    number = calendar.get(Calendar.DAY_OF_MONTH);
                    break;
                case "hour":
                    number = calendar.get(Calendar.HOUR_OF_DAY);
                    break;
                case "minute":
                    number = calendar.get(Calendar.MINUTE);
                    break;
                case "second":
                    number = calendar.get(Calendar.SECOND);
                    break;
                case "currentTimeMillis":
                    number = calendar.getTimeInMillis();
                    break;
                case "date":
                    number = calendar.getTime();
                    break;
                default:
                    throw new IllegalStateException("无效的类型: " + type);
            }
            return number;
        } catch (Exception e) {
            throw new RuntimeException("内部发生致命错误：" + e.getMessage());
        }
    }

    /**
     * 得到年数
     *
     * @return 年数
     */
    public int getYear() {
        return (int) getDateNum("year");
    }

    /**
     * 得到月份
     *
     * @return 得到月份
     */
    public int getMonth() {
        return (int) getDateNum("month");
    }

    /**
     * 得到天数
     */
    public int getDay() {
        return (int) getDateNum("day");
    }

    /**
     * 得到小时
     */
    public int getHour() {
        return (int) getDateNum("hour");
    }

    /**
     * 得到分钟
     */
    public int getMinute() {
        return (int) getDateNum("minute");
    }

    /**
     * 得到秒数
     */
    public int getSecond() {
        return (int) getDateNum("second");
    }

    /**
     * 得到当前系统时间
     *
     * @return date类型系统时间
     */
    public Date getDate() {
        return (Date) getDateNum("date");
    }

    /**
     * 得到当前系统时间
     *
     * @return 系统时间date
     */
    public Date getCurDate() {
        date.setTime(System.currentTimeMillis());
        return date;
    }

    /**
     * 得到当前系统时间毫秒数
     */
    public long getCurTimeMillis() {
        return (long) getDateNum("currentTimeMillis");
    }

    /**
     * 根据指定格式返回时间的样式
     *
     * @param date   时间
     * @param format 样式
     * @return 字符串时间
     */
    public String dateToDateString(Date date, String format) {
        simpleDateFormat.applyPattern(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 一天的毫秒数
     */
    public static final Long ONE_DAY_TIMES = 24 * 60 * 60 * 1000L;

    /**
     * 默认的时间格式
     */
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd";

    /**
     * 默认的开始时间
     */
    public static final String DEFAULT_START_TIME = "1999-12-12";
    /**
     * 默认的截止时间
     */
    public static final String DEFAULT_END_TIME = "2999-12-12";

    /**
     * 按照指定的类型将时间字符串转换为Date对象
     *
     * @param date    时间字符串格式
     * @param pattern 需要格式化的类型
     * @return date类型的时间
     */
    public static Date stringToDate(String date, String pattern) {
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(date, pattern)) {
            throw new IllegalArgumentException("必要参数不能为空");
        }
        Date result;
        try {
            result = new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            log.error("时间转换失败：{}", e.getMessage());
            throw new RuntimeException("时间转换失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 使用默认类型将字符串转换为时间格式
     *
     * @param date 字符串类型的时间格式
     * @return date类型的时间格式
     */
    public static Date stringToDate(String date) {
        return stringToDate(date, DEFAULT_PATTERN);
    }

    /**
     * 加一天
     *
     * @param date    字符类型的时间
     * @param pattern 需要转换的格式
     * @return 加一天之后的时间
     */
    public static Date plusOneDay(String date, String pattern) {
        long newDataTime = stringToDate(date, pattern).getTime() + ONE_DAY_TIMES;
        return new Date(newDataTime);
    }

    /**
     * 加一天
     *
     * @param date 字符类型的时间
     * @return 加一天之后的时间
     */
    public static Date plusOneDay(String date) {
        long newDataTime = stringToDate(date).getTime() + ONE_DAY_TIMES;
        return new Date(newDataTime);
    }

    /**
     * 提供备选方案的时间，常用于设置默认值
     * 如果第一个时间为空或者为null，则会选择备选时间进行转换
     *
     * @param date            首选时间字符串
     * @param alternativeDate 备选时间字符串
     * @param pattern         需要转换的时间格式
     * @return date对象的时间对象
     */
    public static Date stringToDateMore(String date, String alternativeDate, String pattern) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(date)) {
            return stringToDate(date, pattern);
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(alternativeDate)) {
            throw new IllegalArgumentException("请至少传递一项有效时间");
        }
        return stringToDate(alternativeDate, pattern);
    }

    /**
     * 提供备选方案的时间，常用于设置默认值
     * 如果第一个时间为空或者为null，则会选择备选时间进行转换
     *
     * @param date            首选时间字符串
     * @param alternativeDate 备选时间字符串
     * @return date对象的时间对象
     */
    public static Date stringToDateMore(String date, String alternativeDate) {
        return stringToDateMore(date, alternativeDate, DEFAULT_PATTERN);
    }


    /**
     * 提供备选方案的时间，且在原基础上增加一天
     * 如果第一个时间为空或者为null，则会选择备选时间进行转换
     *
     * @param date            首选时间字符串
     * @param alternativeDate 备选时间字符串
     * @param pattern         需要转换的时间格式
     * @return date对象的时间对象
     */
    public static Date stringToDateMorePlusOneDay(String date, String alternativeDate, String pattern) {
        long newDataTime = stringToDateMore(date, alternativeDate, pattern).getTime() + ONE_DAY_TIMES;
        return new Date(newDataTime);
    }

    /**
     * 提供备选方案的时间，且在原基础上增加一天
     * 如果第一个时间为空或者为null，则会选择备选时间进行转换
     *
     * @param date            首选时间字符串
     * @param alternativeDate 备选时间字符串
     * @return date对象的时间对象
     */
    public static Date stringToDateMorePlusOneDay(String date, String alternativeDate) {
        return stringToDateMorePlusOneDay(date, alternativeDate, DEFAULT_PATTERN);
    }

    /**
     * 时间对象转化为字符串
     *
     * @param date    时间对象
     * @param pattern 转换格式
     * @return 时间字符串
     */
    public static String dateToString(Date date, String pattern) {
        if (Objects.isNull(date) || org.apache.commons.lang3.StringUtils.isBlank(pattern)) {
            throw new IllegalArgumentException("请传递正确的参数");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    /**
     * 时间对象转化为字符串
     *
     * @param date 时间对象
     * @return 时间字符串
     */
    public static String dateToString(Date date) {
        return dateToString(date, DEFAULT_PATTERN);
    }

}

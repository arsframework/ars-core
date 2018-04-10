package ars.util;

import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 日期处理工具类
 *
 * @author wuyongqiang
 */
public final class Dates {
    private static String datePattern; // 日期格式模式
    private static String datetimePattern; // 日期时间格式模式
    private static String datenanoPattern; // 日期时间毫秒格式模式
    private static ThreadLocal<DateFormat> threadDateFormat, threadDatetimeFormat, threadDatenanoFormat;

    private Dates() {

    }

    public static String getDatePattern() {
        if (datePattern == null) {
            synchronized (Dates.class) {
                if (datePattern == null) {
                    datePattern = "yyyy-MM-dd";
                }
            }
        }
        return datePattern;
    }

    public static void setDatePattern(String datePattern) {
        if (datePattern == null) {
            throw new IllegalArgumentException("DatePattern must not be null");
        }
        if (Dates.datePattern != null) {
            throw new IllegalStateException("DatePattern already initialized");
        }
        synchronized (Dates.class) {
            if (Dates.datePattern == null) {
                Dates.datePattern = datePattern;
            }
        }
    }

    public static String getDatetimePattern() {
        if (datetimePattern == null) {
            synchronized (Dates.class) {
                if (datetimePattern == null) {
                    datetimePattern = "yyyy-MM-dd HH:mm:ss";
                }
            }
        }
        return datetimePattern;
    }

    public static void setDatetimePattern(String datetimePattern) {
        if (datetimePattern == null) {
            throw new IllegalArgumentException("DatetimePattern must not be null");
        }
        if (Dates.datetimePattern != null) {
            throw new IllegalStateException("DatetimePattern already initialized");
        }
        synchronized (Dates.class) {
            if (Dates.datetimePattern == null) {
                Dates.datetimePattern = datetimePattern;
            }
        }
    }

    public static String getDatenanoPattern() {
        if (datenanoPattern == null) {
            synchronized (Dates.class) {
                if (datenanoPattern == null) {
                    datenanoPattern = "yyyy-MM-dd HH:mm:ss.SSS";
                }
            }
        }
        return datenanoPattern;
    }

    public static void setDatenanoPattern(String datenanoPattern) {
        if (datenanoPattern == null) {
            throw new IllegalArgumentException("DatenanoPattern must not be null");
        }
        if (Dates.datenanoPattern != null) {
            throw new IllegalStateException("DatenanoPattern already initialized");
        }
        synchronized (Dates.class) {
            if (Dates.datenanoPattern == null) {
                Dates.datenanoPattern = datenanoPattern;
            }
        }
    }

    /**
     * 获取日期格式化处理对象
     *
     * @return 日期格式化处理对象
     */
    public static DateFormat getDateFormat() {
        if (threadDateFormat == null) {
            synchronized (Dates.class) {
                if (threadDateFormat == null) {
                    threadDateFormat = new ThreadLocal<DateFormat>();
                    DateFormat format = new SimpleDateFormat(getDatePattern());
                    threadDateFormat.set(format);
                    return format;
                }
            }
        }
        return threadDateFormat.get();
    }

    /**
     * 获取日期时间格式化处理对象
     *
     * @return 日期时间格式化处理对象
     */
    public static DateFormat getDatetimeFormat() {
        if (threadDatetimeFormat == null) {
            synchronized (Dates.class) {
                if (threadDatetimeFormat == null) {
                    threadDatetimeFormat = new ThreadLocal<DateFormat>();
                    DateFormat format = new SimpleDateFormat(getDatetimePattern());
                    threadDatetimeFormat.set(format);
                    return format;
                }
            }
        }
        return threadDatetimeFormat.get();
    }

    /**
     * 获取日期时间毫秒格式化处理对象
     *
     * @return 日期时间毫秒格式化处理对象
     */
    public static DateFormat getDatenanoFormat() {
        if (threadDatenanoFormat == null) {
            synchronized (Dates.class) {
                if (threadDatenanoFormat == null) {
                    threadDatenanoFormat = new ThreadLocal<DateFormat>();
                    DateFormat format = new SimpleDateFormat(getDatenanoPattern());
                    threadDatenanoFormat.set(format);
                    return format;
                }
            }
        }
        return threadDatenanoFormat.get();
    }

    /**
     * 将字符串形式日期时间转换成日期时间对象
     *
     * @param source 日期时间字符串形式
     * @return 日期时间对象
     */
    public static Date parse(String source) {
        return parse(source, getDatetimeFormat(), getDateFormat());
    }

    /**
     * 将字符串形式日期时间转换成日期时间对象
     *
     * @param source   日期时间字符串形式
     * @param patterns 格式模式数组
     * @return 日期时间对象
     */
    public static Date parse(String source, String... patterns) {
        if (source == null) {
            throw new IllegalArgumentException("Source must not be null");
        }
        for (String pattern : patterns) {
            try {
                return new SimpleDateFormat(pattern).parse(source);
            } catch (ParseException e) {
            }
        }
        throw new IllegalArgumentException("Unparseable date:" + source);
    }

    /**
     * 将字符串形式日期时间转换成日期时间对象
     *
     * @param source  日期时间字符串形式
     * @param formats 格式化对象数组
     * @return 日期时间对象
     */
    public static Date parse(String source, DateFormat... formats) {
        if (source == null) {
            throw new IllegalArgumentException("Source must not be null");
        }
        for (DateFormat format : formats) {
            try {
                return format.parse(source);
            } catch (ParseException e) {
            }
        }
        throw new IllegalArgumentException("Unparseable date:" + source);
    }

    /**
     * 将日期时间对象转换成字符串形式
     *
     * @param date 日期时间对象
     * @return 日期时间字符串形式
     */
    public static String format(Date date) {
        return format(date, false);
    }

    /**
     * 将日期时间对象转换成字符串形式
     *
     * @param date 日期时间对象
     * @param nano 是否精确到毫秒
     * @return 日期时间字符串形式
     */
    public static String format(Date date, boolean nano) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        boolean isDateTime = calendar.get(Calendar.HOUR_OF_DAY) != 0 || calendar.get(Calendar.MINUTE) != 0
            || calendar.get(Calendar.SECOND) != 0;
        return isDateTime ? nano ? getDatenanoFormat().format(date) : getDatetimeFormat().format(date)
            : getDateFormat().format(date);
    }

    /**
     * 计算与指定日期相差指定时间量的日期
     *
     * @param date   目标日期时间
     * @param type   时间量类型
     * @param amount 相差时间量
     * @return 结果日期时间
     */
    public static Date differ(Date date, int type, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(type, amount);
        return calendar.getTime();
    }

    /**
     * 根据日期获取年龄
     *
     * @param date 日期
     * @return 年龄
     */
    public static int getAge(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTime(date);
        int birthdayYear = calendar.get(Calendar.YEAR);
        int birthdayMonth = calendar.get(Calendar.MONTH) + 1;
        int birthdayDay = calendar.get(Calendar.DAY_OF_MONTH);
        int age = currentYear - birthdayYear;
        if (currentMonth < birthdayMonth || (currentMonth == birthdayMonth && currentDay < birthdayDay)) {
            age--;
        }
        return age;
    }

    /**
     * 获取当前年份
     *
     * @return 年份
     */
    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * 获取制定日期年份
     *
     * @param date 日期
     * @return 年份
     */
    public static int getYear(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取当前月份
     *
     * @return 月份
     */
    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * 获取制定日期月份
     *
     * @param date 日期
     * @return 月份
     */
    public static int getMonth(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取当前日
     *
     * @return 日
     */
    public static int getDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取制定日期日
     *
     * @param date 日期
     * @return 日
     */
    public static int getDay(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前小时
     *
     * @return 小时
     */
    public static int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取制定日期小时
     *
     * @param date 日期
     * @return 小时
     */
    public static int getHour(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前分钟
     *
     * @return 分钟
     */
    public static int getMinute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    /**
     * 获取制定日期分钟
     *
     * @param date 日期
     * @return 分钟
     */
    public static int getMinute(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获取当前秒
     *
     * @return 秒
     */
    public static int getSecond() {
        return Calendar.getInstance().get(Calendar.SECOND);
    }

    /**
     * 获取制定日期秒
     *
     * @param date 日期
     * @return 秒
     */
    public static int getSecond(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 获取当前毫秒
     *
     * @return 毫秒
     */
    public static int getMillisecond() {
        return Calendar.getInstance().get(Calendar.MILLISECOND);
    }

    /**
     * 获取制定日期毫秒
     *
     * @param date 日期
     * @return 毫秒
     */
    public static int getMillisecond(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MILLISECOND);
    }

    /**
     * 获取带单位的时间表示（d:天、h:时、m:分、s:秒、ms:毫秒）
     *
     * @param time 时间长度
     * @return 带单位的时间表示
     */
    public static String getUnitTime(long time) {
        if (time < 0) {
            throw new IllegalArgumentException("Time must not be less than 0, got " + time);
        }
        StringBuilder buffer = new StringBuilder();
        if (time >= 86400000) {
            buffer.append(Beans.DEFAULT_DECIMAL_FORMAT.format(time / 86400000d)).append('d');
        } else if (time >= 3600000) {
            buffer.append(Beans.DEFAULT_DECIMAL_FORMAT.format(time / 3600000d)).append('h');
        } else if (time >= 60000) {
            buffer.append(Beans.DEFAULT_DECIMAL_FORMAT.format(time / 60000d)).append('m');
        } else if (time >= 1000) {
            buffer.append(Beans.DEFAULT_DECIMAL_FORMAT.format(time / 1000d)).append('s');
        } else {
            buffer.append(time).append("ms");
        }
        return buffer.toString();
    }

    /**
     * 获取当前年份第一天
     *
     * @return 日期
     */
    public static Date getFirstDate() {
        return getFirstDate(getYear());
    }

    /**
     * 获取制定年份第一天
     *
     * @param year 年份
     * @return 日期
     */
    public static Date getFirstDate(int year) {
        if (year < 1970) {
            throw new IllegalArgumentException("Date must not be less than 1970, got " + year);
        }
        return parse(new StringBuilder().append(year).append("-01-").append("01").toString());
    }

}

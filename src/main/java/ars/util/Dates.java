package ars.util;

import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import ars.util.Beans;

/**
 * 日期处理工具类
 * 
 * @author yongqiangwu
 * 
 */
public final class Dates {
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static DateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static DateFormat datenanoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	public static DateFormat getDateFormat() {
		return dateFormat;
	}

	public static void setDateFormat(DateFormat dateFormat) {
		Dates.dateFormat = dateFormat;
	}

	public static DateFormat getDatetimeFormat() {
		return datetimeFormat;
	}

	public static void setDatetimeFormat(DateFormat datetimeFormat) {
		Dates.datetimeFormat = datetimeFormat;
	}

	public static DateFormat getDatenanoFormat() {
		return datenanoFormat;
	}

	public static void setDatenanoFormat(DateFormat datenanoFormat) {
		Dates.datenanoFormat = datenanoFormat;
	}

	/**
	 * 将字符串形式日期时间转换成日期时间对象
	 * 
	 * @param source
	 *            日期时间字符串形式
	 * @return 日期时间对象
	 */
	public static Date parse(String source) {
		return source == null ? null : parse(source, datetimeFormat, dateFormat);
	}

	/**
	 * 将字符串形式日期时间转换成日期时间对象
	 * 
	 * @param source
	 *            日期时间字符串形式
	 * @param formats
	 *            格式数组
	 * @return 日期时间对象
	 */
	public static Date parse(String source, String... formats) {
		if (source != null) {
			for (String format : formats) {
				try {
					return new SimpleDateFormat(format).parse(source);
				} catch (ParseException e) {
				}
			}
			throw new RuntimeException("Unparseable date:" + source);
		}
		return null;
	}

	/**
	 * 将字符串形式日期时间转换成日期时间对象
	 * 
	 * @param source
	 *            日期时间字符串形式
	 * @param formats
	 *            格式化对象数组
	 * @return 日期时间对象
	 */
	public static Date parse(String source, DateFormat... formats) {
		if (source != null) {
			for (DateFormat format : formats) {
				try {
					return format.parse(source);
				} catch (ParseException e) {
				}
			}
			throw new RuntimeException("Unparseable date:" + source);
		}
		return null;
	}

	/**
	 * 将日期时间对象转换成字符串形式
	 * 
	 * @param date
	 *            日期时间对象
	 * @return 日期时间字符串形式
	 */
	public static String format(Date date) {
		return format(date, false);
	}

	/**
	 * 将日期时间对象转换成字符串形式
	 * 
	 * @param date
	 *            日期时间对象
	 * @param nano
	 *            是否精确到毫秒
	 * @return 日期时间字符串形式
	 */
	public static String format(Date date, boolean nano) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		boolean isDateTime = calendar.get(Calendar.HOUR_OF_DAY) != 0 || calendar.get(Calendar.MINUTE) != 0
				|| calendar.get(Calendar.SECOND) != 0;
		return isDateTime ? nano ? datenanoFormat.format(date) : datetimeFormat.format(date) : dateFormat.format(date);
	}

	/**
	 * 计算与指定日期时间相差指定时间量的日期时间
	 * 
	 * @param datetime
	 *            目标日期时间
	 * @param type
	 *            时间量类型
	 * @param amount
	 *            相差时间量
	 * @return 结果日期时间
	 */
	public static Date differ(Date datetime, int type, int amount) {
		if (datetime == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(datetime);
		calendar.add(type, amount);
		return calendar.getTime();
	}

	/**
	 * 根据生日获取年龄
	 * 
	 * @param birthday
	 *            生日
	 * @return 年龄
	 */
	public static int getAge(Date birthday) {
		if (birthday == null) {
			return 0;
		}
		Calendar calendar = Calendar.getInstance();
		int currentYear = calendar.get(Calendar.YEAR);
		int currentMonth = calendar.get(Calendar.MONTH) + 1;
		int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

		calendar.setTime(birthday);
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
	 * 获取带单位的时间表示（d:天、h:时、m:分、s:秒、ms:毫秒）
	 * 
	 * @param time
	 *            时间长度
	 * @return 带单位的时间表示
	 */
	public static String getUnitTime(long time) {
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

}

package ars.invoke;

import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import ars.util.Beans;
import ars.util.Dates;
import ars.util.Strings;
import ars.invoke.request.Requester;

/**
 * 请求调用工具类
 *
 * @author wuyongqiang
 */
public final class Invokes {
    /**
     * 资源地址匹配模式
     */
    public static final Pattern URI_PATTERN = Pattern.compile("\\$\\{ *uri *\\}", Pattern.CASE_INSENSITIVE);

    /**
     * 客户地址匹配模式
     */
    public static final Pattern HOST_PATTERN = Pattern.compile("\\$\\{ *host *\\}", Pattern.CASE_INSENSITIVE);

    /**
     * 用户标识匹配模式
     */
    public static final Pattern USER_PATTERN = Pattern.compile("\\$\\{ *user *\\}", Pattern.CASE_INSENSITIVE);

    /**
     * 请求参数匹配模式
     */
    public static final Pattern PARAM_PATTERN = Pattern
        .compile("\\$\\{ *param\\.[^ }]+ *\\}", Pattern.CASE_INSENSITIVE);

    /**
     * 时间戳匹配模式
     */
    public static final Pattern TIMESTAMP_PATTERN = Pattern.compile("\\$\\{ *timestamp *\\}", Pattern.CASE_INSENSITIVE);

    private Invokes() {

    }

    /**
     * 获取请求日志信息
     *
     * @param requester 请求对象
     * @param value     请求结果值
     * @return 日志信息
     */
    public static String getLog(Requester requester, Object value) {
        return getLog(requester, new Date(), value);
    }

    /**
     * 获取请求日志信息
     *
     * @param requester 请求对象
     * @param timestamp 时间戳
     * @param value     请求结果值
     * @return 日志信息
     */
    public static String getLog(Requester requester, Date timestamp, Object value) {
        if (requester == null) {
            throw new IllegalArgumentException("Requester must not be null");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp must not be null");
        }
        StringBuilder info = new StringBuilder().append('\n').append(Dates.format(requester.getCreated(), true))
            .append(" [").append(requester.getHost()).append(']');
        if (requester.getUser() != null) {
            info.append(" [").append(requester.getUser()).append(']');
        }
        info.append(" [").append(requester.getUri()).append("] [")
            .append(Dates.getUnitTime(timestamp.getTime() - requester.getCreated().getTime())).append("]\n")
            .append(requester.getParameters());
        if (!(value instanceof Throwable)) {
            info.append('\n').append(value);
        }
        return info.append('\n').toString();
    }

    /**
     * 获取对象属性本地化消息
     *
     * @param requester 请求对象
     * @param type      对象类型
     * @param property  属性名称
     * @return 属性本地化消息
     */
    public static String getPropertyMessage(Requester requester, Class<?> type, String property) {
        if (requester == null) {
            throw new IllegalArgumentException("Requester must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type must not be null");
        }
        if (property == null) {
            throw new IllegalArgumentException("Property must not be null");
        }
        String key = new StringBuilder(type.getName()).append('.').append(property).toString();
        return requester.format(key, property);
    }

    /**
     * 获取对象属性本地化消息
     *
     * @param requester  请求对象
     * @param type       对象类型
     * @param properties 属性名称数组
     * @return 属性本地化消息数组
     */
    public static String[] getPropertyMessages(Requester requester, Class<?> type, String... properties) {
        if (requester == null) {
            throw new IllegalArgumentException("Requester must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type must not be null");
        }
        if (properties == null || properties.length == 0) {
            properties = Beans.getProperties(type);
        }
        String[] messages = new String[properties.length];
        String prefix = type.getName();
        for (int i = 0; i < properties.length; i++) {
            String property = properties[i];
            String key = new StringBuilder(prefix).append('.').append(property).toString();
            messages[i] = requester.format(key, property);
        }
        return messages;
    }

    /**
     * 根据请求对象对字符串格式化（不区分大小写）
     * <p>
     * ${uri}：资源地址
     * <p>
     * ${host}：客户地址
     * <p>
     * ${param.参数名称}：请求参数
     * <p>
     * ${timestamp}：请求时间戳
     *
     * @param requester 请求对象
     * @param pattern   字符串格式化模式
     * @return 目标字符串
     */
    public static String format(Requester requester, CharSequence pattern) {
        if (requester == null) {
            throw new IllegalArgumentException("Requester must not be null");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern must not be null");
        }
        String user = requester.getUser();
        String timestamp = Strings.toString(requester.getCreated().getTime());
        pattern = URI_PATTERN.matcher(pattern).replaceAll(requester.getUri());
        pattern = HOST_PATTERN.matcher(pattern).replaceAll(requester.getHost());
        pattern = USER_PATTERN.matcher(pattern).replaceAll(user == null ? Strings.EMPTY_STRING : user);
        pattern = TIMESTAMP_PATTERN.matcher(pattern).replaceAll(timestamp);

        int start = 0;
        StringBuilder buffer = new StringBuilder();
        Matcher matcher = PARAM_PATTERN.matcher(pattern);
        while (matcher.find()) {
            buffer.append(pattern.subSequence(start, matcher.start()));
            CharSequence expression = pattern.subSequence(matcher.start() + 2, matcher.end() - 1);
            String name = Strings.split(expression, '.')[1].trim();
            Object value = requester.getParameter(name);
            if (value != null) {
                buffer.append(Strings.toString(value));
            }
            start = matcher.end();
        }
        if (start > 0) {
            if (start < pattern.length()) {
                buffer.append(pattern.subSequence(start, pattern.length()));
            }
            return buffer.toString();
        }
        return pattern.toString();
    }

}

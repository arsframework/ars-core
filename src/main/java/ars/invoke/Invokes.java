package ars.invoke;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import ars.util.Beans;
import ars.util.Strings;
import ars.invoke.request.Requester;

/**
 * 请求调用工具类
 * 
 * @author yongqiangwu
 * 
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
	public static final Pattern PARAM_PATTERN = Pattern.compile("\\$\\{ *param\\.[^ }]+ *\\}",
			Pattern.CASE_INSENSITIVE);

	/**
	 * 时间戳匹配模式
	 */
	public static final Pattern TIMESTAMP_PATTERN = Pattern.compile("\\$\\{ *timestamp *\\}", Pattern.CASE_INSENSITIVE);

	private static final ThreadLocal<Requester> currentRequester = new ThreadLocal<Requester>();

	private Invokes() {

	}

	/**
	 * 获取当前请求对象
	 * 
	 * @return 请求对象
	 */
	public static Requester getCurrentRequester() {
		Requester requester = currentRequester.get();
		if (requester == null) {
			throw new RuntimeException("No requester found for current thread");
		}
		return requester;
	}

	/**
	 * 设置当前请求对象
	 * 
	 * @param requester
	 *            请求对象
	 */
	public static void setCurrentRequester(Requester requester) {
		currentRequester.set(requester);
	}

	/**
	 * 获取根请求对象
	 * 
	 * @param requester
	 *            请求对象
	 * @return 请求对象
	 */
	public static Requester getRootRequester(Requester requester) {
		if (requester == null) {
			throw new IllegalArgumentException("Illegal requester:" + requester);
		}
		Requester parent = null;
		while ((parent = requester.getParent()) != null) {
			requester = parent;
		}
		return requester;
	}

	/**
	 * 获取对象属性本地化消息
	 * 
	 * @param requester
	 *            请求对象
	 * @param type
	 *            对象类型
	 * @param property
	 *            属性名称
	 * @return 属性本地化消息
	 */
	public static String getPropertyMessage(Requester requester, Class<?> type, String property) {
		if (requester == null) {
			throw new IllegalArgumentException("Illegal requester:" + requester);
		}
		if (type == null) {
			throw new IllegalArgumentException("Illegal model:" + type);
		}
		if (property == null) {
			throw new IllegalArgumentException("Illegal property:" + property);
		}
		String key = new StringBuilder(type.getName()).append('.').append(property).toString();
		return requester.format(key, property);
	}

	/**
	 * 获取对象属性本地化消息
	 * 
	 * @param requester
	 *            请求对象
	 * @param type
	 *            对象类型
	 * @param properties
	 *            属性名称数组
	 * @return 属性本地化消息数组
	 */
	public static String[] getPropertyMessages(Requester requester, Class<?> type, String... properties) {
		if (requester == null) {
			throw new IllegalArgumentException("Illegal requester:" + requester);
		}
		if (type == null) {
			throw new IllegalArgumentException("Illegal model:" + type);
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
	 * 
	 * ${uri}：资源地址
	 * 
	 * ${host}：客户地址
	 * 
	 * ${param.参数名称}：请求参数
	 * 
	 * ${timestamp}：请求时间戳
	 * 
	 * @param requester
	 *            请求对象
	 * @param source
	 *            源字符串
	 * @return 目标字符串
	 */
	public static String format(Requester requester, String source) {
		if (requester == null) {
			throw new IllegalArgumentException("Illegal requester:" + requester);
		}
		if (source == null) {
			throw new IllegalArgumentException("Illegal source:" + source);
		}
		String user = requester.getUser();
		String timestamp = Strings.toString(requester.getCreated().getTime());
		source = URI_PATTERN.matcher(source).replaceAll(requester.getUri());
		source = HOST_PATTERN.matcher(source).replaceAll(requester.getHost());
		source = USER_PATTERN.matcher(source).replaceAll(user == null ? Strings.EMPTY_STRING : user);
		source = TIMESTAMP_PATTERN.matcher(source).replaceAll(timestamp);

		int start = 0;
		StringBuilder buffer = new StringBuilder();
		Matcher matcher = PARAM_PATTERN.matcher(source);
		while (matcher.find()) {
			buffer.append(source.subSequence(start, matcher.start()));
			String expression = source.substring(matcher.start() + 2, matcher.end() - 1);
			String name = Strings.split(expression, '.')[1].trim();
			Object value = requester.getParameter(name);
			if (value != null) {
				buffer.append(Strings.toString(value));
			}
			start = matcher.end();
		}
		if (start > 0) {
			if (start < source.length()) {
				buffer.append(source.subSequence(start, source.length()));
			}
			source = buffer.toString();
		}
		return source;
	}

}

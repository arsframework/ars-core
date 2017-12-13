package ars.invoke;

import java.util.Locale;

/**
 * 消息国际化处理接口
 * 
 * @author yongqiangwu
 * 
 */
public interface Messager {
	/**
	 * 消息本地化
	 * 
	 * @param locale
	 *            本地语言环境
	 * @param key
	 *            消息索引
	 * @param args
	 *            格式化参数
	 * @return 本地化消息
	 */
	public String format(Locale locale, String key, Object[] args);

	/**
	 * 消息本地化
	 * 
	 * @param locale
	 *            本地语言环境
	 * @param key
	 *            消息索引
	 * @param args
	 *            格式化参数
	 * @param text
	 *            消息默认值
	 * @return 本地化消息
	 */
	public String format(Locale locale, String key, Object[] args, String text);

}

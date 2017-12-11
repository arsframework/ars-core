package ars.invoke;

import java.util.Map;
import java.util.Locale;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.text.MessageFormat;

import ars.util.Strings;
import ars.invoke.Messager;

/**
 * 消息国际化处理标准实现
 * 
 * @author wuyq
 * 
 */
public class StandardMessager implements Messager {
	private String[] resources = Strings.EMPTY_ARRAY;
	private Map<Locale, ResourceBundle[]> localeBundles = new HashMap<Locale, ResourceBundle[]>();

	public StandardMessager(String... resources) {
		if (resources == null || resources.length == 0) {
			throw new IllegalArgumentException("Illegal resources:" + Strings.toString(resources));
		}
		this.resources = resources;
	}

	/**
	 * 获取指定语言环境对应的资源绑定对象
	 * 
	 * @param locale
	 *            语言环境
	 * @return 资源绑定对象数组
	 */
	protected ResourceBundle[] getBundles(Locale locale) {
		ResourceBundle[] bundles = this.localeBundles.get(locale);
		if (bundles == null) {
			synchronized (this) {
				bundles = this.localeBundles.get(locale);
				if (bundles == null) {
					bundles = new ResourceBundle[this.resources.length];
					for (int i = 0; i < bundles.length; i++) {
						bundles[i] = ResourceBundle.getBundle(this.resources[i], locale);
					}
					this.localeBundles.put(locale, bundles);
				}
			}
		}
		return bundles;
	}

	/**
	 * 获取消息内容
	 * 
	 * @param locale
	 *            语言环境
	 * @param key
	 *            消息索引
	 * @param text
	 *            消息默认值
	 * @return 消息内容
	 */
	protected String getMessage(Locale locale, String key, String text) {
		ResourceBundle[] bundles = this.getBundles(locale);
		for (ResourceBundle bundle : bundles) {
			if (bundle.containsKey(key)) {
				return bundle.getString(key);
			}
		}
		if (text == null) {
			throw new MissingResourceException(
					"Can't find resource for bundle java.util.PropertyResourceBundle, key " + key,
					StandardMessager.class.getName(), key);
		}
		return text;
	}

	@Override
	public String format(Locale locale, String key, Object[] args) {
		return this.format(locale, key, args, key);
	}

	@Override
	public String format(Locale locale, String key, Object[] args, String text) {
		String message = this.getMessage(locale, key, text);
		return args == null || args.length == 0 ? message : MessageFormat.format(message, args);
	}

}

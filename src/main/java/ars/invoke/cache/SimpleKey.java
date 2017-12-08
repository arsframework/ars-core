package ars.invoke.cache;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Collection;

import ars.util.Beans;
import ars.util.Strings;
import ars.invoke.cache.Key;

/**
 * 缓存标识简单实现
 * 
 * @author wuyq
 *
 */
public class SimpleKey implements Key {
	private static final long serialVersionUID = 1L;

	private String id;
	private String uri;
	private String user;
	private int timeout;
	private Map<String, Object> condtions = new HashMap<String, Object>();

	/**
	 * 设置资源标识
	 * 
	 * @param uri
	 *            资源地址
	 * @return 缓存标识对象
	 */
	public SimpleKey uri(String uri) {
		this.uri = uri;
		return this;
	}

	/**
	 * 设置用户标识
	 * 
	 * @param code
	 *            用户标识
	 * @return 缓存标识对象
	 */
	public SimpleKey user(String code) {
		this.user = code;
		return this;
	}

	/**
	 * 设置缓存标识超时时间（小于0标识没有超时限制）
	 * 
	 * @param timeout
	 *            超时时间（秒）
	 * @return 缓存标识对象
	 */
	public SimpleKey timeout(int timeout) {
		if (timeout < 0) {
			throw new IllegalArgumentException("Illegal timeout:" + timeout);
		}
		this.timeout = timeout;
		return this;
	}

	/**
	 * 设置缓存标识条件
	 * 
	 * @param name
	 *            条件名称
	 * @param value
	 *            条件值
	 * @return 缓存标识对象
	 */
	public SimpleKey condtion(String name, Object value) {
		this.condtions.put(name, value);
		return this;
	}

	/**
	 * 设置缓存标识条件
	 * 
	 * @param condtions
	 *            条件键/值映射表
	 * @return 缓存标识对象
	 */
	public SimpleKey condtion(Map<String, Object> condtions) {
		this.condtions.putAll(condtions);
		return this;
	}

	@Override
	public String getId() {
		if (this.id == null) {
			StringBuilder buffer = new StringBuilder();
			if (this.uri != null) {
				buffer.append('{').append(this.uri).append('}');
			}
			if (this.user != null) {
				buffer.append('{').append(this.user).append('}');
			}
			if (!this.condtions.isEmpty()) {
				int i = 0;
				buffer.append('{');
				for (String key : Beans.sort(this.condtions.keySet())) {
					if (key == null || key.isEmpty()) {
						continue;
					}
					if (i++ > 0) {
						buffer.append(',');
					}
					Object value = this.condtions.get(key);
					if (value instanceof Collection) {
						for (Object v : Beans.sort((Collection<?>) value)) {
							buffer.append(key).append('=');
							if (!Beans.isEmpty(v)) {
								buffer.append(Strings.toString(v));
							}
						}
					} else if (value instanceof Object[]) {
						for (Object v : Beans.sort(Arrays.asList((Object[]) value))) {
							buffer.append(key).append('=');
							if (!Beans.isEmpty(v)) {
								buffer.append(Strings.toString(v));
							}
						}
					} else {
						buffer.append(key).append('=');
						if (!Beans.isEmpty(value)) {
							buffer.append(Strings.toString(value));
						}
					}
				}
				buffer.append('}');
			}
			this.id = buffer.toString();
		}
		return this.id;
	}

	@Override
	public int getTimeout() {
		return this.timeout;
	}

	@Override
	public int hashCode() {
		String id = this.getId();
		return id.isEmpty() ? super.hashCode() : 31 + id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		String id = this.getId();
		if (id.isEmpty()) {
			return super.equals(obj);
		} else if (this == obj) {
			return true;
		}
		return obj != null && obj instanceof SimpleKey && id.equals(((SimpleKey) obj).getId());
	}

}

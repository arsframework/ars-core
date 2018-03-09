package ars.invoke;

import java.util.Set;
import java.util.Arrays;
import java.util.Collection;
import java.io.Serializable;

import ars.util.Beans;
import ars.util.Strings;
import ars.invoke.request.Requester;

/**
 * 可资源数据缓存规则类
 * 
 * @author yongqiangwu
 *
 */
public class Cacheable implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 默认缓存超时时间（秒）
	 */
	public static final int DEFAULT_TIMEOUT = 30 * 60;

	private int timeout = DEFAULT_TIMEOUT; // 缓存超时时间（秒）
	private boolean global = true; // 是否是全局缓存
	private String target; // 缓存目标地址
	private String refresh; // 缓存刷新触发地址

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isGlobal() {
		return global;
	}

	public void setGlobal(boolean global) {
		this.global = global;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getRefresh() {
		return refresh;
	}

	public void setRefresh(String refresh) {
		this.refresh = refresh;
	}

	/**
	 * 获取请求对应的资源数据缓存标识
	 * 
	 * @param requester
	 *            请求对象
	 * @return 缓存标识
	 */
	public final String getKey(Requester requester) {
		if (requester == null) {
			throw new IllegalArgumentException("Illegal requester:" + requester);
		}
		StringBuilder buffer = new StringBuilder("{").append(requester.getUri()).append("}{");
		if (requester.getUser() != null && !this.global) {
			buffer.append(requester.getUser()).append("}{");
		}
		Set<String> names = requester.getParameterNames();
		if (!names.isEmpty()) {
			String[] keys = names.toArray(Strings.EMPTY_ARRAY);
			Arrays.sort(keys);
			for (int i = 0; i < keys.length; i++) {
				if (i > 0) {
					buffer.append(',');
				}
				String key = keys[i];
				Object value = requester.getParameter(key);
				if (value instanceof Collection) {
					Collection<?> collection = (Collection<?>) value;
					if (!collection.isEmpty()) {
						Object[] array = collection.toArray();
						Arrays.sort(array);
						for (Object v : array) {
							buffer.append(key).append('=');
							if (!Beans.isEmpty(v)) {
								buffer.append(Strings.toString(v));
							}
						}
					}
				} else if (value instanceof Object[]) {
					Object[] array = (Object[]) value;
					if (array.length > 0) {
						Object[] copy = Arrays.copyOf(array, array.length);
						Arrays.sort(copy);
						for (Object v : copy) {
							buffer.append(key).append('=');
							if (!Beans.isEmpty(v)) {
								buffer.append(Strings.toString(v));
							}
						}
					}
				} else {
					buffer.append(key).append('=');
					if (!Beans.isEmpty(value)) {
						buffer.append(Strings.toString(value));
					}
				}
			}
		}
		return buffer.append('}').toString();
	}

}

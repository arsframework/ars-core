package ars.invoke.request;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import ars.util.Cache;
import ars.util.SimpleCache;
import ars.invoke.request.SessionFactory;

/**
 * 基于缓存的会话工厂实现
 * 
 * @author yongqiangwu
 *
 */
public class CacheSessionFactory implements SessionFactory {
	/**
	 * 默认会话超时时间（秒）
	 */
	public final static int DEFAULT_TIMEOUT = 30 * 60;

	protected final Cache cache;
	protected final int timeout;

	public CacheSessionFactory() {
		this(DEFAULT_TIMEOUT);
	}

	public CacheSessionFactory(int timeout) {
		this(new SimpleCache(), timeout);
	}

	public CacheSessionFactory(Cache cache) {
		this(cache, DEFAULT_TIMEOUT);
	}

	public CacheSessionFactory(Cache cache, int timeout) {
		if (cache == null) {
			throw new IllegalArgumentException("Illegal cache:" + cache);
		}
		if (timeout < 1) {
			throw new IllegalArgumentException("Illegal timeout:" + timeout);
		}
		this.cache = cache;
		this.timeout = timeout;
	}

	@Override
	public Session getSession(final Requester requester) {
		if (requester == null) {
			throw new IllegalArgumentException("Illegal requester:" + requester);
		}
		return new Session() {
			private Set<String> names; // 所有属性名称集合

			/**
			 * 获取缓存标识
			 * 
			 * @return 缓存标识
			 */
			private String key() {
				return this.key(null);
			}

			/**
			 * 获取属性名称缓存标识
			 * 
			 * @param name
			 *            属性名称
			 * @return 缓存标识
			 */
			private String key(String name) {
				StringBuilder buffer = new StringBuilder();
				if (name == null) {
					buffer.append("session_").append(this.getId());
				} else {
					buffer.append("session_name_").append(this.getId()).append(name);
				}
				return buffer.toString();
			}

			@SuppressWarnings("unchecked")
			private void initializeNames() {
				if (this.names == null) {
					this.names = (Set<String>) cache.get(this.key());
					if (this.names == null) {
						this.names = new HashSet<String>(0);
					}
				}
			}

			@Override
			public String getId() {
				return requester.getClient();
			}

			@Override
			public int getTimeout() {
				return timeout;
			}

			@Override
			public SessionFactory getSessionFactory() {
				return CacheSessionFactory.this;
			}

			@Override
			public Set<String> getAttributeNames() {
				this.initializeNames();
				return Collections.unmodifiableSet(this.names);
			}

			@Override
			public Object getAttribute(String name) {
				if (name == null) {
					throw new IllegalArgumentException("Illegal name:" + name);
				}
				return cache.get(this.key(name));
			}

			@Override
			public void setAttribute(String name, Object value) {
				if (name == null) {
					throw new IllegalArgumentException("Illegal name:" + name);
				}
				cache.set(this.key(name), value);
				this.initializeNames();
				if (this.names.add(name)) {
					cache.set(this.key(), this.names);
				}
			}

			@Override
			public void removeAttribute(String name) {
				if (name == null) {
					throw new IllegalArgumentException("Illegal name:" + name);
				}
				cache.remove(this.key(name));
				this.initializeNames();
				if (this.names.remove(name)) {
					cache.set(this.key(), this.names);
				}
			}

			@Override
			public String toString() {
				this.initializeNames();
				if (this.names.isEmpty()) {
					return "{}";
				}
				int i = 0;
				StringBuilder sb = new StringBuilder();
				sb.append('{');
				for (String name : this.names) {
					if (i++ > 0) {
						sb.append(", ");
					}
					sb.append(name).append('=').append(this.getAttribute(name));
				}
				return sb.append('}').toString();
			}

		};
	}

	@Override
	public void destroy() {
		this.cache.destroy();
	}

}

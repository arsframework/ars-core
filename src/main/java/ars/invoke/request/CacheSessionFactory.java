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
			 * 获取所有属性名称对应缓存标识对象
			 * 
			 * @return 缓存标识对象
			 */
			public Cache.Key getNamesKey() {
				return new Cache.Key() {
					private static final long serialVersionUID = 1L;

					@Override
					public int getTimeout() {
						return timeout;
					}

					@Override
					public String getId() {
						return new StringBuilder("session_names_").append(requester.getClient()).toString();
					}
				};
			}

			/**
			 * 获取属性名称对应缓存标识对象
			 * 
			 * @param name
			 *            属性名称
			 * @return 缓存标识对象
			 */
			private Cache.Key getAttributeKey(final String name) {
				return new Cache.Key() {
					private static final long serialVersionUID = 1L;

					@Override
					public int getTimeout() {
						return timeout;
					}

					@Override
					public String getId() {
						return new StringBuilder("session_attribute_").append(requester.getClient()).append("_")
								.append(name).toString();
					}
				};
			}

			@SuppressWarnings("unchecked")
			private void initializeNames() {
				if (this.names == null) {
					this.names = (Set<String>) cache.get(this.getNamesKey()).getContent();
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
				return cache.get(this.getAttributeKey(name)).getContent();
			}

			@Override
			public void setAttribute(String name, Object value) {
				if (name == null) {
					throw new IllegalArgumentException("Illegal name:" + name);
				}
				cache.set(this.getAttributeKey(name), value);
				this.initializeNames();
				if (this.names.add(name)) {
					cache.set(this.getNamesKey(), this.names);
				}
			}

			@Override
			public void removeAttribute(String name) {
				if (name == null) {
					throw new IllegalArgumentException("Illegal name:" + name);
				}
				cache.remove(this.getAttributeKey(name));
				this.initializeNames();
				if (this.names.remove(name)) {
					cache.set(this.getNamesKey(), this.names);
				}
			}

		};
	}

	@Override
	public void destroy() {
		this.cache.destroy();
	}

}

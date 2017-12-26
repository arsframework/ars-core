package ars.util;

import java.util.Set;
import java.io.IOException;
import java.io.Serializable;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import ars.util.Cache;

/**
 * 基于Redis的数据缓存实现
 * 
 * @author yongqiangwu
 *
 */
public class RedisCache implements Cache {
	/**
	 * 缓存标识前缀
	 */
	public static final String PREFIX = "ars_cache_";

	protected final JedisPool pool;

	public RedisCache() {
		this(new JedisPool());
	}

	public RedisCache(JedisPool pool) {
		if (pool == null) {
			throw new IllegalArgumentException("Illegal pool:" + pool);
		}
		this.pool = pool;
	}

	/**
	 * 缓存值包装类
	 * 
	 * @author yongqiangwu
	 *
	 */
	class ValueWrapper implements Serializable {
		private static final long serialVersionUID = 1L;

		public final Object value; // 缓存值
		public final int timeout; // 超时时间（秒）

		public ValueWrapper(Object value, int timeout) {
			this.value = value;
			this.timeout = timeout;
		}
	}

	@Override
	public Object get(String key) {
		if (key == null) {
			throw new IllegalArgumentException("Illegal key:" + key);
		}
		if (this.pool.isClosed()) {
			throw new RuntimeException("The cache has been destroyed");
		}
		byte[] id = (PREFIX + key).getBytes();
		Jedis jedis = this.pool.getResource();
		try {
			ValueWrapper wrapper = (ValueWrapper) Streams.deserialize(jedis.get(id));
			if (wrapper.timeout > 0) {
				jedis.expire(id, wrapper.timeout);
			}
			return wrapper.value;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			jedis.close();
		}
	}

	@Override
	public void set(String key, Object value) {
		this.set(key, value, 0);
	}

	@Override
	public void set(String key, Object value, int timeout) {
		if (key == null) {
			throw new IllegalArgumentException("Illegal key:" + key);
		}
		if (this.pool.isClosed()) {
			throw new RuntimeException("The cache has been destroyed");
		}
		byte[] bytes;
		byte[] id = (PREFIX + key).getBytes();
		try {
			bytes = Streams.serialize(new ValueWrapper(value, timeout));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Jedis jedis = this.pool.getResource();
		try {
			if (timeout > 0) {
				jedis.setex(id, timeout, bytes);
			} else {
				jedis.set(id, bytes);
			}
		} finally {
			jedis.close();
		}
	}

	@Override
	public void remove(String key) {
		if (key == null) {
			throw new IllegalArgumentException("Illegal key:" + key);
		}
		if (this.pool.isClosed()) {
			throw new RuntimeException("The cache has been destroyed");
		}
		Jedis jedis = this.pool.getResource();
		try {
			Set<String> keys = jedis.keys(PREFIX + key);
			if (!keys.isEmpty()) {
				jedis.del(keys.toArray(Strings.EMPTY_ARRAY));
			}
		} finally {
			jedis.close();
		}
	}

	@Override
	public boolean exists(String key) {
		if (key == null) {
			throw new IllegalArgumentException("Illegal key:" + key);
		}
		if (this.pool.isClosed()) {
			throw new RuntimeException("The cache has been destroyed");
		}
		Jedis jedis = this.pool.getResource();
		try {
			return jedis.exists(PREFIX + key);
		} finally {
			jedis.close();
		}
	}

	@Override
	public void clear() {
		if (this.pool.isClosed()) {
			throw new RuntimeException("The cache has been destroyed");
		}
		Jedis jedis = this.pool.getResource();
		try {
			Set<String> keys = jedis.keys(PREFIX + "*");
			if (!keys.isEmpty()) {
				jedis.del(keys.toArray(Strings.EMPTY_ARRAY));
			}
		} finally {
			jedis.close();
		}
	}

	@Override
	public void destroy() {
		if (!this.pool.isClosed()) {
			synchronized (this.pool) {
				if (!this.pool.isClosed()) {
					this.pool.destroy();
				}
			}
		}
	}

	@Override
	public boolean isDestroyed() {
		return this.pool.isClosed();
	}

}

package ars.util;

import java.util.Set;
import java.io.IOException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import ars.util.Cache;
import ars.util.Caches;

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

	@Override
	public Value get(Key key) {
		if (key == null) {
			throw new IllegalArgumentException("Illegal key:" + key);
		}
		byte[] bytes = null;
		int timeout = key.getTimeout();
		byte[] id = (PREFIX + key.getId()).getBytes();
		Jedis jedis = this.pool.getResource();
		try {
			bytes = jedis.get(id);
			if (bytes != null && timeout > 0) {
				jedis.expire(id, timeout);
			}
		} finally {
			jedis.close();
		}
		try {
			return Caches.value(bytes == null || bytes.length == 0 ? null : Streams.deserialize(bytes), bytes != null);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void set(Key key, Object value) {
		if (key == null) {
			throw new IllegalArgumentException("Illegal key:" + key);
		}
		int timeout = key.getTimeout();
		byte[] id = (PREFIX + key.getId()).getBytes();
		byte[] bytes = Streams.EMPTY_ARRAY;
		if (value != null) {
			try {
				bytes = Streams.serialize(value);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
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
	public void remove(Key key) {
		if (key == null) {
			throw new IllegalArgumentException("Illegal key:" + key);
		}
		Jedis jedis = this.pool.getResource();
		try {
			Set<String> keys = jedis.keys(PREFIX + key.getId());
			if (!keys.isEmpty()) {
				jedis.del(keys.toArray(Strings.EMPTY_ARRAY));
			}
		} finally {
			jedis.close();
		}
	}

	@Override
	public void clear() {
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

}

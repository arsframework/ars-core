package ars.invoke.cache;

import java.util.Set;
import java.io.IOException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import ars.util.Streams;
import ars.util.Strings;
import ars.invoke.cache.Key;
import ars.invoke.cache.Rule;
import ars.invoke.cache.Value;
import ars.invoke.cache.AbstractCache;

/**
 * 基于Redis的数据缓存实现
 * 
 * @author wuyq
 * 
 */
public class RedisCache extends AbstractCache {
	/**
	 * 缓存标识前缀
	 */
	public static final String KEY_PREFIX = "ars_invoke_cache_";

	protected final JedisPool pool;

	public RedisCache(Rule... rules) {
		this(new JedisPool(), rules);
	}

	public RedisCache(JedisPool pool, Rule... rules) {
		super(rules);
		if (pool == null) {
			throw new IllegalArgumentException("Illegal pool:" + pool);
		}
		this.pool = pool;
	}

	@Override
	public void clear() {
		Jedis jedis = this.pool.getResource();
		try {
			Set<String> keys = jedis.keys(KEY_PREFIX + "*");
			if (!keys.isEmpty()) {
				jedis.del(keys.toArray(Strings.EMPTY_ARRAY));
			}
		} finally {
			jedis.close();
		}
	}

	@Override
	public void destroy() {
		try {
			this.clear();
		} finally {
			if (!this.pool.isClosed()) {
				synchronized (this.pool) {
					if (!this.pool.isClosed()) {
						this.pool.destroy();
					}
				}
			}
		}
	}

	@Override
	public Value getCache(Key key) {
		byte[] bytes = null;
		int timeout = key.getTimeout();
		byte[] id = (KEY_PREFIX + key.getId()).getBytes();
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
			return new SimpleValue(bytes != null,
					bytes == null || bytes.length == 0 ? null : Streams.deserialize(bytes));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setCache(Key key, Object value) {
		int timeout = key.getTimeout();
		byte[] id = (KEY_PREFIX + key.getId()).getBytes();
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
	public void removeCache(Key key) {
		Jedis jedis = this.pool.getResource();
		try {
			Set<String> keys = jedis.keys(KEY_PREFIX + key.getId());
			if (!keys.isEmpty()) {
				jedis.del(keys.toArray(Strings.EMPTY_ARRAY));
			}
		} finally {
			jedis.close();
		}
	}

}

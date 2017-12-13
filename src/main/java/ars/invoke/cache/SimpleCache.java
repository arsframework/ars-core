package ars.invoke.cache;

import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Calendar;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ars.util.Dates;
import ars.util.Strings;
import ars.invoke.cache.Key;
import ars.invoke.cache.Rule;
import ars.invoke.cache.Value;
import ars.invoke.cache.SimpleValue;
import ars.invoke.cache.AbstractCache;
import ars.server.task.AbstractTaskServer;

/**
 * 基于内存的数据缓存实现
 * 
 * @author yongqiangwu
 * 
 */
public class SimpleCache extends AbstractCache {
	/**
	 * 缓存值包装对象
	 * 
	 * @author yongqiangwu
	 * 
	 */
	private class ValueWrapper {
		public Object content; // 缓存值内容
		public Date expiration; // 缓存值过期时间

		public ValueWrapper(Object content, Date expiration) {
			this.content = content;
			this.expiration = expiration;
		}

	}

	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Map<String, ValueWrapper> caches = new HashMap<String, ValueWrapper>();

	public SimpleCache(Rule... rules) {
		super(rules);
		this.initializeCleanupServer();
	}

	/**
	 * 初始化缓存清理服务
	 */
	protected void initializeCleanupServer() {
		final AbstractTaskServer cleanup = new AbstractTaskServer() {

			@Override
			protected void execute() throws Exception {
				lock.writeLock().lock();
				try {
					Iterator<String> iterator = caches.keySet().iterator();
					while (iterator.hasNext()) {
						ValueWrapper wrapper = caches.get(iterator.next());
						if (wrapper.expiration != null && !wrapper.expiration.after(new Date())) {
							iterator.remove();
						}
					}
				} finally {
					lock.writeLock().unlock();
				}
			}

		};

		cleanup.setConcurrent(true);
		cleanup.setExpression("0 0 0/1 * * ?");
		cleanup.start();
	}

	@Override
	public void clear() {
		lock.writeLock().lock();
		try {
			this.caches.clear();
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void destroy() {
		this.clear();
	}

	@Override
	public Value getCache(Key key) {
		Date now = new Date();
		boolean expired = false;
		ValueWrapper wrapper = null;
		int timeout = key.getTimeout();
		lock.readLock().lock();
		try {
			wrapper = this.caches.get(key.getId());
			if (wrapper != null && timeout > 0 && wrapper.expiration != null
					&& !(expired = !wrapper.expiration.before(now))) {
				wrapper.expiration = Dates.differ(now, Calendar.SECOND, timeout);
			}
		} finally {
			lock.readLock().unlock();
		}
		boolean cached = wrapper != null && !expired;
		return new SimpleValue(cached, cached ? wrapper.content : null);
	}

	@Override
	public void setCache(Key key, Object value) {
		ValueWrapper wrapper = new ValueWrapper(value, null);
		int timeout = key.getTimeout();
		if (timeout > 0) {
			wrapper.expiration = Dates.differ(new Date(), Calendar.SECOND, timeout);
		}
		lock.writeLock().lock();
		try {
			this.caches.put(key.getId(), wrapper);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void removeCache(Key key) {
		lock.writeLock().lock();
		try {
			Iterator<String> iterator = this.caches.keySet().iterator();
			while (iterator.hasNext()) {
				if (Strings.matches(iterator.next(), key.getId())) {
					iterator.remove();
				}
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

}

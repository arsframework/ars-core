package ars.util;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.io.Serializable;

import ars.util.Cache;
import ars.util.Strings;
import ars.server.Server;
import ars.server.task.AbstractTaskServer;

/**
 * 数据缓存简单实现
 * 
 * @author yongqiangwu
 *
 */
public class SimpleCache implements Cache {
	private boolean destroyed;
	private final Server cleanup = this.initializeCleanupServer();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Map<String, ValueWrapper> values = new HashMap<String, ValueWrapper>();

	public SimpleCache() {
		this.cleanup.start();
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
		public volatile long timestamp = System.currentTimeMillis(); // 时间戳（毫秒）

		public ValueWrapper(Object value, int timeout) {
			this.value = value;
			this.timeout = timeout;
		}

		/**
		 * 判断缓存值是否过期
		 * 
		 * @return true/false
		 */
		public boolean isExpired() {
			return this.timeout > 0 && System.currentTimeMillis() - this.timestamp >= this.timeout * 1000;
		}
	}

	/**
	 * 初始化数据清理服务
	 * 
	 * @return 服务对象
	 */
	protected Server initializeCleanupServer() {
		AbstractTaskServer server = new AbstractTaskServer() {

			@Override
			protected void execute() throws Exception {
				lock.writeLock().lock();
				try {
					Iterator<String> iterator = values.keySet().iterator();
					while (iterator.hasNext()) {
						ValueWrapper wrapper = values.get(iterator.next());
						if (wrapper.isExpired()) {
							iterator.remove();
						}
					}
				} finally {
					lock.writeLock().unlock();
				}
			}

		};
		server.setConcurrent(true);
		server.setExpression("0 0 0/2 * * ?");
		return server;
	}

	@Override
	public Object get(String key) {
		if (key == null) {
			throw new IllegalArgumentException("Illegal key:" + key);
		}
		this.lock.readLock().lock();
		try {
			if (this.destroyed) {
				throw new RuntimeException("The cache has been destroyed");
			}
			ValueWrapper wrapper = this.values.get(key);
			if (wrapper == null || wrapper.isExpired()) {
				return null;
			}
			wrapper.timestamp = System.currentTimeMillis();
			return wrapper.value;
		} finally {
			this.lock.readLock().unlock();
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
		this.lock.writeLock().lock();
		try {
			if (this.destroyed) {
				throw new RuntimeException("The cache has been destroyed");
			}
			this.values.put(key, new ValueWrapper(value, timeout));
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	@Override
	public void remove(String key) {
		if (key == null) {
			throw new IllegalArgumentException("Illegal key:" + key);
		}
		this.lock.writeLock().lock();
		try {
			if (this.destroyed) {
				throw new RuntimeException("The cache has been destroyed");
			}
			Iterator<String> iterator = this.values.keySet().iterator();
			while (iterator.hasNext()) {
				if (Strings.matches(iterator.next(), key)) {
					iterator.remove();
				}
			}
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	@Override
	public boolean exists(String key) {
		if (key == null) {
			throw new IllegalArgumentException("Illegal key:" + key);
		}
		this.lock.readLock().lock();
		try {
			if (this.destroyed) {
				throw new RuntimeException("The cache has been destroyed");
			}
			ValueWrapper wrapper = this.values.get(key);
			return wrapper != null && !wrapper.isExpired();
		} finally {
			this.lock.readLock().unlock();
		}
	}

	@Override
	public void clear() {
		this.lock.writeLock().lock();
		try {
			if (this.destroyed) {
				throw new RuntimeException("The cache has been destroyed");
			}
			this.values.clear();
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	@Override
	public void destroy() {
		if (!this.destroyed) {
			this.lock.writeLock().lock();
			try {
				if (!this.destroyed) {
					this.cleanup.stop();
					this.destroyed = true;
				}
			} finally {
				this.lock.writeLock().unlock();
			}
		}
	}

}

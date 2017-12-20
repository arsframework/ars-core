package ars.util;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ars.util.Cache;
import ars.server.Server;
import ars.server.task.AbstractTaskServer;

/**
 * 数据缓存简单实现
 * 
 * @author yongqiangwu
 *
 */
public class SimpleCache implements Cache {
	private final Server cleanup = this.initializeCleanupServer();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Map<String, ValueWrapper> values = new HashMap<String, ValueWrapper>();

	public SimpleCache() {
		this.cleanup.start();
	}

	/**
	 * 缓存值包装类
	 * 
	 * @author wuyq
	 *
	 */
	class ValueWrapper {
		public final Object value; // 令牌标识
		public long deadline; // 过期时间戳（毫秒）

		public ValueWrapper(Object value, long deadline) {
			this.value = value;
			this.deadline = deadline;
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
					System.out.println(values);
					Iterator<String> iterator = values.keySet().iterator();
					while (iterator.hasNext()) {
						ValueWrapper wrapper = values.get(iterator.next());
						if (System.currentTimeMillis() >= wrapper.deadline) {
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
	public Value get(Key key) {
		if (key == null) {
			throw new IllegalArgumentException("Illegal key:" + key);
		}
		int timeout = key.getTimeout();
		long timestamp = System.currentTimeMillis();
		final ValueWrapper wrapper = this.values.get(key.getId());
		final boolean expired = wrapper == null || timeout > 0 && timestamp >= wrapper.deadline;
		if (!expired) {
			wrapper.deadline = timestamp + timeout * 1000;
		}
		return new Value() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCached() {
				return !expired;
			}

			@Override
			public Object getContent() {
				return expired ? null : wrapper.value;
			}

		};
	}

	@Override
	public void set(Key key, Object value) {
		if (key == null) {
			throw new IllegalArgumentException("Illegal key:" + key);
		}
		int timeout = key.getTimeout();
		long deadline = timeout > 0 ? System.currentTimeMillis() + timeout * 1000 : 0;
		this.lock.writeLock().lock();
		try {
			this.values.put(key.getId(), new ValueWrapper(value, deadline));
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	@Override
	public void remove(Key key) {
		if (key == null) {
			throw new IllegalArgumentException("Illegal key:" + key);
		}
		this.lock.writeLock().lock();
		try {
			this.values.remove(key.getId());
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	@Override
	public void clear() {
		this.lock.writeLock().lock();
		try {
			this.values.clear();
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	@Override
	public void destroy() {
		this.cleanup.stop();
	}

}

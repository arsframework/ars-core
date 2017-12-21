package ars.util;

import ars.util.Cache;

/**
 * 缓存处理工具类
 * 
 * @author yongqiangwu
 *
 */
public final class Caches {
	/**
	 * 构建缓存标识对象
	 * 
	 * @param id
	 *            缓存标识编号
	 * @return 缓存标识对象
	 */
	public static Cache.Key key(final String id) {
		return key(id, 0);
	}

	/**
	 * 构建缓存键对象
	 * 
	 * @param id
	 *            缓存键编号
	 * @param timeout
	 *            缓存超时时间（秒）
	 * @return 缓存键对象
	 */
	public static Cache.Key key(final String id, final int timeout) {
		if (id == null) {
			throw new IllegalArgumentException("Illegal id:" + id);
		}
		return new Cache.Key() {
			private static final long serialVersionUID = 1L;

			@Override
			public int getTimeout() {
				return timeout;
			}

			@Override
			public String getId() {
				return id;
			}
		};
	}

	/**
	 * 获取缓存值对象
	 * 
	 * @param content
	 *            缓存内容
	 * @return 缓存值对象
	 */
	public static Cache.Value value(final Object content) {
		return new Cache.Value() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCached() {
				return true;
			}

			@Override
			public Object getContent() {
				return content;
			}
		};
	}

	/**
	 * 获取缓存值对象
	 * 
	 * @param content
	 *            缓存内容
	 * @param cached
	 *            是否已缓存
	 * @return 缓存值对象
	 */
	public static Cache.Value value(final Object content, final boolean cached) {
		return new Cache.Value() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCached() {
				return cached;
			}

			@Override
			public Object getContent() {
				return content;
			}
		};
	}

}

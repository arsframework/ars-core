package ars.util;

import java.io.Serializable;

/**
 * 数据缓存接口
 * 
 * @author yongqiangwu
 * 
 */
public interface Cache {
	/**
	 * 缓存标识接口
	 * 
	 * @author yongqiangwu
	 * 
	 */
	interface Key extends Serializable {
		/**
		 * 获取识别号
		 * 
		 * @return 识别号
		 */
		public String getId();

		/**
		 * 获取超时时间（秒）
		 * 
		 * @return 超时时间
		 */
		public int getTimeout();

	}

	/**
	 * 缓存值接口
	 * 
	 * @author yongqiangwu
	 *
	 */
	interface Value extends Serializable {
		/**
		 * 判断缓存值是否已缓存
		 * 
		 * @return true/false
		 */
		public boolean isCached();

		/**
		 * 获取缓存值内容
		 * 
		 * @return 缓存值内容
		 */
		public Object getContent();

	}

	/**
	 * 获取缓存值
	 * 
	 * @param key
	 *            缓存标识
	 * @return 缓存值对象
	 */
	public Value get(Key key);

	/**
	 * 设置缓存值
	 * 
	 * @param key
	 *            缓存标识
	 * @param value
	 *            缓存值
	 */
	public void set(Key key, Object value);

	/**
	 * 移除缓存
	 * 
	 * @param key
	 *            缓存标识
	 */
	public void remove(Key key);

	/**
	 * 清空所有缓存数据
	 */
	public void clear();

	/**
	 * 销毁缓存对象
	 */
	public void destroy();

}

package ars.util;

/**
 * 数据缓存接口
 * 
 * @author yongqiangwu
 * 
 */
public interface Cache {
	/**
	 * 获取缓存值
	 * 
	 * @param key
	 *            缓存标识
	 * @return 缓存值对象
	 */
	public Object get(String key);

	/**
	 * 设置缓存值
	 * 
	 * @param key
	 *            缓存标识
	 * @param value
	 *            缓存值
	 */
	public void set(String key, Object value);

	/**
	 * 设置缓存值
	 * 
	 * @param key
	 *            缓存标识
	 * @param value
	 *            缓存值
	 * @param timeout
	 *            超时时间（秒）
	 */
	public void set(String key, Object value, int timeout);

	/**
	 * 移除缓存
	 * 
	 * @param key
	 *            缓存标识
	 */
	public void remove(String key);

	/**
	 * 判断缓存标识是否存在
	 * 
	 * @param key
	 *            缓存标识
	 * @return true/false
	 */
	public boolean exists(String key);

	/**
	 * 清空所有缓存数据
	 */
	public void clear();

	/**
	 * 销毁缓存对象
	 */
	public void destroy();

}

package ars.invoke.cache;

import ars.invoke.Context;
import ars.invoke.cache.Key;
import ars.invoke.cache.Value;
import ars.invoke.request.Requester;

/**
 * 请求数据缓存接口
 * 
 * @author wuyq
 * 
 */
public interface Cache {
	/**
	 * 清空所有缓存数据
	 */
	public void clear();

	/**
	 * 销毁缓存对象
	 */
	public void destroy();

	/**
	 * 缓存初始化
	 * 
	 * @param context
	 *            系统上下文对象
	 */
	public void initialize(Context context);

	/**
	 * 根据请求判断是否可缓存
	 * 
	 * @param requester
	 *            请求对象
	 * @return true/false
	 */
	public boolean isCacheable(Requester requester);

	/**
	 * 获取缓存标识
	 * 
	 * @param requester
	 *            请求对象
	 * @return 缓存标识
	 */
	public Key getKey(Requester requester);

	/**
	 * 获取缓存值
	 * 
	 * @param key
	 *            缓存标识
	 * @return 缓存值对象
	 */
	public Value getCache(Key key);

	/**
	 * 设置缓存值
	 * 
	 * @param key
	 *            缓存标识
	 * @param value
	 *            缓存值
	 */
	public void setCache(Key key, Object value);

	/**
	 * 移除缓存
	 * 
	 * @param key
	 *            缓存标识
	 */
	public void removeCache(Key key);

}

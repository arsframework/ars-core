package ars.invoke;

import ars.invoke.Router;
import ars.invoke.Messager;
import ars.invoke.cache.Cache;

/**
 * 请求调用上下文接口
 * 
 * @author yongqiangwu
 * 
 */
public interface Context {
	/**
	 * 获取缓存对象
	 * 
	 * @return 缓存对象
	 */
	public Cache getCache();

	/**
	 * 获取资源路由对象
	 * 
	 * @return 资源路由对象
	 */
	public Router getRouter();

	/**
	 * 获取消息国际化处理对象
	 * 
	 * @return 消息国际化处理对象
	 */
	public Messager getMessager();

}

package ars.invoke;

import ars.invoke.Router;
import ars.invoke.Messager;
import ars.invoke.cache.InvokeCache;
import ars.invoke.request.SessionFactory;

/**
 * 请求调用上下文接口
 * 
 * @author yongqiangwu
 * 
 */
public interface Context {
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

	/**
	 * 获取系统缓存处理对象
	 * 
	 * @return 缓存处理对象
	 */
	public InvokeCache getCache();

	/**
	 * 获取会话工厂对象
	 * 
	 * @return 会话工厂对象
	 */
	public SessionFactory getSessionFactory();

}

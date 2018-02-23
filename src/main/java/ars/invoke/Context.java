package ars.invoke;

import ars.invoke.Router;
import ars.invoke.Messager;
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
	 * 获取会话工厂对象
	 * 
	 * @return 会话工厂对象
	 */
	public SessionFactory getSessionFactory();

	/**
	 * 获取上下文对象实例
	 * 
	 * @param type
	 *            对象类型
	 * @return 对象实例
	 */
	public <T> T getBean(Class<T> type);

}

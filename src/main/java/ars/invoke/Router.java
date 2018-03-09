package ars.invoke;

import java.util.Map;
import java.util.List;

import ars.util.Cache;
import ars.invoke.Invoker;
import ars.invoke.Resource;
import ars.invoke.Cacheable;
import ars.invoke.request.Requester;
import ars.invoke.event.InvokeEvent;
import ars.invoke.event.InvokeListener;

/**
 * 请求调用路由接口
 * 
 * @author yongqiangwu
 * 
 */
public interface Router {
	/**
	 * 初始化资源路由器
	 */
	public void initialize();

	/**
	 * 获取所有接口地址
	 * 
	 * @return 接口地址集合
	 */
	public List<String> getApis();

	/**
	 * 接口是否已注册
	 * 
	 * @param api
	 *            接口地址
	 * @return true/false
	 */
	public boolean isRegistered(String api);

	/**
	 * 请求路由
	 * 
	 * @param requester
	 *            请求对象
	 * @return 请求结果
	 */
	public Object routing(Requester requester);

	/**
	 * 资源注册
	 * 
	 * @param api
	 *            接口地址
	 * @param invoker
	 *            资源调用器
	 * @param resource
	 *            服务资源
	 */
	public void register(String api, Invoker invoker, Resource resource);

	/**
	 * 资源注册
	 * 
	 * @param api
	 *            接口地址
	 * @param invoker
	 *            资源调用器
	 * @param resource
	 *            服务资源
	 * @param cover
	 *            是否覆盖
	 */
	public void register(String api, Invoker invoker, Resource resource, boolean cover);

	/**
	 * 设置资源缓存处理器
	 * 
	 * @param cache
	 *            缓存处理器
	 */
	public void setCache(Cache cache);

	/**
	 * 设置可缓存资源规则
	 * 
	 * @param cacheables
	 *            可缓存资源规则数组
	 */
	public void setCacheables(Cacheable... cacheables);

	/**
	 * 设置请求转发资源映射
	 * 
	 * @param forwards
	 *            请求转发资源映射
	 */
	public void setForwards(Map<String, String> forwards);

	/**
	 * 设置事件监听器
	 * 
	 * @param <E>
	 *            事件类型
	 * @param type
	 *            事件类型对象
	 * @param listeners
	 *            事件监听器数组
	 */
	public <E extends InvokeEvent> void setListeners(Class<E> type, InvokeListener<E>... listeners);

	/**
	 * 销毁资源路由器
	 */
	public void destroy();

}

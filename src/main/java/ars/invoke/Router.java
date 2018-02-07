package ars.invoke;

import java.util.Map;
import java.util.List;

import ars.util.Cache;
import ars.invoke.Invoker;
import ars.invoke.Resource;
import ars.invoke.CacheRule;
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
	 * 资源路由器初始化
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
	 * 撤销注册资源
	 * 
	 * @param api
	 *            接口地址
	 */
	public void revoke(String api);

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
	 * 设置缓存处理对象
	 * 
	 * @param cache
	 *            缓存处理对象
	 */
	public void setCache(Cache cache);

	/**
	 * 设置资源数据缓存规则
	 * 
	 * @param rules
	 *            缓存规则数组
	 */
	public void setCacheRules(CacheRule... rules);

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

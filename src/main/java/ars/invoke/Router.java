package ars.invoke;

import java.util.List;

import ars.invoke.Invoker;
import ars.invoke.Resource;
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
	 * 添加事件监听器
	 * 
	 * @param <E>
	 *            事件类型
	 * @param type
	 *            事件类型对象
	 * @param listeners
	 *            事件监听器数组
	 */
	public <E extends InvokeEvent> void addListeners(Class<E> type, InvokeListener<E>... listeners);

}

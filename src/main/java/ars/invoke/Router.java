package ars.invoke;

import java.util.List;

import ars.invoke.Invoker;
import ars.invoke.Resource;
import ars.invoke.request.Requester;
import ars.invoke.event.InvokeListener;

/**
 * 请求调用路由接口
 * 
 * @author wuyq
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
	 * 获取接口地址
	 * 
	 * @param pattern
	 *            匹配模式
	 * @return 接口地址集合
	 */
	public List<String> getApis(String pattern);

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
	 * 设置事件监听器
	 * 
	 * @param listeners
	 *            事件监听器数组
	 */
	public void setListeners(InvokeListener<?>... listeners);

}

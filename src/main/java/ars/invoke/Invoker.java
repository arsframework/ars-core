package ars.invoke;

import ars.invoke.Resource;
import ars.invoke.request.Requester;

/**
 * 服务调用接口
 * 
 * @author wuyq
 * 
 */
public interface Invoker {
	/**
	 * 执行请求调用
	 * 
	 * @param requester
	 *            请求对象
	 * @param resource
	 *            接口资源
	 * @return 调用结果
	 * @throws Exception
	 */
	public Object execute(Requester requester, Resource resource) throws Exception;

}

package ars.invoke.request;

import ars.invoke.request.Session;
import ars.invoke.request.Requester;

/**
 * 会话对象工厂接口
 * 
 * @author yongqiangwu
 *
 */
public interface SessionFactory {
	/**
	 * 获取请求对应会话对象
	 * 
	 * @param requester
	 *            请求对象
	 * @return 会话对象
	 */
	public Session getSession(Requester requester);

	/**
	 * 销毁会话工厂对象
	 */
	public void destroy();

}

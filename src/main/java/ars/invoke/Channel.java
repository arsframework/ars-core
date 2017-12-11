package ars.invoke;

import ars.invoke.Context;
import ars.invoke.request.Requester;

/**
 * 请求调用通道接口
 * 
 * @author wuyq
 * 
 */
public interface Channel {
	/**
	 * 获取请求调用上下文对象
	 * 
	 * @return 请求调用上下文对象
	 */
	public Context getContext();

	/**
	 * 设置请求调用上下文对象
	 * 
	 * @param context
	 *            请求调用上下文对象
	 */
	public void setContext(Context context);

	/**
	 * 请求调度
	 * 
	 * @param requester
	 *            请求对象
	 * @return 请求结果
	 * @throws Exception
	 *             操作异常
	 */
	public Object dispatch(Requester requester) throws Exception;

}

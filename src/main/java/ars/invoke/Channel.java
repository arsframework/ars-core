package ars.invoke;

import ars.invoke.Context;

/**
 * 请求调用通道接口
 * 
 * @author yongqiangwu
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

}

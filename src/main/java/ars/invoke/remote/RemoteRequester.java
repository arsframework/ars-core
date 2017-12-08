package ars.invoke.remote;

import ars.invoke.request.Requester;

/**
 * 基于ICE请求对象
 * 
 * @author wuyq
 * 
 */
public interface RemoteRequester extends Requester {
	/**
	 * 获取ICE远程调用请求上下文
	 * 
	 * @return 上下文对象
	 */
	public Ice.Current getIceContext();

}

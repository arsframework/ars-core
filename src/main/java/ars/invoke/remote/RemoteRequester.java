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
	 * 获取ICE当前请求对象
	 * 
	 * @return 当前请求对象
	 */
	public Ice.Current getCurrent();

}

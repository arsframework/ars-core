package ars.invoke.remote;

import ars.invoke.Channel;

/**
 * 基于ICE远程调用通道接口
 * 
 * @author yongqiangwu
 * 
 */
public interface RemoteChannel extends Channel, Ice.Object {
	/**
	 * 获取通道身份标识
	 * 
	 * @return 身份标识
	 */
	public String getIdentifier();

}

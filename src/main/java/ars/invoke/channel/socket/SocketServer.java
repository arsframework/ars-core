package ars.invoke.channel.socket;

import java.nio.channels.Channel;

import ars.server.Server;

/**
 * 套接字通信服务端接口
 * 
 * @author yongqiangwu
 * 
 */
public interface SocketServer extends Server {
	/**
	 * 接收数据
	 * 
	 * @param channel
	 *            数据通道
	 * @throws Exception
	 *             操作异常
	 */
	public void accept(Channel channel) throws Exception;

}

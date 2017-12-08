package ars.invoke.channel.http.tags;

import Ice.ObjectPrx;
import ars.invoke.remote.Remotes;
import ars.invoke.remote.Protocol;
import ars.invoke.channel.http.tags.ResourceTag;

/**
 * 远程资源调用自定义标签
 * 
 * @author wuyq
 * 
 */
public class RemoteTag extends ResourceTag {
	private Protocol protocol; // 接口协议
	private String host; // 主机地址
	private int port; // 主机端口

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	protected Object execute() throws Exception {
		ObjectPrx proxy = Remotes.getProxy(this.protocol, this.host, this.port);
		return Remotes.invoke(proxy, this.getApi(), this.getParameters());
	}

}

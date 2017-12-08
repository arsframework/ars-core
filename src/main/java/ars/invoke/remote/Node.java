package ars.invoke.remote;

import java.io.Serializable;

import ars.util.Strings;

/**
 * 远程节点
 * 
 * @author wuyq
 * 
 */
public class Node implements Serializable {
	private static final long serialVersionUID = 1L;

	private Protocol protocol; // 传输协议
	private String host; // 主机地址
	private int port; // 端口号

	public Node(Protocol protocol, String host, int port) {
		if (protocol == null) {
			throw new IllegalArgumentException("Illegal protocol:" + protocol);
		} else if (Strings.isEmpty(host) || (host = host.trim()).isEmpty()) {
			throw new IllegalArgumentException("Illegal host:" + host);
		} else if (port < 1) {
			throw new IllegalArgumentException("Illegal port:" + port);
		}
		this.protocol = protocol;
		this.host = host;
		this.port = port;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	@Override
	public int hashCode() {
		int code = 1;
		code = 31 * code + this.protocol.hashCode();
		code = 31 * code + this.host.hashCode();
		code = 31 * code + this.port;
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || !(obj instanceof Node)) {
			return false;
		}
		Node node = (Node) obj;
		return this.protocol == node.getProtocol()
				&& this.host.equals(node.getHost())
				&& this.port == node.getPort();
	}

	@Override
	public String toString() {
		return new StringBuilder().append(this.protocol).append(':')
				.append(this.host).append(':').append(this.port).toString();
	}

}

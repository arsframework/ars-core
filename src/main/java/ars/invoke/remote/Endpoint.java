package ars.invoke.remote;

import java.util.Arrays;

import ars.util.Beans;
import ars.util.Strings;
import ars.invoke.Resource;
import ars.invoke.remote.Node;

/**
 * 远程资源端点
 * 
 * @author wuyq
 * 
 */
public class Endpoint implements Resource {
	private static final long serialVersionUID = 1L;

	private String uri; // 资源地址
	private Node[] nodes; // 节点数组

	public Endpoint(String uri, Node... nodes) {
		if (Strings.isEmpty(uri) || (uri = uri.trim()).isEmpty()) {
			throw new IllegalArgumentException("Illegal uri:" + uri);
		}
		if (nodes == null || nodes.length == 0) {
			throw new IllegalArgumentException("Illegal nodes:"
					+ Arrays.toString(nodes));
		}
		this.uri = uri;
		this.nodes = nodes;
	}

	public Endpoint(Protocol protocol, String host, int port, String uri) {
		this(uri, new Node(protocol, host, port));
	}

	public String getUri() {
		return uri;
	}

	public Node[] getNodes() {
		return nodes;
	}

	@Override
	public int hashCode() {
		int code = 1;
		code = 31 * code + this.uri == null ? 0 : this.uri.hashCode();
		for (Node node : this.nodes) {
			code = 31 * code + node.hashCode();
		}
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || !(obj instanceof Endpoint)) {
			return false;
		}
		Endpoint endpoint = (Endpoint) obj;
		return Beans.isEqual(this.nodes, endpoint.getNodes())
				&& (this.uri == null && endpoint.getUri() == null || (this.uri != null && this.uri
						.equals(endpoint.getUri())));
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder(Arrays.toString(this.nodes));
		return this.uri == null ? buffer.toString() : buffer.append(':')
				.append(this.uri).toString();
	}

}

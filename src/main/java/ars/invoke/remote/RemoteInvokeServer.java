package ars.invoke.remote;

import java.util.Map;

import ars.util.Strings;
import ars.invoke.remote.Node;
import ars.invoke.remote.Remotes;
import ars.invoke.remote.Protocol;
import ars.invoke.remote.RemoteChannel;
import ars.server.AbstractServer;

/**
 * 基于ICE消息中间的远程调用服务
 * 
 * @author yongqiangwu
 * 
 */
public class RemoteInvokeServer extends AbstractServer {
	private Node[] nodes; // 服务节点
	private RemoteChannel[] channels;
	private Map<String, String> configure; // 配置映射表
	private Ice.Communicator communicator; // 通信器

	public Node[] getNodes() {
		return nodes;
	}

	public void setNodes(Node... nodes) {
		this.nodes = nodes;
	}

	public RemoteChannel[] getChannels() {
		return channels;
	}

	public void setChannels(RemoteChannel... channels) {
		this.channels = channels;
	}

	public Map<String, String> getConfigure() {
		return configure;
	}

	public void setConfigure(Map<String, String> configure) {
		this.configure = configure;
	}

	@Override
	protected void initialize() {
		if (this.channels == null) {
			throw new RuntimeException("Channel has not been initialized");
		}
		if (this.nodes == null || this.nodes.length == 0) {
			this.nodes = new Node[] { new Node(Protocol.tcp, Strings.LOCALHOST_ADDRESS, 10000) };
		}
		this.communicator = Remotes.initializeCommunicator(this.configure);
	}

	@Override
	public void run() {
		try {
			Ice.ObjectAdapter adapter = this.communicator.createObjectAdapterWithEndpoints(Remotes.COMMON_ADAPTER_NAME,
					Remotes.getAddress(this.nodes));
			for (RemoteChannel channel : this.channels) {
				adapter.add(channel, Ice.Util.stringToIdentity(channel.getIdentifier()));
			}
			adapter.activate();
			this.communicator.waitForShutdown();
		} finally {
			this.destroy();
		}
	}

	@Override
	protected void destroy() {
		if (this.communicator != null) {
			this.communicator.shutdown();
			this.communicator.destroy();
		}
	}

}

package ars.invoke.channel.socket;

import java.util.Iterator;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.DatagramChannel;

import ars.server.Servers;
import ars.server.AbstractServer;
import ars.invoke.channel.socket.SocketServer;

/**
 * 基于UDP协议的数据接口服务抽象实现
 * 
 * @author wuyq
 * 
 */
public abstract class AbstractUdpServer extends AbstractServer implements
		SocketServer {
	private int port = 20000;
	private Selector selector;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		if (port < 1) {
			throw new IllegalArgumentException("Illegal port:" + port);
		}
		this.port = port;
	}

	@Override
	protected void initialize() {
		try {
			this.selector = Selector.open();
			DatagramChannel channel = DatagramChannel.open();
			channel.configureBlocking(false);
			channel.socket().bind(new InetSocketAddress(this.getPort()));
			channel.register(this.selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public final void run() {
		while (this.isAlive()) {
			try {
				if (this.selector.select() > 0) {
					Iterator<SelectionKey> iter = this.selector.selectedKeys()
							.iterator();
					while (iter.hasNext()) {
						SelectionKey key = iter.next();
						if (key.isReadable() && key.isValid()) {
							try {
								this.accept(key.channel());
							} catch (Exception e) {
								key.cancel();
								throw e;
							}
						}
					}
				}
			} catch (Exception e) {
				Servers.logger.error("Server execute failed", e);
			} finally {
				this.selector.selectedKeys().clear();
			}
		}
	}

	@Override
	protected void destroy() {

	}

}

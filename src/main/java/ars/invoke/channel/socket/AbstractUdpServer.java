package ars.invoke.channel.socket;

import java.util.Iterator;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.DatagramChannel;

import ars.util.AbstractServer;
import ars.invoke.channel.socket.SocketServer;

/**
 * 基于UDP协议的数据接口服务抽象实现
 * 
 * @author yongqiangwu
 * 
 */
public abstract class AbstractUdpServer extends AbstractServer implements SocketServer {
	private int port = 10000;
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
	public void run() {
		try {
			this.selector = Selector.open();
			DatagramChannel channel = DatagramChannel.open();
			channel.configureBlocking(false);
			channel.socket().bind(new InetSocketAddress(this.port));
			channel.register(this.selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		while (this.isAlive()) {
			try {
				if (this.selector.select() > 0) {
					Iterator<SelectionKey> iter = this.selector.selectedKeys().iterator();
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
				this.logger.error("Server execute failed", e);
			} finally {
				this.selector.selectedKeys().clear();
			}
		}
	}

}

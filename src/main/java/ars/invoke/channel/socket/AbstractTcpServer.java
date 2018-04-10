package ars.invoke.channel.socket;

import java.util.Iterator;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;

import ars.util.AbstractServer;

/**
 * 基于TCP协议的数据接口服务抽象实现
 *
 * @author wuyongqiang
 */
public abstract class AbstractTcpServer extends AbstractServer implements SocketServer {
    private int port = 10000;
    private Selector selector;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        if (port < 1) {
            throw new IllegalArgumentException("Port must not be less than 1, got " + port);
        }
        this.port = port;
    }

    @Override
    public void run() {
        try {
            this.selector = Selector.open();
            ServerSocketChannel channel = ServerSocketChannel.open();
            channel.configureBlocking(false);
            channel.socket().bind(new InetSocketAddress(this.port));
            channel.register(this.selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.logger.info("TCP/IP server listener at port {}", this.port);
        while (this.isAlive()) {
            try {
                if (this.selector.select() > 0) {
                    Iterator<SelectionKey> iter = this.selector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        if (key.isAcceptable()) {
                            SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
                            channel.configureBlocking(false);
                            channel.register(this.selector, SelectionKey.OP_READ);
                        } else if (key.isReadable() && key.isValid()) {
                            this.accept(key.channel());
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

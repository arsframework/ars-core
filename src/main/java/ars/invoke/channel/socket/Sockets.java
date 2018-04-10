package ars.invoke.channel.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;
import java.nio.channels.DatagramChannel;
import java.net.SocketAddress;

/**
 * 套节字通信处理工具类
 *
 * @author wuyongqiang
 */
public final class Sockets {
    private Sockets() {

    }

    /**
     * TCP方式发送请求
     *
     * @param address 目标地址
     * @param bytes   字节数据
     * @return 链接通道
     * @throws IOException IO操作异常
     */
    public static Channel tcp(SocketAddress address, byte[] bytes) throws IOException {
        if (address == null) {
            throw new IllegalArgumentException("SocketAddress must not be null");
        }
        if (bytes == null) {
            throw new IllegalArgumentException("Bytes must not be null");
        }
        SocketChannel channel = SocketChannel.open();
        channel.connect(address);
        channel.write(ByteBuffer.wrap(bytes));
        return channel;
    }

    /**
     * UDP方式发送请求
     *
     * @param address 目标地址
     * @param bytes   字节数据
     * @return 链接通道
     * @throws IOException IO操作异常
     */
    public static Channel udp(SocketAddress address, byte[] bytes) throws IOException {
        if (address == null) {
            throw new IllegalArgumentException("SocketAddress must not be null");
        }
        if (bytes == null) {
            throw new IllegalArgumentException("Bytes must not be null");
        }
        DatagramChannel channel = DatagramChannel.open();
        channel.connect(address);
        channel.write(ByteBuffer.wrap(bytes));
        return channel;
    }

}

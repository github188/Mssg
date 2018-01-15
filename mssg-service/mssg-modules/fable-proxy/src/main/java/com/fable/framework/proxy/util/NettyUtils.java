package com.fable.framework.proxy.util;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.SystemUtils;

/**
 * for linux and windows
 * <p>
 * Support more if possible.
 *
 * @author stormning 2017/9/26
 * @since 1.3.0
 */
public class NettyUtils {

    private static final boolean EPOLL = SystemUtils.IS_OS_LINUX;

    /**
     * for both udp and tcp event loop group
     *
     * @param threadCount
     * @return
     */
    public static EventLoopGroup createEventLoopGroup(int threadCount) {
        return EPOLL ? new EpollEventLoopGroup(threadCount) : new NioEventLoopGroup(threadCount);
    }

    /**
     * for both udp output and newProp channel
     *
     * @return
     */
    public static Class<? extends Channel> getUdpChannelClass() {
        return EPOLL ? EpollDatagramChannel.class : NioDatagramChannel.class;
    }

    /**
     * for tcp output channel
     *
     * @return
     */
    public static Class<? extends ServerChannel> getTcpServerChannelClass() {
        return EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }


    /**
     * for tcp client channel
     *
     * @return
     */
    public static Class<? extends SocketChannel> getTcpClientChannelClass() {
        return EPOLL ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    public static void send(Channel channel, Object message) {
        channel.writeAndFlush(message);
    }
}

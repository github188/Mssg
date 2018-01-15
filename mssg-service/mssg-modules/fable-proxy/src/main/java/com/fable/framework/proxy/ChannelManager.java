package com.fable.framework.proxy;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * .
 *
 * @author stormning 2017/11/3
 * @since 1.3.0
 */
public interface ChannelManager {

    /**
     * store a channel
     * 1. udp output channel
     * 2. tcp connected channel
     *
     * @param channel
     */
    void store(ChannelFuture channel);

    ChannelFuture selectTcpChannel(
            ChannelHandlerContext ctx,
            ClientListener clientListener,
            InetSocketAddress local,
            InetSocketAddress remote);

    ChannelFuture selectUdpChannel(
            ChannelHandlerContext ctx,
            InetSocketAddress local,
            ChannelHandler[] clientEncoders
    );
}

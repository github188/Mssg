package com.fable.framework.proxy;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;

import java.net.InetSocketAddress;

/**
 * .
 *
 * @author stormning 2017/9/27
 * @since 1.3.0
 */
public interface BootstrapManager<B extends AbstractBootstrap<B, C>, C extends Channel> {

    ChannelFuture bind(InetSocketAddress address, ChannelHandler... channelHandlers);

    Protocol getProtocol();
}

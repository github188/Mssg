package com.fable.framework.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;

/**
 * .
 *
 * @author stormning 2017/11/3
 * @since 1.3.0
 */
public interface ClientListener {

    void onBootstrap(ChannelHandlerContext contextIn, Bootstrap bootstrap);
}

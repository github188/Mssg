package com.fable.framework.proxy.socks;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


/**
 * .
 *
 * @author stormning 2017/12/4
 * @since 1.3.0
 */
public final class SocksServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(
//                new LoggingHandler(LogLevel.ERROR),
                new SocksPortUnificationServerHandler(),
                SocksServerHandler.INSTANCE);
    }
}

package com.fable.framework.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * .
 *
 * @author stormning 2017/11/3
 * @since 1.3.0
 */
public class TcpProxyHandler extends AbstractProxyHandler<ByteBuf, ByteBuf, ByteBuf> {

    public TcpProxyHandler(ChannelManager channelManager) {
        super(channelManager);
    }

    @Override
    protected ByteBuf handle(ChannelHandlerContext ctx, ByteBuf msg) {
        return msg;
    }

    @Override
    protected ByteBuf getOutboundMessage(ByteBuf handled) {
        return handled;
    }

    @Override
    protected InetSocketAddress getRemoteAddress(ChannelHandlerContext ctx, ByteBuf handled) {
        return (InetSocketAddress) ctx.channel().remoteAddress();
    }
}

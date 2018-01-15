package com.fable.framework.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

/**
 * .
 *
 * @author stormning 2017/11/27
 * @since 1.3.0
 */
@Slf4j
public class UdpProxyHandler extends AbstractProxyHandler<ByteBuf, DatagramPacket, ByteBuf> {

    public UdpProxyHandler(ChannelManager channelManager) {
        super(channelManager);
    }

    @Override
    protected ByteBuf handle(ChannelHandlerContext ctx, DatagramPacket msg) {
        return msg.content().retain();
    }

    @Override
    protected ByteBuf getOutboundMessage(ByteBuf handled) {
        return handled;
    }
}
